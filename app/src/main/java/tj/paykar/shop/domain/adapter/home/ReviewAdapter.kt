package tj.paykar.shop.domain.adapter.home

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tj.paykar.shop.R
import tj.paykar.shop.data.model.ReviewModel
import tj.paykar.shop.databinding.ItemReviewsBinding

class ReviewAdapter(context: Context): RecyclerView.Adapter<ReviewAdapter.ReviewHolder>() {

    var reviewList = ArrayList<ReviewModel>()
    private var mContext = context

    class ReviewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = ItemReviewsBinding.bind(item)

        fun bind(review: ReviewModel, context: Context) = with(binding) {
            name.text = review.name
            val comment = Html.fromHtml(review.review, Html.FROM_HTML_MODE_LEGACY).toString()
            reviewText.text = comment
            quote.setColorFilter(context.resources.getColor(R.color.shopGrey))

                star1.setColorFilter(context.resources.getColor(R.color.yellow))
                star2.setColorFilter(context.resources.getColor(R.color.yellow))
                star3.setColorFilter(context.resources.getColor(R.color.yellow))
                star4.setColorFilter(context.resources.getColor(R.color.yellow))
                star5.setColorFilter(context.resources.getColor(R.color.yellow))

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_reviews, parent, false)
        return ReviewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewHolder, position: Int) {
        return holder.bind(reviewList[position], mContext)
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }
}