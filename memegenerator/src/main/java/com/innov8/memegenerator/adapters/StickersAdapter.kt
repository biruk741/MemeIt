package com.innov8.memegenerator.adapters

import android.content.Context
import android.view.View
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.innov8.memegenerator.R

class StickersAdapter(context: Context, val span: Int) : ListAdapter<String>(context, R.layout.list_item_sticker) {
    val size = context.resources.displayMetrics.widthPixels / span
    var onItemClick: ((String) -> Unit)? = null
    override fun createViewHolder(view: View): MyViewHolder<String> {
        return StickerViewHolder(view)
    }

    inner class StickerViewHolder(itemView: View) : MyViewHolder<String>(itemView) {
        private val stickerV: SimpleDraweeView = itemView.findViewById(R.id.sticker_view)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(getItemAt(item_position))
            }
        }

        override fun bind(t: String) {
            val req = ImageRequestBuilder.fromRequest(ImageRequest.fromUri(t))
                    .setResizeOptions(ResizeOptions.forSquareSize(size))
                    .build()
            stickerV.setImageRequest(req)
        }

    }
}