package com.lollipop.lnote.list

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.lnote.R
import com.lollipop.lnote.info.NoteOverviewInfo

/**
 * @author lollipop
 * @date 2020/3/1 15:27
 * 笔记概览的Holder
 */
class NoteOverviewHolder private
    constructor(view: View,
                private val showDate: (Int) -> Boolean,
                private val showCard: (Int) -> Boolean,
                private val onClick: (Int) -> Unit): RecyclerView.ViewHolder(view) {

    companion object {
        fun create(group: ViewGroup, showDate: (Int) -> Boolean,
                   showCard: (Int) -> Boolean,
                   onClick: (Int) -> Unit): NoteOverviewHolder {
            return NoteOverviewHolder(
                LayoutInflater.from(group.context).inflate(
                    R.layout.item_main_note, group, false),
                showDate, showCard, onClick)
        }
    }

    private val dateView: View by lazy {
        itemView.findViewById<View>(R.id.timerView)
    }

    private val dayView: TextView by lazy {
        itemView.findViewById<TextView>(R.id.dayView)
    }

    private val monthView: TextView by lazy {
        itemView.findViewById<TextView>(R.id.monthView)
    }

    private val cardView: View by lazy {
        itemView.findViewById<View>(R.id.cardView).apply {
            setOnClickListener {
                onCardClick()
            }
        }
    }

    private val labelDrawable: ColorDrawable by lazy {
        val drawable = ColorDrawable()
        itemView.findViewById<ImageView>(R.id.noteLabelView).setImageDrawable(drawable)
        drawable
    }

    private val titleView: TextView by lazy {
        itemView.findViewById<TextView>(R.id.noteTitleView)
    }

    private val overviewView: TextView by lazy {
        itemView.findViewById<TextView>(R.id.noteOverviewView)
    }

    private val expenditureView: View by lazy {
        itemView.findViewById<View>(R.id.expenditureView)
    }

    private val incomeView: View by lazy {
        itemView.findViewById<View>(R.id.incomeView)
    }

    private val alertView: View by lazy {
        itemView.findViewById<View>(R.id.alertView)
    }

    private fun onCardClick() {
        if (showCard(adapterPosition)) {
            onClick(adapterPosition)
        }
    }

    fun onBind(info: NoteOverviewInfo) {
        val isShowCard = showCard(adapterPosition)
        if (!isShowCard) {
            dateView.visibility = View.INVISIBLE
            cardView.visibility = View.GONE
            return
        }
        val isShowDate = showDate(adapterPosition)
        dateView.visibility = if (isShowDate) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
        dayView.text = info.dayStr
        monthView.text = info.monthStr

        cardView.visibility = View.VISIBLE
        labelDrawable.color = info.labelColor
        titleView.text = info.title
        overviewView.text = info.overview
        expenditureView.isShow(info.isExpenditure)
        incomeView.isShow(info.isIncome)
        alertView.isShow(info.isAlert)
    }

    private fun View.isShow(value: Boolean) {
        visibility = if (value) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

}