package com.bennyv17.river.highlighter.view

import android.content.Context
import android.graphics.*
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet
import com.bennyv17.river.util.Tool
import android.text.Selection
import android.view.MotionEvent
import com.bennyv17.river.R
import android.text.Spannable
import android.text.Editable
import android.text.style.ReplacementSpan

open class LineNumberEditText(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {

    private val tempRect: Rect = Rect()
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dimPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val selectionPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paint.typeface = Typeface.MONOSPACE
        paint.style = Paint.Style.FILL
        paint.color = currentTextColor
        paint.textSize = textSize

        selectionPaint.style = Paint.Style.FILL
        selectionPaint.color = Tool.getThemeColor(getContext(), R.attr.colorAccent)
        selectionPaint.alpha = 40

        dimPaint.style = Paint.Style.FILL
        dimPaint.color = Color.DKGRAY
        dimPaint.alpha = 20

        updatePadding()

        setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    if (event.x < paddingLeft) {
                        val clickedLine: Int = ((event.y - paddingTop) / lineHeight).toInt()

                        if (clickedLine > lineCount - 1)
                            return@setOnTouchListener false

                        val start = layout.getLineStart(clickedLine)
                        val end = layout.getLineEnd(clickedLine) - 1

                        if (start < text.length && end < text.length)
                            setSelection(start, end)
                    }
                }
            }
            false
        }
    }

    override fun setTextSize(unit: Int, size: Float) {
        super.setTextSize(unit, size)

        paint.textSize = textSize
    }

    override fun onTextChanged(m_text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(m_text, start, lengthBefore, lengthAfter)

        applyTabWidth(text, start, start + lengthAfter)
    }

    private fun applyTabWidth(text: Editable, start: Int, end: Int) {
        var m_start = start
        val str = text.toString()
        val tabWidth = getPaint().measureText("m") * 2
        while (m_start < end) {
            val index = str.indexOf("\t", m_start)
            if (index < 0)
                break
            text.setSpan(CustomTabWidthSpan(tabWidth.toInt()), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            m_start = index + 1
        }
    }

    private fun updatePadding() {
        val predictText = get0Of(getDigitCountOf(lineCount))
        paint.getTextBounds(predictText, 0, predictText.length, tempRect)
        setPadding(tempRect.width() + Tool.dp2px(20f).toInt(), paddingTop, paddingRight, paddingBottom)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        updatePadding()
    }

    override fun onDraw(canvas: Canvas) {
        var currentBaseline = baseline

        for (i in 0 until lineCount) {
            val numberText = (i + 1).toString()
            val predictText = get0Of(getDigitCountOf(i + 1))
            paint.getTextBounds(predictText, 0, predictText.length, tempRect)
            canvas.drawText(numberText, (paddingLeft - tempRect.width() - Tool.dp2px(6f).toInt()).toFloat(), currentBaseline.toFloat(), paint)

            if (isFocused && i == getCurrentCursorLine())
                canvas.drawRect(paddingLeft.toFloat(), currentBaseline.toFloat() - lineHeight + (if (i == 0) 0 else Tool.dp2px(4f).toInt()), right.toFloat(), currentBaseline.toFloat() + Tool.dp2px(5f).toInt(), selectionPaint)

            if (i != lineCount - 1)
                currentBaseline += lineHeight
        }

        val startX = paddingLeft.toFloat() - Tool.dp2px(2f).toInt()
        val endY = currentBaseline.toFloat() + Tool.dp2px(4f)


        canvas.drawRect(0f, 0f, startX, endY, dimPaint)
        canvas.drawLine(startX, 0f, startX, endY, paint)

        super.onDraw(canvas)
    }

    private fun getCurrentCursorLine(): Int {
        val selectionStart = Selection.getSelectionStart(text)
        val layout = layout

        return if (selectionStart != -1) {
            layout.getLineForOffset(selectionStart)
        } else -1
    }

    private fun get0Of(n: Int): String {
        var str = ""
        for (i in 0 until n) {
            str += "0"
        }
        return str
    }

    private fun getDigitCountOf(n: Int): Int {
        if (n < 100000) {
            // 5 or less
            if (n < 100) {
                // 1 or 2
                return if (n < 10)
                    1
                else
                    2
            } else {
                // 3 or 4 or 5
                return if (n < 1000)
                    3
                else {
                    // 4 or 5
                    if (n < 10000)
                        4
                    else
                        5
                }
            }
        } else {
            // 6 or more
            if (n < 10000000) {
                // 6 or 7
                return if (n < 1000000)
                    6
                else
                    7
            } else {
                // 8 to 10
                return if (n < 100000000)
                    8
                else {
                    // 9 or 10
                    if (n < 1000000000)
                        9
                    else
                        10
                }
            }
        }
    }

    class CustomTabWidthSpan(private val tabWidth: Int) : ReplacementSpan() {

        override fun getSize(paint: Paint?, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
            return tabWidth
        }

        override fun draw(canvas: Canvas?, text: CharSequence?, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint?) {
        }

    }
}