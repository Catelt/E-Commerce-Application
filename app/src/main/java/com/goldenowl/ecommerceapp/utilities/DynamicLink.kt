package com.goldenowl.ecommerceapp.utilities

import android.net.Uri
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase

object DynamicLink {
    private const val BASE_URI_PREFIX = "https://zetashop.page.link"
    fun createDynamicLinkProduct(idProduct: String, handlerLink: (String) -> Unit) {
        Firebase.dynamicLinks.shortLinkAsync {
            link = Uri.parse("$BASE_LINK_PRODUCT${idProduct}")
            domainUriPrefix = BASE_URI_PREFIX
        }.addOnSuccessListener {
            handlerLink(it.shortLink.toString())
        }.addOnFailureListener {
        }
    }
}