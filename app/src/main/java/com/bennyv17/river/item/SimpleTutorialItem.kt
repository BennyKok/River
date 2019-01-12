package com.bennyv17.river.item

import android.animation.ValueAnimator
import android.util.Log
import androidx.annotation.StringRes
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bennyv17.river.R
import com.bennyv17.river.util.Tool
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.expandable.items.AbstractExpandableItem
import com.mikepenz.fastadapter.listeners.OnClickListener
import com.mikepenz.materialize.holder.StringHolder

class SimpleTutorialItem(val title: StringHolder, val showBackground: Boolean, val background: Int, var locked: Boolean = true) : AbstractExpandableItem<SimpleTutorialItem, SimpleTutorialItem.ViewHolder, SimpleSubTutorialItem>() {

    constructor(sectionTitle: String, locked: Boolean) : this(StringHolder(sectionTitle),false,-1, locked)

    constructor(sectionTitle: String, background: Int, locked: Boolean) : this(StringHolder(sectionTitle),true,background, locked)

    constructor(@StringRes sectionTitle: Int, background: Int, locked: Boolean) : this(StringHolder(sectionTitle), true, background, locked)

    constructor(@StringRes sectionTitle: Int, locked: Boolean) : this(StringHolder(sectionTitle), false, -1, locked)

    private var mOnClickListener: OnClickListener<SimpleTutorialItem>? = null

    fun withLocked(locked: Boolean) {
        this.locked = locked
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_tutorial_section_title
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun getType(): Int {
        return R.id.simple_text_item
    }

    override fun bindView(holder: ViewHolder?, payloads: MutableList<Any>?) {
        super.bindView(holder, payloads)

        StringHolder.applyTo(title, holder?.titleTextView)
        updateTitleSize(this, holder!!.itemView, false)

        holder!!.lockedIcon.visibility = if (!locked) View.GONE else View.VISIBLE
        holder.titleTextView.alpha = if (locked) 0.5f else 1f

        if (showBackground) {
            holder.background.visibility = View.VISIBLE
            holder.background.setImageResource(background)
        }else {
            holder.background.visibility = View.GONE
            holder.background.setImageDrawable(null)
        }
    }

    override fun unbindView(holder: ViewHolder?) {
        super.unbindView(holder)

        holder?.titleTextView?.text = null
        holder?.arrowIcon?.clearAnimation()
        holder?.background?.setImageDrawable(null)
    }

    fun getOnClickListener(): OnClickListener<SimpleTutorialItem> {
        return mOnClickListener!!
    }

    fun withOnClickListener(mOnClickListener: OnClickListener<SimpleTutorialItem>): SimpleTutorialItem {
        this.mOnClickListener = mOnClickListener
        return this
    }

    override fun isAutoExpanding(): Boolean {
        if (locked)
            return false
        return super.isAutoExpanding()
    }

    private val onClickListener = OnClickListener<SimpleTutorialItem> { v, adapter, item, position ->
        if (item.subItems != null && !locked) {
            //Log.d("Hey","Shit")
            updateTitleSize(item, v!!, true)
            return@OnClickListener mOnClickListener == null || mOnClickListener!!.onClick(v, adapter, item, position)
        }
        mOnClickListener != null && mOnClickListener!!.onClick(v, adapter, item, position)
    }

    private fun updateTitleSize(item: SimpleTutorialItem, v: View, animate: Boolean) {
        if (!item.isExpanded) {
            v.findViewById<View>(R.id.tutorial_section_arrow).animate().rotation(-90f)

            val startSize = Tool.px2dp(v.findViewById<TextView>(R.id.tutorial_section_title).textSize)
            val endSize = 24f

            if (animate)
                animateTextSize(v, startSize, endSize)
            else
                v.findViewById<TextView>(R.id.tutorial_section_title).textSize = endSize
        } else {
            v.findViewById<View>(R.id.tutorial_section_arrow).animate().rotation(0f)

            val startSize = Tool.px2dp(v.findViewById<TextView>(R.id.tutorial_section_title).textSize)
            val endSize = 30f

            if (animate)
                animateTextSize(v, startSize, endSize)
            else
                v.findViewById<TextView>(R.id.tutorial_section_title).textSize = endSize
        }
    }

    private fun animateTextSize(textView: View, startSize: Float, endSize: Float) {
        val animationDuration = 200

        val animator = ValueAnimator.ofFloat(startSize, endSize)
        animator.duration = animationDuration.toLong()

        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            textView.findViewById<TextView>(R.id.tutorial_section_title).textSize = animatedValue
        }

        animator.start()
    }

    override fun getOnItemClickListener(): OnClickListener<SimpleTutorialItem> {
        return onClickListener
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.tutorial_section_title)
        val arrowIcon: ImageView = view.findViewById(R.id.tutorial_section_arrow)
        val lockedIcon: ImageView = view.findViewById(R.id.tutorial_section_locked_icon)
        val background: ImageView = view.findViewById(R.id.tutorial_section_background)
    }
}