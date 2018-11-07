package com.bennyv17.river.item

import android.view.View
import android.widget.TextView
import com.bennyv17.river.R
import com.mikepenz.fastadapter.items.AbstractItem
import java.io.File

class ProjectItem(var title: String)
    : AbstractItem<ProjectItem, ProjectItem.ViewHolder>() {

    var listener: View.OnClickListener? = null
    var longListener: View.OnLongClickListener? = null

    override fun getType(): Int {
        return R.id.project_item
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_project
    }

    override fun bindView(holder: ViewHolder, payloads: List<Any>?) {
        super.bindView(holder, payloads)

        holder.title.text = title
        holder.itemView.setOnClickListener(listener)
        holder.itemView.setOnLongClickListener(longListener)
    }

    override fun unbindView(holder: ViewHolder?) {
        super.unbindView(holder)

        holder?.title?.text = null
        holder?.itemView?.setOnClickListener(null)
        holder?.itemView?.setOnLongClickListener(null)
    }

    fun withClickListener(listener: () -> Unit): ProjectItem {
        this.listener = View.OnClickListener {
            listener.invoke()
        }
        return this
    }

    fun withLongClickListener(listener: () -> Unit): ProjectItem {
        this.longListener = View.OnLongClickListener {
            listener.invoke()

            true
        }
        return this
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.project_title)
        //val card: MaterialCardView = view.findViewById(R.id.message_card)
    }
}