package com.bennyv17.river.item

import android.animation.ValueAnimator
import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bennyv17.river.R
import com.bennyv17.river.util.Tool
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.commons.items.AbstractExpandableItem
import com.mikepenz.materialize.holder.StringHolder

class SimpleTutorialItem(val title: StringHolder, var locked: Boolean = true) : AbstractExpandableItem<SimpleTutorialItem, SimpleTutorialItem.ViewHolder, SimpleSubTutorialItem>() {

    constructor(sectionTitle: String, locked: Boolean) : this(StringHolder(sectionTitle), locked)

    constructor(sectionTitle: String) : this(StringHolder(sectionTitle))

    constructor(@StringRes sectionTitle: Int, locked: Boolean) : this(StringHolder(sectionTitle), locked)

    constructor(@StringRes sectionTitle: Int) : this(StringHolder(sectionTitle))

    private var mOnClickListener: FastAdapter.OnClickListener<SimpleTutorialItem>? = null

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
        updateTitleSize(this, holder!!.itemView)

        holder.lockedIcon.visibility = if (!locked) View.GONE else View.VISIBLE
        holder.titleTextView.alpha = if (locked) 0.5f else 1f
    }

    override fun unbindView(holder: ViewHolder?) {
        super.unbindView(holder)

        holder?.titleTextView?.text = null
        holder?.arrowIcon?.clearAnimation()
    }

    fun getOnClickListener(): FastAdapter.OnClickListener<SimpleTutorialItem> {
        return mOnClickListener!!
    }

    fun withOnClickListener(mOnClickListener: FastAdapter.OnClickListener<SimpleTutorialItem>): SimpleTutorialItem {
        this.mOnClickListener = mOnClickListener
        return this
    }

    override fun isAutoExpanding(): Boolean {
        if (locked)
            return false
        return super.isAutoExpanding()
    }

    private val onClickListener = FastAdapter.OnClickListener<SimpleTutorialItem> { v, adapter, item, position ->
        if (item.subItems != null && !locked) {
            updateTitleSize(item, v)
            return@OnClickListener mOnClickListener == null || mOnClickListener!!.onClick(v, adapter, item, position)
        }
        mOnClickListener != null && mOnClickListener!!.onClick(v, adapter, item, position)
    }

    private fun updateTitleSize(item: SimpleTutorialItem, v: View) {
        if (!item.isExpanded) {
            v.findViewById<View>(R.id.tutorial_section_arrow).animate().rotation(-90f)

            val startSize = Tool.px2dp(v.findViewById<TextView>(R.id.tutorial_section_title).textSize)
            val endSize = 24f

            animateTextSize(v, startSize, endSize)
        } else {
            v.findViewById<View>(R.id.tutorial_section_arrow).animate().rotation(0f)

            val startSize = Tool.px2dp(v.findViewById<TextView>(R.id.tutorial_section_title).textSize)
            val endSize = 30f

            animateTextSize(v, startSize, endSize)
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

    override fun getOnItemClickListener(): FastAdapter.OnClickListener<SimpleTutorialItem> {
        return onClickListener
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.tutorial_section_title)
        val arrowIcon: ImageView = view.findViewById(R.id.tutorial_section_arrow)
        val lockedIcon: ImageView = view.findViewById(R.id.tutorial_section_locked_icon)
    }
}