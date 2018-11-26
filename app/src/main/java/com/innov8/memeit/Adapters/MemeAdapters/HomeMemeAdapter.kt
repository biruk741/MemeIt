package com.innov8.memeit.Adapters.MemeAdapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memeit.Adapters.MemeAdapters.ViewHolders.*
import com.innov8.memeit.CustomViews.MemeView
import com.innov8.memeit.R
import com.innov8.memeit.measure
import com.memeit.backend.dataclasses.HomeElement

class HomeMemeAdapter(context: Context) : MemeAdapter(context) {
    val usersPool = RecyclerView.RecycledViewPool()
    val tagsPool = RecyclerView.RecycledViewPool()
    val templatesPool = RecyclerView.RecycledViewPool()

    init {


    }

    override fun createHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        return when (viewType) {
            HomeElement.MEME_TYPE -> {
                MemeListViewHolder(MemeView(context), this)
            }
            HomeElement.USER_SUGGESTION_TYPE -> {
                val v = inflater.inflate(R.layout.list_item_list, parent, false)
                UserSuggestionHolder(v, this)
            }
            HomeElement.TAG_SUGGESTION_TYPE -> {
                val v = inflater.inflate(R.layout.list_item_list, parent, false)
                TagSuggestionHolder(v, this)
            }
            HomeElement.MEME_TEMPLATE_SUGGESTION_TYPE -> {
                val v = inflater.inflate(R.layout.list_item_list, parent, false)
                MemeTemplateSuggestionHolder(v, this)
            }
            HomeElement.AD_TYPE -> {
                measure("ad whole") {
                    val v = inflater.inflate(R.layout.list_item_ad_holder, parent, false)
                    measure("ad create") { AdHolder(v, this) }
                }
            }
            else -> {
                throw IllegalArgumentException("ViewType must be one of the four")
            }
        }
    }
}