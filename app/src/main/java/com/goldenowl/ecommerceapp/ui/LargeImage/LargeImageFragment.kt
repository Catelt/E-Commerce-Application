package com.goldenowl.ecommerceapp.ui.LargeImage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.goldenowl.ecommerceapp.adapters.ImageTouchAdapter
import com.goldenowl.ecommerceapp.databinding.FragmentLargeImageBinding
import com.goldenowl.ecommerceapp.ui.BaseFragment
import com.goldenowl.ecommerceapp.utilities.BUNDLE_KEY_POSITION
import com.goldenowl.ecommerceapp.utilities.URL_IMAGE


class LargeImageFragment : BaseFragment() {
    private var argument: String = ""
    private var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            argument = it.getString(URL_IMAGE).toString()
            currentPosition = it.getInt(BUNDLE_KEY_POSITION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLargeImageBinding.inflate(inflater, container, false)
        if (argument.isNotBlank() && argument != "null") {
            val listUrl = argument.split("`")
            val adapter = ImageTouchAdapter(this@LargeImageFragment, listUrl)
            binding.apply {
                viewPagerImage.adapter = adapter
                viewPagerImage.registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        currentPosition = position
                        super.onPageSelected(position)
                    }
                })
                btnBack.setOnClickListener {
                    findNavController().navigateUp()
                }
                btnDownload.setOnClickListener {
                    adapter.onClickDownload(currentPosition)
                }
                viewPagerImage.currentItem = currentPosition
            }
        }
        return binding.root
    }
}