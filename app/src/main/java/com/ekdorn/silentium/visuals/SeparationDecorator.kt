package com.ekdorn.silentium.visuals

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.ekdorn.silentium.R


class SeparationDecorator(context: Context) : RecyclerView.ItemDecoration() {
    private var sepIndexes = emptyList<Int>()
    var separators = emptyList<Pair<Int, String>>()
        set(value) {
            sepIndexes = value.map { it.first }
            field = value
        }

    private val text = context.resources.getDimension(R.dimen.recycler_view_separator_size)
    private val spacing = context.resources.getDimension(R.dimen.activity_margin).toInt()
    private val separator = text * 2
    private val paint: Paint = Paint().apply {
        textSize = text
        isAntiAlias = true
        typeface = Typeface.defaultFromStyle(Typeface.BOLD)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) = parent.children.forEach { view ->
        separators.find { it.first == parent.getChildAdapterPosition(view) }?.apply {
            c.drawText(second, view.left.toFloat(), view.top - separator / 2F + text / 3F + view.translationY, paint)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        var top = 0
        var bottom = 0
        if (parent.getChildAdapterPosition(view) == 0) top += spacing
        if (parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1) bottom += spacing
        if (parent.getChildAdapterPosition(view) in sepIndexes) top = separator.toInt()
        outRect.set(0, top, 0, bottom)
    }
}
