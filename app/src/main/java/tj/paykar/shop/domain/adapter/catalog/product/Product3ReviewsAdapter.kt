package tj.paykar.shop.domain.adapter.catalog.product

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tj.paykar.shop.R
import tj.paykar.shop.data.model.shop.ProductReviewModel
import tj.paykar.shop.databinding.ProductReviewLayoutBinding
import tj.paykar.shop.databinding.SeeMoreLayoutBinding
import tj.paykar.shop.domain.reprository.shop.SeeMoreReviewsListener

private const val VIEW_TYPE_NORMAL = 1
private const val VIEW_TYPE_FOOTER = 2

class Product3ReviewsAdapter(private val context: Context, private val dataList: List<ProductReviewModel>, private val moreThan3: Boolean, private val seeMoreListener: tj.paykar.shop.domain.reprository.shop.SeeMoreReviewsListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (moreThan3 && position == dataList.size) {
            VIEW_TYPE_FOOTER
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_NORMAL) {
             val view = LayoutInflater.from(parent.context).inflate(R.layout.product_review_layout, parent, false)
             NormalViewHolder(view)
        } else {
             val view = LayoutInflater.from(parent.context).inflate(R.layout.see_more_layout, parent, false)
             FooterViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NormalViewHolder) {
            holder.bind(dataList[position])
        } else if (holder is FooterViewHolder) {
            holder.bind()
        }
    }

    override fun getItemCount(): Int {
        return if (moreThan3){
            dataList.size + 1
        } else {
            dataList.size
        }
    }

    // ViewHolder для обычных элементов списка
    inner class NormalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
                    star1.setColorFilter(context.resources.getColor(R.color.yellow))
                }
                2->{
                    star1.setColorFilter(context.resources.getColor(R.color.yellow))
                    star2.setColorFilter(context.resources.getColor(R.color.yellow))
                }
                3->{
                    star1.setColorFilter(context.resources.getColor(R.color.yellow))
                    star2.setColorFilter(context.resources.getColor(R.color.yellow))
                    star3.setColorFilter(context.resources.getColor(R.color.yellow))
                }
                4->{
                    star1.setColorFilter(context.resources.getColor(R.color.yellow))
                    star2.setColorFilter(context.resources.getColor(R.color.yellow))
                    star3.setColorFilter(context.resources.getColor(R.color.yellow))
                    star4.setColorFilter(context.resources.getColor(R.color.yellow))
                }
                5->{
                    star1.setColorFilter(context.resources.getColor(R.color.yellow))
                    star2.setColorFilter(context.resources.getColor(R.color.yellow))
                    star3.setColorFilter(context.resources.getColor(R.color.yellow))
                    star4.setColorFilter(context.resources.getColor(R.color.yellow))
                    star5.setColorFilter(context.resources.getColor(R.color.yellow))
                }
                else->{}
            }
        }
    }

    // ViewHolder для "footer"
    inner class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = SeeMoreLayoutBinding.bind(itemView)
        fun bind() = with(binding) {
            seeMore.setOnClickListener {
                Log.d("--F FooterClick", "Done")
                seeMoreListener.seeMore()
                //context.startActivity(Intent(context, MainActivity::class.java))
            }
        }
    }
}
