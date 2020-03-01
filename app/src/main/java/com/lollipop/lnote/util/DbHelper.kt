package com.lollipop.lnote.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * @author lollipop
 * @date 2020/2/29 23:24
 * 数据库操作类
 */
class DbHelper(context: Context): SQLiteOpenHelper(context, DB_NAME, null, VERSION) {

    companion object {
        const val DB_NAME = "LNote"
        const val VERSION = 1
    }

    /**
     * 标签表
     */
    private object Label {
        /**
         * 表前缀
         */
        private const val TAG = "LABEL_"
        /**
         * 表名
         */
        const val TABLE = "${TAG}TABLE"
        /**
         * ID
         */
        const val ID = "${TAG}ID"
        /**
         * 颜色值
         */
        const val COLOR = "${TAG}COLOR"
        /**
         * 名字
         */
        const val NAME = "${TAG}NAME"

        const val CREATOR = "CREATE TABLE $TABLE (  " +
                " $ID INTEGER PRIMARY KEY,  " +
                " $COLOR INTEGER ,  " +
                " $NAME VARCHAR " +
                " );"
    }

    /**
     * 样式表
     */
    private object Style {
        /**
         * 表前缀
         */
        private const val TAG = "STYLE_"
        /**
         * 表名
         */
        const val TABLE = "${TAG}TABLE"
        /**
         * ID
         */
        const val ID = "${TAG}ID"
        /**
         * 样式类型
         */
        const val TYPE = "${TAG}TYPE"
        /**
         * 样式内容
         */
        const val INFO = "${TAG}INFO"
        /**
         * 样式名
         */
        const val NAME = "${TAG}NAME"

        const val CREATOR = "CREATE TABLE $TABLE (  " +
                " $ID INTEGER PRIMARY KEY,  " +
                " $TYPE VARCHAR ,  " +
                " $INFO VARCHAR ,  " +
                " $NAME VARCHAR " +
                " );"
    }

    /**
     * 笔记表
     */
    private object Note {
        /**
         * 表前缀
         */
        private const val TAG = "NOTE_"
        /**
         * 表名
         */
        const val TABLE = "${TAG}TABLE"
        /**
         * ID
         */
        const val ID = "${TAG}ID"
        /**
         * 笔记内容
         */
        const val INFO = "${TAG}INFO"
        /**
         * 标签
         */
        const val LABEL = "${TAG}LABEL"
        /**
         * 日期
         */
        const val DATE = "${TAG}DATE"
        /**
         * 时间戳
         */
        const val TIME = "${TAG}TIME"
        /**
         * 概览信息
         */
        const val OVERVIEW = "${TAG}OVERVIEW"

        const val CREATOR = "CREATE TABLE $TABLE (  " +
                " $ID INTEGER PRIMARY KEY,  " +
                " $LABEL INTEGER ,  " +
                " $INFO VARCHAR ,  " +
                " $DATE INTEGER , " +
                " $TIME INTEGER , " +
                " $OVERVIEW VARCHAR " +
                " );"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}