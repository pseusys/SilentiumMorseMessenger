package com.ekdorn.silentium.preferences

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.ekdorn.silentium.R
import com.ekdorn.silentium.managers.NetworkManager
import com.ekdorn.silentium.utils.Observer


class ImagePreference(context: Context, attrs: AttributeSet?, defStyleAttr: Int): Preference(context, attrs, defStyleAttr) {
    constructor (context: Context, attrs: AttributeSet?): this(context, attrs, R.attr.preferenceStyle)
    constructor (context: Context): this(context, null)

    private var click: (() -> Unit)? = null

    var value: Uri = Uri.EMPTY
        set (value) {
            callChangeListener(value)
            field = value
            persistString(value.toString())
            notifyChanged()
            Glide.with(context).clear(future)
            loadPic(value)
        }

    private var future: CustomTarget<Bitmap>? = null


    init {
        widgetLayoutResource = R.layout.part_close_button
        loadPic(value)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        holder.findViewById(R.id.pref_close_button).setOnClickListener { value = Uri.EMPTY }
    }

    override fun onPrepareForRemoval() {
        super.onPrepareForRemoval()
        Glide.with(context).clear(future)
    }

    override fun onClick() = if (click != null) click!!() else Unit

    fun setupCallback (observer: Observer) {
        click = { observer.launch(ActivityResultContracts.GetContent::class, "image/*") { it?.apply { value = this } } }
    }

    private fun loadPic (pic: Uri?) {
        val target = object : CustomTarget<Bitmap>(100, 100) {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                icon = BitmapDrawable(context.resources, resource)
            }
            override fun onLoadCleared(placeholder: Drawable?) {}
        }
        val picture = if (pic == Uri.EMPTY) R.drawable.picture_image else pic
        future = Glide.with(context).setDefaultRequestOptions(NetworkManager.options).asBitmap().load(picture).into(target)
    }
}
