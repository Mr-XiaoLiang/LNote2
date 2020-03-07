package com.lollipop.lnote.list.paragraph

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.lnote.R
import com.lollipop.lnote.info.paragraph.ParagraphInfo
import java.lang.RuntimeException

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
        val paragraphInfo = (info.javaClass as? T)?: throw RuntimeException("$info is not the required type")
        onBind(paragraphInfo, inEdit)
    }

    abstract fun onBind(info: T, inEdit: Boolean)

}