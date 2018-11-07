package com.bennyv17.river.item

import android.widget.TextView
import android.view.View
import com.bennyv17.river.R
import com.mikepenz.fastadapter.items.AbstractItem

class SimpleTextItem(var text: String? = null) : AbstractItem<SimpleTextItem, SimpleTextItem.ViewHolder>() {

    override fun getType(): Int {
        return R.id.simple_text_item
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_simple_text
    }

    override fun bindView(viewHolder: ViewHolder, payloads: List<Any>?) {
        super.bindView(viewHolder, payloads)

        viewHolder.textView.text = text
    }

    override fun unbindView(holder: ViewHolder?) {
        super.unbindView(holder)
        holder!!.textView.text = null
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        var textView: TextView = view.findViewById(R.id.simple_text)
    }
}