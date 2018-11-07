package com.bennyv17.river.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
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

class RiveScriptPlayground : AppCompatActivity() {

    private lateinit var conversationAdapter: FastItemAdapter<SimpleMessageItem>
    private lateinit var suggestionAdapter: FastItemAdapter<SimpleMessageItem>

    private var pref: SharedPreferences? = null

    lateinit var scriptFile: File

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

        val bot = RiveScript(Config.Builder
                .utf8()
                .unicodePunctuation("[.,!?;:]")
                .build())
        bot.loadFile(scriptFile)
        bot.sortReplies()

        conversation_list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        conversation_list.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()

        conversationAdapter = FastItemAdapter()
        conversation_list.adapter = conversationAdapter

        conversationAdapter.withSelectable(false)
        conversationAdapter.withOnClickListener { _, _, _, _ ->
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
                postInput(suggestion, bot)
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

            postInput(input, bot)
        }

        KeyboardVisibilityEvent.setEventListener(this) { isOpen ->
            if (isOpen) {
                info.visibility = View.GONE
            } else {
                info.visibility = View.VISIBLE
            }
        }
    }

    private fun postInput(input: String, bot: RiveScript) {
        conversationAdapter.add(SimpleMessageItem("You", input, true))
        conversation_list.scrollToPosition(conversationAdapter.itemCount - 1)

        val reply = bot.reply("user", input)

        conversationAdapter.add(SimpleMessageItem(scriptFile.nameWithoutExtension, reply, false))
        conversation_list.scrollToPosition(conversationAdapter.itemCount - 1)

        user_input.text!!.clear()
    }
}
