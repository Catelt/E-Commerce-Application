package com.goldenowl.ecommerceapp.ui

import android.content.Intent
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.ui.General.LoadingDialog
import com.goldenowl.ecommerceapp.utilities.BUNDLE_KEY_POSITION
import com.goldenowl.ecommerceapp.utilities.NetworkHelper
import com.goldenowl.ecommerceapp.utilities.URL_IMAGE
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseFragment : Fragment() {
    private var loadingDialog = LoadingDialog(this)

    fun toastMessage(string: String) {
        if (string.isNotBlank()) {
            Toast.makeText(
                activity,
                string,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            loadingDialog.startLoading()
        } else {
            loadingDialog.dismiss()
        }
    }

    fun setLoadingCustom(progress: ProgressBar, isLoading: Boolean){
        progress.isVisible = isLoading
    }

    fun touchImage(images: List<String>, position: Int = 0) {
        findNavController().navigate(
            R.id.largeImageFragment,
            bundleOf(
                URL_IMAGE to images.joinToString("`"),
                BUNDLE_KEY_POSITION to position
            )
        )
    }

    fun checkInternet() {
        if (!NetworkHelper.isNetworkAvailable(requireContext())) {
            findNavController().navigate(R.id.noInternetFragment)
        }
    }

    open fun onBackPressed(): Boolean {
        return false
    }

    fun share(link: String) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            val shareMessage = link.trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, getString(R.string.title_share)))
        } catch (e: Exception) {
            e.toString();
        }
    }


    fun alertEditText(alert: String?, txtLayout: TextInputLayout) {
        if (!alert.isNullOrEmpty()) {
            txtLayout.isErrorEnabled = true
            txtLayout.error = alert
            txtLayout.endIconMode = TextInputLayout.END_ICON_NONE
        } else {
            txtLayout.isErrorEnabled = false
            txtLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
        }
    }
}