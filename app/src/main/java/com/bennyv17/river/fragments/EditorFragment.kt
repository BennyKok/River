package com.bennyv17.river.fragments

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.bennyv17.river.R
import com.bennyv17.river.activity.Main3Activity
import com.bennyv17.river.highlighter.syntax.RiveScriptSyntax
import com.bennyv17.river.highlighter.theme.RiveScriptDefaultTheme
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_editor.view.*

class EditorFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_editor, container, false)

//        rootView.fab_play.setOnClickListener {
//
//        }

        rootView.editor.setHorizontallyScrolling(true)
        rootView.editor.syntax = RiveScriptSyntax
        rootView.editor.syntaxTheme = RiveScriptDefaultTheme

        rootView.editor.setTextSize(TypedValue.COMPLEX_UNIT_SP, (activity as Main3Activity).editorTextSize.toFloat())

        return rootView
    }

    fun setCode(code: String) {

    }

//    override fun onStart() {
//        super.onStart()
//
//        if (dialog != null) {
//            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet);
//            bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
//        }
//        view!!.post {
//
//            val params = (view!!.parent as View).layoutParams as CoordinatorLayout.LayoutParams
//            val behavior = params.behavior
//            val bottomSheetBehavior = behavior as BottomSheetBehavior
//            bottomSheetBehavior.peekHeight = view!!.measuredHeight
//
////            (view!!.parent as View).setBackgroundColor(Color.TRANSPARENT)
//
//        }
//    }

    companion object {

        fun newInstance(): EditorFragment {
            return EditorFragment()
        }

    }
}