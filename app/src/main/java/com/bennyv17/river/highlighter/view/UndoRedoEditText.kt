package com.bennyv17.river.highlighter.view

import android.content.Context
import android.content.SharedPreferences
import android.text.Editable
import android.text.Selection
import android.text.TextUtils
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import android.widget.TextView
import java.util.*

open class UndoRedoEditText(context: Context, attrs: AttributeSet) : LineNumberEditText(context, attrs) {

    private var mUndoRedoHelper: UndoRedoHelper

    init {
        mUndoRedoHelper = UndoRedoHelper(this)
    }

    open fun undo() {
        if (mUndoRedoHelper.canUndo) {
            try {
                mUndoRedoHelper.undo()
            } catch (e: Exception) {
            }
        }
    }

    open fun redo() {
        if (mUndoRedoHelper.canRedo) {
            try {
                mUndoRedoHelper.redo()
            } catch (e: Exception) {
            }
        }
    }

    open fun clearHistory() {
        mUndoRedoHelper.clearHistory()
    }

    /*
    *  Copyright (c) 2017 Tran Le Duy
    *
    * Licensed under the Apache License, Version 2.0 (the "License");
    * you may not use this file except in compliance with the License.
    * You may obtain a copy of the License at
    *
    *     http://www.apache.org/licenses/LICENSE-2.0
    *
    * Unless required by applicable law or agreed to in writing, software
    * distributed under the License is distributed on an "AS IS" BASIS,
    * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    * See the License for the specific language governing permissions and
    * limitations under the License.
    */

    /**
     * Translated to kotlin from java
     * original source from https://github.com/tranleduy2000/javaide/blob/master/app/src/main/java/com/duy/ide/editor/code/view/UndoRedoSupportEditText.java
     * @see UndoRedoHelper
     */

    class UndoRedoHelper(private val mTextView: TextView?) {
        private var mIsUndoOrRedo = false

        private val mEditHistory: EditHistory

        private val mChangeListener: EditTextChangeListener

        val canUndo: Boolean
            get() = (mEditHistory.mmPosition > 0)

        val canRedo: Boolean
            get() = (mEditHistory.mmPosition < mEditHistory.mmHistory.size)

        init {
            mEditHistory = EditHistory()
            mChangeListener = EditTextChangeListener()
            mTextView!!.addTextChangedListener(mChangeListener)
        }

        fun disconnect() {
            mTextView?.removeTextChangedListener(mChangeListener)
        }

        fun setMaxHistorySize(maxHistorySize: Int) {
            mEditHistory.setMaxHistorySize(maxHistorySize)
        }

        fun clearHistory() {
            mEditHistory.clear()
        }

        fun undo() {
            val edit = mEditHistory.previous ?: return

            val editable = mTextView!!.editableText
            val start = edit.start
            val end = start + if (edit!!.after != null) edit.after.length else 0

            mIsUndoOrRedo = true
            editable.replace(start, end, edit.before)
            mIsUndoOrRedo = false

            for (o in editable.getSpans(0, editable.length, UnderlineSpan::class.java)) {
                editable.removeSpan(o)
            }

            Selection.setSelection(editable, if (edit.before == null) start else start + edit.before.length)
        }

        fun redo() {
            val edit = mEditHistory.next ?: return

            val text = mTextView!!.editableText
            val start = edit.start
            val end = start + if (edit!!.before != null) edit.before.length else 0

            mIsUndoOrRedo = true
            text.replace(start, end, edit.after)
            mIsUndoOrRedo = false

            // This will get rid of underlines inserted when editor tries to come
            // up with a suggestion.
            for (o in text.getSpans(0, text.length, UnderlineSpan::class.java)) {
                text.removeSpan(o)
            }

            Selection.setSelection(text, if (edit.after == null)
                start
            else
                start + edit.after.length)
        }

        fun storePersistentState(editor: SharedPreferences.Editor, prefix: String) {
            // Store hash code of text in the editor so that we can check if the
            // editor contents has changed.
            editor.putString(prefix + ".hash", prefix.hashCode().toString())
            editor.putInt(prefix + ".maxSize", mEditHistory.mmMaxHistorySize)
            editor.putInt(prefix + ".position", mEditHistory.mmPosition)
            editor.putInt(prefix + ".size", mEditHistory.mmHistory.size)

            var i = 0
            for (ei in mEditHistory.mmHistory) {
                val pre = prefix + "." + i

                editor.putInt(pre + ".start", ei.start)
                editor.putString(pre + ".before", ei.before.toString())
                editor.putString(pre + ".after", ei.after.toString())

                i++
            }
        }

