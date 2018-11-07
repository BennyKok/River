//package com.bennyv17.river.activity
//
//import android.content.Context
//import android.content.Intent
//import android.content.SharedPreferences
//import android.graphics.Color
//import android.graphics.Point
//import android.graphics.Typeface
//import android.graphics.drawable.ColorDrawable
//import android.net.Uri
//import android.os.Bundle
//import android.os.Environment
//import androidx.core.content.res.ResourcesCompat
//import androidx.drawerlayout.widget.DrawerLayout
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import android.util.TypedValue
//import android.view.Gravity
//import android.view.Menu
//import android.view.MenuItem
//import android.view.View
//import android.widget.*
//import com.afollestad.assent.Assent
//import com.afollestad.assent.AssentActivity
//import com.afollestad.assent.AssentCallback
//import com.afollestad.materialdialogs.MaterialDialog
//import com.afollestad.materialdialogs.folderselector.FolderChooserDialog
//import com.anjlab.android.iab.v3.BillingProcessor
//import com.anjlab.android.iab.v3.TransactionDetails
//import com.bennyv17.river.BuildConfig
//import com.bennyv17.river.PrivateData
//import com.bennyv17.river.R
//import com.bennyv17.river.highlighter.syntax.RiveScriptSyntax
//import com.bennyv17.river.highlighter.theme.RiveScriptDefaultTheme
//import com.bennyv17.river.item.SimpleTextItem
//import com.bennyv17.river.item.SimpleTutorialItem
//import com.bennyv17.river.item.TutorialItemActionCallback
//import com.bennyv17.river.template.RiveScriptTemplate
//import com.bennyv17.river.tutorial.RiveScriptTutorial
//import com.bennyv17.river.util.Tool
//import com.getkeepsafe.taptargetview.TapTarget
//import com.getkeepsafe.taptargetview.TapTargetView
//import com.mikepenz.aboutlibraries.Libs
//import com.mikepenz.aboutlibraries.LibsBuilder
//import com.mikepenz.fastadapter.FastAdapter
//import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
//import com.mikepenz.itemanimators.SlideInOutLeftAnimator
//import com.mikepenz.materialdrawer.Drawer
//import com.mikepenz.materialdrawer.DrawerBuilder
//import com.mikepenz.materialdrawer.model.DividerDrawerItem
//import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
//import kotlinx.android.synthetic.main.activity_main.*
//import kotlinx.android.synthetic.main.header_main_left_drawer.*
//import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
//import java.io.File
//
//class MainActivity : AssentActivity()
//        , FolderChooserDialog.FolderCallback
//        , TutorialItemActionCallback
//        , BillingProcessor.IBillingHandler {
//
//    companion object {
//        val PROJECT_DIR = Environment.getExternalStorageDirectory().path + ("/River/Project")
//        val RIVE_SCRIPT_EXTENSION = ".rive"
//        val UNTITLED = "Untitled"
//        val ALL_SYMBOLS = arrayOf("⇥", "!", "+", "-", "*", "#", "//", "_", "^", "{}", "{/}", "()", "<>", "=", "<", ">", "[]", "|", "@")
//
//        //Extra
//        val extra_scrip_data = "script_data"
//
//        //Pref ID
//        val pref_id = "riverPref"
//        val pref_id_unlocked = "unlocked"
//        val pref_id_dark_theme = "darkTheme"
//        val pref_id_project_dir = "projectDir"
//        val pref_id_last_opened_script = "lastOpenedScript"
//        val pref_id_editor_typeface = "editorTypeface"
//        val pref_id_editor_text_size = "editorTextSize"
//    }
//
//
//    private val onDrawerItemClickListener = Drawer.OnDrawerItemClickListener { _, position, _ ->
//        if (position > allScripts.size - 1) return@OnDrawerItemClickListener false
//
//        openScript(allScripts[position])
//        leftDrawer.closeDrawer()
//        true
//    }
//
//    private val onDrawerItemLongClickListener = Drawer.OnDrawerItemLongClickListener { _, position_0, _ ->
//        if (position_0 >= allScripts.size) return@OnDrawerItemLongClickListener false
//
//        val projectName = allScripts[position_0].nameWithoutExtension
//        val file = allScripts[position_0]
//
//        MaterialDialog.Builder(this@MainActivity)
//                .title(projectName)
//                .items(getString(R.string.rename), getString(R.string.delete))
//                .itemsCallback { _, _, position, _ ->
//                    when (position) {
//                        0 -> {
//                            MaterialDialog.Builder(this)
//                                    .title(getString(R.string.rename) + " " + projectName)
//                                    .input("", projectName, false, { _, input ->
//                                        var needToReopen = false
//                                        if (file == editingFile)
//                                            needToReopen = true
//
//                                        val newFile = File(file.parentFile, input.toString() + RIVE_SCRIPT_EXTENSION)
//                                        file.renameTo(newFile)
//
//                                        allScripts[position_0] = newFile
//
//                                        if (needToReopen) {
//                                            editingFile = newFile
//                                            editingFileName = newFile.nameWithoutExtension
//
//                                            openScript(newFile)
//                                        }
//
//                                        getAllScripts()
//                                    }).show()
//                        }
//                        1 -> {
//                            MaterialDialog.Builder(this)
//                                    .title(getString(R.string.delete) + " " + projectName)
//                                    .content(getString(R.string.are_you_sure))
//                                    .positiveText(getString(R.string.confirm))
//                                    .negativeText(getString(R.string.cancel))
//                                    .onPositive { _, _ ->
//                                        var needToReopen = false
//                                        if (file == editingFile)
//                                            needToReopen = true
//                                        if (file.delete())
//                                            Tool.toast(this, getString(R.string.deleted))
//                                        else
//                                            Tool.toast(this, getString(R.string.unable_to_delete))
//                                        if (needToReopen)
//                                            setBlankScript()
//
//                                        getAllScripts()
//                                        leftDrawer.deselect()
//                                    }
//                                    .show()
//                        }
//                    }
//                }
//                .show()
//
//        true
//    }
//
//    private val drawerListener: androidx.drawerlayout.widget.DrawerLayout.DrawerListener = object : androidx.drawerlayout.widget.DrawerLayout.DrawerListener {
//        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
//        }
//
//        override fun onDrawerClosed(drawerView: View) {
//        }
//
//        override fun onDrawerOpened(drawerView: View) {
//        }
//
//        override fun onDrawerStateChanged(newState: Int) {
//            editor.clearFocus()
//        }
//    }
//
//
//    private val rightDrawerListener: androidx.drawerlayout.widget.DrawerLayout.DrawerListener = object : androidx.drawerlayout.widget.DrawerLayout.DrawerListener {
//        override fun onDrawerStateChanged(newState: Int) {
//            editor.clearFocus()
//            leftDrawerSliding = false
//        }
//
//        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
//            leftDrawerSliding = true
//        }
//
//        override fun onDrawerClosed(drawerView: View) {
//            rightDrawer.drawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNDEFINED)
//        }
//
//        override fun onDrawerOpened(drawerView: View) {
//            rightDrawer.drawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_OPEN)
//        }
//    }
//
//    private var editingFile: File? = null
//    private var editingFileName: String? = UNTITLED
//
//    private var permissionsDenied = false
//
//    private lateinit var leftDrawer: Drawer
//    private lateinit var rightDrawer: Drawer
//
//    private lateinit var allScripts: ArrayList<File>
//
//    private lateinit var tutorialAdapter: FastItemAdapter<SimpleTutorialItem>
//
//    private var leftDrawerSliding = false
//
//    //Menu Item
//    private var menu_undo: MenuItem? = null
//    private var menu_redo: MenuItem? = null
//    private var menu_run: MenuItem? = null
//    private var menu_text: MenuItem? = null
//
//    //Pref
//    private var pref: SharedPreferences? = null
//    private var dirPath: String = PROJECT_DIR
//    private var editorTextSize: Int = 16
//    private var darkTheme: Boolean = false
//    private var unlocked: Boolean = false
//
//    private lateinit var bp: BillingProcessor
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        pref = getSharedPreferences(pref_id, Context.MODE_PRIVATE)
//        editorTextSize = pref!!.getInt(pref_id_editor_text_size, editorTextSize)
//        dirPath = pref!!.getString(pref_id_project_dir, PROJECT_DIR)
//
//        unlocked = pref!!.getBoolean(pref_id_unlocked, false)
//        darkTheme = pref!!.getBoolean(pref_id_dark_theme, false) and unlocked
//
//        if (darkTheme) {
//            setTheme(R.style.AppTheme_Dark)
//        } else {
//            setTheme(R.style.AppTheme)
//        }
//
//        setContentView(R.layout.activity_main)
//        setSupportActionBar(toolbar)
//
//        toolbar.subtitle = UNTITLED
//
//        initEditor()
//        updateEditorTypeface()
//
//        initSymbolToolBar()
//        initDrawer(savedInstanceState)
//        if (checkPermissions(true)) {
//            getAllScripts()
//            resumeLastSession()
//        }
//        fab.setOnClickListener {
//            rightDrawer.openDrawer()
//        }
//
//        KeyboardVisibilityEvent.setEventListener(this) { isOpen ->
//            if (isOpen) {
//                fab.hide()
//                menu_run?.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
//                menu_text?.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
//
//                menu_redo?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
//                menu_undo?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
//
//            } else {
//                fab.show()
//                menu_run?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
//                menu_text?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
//
//                menu_redo?.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
//                menu_undo?.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
//            }
//        }
//
//        bp = BillingProcessor.newBillingProcessor(this, PrivateData.licenceKey, this)
//        bp.initialize()
//
//        if (checkPermissions(false) && pref!!.getString("previousVersion", "") != BuildConfig.VERSION_NAME) {
//            pref!!.edit().putString("previousVersion", BuildConfig.VERSION_NAME).apply()
//            showChangelog()
//        }
//
//        if (pref!!.getBoolean("firstStart", true)) {
//            editor.setText("! version = 2.0\n\n+ hello bot\n- Hello human!")
//            startActivity(Intent(this, IntroActivity::class.java))
//            TapTargetView.showFor(this,
//                    TapTarget.forView(fab, "Learn", "Get started by browsing through the tutorials.")
//                            .transparentTarget(false)
//                            .icon(getDrawable(R.drawable.ic_live_help_24dp))
//                            .outerCircleAlpha(0.8f),
//                    object : TapTargetView.Listener() {
//                        override fun onTargetClick(view: TapTargetView?) {
//                            pref!!.edit().putBoolean("firstStart", false).apply()
//                            rightDrawer.openDrawer()
//                            super.onTargetClick(view)
//                        }
//                    }
//            )
//        }
//
//        //Log.d();
//    }
//
//    private fun showChangelog() {
//        MaterialDialog.Builder(this)
//                .title(getString(R.string.changelog))
//                .customView(R.layout.dialog_changelog, false)
//                .positiveText("Continue")
//                .show()
//    }
//
//    private fun getAllScripts() {
//        allScripts = Tool.listDirectory(dirPath, RIVE_SCRIPT_EXTENSION)
//
//        leftDrawer.removeAllItems()
//        allScripts
//                .map { SecondaryDrawerItem().withName(it.nameWithoutExtension).withIcon(if (!darkTheme) R.drawable.ic_description_24dp else R.drawable.ic_description_white_24dp) }
//                .forEach { leftDrawer.addItem(it) }
//
//        leftDrawer.addItems(
//                SecondaryDrawerItem().withName(R.string.new_script)
//                        .withIcon(if (!darkTheme) R.drawable.ic_add_24dp else R.drawable.ic_add_white_24dp)
//                        .withSelectable(false)
//                        .withOnDrawerItemClickListener { _, _, _ ->
//                            newScript()
//                            true
//                        }
//                ,
//                DividerDrawerItem()
//                ,
//                SecondaryDrawerItem()
//                        .withName(getString(R.string.template))
//                        .withDescription(getString(R.string.select_template_description))
//                        .withSelectable(false)
//                        .withOnDrawerItemClickListener { _, _, _ ->
//                            MaterialDialog.Builder(this)
//                                    .items(RiveScriptTemplate.getTemplateTitles())
//                                    .itemsCallback { _, _, position, _ ->
//                                        val temp = RiveScriptTemplate.getTemplate()[position]
//                                        editor.setText("! version = 2.0\n\n$temp")
//                                    }
//                                    .show()
//                            true
//                        }
//        )
//    }
//
//
//    private fun initDrawer(savedInstanceState: Bundle?) {
//        leftDrawer = DrawerBuilder().withActivity(this)
//                .withToolbar(toolbar)
//                .withStickyHeader(R.layout.header_main_left_drawer)
//                .withTranslucentStatusBar(true)
//                .withDrawerGravity(Gravity.START)
//                .withOnDrawerItemClickListener(onDrawerItemClickListener)
//                .withOnDrawerItemLongClickListener(onDrawerItemLongClickListener)
//                .withSavedInstance(savedInstanceState)
//                .build()
//
//
//        leftDrawer.drawerLayout.addDrawerListener(drawerListener)
//
//        header_path_text_view.text = dirPath
//        header_open_folder.setOnClickListener {
//            FolderChooserDialog.Builder(this)
//                    .initialPath(dirPath)
//                    .goUpLabel("/...")
//                    .show(supportFragmentManager)
//        }
//
//        val tutorial_view = layoutInflater.inflate(R.layout.right_drawer_tutorial, null)
//        rightDrawer = DrawerBuilder().withActivity(this)
//                .withTranslucentStatusBar(true)
//                .withDrawerGravity(Gravity.END)
//                .withCustomView(tutorial_view)
//                .withSavedInstance(savedInstanceState)
//                .build()
//
//        rightDrawer.drawerLayout.addDrawerListener(rightDrawerListener)
//        val pt = Point()
//        rightDrawer.drawerLayout.setOnTouchListener { _, event ->
//            if (leftDrawerSliding) return@setOnTouchListener false
//            val x = event.x.toInt()
//            val drawerWidth = resources.getDimension(R.dimen.material_drawer_width).toInt()
//            windowManager.defaultDisplay.getSize(pt)
//            if (x < pt.x - drawerWidth) {
//                rightDrawer.closeDrawer()
//                return@setOnTouchListener true
//            }
//            return@setOnTouchListener false
//        }
//
//
//        val tutorial_recycler: androidx.recyclerview.widget.RecyclerView = tutorial_view.findViewById(R.id.tutorial_recycler)
//        tutorialAdapter = FastItemAdapter<SimpleTutorialItem>()
//        tutorialAdapter.withPositionBasedStateManagement(false)
//
//        tutorial_recycler.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
//        tutorial_recycler.itemAnimator = SlideInOutLeftAnimator(tutorial_recycler)
//        tutorial_recycler.adapter = tutorialAdapter
//
//        tutorialAdapter.withSavedInstanceState(savedInstanceState)
//        val items = RiveScriptTutorial.getTutorialItems(this)
//        items.forEach {
//            if (it.locked)
//                it.withLocked(!unlocked)
//
//            it.withOnClickListener(FastAdapter.OnClickListener { v, adapter, item, position ->
//                if (item.locked)
//                    unlockExtras()
//                true
//            })
//        }
//        tutorialAdapter.withOnlyOneExpandedItem(true)
//        tutorialAdapter.add(items)
//
//        val header_collapse: ImageButton = tutorial_view.findViewById(R.id.header_collapse)
//        header_collapse.setOnClickListener {
//            tutorialAdapter.collapse()
//        }
//    }
//
//    override fun onTryCode(code: String) {
//        saveEditingScript()
//        setBlankScript()
//        editor.setText("! version = 2.0\n\n$code")
//        rightDrawer.closeDrawer()
//        TapTargetView.showFor(this,
//                TapTarget.forToolbarMenuItem(toolbar, R.id.action_run, "", "")
//                        .transparentTarget(false)
//                        .cancelable(true)
//                        .outerCircleAlpha(0.3f),
//                object : TapTargetView.Listener() {
//                    override fun onTargetClick(view: TapTargetView?) {
//                        runScript()
//                        super.onTargetClick(view)
//                    }
//                }
//        )
//    }
//
//    private fun resumeLastSession() {
//        val lastOpenedPath = pref!!.getString(pref_id_last_opened_script, null)
//
//        if (!lastOpenedPath.isNullOrEmpty()) {
//            editingFile = File(lastOpenedPath)
//
//            if (editingFile!!.exists()) {
//                editingFileName = editingFile!!.nameWithoutExtension
//                toolbar.subtitle = editingFileName
//
//                editor.setText(Tool.getProject(editingFile!!))
//                editor.callOnClick()
//                editor.post({
//                    editor.setSelection(0)
//                })
//                editor.clearHistory()
//            } else {
//                editingFile = null
//            }
//        }
//    }
//
//    private fun checkPermissions(request: Boolean): Boolean {
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
//    }
//
//    private fun initEditor() {
//        editor.setHorizontallyScrolling(true)
//        editor.syntax = RiveScriptSyntax
//        editor.syntaxTheme = RiveScriptDefaultTheme
//
//        editor.setTextSize(TypedValue.COMPLEX_UNIT_SP, editorTextSize.toFloat())
//    }
//
//    private fun initSymbolToolBar() {
//        editor_symbol_tool_bar.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
//        val symbolListAdapter = FastItemAdapter<SimpleTextItem>()
//        editor_symbol_tool_bar.adapter = symbolListAdapter
//
//        val itemList = ALL_SYMBOLS.indices.map { SimpleTextItem(ALL_SYMBOLS[it]) }
//        symbolListAdapter.add(itemList)
//
//        symbolListAdapter.withOnClickListener { _, _, item, position ->
//            if (position == 0)
//                editor.text?.insert(editor.selectionStart, "\t")
//            else
//                editor.text?.insert(editor.selectionStart, item.text)
//
//            if (item.text!!.length >= 2 && item.text!! != "//") {
//                editor.setSelection(editor.selectionStart - 1)
//            }
//
//            true
//        }
//    }
//
//    override fun onFolderSelection(dialog: FolderChooserDialog, folder: File) {
//        dirPath = folder.absolutePath
//        header_path_text_view.text = dirPath
//        pref!!.edit().putString(pref_id_project_dir, dirPath).apply()
//        getAllScripts()
//    }
//
//    override fun onFolderChooserDismissed(dialog: FolderChooserDialog) {
//
//    }
//
//    override fun onBillingInitialized() {
//
//    }
//
//    override fun onPurchaseHistoryRestored() {
//        unlocked = bp.isPurchased(PrivateData.sku1)
//        pref!!.edit().putBoolean(pref_id_unlocked, unlocked).apply()
//
//        updateUnlockedState()
//    }
//
//    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
//        unlocked = productId == PrivateData.sku1
//
//        updateUnlockedState()
//    }
//
//    private fun updateUnlockedState() {
//        if (unlocked) {
//            tutorialAdapter.adapterItems.forEach {
//                it.withLocked(false)
//            }
//            tutorialAdapter.notifyAdapterDataSetChanged()
//
//            pref!!.edit().putBoolean(pref_id_unlocked, unlocked).apply()
//            Tool.toast(this, "Unlock success!")
//        }
//    }
//
//    override fun onBillingError(errorCode: Int, error: Throwable?) {
//        Tool.toast(this, "Unable to unlock!!!")
//    }
//
//    private fun unlockExtras() {
//        MaterialDialog.Builder(this)
//                .iconRes(R.drawable.ic_lock_open_24dp)
//                .title(getString(R.string.unlock_extras))
//                .content(getString(R.string.unlock_extras_description))
//                .negativeText(getString(R.string.later))
//                .positiveText(getString(R.string.unlock_now))
//                .onPositive { _, _ ->
//                    if (BillingProcessor.isIabServiceAvailable(this) && bp.isOneTimePurchaseSupported) {
//                        bp.loadOwnedPurchasesFromGoogle()
//                        bp.purchase(this, PrivateData.sku1)
//                    } else {
//                        Tool.toast(this, "IAB services not available")
//                    }
//                }
//                .show()
//    }
//
//    public override fun onDestroy() {
//        bp.release()
//        super.onDestroy()
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.action_run -> {
//                runScript()
//                true
//            }
//            R.id.action_theme -> {
//                if (unlocked) {
//                    MaterialDialog.Builder(this).title(getString(R.string.theme_)).items(getString(R.string.default_), getString(R.string.dark)).itemsCallbackSingleChoice(
//                            if (pref!!.getBoolean(pref_id_dark_theme, false)) 1 else 0
//                    )
//                    { _, _, which, _ ->
//                        MaterialDialog.Builder(this).title(getString(R.string.restart_title))
//                                .content(getString(R.string.restart_description))
//                                .negativeText(R.string.cancel)
//                                .positiveText(getString(R.string.restart))
//                                .onPositive { _, _ ->
//                                    pref!!.edit().putBoolean(pref_id_dark_theme, which == 1).apply()
//                                    recreate()
//                                }.show()
//                        true
//                    }.show()
//                } else {
//                    unlockExtras()
//                }
//                true
//            }
//            R.id.action_changelog -> {
//                showChangelog()
//                true
//            }
//            R.id.action_save -> {
//                Tool.toast(this, "Saved")
//                saveEditingScript()
//                true
//            }
//            R.id.action_redo -> {
//                editor.redo()
//                true
//            }
//            R.id.action_undo -> {
//                editor.undo()
//                true
//            }
//            R.id.action_intro -> {
//                pref!!.edit().putBoolean("firstStart", true).apply()
//                recreate()
//                true
//            }
//            R.id.action_about -> {
//                MaterialDialog.Builder(this)
//                        .iconRes(R.mipmap.ic_launcher)
//                        .title(R.string.app_name)
//                        .content(R.string.river_about, true)
//                        .negativeText("GitHub")
//                        .onNegative { _, _ ->
//                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/BennyKok"))
//                            startActivity(browserIntent)
//                        }
//                        .positiveText(if (unlocked) getString(R.string.unlocked) else getString(R.string.unlock))
//                        .onPositive { _, _ ->
//                            if (unlocked) {
//                                MaterialDialog.Builder(this)
//                                        .title("Unlocked!")
//                                        .content("Thank you so much, you have already unlocked. I will keep it up. Stay tuned for future updates.")
//                                        .positiveText("Got it")
//                                        .show()
//                            } else
//                                unlockExtras()
//                        }
//                        .neutralText("Libraries")
//                        .onNeutral { _, _ ->
//                            LibsBuilder()
//                                    .withActivityStyle(if (darkTheme) Libs.ActivityStyle.DARK else Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
//                                    .withActivityTitle("Libraries")
//                                    .withLicenseShown(true)
//                                    .start(this)
//                        }
//                        .show()
//                true
//            }
//            R.id.action_style -> {
//                val popupWindow = PopupWindow(layoutInflater.inflate(R.layout.popup_text_size, null), 300, 300)
//                popupWindow.isOutsideTouchable = true
//                popupWindow.isFocusable = true
//                popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//                val popup_text_size = popupWindow.contentView.findViewById<TextView>(R.id.popup_text_size)
//                popup_text_size.text = editorTextSize.toString() + "sp"
//
//                val popup_add = popupWindow.contentView.findViewById<ImageButton>(R.id.popup_add)
//                val popup_minus = popupWindow.contentView.findViewById<ImageButton>(R.id.popup_minus)
//
//                popup_add.setOnClickListener {
//                    if (editorTextSize >= 40) return@setOnClickListener
//                    editorTextSize++
//                    popup_text_size.text = editorTextSize.toString() + "sp"
//
//                    saveTextSize()
//                }
//                popup_minus.setOnClickListener {
//                    if (editorTextSize <= 14) return@setOnClickListener
//                    editorTextSize--
//                    popup_text_size.text = editorTextSize.toString() + "sp"
//
//                    saveTextSize()
//                }
//
//                val popup_typeface = popupWindow.contentView.findViewById<Button>(R.id.popup_typeface)
//                val selectedTypeface = pref!!.getInt(pref_id_editor_typeface, 0)
//                popup_typeface.text = resources.getStringArray(R.array.editor_typeface)[selectedTypeface]
//                popup_typeface.setOnClickListener {
//                    MaterialDialog.Builder(this)
//                            .title(getString(R.string.typeface))
//                            .items(R.array.editor_typeface)
//                            .itemsCallbackSingleChoice(pref!!.getInt(pref_id_editor_typeface, 0), { _, _, position, text ->
//                                pref!!.edit().putInt(pref_id_editor_typeface, position).apply()
//                                popup_typeface.text = text
//
//                                updateEditorTypeface()
//                                true
//                            })
//                            .show()
//                }
//
//                val menu_item = findViewById<View>(R.id.action_style)
//                if (menu_item == null) {
//                    popupWindow.showAtLocation(toolbar, Gravity.END or Gravity.TOP, 0, 0)
//                } else {
//                    popupWindow.showAsDropDown(menu_item)
//                }
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
//
//    private fun runScript() {
//        if (editor.text.isNullOrEmpty()) {
//            Tool.toast(this, "Empty script.")
//            return
//        }
//        saveEditingScript()
//        if (editingFile == null)
//            editingFile = File(PROJECT_DIR + editingFileName + RIVE_SCRIPT_EXTENSION)
//
//        val intent = Intent(this, RiveScriptPlayground::class.java)
//        intent.putExtra(extra_scrip_data, editingFile!!.absoluteFile.toString())
//        startActivity(intent)
//    }
//
//    private fun updateEditorTypeface() {
//        editor.typeface = when (pref!!.getInt(pref_id_editor_typeface, 0)) {
//            0 -> Typeface.DEFAULT
//            1 -> Typeface.MONOSPACE
//            2 -> ResourcesCompat.getFont(this, R.font.noto_sans)
//            else -> Typeface.DEFAULT
//        }
//    }
//
//    private fun saveTextSize() {
//        editor.setTextSize(TypedValue.COMPLEX_UNIT_SP, editorTextSize.toFloat())
//        pref!!.edit().putInt(pref_id_editor_text_size, editorTextSize).apply()
//    }
//
//    private fun newScript() {
//        MaterialDialog.Builder(this)
//                .title(getString(R.string.new_script))
//                .input("", "", false, { _, input ->
//                    setBlankScript(Tool.stripExtension(input.toString()))
//
//                    getAllScripts()
//                    leftDrawer.deselect()
//                }).show()
//    }
//
//    private fun setBlankScript() {
//        setBlankScript(UNTITLED)
//    }
//
//    private fun setBlankScript(name: String) {
//        editingFile = null
//        editingFileName = name
//
//        toolbar.subtitle = editingFileName
//        editor.text = null
//        editor.callOnClick()
//    }
//
//    private fun openScript(file: File) {
//        saveEditingScript()
//
//        editingFile = file
//
//        editingFileName = file.nameWithoutExtension
//        toolbar.subtitle = editingFileName
//
//        val script: String = Tool.getProject(file)
//        editor.setText(script)
//        editor.callOnClick()
//
//        editor.highlightText()
//        editor.clearHistory()
//    }
//
//    private fun saveEditingScript() {
//        if (!editingFileName.isNullOrEmpty() && !permissionsDenied && !(editingFileName == UNTITLED && editor.text.isNullOrEmpty())) {
//            editingFile = Tool.saveScript(editingFileName!!, editor.text.toString())
//        }
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_main, menu)
//        menu_undo = menu.findItem(R.id.action_undo)
//        menu_redo = menu.findItem(R.id.action_redo)
//        menu_run = menu.findItem(R.id.action_run)
//        menu_text = menu.findItem(R.id.action_style)
//
//        menu_run?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
//        menu_text?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
//
//        menu_redo?.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
//        menu_undo?.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
//
//        return true
//    }
//
//    override fun onResume() {
//        super.onResume()
//
//        if (checkPermissions(false))
//            getAllScripts()
//    }
//
//    override fun onBackPressed() {
//        when {
//            rightDrawer.isDrawerOpen -> rightDrawer.closeDrawer()
//            leftDrawer.isDrawerOpen -> leftDrawer.closeDrawer()
//            else -> super.onBackPressed()
//        }
//    }
//
//    override fun onPause() {
//        if (editingFile != null)
//            pref!!.edit().putString(pref_id_last_opened_script, editingFile!!.absolutePath).apply()
//
//        saveEditingScript()
//
//        super.onPause()
//    }
//}
