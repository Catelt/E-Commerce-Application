package com.goldenowl.ecommerceapp.ui.qrscan

import androidx.lifecycle.MutableLiveData
import com.goldenowl.ecommerceapp.core.BaseViewModel
import com.goldenowl.ecommerceapp.utilities.NULL
import com.goldenowl.ecommerceapp.utilities.PRODUCT_FIREBASE
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QrScanViewModel @Inject constructor(
    private val db: FirebaseFirestore
) : BaseViewModel() {
    val statusCheckProduct = MutableLiveData("")

    fun checkProduct(idProduct: String) {
        db.collection(PRODUCT_FIREBASE).document(idProduct).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        statusCheckProduct.postValue(idProduct)

                    } else {
                        statusCheckProduct.postValue(NULL)
                    }
                }
            }
    }
}