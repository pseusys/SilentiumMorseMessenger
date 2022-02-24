package com.ekdorn.silentium.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.ekdorn.silentium.R


class DescriptiveRecyclerView(context: Context, attributes: AttributeSet?, style: Int) : ConstraintLayout(context, attributes, style) {
    constructor(context: Context, attributes: AttributeSet?): this(context, attributes, 0)
    constructor(context: Context): this(context, null)

    abstract class Adapter <ViewHolder: RecyclerView.ViewHolder> : RecyclerView.Adapter<ViewHolder>() {
        open fun separators(): List<Pair<Int, String>> = emptyList()
    }

    class SeparationDecorator(context: Context) : ItemDecoration() {
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

    private val recycler: RecyclerView
    private val emptyText: TextView

    init {
        View.inflate(context, R.layout.view_descriptive_recyclerview, this)
        recycler = findViewById(R.id.recycler)
        emptyText = findViewById(R.id.empty_text)

        context.theme.obtainStyledAttributes(attributes, R.styleable.DescriptiveRecyclerView, 0, 0).apply {
            try { emptyText.text = getString(R.styleable.DescriptiveRecyclerView_text) } finally { recycle() }
        }
    }

    fun initRecycler(adapter: Adapter<*>, manager: RecyclerView.LayoutManager) = recycler.apply {
        this.layoutManager = manager
        this.adapter = adapter
        this.itemAnimator = DefaultItemAnimator()

        val decorator = SeparationDecorator(context)
        this.addItemDecoration(decorator)

        val itemsChanged = { separators: List<Pair<Int, String>> ->
            decorator.separators = separators
            emptyText.alpha = if (recycler.adapter?.itemCount == 0) 1F else 0F
        }

        itemsChanged(adapter.separators())
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                itemsChanged(adapter.separators())
            }

            override fun onItemRangeChanged(ps: Int, ic: Int) {
                super.onItemRangeChanged(ps, ic)
                itemsChanged(adapter.separators())
            }

            override fun onItemRangeChanged(ps: Int, ic: Int, p: Any?) {
                super.onItemRangeChanged(ps, ic, p)
                itemsChanged(adapter.separators())
            }

            override fun onItemRangeMoved(fp: Int, tp: Int, ic: Int) {
                super.onItemRangeMoved(fp, tp, ic)
                itemsChanged(adapter.separators())
            }

            override fun onItemRangeInserted(ps: Int, ic: Int) {
                super.onItemRangeInserted(ps, ic)
                itemsChanged(adapter.separators())
            }

            override fun onItemRangeRemoved(ps: Int, ic: Int) {
                super.onItemRangeRemoved(ps, ic)
                itemsChanged(adapter.separators())
            }
        })
    }
}
