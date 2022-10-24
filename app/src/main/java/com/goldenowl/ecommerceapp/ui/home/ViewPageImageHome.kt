package com.goldenowl.ecommerceapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.goldenowl.ecommerceapp.databinding.ItemViewPagerHomeBinding
import com.goldenowl.ecommerceapp.utilities.GlideDefault

class ViewPageImageHome(url: String = "",title: String = "") : Fragment() {
    private lateinit var binding: ItemViewPagerHomeBinding
    private var _url = url
    private var _title = title

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            _url = getString(URL) ?: _url
            _title = getString(TITLE) ?: _title
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ItemViewPagerHomeBinding.inflate(inflater, container, false)

        binding.apply {
            GlideDefault.showHome(requireContext(), _url, imgHome)
            txtTitle.text = _title
            imgHome.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToCatalogFragment(
                    nameCategories = "",
                    nameProduct = null
                )
                findNavController().navigate(action)
            }
        }
        return binding.root
    }

    companion object{
        const val URL = "URL"
        const val TITLE = "TITLE"

        @JvmStatic
        fun newInstance(_url: String,_title: String) = ViewPageImageHome().apply {
            arguments?.apply {
                putString(URL,_url)
                putString(TITLE,_title)
            }
        }
    }
}