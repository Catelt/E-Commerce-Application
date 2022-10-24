package com.goldenowl.ecommerceapp.ui.profile

import android.content.Intent
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.core.BaseFragment
import com.goldenowl.ecommerceapp.databinding.FragmentProfileNoLoginBinding
import com.goldenowl.ecommerceapp.ui.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileNoLoginFragment : BaseFragment<FragmentProfileNoLoginBinding>(
    FragmentProfileNoLoginBinding::inflate
) {
    override var isHideBottom = false

    override fun setUpViews() {
        binding.apply {
            appBarLayout.topAppBar.title = getString(R.string.my_profile)

            profileNoLoginLayout.setOnClickListener {
                startActivity(Intent(activity, AuthActivity::class.java))
                activity?.finish()
            }
        }
    }
}