package com.innov8.memegenerator.memeEngine

import android.content.Context
import android.graphics.*
import android.text.*
import android.text.Layout.Alignment.*
import android.text.style.StyleSpan
import android.util.AttributeSet
import androidx.core.graphics.withTranslation
import androidx.core.text.set
import androidx.core.text.toSpannable
import com.afollestad.materialdialogs.MaterialDialog
import com.memeit.backend.models.MemeItemProperty
import com.memeit.backend.models.MemeTextItemProperty
import com.memeit.backend.models.MemeTextStyleProperty
import com.innov8.memeit.commons.dp
import com.innov8.memeit.commons.models.TypefaceManager
import com.innov8.memeit.commons.sp
import com.innov8.memeit.commons.toSP

class MemeTextItem : MemeItemView {
    private lateinit var dynamicLayout: DynamicLayout
    var text: String = ""
        set(value) {
            field = value
            resizeToWrapText(true)
        }
    private var hint: String = "Add your text here"
    private var font = "Aileron"
    private var bold: Boolean = false
    private var italic: Boolean = false
    private var allCaps: Boolean = false
    private var stroke: Boolean = false
    private var strokeColor: Int = Color.WHITE
    private var color: Int = Color.BLACK
    private var bgColor: Int = Color.TRANSPARENT
    private var alignment: Layout.Alignment = ALIGN_CENTER

    constructor(context: Context, requiredWidth: Int = 100, requiredHeight: Int = 100) : super(context, requiredWidth, requiredHeight) {
        initDefaultDynamicLayout()
    }

