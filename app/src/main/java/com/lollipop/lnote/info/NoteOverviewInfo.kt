package com.lollipop.lnote.info

import android.graphics.Color
import android.text.TextUtils
import com.lollipop.lnote.util.numberFormat
import org.json.JSONObject

/**
 * @author lollipop
 * @date 2020/2/29 23:07
 * 笔记概览信息
 */
class NoteOverviewInfo(val id: Int, val date: Int, val info: String) {

    companion object {
        private const val LABEL_COLOR = "LABEL_COLOR"
        private const val TITLE = "TITLE"
        private const val OVERVIEW = "OVERVIEW"
        private const val EXPENDITURE = "EXPENDITURE"
        private const val INCOME = "INCOME"
        private const val ALERT = "ALERT"
    }

    private val infoObject: JSONObject = if (TextUtils.isEmpty(info)) {
        JSONObject()
    } else {
        try {
            JSONObject(info)
        } catch (e: Exception) {
            JSONObject()
        }
    }

    /**
     * 标签颜色
     */
    val labelColor = infoObject.optInt(LABEL_COLOR, Color.TRANSPARENT)

    /**
     * 标题
     */
    val title = infoObject.optString(TITLE, "")

    /**
     * 预览
     */
    val overview = infoObject.optString(OVERVIEW, "")

    /**
     * 包含支出
     */
    val isExpenditure = infoObject.optBoolean(EXPENDITURE, false)

    /**
     * 包含收入
     */
    val isIncome = infoObject.optBoolean(INCOME, false)

    /**
     * 包含提醒
     */
    val isAlert = infoObject.optBoolean(ALERT, false)

    /**
     * 日期的文本内容
     */
    val dayStr = (date % 100).toString().numberFormat()

    /**
     * 月份的文本内容
     */
    val monthStr = ((date / 100) % 100).toString().numberFormat()

}