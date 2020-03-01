package com.lollipop.lnote.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.lnote.info.NoteOverviewInfo

/**
 * @author lollipop
 * @date 2020/3/1 16:02
 */
class NoteOverviewAdapter(
    private val onClick: (NoteOverviewInfo) -> Unit): RecyclerView.Adapter<NoteOverviewHolder>() {

    private val emptyInfo = NoteOverviewInfo(0, 0, "")

    private val data: ArrayList<NoteOverviewInfo> = ArrayList()

    fun addData(infoList: ArrayList<NoteOverviewInfo>) {
        if (infoList.isEmpty()) {
            return
        }
        val startRange = data.size
        data.addAll(infoList)
        notifyItemRangeInserted(startRange, infoList.size)
    }

    fun getItem(position: Int): NoteOverviewInfo? {
        if (position < 0 || position >= data.size) {
            return null
        }
        return data[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteOverviewHolder {
        return NoteOverviewHolder.create(parent,
            { isShowDate(it) },
            { isShowCard(it) },
            { getItem(it)?.let { info ->
                onClick(info)
            } })
    }

    override fun getItemCount(): Int {
        return data.size + 1
    }

    override fun onBindViewHolder(holder: NoteOverviewHolder, position: Int) {
        holder.onBind(getItem(position)?:emptyInfo)
    }

    private fun isShowDate(position: Int): Boolean {
        if (position < 1) {
            return true
        }
        val lastInfo = getItem(position - 1) ?: return true
        val thisInfo = getItem(position) ?: return false
        if (lastInfo.date != thisInfo.date) {
            return true
        }
        return false
    }

    private fun isShowCard(position: Int): Boolean {
        return position >= 0 && position < data.size
    }

}