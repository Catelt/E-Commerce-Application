package com.goldenowl.ecommerceapp.ui.order

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListProductOrderAdapter
import com.goldenowl.ecommerceapp.core.BaseFragment
import com.goldenowl.ecommerceapp.data.Order
import com.goldenowl.ecommerceapp.data.ProductOrder
import com.goldenowl.ecommerceapp.databinding.FragmentOrderDetailBinding
import com.goldenowl.ecommerceapp.ui.general.LoadingDialog
import com.goldenowl.ecommerceapp.utilities.DateFormat
import com.goldenowl.ecommerceapp.utilities.ID_ORDER
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailFragment : BaseFragment<FragmentOrderDetailBinding>(
    FragmentOrderDetailBinding::inflate
) {
    override val viewModel: OrderViewModel by viewModels()
    private val adapter = ListProductOrderAdapter()
    private val loadingDialog = LoadingDialog(this)
    private lateinit var listProductOrder: List<ProductOrder>

    override fun setUpArgument(bundle: Bundle) {
        arguments?.let {
            val idOrder = it.getString(ID_ORDER) ?: ""
            if(idOrder.isNotBlank()){
                viewModel.setIdOrder(idOrder)
                viewModel.isLoading.postValue(true)
            }
        }
    }

    override fun setUpViews() {
        binding.apply {
            appBarLayout.MaterialToolbar.title = getString(R.string.order_details)
            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            btnReOrder.setOnClickListener {
                loadingDialog.startLoading()
                viewModel.reOrder(listProductOrder)
            }
        }
    }

    override fun setUpObserve() {
        viewModel.apply {
            order.observe(viewLifecycleOwner) {
                if(it != null && it.id.isNotBlank()){
                    listProductOrder = it.products
                    setupUI(it)
                    viewModel.isLoading.postValue(false)
                }
            }
            dismiss.observe(viewLifecycleOwner) {
                if (it) {
                    loadingDialog.dismiss()
                    findNavController().navigateUp()
                }
            }
            viewModel.isLoading.observe(viewLifecycleOwner){
                setLoading(it)
            }
        }
    }

    private fun setupUI(order: Order) {
        binding.apply {
            order.apply {
                txtIdOrder.text = "Order №$id"
                txtTrackingNumber.text = trackingNumber
                timeCreated.let {
                    txtTimeCreated.text = DateFormat.default.format(it).toString()
                }

                viewModel.setUIStatus(requireContext(), txtStatus, status)
                txtNumberProduct.text = "${products.size} items"
                adapter.submitList(products)
                recyclerViewProduct.adapter = adapter
                recyclerViewProduct.layoutManager = LinearLayoutManager(context)

                txtShippingAddress.text = shippingAddress

                txtNumberCard.text = "**** **** **** $payment"
                if (isTypePayment == 0) {
                    imgLogoCard.setImageResource(R.drawable.ic_mastercard)
                } else {
                    imgLogoCard.setImageResource(R.drawable.ic_visa2)
                }

                txtDeliveryMethod.text = "${delivery?.name}, 3 days, ${delivery?.price}\$"

                viewModel.setUIStatus(requireContext(), txtStatus, status)
                if (promotion == "0") {
                    txtDiscountMethod.text = ""
                } else {
                    txtDiscountMethod.text = "$promotion%, Personal promo code"
                }
                txtTotalAmount.text = "$total\$"
            }
        }
    }
}