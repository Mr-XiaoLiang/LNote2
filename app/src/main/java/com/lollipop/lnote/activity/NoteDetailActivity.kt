package com.lollipop.lnote.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import com.lollipop.lnote.R
import com.lollipop.lnote.info.NoteOverviewInfo
import com.lollipop.lnote.util.doAsync
import com.lollipop.lnote.util.onUI

/**
 * 笔记详情页
 * @author Lollipop
 */
class NoteDetailActivity : BaseActivity() {

    companion object {
        fun start(context: Activity, info: NoteOverviewInfo) {
            context.startActivity(Intent(context, NoteDetailActivity::class.java))
        }
    }

    override val floatingViewId: Int
        get() = 0

    override val contentViewId: Int
        get() = R.layout.activity_note_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startLoading()
        doAsync {
            Thread.sleep(10 * 1000L)
            onUI {
                stopLoading()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_note_detail, menu)
        return true
    }

}
