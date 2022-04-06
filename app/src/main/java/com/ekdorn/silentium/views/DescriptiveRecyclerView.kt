package com.ekdorn.silentium.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.ekdorn.silentium.R
import com.ekdorn.silentium.databinding.ViewDescriptiveRecyclerviewBinding
import com.ekdorn.silentium.visuals.DoubleItemCallback
import com.ekdorn.silentium.visuals.SeparationDecorator


class DescriptiveRecyclerView(context: Context, attributes: AttributeSet?, style: Int) : ConstraintLayout(context, attributes, style) {
    constructor(context: Context, attributes: AttributeSet?): this(context, attributes, 0)
    constructor(context: Context): this(context, null)

    abstract class Adapter <ViewHolder: RecyclerView.ViewHolder> : RecyclerView.Adapter<ViewHolder>() {
        open fun separators(): List<Pair<Int, String>> = emptyList()

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.setOnClickListener { onClick(viewHolder, viewHolder.bindingAdapterPosition) }
            viewHolder.itemView.setOnLongClickListener { onLongClick(viewHolder, viewHolder.bindingAdapterPosition); true }
        }

        abstract fun onClick(viewHolder: ViewHolder, position: Int)

        abstract fun onLongClick(viewHolder: ViewHolder, position: Int)
    }

    private val binding = ViewDescriptiveRecyclerviewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        context.theme.obtainStyledAttributes(attributes, R.styleable.DescriptiveRecyclerView, 0, 0).apply {
            try { binding.emptyText.text = getString(R.styleable.DescriptiveRecyclerView_text) } finally { recycle() }
        }
    }

    fun initRecycler(adapter: Adapter<*>, manager: RecyclerView.LayoutManager) = binding.recycler.apply {
        this.layoutManager = manager
        this.adapter = adapter
        this.itemAnimator = DefaultItemAnimator()

        val decorator = SeparationDecorator(context)
        this.addItemDecoration(decorator)

        val itemsChanged = { separators: List<Pair<Int, String>> ->
            decorator.separators = separators
            invalidateItemDecorations()
            binding.emptyText.alpha = if (binding.recycler.adapter?.itemCount == 0) 1F else 0F
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

    fun setItemCallback(callback: DoubleItemCallback) = ItemTouchHelper(callback).attachToRecyclerView(binding.recycler)
}
