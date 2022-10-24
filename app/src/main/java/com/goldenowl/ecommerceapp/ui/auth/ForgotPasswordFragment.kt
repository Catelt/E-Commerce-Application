package com.goldenowl.ecommerceapp.ui.auth

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.core.BaseFragment
import com.goldenowl.ecommerceapp.databinding.FragmentForgotPasswordBinding
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : BaseFragment<FragmentForgotPasswordBinding>(
    FragmentForgotPasswordBinding::inflate
) {
    override val viewModel: AuthViewModel by viewModels()

    override val color = R.color.grey2

    override fun setUpObserve() {
        viewModel.apply {
            toastMessage.observe(viewLifecycleOwner) { str ->
                toastMessage(str)
                if (str == SUCCESS) {
                    findNavController().navigateUp()
                }
                toastMessage.postValue("")
            }

            validEmailLiveData.observe(viewLifecycleOwner) {
                alertEmail(it)
            }

            isLoading.observe(viewLifecycleOwner) {
                setLoading(it)
            }
        }
    }

    override fun setUpViews() {
        binding.apply {
            appBarLayout.MaterialToolbar.title = getString(R.string.forgot_password)
            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            btnForgetPassword.setOnClickListener {
                viewModel.forgotPassword(editTextEmail.text.toString())
            }
        }
    }

    private fun alertEmail(alert: String?) {
        binding.apply {
            if (!alert.isNullOrEmpty()) {
                txtLayoutEmail.isErrorEnabled = true
                txtLayoutEmail.error = alert
                txtLayoutEmail.endIconMode = TextInputLayout.END_ICON_NONE
            } else {
                txtLayoutEmail.isErrorEnabled = false
                txtLayoutEmail.endIconMode = TextInputLayout.END_ICON_CUSTOM
            }
        }
    }

    companion object {
        const val SUCCESS = "Email sent."
    }
}