    constructor(context: Context, tp: MemeTextItemProperty, mw: Int = 0, mh: Int = 0) : super(context, tp, mw, mh) {
        tp.tsp?.let {
            dynamicLayout = DynamicLayout(drawingText, TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = TypefaceManager.byName(it.font)
                color = it.textColor
                textSize = it.textSize.sp(context)
                style = Paint.Style.FILL
                strokeWidth = it.strokeWidth.sp(context)
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
            }, itemWidth, it.alignment, 1f, 0f, false)
            bgPaint.color = it.bgColor
            color = it.textColor
            font = it.font
            bold = it.bold
            italic = it.italic
            allCaps = it.allCap
            stroke = it.stroked
            strokeColor = it.strokeColor
            bgColor = it.bgColor
            alignment = it.alignment

        } ?: initDefaultDynamicLayout()
    }


    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initDefaultDynamicLayout()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initDefaultDynamicLayout()
    }


    private val bgPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = bgColor
        }
    }

    init {
        minimumWidth = 10.dp(context)
        minimumHeight = 10.dp(context)
        val pad = 2.dp(context)
        setPadding(pad, 0, pad, 0)
        if (isInMemeEditor)
            onClickListener = {
                MaterialDialog.Builder(context)
                        .title("Insert Text")
                        .inputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                        .input("Write the text here", text
                        ) { _, input ->
                            text = input.toString()
                        }
                        .show()
            }
    }

    private fun initDefaultDynamicLayout() {
        dynamicLayout = DynamicLayout(drawingText, TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = TypefaceManager.byName(font)
            color = Color.BLACK
            textSize = 20f.sp(context)
            style = Paint.Style.FILL
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }, itemWidth, alignment, 1f, 0f, false)
    }


    fun setTypeface(font: String) {
        this.font = font
        dynamicLayout.paint.typeface = TypefaceManager.byName(font)
        resizeToWrapText()
    }

    fun setTextSize(value: Float) {
        dynamicLayout.paint.textSize = value
        resizeToWrapText(true)
    }

    fun setBold(value: Boolean) {
        bold = value
        recreateDynamicLayout()
        invalidate()
    }

    fun setItalic(value: Boolean) {
        italic = value
        recreateDynamicLayout()
        invalidate()
    }

    fun setAllCaps(value: Boolean) {
        allCaps = value
        recreateDynamicLayout()
        invalidate()
    }

    fun setStroke(value: Boolean) {
        stroke = value
        invalidate()
    }

    fun setStrokeWidth(value: Float) {
        dynamicLayout.paint.strokeWidth = value
        invalidate()
    }

    fun setStrokeColor(value: Int) {
        strokeColor = value
        invalidate()
    }

    fun setTextColor(value: Int) {
        color = value
        dynamicLayout.paint.color = value
        invalidate()
    }

    fun setBgColor(value: Int) {
        bgColor = value
        bgPaint.color = value
        invalidate()
    }

    fun setAlignment(value: Layout.Alignment) {
        alignment = value
        recreateDynamicLayout()
        invalidate()
    }


    private val drawingText: Spannable
        get() {
            val t = text.takeIf { it.isNotBlank() } ?: hint
            val s = (if (allCaps) t.toUpperCase() else t).toSpannable()
            if (bold && italic) s[0..s.length] = StyleSpan(Typeface.BOLD_ITALIC)
            else {
                if (bold) s[0..s.length] = StyleSpan(Typeface.BOLD)
                if (italic) s[0..s.length] = StyleSpan(Typeface.ITALIC)
            }
            return s
        }

    private fun recreateDynamicLayout() {
        dynamicLayout = DynamicLayout(drawingText, dynamicLayout.paint, itemWidth, alignment, 0.8f, 0f, false)
    }

    private fun resizeToWrapText(reset: Boolean = false) {
        if (reset) recreateDynamicLayout()
        if (isInMemeEditor && itemHeight < dynamicLayout.height)
            itemHeight = dynamicLayout.height
        invalidate()
        requestLayout()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        recreateDynamicLayout()
    }


    override fun onDraw(canvas: Canvas) {
        canvas.drawRect(itemX, itemY, itemX + itemWidth, itemY + itemHeight, bgPaint)
        super.onDraw(canvas)
        var a = itemHeight - dynamicLayout.height
        a = if (a > 0) a / 2 else 0
        canvas.withTranslation(itemX + paddingLeft, itemY + paddingTop + a) {
            if (stroke) {
                dynamicLayout.paint.style = Paint.Style.STROKE
                dynamicLayout.paint.color = strokeColor
                dynamicLayout.draw(this)
                dynamicLayout.paint.style = Paint.Style.FILL
            }
            dynamicLayout.paint.color = color
            dynamicLayout.draw(this)
        }
    }

    fun applyTextStyleProperty(tp: MemeTextStyleProperty, applySize: Boolean = true, text: String = "") {
        color = tp.textColor
        if (applySize) dynamicLayout.paint.textSize = tp.textSize.sp(context)
        font = tp.font
        dynamicLayout.paint.typeface = TypefaceManager.byName(tp.font)
        bold = tp.bold
        italic = tp.italic
        allCaps = tp.allCap
        stroke = tp.stroked
        strokeColor = tp.strokeColor
        dynamicLayout.paint.strokeWidth = tp.strokeWidth
        alignment = tp.alignment
        bgColor = tp.bgColor
        this.text = if (text.isBlank()) this.text else text
    }

    override fun copy(): MemeTextItem {
        return MemeTextItem(context, generateProperty() as MemeTextItemProperty, maxWidth, maxHeight).apply {
            x += 10.dp(context)
            y += 10.dp(context)
            text = text
        }
    }

    override fun generateProperty(): MemeItemProperty {
        return MemeTextItemProperty(
                x / maxWidth,
                y / maxHeight,
                itemWidth.toFloat() / maxWidth,
                itemHeight.toFloat() / maxHeight,
                rotation,
                text,
                generateTextStyleProperty()
        )
    }

    fun generateTextStyleProperty() = MemeTextStyleProperty(
            dynamicLayout.paint.textSize.toSP(context),
            color,
            font,
            bold, italic, allCaps,
            stroke, strokeColor, dynamicLayout.paint.strokeWidth.toSP(context),
            bgColor,
            alignment
    )


}
