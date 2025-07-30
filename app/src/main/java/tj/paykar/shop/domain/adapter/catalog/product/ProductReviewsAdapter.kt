package tj.paykar.shop.domain.adapter.catalog.product

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.ProductReviewModel
import tj.paykar.shop.databinding.ProductReviewLayoutBinding

class ProductReviewsAdapter(context: Context): RecyclerView.Adapter<ProductReviewsAdapter.ReviewHolder>() {

    var reviews = ArrayList<ProductReviewModel>()
    var mContext = context
    inner class ReviewHolder(view:View): RecyclerView.ViewHolder(view) {
        val binding = ProductReviewLayoutBinding.bind(view)

        fun bind(review: ProductReviewModel) = with(binding){
            dignitiesText.text = if (review.dignity != "") review.dignity else "Нет замечаний"
            flawsText.text = if (review.flaws != "") review.flaws else "Нет замечаний"
            commentText.text = if (review.comments != "") review.comments else "Нет комментарий"

            setStars(review.rating ?: 0)
        }

        private fun setStars(stars: Int) = with(binding){
            when(stars){
                1->{
                    star1.setColorFilter(mContext.resources.getColor(R.color.yellow))
                }
                2->{
                    star1.setColorFilter(mContext.resources.getColor(R.color.yellow))
                    star2.setColorFilter(mContext.resources.getColor(R.color.yellow))
                }
                3->{
                    star1.setColorFilter(mContext.resources.getColor(R.color.yellow))
                    star2.setColorFilter(mContext.resources.getColor(R.color.yellow))
                    star3.setColorFilter(mContext.resources.getColor(R.color.yellow))
                }
                4->{
                    star1.setColorFilter(mContext.resources.getColor(R.color.yellow))
                    star2.setColorFilter(mContext.resources.getColor(R.color.yellow))
                    star3.setColorFilter(mContext.resources.getColor(R.color.yellow))
                    star4.setColorFilter(mContext.resources.getColor(R.color.yellow))
                }
                5->{
                    star1.setColorFilter(mContext.resources.getColor(R.color.yellow))
                    star2.setColorFilter(mContext.resources.getColor(R.color.yellow))
                    star3.setColorFilter(mContext.resources.getColor(R.color.yellow))
                    star4.setColorFilter(mContext.resources.getColor(R.color.yellow))
                    star5.setColorFilter(mContext.resources.getColor(R.color.yellow))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_review_layout2, parent, false)
        return ReviewHolder(view)
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    override fun onBindViewHolder(holder: ReviewHolder, position: Int) {
        return holder.bind(reviews[position])
    }
}