package com.goldenowl.ecommerceapp.ui.auth

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.goldenowl.ecommerceapp.MainActivity
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.core.BaseFragment
import com.goldenowl.ecommerceapp.core.OnSignInStartedListener
import com.goldenowl.ecommerceapp.databinding.FragmentSignUpBinding
import com.goldenowl.ecommerceapp.utilities.REQUEST_SIGN_IN
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>(
    FragmentSignUpBinding::inflate
) {
    override val viewModel: AuthViewModel by viewModels()

    override val color = R.color.grey2

    override fun setUpViews() {
        binding.apply {
            appBarLayout.topAppBar.title = getString(R.string.sign_up)
            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()
            }

            btnAlreadyHave.setOnClickListener(
                Navigation.createNavigateOnClickListener(
                    R.id.action_signUpFragment_to_loginFragment,
                    null
                )
            )

            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()
            }

            btnSignUp.setOnClickListener {
                viewModel.signUp(
                    editTextName.text.toString(),
                    editTextEmail.text.toString(),
                    editTextPassword.text.toString()
                )
            }

            editTextName.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    viewModel.validName(editTextName.text.toString())
                }
            }

            editTextEmail.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    viewModel.validEmail(editTextEmail.text.toString())
                }
            }

            editTextPassword.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    viewModel.validPassword(editTextPassword.text.toString())
                }
            }

            btnGoogle.setOnClickListener {
                viewModel.signInWithGoogle(object :
                    OnSignInStartedListener {
                    override fun onSignInStarted(client: GoogleSignInClient?) {
                        startActivityForResult(client?.signInIntent, REQUEST_SIGN_IN)
                    }
                })
            }
        }
    }

    override fun setUpObserve() {
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

            validNameLiveData.observe(viewLifecycleOwner) {
                alertName(it)
            }

            validEmailLiveData.observe(viewLifecycleOwner) {
                alertEmail(it)
            }

            validPasswordLiveData.observe(viewLifecycleOwner) {
                alertPassword(it)
            }

            isLoading.observe(viewLifecycleOwner) {
                setLoading(it)
            }
            isBlock.observe(viewLifecycleOwner){
                binding.btnSignUp.isEnabled = !it
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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


    private fun alertName(alert: String) {
        alertEditText(alert,binding.txtLayoutName)
    }

    private fun alertEmail(alert: String) {
        alertEditText(alert,binding.txtLayoutEmail)
    }

    private fun alertPassword(alert: String) {
        alertEditText(alert,binding.txtLayoutPassword)
    }
}