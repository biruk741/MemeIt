package com.innov8.memeit.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequest
import com.innov8.memeit.commons.MyViewHolder
import com.innov8.memeit.R
import com.memeit.backend.models.Badge

class BadgeAdapter(val context: Context) : RecyclerView.Adapter<MyViewHolder<Badge>>() {
    private var allBadges = mutableListOf<Badge>()
    private val awardedBadges = mutableSetOf<Badge>()
    private val inflater = LayoutInflater.from(context)

    companion object {
        const val MODE_GRID = 5
        const val MODE_LIST = 1
    }

    var mode = MODE_GRID

    init {
        allBadges = Badge.allBadges.toMutableList()
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder<Badge> {
        val view = inflater.inflate(R.layout.list_item_badge_grid, parent, false)
        return BadgeGridListViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder<Badge>, position: Int) {
        holder.item_position = position
        holder.bind(allBadges[position])
    }

    override fun getItemCount(): Int = allBadges.size

    fun addAll(awardedBadges: List<Badge>) {
        if (awardedBadges.isEmpty()) return
        this.awardedBadges.addAll(awardedBadges)
        notifyDataSetChanged()
    }

    fun add(item: Badge) {
        awardedBadges.add(item)
        notifyItemChanged(allBadges.indexOf(item))
    }


    fun remove(item: Badge) {
        if (awardedBadges.contains(item)) {
            val index = allBadges.indexOf(item)
            awardedBadges.remove(item)
            notifyItemChanged(index)
        }
    }

    fun clear() {
        awardedBadges.clear()
        notifyDataSetChanged()
    }

    fun setAll(awardedBadges: List<Badge>) {
        this.awardedBadges.clear()
        this.awardedBadges.addAll(awardedBadges)
        notifyDataSetChanged()
    }

    internal inner class BadgeGridListViewHolder(itemView: View) : MyViewHolder<Badge>(itemView) {
        private val badgeIconV: SimpleDraweeView = itemView.findViewById(R.id.badge_icon)
        private val badgeLabelV: TextView = itemView.findViewById(R.id.badge_label)
        private val badgeDescV: TextView = itemView.findViewById(R.id.badge_description)

        override fun bind(t: Badge) {
            if (mode == MODE_GRID) {
                badgeLabelV.visibility = View.GONE
                badgeDescV.visibility = View.GONE
            } else {
                badgeLabelV.visibility = View.VISIBLE
                badgeDescV.visibility = View.VISIBLE
            }
            badgeIconV.setActualImageResource(t.getDrawableId(context))
            if (awardedBadges.contains(t)) {
                itemView.alpha = 1f
            } else {
                itemView.alpha = 0.5f
            }
            badgeLabelV.text = t.label
            badgeDescV.text = t.description
        }
    }
}
