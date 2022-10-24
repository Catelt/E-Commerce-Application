package com.goldenowl.ecommerceapp.ui.general

import androidx.navigation.fragment.findNavController
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.core.BaseFragment
import com.goldenowl.ecommerceapp.databinding.FragmentNoInternetBinding
import com.goldenowl.ecommerceapp.utilities.NetworkHelper
import com.goldenowl.ecommerceapp.utilities.WARNING_CHECK_AGAIN

class NoInternetFragment : BaseFragment<FragmentNoInternetBinding>(
    FragmentNoInternetBinding::inflate
) {
    override fun setUpViews() {
        binding.btnTryAgain.setOnClickListener {
            if(NetworkHelper.isNetworkAvailable(requireContext())){
                findNavController().navigateUp()
            }
            else{
                toastMessage(WARNING_CHECK_AGAIN)
            }
        }
    }

    override fun onBackPressed(): Boolean {
        findNavController().popBackStack(R.id.homeFragment,false)
        return true
    }
}