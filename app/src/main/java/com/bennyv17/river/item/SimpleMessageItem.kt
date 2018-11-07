package com.bennyv17.river.item

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.cardview.widget.CardView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bennyv17.river.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.mikepenz.fastadapter.items.AbstractItem

class SimpleMessageItem(private var sender: String? = null, var message: String? = null, private var isSelf: Boolean = false, private var autoGravity: Boolean = true)
    : AbstractItem<SimpleMessageItem, SimpleMessageItem.ViewHolder>() {

    var listener: View.OnClickListener? = null
    var longListener: View.OnLongClickListener? = null

    override fun getType(): Int {
        return R.id.simple_message_item
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_simple_message
    }

    override fun bindView(viewHolder: ViewHolder, payloads: List<Any>?) {
        super.bindView(viewHolder, payloads)

        viewHolder.message.text = message
        viewHolder.message.gravity = if (isSelf) Gravity.END or Gravity.CENTER_VERTICAL else Gravity.START or Gravity.CENTER_VERTICAL
//        viewHolder.message.setTextColor(Color.DKGRAY)
        //Important
        viewHolder.message.requestLayout()

        viewHolder.message.strokeColor = ColorStateList.valueOf(if (isSelf) Color.parseColor("#FFDDDDDD") else viewHolder.message.context.resources.getColor(R.color.colorPrimary))
        if (autoGravity)
            (viewHolder.message.layoutParams as FrameLayout.LayoutParams).gravity = if (isSelf) Gravity.END else Gravity.START
        else {
            (viewHolder.message.layoutParams as FrameLayout.LayoutParams).gravity = Gravity.START
        }

        if (listener != null)
            viewHolder.message.setOnClickListener(listener)
        if (longListener != null)
            viewHolder.message.setOnLongClickListener(longListener)
    }

    override fun unbindView(holder: ViewHolder?) {
        super.unbindView(holder)

        holder?.message?.text = null
        holder?.message?.setOnClickListener(null)
        holder?.message?.setOnLongClickListener(null)
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    fun withClickListener(listener: () -> Unit): SimpleMessageItem {
        this.listener = View.OnClickListener {
            listener.invoke()
        }
        return this
    }


    fun withLongClickListener(listener: () -> Unit): SimpleMessageItem {
        this.longListener = View.OnLongClickListener {
            listener.invoke()

            true
        }
        return this
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val message = view.findViewById<MaterialButton>(R.id.message_content)!!

        init {
            //setIsRecyclable(false)
        }
    }
}