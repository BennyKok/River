package com.bennyv17.river.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.viewpager.widget.ViewPager
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.isAllGranted
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.bennyv17.river.BuildConfig
import com.bennyv17.river.R
import com.bennyv17.river.callback.RiverInterface
import com.bennyv17.river.fragments.EditorFragment
import com.bennyv17.river.fragments.LearnFragment
import com.bennyv17.river.fragments.ProjectFragment
import com.bennyv17.river.highlighter.syntax.RiveScriptSyntax
import com.bennyv17.river.highlighter.theme.RiveScriptDefaultTheme
import com.bennyv17.river.item.SimpleTextItem
import com.bennyv17.river.util.Tool
import com.flipboard.bottomsheet.BottomSheetLayout
import com.google.android.material.tabs.TabLayout
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import de.psdev.licensesdialog.LicensesDialog
import de.psdev.licensesdialog.licenses.MITLicense
import de.psdev.licensesdialog.model.Notice
import de.psdev.licensesdialog.model.Notices
import kotlinx.android.synthetic.main.activity_main3.*
import kotlinx.android.synthetic.main.fragment_editor.view.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import java.io.File


class Main3Activity : AppCompatActivity(), BillingProcessor.IBillingHandler, RiverInterface {

    private var pref: SharedPreferences? = null
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var darkTheme: Boolean = false
    private var unlocked: Boolean = false
    var editorTextSize: Int = 16
    private lateinit var bp: BillingProcessor

    private lateinit var learnFragment: LearnFragment
    private lateinit var projectFragment: ProjectFragment
    private lateinit var editorFragment: EditorFragment

    companion object {
        val PROJECT_DIR = Environment.getExternalStorageDirectory().path + ("/River/Project")
        const val RIVE_SCRIPT_EXTENSION = ".rive"
        const val UNTITLED = "Untitled"
        val ALL_SYMBOLS = arrayOf("⇥", "!", "+", "-", "*", "#", "//", "_", "^", "{}", "{/}", "()", "<>", "=", "<", ">", "[]", "|", "@")

        //Extra
        const val extra_scrip_data = "script_data"

        //Pref ID
        const val pref_id = "riverPref"
        const val pref_id_unlocked = "unlocked"
        const val pref_id_dark_theme = "darkTheme"
        const val pref_id_project_dir = "projectDir"
        const val pref_id_last_opened_script = "lastOpenedScript"
        const val pref_id_editor_typeface = "editorTypeface"
        const val pref_id_editor_interpreter = "editorInterpreter"
        const val pref_id_editor_text_size = "editorTextSize"
    }

    /**
     * Activity Cycles
     */


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpPreference()

        setContentView(R.layout.activity_main3)

        setSupportActionBar(toolbar)

        setUpViewPager()

        setUpFab()

        setUpEditor()

        bp = BillingProcessor.newBillingProcessor(this, resources.getString(R.string.licence_key), this)
        bp.initialize()

        checkPermissions(true)

