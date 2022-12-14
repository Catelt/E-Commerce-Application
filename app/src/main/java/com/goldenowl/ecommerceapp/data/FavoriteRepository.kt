package com.goldenowl.ecommerceapp.data

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.utilities.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FavoriteRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val userManager: UserManager,
) : BaseRepository() {
    val favoriteAndProduct = MutableLiveData<MutableList<FavoriteAndProduct>>()
    private val listIdProductFavorite = MutableLiveData<List<String>>()

    fun fetchFavoriteAndProduct() {
        if (userManager.isLogged()) {
            isSuccess.postValue(false)
            db.collection(USER_FIREBASE)
                .document(userManager.getAccessToken())
                .collection(FAVORITE_FIREBASE)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.size() == 0) {
                        favoriteAndProduct.postValue(mutableListOf())
                        isSuccess.postValue(true)
                    } else {
                        val list = mutableListOf<FavoriteAndProduct>()
                        for (document in documents) {
                            val favorite = document.toObject<Favorite>()
                            db.collection(PRODUCT_FIREBASE).document(favorite.idProduct).get()
                                .addOnSuccessListener { document2 ->
                                    document2.toObject<Product>()?.let {
                                        list.add(FavoriteAndProduct(favorite, it))
                                    }
                                    favoriteAndProduct.postValue(list)
                                    isSuccess.postValue(true)
                                }
                        }
                    }
                }
        }
        getListIdProductFavorite()
    }


    fun insertFavorite(idProduct: String, color: String, size: String): MutableLiveData<Boolean> {
        val isFinish = MutableLiveData(false)
        val favorite = Favorite(
            id = Date().time.toString(),
            idProduct = idProduct,
            color = color,
            size = size,
        )
        if (userManager.isLogged()) {
            db.collection(USER_FIREBASE)
                .document(userManager.getAccessToken())
                .collection(FAVORITE_FIREBASE)
                .whereEqualTo(SIZE, favorite.size)
                .whereEqualTo(COLOR, favorite.color)
                .whereEqualTo(ID_PRODUCT, favorite.idProduct)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.size() == 0) {
                        db.collection(USER_FIREBASE)
                            .document(userManager.getAccessToken())
                            .collection(FAVORITE_FIREBASE)
                            .document(favorite.id)
                            .set(favorite)
                            .addOnSuccessListener {
                                insertFavoriteLocal(favorite)
                                getListIdProductFavorite()
                                isFinish.postValue(true)
                            }
                    } else {
                        isFinish.postValue(true)
                    }
                }
        }
        return isFinish
    }

    fun insertFavoriteLocal(favorite: Favorite) {
        favoriteAndProduct.value?.let {
            db.collection(PRODUCT_FIREBASE).document(favorite.idProduct).get()
                .addOnSuccessListener { document2 ->
                    document2.toObject<Product>()?.let { product ->
                        it.add(FavoriteAndProduct(favorite, product))
                        favoriteAndProduct.postValue(it)
                    }
                }
        }
    }

    fun removeFavoriteFirebase(favorite: FavoriteAndProduct) {
        if (userManager.isLogged()) {
            favoriteAndProduct.value?.let {
                it.remove(favorite)
                favoriteAndProduct.postValue(it)
            }
            db.collection(USER_FIREBASE)
                .document(userManager.getAccessToken())
                .collection(FAVORITE_FIREBASE)
                .document(favorite.favorite.id).delete()
                .addOnSuccessListener {
                    getListIdProductFavorite()
                }
        }
    }

    private fun getListIdProductFavorite() {
        if (userManager.isLogged()) {
            db.collection(USER_FIREBASE)
                .document(userManager.getAccessToken())
                .collection(FAVORITE_FIREBASE)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.size() > 0) {
                        val list = mutableSetOf<String>()
                        for (document in documents) {
                            val favorite = document.toObject<Favorite>()
                            list.add(favorite.idProduct)
                        }
                        listIdProductFavorite.postValue(list.toList())
                    } else {
                        listIdProductFavorite.postValue(emptyList())
                    }
                }
        }
    }

    fun setButtonFavorite(context: Context, buttonView: View, idProduct: String) {
        if (!userManager.isLogged()) {
            buttonView.visibility = View.GONE
        } else {
            buttonView.visibility = View.VISIBLE
            listIdProductFavorite.value?.let {
                if (it.contains(idProduct)) {
                    buttonView.background = ContextCompat.getDrawable(
                        context,
                        R.drawable.btn_favorite_active
                    )
                } else {
                    buttonView.background = ContextCompat.getDrawable(
                        context,
                        R.drawable.btn_favorite_no_active
                    )
                }
            }
        }
    }
}