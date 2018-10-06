package com.innov8.memeit.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.cloudinary.android.MediaManager
import com.facebook.ads.*
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.view.SimpleDraweeView
import com.github.ybq.android.spinkit.style.CubeGrid
import com.innov8.memegenerator.utils.toast
import com.innov8.memeit.*
import com.innov8.memeit.Activities.CommentsActivity
import com.innov8.memeit.Activities.ProfileActivity
import com.innov8.memeit.Activities.ReactorListActivity
import com.innov8.memeit.Adapters.ListMemeAdapter.Companion.activeRID
import com.innov8.memeit.CustomClasses.LoadingDrawable
import com.innov8.memeit.CustomViews.ProfileDraweeView
import com.memeit.backend.dataclasses.*
import com.memeit.backend.kotlin.MemeItMemes
import com.memeit.backend.kotlin.MemeItUsers
import com.memeit.backend.kotlin.call
import com.stfalcon.frescoimageviewer.ImageViewer
import com.varunest.sparkbutton.SparkButton

abstract class MemeAdapter(val context: Context) : RecyclerView.Adapter<MemeViewHolder>() {
    companion object {
        const val LIST_ADAPTER: Byte = 1
        const val GRID_ADAPTER: Byte = 2
        const val HOME_ADAPTER: Byte = 3
        fun create(type: Byte, context: Context): MemeAdapter = when (type) {
            LIST_ADAPTER -> ListMemeAdapter(context)
            GRID_ADAPTER -> GridMemeAdapter(context)
            HOME_ADAPTER -> HomeMemeAdapter(context)
            else ->
                throw IllegalArgumentException("Use one of (LIST_ADAPTER,GRID_ADAPTER,HOME_ADAPTER)")
        }

        const val LOADING_TYPE = 5864
    }

    var loading: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                if (field)
                    notifyItemInserted(items.size)
                else
                    notifyItemRemoved(items.size)
            }

        }
    val items = mutableListOf<HomeElement>()
    fun addAll(homeElements: List<HomeElement>) {
        if (homeElements.isEmpty()) return
        val start = items.size
        items.addAll(homeElements)
        notifyItemRangeInserted(start, homeElements.size)
    }

    fun add(homeElement: HomeElement) {
        items.add(homeElement)
        notifyItemInserted(items.size - 1)
    }

    fun remove(homeElement: HomeElement) {
        if (items.contains(homeElement)) {
            val index = items.indexOf(homeElement)
            items.remove(homeElement)
            notifyItemRemoved(index)
        }
    }

    fun clear() {
        items.clear()
        log("setSe", "cleared")
        notifyDataSetChanged()
    }

    fun setAll(homeElements: List<HomeElement>) {
        items.clear()
        items.addAll(homeElements)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        return if (viewType == LOADING_TYPE) {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.item_list_meme_loading, parent, false)
            val progress = view.findViewById<ProgressBar>(R.id.meme_loading)
            val d = CubeGrid()
            d.color = Color.rgb(255, 100, 0)
            progress.indeterminateDrawable = d
            object : MemeViewHolder(view, this) {
                override fun bind(homeElement: HomeElement) {
                    //do nothing here
                }
            }
        } else {
            val memeViewHolder = createHolder(parent, viewType)
            memeViewHolder.memeClickedListener = { memeID ->
                val list: List<Meme> = items.filter { it is Meme }
                        .map { it as Meme }
                        .toList()
                val hierarchy = GenericDraweeHierarchyBuilder.newInstance(context.resources)
                        .setProgressBarImage(LoadingDrawable(context))

                ImageViewer.Builder<Meme>(context, list)
                        .setFormatter {
                            MediaManager.get()
                                    .url()
                                    .source(it.imageId)
                                    .generate()
                        }
                        .setCustomDraweeHierarchyBuilder(hierarchy)
                        .setBackgroundColor(Color.WHITE)
                        .setStartPosition(list.map { it.id }.indexOf(memeID))
                        .show()
            }
            memeViewHolder
        }
    }

    override fun getItemCount(): Int = items.size + if (loading) 1 else 0
    override fun onBindViewHolder(holder: MemeViewHolder, position: Int) {
        if (getItemViewType(position) != LOADING_TYPE) {
            holder.itemPosition = position
            holder.bind(items[position])
        }
    }

    override fun getItemViewType(position: Int): Int =
            if (position >= items.size) LOADING_TYPE else items[position].itemType


    abstract fun createHolder(parent: ViewGroup, viewType: Int): MemeViewHolder

    open fun createLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

    fun make(): MyS = MyS()
    inner class MyS : SwipeController() {
        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            val v = viewHolder as MemeViewHolder
            return if (items.size > 0 && items[v.itemPosition] is Meme) {
                makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
            } else {
                makeMovementFlags(0, 0)
            }
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val pos = (viewHolder as MemeViewHolder).itemPosition
            //val meme = items[pos] as Meme
            when (direction) {
                ItemTouchHelper.LEFT -> {
                    context.toast("left")
                }
                ItemTouchHelper.RIGHT -> {
                    context.toast("right")
                }
            }
        }
    }
}

