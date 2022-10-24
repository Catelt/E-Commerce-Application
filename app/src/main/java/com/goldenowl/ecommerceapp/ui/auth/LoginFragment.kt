package com.goldenowl.ecommerceapp.ui.auth

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.facebook.login.LoginManager
import com.goldenowl.ecommerceapp.MainActivity
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.core.BaseFragment
import com.goldenowl.ecommerceapp.core.OnSignInStartedListener
import com.goldenowl.ecommerceapp.databinding.FragmentLoginBinding
import com.goldenowl.ecommerceapp.utilities.REQUEST_SIGN_IN
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(
    FragmentLoginBinding::inflate
) {
    override val viewModel: AuthViewModel by viewModels()

    override val color = R.color.grey2

    override fun setUpViews() {
        binding.apply {
            appBarLayout.topAppBar.title = getString(R.string.login)
            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
            }

            btnLogin.setOnClickListener {
                viewModel.logIn(
                    editTextEmail.text.toString(),
                    editTextPassword.text.toString()
                )
            }

            btnGoogle.setOnClickListener {
                viewModel.signInWithGoogle(object :
                    OnSignInStartedListener {
                    override fun onSignInStarted(client: GoogleSignInClient?) {
                        startActivityForResult(client?.signInIntent, REQUEST_SIGN_IN)
                    }
                })
            }
            btnFacebook.setOnClickListener {
                LoginManager.getInstance()
                    .logInWithReadPermissions(
                        requireActivity(), viewModel.callbackManager, listOf(
                            AuthViewModel.PUBLIC_PROFILE,
                            AuthViewModel.EMAIL,
                            AuthViewModel.USER_FRIEND
                        )
                    )
                viewModel.loginWithFacebook()
            }

            btnForgetPassword.setOnClickListener {
                findNavController().navigate(R.id.forgotPasswordFragment)
            }
        }

    }

    override fun setUpObserve() {
        viewModel.initLogin()
        viewModel.apply {
            toastMessage.observe(viewLifecycleOwner) { str ->
                toastMessage(str)
                toastMessage.postValue("")
            }
            userLiveData.observe(viewLifecycleOwner) {
                if (it != null) {
                    startActivity(Intent(activity, MainActivity::class.java))
                    activity?.finish()
                    resetBlock()
                }
            }

            validEmailLiveData.observe(viewLifecycleOwner) {
                alertEmail(it)
            }
            isLoading.observe(viewLifecycleOwner) {
                setLoading(it)
            }
            isBlock.observe(viewLifecycleOwner){
                binding.btnLogin.isEnabled = !it
            }

        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewModel.callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SIGN_IN && resultCode == Activity.RESULT_OK && data != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                account.idToken?.let { viewModel.firebaseAuthWithGoogle(it) }

            } catch (e: ApiException) {
                Toast.makeText(this.context, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private fun alertEmail(alert: String?) {
        alertEditText(alert,binding.txtLayoutEmail)
    }
}