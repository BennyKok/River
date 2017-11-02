package com.bennyv17.river.item

import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.bennyv17.river.R
import com.bennyv17.river.highlighter.view.LineNumberEditText
import com.mikepenz.fastadapter.commons.items.AbstractExpandableItem
import com.mikepenz.materialize.holder.StringHolder

class SimpleSubTutorialItem(val title: StringHolder, val content: StringHolder, val code: StringHolder, val callback: TutorialItemActionCallback?)
    : AbstractExpandableItem<SimpleTutorialItem, SimpleSubTutorialItem.ViewHolder, SimpleSubTutorialItem>()
        , View.OnClickListener {

    constructor(@StringRes subTitle: Int, @StringRes subContent: Int)
            : this(StringHolder(subTitle), StringHolder(subContent), StringHolder(""), null)

    constructor(subTitle: String, subContent: String)
            : this(StringHolder(subTitle), StringHolder(subContent), StringHolder(""), null)

    constructor(subTitle: String, subContent: String, codeExample: String, callback: TutorialItemActionCallback)
            : this(StringHolder(subTitle), StringHolder(subContent), StringHolder(codeExample), callback)

    constructor(@StringRes subTitle: Int, @StringRes subContent: Int, @StringRes codeExample: Int, callback: TutorialItemActionCallback)
            : this(StringHolder(subTitle), StringHolder(subContent), StringHolder(codeExample), callback)

    constructor(@StringRes subTitle: Int, @StringRes subContent: Int, codeExample: String, callback: TutorialItemActionCallback)
            : this(StringHolder(subTitle), StringHolder(subContent), StringHolder(codeExample), callback)

    override fun getLayoutRes(): Int {
        return R.layout.item_sub_tutorial
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun getType(): Int {
        return R.id.simple_tutorial_sub_item
    }

    override fun bindView(holder: ViewHolder?, payloads: MutableList<Any>?) {
        super.bindView(holder, payloads)

        StringHolder.applyTo(title, holder?.titleTextView)
        StringHolder.applyTo(content, holder?.contentTextView)
        StringHolder.applyTo(code, holder?.codeView)

        if (!code.getText(holder?.codeView?.context).isNullOrEmpty()) {
            holder?.tryButton?.setOnClickListener(this)
            holder?.tryButton?.visibility = View.VISIBLE
            holder?.codeView?.visibility = View.VISIBLE
        } else {
            holder?.tryButton?.setOnClickListener(null)
            holder?.tryButton?.visibility = View.GONE
            holder?.codeView?.visibility = View.GONE
        }
    }

    override fun onClick(v: View) {
        val m_code = code.getText(v.context)
        if (!m_code.isNullOrEmpty()) {
            callback?.onTryCode(m_code)
        }
    }

    override fun unbindView(holder: ViewHolder?) {
        super.unbindView(holder)

        holder?.titleTextView?.text = null
        holder?.contentTextView?.text = null
        holder?.codeView?.text = null
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.tutorial_sub_title)
        val contentTextView: TextView = view.findViewById(R.id.tutorial_sub_content)
        val codeView: LineNumberEditText = view.findViewById(R.id.tutorial_code_view)
        val tryButton: Button = view.findViewById(R.id.tutorial_try_button)

        init {
            //codeView.syntax = RiveScriptSyntax
            //codeView.syntaxTheme = RiveScriptDefaultTheme
        }
    }
}