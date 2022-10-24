package com.goldenowl.ecommerceapp.ui.Tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.goldenowl.ecommerceapp.databinding.FragmentIntroPageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroPageFragment(img: Int = 0, nameTopic: String = "") : Fragment() {
    private var _img = img
    private var _nameTopic = nameTopic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            _img =  getInt(IMG)
            _nameTopic = getString(TITLE) ?: _nameTopic
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentIntroPageBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        binding.txtIntro.text = _nameTopic
        binding.imgIntro.setImageResource(_img)
        return binding.root
    }

    companion object{
        const val IMG = "IMG"
        const val TITLE = "TITLE"

        @JvmStatic
        fun newInstance(_img: Int,_nameTopic: String) = IntroPageFragment().apply {
            arguments?.apply {
                putInt(IMG,_img)
                putString(TITLE,_nameTopic)
            }
        }
    }
}