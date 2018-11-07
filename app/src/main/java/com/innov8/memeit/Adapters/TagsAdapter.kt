package com.innov8.memeit.Adapters

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.github.ybq.android.spinkit.style.CubeGrid
import com.innov8.memegenerator.Adapters.MyViewHolder
import com.innov8.memeit.CustomClasses.CustomMethods
import com.innov8.memeit.R
import com.innov8.memeit.commons.toast
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.memeit.backend.dataclasses.Tag

class TagsAdapter(context: Context) : SimpleELEListAdapter<Tag>(context, R.layout.list_item_tags) {


    override var emptyDrawableId: Int = R.drawable.tag2
    override var errorDrawableId: Int = R.drawable.ic_no_internet
    override var emptyDescription: String = "No Tags"
    override var errorDescription: String = "Couldn't load Tags"
    override var emptyActionText: String? = ""
    override var errorActionText: String? = "Try Again"
    override val loadingDrawable = CubeGrid().apply {
        color = Color.rgb(255, 100, 0)

    }


    override fun createViewHolder(view: View): MyViewHolder<Tag> {
        return TagsViewHolder(view)
    }


    private val colors = context.resources.getStringArray(R.array.tagColors)
            .map { Color.parseColor(it) }

    private fun getColor(tag: String): Int {
        val i = tag.toCharArray()
                .map { it.toInt() }
                .toIntArray()
                .sum()
        return colors[i % colors.size]
    }

    inner class TagsViewHolder(itemView: View) : MyViewHolder<Tag>(itemView) {
        private val overlay: View = itemView.findViewById(R.id.overlay)
        private val tagV: TextView = itemView.findViewById(R.id.tag)
        private val tagPostCountV: TextView = itemView.findViewById(R.id.tag_post_count)
        private val tagFollowV: TextView = itemView.findViewById(R.id.follow_tag)

        init {
            tagFollowV.setOnClickListener { _ ->
                val t = getItemAt(item_position)
                if (tagFollowV.text == "Unfollow")
                    MemeItUsers.unfollowTag(t.tag).call({

                        context?.toast("Unfollowed")
                        tagFollowV.text = "Follow"
                    }, {
                        context?.toast("Failed to Unfollow:- $it")

                    })
                else
                    MemeItUsers.followTags(arrayOf(t.tag)).call({
                        context?.toast("Followed")
                        tagFollowV.text = "Unfollow"
                    }, {
                        context?.toast("Failed to Follow:- $it")
                    })
            }
        }

        override fun bind(t: Tag) {
            tagFollowV.text = if (t.followed) "Unfollow" else "Follow"
            overlay.setBackgroundColor(getColor(t.tag))
            tagV.text = "#${t.tag}"
            tagPostCountV.text = CustomMethods.formatNumber(t.count, "post", "posts")
        }
    }
}