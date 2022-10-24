package com.goldenowl.ecommerceapp.core

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.ui.general.LoadingDialog
import com.goldenowl.ecommerceapp.utilities.BUNDLE_KEY_POSITION
import com.goldenowl.ecommerceapp.utilities.NetworkHelper
import com.goldenowl.ecommerceapp.utilities.URL_IMAGE
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputLayout

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<Binding: ViewBinding>(
    private val inflate: Inflate<Binding>
) : Fragment() {
    protected lateinit var binding: Binding

    open var isHideBottom: Boolean = true

    open var isFullScreen: Boolean = false

    open val color: Int = R.color.white

    open val viewModel: BaseViewModel? get() = null

    private var loadingDialog = LoadingDialog(this)

    open fun setUpViews() {}

    open fun setUpObserve() {}

    open fun setUpAdapter() {}

    open fun setUpArgument(bundle: Bundle) {}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.run {
            setUpArgument(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = inflate(inflater, container, false)
        setFullScreen(color)
        hideBottomNavigation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapter()
        setUpObserve()
        setUpViews()
    }

    private fun changeColorStatusBar(color: Int) {
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), color)
    }

    private fun setFullScreen(color: Int = R.color.white) {
        activity?.let {
            if (isFullScreen) {
                WindowCompat.setDecorFitsSystemWindows(it.window, false)
                changeColorStatusBar(R.color.transparent)
            } else {
                WindowCompat.setDecorFitsSystemWindows(it.window, true)
                changeColorStatusBar(color)
            }
        }
    }

    private fun hideBottomNavigation() {
        activity?.let {
            it.findViewById<BottomNavigationView>(R.id.bottomNavigation)
                ?.let { bottomNavigationView ->
                    bottomNavigationView.visibility = if (isHideBottom) View.GONE else View.VISIBLE
                }
        }
    }

    private var toast : Toast? = null
    fun toastMessage(message: String) {
        if (message.isNotBlank()){
            if (toast != null){
                toast?.cancel()
            }
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            toast?.show()
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