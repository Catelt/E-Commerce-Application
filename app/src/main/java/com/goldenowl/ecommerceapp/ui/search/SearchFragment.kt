package com.goldenowl.ecommerceapp.ui.search

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListCategoryAdapter
import com.goldenowl.ecommerceapp.adapters.ListHistoryAdapter
import com.goldenowl.ecommerceapp.core.BaseFragment
import com.goldenowl.ecommerceapp.databinding.FragmentSearchBinding
import com.goldenowl.ecommerceapp.ui.shop.ShopViewModel
import com.goldenowl.ecommerceapp.utilities.HISTORY
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(
    FragmentSearchBinding::inflate
) {
    override val viewModel: ShopViewModel by viewModels()
    private var listHistory: MutableList<String> = mutableListOf()
    private var listCategory: List<String> = emptyList()
    private lateinit var adapterHistoryAdapter: ListHistoryAdapter
    private lateinit var adapterCategoryAdapter: ListCategoryAdapter
    private var isViewAll: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadData()
    }

    override fun setUpAdapter() {
        adapterHistoryAdapter = ListHistoryAdapter {
            actionFragment("", it)
        }

        adapterCategoryAdapter = ListCategoryAdapter { str ->
            actionFragment(str, null)
        }
    }

    override fun setUpObserve() {
        viewModel.apply {
            allCategory.observe(viewLifecycleOwner) {
                listCategory = it
                if (!isViewAll) {
                    adapterCategoryAdapter.submitList(setDefaultList())
                } else {
                    adapterCategoryAdapter.submitList(it)
                }
            }
        }
    }

    override fun setUpViews() {
        binding.apply {
            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.FLEX_START
            layoutManager.alignItems = AlignItems.FLEX_START
            recyclerViewHistory.layoutManager = layoutManager

            recyclerViewHistory.adapter = adapterHistoryAdapter
            setHistoryLayout()

            adapterHistoryAdapter.submitList(listHistory.toList())

            recyclerViewCategory.layoutManager =
                GridLayoutManager(context, GRIDVIEW_SPAN_COUNT)
            recyclerViewCategory.adapter = adapterCategoryAdapter



            btnViewAll.setOnClickListener {
                adapterCategoryAdapter.submitList(listCategory)
                btnViewAll.visibility = View.GONE
                isViewAll = true
            }

            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
            editTextSearch.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                    if ((event?.action == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)
                    ) {
                        checkHistory(editTextSearch.text.toString())
                        saveData()
                        actionFragment("", editTextSearch.text.toString())
                        return true
                    }
                    return false
                }
            })
            editTextSearch.postDelayed({
                showKeyboard(requireActivity(), editTextSearch)}, 50)
            btnQR.setOnClickListener {
                findNavController().navigate(R.id.qrScanFragment)
            }
        }
    }

    private fun showKeyboard(activity: Activity, editText: EditText) {
        val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        editText.requestFocus()
        inputMethodManager.showSoftInput(editText, 0)
    }

    private fun setHistoryLayout() {
        if (listHistory.isEmpty()) {
            binding.historyLayout.visibility = View.GONE
        } else {
            binding.historyLayout.visibility = View.VISIBLE
        }
    }

    private fun loadData() {
        val sharedPreferences: SharedPreferences? =
            activity?.getSharedPreferences(HISTORY, MODE_PRIVATE)
        sharedPreferences?.let {
            val json = sharedPreferences.getString(HISTORY, "")
            if (!json.isNullOrBlank()) {
                val type = object : TypeToken<List<String?>?>() {}.type
                val arrPackageData = Gson().fromJson<List<String>>(json, type)
                listHistory.addAll(arrPackageData)
            }
        }
    }

    private fun saveData() {
        val sharedPreferences: SharedPreferences? =
            activity?.getSharedPreferences(HISTORY, MODE_PRIVATE)
        val json = Gson().toJson(listHistory)
        sharedPreferences?.let {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString(HISTORY, json)
            editor.commit()
        }
    }

    private fun setDefaultList(): List<String> {
        if (listCategory.size > MAX_CATEGORY) {
            binding.btnViewAll.visibility = View.VISIBLE
            return listCategory.subList(0, MAX_CATEGORY)
        }
        binding.btnViewAll.visibility = View.GONE
        return listCategory
    }

    private fun checkHistory(string: String) {
        if (listHistory.contains(string)) {
            return
        }
        if (listHistory.size > 9) {
            listHistory.removeAt(0)
        }
        listHistory.add(string)


    }

    fun actionFragment(category: String, name: String?) {
        val action = SearchFragmentDirections.actionSearchFragmentToCatalogFragment(
            nameCategories = category,
            nameProduct = name
        )
        findNavController().navigate(action)
    }

    override fun onPause() {
        super.onPause()
        binding.editTextSearch.clearFocus()
        (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            binding.editTextSearch.windowToken,
            0
        )
    }

    companion object {
        const val MAX_CATEGORY = 6
        const val GRIDVIEW_SPAN_COUNT = 2
    }
}