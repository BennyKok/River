package com.bennyv17.river.item

import android.content.res.ColorStateList
import android.graphics.Color
import android.support.v7.widget.CardView
import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.bennyv17.river.R
import com.bennyv17.river.util.Tool
import com.mikepenz.fastadapter.items.AbstractItem

class SimpleMessageItem(private var sender: String? = null, private var message: String? = null, private var isSelf: Boolean = false)
    : AbstractItem<SimpleMessageItem, SimpleMessageItem.ViewHolder>() {

    override fun getType(): Int {
        return R.id.simple_message_item
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_simple_message
    }

    override fun bindView(viewHolder: ViewHolder, payloads: List<Any>?) {
        super.bindView(viewHolder, payloads)

        viewHolder.message.text = message
        viewHolder.message.gravity = if (isSelf) Gravity.END else Gravity.START
        viewHolder.message.setTextColor(Color.WHITE)
        //Important
        viewHolder.message.requestLayout()
        viewHolder.card.cardBackgroundColor = ColorStateList.valueOf(if (isSelf) viewHolder.card.context.resources.getColor(R.color.colorAccent) else viewHolder.card.context.resources.getColor(R.color.colorPrimary))
        (viewHolder.card.layoutParams as FrameLayout.LayoutParams).gravity = if (isSelf) Gravity.END else Gravity.START
    }

    override fun unbindView(holder: ViewHolder?) {
        super.unbindView(holder)

        holder?.message?.text = null
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val message: TextView = view.findViewById(R.id.message_content)
        val card: CardView = view.findViewById(R.id.message_card)

        init {
            //setIsRecyclable(false)
        }
    }
}