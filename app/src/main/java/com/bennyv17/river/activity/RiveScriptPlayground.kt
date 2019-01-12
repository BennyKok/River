package com.bennyv17.river.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.bennyv17.river.R
import com.bennyv17.river.item.SimpleMessageItem
import com.bennyv17.river.util.Tool
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.rivescript.Config
import com.rivescript.RiveScript
import kotlinx.android.synthetic.main.activity_rive_script_playground.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import java.io.File
import java.util.regex.Pattern
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Debug
import android.util.Log
import com.evgenii.jsevaluator.JsEvaluator
import java.io.IOException
import java.util.*
import com.evgenii.jsevaluator.interfaces.JsCallback


class RiveScriptPlayground : AppCompatActivity() {

    private lateinit var conversationAdapter: FastItemAdapter<SimpleMessageItem>
    private lateinit var suggestionAdapter: FastItemAdapter<SimpleMessageItem>

    val jsEvaluator = JsEvaluator(this)

    private var pref: SharedPreferences? = null

    lateinit var scriptFile: File

    var code = ""

    private lateinit var bot: RiveScript

    private var selectedInterpreter = 0


    private fun loadJs(fileName: String): String? {
        try {
            return readFile(fileName)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    @Throws(IOException::class)
    private fun readFile(fileName: String): String {
        val am = assets
        val inputStream = am.open(fileName)

        val scanner = Scanner(inputStream, "UTF-8")
        return scanner.useDelimiter("\\A").next()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        pref = getSharedPreferences(Main2Activity.pref_id, Context.MODE_PRIVATE)
        if (pref!!.getBoolean(Main2Activity.pref_id_dark_theme, false)) {
            setTheme(R.style.AppTheme_Dark)
        } else {
            setTheme(R.style.AppTheme)
        }

        setContentView(R.layout.activity_rive_script_playground)

        if (intent.extras == null) finish()
        val scriptPath = intent.extras.getString(Main2Activity.extra_scrip_data, null)
        if (scriptPath.isNullOrEmpty()) finish()

        scriptFile = File(scriptPath)

        info.text = "Type in anything to chat with " + scriptFile.nameWithoutExtension

        selectedInterpreter = pref!!.getInt(Main2Activity.pref_id_editor_interpreter, 0)

        label_interpreter.text = "Interpreter : " + resources.getStringArray(R.array.editor_interpreter)[selectedInterpreter]

        if (selectedInterpreter == 0) {
            bot = RiveScript(Config.Builder
                    .utf8()
                    .unicodePunctuation("[.,!?;:]")
                    .build())
            bot.loadFile(scriptFile)
            bot.sortReplies()
        }

        conversation_list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        conversation_list.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()

        conversationAdapter = FastItemAdapter()
        conversation_list.adapter = conversationAdapter

        conversationAdapter.withSelectable(false)
        conversationAdapter.withOnClickListener { _, _, item, _ ->

            true
        }

        suggestion_list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        suggestion_list.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()

        suggestionAdapter = FastItemAdapter()
        suggestion_list.adapter = suggestionAdapter

        val script = Tool.getProject(scriptFile)
        val pattern = Pattern.compile("^(\\s*[+]\\s*).*$", Pattern.MULTILINE)
        val matcher = pattern.matcher(script)
        val suggestion_items = arrayListOf<SimpleMessageItem>()
        while (matcher.find()) {
            var suggestion = matcher.group().trim().removePrefix("+").trim().replace("*", "")
            if (suggestion.contains("//")) {
                val s = suggestion.indexOf("//")
                suggestion = suggestion.removeRange(s, suggestion.length).trim()
            }

            suggestion_items.add(SimpleMessageItem(
                    message = suggestion,
                    isSelf = true,
                    autoGravity = false
            ).withClickListener {
                postInput(script, suggestion)
            }.withLongClickListener {
                user_input.setText(suggestion)
                user_input.setSelection(user_input.text!!.length)
            })
        }

        if (suggestion_items.size == 0)
        //divider.visibility = View.GONE
        else
            suggestionAdapter.add(suggestion_items)

        suggestionAdapter.withSelectable(false)

        input_fab.setOnClickListener {
            val input = user_input.text.toString()
            if (input.isEmpty()) {
                return@setOnClickListener
            }

            postInput(script, input)
        }

        KeyboardVisibilityEvent.setEventListener(this) { isOpen ->
            if (isOpen) {
                info.visibility = View.GONE
            } else {
                info.visibility = View.VISIBLE
            }
        }
    }

    fun getReply(code: String, user: String, input: String) {
        var jsCode = loadJs("rivescript.min.js")
        jsCode += loadJs("rsrunner.js")
        jsEvaluator.callFunction(jsCode,
                object : JsCallback {

                    override fun onResult(result: String) {
                        conversationAdapter.add(
                                SimpleMessageItem(scriptFile.nameWithoutExtension, result, false).withCopyOption()
                        )
                        conversation_list.scrollToPosition(conversationAdapter.itemCount - 1)
                    }

                    override fun onError(errorMessage: String) {
                        Log.d("Shit", errorMessage)
                    }
                }, "getRsReply", code, user, input)
    }

    private fun postInput(code: String, input: String) {
        conversationAdapter.add(
                SimpleMessageItem("You", input, true).withCopyOption()
        )
        conversation_list.scrollToPosition(conversationAdapter.itemCount - 1)

        when (selectedInterpreter){
            0->{
                conversationAdapter.add(
                        SimpleMessageItem(scriptFile.nameWithoutExtension, bot.reply("user", input), false).withCopyOption()
                )
                conversation_list.scrollToPosition(conversationAdapter.itemCount - 1)
            }
            1 -> getReply(code, "user", input)
        }

        user_input.text!!.clear()
    }

    private fun SimpleMessageItem.withCopyOption(): SimpleMessageItem {
        withClickListener {
            MaterialDialog(this@RiveScriptPlayground).show {
                listItems(items = arrayListOf("Copy")) { dialog, index, text ->
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("River", this@withCopyOption.message)
                    clipboard.primaryClip = clip
                }
            }
        }
        return this
    }
}
