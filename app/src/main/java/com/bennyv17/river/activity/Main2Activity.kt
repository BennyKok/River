package com.bennyv17.river.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Html
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.isAllGranted
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.bennyv17.river.BuildConfig
import com.bennyv17.river.R
import com.bennyv17.river.highlighter.syntax.RiveScriptSyntax
import com.bennyv17.river.highlighter.theme.RiveScriptDefaultTheme
import com.bennyv17.river.item.ProjectItem
import com.bennyv17.river.item.SimpleTextItem
import com.bennyv17.river.item.SimpleTutorialItem
import com.bennyv17.river.item.TutorialItemActionCallback
import com.bennyv17.river.template.RiveScriptTemplate
import com.bennyv17.river.tutorial.RiveScriptTutorial
import com.bennyv17.river.util.Tool
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.fastadapter.expandable.ExpandableExtension
import com.mikepenz.fastadapter.listeners.OnClickListener
import kotlinx.android.synthetic.main.activity_main2.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import java.io.File

class Main2Activity : AppCompatActivity(), BillingProcessor.IBillingHandler, TutorialItemActionCallback {

    fun onClick(v: View?) {
        when (v!!.id) {
            R.id.action_redo -> editor.redo()
            R.id.action_undo -> editor.undo()
            R.id.fab_play -> runScript()
//            R.id.label_header -> rename()
            R.id.file -> {
                goToProject()
            }
            R.id.settings -> settingsDialog()
            R.id.add_project -> newScript()
        }
    }

    private fun settingsDialog() {
        val dialog = MaterialDialog(this).show {
            title(text = "Settings")
            customView(R.layout.dialog_settings)
            negativeButton(R.string.about) {
                showAbout()
            }
            if (!unlocked)
                positiveButton(text = if (unlocked) getString(R.string.unlocked) else getString(R.string.unlock)) {
                    if (unlocked) {
                        MaterialDialog(this@Main2Activity)
                                .title(text = "Unlocked!")
                                .message(text = "Thank you so much, you have already unlocked. I will keep it up. Stay tuned for future updates.")
                                .positiveButton(text = "Got it")
                                .show()
                    } else
                        unlockExtras()
                }
            neutralButton(text = "Libraries") {
                LibsBuilder()
                        .withActivityStyle(if (darkTheme) Libs.ActivityStyle.DARK else Libs.ActivityStyle.LIGHT)
                        .withActivityTitle("Libraries")
                        .withLicenseShown(true)
                        .start(this@Main2Activity)
            }
        }

        with(dialog.getCustomView()!!) {
            val theme = findViewById<Switch>(R.id.switch1)
            val popup_typeface = findViewById<Button>(R.id.popup_typeface)
            val popup_text_size = findViewById<TextView>(R.id.popup_text_size)
            val popup_add = findViewById<Button>(R.id.popup_add)
            val popup_minus = findViewById<Button>(R.id.popup_minus)

            if (!unlocked)
                theme.isEnabled = false
            theme.setOnClickListener { it ->
                MaterialDialog(this@Main2Activity).show {
                    title(R.string.restart_title)
                    message(R.string.restart_description)
                    negativeButton(R.string.cancel) {
                        theme.isChecked = false
                    }
                    onDismiss {
                        theme.isChecked = false
                    }
                    positiveButton(R.string.restart) {
                        pref!!.edit().putBoolean(pref_id_dark_theme, theme.isChecked).apply()
                        recreate()
                    }
                }
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
                MaterialDialog(this@Main2Activity).show {
                    title(R.string.typeface)
                    listItemsSingleChoice(
                            res = R.array.editor_typeface,
                            initialSelection = pref!!.getInt(pref_id_editor_typeface, 0))
                    { dialog, index, text ->
                        pref!!.edit().putInt(pref_id_editor_typeface, index).apply()
                        popup_typeface.text = text

                        updateEditorTypeface()
                    }
                }

            }
        }

    }

    private fun rename() {
        val projectName = editingFile!!.nameWithoutExtension
        val file = editingFile!!

        MaterialDialog(this)
                .title(text = getString(R.string.rename) + " " + projectName)
                .input(hint = projectName) { _, input ->
                    var needToReopen = false
                    if (file == editingFile)
                        needToReopen = true

                    val newFile = File(file.parentFile, input.toString() + RIVE_SCRIPT_EXTENSION)
                    file.renameTo(newFile)

                    if (needToReopen) {
                        editingFile = newFile
                        editingFileName = newFile.nameWithoutExtension

                        openScript(newFile)
                    }

                    getAllScripts()
                }.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        Assent.handleResult(permissions, grantResults)
    }

