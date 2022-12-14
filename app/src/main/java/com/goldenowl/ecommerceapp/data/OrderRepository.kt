package com.goldenowl.ecommerceapp.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.goldenowl.ecommerceapp.utilities.ORDER_USER
import com.goldenowl.ecommerceapp.utilities.STATUS_ORDER
import com.goldenowl.ecommerceapp.utilities.TIME_CREATE
import com.goldenowl.ecommerceapp.utilities.USER_FIREBASE
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val userManager: UserManager,
    private val db: FirebaseFirestore
) {
    val totalOrder = MutableLiveData(0)

    fun getOrderStatus(status: Int): MutableLiveData<List<Order>> {
        val result = MutableLiveData<List<Order>>()
        db.collection(USER_FIREBASE)
            .document(userManager.getAccessToken())
            .collection(ORDER_USER)
            .whereEqualTo(STATUS_ORDER, status)
            .orderBy(TIME_CREATE, TypeSort.DESCENDING.value)
            .get()
            .addOnSuccessListener { documents ->
                val list = mutableSetOf<Order>()
                for (doc in documents) {
                    list.add(doc.toObject())
                }
                result.postValue(list.toList())
            }
        return result
    }

    fun getSize() {
        if (userManager.isLogged()) {
            db.collection(USER_FIREBASE)
                .document(userManager.getAccessToken())
                .collection(ORDER_USER)
                .get()
                .addOnSuccessListener { documents ->
                    totalOrder.postValue(documents.size())
                }
        }
    }

    fun getOrder(idOrder: String): Flow<Order> {
        val result = MutableLiveData<Order>()
        if (userManager.isLogged() && idOrder.isNotBlank()) {
            db.collection(USER_FIREBASE)
                .document(userManager.getAccessToken())
                .collection(ORDER_USER)
                .document(idOrder)
                .get()
                .addOnSuccessListener { document ->
                    result.postValue(document.toObject())
                }
        }
        return result.asFlow()
    }

    fun setOrderFirebase(order: Order): MutableLiveData<Boolean> {
        val result = MutableLiveData(false)
        db.collection(USER_FIREBASE)
            .document(userManager.getAccessToken())
            .collection(ORDER_USER)
            .document(order.id)
            .set(order)
            .addOnSuccessListener {
                result.postValue(true)
            }
        return result
    }
}