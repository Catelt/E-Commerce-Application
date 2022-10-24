package com.goldenowl.ecommerceapp.ui.Profile

import android.widget.TextView
import androidx.fragment.app.Fragment
import com.facebook.login.LoginManager
import com.goldenowl.ecommerceapp.data.*
import com.goldenowl.ecommerceapp.ui.BaseViewModel
import com.goldenowl.ecommerceapp.utilities.GlideDefault
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hdodenhof.circleimageview.CircleImageView
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val shippingAddressRepository: ShippingAddressRepository,
    private val paymentRepository: PaymentRepository,
    private val reviewRepository: ReviewRepository,
    val userManager: UserManager,
    private val firebaseAuth: FirebaseAuth,
    private val googleSignInClient: GoogleSignInClient
) : BaseViewModel() {
    val address = shippingAddressRepository.listAddress
    val payment = paymentRepository.card
    val totalOrder = orderRepository.totalOrder
    val reviews = reviewRepository.reviews
    fun setupProfileUI(
        fragment: Fragment,
        name: TextView,
        email: TextView,
        avatar: CircleImageView
    ) {
        if (userManager.isLogged()) {
            name.text = userManager.getName()
            email.text = userManager.getEmail()
            GlideDefault.userImage(
                fragment.requireContext(),
                userManager.getAvatar(),
                avatar
            )
        }
    }

    fun getAddress(){
        shippingAddressRepository.fetchAddress()
    }

    fun getReviews(){
        reviewRepository.getAllReviewOfUser()
    }

    fun getTotalOrder(){
        orderRepository.getSize()
    }

    fun getPayment() {
        if (userManager.getPayment().isNotBlank()) {
            paymentRepository.getCard(userManager.getPayment())
        }
        else{
            paymentRepository.card.postValue(Card())
        }
    }

    fun logOut() {
        firebaseAuth.signOut()
        userManager.logOut()
        //Google SignOut
        googleSignInClient.signOut()
        //Facebook SignOut
        LoginManager.getInstance().logOut();
    }

    fun getAvatar(): String {
        return userManager.getAvatar()
    }
}