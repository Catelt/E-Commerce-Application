package com.goldenowl.ecommerceapp.ui.general

import android.content.Intent
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.core.BaseFragment
import com.goldenowl.ecommerceapp.databinding.FragmentWarningBinding
import com.goldenowl.ecommerceapp.ui.auth.AuthActivity

class WarningFragment : BaseFragment<FragmentWarningBinding>(
    FragmentWarningBinding::inflate
){
    override var isHideBottom = false

    override fun setUpViews() {
        val ss = SpannableString(getString(R.string.please_login_to_use_this_function))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                startActivity(Intent(activity, AuthActivity::class.java))
                activity?.finish()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }

        ss.setSpan(clickableSpan, 7, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.txtAlert.apply {
            text = ss
            movementMethod = LinkMovementMethod.getInstance();
            highlightColor = Color.TRANSPARENT
        }
    }
}