        @Throws(IllegalStateException::class)
        fun restorePersistentState(sp: SharedPreferences, prefix: String): Boolean {
            val ok = doRestorePersistentState(sp, prefix)
            if (!ok) {
                mEditHistory.clear()
            }
            return ok
        }

        private fun doRestorePersistentState(sp: SharedPreferences, prefix: String): Boolean {
            val hash = sp.getString(prefix + ".hash", null) ?: // No state to be restored.
                    return true

            if (Integer.valueOf(hash) != prefix.hashCode()) {
                return false
            }

            mEditHistory.clear()
            mEditHistory.mmMaxHistorySize = sp.getInt(prefix + ".maxSize", -1)

            val count = sp.getInt(prefix + ".size", -1)
            if (count == -1) {
                return false
            }

            for (i in 0 until count) {
                val pre = prefix + "." + i

                val start = sp.getInt(pre + ".start", -1)
                val before = sp.getString(pre + ".before", null)
                val after = sp.getString(pre + ".after", null)

                if (start == -1 || before == null || after == null) {
                    return false
                }
                mEditHistory.add(EditItem(start, before, after))
            }

            mEditHistory.mmPosition = sp.getInt(prefix + ".position", -1)
            return mEditHistory.mmPosition != -1

        }

        // =================================================================== //

        internal enum class ActionType {
            INSERT, DELETE, PASTE, NOT_DEF
        }

        private inner class EditHistory {
            val mmHistory = LinkedList<EditItem>()
            var mmPosition = 0
            var mmMaxHistorySize = -1

            val current: EditItem?
                get() = if (mmPosition == 0) {
                    null
                } else mmHistory.get(mmPosition - 1)

            val previous: EditItem?
                get() {
                    if (mmPosition == 0) {
                        return null
                    }
                    mmPosition--
                    return mmHistory.get(mmPosition)
                }

            val next: EditItem?
                get() {
                    if (mmPosition >= mmHistory.size) {
                        return null
                    }

                    val item = mmHistory.get(mmPosition)
                    mmPosition++
                    return item
                }

            fun clear() {
                mmPosition = 0
                mmHistory.clear()
            }

            fun add(item: EditItem) {
                while (mmHistory.size > mmPosition) {
                    mmHistory.removeLast()
                }
                mmHistory.add(item)
                mmPosition++

                if (mmMaxHistorySize >= 0) {
                    trimHistory()
                }
            }

            fun setMaxHistorySize(maxHistorySize: Int) {
                mmMaxHistorySize = maxHistorySize
                if (mmMaxHistorySize >= 0) {
                    trimHistory()
                }
            }

            fun trimHistory() {
                while (mmHistory.size > mmMaxHistorySize) {
                    mmHistory.removeFirst()
                    mmPosition--
                }

                if (mmPosition < 0) {
                    mmPosition = 0
                }
            }
        }

        private data class EditItem(var start: Int, var before: CharSequence, var after: CharSequence)

        private inner class EditTextChangeListener : TextWatcher {
            private var mBeforeChange: CharSequence? = null
            private var mAfterChange: CharSequence? = null
            private var lastActionType = ActionType.NOT_DEF
            private var lastActionTime: Long = 0

            private val actionType: ActionType
                get() {
                    return if (!TextUtils.isEmpty(mBeforeChange) && TextUtils.isEmpty(mAfterChange)) {
                        ActionType.DELETE
                    } else if (TextUtils.isEmpty(mBeforeChange) && !TextUtils.isEmpty(mAfterChange)) {
                        ActionType.INSERT
                    } else {
                        ActionType.PASTE
                    }
                }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                if (mIsUndoOrRedo) {
                    return
                }
                mBeforeChange = s.subSequence(start, start + count)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (mIsUndoOrRedo) {
                    return
                }
                mAfterChange = s.subSequence(start, start + count)
                makeBatch(start)
            }

            private fun makeBatch(start: Int) {
                val at = actionType
                val editItem = mEditHistory.current
                if (lastActionType != at || ActionType.PASTE == at || System.currentTimeMillis() - lastActionTime > 1000 || editItem == null) {
                    mEditHistory.add(EditItem(start, mBeforeChange!!, mAfterChange!!))
                } else {
                    if (at == ActionType.DELETE) {
                        editItem.start = start
                        editItem.before = (mBeforeChange!!).toString() + editItem.before.toString()
                    } else {
                        editItem.after = (editItem.after).toString() + mAfterChange!!.toString()
                    }
                }
                lastActionType = at
                lastActionTime = System.currentTimeMillis()
            }

            override fun afterTextChanged(s: Editable) {}
        }
    }
}