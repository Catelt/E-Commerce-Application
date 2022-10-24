package com.goldenowl.ecommerceapp.ui.profile

import android.content.Intent
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.core.BaseFragment
import com.goldenowl.ecommerceapp.databinding.FragmentProfileLoginBinding
import com.goldenowl.ecommerceapp.ui.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileLoginFragment : BaseFragment<FragmentProfileLoginBinding>(
    FragmentProfileLoginBinding::inflate
) {
    override val viewModel: ProfileViewModel by viewModels()
    override var isHideBottom = false

    private var totalAddress = 0
    private var paymentTxt = ""
    private var totalOrder = 0
    private var totalReview = 0

    override fun setUpViews() {
        binding.apply {
            appBarLayout.topAppBar.title = getString(R.string.my_profile)
            setDefault()
            imgAvatar.setOnClickListener {
                touchImage(listOf(viewModel.getAvatar()))
            }
            btnLogout.setOnClickListener {
                viewModel.logOut()
                startActivity(Intent(activity, AuthActivity::class.java))
                activity?.finish()
            }

            myOrderLayout.setOnClickListener {
                findNavController().navigate(R.id.ordersFragment)
            }
            shippingLayout.setOnClickListener {
                findNavController().navigate(R.id.shippingAddressFragment)
            }
            paymentLayout.setOnClickListener {
                findNavController().navigate(R.id.paymentMethodFragment)
            }
            promocodesLayout.setOnClickListener {
                findNavController().navigate(R.id.promoListFragment)
            }
            myReviewLayout.setOnClickListener {
                findNavController().navigate(R.id.reviewListFragment)
            }
            settingLayout.setOnClickListener {
                findNavController().navigate(R.id.settingFragment)
            }
        }
        viewModel.isLoading.postValue(false)
    }

    override fun setUpObserve() {
        viewModel.apply {
            isLoading.postValue(true)
            address.observe(viewLifecycleOwner) {
                setDefault()
            }
            payment.observe(viewLifecycleOwner) {
                setDefault()
            }

            totalOrder.observe(viewLifecycleOwner) {
                setDefault()
            }
            isLoading.observe(viewLifecycleOwner) {
                setLoading(it)
            }
        }
    }

    private fun setDefault() {
        viewModel.apply {
            this@ProfileLoginFragment.totalAddress = address.value?.size ?: 0
            setSubTitleCard()
            this@ProfileLoginFragment.totalOrder = totalOrder.value ?: 0
            this@ProfileLoginFragment.totalReview = reviews.value?.size ?: 0
        }
        binding.apply {
            txtSubTitleShipping.text = "$totalAddress addresses"
            txtSubTitlePayment.text = paymentTxt
            txtSubTitleOrder.text = "Already have $totalOrder orders"
            txtSubTitleReview.text = "Reviews for $totalReview items"
        }
    }

    private fun freshData() {
        viewModel.apply {
            getAddress()
            getPayment()
            getTotalOrder()
            getReviews()
        }
    }

    private fun setSubTitleCard() {
        var str = ""
        viewModel.payment.value?.let {
            if (it.id.isNotBlank()) {
                str = if (it.number[0] == '4') {
                    "Visa  **${it.number.substring(it.number.length - 2)}"
                } else {
                    "Mastercard  **${it.number.substring(it.number.length - 2)}"
                }
            }
        }
        this@ProfileLoginFragment.paymentTxt = str
    }

    override fun onResume() {
        super.onResume()
        freshData()
        binding.apply {
            viewModel.setupProfileUI(this@ProfileLoginFragment, txtName, txtEmail, imgAvatar)
        }
    }
}