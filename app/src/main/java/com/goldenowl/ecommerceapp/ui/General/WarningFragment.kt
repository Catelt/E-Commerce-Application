package com.goldenowl.ecommerceapp.ui.General

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.FragmentWarningBinding
import com.goldenowl.ecommerceapp.ui.Auth.AuthActivity

class WarningFragment : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentWarningBinding.inflate(inflater,container,false)
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

        return binding.root
    }
}