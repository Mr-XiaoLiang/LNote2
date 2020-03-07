package com.lollipop.lnote.list.paragraph

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.lnote.R
import com.lollipop.lnote.info.paragraph.ParagraphInfo
import com.lollipop.lnote.util.compatColor

/**
 * @author lollipop
 * @date 2020/3/7 23:37
 * 段落的Info
 */
abstract class ParagraphHolder<T: ParagraphInfo>(group: ViewGroup):
    RecyclerView.ViewHolder(createBaseView(group)) {

    companion object {
        private fun createBaseView(group: ViewGroup): View {
            return LayoutInflater.from(group.context)
                .inflate(R.layout.item_paragraph_base, group, false)
        }
    }

    protected abstract val layoutId: Int

    init {
        initContent()
    }

    private fun initContent() {
        if (layoutId != 0) {
            val contentGroup = itemView.findViewById<ViewGroup>(R.id.itemContentGroup)
            LayoutInflater.from(itemView.context).inflate(layoutId, contentGroup, true)
        }
    }

    fun bind(info: ParagraphInfo, inEdit: Boolean) {
        updateGroup(inEdit)
        val paragraphInfo = info.javaClass as? T
        if (paragraphInfo != null) {
            onBind(paragraphInfo, inEdit)
        }
    }

    private fun updateGroup(inEdit: Boolean) {
        itemView.setBackgroundColor(if (inEdit) {
            itemView.compatColor(R.color.paragraphEditBackground)
        } else {
            Color.TRANSPARENT
        })
        itemView.layoutParams.let {
            if (it is ViewGroup.MarginLayoutParams) {
                if (inEdit) {
                    it.setMargins(
                        0,
                        itemView.resources.getDimension(R.dimen.paragraphEditTop).toInt(),
                        0,
                        0)
                } else {
                    it.setMargins(0, 0, 0, 0)
                }
            }
        }
        itemView.translationZ = if (inEdit) {
            itemView.resources.getDimension(R.dimen.paragraphEditZ)
        } else { 0F }
    }

    abstract fun onBind(info: T, inEdit: Boolean)

    abstract fun openStylePanel()

}