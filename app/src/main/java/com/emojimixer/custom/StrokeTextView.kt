package com.emojimixer.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.emojimixer.R

class StrokeTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var stroke = true
    private var strokeWidth = 2f
    private var strokeColor = Color.parseColor("#2B4E83")

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.StrokeTextView,
            0, 0
        ).apply {
            try {
                stroke = getBoolean(R.styleable.StrokeTextView_stroke, false)
                strokeWidth = getDimension(R.styleable.StrokeTextView_strokeWidth, 2f)
                strokeColor = getColor(R.styleable.StrokeTextView_strokeColor, currentTextColor)


            } finally {
                recycle()
            }
        }
    }

    fun setStrokeWidth(width: Float) {
        strokeWidth =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, resources.displayMetrics)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        // Enable drawing of the fill first
        val originalTextColor = currentTextColor
        paint.style = Paint.Style.FILL
        paint.color = originalTextColor

        // Draw the fill text
        super.onDraw(canvas)

        if (stroke && strokeWidth > 0) {
            // Now, draw the stroke over the filled text
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = strokeWidth
            paint.color = strokeColor

            // Draw the text as a stroke
            super.onDraw(canvas)
        }
    }


    fun setCustomFont(context: Context, fontId: Int) {
        typeface = ResourcesCompat.getFont(context, fontId)
    }
}
