package com.lollipop.lnote.util

import android.content.res.ColorStateList
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.lollipop.lnote.R

/**
 * @author lollipop
 * @date 2020/3/4 01:00
 * 消息的辅助类
 */
class NotificationHelper(group: ViewGroup): View.OnClickListener, View.OnAttachStateChangeListener {

    companion object {
        private const val WHAT_TIMEOUT = 233
    }

    private val context = group.context
    private val panelView: View
    private val notificationIcon: ImageView
    private val notificationContent: TextView
    private val actionBtn: MaterialButton

    private var onClickListener: (() -> Unit)? = null
    private var onDismissListener: ((DismissType) -> Unit)? = null

    private val timeOut = 25 * 100L
    private val messageHandler: Handler by lazy {
        Handler()
    }

    private val notificationIconTint: ColorStateList by lazy {
        ColorStateList.valueOf(context.compatColor(R.color.topNotificationIcon))
    }
    private val alertIconTint: ColorStateList by lazy {
        ColorStateList.valueOf(context.compatColor(R.color.topAlertIcon))
    }

    init {
        panelView = LayoutInflater.from(context).inflate(
            R.layout.fragment_notification, group, true)
        notificationIcon = panelView.findViewById(R.id.notificationIcon)
        notificationContent = panelView.findViewById(R.id.notificationContent)
        actionBtn = panelView.findViewById(R.id.actionBtn)
        initView()
    }

    private fun initView() {
        panelView.visibility = View.INVISIBLE
        actionBtn.setOnClickListener(this)
        panelView.addOnAttachStateChangeListener(this)
    }

    fun onInsetChange(left: Int, top: Int, right: Int, bottom: Int) {
        panelView.setPadding(left, top, right, 0)
    }

    fun notify(value: CharSequence, icon: Int = 0, action: CharSequence = "",
               onClick: (() -> Unit)? = null,
               onDismiss: ((DismissType) -> Unit)? = null) {
        updateByNotification()
        updateValue(value, icon, action, onClick, onDismiss)
    }

    fun alert(value: CharSequence, icon: Int = 0, action: CharSequence = "",
              onClick: (() -> Unit)? = null,
              onDismiss: ((DismissType) -> Unit)? = null) {
        updateByAlert()
        updateValue(value, icon, action, onClick, onDismiss)
    }

    private fun updateValue(value: CharSequence, icon: Int = 0, action: CharSequence = "",
                            onClick: (() -> Unit)? = null,
                            onDismiss: ((DismissType) -> Unit)? = null) {
        notificationContent.text = value
        if (icon != 0) {
            notificationIcon.visibility = View.VISIBLE
            notificationIcon.setImageResource(icon)
        } else {
            notificationIcon.visibility = View.GONE
        }
        if (!TextUtils.isEmpty(action)) {
            actionBtn.visibility = View.VISIBLE
            actionBtn.text = action
            onClickListener = onClick
        } else {
            actionBtn.visibility = View.GONE
        }
        onDismissListener = onDismiss
    }

    private fun updateByNotification() {
        panelView.setBackgroundColor(context.compatColor(R.color.topNotificationBackground))
        notificationContent.setTextColor(context.compatColor(R.color.topNotificationContent))
        notificationIcon.imageTintList = notificationIconTint
        actionBtn.setTextColor(context.compatColor(R.color.topNotificationAction))
    }

    private fun updateByAlert() {
        panelView.setBackgroundColor(context.compatColor(R.color.topAlertBackground))
        notificationContent.setTextColor(context.compatColor(R.color.topAlertContent))
        notificationIcon.imageTintList = alertIconTint
        actionBtn.setTextColor(context.compatColor(R.color.topAlertAction))
    }

    private fun onDismiss(dismissType: DismissType) {
        onClickListener = null
        onDismissListener?.invoke(dismissType)
        onDismissListener = null
    }

    private fun doDismiss(dismissType: DismissType) {
        messageHandler.removeMessages(WHAT_TIMEOUT)
        onDismiss(dismissType)
    }

    private fun onShow() {
        doDismiss(DismissType.Replace)
        messageHandler.sendEmptyMessageDelayed(WHAT_TIMEOUT, timeOut)
        // TODO
    }

    override fun onViewDetachedFromWindow(v: View?) {
        doDismiss(DismissType.Detached)
    }

    override fun onViewAttachedToWindow(v: View?) {  }

    override fun onClick(v: View?) {
        doDismiss(DismissType.Action)
    }

    enum class DismissType {
        /**
         * 超时
         */
        TimeOut,
        /**
         * 被点击
         */
        Action,
        /**
         * 被移除
         */
        Remove,
        /**
         * View被销毁
         */
        Detached,
        /**
         * 被顶替
         */
        Replace
    }

}