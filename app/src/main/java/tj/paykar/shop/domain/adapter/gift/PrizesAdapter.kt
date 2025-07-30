//package tj.paykar.shop.domain.adapter.prizes
//
//import android.animation.AnimatorInflater
//import android.animation.AnimatorSet
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import tj.paykar.shop.R
//import tj.paykar.shop.databinding.ItemPrizesBinding
//import tj.paykar.shop.domain.reprository.shop.PrizesItemClickManager
//
//class PrizesAdapter(private val context: Context, private var prizesList: ArrayList<GiftModel>, val clickListener: PrizesItemClickManager): RecyclerView.Adapter<PrizesAdapter.PrizesViewHolder>() {
//    var clicked = false
//    inner class PrizesViewHolder(view: View): RecyclerView.ViewHolder(view) {
//        val binding = ItemPrizesBinding.bind(view)
//
//        val scale: Float = context.resources.displayMetrics.density
//        val front_anim = AnimatorInflater.loadAnimator(context, R.animator.front_animator) as AnimatorSet
//        val back_anim = AnimatorInflater.loadAnimator(context, R.animator.back_animator) as AnimatorSet
//        fun bind(gift: GiftModel) = with(binding){
//            giftName.text = gift.name
//            cardFront.cameraDistance = 8000 * scale
//            cardBack.cameraDistance = 8000 * scale
//
//            Glide.with(context)
//                .load("https://mobileapp.paykar.tj/api/gifts/images/" + gift.image)
//                .centerCrop()
//                .placeholder(R.drawable.nophoto)
//                .into(giftImage)
//
//            itemView.setOnClickListener {
//                if (!clicked){
//                    front_anim.setTarget(cardFront)
//                    back_anim.setTarget(cardBack)
//                    front_anim.start()
//                    back_anim.start()
//                    clicked = true
//                    clickListener.give(adapterPosition)
//                }
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrizesViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//        val view = inflater.inflate(R.layout.item_prizes, parent, false)
//        return PrizesViewHolder(view)
//    }
//
//    override fun getItemCount(): Int {
//        return prizesList.size
//    }
//
//    override fun onBindViewHolder(holder: PrizesViewHolder, position: Int) {
//        return holder.bind(prizesList[position])
//    }
//}