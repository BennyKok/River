package com.bennyv17.river.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bennyv17.river.R
import com.bennyv17.river.activity.Main3Activity
import com.bennyv17.river.callback.RiverInterface
import com.bennyv17.river.item.SimpleTutorialItem
import com.bennyv17.river.item.TutorialItemActionCallback
import com.bennyv17.river.tutorial.RiveScriptTutorial
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.fastadapter.expandable.ExpandableExtension
import com.mikepenz.fastadapter.listeners.OnClickListener
import kotlinx.android.synthetic.main.frament_learn.view.*

class LearnFragment : Fragment(), TutorialItemActionCallback {

    private var tutorialAdapter: FastItemAdapter<SimpleTutorialItem> = FastItemAdapter()
    private val expandableExtension = ExpandableExtension<SimpleTutorialItem>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.frament_learn, container, false)

        rootView.tutorial_recycler.adapter = tutorialAdapter

        initTutorial()

        return rootView
    }

    fun unlockTutorialState() {
        tutorialAdapter.adapterItems.forEach {
            it.withLocked(false)
        }
        tutorialAdapter.notifyAdapterDataSetChanged()

    }

    override fun onTryCode(code: String) {
        (activity as Main3Activity).tryCode(code)
    }

    private fun initTutorial() {
        val items = RiveScriptTutorial.getTutorialItems(this)
        items.forEach {
            if (it.locked)
                it.withLocked(!(activity as RiverInterface).isUnlocked())

            it.withOnClickListener(OnClickListener { v, adapter, item, position ->
                if (item.locked)
                    (activity as RiverInterface).unlockExtras()
                false
            })
        }
        tutorialAdapter.addExtension(expandableExtension)
        expandableExtension.withOnlyOneExpandedItem(true)
        tutorialAdapter.add(items)
    }

    companion object {

        fun newInstance(): LearnFragment {
            return LearnFragment()
        }
    }
}