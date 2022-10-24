package com.goldenowl.ecommerceapp.ui.shop

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListCategoriesAdapter2
import com.goldenowl.ecommerceapp.core.BaseFragment
import com.goldenowl.ecommerceapp.databinding.FragmentShopBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ShopFragment : BaseFragment<FragmentShopBinding>(
    FragmentShopBinding::inflate
) {
    override val viewModel: ShopViewModel by viewModels()
    override var isHideBottom = false
    private lateinit var adapterCategory: ListCategoriesAdapter2


    override fun setUpAdapter() {
        adapterCategory = ListCategoriesAdapter2 { str ->
            val action = ShopFragmentDirections.actionShopFragmentToCatalogFragment(
                nameCategories = str,
                nameProduct = null
            )
            findNavController().navigate(action)
        }
        checkInternet()
    }

    override fun setUpObserve() {
        viewModel.apply {
            allCategory.observe(viewLifecycleOwner) {
                adapterCategory.submitList(it)
            }
        }
    }

    override fun setUpViews() {
        binding.apply {
            MaterialToolbar.title = getString(R.string.categories)
            recyclerViewCategories.layoutManager = LinearLayoutManager(context)
            recyclerViewCategories.adapter = adapterCategory

            btnViewAllItems.setOnClickListener {
                val action = ShopFragmentDirections.actionShopFragmentToCatalogFragment(
                    nameCategories = "",
                    nameProduct = null
                )
                findNavController().navigate(action)
            }

            // Handle Search Bar
            MaterialToolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.ic_search -> {
                        findNavController().navigate(R.id.searchFragment)
                        true
                    }
                    else -> false
                }
            }
        }
    }
}