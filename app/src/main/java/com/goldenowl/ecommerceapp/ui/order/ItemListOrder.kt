package com.goldenowl.ecommerceapp.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListOrderAdapter
import com.goldenowl.ecommerceapp.databinding.ItemViewPagerListOrderBinding
import com.goldenowl.ecommerceapp.core.BaseFragment
import com.goldenowl.ecommerceapp.utilities.ID_ORDER
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemListOrder(private val statusType: Int) : BaseFragment<ItemViewPagerListOrderBinding>(
    ItemViewPagerListOrderBinding::inflate
) {
    override val viewModel: OrderViewModel by viewModels()
    private lateinit var adapter: ListOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.isLoading.postValue(true)
    }

    override fun setUpAdapter() {
        adapter = ListOrderAdapter({
            findNavController().navigate(R.id.orderDetailFragment, bundleOf(ID_ORDER to it))
        }, { order, textView ->
            viewModel.setUIStatus(requireContext(), textView, order.status)
        })
    }

    override fun setUpViews() {
        binding.apply {
            recyclerViewOrder.adapter = adapter
            recyclerViewOrder.layoutManager = LinearLayoutManager(context)
        }
    }

    override fun setUpObserve() {
        viewModel.apply {
            getOrderStatus(statusType).observe(viewLifecycleOwner) { data ->
                adapter.submitList(data)
                viewModel.isLoading.postValue(false)
            }

            isLoading.observe(viewLifecycleOwner){
                setLoadingCustom(binding.progressBar,it)
            }
        }

    }

}