        if (checkPermissions(false) && pref!!.getString("previousVersion", "") != BuildConfig.VERSION_NAME) {
            pref!!.edit().putString("previousVersion", BuildConfig.VERSION_NAME).apply()
            showChangelog()
        }


    }

    fun showEditorWihtCode(code: String) {
        val editorLayout = LayoutInflater.from(this).inflate(R.layout.fragment_editor, bottomsheet, false)

        editorLayout.fab_play.setOnClickListener {
            runScript(code)
        }

        //Symbol list
        val symbolListAdapter = FastItemAdapter<SimpleTextItem>()
        editorLayout.editor_symbol_tool_bar.adapter = symbolListAdapter

        val itemList = ALL_SYMBOLS.indices.map { SimpleTextItem(ALL_SYMBOLS[it]) }
        symbolListAdapter.add(itemList)

        symbolListAdapter.withOnClickListener { _, _, item, position ->
            if (position == 0)
                editorLayout.editor.text?.insert(editorLayout.editor.selectionStart, "\t")
            else
                editorLayout.editor.text?.insert(editorLayout.editor.selectionStart, item.text)

            if (item.text!!.length >= 2 && item.text!! != "//") {
                editorLayout.editor.setSelection(editorLayout.editor.selectionStart - 1)
            }
            true
        }

        //Undo Redo buttons
        (editorLayout.action_redo as ImageButton).setImageResource(R.drawable.ic_redo_24dp)
        (editorLayout.action_undo as ImageButton).setImageResource(R.drawable.ic_undo_24dp)

        editorLayout.action_redo.setOnClickListener {
            editorLayout.editor.redo()
        }

        editorLayout.action_undo.setOnClickListener {
            editorLayout.editor.undo()
        }

        editorLayout.editor.setHorizontallyScrolling(true)
        editorLayout.editor.syntax = RiveScriptSyntax
        editorLayout.editor.syntaxTheme = RiveScriptDefaultTheme

        editorLayout.editor.setTextSize(TypedValue.COMPLEX_UNIT_SP, editorTextSize.toFloat())

        editorLayout.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottomsheet.isSheetShowing) {
                bottomsheet.expandSheet()
            }
        }
        KeyboardVisibilityEvent.setEventListener(this) { isOpen ->
            if (isOpen) {
                if (bottomsheet.isSheetShowing) {
                    bottomsheet.expandSheet()
                }
            } else {
                if (bottomsheet.isSheetShowing)
                    bottomsheet.peekSheet()
            }
        }
        //Set the text
        editorLayout.editor.setText(code)

        bottomsheet.showWithSheetView(editorLayout)
    }

    private fun runScript(code: String) {
        if (code.isEmpty()) {
            Tool.toast(this, "Empty script.")
            return
        }

        val intent = Intent(this, RiveScriptPlayground::class.java)
        intent.putExtra(extra_scrip_data, code)
        startActivity(intent)
    }

    private fun setUpEditor() {
        bottomsheet.setShouldDimContentView(false)
    }

    private fun showAbout() {
        MaterialDialog(this).show {
            icon(R.mipmap.ic_launcher)
            title(R.string.app_name)
            message(text = Html.fromHtml(getString(R.string.river_about)))
            positiveButton(R.string.github) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/BennyKok"))
                startActivity(browserIntent)
            }
            neutralButton(text = "Changelog") {
                showChangelog()
            }
            negativeButton(text = "Email") {
                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto: itechbenny@gmail.com"))
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback on River(IDE for RiveScript)")
                startActivity(Intent.createChooser(intent, "Send Feedback"))
            }

        }
    }

    private fun settingsDialog() {
        val dialog = MaterialDialog(this).show {
            title(text = "Settings")
            customView(R.layout.dialog_settings)
            positiveButton(R.string.about) {
                showAbout()
            }
            if (!unlocked)
                neutralButton(text = if (unlocked) getString(R.string.unlocked) else getString(R.string.unlock)) {
                    if (unlocked) {
                        MaterialDialog(this@Main3Activity)
                                .title(text = "Unlocked!")
                                .message(text = "Thank you so much, you have already unlocked. I will keep it up. Stay tuned for future updates.")
                                .positiveButton(text = "Got it")
                                .show()
                    } else
                        unlockExtras()
                }
            negativeButton(text = "Libraries") {
                val notices = Notices()
                notices.addNotice(Notice("RiveScript Contrib CoffeeScript", "https://github.com/aichaos/rivescript-js/tree/master/contrib/coffeescript", "Noah Petherbridge", MITLicense()))
                notices.addNotice(Notice("RiveScript JS", "https://github.com/aichaos/rivescript-js", "Noah Petherbridge", MITLicense()))
                notices.addNotice(Notice("RiveScript Java", "https://github.com/aichaos/rivescript-java", "the original author or authors", MITLicense()))

                LicensesDialog.Builder(this@Main3Activity)
                        .setNotices(notices)
                        .setIncludeOwnLicense(true)
                        .build()
                        .show()
            }
        }

        with(dialog.getCustomView()!!) {
            val theme = findViewById<Switch>(R.id.switch1)
            val popup_typeface = findViewById<Button>(R.id.popup_typeface)
            val popup_interpreter = findViewById<Button>(R.id.popup_interpreter)
            val popup_text_size = findViewById<TextView>(R.id.popup_text_size)
            val popup_add = findViewById<Button>(R.id.popup_add)
            val popup_minus = findViewById<Button>(R.id.popup_minus)

            if (!unlocked)
                theme.isEnabled = false

            theme.isChecked = pref!!.getBoolean(pref_id_dark_theme, false)
            theme.setOnCheckedChangeListener { buttonView, isChecked ->
                pref!!.edit().putBoolean(pref_id_dark_theme, isChecked).apply()
                recreate()
            }

            popup_text_size.text = editorTextSize.toString() + "sp"
            popup_add.setOnClickListener {
                if (editorTextSize >= 40) return@setOnClickListener
                editorTextSize++
                popup_text_size.text = editorTextSize.toString() + "sp"

                saveTextSize()
            }
            popup_minus.setOnClickListener {
                if (editorTextSize <= 14) return@setOnClickListener
                editorTextSize--
                popup_text_size.text = editorTextSize.toString() + "sp"

                saveTextSize()
            }

            val selectedTypeface = pref!!.getInt(pref_id_editor_typeface, 0)
            popup_typeface.text = resources.getStringArray(R.array.editor_typeface)[selectedTypeface]
            popup_typeface.setOnClickListener {
                MaterialDialog(this@Main3Activity).show {
                    title(R.string.typeface)
                    listItemsSingleChoice(
                            res = R.array.editor_typeface,
                            initialSelection = pref!!.getInt(pref_id_editor_typeface, 0))
                    { dialog, index, text ->
                        pref!!.edit().putInt(pref_id_editor_typeface, index).apply()
                        popup_typeface.text = text
                    }
                }

            }

            val selectedInterpreter = pref!!.getInt(pref_id_editor_interpreter, 0)
            popup_interpreter.text = resources.getStringArray(R.array.editor_interpreter)[selectedInterpreter]
            popup_interpreter.setOnClickListener {
                MaterialDialog(this@Main3Activity).show {
                    title(R.string.typeface)
                    listItemsSingleChoice(
                            res = R.array.editor_interpreter,
                            initialSelection = pref!!.getInt(pref_id_editor_interpreter, 0))
                    { dialog, index, text ->
                        pref!!.edit().putInt(pref_id_editor_interpreter, index).apply()
                        popup_interpreter.text = text
                    }
                }

            }
        }

    }

    private fun saveTextSize() {
        pref!!.edit().putInt(Main3Activity.pref_id_editor_text_size, editorTextSize).apply()
    }

    private fun showChangelog() {
        MaterialDialog(this)
                .title(R.string.changelog)
                .customView(R.layout.dialog_changelog)
                .positiveButton(text = "Continue")
                .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main3, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.action_settings -> {
                settingsDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpFab() {
        fab_add.setOnClickListener {
            projectFragment.newScript()
        }
        updateFabVisibility()
    }

    private fun checkPermissions(request: Boolean): Boolean {
        return if (!isAllGranted(Permission.READ_EXTERNAL_STORAGE) || !isAllGranted(Permission.WRITE_EXTERNAL_STORAGE)) {
            if (request) {
                askForPermissions(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE) { result ->
                    if (!result.isAllGranted(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)) {
                        Toast.makeText(this, "River will not be able to work without those permission", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        projectFragment.initAllScripts()
                    }
                }
            }
            false
        } else {
            true
        }
    }

    private fun setUpPreference() {
        pref = getSharedPreferences(pref_id, Context.MODE_PRIVATE)
        unlocked = pref!!.getBoolean(Main2Activity.pref_id_unlocked, false)
        darkTheme = pref!!.getBoolean(Main2Activity.pref_id_dark_theme, false) and unlocked

        //darkTheme = true
        if (darkTheme) {
            setTheme(R.style.AppTheme_Dark)
        } else {
            setTheme(R.style.AppTheme)
        }
    }

    public override fun onDestroy() {
        bp.release()
        super.onDestroy()
    }

    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)

        if (fragment is LearnFragment) {
            learnFragment = fragment
        } else if (fragment is ProjectFragment) {
            projectFragment = fragment
        }
    }

    private fun setUpViewPager() {
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {}

            override fun onTabUnselected(p0: TabLayout.Tab?) {}

            override fun onTabSelected(p0: TabLayout.Tab?) {
                container.currentItem = p0!!.position
                updateFabVisibility()
            }

        })
        container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                tabs.getTabAt(position)!!.select()
                updateFabVisibility()
            }

        })
    }

    fun updateFabVisibility() {
        if (container.currentItem == 1) {
            fab_add.show()
        } else {
            fab_add.hide()
        }
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> LearnFragment.newInstance()
                1 -> ProjectFragment.newInstance()
                else -> LearnFragment.newInstance()
            }
        }

        override fun getCount(): Int {
            return 2
        }

    }

    /**
     * Interface with Fragments
     */

    override fun isUnlocked(): Boolean = unlocked

    override fun unlockExtras() {
        MaterialDialog(this).show {
            title(R.string.unlock_extras)
            message(R.string.unlock_extras_description)
            negativeButton(R.string.later)
            positiveButton(R.string.unlock_now) {
                if (BillingProcessor.isIabServiceAvailable(this@Main3Activity) && bp.isOneTimePurchaseSupported) {
                    bp.loadOwnedPurchasesFromGoogle()
                    bp.purchase(this@Main3Activity, resources.getString(R.string.sku1))
                } else {
                    Tool.toast(this@Main3Activity, "IAB services not available")
                }
            }
        }
    }

    override fun isPermissionGranted(): Boolean = checkPermissions(false)


    /**
     * IAP Callbacks
     */

    override fun onBillingInitialized() {

    }

    override fun onPurchaseHistoryRestored() {
        unlocked = bp.isPurchased(resources.getString(R.string.sku1))
        pref!!.edit().putBoolean(Main2Activity.pref_id_unlocked, unlocked).apply()

        updateUnlockedState()
    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        unlocked = productId == resources.getString(R.string.sku1)

        updateUnlockedState()
    }

    private fun updateUnlockedState() {
        if (unlocked) {
            learnFragment.unlockTutorialState()
            pref!!.edit().putBoolean(Main2Activity.pref_id_unlocked, unlocked).apply()
            Tool.toast(this, "Unlock success!")
        }
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        Tool.toast(this, "Unable to unlock!!!")
    }

}
