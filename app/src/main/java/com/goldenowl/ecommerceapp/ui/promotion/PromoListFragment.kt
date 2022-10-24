package com.goldenowl.ecommerceapp.ui.promotion

import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListPromotionAdapter
import com.goldenowl.ecommerceapp.core.BaseFragment
import com.goldenowl.ecommerceapp.data.TypeSort
import com.goldenowl.ecommerceapp.databinding.FragmentPromoListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PromoListFragment : BaseFragment<FragmentPromoListBinding>(
    FragmentPromoListBinding::inflate
) {
    override val viewModel: PromotionViewModel by viewModels()
    private lateinit var adapter: ListPromotionAdapter
    private val txtFilter = listOf("Date ASC", "Date DES", "Percent ASC", "Percent DES")

    override fun setUpAdapter() {
        adapter = ListPromotionAdapter {
            viewModel.getPromotion(it.id)
            findNavController().popBackStack(R.id.profileLoginFragment,true)
            findNavController().navigate(R.id.bagFragment)
        }
    }

    override fun setUpObserve() {
        viewModel.apply {
            promotions.observe(viewLifecycleOwner) {
                binding.nestedScrollView.scrollTo(0,0)
                adapter.submitList(it)
            }
        }
    }

    override fun setUpViews() {
        binding.apply {
            appBarLayout.MaterialToolbar.title = getString(R.string.promo_list)
            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            recyclerViewPromotion.layoutManager = LinearLayoutManager(context)
            recyclerViewPromotion.adapter = adapter

            appBarLayout.btnFilter.setOnClickListener {
                showMenu(it)
            }
        }
    }

    private fun showMenu(v: View) {
        val popup = PopupMenu(context, v)
        popup.menuInflater.inflate(R.menu.menu_filter_promo, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.sortDateAsc -> {
                    binding.appBarLayout.txtNameFilter.text = txtFilter[0]
                    viewModel.filterPromotion(FilterPromotion.DATE, TypeSort.ASCENDING)
                    false
                }
                R.id.sortDateDes -> {
                    binding.appBarLayout.txtNameFilter.text = txtFilter[1]
                    viewModel.filterPromotion(FilterPromotion.DATE, TypeSort.DESCENDING)
                    false
                }
                R.id.sortPercentAsc -> {
                    binding.appBarLayout.txtNameFilter.text = txtFilter[2]
                    viewModel.filterPromotion(FilterPromotion.PERCENT, TypeSort.ASCENDING)
                    false
                }
                R.id.sortPercentDes -> {
                    binding.appBarLayout.txtNameFilter.text = txtFilter[3]
                    viewModel.filterPromotion(FilterPromotion.PERCENT, TypeSort.DESCENDING)
                    false
                }
                else -> false
            }
        }

        popup.setOnDismissListener {}
        popup.show()
    }
}