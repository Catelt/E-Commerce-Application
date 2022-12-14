package com.goldenowl.ecommerceapp.ui.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.goldenowl.ecommerceapp.core.BaseViewModel
import com.goldenowl.ecommerceapp.data.*
import com.goldenowl.ecommerceapp.utilities.MAX
import com.goldenowl.ecommerceapp.utilities.MIN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val shippingAddressRepository: ShippingAddressRepository,
    private val paymentRepository: PaymentRepository,
    private val orderRepository: OrderRepository,
    private val bagRepository: BagRepository,
    private val promotionRepository: PromotionRepository,
    private val userManager: UserManager,
) : BaseViewModel() {
    private val statusIdPayment = MutableStateFlow("")
    val bag = bagRepository.bagAndProduct
    val shippingAddress = shippingAddressRepository.address
    val promotion = promotionRepository.promotion

    init {
        bagRepository.fetchBagAndProduct()
    }

    val payment = statusIdPayment.flatMapLatest {
        getPayment(it).asFlow()
    }.asLiveData()


    private fun getPayment(idPayment: String): LiveData<Card> {
        val result = paymentRepository.card
        if (idPayment.isNotBlank()) {
            paymentRepository.getCard(idPayment)
        } else {
            paymentRepository.card.postValue(Card())
        }
        return result
    }

    fun getPromotion(idPromotion: String): LiveData<Promotion> {
        val result = promotionRepository.promotion
        if (idPromotion.isNotBlank()) {
            promotionRepository.getPromotion(idPromotion)
        }
        return result
    }

    fun setPromotionDefault() {
        promotionRepository.promotion.postValue(Promotion())
    }

    fun setOrderFirebase(order: Order): MutableLiveData<Boolean> {
        isLoading.postValue(true)
        return orderRepository.setOrderFirebase(order)
    }

    fun checkOrder(
        bags: List<BagAndProduct>,
        address: ShippingAddress?,
        card: Card?,
        total: Int,
        delivery: Delivery?,
        promotion: Promotion?
    ): MutableLiveData<Order> {
        if (address == null || address.fullName.isBlank()) {
            toastMessage.postValue(ALERT_ADDRESS)
            return MutableLiveData()
        }
        if (card == null || card.id.isBlank()) {
            toastMessage.postValue(ALERT_PAYMENT)
            return MutableLiveData()
        }
        if (delivery == null) {
            toastMessage.postValue(ALERT_DELIVERY)
            return MutableLiveData()
        }
        isLoading.postValue(true)
        val listBag = getBag(bags)
        val numberCard = getNumberCard(card)
        var sale = 0
        promotion?.let {
            sale = it.salePercent.toInt()
        }
        val order = createOrder(
            listBag,
            total.toFloat(),
            getAddress(address),
            numberCard.first,
            numberCard.second,
            delivery,
            sale.toString(),
        )
        return MutableLiveData(order)
    }

    private fun createOrder(
        product: List<ProductOrder>,
        total: Float,
        shippingAddress: String,
        payment: String,
        typePayment: Int,
        delivery: Delivery,
        promotion: String,
    ) = Order(
        id = (MIN..MAX).random().toString(),
        products = product,
        total = total,
        status = 1,
        shippingAddress = shippingAddress,
        payment = payment,
        isTypePayment = typePayment,
        delivery = delivery,
        promotion = promotion,
    )

    private fun getBag(bagAndProducts: List<BagAndProduct>): MutableList<ProductOrder> {
        val list: MutableList<ProductOrder> = mutableListOf()
        for (bagAndProduct in bagAndProducts) {
            bagAndProduct.apply {
                val size = product.getColorAndSize(
                    bag.color,
                    bag.size
                )
                var price: Long = 0
                size?.let {
                    var salePercent = 0
                    if (product.salePercent != null) {
                        salePercent = product.salePercent
                    }
                    price = size.price * (100 - salePercent) / 100
                }

                list.add(
                    ProductOrder(
                        idProduct = product.id,
                        image = product.images[0],
                        title = product.title,
                        brandName = product.brandName,
                        size = bag.size,
                        color = bag.color,
                        units = bag.quantity.toInt(),
                        price = price.toFloat(),
                    )
                )
            }
        }
        return list
    }

    private fun getAddress(shippingAddress: ShippingAddress): String {
        shippingAddress.apply {
            return "$address\n$city, $state $zipCode, $country"
        }
    }

    private fun getNumberCard(card: Card): Pair<String, Int> {
        val type = if (card.number[0] == '4') {
            0
        } else {
            1
        }
        val number = card.number.substring(card.number.length - 4)
        return Pair(number, type)
    }

    fun removeAllBag() {
        bagRepository.removeAllFirebase()
    }

    fun setIdAddressDefault() {
        if (userManager.getAddress().isNotBlank()) {
            shippingAddressRepository.getAddress(userManager.getAddress())
        } else {
            shippingAddressRepository.address.postValue(ShippingAddress())
        }
    }

    fun setIdPaymentDefault() {
        statusIdPayment.value = userManager.getPayment()
    }

    fun calculatorTotalOrder(list: List<BagAndProduct>, salePercent: Long = 0): Int {
        return bagRepository.calculatorTotal(list, salePercent)
    }

    companion object {
        const val ALERT_DELIVERY = "Please choose one delivery"
        const val ALERT_ADDRESS = "Please choose one address"
        const val ALERT_PAYMENT = "Please choose one payment"
    }
}