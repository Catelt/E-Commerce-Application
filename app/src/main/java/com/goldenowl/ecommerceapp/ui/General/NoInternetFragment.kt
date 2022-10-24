package com.goldenowl.ecommerceapp.ui.General

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.FragmentNoInternetBinding
import com.goldenowl.ecommerceapp.ui.BaseFragment
import com.goldenowl.ecommerceapp.utilities.NetworkHelper
import com.goldenowl.ecommerceapp.utilities.WARNING_CHECK_AGAIN

class NoInternetFragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNoInternetBinding.inflate(inflater,container,false)

        binding.btnTryAgain.setOnClickListener {
            if(NetworkHelper.isNetworkAvailable(requireContext())){
                findNavController().navigateUp()
            }
            else{
                toastMessage(WARNING_CHECK_AGAIN)
            }
        }

        return binding.root
    }

    override fun onBackPressed(): Boolean {
        findNavController().popBackStack(R.id.homeFragment,false)
        return true
    }
}