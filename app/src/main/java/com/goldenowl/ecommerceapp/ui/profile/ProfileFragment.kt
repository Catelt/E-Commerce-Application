package com.goldenowl.ecommerceapp.ui.profile

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.core.BaseFragment
import com.goldenowl.ecommerceapp.databinding.FragmentProfileBinding
import com.goldenowl.ecommerceapp.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(
    FragmentProfileBinding::inflate
) {
    override val viewModel: AuthViewModel by viewModels()
    override var isHideBottom = false

    override fun setUpViews() {
        if (!viewModel.isLogged()) {
            findNavController().navigate(R.id.action_profileFragment_to_profileNoLoginFragment)
        } else {
            findNavController().navigate(R.id.action_profileFragment_to_profileLoginFragment)
        }
    }
}