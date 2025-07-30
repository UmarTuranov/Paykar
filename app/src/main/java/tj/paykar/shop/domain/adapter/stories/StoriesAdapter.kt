package tj.paykar.shop.domain.adapter.stories

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import tj.paykar.shop.R
import tj.paykar.shop.data.model.home.StoriesModel
import tj.paykar.shop.data.storage.StoriesStorage
import tj.paykar.shop.databinding.ItemStoriesBinding
import tj.paykar.shop.domain.usecase.wallet.functions.addBlinkEffect
import tj.paykar.shop.presentation.stories.StoryActivity

class StoriesAdapter(context: Context): RecyclerView.Adapter<StoriesAdapter.StoriesViewHolder>() {

    var stories = ArrayList<StoriesModel>()
    val mContext = context

    inner class StoriesViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemStoriesBinding.bind(view)

        fun bind(story: StoriesModel){
            val seen = StoriesStorage(mContext).getSeen()
            val grey = ContextCompat.getColor(mContext, R.color.shopGrey)
            if (story.id in seen){
                binding.storyCard.setCardBackgroundColor(grey)
            }
            Picasso.get()
                .load("https://paykar.shop" + story.picture)
                .placeholder(R.drawable.nophoto)
                .into(binding.previewImage)
        }

        init {
            view.setOnClickListener {
                addBlinkEffect(it)
                val position: Int = adapterPosition
                val intent = Intent(view.context, StoryActivity::class.java)
                intent.putExtra("storyId", stories[position].id)
                //intent.putExtra("storyType", stories[position].type)
                intent.putExtra("storyType", "Image")
                //intent.putExtra("storyName", stories[position].name)
                intent.putExtra("storyName", stories[position].picture)
                intent.putExtra("storyPosition", position)
                view.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoriesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_stories, parent, false)
        return StoriesViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoriesViewHolder, position: Int){
        holder.bind(stories[position])
        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        if (position == stories.size - 1) {
            layoutParams.marginEnd = (16 * mContext.resources.displayMetrics.density).toInt()
        } else {
            layoutParams.marginStart = (8 * mContext.resources.displayMetrics.density).toInt()
            layoutParams.marginEnd = 0
        }

        if (position == 0) {
            layoutParams.marginStart = (16 * mContext.resources.displayMetrics.density).toInt()
        } else if (position != 0 && position != stories.size - 1) {
            layoutParams.marginStart = (8 * mContext.resources.displayMetrics.density).toInt()
        } else if (position != 0 && position == stories.size - 1) {
            layoutParams.marginEnd = (16 * mContext.resources.displayMetrics.density).toInt()
            layoutParams.marginStart = (8 * mContext.resources.displayMetrics.density).toInt()
        }
        holder.itemView.layoutParams = layoutParams
    }

    override fun getItemCount(): Int = stories.size
}