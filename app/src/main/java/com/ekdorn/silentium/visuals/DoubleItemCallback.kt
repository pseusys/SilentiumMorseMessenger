package com.ekdorn.silentium.visuals

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class DoubleItemCallback(context: Context, private val leftAction: VisualAction? = null, private val rightAction: VisualAction? = null) : ItemTouchHelper.SimpleCallback(0, (if (leftAction != null) ItemTouchHelper.LEFT else 0) or (if (rightAction != null) ItemTouchHelper.RIGHT else 0)) {
    private val background = ColorDrawable()
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.DST) }

    init {
        leftAction?.load(context)
        rightAction?.load(context)
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        var flags = 0
        if (viewHolder.bindingAdapterPosition in leftAction?.views ?: IntRange.EMPTY) flags = flags or ItemTouchHelper.LEFT
        if (viewHolder.bindingAdapterPosition in rightAction?.views ?: IntRange.EMPTY) flags = flags or ItemTouchHelper.RIGHT
        return makeMovementFlags(0, flags)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when (direction) {
            ItemTouchHelper.LEFT -> leftAction?.callback?.invoke(viewHolder.bindingAdapterPosition)
            ItemTouchHelper.RIGHT -> rightAction?.callback?.invoke(viewHolder.bindingAdapterPosition)
            else -> Log.e("DoubleItemChecker", "viewHolder ${viewHolder.bindingAdapterPosition} was swiped to direction $direction")
        }
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = (dX == 0F) && !isCurrentlyActive

        val action = if (isCanceled || (viewHolder.bindingAdapterPosition == -1)) {
            c.clear(itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
            return super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        } else if ((dX > 0) && (rightAction != null)) rightAction
        else if ((dX < 0) && (leftAction != null)) leftAction
        else return super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val iconMargin = (itemHeight - action.iconDrawable.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemHeight - action.iconDrawable.intrinsicHeight) / 2
        val iconBottom = iconTop + action.iconDrawable.intrinsicHeight
        val iconLeft: Int
        val iconRight: Int

        if (action == leftAction) {
            iconLeft = itemView.right - iconMargin - action.iconDrawable.intrinsicWidth
            iconRight = itemView.right - iconMargin
            background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        } else {
            iconRight = itemView.left + iconMargin + action.iconDrawable.intrinsicWidth
            iconLeft = itemView.left + iconMargin
            background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
        }

        background.color = action.colorColor
        background.draw(c)

        action.iconDrawable.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        action.tintColor?.let { action.iconDrawable.setTint(it) }
        action.iconDrawable.draw(c)

        return super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun Canvas.clear(left: Float, top: Float, right: Float, bottom: Float) = drawRect(left, top, right, bottom, clearPaint)
}
