package com.goldenowl.ecommerceapp.ui.favorite

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListFavoriteAdapter
import com.goldenowl.ecommerceapp.core.BaseFragment
import com.goldenowl.ecommerceapp.databinding.FragmentFavoritesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : BaseFragment<FragmentFavoritesBinding>(
    FragmentFavoritesBinding::inflate
) {
    override val viewModel: FavoriteViewModel by activityViewModels()
    private lateinit var adapterFavorite: ListFavoriteAdapter
    override var isHideBottom = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!viewModel.isLogged()) {
            findNavController().navigate(R.id.action_favoritesFragment_to_warningFragment)
        }
    }

    override fun setUpAdapter() {
        adapterFavorite = ListFavoriteAdapter({
            viewModel.removeFavorite(it)
        }, {
            val action = FavoritesFragmentDirections.actionFavoritesFragmentToProductDetailFragment(
                idProduct = it.product.id
            )
            findNavController().navigate(action)
        }, { buttonView, favoriteAndProduct ->
            viewModel.insertBag(
                favoriteAndProduct.product.id,
                favoriteAndProduct.favorite.color,
                favoriteAndProduct.favorite.size,
            )
            buttonView.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.btn_bag_active
            )
        }, { view, favorite ->
            viewModel.setButtonBag(requireContext(), view, favorite)
        })
    }

    override fun setUpViews() {
        binding.apply {
            appBarLayout.topAppBar.title = getString(R.string.favorite)

            recyclerViewProduct.layoutManager = LinearLayoutManager(context)
            recyclerViewProduct.adapter = adapterFavorite

            // Handle Search Bar
            appBarLayout.MaterialToolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.ic_search -> {
                        val searchView = it.actionView as SearchView
                        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                            override fun onQueryTextSubmit(query: String?): Boolean {
                                return true
                            }

                            override fun onQueryTextChange(newText: String?): Boolean {
                                if (!newText.isNullOrEmpty()) {
                                    viewModel.favoriteAndProducts.value?.let { list ->
                                        adapterFavorite.submitList(list.filter { favorite ->
                                            favorite.product.title.lowercase()
                                                .contains(newText.lowercase())
                                        })
                                    }
                                } else {
                                    adapterFavorite.submitList(
                                        viewModel.favoriteAndProducts.value ?: emptyList()
                                    )
                                }
                                return true
                            }
                        })

                        true
                    }
                    else -> false
                }
            }
        }
    }

    override fun setUpObserve() {
        viewModel.apply {
            favoriteAndProducts.observe(viewLifecycleOwner) {
                adapterFavorite.submitList(it)
                adapterFavorite.notifyDataSetChanged()
            }

            isLoading.observe(viewLifecycleOwner) {
                setLoading(it)
            }

            isSuccess.observe(viewLifecycleOwner){
                isLoading.postValue(!it)
            }
        }
    }
}