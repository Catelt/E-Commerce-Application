package com.goldenowl.ecommerceapp.utilities

import android.net.Uri
import android.widget.Toast
import com.facebook.FacebookSdk.getApplicationContext
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase

object DynamicLink {
    private const val BASE_URI_PREFIX = "https://zetashop.page.link"
    fun createDynamicLinkProduct(idProduct: String, handlerLink: (String) -> Unit) {
        Firebase.dynamicLinks.shortLinkAsync {
            link = Uri.parse("$BASE_LINK_PRODUCT${idProduct}")
            domainUriPrefix = BASE_URI_PREFIX
            androidParameters(getApplicationContext().packageName) {}
        }.addOnSuccessListener {
            handlerLink(it.shortLink.toString())
        }.addOnFailureListener {
            Toast.makeText(getApplicationContext(), WARNING_SOMETHING_WRONG, Toast.LENGTH_SHORT)
                .show()
        }
    }
}