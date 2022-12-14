package com.goldenowl.ecommerceapp.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goldenowl.ecommerceapp.data.Favorite
import com.goldenowl.ecommerceapp.data.FavoriteAndProduct
import com.goldenowl.ecommerceapp.databinding.ItemProductFavoriteBinding
import com.goldenowl.ecommerceapp.utilities.GlideDefault

class ListFavoriteAdapter(
    private val onCloseClicked: (FavoriteAndProduct) -> Unit,
    private val onItemClicked: (FavoriteAndProduct) -> Unit,
    private val onBagClicked: (View, FavoriteAndProduct) -> Unit,
    private val setButtonBag: (View, Favorite) -> Unit
) :
    ListAdapter<FavoriteAndProduct, ListFavoriteAdapter.ItemViewHolder>(DiffCallback) {

    class ItemViewHolder(
        private val onCloseClicked: (FavoriteAndProduct) -> Unit,
        private val onBagClicked: (View, FavoriteAndProduct) -> Unit,
        private val setButtonBag: (View, Favorite) -> Unit,
        private var binding: ItemProductFavoriteBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(favoriteAndProduct: FavoriteAndProduct) {
            val size = favoriteAndProduct.product.getColorAndSize(
                favoriteAndProduct.favorite.color,
                favoriteAndProduct.favorite.size
            )
            binding.apply {
                GlideDefault.show(
                    itemView.context,
                    favoriteAndProduct.product.images[0],
                    imgProduct,
                    true
                )
                txtName.text = favoriteAndProduct.product.title
                txtBrandName.text = favoriteAndProduct.product.brandName
                ratingBar.rating = favoriteAndProduct.product.reviewStars.toFloat()
                txtColorInput.text = favoriteAndProduct.favorite.color
                txtSizeInput.text = favoriteAndProduct.favorite.size

                txtNumberVote.text = "(${favoriteAndProduct.product.numberReviews})"
                txtPrice.text = "${size?.price}\$"

                size?.quantity?.let {
                    if (size.quantity < 1) {
                        grayOutLayout.visibility = View.VISIBLE
                        txtSoldOut.visibility = View.VISIBLE
                        btnBag.visibility = View.GONE
                    } else {
                        grayOutLayout.visibility = View.GONE
                        txtSoldOut.visibility = View.GONE
                        btnBag.visibility = View.VISIBLE
                    }
                    if (favoriteAndProduct.product.salePercent != null) {
                        txtPrice.paintFlags = txtPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        txtSalePrice.visibility = View.VISIBLE
                        txtSalePercent.visibility = View.VISIBLE
                        txtSalePercent.text = "-${favoriteAndProduct.product.salePercent}%"
                        txtSalePrice.text =
                            "${size.price * (100 - favoriteAndProduct.product.salePercent) / 100}\$"
                    } else {
                        txtPrice.paintFlags = 0
                        txtSalePercent.visibility = View.GONE
                        txtSalePrice.visibility = View.GONE
                    }
                }

                btnRemoveFavorite.setOnClickListener {
                    onCloseClicked(favoriteAndProduct)
                }

                setButtonBag(btnBag, favoriteAndProduct.favorite)
                btnBag.setOnClickListener {
                    onBagClicked(btnBag, favoriteAndProduct)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            onCloseClicked,
            onBagClicked,
            setButtonBag,
            ItemProductFavoriteBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<FavoriteAndProduct>() {
            override fun areItemsTheSame(
                oldProduct: FavoriteAndProduct,
                newProduct: FavoriteAndProduct
            ): Boolean {
                return newProduct === oldProduct
            }

            override fun areContentsTheSame(
                oldProduct: FavoriteAndProduct,
                newProduct: FavoriteAndProduct
            ): Boolean {
                return newProduct == oldProduct
            }
        }
    }

}