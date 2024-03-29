package com.goldenowl.ecommerceapp.ui.shop

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ImageProductAdapter
import com.goldenowl.ecommerceapp.adapters.ListProductGridAdapter
import com.goldenowl.ecommerceapp.adapters.SpinnerAdapter
import com.goldenowl.ecommerceapp.core.BaseFragment
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.databinding.FragmentProductDetailBinding
import com.goldenowl.ecommerceapp.ui.bag.BottomSheetCart
import com.goldenowl.ecommerceapp.ui.favorite.BottomSheetFavorite
import com.goldenowl.ecommerceapp.utilities.BUNDLE_KEY_IS_FAVORITE
import com.goldenowl.ecommerceapp.utilities.ID_PRODUCT
import com.goldenowl.ecommerceapp.utilities.NetworkHelper
import com.goldenowl.ecommerceapp.utilities.REQUEST_KEY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailFragment : BaseFragment<FragmentProductDetailBinding>(
    FragmentProductDetailBinding::inflate
) {
    override val viewModel: ShopViewModel by viewModels()

    private lateinit var adapterRelated: ListProductGridAdapter
    private lateinit var colors: MutableList<String>
    private lateinit var sizes: MutableList<String>
    private var selectSize: Int = 0
    private var selectColor: Int = 0
    private var idProduct = ""
    private val handlerFragment = Handler(Looper.getMainLooper())

    override fun setUpArgument(bundle: Bundle) {
        arguments?.let {
            idProduct = it.getString(ID_PRODUCT) ?: ""
            if (idProduct.isNotBlank()) {
                viewModel.isLoading.postValue(true)
                viewModel.setProduct(idProduct)
            } else {
                findNavController().navigateUp()
            }
        }
        checkInternet()
    }

    override fun setUpAdapter() {
        adapterRelated = ListProductGridAdapter({
            if (it.id != idProduct) {
                viewModel.isLoading.postValue(true)
                viewModel.setProduct(it.id)
                idProduct = it.id
                binding.nestedScrollView.apply {
                    scrollTo(0, 0)
                }
            }
        }, { btnFavorite, product ->
            val bottomSheetSize = BottomSheetFavorite(product, null, null)
            viewModel.btnFavorite.postValue(btnFavorite)
            bottomSheetSize.show(parentFragmentManager, BottomSheetFavorite.TAG)
        }, { view, product ->
            viewModel.setButtonFavorite(requireContext(), view, product.id)
        })
    }

    override fun setUpObserve() {
        viewModel.apply {
            toastMessage.observe(viewLifecycleOwner) { str ->
                toastMessage(str)
                toastMessage.postValue("")
            }

            product.observe(viewLifecycleOwner) {
                if (it != null) {
                    colors = viewModel.getAllColor()
                    sizes = viewModel.getAllSize()
                    selectSize = 0
                    selectColor = 0
                    viewModel.setCategory(it.categoryName)
                    bind(it)
                }
            }

            products.observe(viewLifecycleOwner) {

            }

            isLoading.observe(viewLifecycleOwner) {
                setLoading(it)
            }
        }
    }

    override fun setUpViews() {
        setFragmentListener()
    }

    fun changePrice() {
        val product = viewModel.product.value
        product?.let {
            binding.txtPrice.text = "\$${it.colors[selectColor].sizes[selectSize].price}"
        }
    }

    fun bind(product: Product) {
        binding.apply {
            MaterialToolbar.title = product.title

            MaterialToolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            MaterialToolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.btnShare -> {
                        false
                    }
                    else -> false
                }
            }
            //Set Detail
            viewPagerImageProduct.apply {
                if (NetworkHelper.isNetworkAvailable(requireContext())) {
                    val adapterImage =
                        ImageProductAdapter(this@ProductDetailFragment, product.images) {
                            touchImage(
                                product.images,
                                it
                            )
                        }
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
                    val images = listOf(product.images[0])
                    val adapterImage = ImageProductAdapter(this@ProductDetailFragment, images) {
                        touchImage(
                            product.images,
                            it
                        )
                    }
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

            txtBrandName.text = product.brandName
            txtTitle.text = product.title
            txtDescription.text = product.description
            ratingBar.rating = product.reviewStars
            txtNumberVote.text = "(${product.numberReviews})"
            txtPrice.text = "\$${product.colors[0].sizes[0].price}"


            //Spinner Size
            val adapterSize = SpinnerAdapter(requireContext(), sizes)
            spinnerSize.adapter = adapterSize
            spinnerSize.setSelection(0)
            spinnerSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectSize = position
                    changePrice()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }

            //Spinner Color
            val adapterColor = SpinnerAdapter(requireContext(), colors)
            spinnerColor.adapter = adapterColor
            spinnerColor.setSelection(0)
            spinnerColor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectColor = position
                    changePrice()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }

            recyclerViewProduct.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL, false
            )
            recyclerViewProduct.adapter = adapterRelated


            //Set Button Favorite
            viewModel.setButtonFavorite(requireContext(), btnFavorite, product.id)

            btnFavorite.setOnClickListener {
                viewModel.btnFavorite.postValue(btnFavorite)
                val bottomSheetSize =
                    BottomSheetFavorite(product, selectSize, product.colors[selectColor].color)
                bottomSheetSize.show(parentFragmentManager, BottomSheetFavorite.TAG)
            }
            btnAddToCart.isEnabled = viewModel.userManager.isLogged()
            btnAddToCart.setOnClickListener {
                val bottomSheetCart = BottomSheetCart(product, selectSize, selectColor)
                bottomSheetCart.show(parentFragmentManager, BottomSheetCart.TAG)
            }

            btnRatingBar.setOnClickListener {
                val action =
                    ProductDetailFragmentDirections.actionProductDetailFragmentToRatingProductFragment(
                        idProduct = idProduct
                    )
                findNavController().navigate(action)
            }
            viewModel.relatedProduct(product.categoryName).observe(viewLifecycleOwner) {
                adapterRelated.submitList(it.filter { product -> product.id != idProduct })
                binding.txtNumberRelated.text = "${it.size - 1} items"
            }
            viewModel.isLoading.postValue(false)
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
            binding.viewPagerImageProduct.setCurrentItem(
                binding.viewPagerImageProduct.currentItem + 1,
                true
            )
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