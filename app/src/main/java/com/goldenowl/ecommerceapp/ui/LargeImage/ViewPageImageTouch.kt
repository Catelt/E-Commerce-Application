package com.goldenowl.ecommerceapp.ui.LargeImage

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.ItemViewPagerImageTouchBinding
import com.goldenowl.ecommerceapp.ui.BaseFragment
import com.goldenowl.ecommerceapp.utilities.FileUtil
import com.goldenowl.ecommerceapp.utilities.GlideDefault


class ViewPageImageTouch(private val url: String) : BaseFragment() {
    private lateinit var binding: ItemViewPagerImageTouchBinding
    private var haveImage = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ItemViewPagerImageTouchBinding.inflate(inflater, container, false)
        Glide.with(requireContext())
            .load(url)
            .placeholder(GlideDefault.createCircularProgress(requireContext()))
            .error(R.drawable.img_default)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    p0: GlideException?,
                    p1: Any?,
                    p2: Target<Drawable>?,
                    p3: Boolean
                ): Boolean {
                    haveImage = false
                    return false
                }

                override fun onResourceReady(
                    p0: Drawable?,
                    p1: Any?,
                    p2: Target<Drawable>?,
                    p3: DataSource?,
                    p4: Boolean
                ): Boolean {
                    haveImage = true
                    return false
                }
            })
            .into(binding.touchImageView)
        return binding.root
    }

    fun downloadImage() {
        if(haveImage){
            val bitmap = binding.touchImageView.drawable.toBitmap()
            FileUtil.getImageUri(requireContext(), bitmap)?.let {
                toastMessage(getString(R.string.save_success))
            }
        }
    }
}