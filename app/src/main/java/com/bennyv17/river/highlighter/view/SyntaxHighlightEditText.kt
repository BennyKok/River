package com.bennyv17.river.highlighter.view

import android.content.Context
import android.os.Handler
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import com.bennyv17.river.highlighter.AsyncHighLightSpan
import com.bennyv17.river.highlighter.SyntaxHighlighter
import com.bennyv17.river.highlighter.syntax.Syntax
import com.bennyv17.river.highlighter.theme.SyntaxTheme

open class SyntaxHighlightEditText(context: Context, attrs: AttributeSet) :
        UndoRedoEditText(context, attrs), AsyncHighLightSpan.OnHighlightReadyListener {

    private var task: AsyncHighLightSpan? = null
    private var updateHandler = Handler(getContext().mainLooper)
    var autoHighLight = true

    private var highlightRunnable: Runnable = Runnable {
        task = AsyncHighLightSpan(this)
        task!!.execute(text.toString())
    }

    private var consumeNextTextChange = false
    private var preTextLength = 0

    private var themeColors: HashMap<String, Int>? = null

    var syntax: Syntax? = null
        set(value) {
            field = value

            val language = SyntaxHighlighter.Language(syntax!!.getName())
            language.patterns = syntax!!.getPatterns()

            SyntaxHighlighter.highlightLang = language
        }

    var syntaxTheme: SyntaxTheme? = null
        set(value) {
            field = value

            themeColors = syntaxTheme!!.getThemeColors()
        }

    override fun onHighlight(type: String, start: Int, end: Int) {
        if (themeColors == null) throw IllegalStateException("Haven't init the syntax theme")

        if (end <= text!!.length && start <= text!!.length) {
            //TODO()
            //val foregroundSpanConflicts = text.getSpans(start, end, ForegroundColorSpan::class.java)
            //for (foregroundSpan in foregroundSpanConflicts)
            //    text.removeSpan(foregroundSpan)

            text!!.setSpan(ForegroundColorSpan(themeColors!![type]!!), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    override fun onBeforeHighlight() {
        val spans = text!!.getSpans(0, text!!.length - 1, ForegroundColorSpan::class.java)
        for (span in spans)
            text!!.removeSpan(span)
    }

    fun highlightText() {
        if (task != null)
            task!!.cancel(true)
        updateHandler.removeCallbacks(highlightRunnable)
        updateHandler.postDelayed(highlightRunnable, 400)
    }

    override fun onTextChanged(m_text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(m_text, start, lengthBefore, lengthAfter)

        if (!autoHighLight) return

        if (text!!.length == preTextLength)
            consumeNextTextChange = true
        preTextLength = text!!.length

        if (consumeNextTextChange) {
            consumeNextTextChange = false
            return
        }

        highlightText()
    }

}