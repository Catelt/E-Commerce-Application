package com.goldenowl.ecommerceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goldenowl.ecommerceapp.data.Review
import com.goldenowl.ecommerceapp.databinding.ItemReviewBinding
import com.goldenowl.ecommerceapp.utilities.DateFormat
import de.hdodenhof.circleimageview.CircleImageView

class ListReviewAdapter(
    private val setNameAndAvatar: (TextView, CircleImageView, String) -> Unit,
    private val onHelpfulClicked: (Review, TextView, ImageView, Boolean) -> Unit,
    private val setHelpfulButton: (Review, TextView, ImageView) -> Unit,
    private val setRecyclerView: (RecyclerView, Review) -> Unit,
) :
    ListAdapter<Review, ListReviewAdapter.ItemViewHolder>(DiffCallback) {

    var isHelpful: Boolean = false

    class ItemViewHolder(
        private val setNameAndAvatar: (TextView, CircleImageView, String) -> Unit,
        private val onHelpfulClicked: (Review, TextView, ImageView, Boolean) -> Unit,
        private val setHelpfulButton: (Review, TextView, ImageView) -> Unit,
        private val setRecyclerVIew: (RecyclerView, Review) -> Unit,
        private var isHelpful: Boolean,
        private val binding: ItemReviewBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review) {
            binding.apply {
                txtDescription.text = review.description
                setNameAndAvatar(txtName, imgAvatar, review.idUser)

                ratingBar.rating = review.star.toFloat()

                val timeCreated = review.createdTimer?.toDate()

                timeCreated?.let {
                    txtCreated.text = DateFormat.review.format(it).toString()
                }

                setHelpfulButton(review, txtHelpful, icLike)
                setRecyclerVIew(recyclerViewImageReview, review)
                btnHelpful.setOnClickListener {
                    isHelpful = !isHelpful
                    onHelpfulClicked(review, txtHelpful, icLike, isHelpful)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            setNameAndAvatar,
            onHelpfulClicked,
            setHelpfulButton,
            setRecyclerView,
            isHelpful,
            ItemReviewBinding.inflate(
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
        holder.bind(current)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Review>() {
            override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem == newItem
            }
        }
    }
}