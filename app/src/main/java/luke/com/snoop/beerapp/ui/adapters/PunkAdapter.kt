package luke.com.snoop.beerapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils.loadAnimation
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import luke.com.snoop.beerapp.R
import luke.com.snoop.beerapp.domain.entities.Beer
import com.facebook.drawee.view.SimpleDraweeView

class PunkAdapter(private val exampleList: List<Beer>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var onItemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(item: Beer)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home, parent, false)

        return PunkViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PunkViewHolder) {
            val currentItem = exampleList[position]
            holder.beerImage.setImageURI(currentItem.imageURL)
            holder.beerName.text = currentItem.name
            holder.beerTagline.text = currentItem.tagline
            holder.beerABV.text = currentItem.abv.toString()

            holder.itemView.animation =
                loadAnimation(holder.itemView.context, R.anim.fade_scale_animation)

            holder.itemView.setOnClickListener {
                onItemClickListener.onItemClick(exampleList[position])
            }
        }
    }


    override fun getItemCount() = exampleList.size

    class PunkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val beerImage: SimpleDraweeView = itemView.findViewById(R.id.beer_image)
        val beerName: TextView = itemView.findViewById(R.id.beer_name)
        val beerTagline: TextView = itemView.findViewById(R.id.beer_tagline)
        val beerABV: TextView = itemView.findViewById(R.id.beer_abv)
    }

}