class ListMemeAdapter(context: Context) : MemeAdapter(context) {
    companion object {
        val activeRID = intArrayOf(R.drawable.laughing, R.drawable.rofl, R.drawable.neutral, R.drawable.angry)
    }

    override fun createHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        if (viewType != HomeElement.MEME_TYPE)
            throw IllegalStateException("View Type must only be MEME_TYPE in ListMemeAdapter")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.list_item_meme, parent, false)
        return MemeListViewHolder(view, this)
    }
}

class GridMemeAdapter(context: Context) : MemeAdapter(context) {
    companion object {
        const val GRID_SPAN_COUNT = 3
    }

    override fun createHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        if (viewType != HomeElement.MEME_TYPE)
            throw IllegalStateException("View Type must only be MEME_TYPE in GridMemeAdapter")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.list_item_meme_grid, parent, false)
        return MemeGridViewHolder(view, this)
    }

    override fun createLayoutManager(): RecyclerView.LayoutManager = GridLayoutManager(context, GRID_SPAN_COUNT, RecyclerView.VERTICAL, false)


}

class HomeMemeAdapter(context: Context) : MemeAdapter(context) {
    val usersPool = RecyclerView.RecycledViewPool()
    val tagsPool = RecyclerView.RecycledViewPool()
    val temlplatesPool = RecyclerView.RecycledViewPool()
    override fun createHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        when (viewType) {
            HomeElement.MEME_TYPE -> {
                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.list_item_meme, parent, false)
                return MemeListViewHolder(view, this)
            }
            HomeElement.USER_SUGGESTION_TYPE -> {
                val inflater = LayoutInflater.from(context)
                val v = inflater.inflate(R.layout.list_item_list, parent, false)
                return UserSuggestionHolder(v, this)
            }
            HomeElement.TAG_SUGGESTION_TYPE -> {
                val inflater = LayoutInflater.from(context)
                val v = inflater.inflate(R.layout.list_item_list, parent, false)
                return TagSuggestionHolder(v, this)
            }
            HomeElement.MEME_TEMPLATE_SUGGESTION_TYPE -> {
                val inflater = LayoutInflater.from(context)
                val v = inflater.inflate(R.layout.list_item_list, parent, false)
                return MemeTemplateSuggestionHolder(v, this)
            }
            HomeElement.AD_TYPE -> {
                val inflater = LayoutInflater.from(context)
                val v = inflater.inflate(R.layout.list_item_ad2, parent, false)
                return AdHolder(v, this)
            }
            else -> {
                throw IllegalArgumentException("ViewType must be one of the four")
            }
        }
    }
}


abstract class MemeViewHolder(itemView: View, val memeAdapter: MemeAdapter) : RecyclerView.ViewHolder(itemView) {
    var itemPosition = 0
    var memeClickedListener: ((String) -> Unit)? = null
    abstract fun bind(homeElement: HomeElement)
}


class MemeListViewHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    private val posterPicV: ProfileDraweeView = itemView.findViewById(R.id.notif_icon)
    private val memeImageV: SimpleDraweeView = itemView.findViewById(R.id.meme_image)
    private val commentBtnV: ImageButton = itemView.findViewById(R.id.meme_comment)
    private val posterNameV: TextView = itemView.findViewById(R.id.meme_poster_name)
    private val memeDateV: TextView = itemView.findViewById(R.id.meme_time)
    private val reactionCountV: TextView = itemView.findViewById(R.id.meme_reactions)
    private val commentCountV: TextView = itemView.findViewById(R.id.meme_comment_count)
    private val memeMenu: ImageButton = itemView.findViewById(R.id.meme_options)
    private val reactButton: SparkButton = itemView.findViewById(R.id.react_button)
    private val favButton: SparkButton = itemView.findViewById(R.id.fav_button)
    private val reactGroup: Group = itemView.findViewById(R.id.react_group)
    private val memeTags: TextView = itemView.findViewById(R.id.meme_tags)


    init {
//        memeImageV.setOnClickListener { memeClickedListener?.invoke(getCurrentMeme().id) }
        commentBtnV.setOnClickListener {
            val meme = currentMeme
            val intent = Intent(memeAdapter.context, CommentsActivity::class.java)
            intent.putExtra(CommentsActivity.MEME_PARAM_KEY, meme)
            memeAdapter.context.startActivity(intent)
        }
        posterPicV.setOnClickListener {
            val i = Intent(memeAdapter.context, ProfileActivity::class.java)
            i.putExtra("user", currentMeme.poster?.toUser())
            memeAdapter.context.startActivity(i)
        }
        reactionCountV.setOnClickListener {
            val i = Intent(memeAdapter.context, ReactorListActivity::class.java)
            i.putExtra("mid", currentMeme.id)
            memeAdapter.context.startActivity(i)
        }

        val reactListener = OnClickListener { view ->
            var reactionType: Reaction.ReactionType? = null
            var reactRes = 0
            when (view.id) {
                R.id.react_funny -> {
                    reactionType = Reaction.ReactionType.FUNNY
                    reactRes = activeRID[0]
                }
                R.id.react_veryfunny -> {
                    reactionType = Reaction.ReactionType.VERY_FUNNY
                    reactRes = activeRID[1]
                }
                R.id.react_stupid -> {
                    reactionType = Reaction.ReactionType.STUPID
                    reactRes = activeRID[2]
                }
                R.id.react_angry -> {
                    reactionType = Reaction.ReactionType.ANGERING
                    reactRes = activeRID[3]
                }
            }
            react(reactionType, reactRes)
            toggleReactVisibility()
        }
        itemView.findViewById<View>(R.id.react_funny).setOnClickListener(reactListener)
        itemView.findViewById<View>(R.id.react_veryfunny).setOnClickListener(reactListener)
        itemView.findViewById<View>(R.id.react_stupid).setOnClickListener(reactListener)
        itemView.findViewById<View>(R.id.react_angry).setOnClickListener(reactListener)
        /*reactButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(mContext, "long", Toast.LENGTH_SHORT).show();
                toggleReactVisibility();
                return true;
            }
        });*/
        reactButton.setOnClickListener {
            toggleReactVisibility()

        }
        favButton.isChecked = false
        favButton.setOnClickListener { view ->
            val meme = currentMeme
            if (meme.isMyFavourite)
                MemeItMemes.removeMemeFromFavourite(meme.id!!).call({
                    meme.isMyFavourite = false
                    favButton.isChecked = false
                }, onError())
            else
                MemeItMemes.addMemeToFavourite(meme.id!!).call({
                    meme.isMyFavourite = true
                    favButton.playAnimation()
                    favButton.isChecked = true
                }, onError())
        }
        memeMenu.setOnClickListener { showMemeMenu() }
        memeTags.setOnClickListener { showFollowDialog() }

    }

    private fun toggleReactVisibility() {
        reactGroup.visibility = if (reactGroup.visibility == VISIBLE) GONE else VISIBLE
    }

    private fun showFollowDialog() {
        val meme = currentMeme
        MaterialDialog.Builder(memeAdapter.context)
                .title("Choose Tags to follow")
                .items(meme.tags.map { "#$it" })
                .itemsCallbackMultiChoice(null) { _, _, _ ->
                    true
                }
                .positiveText("Follow")
                .negativeText("Cancel")
                .onPositive { dialog, _ ->
                    val si = dialog.selectedIndices
                    val selectedTags = meme.tags.filterIndexed { index, _ ->
                        si?.contains(index) ?: false
                    }.toTypedArray()
                    log(selectedTags)
                    MemeItUsers.followTags(selectedTags).call({}, onError("Failed folloeing tags")
                }
                .show()
    }

    fun onError(message: String = "", action: (() -> Unit)? = null): (String) -> Unit = {
        memeAdapter.context.toast("$message: $it")
        action?.invoke()
    }

    private fun react(reactionType: Reaction.ReactionType?, finalReactRes: Int) {
        MemeItMemes.reactToMeme(Reaction.create(reactionType, currentMeme.id)).call({
            if (currentMeme.myReaction == null) currentMeme.reactionCount++
            currentMeme.myReaction = Reaction.create(reactionType, null)
            reactButton.setActiveImage(finalReactRes)
            reactButton.playAnimation()
            reactButton.isChecked = true
        }, onError("Reaction Failed"))
    }


    override fun bind(homeElement: HomeElement) {
        val meme = homeElement as Meme

        reactGroup.visibility = GONE

        posterNameV.text = meme.poster?.name
        posterPicV.text = meme.poster?.name.prefix()
        reactionCountV.text = String.format("%d people reacted", meme.reactionCount)
        commentCountV.text = meme.commentCount.toString()
        posterPicV.loadImage(meme.poster?.profileUrl)
        memeDateV.text = meme.date?.formateAsDate()
        if (meme.tags.isEmpty()) {
            memeTags.visibility = GONE
        } else {
            memeTags.visibility = VISIBLE
            memeTags.text = meme.tags.joinToString(", ") { "#$it" }
        }
        if (meme.myReaction !== null) {
            reactButton.setActiveImage(activeRID[meme.myReaction!!.type.ordinal])
            reactButton.isChecked = true
        } else {
            reactButton.isChecked = false
        }
        favButton.isChecked = meme.isMyFavourite
        memeImageV.layoutParams.width = screenWidth
        memeImageV.layoutParams.height = (screenWidth / meme.imageRatio).toInt().trim(200.dp, screenHeight - 200.dp)
        memeImageV.requestLayout()
        memeImageV.loadMeme(meme)
    }

    private fun showMemeMenu() {
        val menu = PopupMenu(memeAdapter.context, memeMenu)
        if (MemeItUsers.getMyUser()!!.userID.equals(currentMeme.poster?.id))
            menu.menuInflater.inflate(R.menu.meme_menu, menu.menu)
        else
            menu.menuInflater.inflate(R.menu.meme_menu_not_own, menu.menu)

        menu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_delete_meme -> {
                    MemeItMemes.deleteMeme(currentMeme.id!!).call(
                            {
                                memeAdapter.remove(Meme(currentMeme.id))
                            }, onError("Error deleting Meme")
                    )
                    return@OnMenuItemClickListener true
                }
                R.id.menu_report_meme -> {
                    MaterialDialog.Builder(memeAdapter.context)
                            .title("Report")
                            .items("Pornography", "Abuse", "Violence", "Not appropriate", "Other")
                            .itemsCallbackMultiChoice(null) { _, _, _ ->
                                true
                            }
                            .positiveText("Report")
                            .negativeText("Cancel")
                            .onPositive { _, _ ->
                                //todo jv finish this up this shit giving me a hard time
                            }
                            .show()

                }
            }
            false
        })
        menu.show()
    }

    private val currentMeme: Meme
        get() {
            return memeAdapter.items[itemPosition] as Meme
        }
}

class MemeGridViewHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    private val memeImageV: SimpleDraweeView = itemView.findViewById(R.id.meme_image)
    val width = screenWidth / GridMemeAdapter.GRID_SPAN_COUNT

    init {
        val lp = FrameLayout.LayoutParams(width, width)
        memeImageV.layoutParams = lp
        memeImageV.setOnClickListener { memeClickedListener?.invoke(getCurrentMeme().id!!) }
        memeImageV.hierarchy.setProgressBarImage(LoadingDrawable(memeAdapter.context))
    }

    override fun bind(homeElement: HomeElement) {
        val meme = homeElement as Meme
        memeImageV.loadMeme(meme, width)
    }

    private fun getCurrentMeme(): Meme {
        return memeAdapter.items[itemPosition] as Meme
    }
}

class UserSuggestionHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    val list: RecyclerView = itemView.findViewById(R.id.list_recyc)
    val title: TextView = itemView.findViewById(R.id.list_title)
    private val adapter: UserSugAdapter = UserSugAdapter(memeAdapter.context)

    init {
        list.makeLinear(RecyclerView.HORIZONTAL)
        list.adapter = adapter
        title.text = "User Suggestions"
        memeAdapter as HomeMemeAdapter

        list.setRecycledViewPool(memeAdapter.usersPool)
    }

    override fun bind(homeElement: HomeElement) {
        val a = homeElement as UserSuggestion
        adapter.setAll(a.users)
    }
}

class TagSuggestionHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    val list: RecyclerView = itemView.findViewById(R.id.list_recyc)
    val title: TextView = itemView.findViewById(R.id.list_title)
    private val adapter: TagsAdapter = TagsAdapter(memeAdapter.context)

    init {
        list.makeLinear(RecyclerView.HORIZONTAL)
        list.adapter = adapter
        title.text = "Recommended Tags"
        memeAdapter as HomeMemeAdapter
        list.setRecycledViewPool(memeAdapter.tagsPool)
    }

    override fun bind(homeElement: HomeElement) {
        val a = homeElement as TagSuggestion
        adapter.setAll(a.tags)
    }
}

class MemeTemplateSuggestionHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    val list: RecyclerView = itemView.findViewById(R.id.list_recyc)
    val title: TextView = itemView.findViewById(R.id.list_title)

    private val adapter: TemplateSugAdapter = TemplateSugAdapter(memeAdapter.context)

    init {
        list.makeLinear(RecyclerView.HORIZONTAL)
        list.adapter = adapter
        title.text = "Meme Templates to Edit"
        memeAdapter as HomeMemeAdapter
        list.setRecycledViewPool(memeAdapter.temlplatesPool)

    }

    override fun bind(homeElement: HomeElement) {
        val a = homeElement as MemeTemplateSuggestion
        adapter.setAll(a.templates)
    }

}

class AdHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    private val nativeAdIcon: AdIconView = itemView.findViewById(R.id.native_ad_icon)
    private val nativeAdTitle: TextView = itemView.findViewById(R.id.native_ad_title)
    private val nativeAdMedia: MediaView = itemView.findViewById(R.id.native_ad_media)
    private val nativeAdSocialContext: TextView = itemView.findViewById(R.id.native_ad_social_context)
    private val nativeAdBody: TextView = itemView.findViewById(R.id.native_ad_body)
    private val sponsoredLabel: TextView = itemView.findViewById(R.id.native_ad_sponsored_label)
    private val nativeAdCallToAction: Button = itemView.findViewById(R.id.native_ad_call_to_action)
    private val adChoicesContainer: LinearLayout = itemView.findViewById(R.id.ad_choices_container)


    private fun bindAd(nativeAd: NativeAd) {

        nativeAd.unregisterView()
        adChoicesContainer.removeAllViews()
        val adChoicesView = AdChoicesView(memeAdapter.context, nativeAd, true)
        adChoicesContainer.addView(adChoicesView, 0)
        nativeAdTitle.text = nativeAd.advertiserName
        nativeAdBody.text = nativeAd.adBodyText
        nativeAdSocialContext.text = nativeAd.adSocialContext
        nativeAdCallToAction.visibility = if (nativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
        nativeAdCallToAction.text = nativeAd.adCallToAction
        sponsoredLabel.text = nativeAd.sponsoredTranslation
        val clickableViews = listOf(nativeAdTitle, nativeAdCallToAction)
        nativeAd.registerViewForInteraction(
                itemView,
                nativeAdMedia,
                nativeAdIcon,
                clickableViews)
    }


    override fun bind(homeElement: HomeElement) {
        val nativeAd: NativeAd = NativeAd(memeAdapter.context, "YOUR_PLACEMENT_ID")
        nativeAd.setAdListener(object : NativeAdListener {
            override fun onAdClicked(p0: Ad) {
                log("qqq facebook add", "ad clicked")

            }

            override fun onMediaDownloaded(p0: Ad) {
                log("qqq facebook add", "media downloaded")

            }

            override fun onError(p0: Ad, p1: AdError) {
                log("qqq facebook add", "ad failed", p1.errorMessage)
            }

            override fun onLoggingImpression(p0: Ad) {
                log("qqq facebook add", "logging impression")
            }

            override fun onAdLoaded(ad: Ad) {
                log("qqq facebook add", "ad loaded")

                if (nativeAd != ad) {
                    return
                }
                bindAd(nativeAd)
            }
        })
        nativeAd.loadAd()
    }
}