package com.bennyv17.river.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.bennyv17.river.R
import com.bennyv17.river.item.SimpleMessageItem
import com.bennyv17.river.item.SimpleTextItem
import com.bennyv17.river.util.Tool
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.rivescript.RiveScript
import kotlinx.android.synthetic.main.activity_rive_script_playground.*
import java.io.File
import java.util.regex.Pattern

class RiveScriptPlayground : AppCompatActivity() {

    private lateinit var conversationAdapter: FastItemAdapter<SimpleMessageItem>
    private lateinit var suggestionAdapter: FastItemAdapter<SimpleTextItem>

    private var pref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pref = getSharedPreferences(MainActivity.pref_id, Context.MODE_PRIVATE)
        //TODO(and pref!!.getBoolean(MainActivity.pref_id_unlocked, true))
        if (pref!!.getBoolean(MainActivity.pref_id_dark_theme, false)) {
            setTheme(R.style.AppTheme_Dark)
        } else {
            setTheme(R.style.AppTheme)
        }

        setContentView(R.layout.activity_rive_script_playground)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (intent.extras == null) finish()
        val scriptPath = intent.extras.getString(MainActivity.extra_scrip_data, null)
        if (scriptPath.isNullOrEmpty()) finish()

        val scriptFile = File(scriptPath)

        toolbar.subtitle = Tool.stripExtension(scriptFile.name)

        val bot = RiveScript()
        bot.loadFile(scriptFile)
        bot.sortReplies()

        conversation_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        conversation_list.itemAnimator = DefaultItemAnimator()

        conversationAdapter = FastItemAdapter()
        conversation_list.adapter = conversationAdapter

        conversationAdapter.withSelectable(false)
        conversationAdapter.withOnClickListener({ _, _, _, _ ->
            true
        })

        suggestion_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        suggestion_list.itemAnimator = DefaultItemAnimator()

        suggestionAdapter = FastItemAdapter()
        suggestion_list.adapter = suggestionAdapter

        val script = Tool.getProject(scriptFile)
        val pattern = Pattern.compile("^(\\s*[+]\\s*).*$", Pattern.MULTILINE)
        val matcher = pattern.matcher(script)
        val suggestion_items = arrayListOf<SimpleTextItem>()
        while (matcher.find()) {
            suggestion_items.add(SimpleTextItem(matcher.group().trim().removePrefix("+").trim()))
        }

        if (suggestion_items.size == 0)
            divider.visibility = View.GONE
        else
            suggestionAdapter.add(suggestion_items)

        suggestionAdapter.withSelectable(false)
        suggestionAdapter.withOnClickListener({ _, _, item, _ ->
            postInput(item.text!!, bot)
            true
        })
        suggestionAdapter.withOnLongClickListener({ _, _, item, _ ->
            user_input.setText(item.text!!)
            user_input.setSelection(user_input.text.length)
            true
        })

        input_fab.setOnClickListener({
            val input = user_input.text.toString()
            if (input.isEmpty()) {
                return@setOnClickListener
            }

            postInput(input, bot)
        })
    }

    private fun postInput(input: String, bot: RiveScript) {
        conversationAdapter.add(SimpleMessageItem("You", input, true))
        conversation_list.scrollToPosition(conversationAdapter.itemCount - 1)

        val reply = bot.reply("user", input)

        conversationAdapter.add(SimpleMessageItem(toolbar.subtitle.toString(), reply, false))
        conversation_list.scrollToPosition(conversationAdapter.itemCount - 1)

        user_input.text.clear()
    }
}
