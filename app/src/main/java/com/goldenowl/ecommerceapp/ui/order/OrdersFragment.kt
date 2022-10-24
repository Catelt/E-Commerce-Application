package com.goldenowl.ecommerceapp.ui.order

import androidx.navigation.fragment.findNavController
import com.goldenowl.ecommerceapp.adapters.StatusPagerAdapter
import com.goldenowl.ecommerceapp.core.BaseFragment
import com.goldenowl.ecommerceapp.databinding.FragmentOrdersBinding
import com.goldenowl.ecommerceapp.utilities.statuses
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrdersFragment : BaseFragment<FragmentOrdersBinding>(
    FragmentOrdersBinding::inflate
) {
    override var isHideBottom = false

    override fun setUpViews() {
        binding.apply {
            viewPager.adapter = StatusPagerAdapter(this@OrdersFragment)
            TabLayoutMediator(viewPagerTabs, viewPager) { tab, position ->
                tab.text = statuses[position]
            }.attach()

            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}