    private fun runScript() {
        if (editor.text.isNullOrEmpty()) {
            Tool.toast(this, "Empty script.")
            return
        }
        saveEditingScript()
        if (editingFile == null)
            editingFile = File(PROJECT_DIR + editingFileName + RIVE_SCRIPT_EXTENSION)

        val intent = Intent(this, RiveScriptPlayground::class.java)
        intent.putExtra(extra_scrip_data, editingFile!!.absoluteFile.toString())
        startActivity(intent)
    }

    private var editingFile: File? = null
    private var editingFileName: String? = UNTITLED

    private var permissionsDenied = false

    private lateinit var allScripts: ArrayList<File>

    //Pref
    private var pref: SharedPreferences? = null
    private var dirPath: String = PROJECT_DIR
    private var editorTextSize: Int = 16
    private var darkTheme: Boolean = false
    private var unlocked: Boolean = false

    private lateinit var bp: BillingProcessor

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
        const val pref_id_editor_text_size = "editorTextSize"
    }

    private val projectAdapter: FastItemAdapter<ProjectItem> = FastItemAdapter()
    private var tutorialAdapter: FastItemAdapter<SimpleTutorialItem> = FastItemAdapter()
    private val expandableExtension = ExpandableExtension<SimpleTutorialItem>()

    private val s = "! version = 2.0\n\n+ hello bot\n- Hello human!"

    //on create
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Assent.setActivity(this, this)

        pref = getSharedPreferences(pref_id, Context.MODE_PRIVATE)
        editorTextSize = pref!!.getInt(pref_id_editor_text_size, editorTextSize)
        dirPath = pref!!.getString(pref_id_project_dir, PROJECT_DIR)

        unlocked = pref!!.getBoolean(pref_id_unlocked, false)
        darkTheme = pref!!.getBoolean(pref_id_dark_theme, false) and unlocked

        //darkTheme = true
        if (darkTheme) {
            setTheme(R.style.AppTheme_Dark)
        } else {
            setTheme(R.style.AppTheme)
        }

        setContentView(R.layout.activity_main2)

        tutorial_recycler.adapter = tutorialAdapter
        project_recycler.adapter = projectAdapter

        if (checkPermissions(true)) {
            getAllScripts()
            resumeLastSession()
        }

        initTutorial()
        initEditor()
        initSymbolToolBar()

        (action_redo as ImageButton).setImageResource(R.drawable.ic_redo_24dp)
        (action_undo as ImageButton).setImageResource(R.drawable.ic_undo_24dp)
        (settings as ImageButton).setImageResource(R.drawable.ic_outline_settings_24px)
        (add_project as ImageButton).setImageResource(R.drawable.ic_add_24dp)

        bp = BillingProcessor.newBillingProcessor(this, resources.getString(R.string.licence_key), this)
        bp.initialize()

        if (checkPermissions(false) && pref!!.getString("previousVersion", "") != BuildConfig.VERSION_NAME) {
            pref!!.edit().putString("previousVersion", BuildConfig.VERSION_NAME).apply()
            showChangelog()
        }

        if (pref!!.getBoolean("firstStart", true)) {
            editor.setText(s)

            pref!!.edit {
                putBoolean("firstStart", false)
            }
        }

        hideEditButton()
        goToProject()
    }

    private fun initTutorial() {
        val items = RiveScriptTutorial.getTutorialItems(this)
        items.forEach {
            if (it.locked)
                it.withLocked(!unlocked)

            it.withOnClickListener(OnClickListener { v, adapter, item, position ->
                if (item.locked)
                    unlockExtras()
                false
            })
        }
        tutorialAdapter.addExtension(expandableExtension)
        expandableExtension.withOnlyOneExpandedItem(true)
        tutorialAdapter.add(items)
    }

    private fun initSymbolToolBar() {
        val symbolListAdapter = FastItemAdapter<SimpleTextItem>()
        editor_symbol_tool_bar.adapter = symbolListAdapter

        val itemList = ALL_SYMBOLS.indices.map { SimpleTextItem(ALL_SYMBOLS[it]) }
        symbolListAdapter.add(itemList)

        symbolListAdapter.withOnClickListener { _, _, item, position ->
            if (position == 0)
                editor.text?.insert(editor.selectionStart, "\t")
            else
                editor.text?.insert(editor.selectionStart, item.text)

            if (item.text!!.length >= 2 && item.text!! != "//") {
                editor.setSelection(editor.selectionStart - 1)
            }
            true
        }
    }

    private fun goToProject() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        beginTrans()

        hideEditButton()

        editor_card_container.visibility = View.GONE
        bar_container.visibility = View.GONE
        label_header.visibility = View.GONE

        label_project_header.visibility = View.VISIBLE
        label_project_header2.visibility = View.VISIBLE
        project_card_container.visibility = View.VISIBLE
        learn_card_container.visibility = View.VISIBLE
        add_project.visibility = View.VISIBLE
    }

    private fun goToEdit(setEditor: Boolean = true) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        beginTrans()

        showEditButton()
        if (setEditor)
            setEditor(editingFile)

        focusEditor()

        editor_card_container.visibility = View.VISIBLE
        bar_container.visibility = View.VISIBLE
        label_header.visibility = View.VISIBLE

        label_project_header.visibility = View.GONE
        label_project_header2.visibility = View.GONE
        project_card_container.visibility = View.GONE
        learn_card_container.visibility = View.GONE
        add_project.visibility = View.GONE
    }

    private fun beginTrans() {
        TransitionManager.beginDelayedTransition(scene_root, TransitionSet()
                .addTransition(Fade()
                        .addTarget(label_header)
                        .addTarget(label_project_header)
                        .addTarget(label_project_header2)
                        .addTarget(project_card_container)
                        .addTarget(learn_card_container)
                        .addTarget(add_project)
                        .setDuration(200L)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                )
                .addTransition(Slide(Gravity.END)
                        .setDuration(300L)
                        .addTarget(editor_card_container)
                        .setInterpolator(OvershootInterpolator(0.5f))
                )
                .addTransition(Slide(Gravity.BOTTOM)
                        .addTarget(bar_container)
                        .setDuration(300L)
                        .setInterpolator(OvershootInterpolator(0.3f))
                )
                .setOrdering(TransitionSet.ORDERING_TOGETHER)
        )
    }

    private fun hideEditButton() {
        fab_play.hide()
    }

    private fun showEditButton() {
        fab_play.show()
    }

    private fun checkPermissions(request: Boolean): Boolean {
//        return if (!Assent.isPermissionGranted(Assent.READ_EXTERNAL_STORAGE) || !Assent.isPermissionGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
//            if (request)
//                Assent.requestPermissions(AssentCallback { result ->
//                    if (!result.allPermissionsGranted()) {
//                        permissionsDenied = true
//                        Toast.makeText(this, "River will not be able to work without those permission", Toast.LENGTH_SHORT).show()
//                        finish()
//                    } else {
//                        getAllScripts()
//                        resumeLastSession()
//                        showChangelog()
//                    }
//                }, 69, Assent.READ_EXTERNAL_STORAGE, Assent.WRITE_EXTERNAL_STORAGE)
//            false
//        } else {
//            true
//        }
        return if (!isAllGranted(Permission.READ_EXTERNAL_STORAGE) || !isAllGranted(Permission.WRITE_EXTERNAL_STORAGE)) {
            if (request) {
                askForPermissions(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE) { result ->
                    if (!result.isAllGranted(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)) {
                        permissionsDenied = true
                        Toast.makeText(this, "River will not be able to work without those permission", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        getAllScripts()
                        resumeLastSession()
                        showChangelog()
                    }
                }
            }
            false
        } else {
            true
        }
    }

    override fun onTryCode(code: String) {
        saveEditingScript()
        setBlankScript("Try Code")
        goToEdit()
        editor.setText("! version = 2.0\n\n$code")
    }

    private fun resumeLastSession() {
        val lastOpenedPath = pref!!.getString(pref_id_last_opened_script, null)

        if (!lastOpenedPath.isNullOrEmpty()) {
            editingFile = File(lastOpenedPath)

            if (editingFile!!.exists()) {
                editingFileName = editingFile!!.nameWithoutExtension
                title = editingFileName

                editor.setText(Tool.getProject(editingFile!!))
                editor.callOnClick()
                editor.post {
                    editor.setSelection(0)
                }
                editor.clearHistory()
            } else {
                editingFile = null
            }
        }
    }

    override fun onBillingInitialized() {

    }

    override fun onPurchaseHistoryRestored() {
        unlocked = bp.isPurchased(resources.getString(R.string.sku1))
        pref!!.edit().putBoolean(pref_id_unlocked, unlocked).apply()

        updateUnlockedState()
    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        unlocked = productId == resources.getString(R.string.sku1)

        updateUnlockedState()
    }

    private fun updateUnlockedState() {
        if (unlocked) {
            tutorialAdapter.adapterItems.forEach {
                it.withLocked(false)
            }
            tutorialAdapter.notifyAdapterDataSetChanged()

            pref!!.edit().putBoolean(pref_id_unlocked, unlocked).apply()
            Tool.toast(this, "Unlock success!")
        }
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        Tool.toast(this, "Unable to unlock!!!")
    }

    private fun unlockExtras() {
        MaterialDialog(this).show {
            title(R.string.unlock_extras)
            message(R.string.unlock_extras_description)
            negativeButton(R.string.later)
            positiveButton(R.string.unlock_now) {
                if (BillingProcessor.isIabServiceAvailable(this@Main2Activity) && bp.isOneTimePurchaseSupported) {
                    bp.loadOwnedPurchasesFromGoogle()
                    bp.purchase(this@Main2Activity, resources.getString(R.string.sku1))
                } else {
                    Tool.toast(this@Main2Activity, "IAB services not available")
                }
            }
        }
    }

    public override fun onDestroy() {
        bp.release()
        super.onDestroy()
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
            negativeButton(text = "Email") {
                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto: itechbenny@gmail.com"))
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback on River(IDE for RiveScript)")
                startActivity(Intent.createChooser(intent, "Send Feedback"))
            }

        }
    }


    override fun onResume() {
        super.onResume()

        if (checkPermissions(false))
            getAllScripts()

//        Assent.setActivity(this, this)
    }

    override fun onPause() {
        if (editingFile != null)
            pref!!.edit().putString(pref_id_last_opened_script, editingFile!!.absolutePath).apply()

        saveEditingScript()

//        if (isFinishing)
//            Assent.setActivity(this, null)

        super.onPause()
    }

    fun showThemeDialog() {
        if (unlocked) {
            MaterialDialog(this)
                    .title(R.string.theme_)
                    .listItemsSingleChoice(
                            initialSelection = if (pref!!.getBoolean(pref_id_dark_theme, false)) 1 else 0,
                            items = arrayListOf(getString(R.string.default_), getString(R.string.dark))
                    ) { dialog, index, text ->
                        MaterialDialog(this).title(R.string.restart_title)
                                .message(R.string.restart_description)
                                .negativeButton(R.string.cancel)
                                .positiveButton(R.string.restart) {
                                    pref!!.edit().putBoolean(pref_id_dark_theme, index == 1).apply()
                                    recreate()
                                }
                                .show()
                    }
                    .show()
        } else {
            unlockExtras()
        }
    }

    private fun updateEditorTypeface() {
        editor.typeface = when (pref!!.getInt(pref_id_editor_typeface, 0)) {
            0 -> Typeface.DEFAULT
            1 -> Typeface.MONOSPACE
            2 -> ResourcesCompat.getFont(this, R.font.noto_sans)
            else -> Typeface.DEFAULT
        }
    }

    private fun saveTextSize() {
        editor.setTextSize(TypedValue.COMPLEX_UNIT_SP, editorTextSize.toFloat())
        pref!!.edit().putInt(pref_id_editor_text_size, editorTextSize).apply()
    }

    private fun getAllScripts() {
        allScripts = Tool.listDirectory(dirPath, RIVE_SCRIPT_EXTENSION)

        projectAdapter.clear()
        allScripts.forEach {
            projectAdapter.add(ProjectItem(it.nameWithoutExtension)
                    .withClickListener {
                        setScript(it)
                        goToEdit()
                    }
                    .withLongClickListener {
                        val projectName = it.nameWithoutExtension
                        val items = arrayListOf(getString(R.string.rename), getString(R.string.delete))

                        MaterialDialog(this@Main2Activity).show {
                            title(text = projectName)
                            listItems(items = items) { dialog, index, text ->
                                when (index) {
                                    0 -> {
                                        MaterialDialog(this@Main2Activity).show {
                                            title(text = getString(R.string.rename) + " " + projectName)
                                            input(hint = projectName) { _, input ->
                                                var needToReopen = false
                                                if (file == editingFile)
                                                    needToReopen = true

                                                val newFile = File(it.parentFile, input.toString() + RIVE_SCRIPT_EXTENSION)
                                                it.renameTo(newFile)

                                                if (needToReopen) {
                                                    editingFile = newFile
                                                    editingFileName = newFile.nameWithoutExtension

                                                    openScript(newFile)
                                                }

                                                getAllScripts()
                                            }
                                        }
                                    }
                                    1 -> {
                                        MaterialDialog(this@Main2Activity).show {
                                            title(text = getString(R.string.delete) + " " + projectName)
                                            message(R.string.are_you_sure)
                                            negativeButton(R.string.cancel)
                                            positiveButton(R.string.confirm) { dialog ->
                                                var needToReopen = false
                                                if (it == editingFile)
                                                    needToReopen = true
                                                if (it.delete())
                                                    Tool.toast(this@Main2Activity, getString(R.string.deleted))
                                                else
                                                    Tool.toast(this@Main2Activity, getString(R.string.unable_to_delete))
                                                if (needToReopen)
                                                    setBlankScript()

                                                getAllScripts()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
            )
        }

//        projectAdapter.add(ProjectItem("+ New Script").withClickListener {
//            newScript()
//        })
    }

    private fun newScript() {
        MaterialDialog(this)
                .title(text = "Create new chatbot")
                .listItems(items = arrayListOf("New", "From Template")) { dialog, index, text ->
                    when (index) {
                        0 -> {
                            MaterialDialog(this)
                                    .title(text = "New Bot Name")
                                    .input { _, input ->
                                        setBlankScript(Tool.stripExtension(input.toString()))
                                        goToEdit()
                                        saveEditingScript()
                                        getAllScripts()
                                    }.show()
                        }
                        1 -> {
                            MaterialDialog(this)
                                    .listItems(items = RiveScriptTemplate.getTemplateTitles())
                                    { dialog, index, text ->
                                        MaterialDialog(this)
                                                .title(text = "Template Bot Name")
                                                .input { _, input ->
                                                    val temp = RiveScriptTemplate.getTemplate()[index]

                                                    setBlankScript(
                                                            Tool.stripExtension(input.toString()),
                                                            "! version = 2.0\n\n$temp"
                                                    )
                                                    goToEdit(false)

                                                    saveEditingScript()
                                                    getAllScripts()
                                                }.show()
                                    }
                                    .show()
                        }
                    }
                }.show()


    }

    private fun setBlankScript() {
        setBlankScript(UNTITLED)
    }

    private fun setBlankScript(name: String, code: String? = null) {
        editingFile = null
        editingFileName = name

        updateTitleName()
        editor.setText(code)
        editor.callOnClick()
    }

    private fun openScript(file: File) {
        setScript(file)
        setEditor(file)
    }

    private fun setEditor(file: File?) {
        updateTitleName()

        if (file != null) {
            val script: String = Tool.getProject(file)
            editor.setText(script)
        } else {
            editor.setText(null)
        }
    }

    private fun focusEditor() {
        editor.callOnClick()

        editor.highlightText()
        editor.clearHistory()
    }

    private fun setScript(file: File) {
        if (editor != null)
            saveEditingScript()

        editingFile = file

        editingFileName = file.nameWithoutExtension
    }

    private fun updateTitleName() {
        title = editingFileName
    }

    private fun saveEditingScript() {
        if (!editingFileName.isNullOrEmpty() && !permissionsDenied && !(editingFileName == UNTITLED && (editor.text.isNullOrEmpty() || editor.text.toString() == s))) {
            editingFile = Tool.saveScript(editingFileName!!, editor.text.toString())
        }
    }

    private fun showChangelog() {
        MaterialDialog(this)
                .title(R.string.changelog)
                .customView(R.layout.dialog_changelog)
                .positiveButton(text = "Continue")
                .show()
    }

    override fun setTitle(title: CharSequence?) {
        label_header.text = title
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_redo -> {
                editor.redo()
                true
            }
            R.id.action_undo -> {
                editor.undo()
                true
            }
            else -> {
                false
            }
        }
    }

    override fun onBackPressed() {
        if (project_card_container.visibility == View.GONE) {
            goToProject()
        } else {
            super.onBackPressed()
        }
    }

    private fun initEditor() {
        editor.setHorizontallyScrolling(true)
        editor.syntax = RiveScriptSyntax
        editor.syntaxTheme = RiveScriptDefaultTheme

        editor.setTextSize(TypedValue.COMPLEX_UNIT_SP, editorTextSize.toFloat())

        KeyboardVisibilityEvent.setEventListener(this) { isOpen ->
            if (isOpen) {

            } else {

            }
        }
    }


}
