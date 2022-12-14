package com.goldenowl.ecommerceapp.ui.favorite

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.goldenowl.ecommerceapp.core.BaseViewModel
import com.goldenowl.ecommerceapp.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val bagRepository: BagRepository,
    private val userManager: UserManager,
) :
    BaseViewModel() {
    val favoriteAndProducts = favoriteRepository.favoriteAndProduct
    val isSuccess = favoriteRepository.isSuccess
    init {
        bagRepository.getBags()
    }

    fun insertBag(idProduct: String, color: String, size: String) {
        bagRepository.insertBag(idProduct, color, size)
    }


    fun removeFavorite(favorite: FavoriteAndProduct) {
        favoriteRepository.removeFavoriteFirebase(favorite)
    }

    fun insertFavorite(idProduct: String, size: String, color: String): MutableLiveData<Boolean> {
        return favoriteRepository.insertFavorite(idProduct, color, size)
    }


    fun setButtonBag(context: Context, buttonView: View, favorite: Favorite) {
        bagRepository.setButtonBag(context, buttonView, favorite)
    }

    fun isLogged(): Boolean {
        return userManager.isLogged()
    }
}
