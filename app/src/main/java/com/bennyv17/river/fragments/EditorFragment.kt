package com.bennyv17.river.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bennyv17.river.R
import com.bennyv17.river.activity.Main3Activity

class EditorFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_editor, container, false)

        (context as Main3Activity).attachEditor(rootView)

        return rootView
    }
    companion object {

        fun newInstance(): EditorFragment {
            val frag = EditorFragment()
            return frag
        }

    }

}