package com.goldenowl.ecommerceapp.core

import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.goldenowl.ecommerceapp.ui.general.LoadingDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class BaseBottomSheetDialog : BottomSheetDialogFragment() {
    private val loadingDialog = LoadingDialog(this)

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

    fun setLoadingButton(progress: ProgressBar, btn: Button, isLoading: Boolean){
        if (isLoading) {
            progress.visibility = View.VISIBLE
            btn.visibility = View.GONE
        } else {
            progress.visibility = View.GONE
            btn.visibility = View.VISIBLE
        }
    }

    companion object {
        const val GRIDVIEW_SPAN_COUNT = 3
    }
}