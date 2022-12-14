package com.goldenowl.ecommerceapp.ui.home

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.goldenowl.ecommerceapp.data.*
import com.goldenowl.ecommerceapp.core.BaseViewModel
import com.goldenowl.ecommerceapp.utilities.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val favoriteRepository: FavoriteRepository,
    private val bagRepository: BagRepository,
    private val db: FirebaseFirestore,
    val userManager: UserManager,
) : BaseViewModel() {
    val category = productRepository.getAllCategory().asLiveData()
    val btnFavorite = MutableLiveData<View>()
    val loadMore = MutableLiveData(true)
    val checkSale = MutableLiveData(false)

    init {
        favoriteRepository.fetchFavoriteAndProduct()
        bagRepository.fetchBagAndProduct()
    }

    fun getProductWithCategory(category: String): MutableLiveData<List<Product>> {
        val result: MutableLiveData<List<Product>> = MutableLiveData(emptyList())
        db.collection(PRODUCT_FIREBASE)
            .whereEqualTo(CATEGORY_NAME, category)
            .get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<Product>()
                for (document in documents) {
                    list.add(document.toObject())
                }
                result.postValue(list)
            }
        return result
    }

    fun getNewProduct(): MutableLiveData<List<Product>> {
        val result: MutableLiveData<List<Product>> = MutableLiveData(emptyList())
        db.collection(PRODUCT_FIREBASE)
            .orderBy(CREATED_DATE, Query.Direction.DESCENDING)
            .limit(LIMIT.toLong())
            .get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<Product>()
                for (document in documents) {
                    list.add(document.toObject())
                }
                result.postValue(list)
            }
        return result
    }

    fun getSaleProduct(): MutableLiveData<List<Product>> {
        val result: MutableLiveData<List<Product>> = MutableLiveData(emptyList())
        db.collection(PRODUCT_FIREBASE)
            .whereNotEqualTo(SALE_PERCENT, null)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.size() == 0) {
                    checkSale.postValue(true)
                } else {
                    val list = mutableListOf<Product>()
                    for (document in documents) {
                        list.add(document.toObject())
                    }
                    result.postValue(list)
                }
            }
        return result
    }

    fun setButtonFavorite(context: Context, buttonView: View, idProduct: String) {
        favoriteRepository.setButtonFavorite(context, buttonView, idProduct)
    }
}