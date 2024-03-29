package com.goldenowl.ecommerceapp.ui.home

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ImageHomeAdapter
import com.goldenowl.ecommerceapp.adapters.ListHomeAdapter
import com.goldenowl.ecommerceapp.adapters.ListProductGridAdapter
import com.goldenowl.ecommerceapp.core.BaseFragment
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.databinding.FragmentHomeBinding
import com.goldenowl.ecommerceapp.ui.favorite.BottomSheetFavorite
import com.goldenowl.ecommerceapp.utilities.BUNDLE_KEY_IS_FAVORITE
import com.goldenowl.ecommerceapp.utilities.IS_FIRST
import com.goldenowl.ecommerceapp.utilities.NEW
import com.goldenowl.ecommerceapp.utilities.NetworkHelper
import com.goldenowl.ecommerceapp.utilities.REQUEST_KEY
import com.goldenowl.ecommerceapp.utilities.SALE
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::inflate
) {
    private lateinit var adapter: ListHomeAdapter
    override var isHideBottom = false
    override var isFullScreen = true

    override val viewModel: HomeViewModel by viewModels()
    private var category: List<String> = emptyList()
    private var product: MutableMap<String, List<Product>> = mutableMapOf()
    private val handlerFragment = Handler(Looper.getMainLooper())
    private val listImage = listOf(
        "https://firebasestorage.googleapis.com/v0/b/e-commerce-application-ceb40.appspot.com/o/home%2Fimg_home.png?alt=media&token=7df7534e-5969-4349-a50f-b21d7f32803e",
        "https://firebasestorage.googleapis.com/v0/b/e-commerce-application-ceb40.appspot.com/o/home%2Fimg_home_1.png?alt=media&token=dc010e6e-21da-42b3-a348-8084efd3207c",
        "https://firebasestorage.googleapis.com/v0/b/e-commerce-application-ceb40.appspot.com/o/home%2Fimg_home_2.png?alt=media&token=2d73d133-8c15-4ccb-8fdd-9f242d6447b1",
        "https://firebasestorage.googleapis.com/v0/b/e-commerce-application-ceb40.appspot.com/o/home%2Fimg_home_3.png?alt=media&token=257fbeb4-20da-4a70-a587-c515e4aaec0c",
        "https://firebasestorage.googleapis.com/v0/b/e-commerce-application-ceb40.appspot.com/o/home%2Fimg_home_4.png?alt=media&token=8f1ad2d8-8a7f-4237-99a8-3984269881ec",
    )
    private val listTitle = listOf(
        "Summer clothes",
        "Street clothes",
        "Sleep clothes",
        "Sport clothes",
        "Inform clothes"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Check Tutorial
        val sharedPref = this.activity?.getPreferences(Context.MODE_PRIVATE)
        val isFirst = sharedPref?.getBoolean(IS_FIRST, true);
        if (isFirst == true) {
            sharedPref.edit().putBoolean(IS_FIRST, false).apply()
            findNavController().navigate(R.id.viewPageTutorialFragment)
        }
        viewModel.isLoading.postValue(true)
        setFragmentListener()
    }

    override fun setUpAdapter() {
        adapter = ListHomeAdapter { recyclerView, textView, s ->
            val adapterItem = ListProductGridAdapter({
                val action = HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(
                    idProduct = it.id
                )
                findNavController().navigate(action)
            }, { btnFavorite, product ->
                val bottomSheetSize = BottomSheetFavorite(product, null, null)
                bottomSheetSize.show(parentFragmentManager, BottomSheetFavorite.TAG)
                viewModel.btnFavorite.postValue(btnFavorite)
            }, { view, product ->
                viewModel.setButtonFavorite(requireContext(), view, product.id)
            })
            when (s) {
                SALE -> {
                    adapterItem.submitList(product[SALE])
                }
                NEW -> {
                    adapterItem.submitList(product[NEW])
                }
                else -> {
                    adapterItem.submitList(product[s])
                }
            }

            recyclerView.adapter = adapterItem
            recyclerView.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL, false
            )
            textView.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToCatalogFragment(
                    nameCategories = s,
                    nameProduct = null
                )

                findNavController().navigate(action)
            }
        }
    }

    override fun setUpViews() {
        binding.apply {
            recyclerListHome.adapter = adapter
            recyclerListHome.layoutManager = LinearLayoutManager(context)
            adapter.submitList(product.keys.toList())
            setupScroll()
            if (viewModel.isLoading.value == false) {
                viewModel.isLoading.postValue(false)
            }
            //set viewPager
            viewPagerHome.apply {
                if (NetworkHelper.isNetworkAvailable(requireContext())) {
                    val adapterImage =
                        ImageHomeAdapter(this@HomeFragment, listImage, listTitle)
                    adapter = adapterImage
                    setCurrentItem(1, false)
                    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageScrollStateChanged(state: Int) {
                            super.onPageScrollStateChanged(state)
                            if (state == ViewPager2.SCROLL_STATE_IDLE) {
                                when (currentItem) {
                                    adapterImage.itemCount - 1 -> setCurrentItem(1, false)
                                    0 -> setCurrentItem(adapterImage.itemCount - 2, false)
                                }
                            }
                        }
                    })
                } else {
                    val adapterImage = ImageHomeAdapter(
                        this@HomeFragment, listOf(listImage[0]),
                        listOf(listTitle[0])
                    )
                    adapter = adapterImage
                }

                autoScroll()
                //Auto scroll
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        autoScroll()
                    }
                })
            }
        }
    }

    override fun setUpObserve() {
        viewModel.apply {
            category.observe(viewLifecycleOwner) {
                this@HomeFragment.category = it
                loadMore.postValue(true)
            }

            isLoading.observe(viewLifecycleOwner) {
                if (it) {
                    binding.progressBar.visibility = View.VISIBLE
                    setupScroll()
                } else {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun setupScroll() {
        binding.apply {
            nestedScrollView.viewTreeObserver?.addOnScrollChangedListener {
                nestedScrollView.apply {
                    if (view != null) {
                        val view = getChildAt(0)
                        if (view != null) {
                            val diff = view.bottom - (height + scrollY)
                            if (diff <= 0) {
                                if (viewModel.loadMore.value == true) {
                                    viewModel.isLoading.postValue(true)
                                    viewModel.loadMore.postValue(false)
                                    if (product.size < category.size + 2) {
                                        viewModel.apply {
                                            if (product.isEmpty()) {
                                                getSaleProduct().observe(viewLifecycleOwner) {
                                                    if (it.isNotEmpty()) {
                                                        product[SALE] = it
                                                        adapter.submitList(product.keys.toList())
                                                        viewModel.isLoading.postValue(false)
                                                        viewModel.loadMore.postValue(true)
                                                    }
                                                }
                                            } else if (product.size == 1 && checkSale.value == false) {
                                                getNewProduct().observe(viewLifecycleOwner) { list ->
                                                    if (list.isNotEmpty()) {
                                                        product[NEW] = list
                                                        adapter.submitList(product.keys.toList())
                                                        viewModel.isLoading.postValue(false)
                                                        viewModel.loadMore.postValue(true)
                                                    }
                                                }
                                            } else {
                                                val index = product.size - 2
                                                if (index >= 0) {
                                                    getProductWithCategory(this@HomeFragment.category[index])
                                                        .observe(viewLifecycleOwner) {
                                                            if (it.isNotEmpty()) {
                                                                product[this@HomeFragment.category[index]] =
                                                                    it
                                                                adapter.submitList(product.keys.toList())
                                                                viewModel.isLoading.postValue(false)
                                                                viewModel.loadMore.postValue(true)
                                                            }
                                                        }
                                                }
                                            }
                                        }
                                    } else {
                                        viewModel.isLoading.postValue(false)
                                        viewModel.loadMore.postValue(false)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setFragmentListener() {
        setFragmentResultListener(REQUEST_KEY) { _, bundle ->
            val result = bundle.getBoolean(BUNDLE_KEY_IS_FAVORITE, false)
            if (result) {
                viewModel.btnFavorite.value?.let {
                    it.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.btn_favorite_active
                    )
                }
            }
        }
    }


    private fun autoScroll() {
        handlerFragment.removeMessages(0)
        handlerFragment.postDelayed({
            binding.viewPagerHome.setCurrentItem(binding.viewPagerHome.currentItem + 1, true)
        }, 5000)
    }

    override fun onPause() {
        handlerFragment.removeMessages(0)
        super.onPause()
    }

    override fun onResume() {
        autoScroll()
        super.onResume()
    }

    override fun onDestroy() {
        handlerFragment.removeMessages(0)
        super.onDestroy()
    }
}