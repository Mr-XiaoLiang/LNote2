package com.lollipop.lnote.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.lollipop.lnote.R

/**
 * @author lollipop
 * @date 2020/3/4 01:00
 * 消息的辅助类
 */
class NotificationHelper(group: ViewGroup) {

    private val context = group.context
    private val panelView: View
    private val notificationGroup: RecyclerView
    private val clearAllBtn: ExtendedFloatingActionButton

    init {
        panelView = LayoutInflater.from(context).inflate(
            R.layout.fragment_notification, group, true)
        notificationGroup = panelView.findViewById(R.id.notificationGroup)
        clearAllBtn = panelView.findViewById(R.id.clearAllBtn)
    }

}