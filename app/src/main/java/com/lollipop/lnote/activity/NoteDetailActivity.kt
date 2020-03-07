package com.lollipop.lnote.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.core.content.ContextCompat
import com.lollipop.lnote.R
import com.lollipop.lnote.dialog.LongEditDialog
import com.lollipop.lnote.info.NoteOverviewInfo
import com.lollipop.lnote.skin.NoteSkin
import com.lollipop.lnote.util.compatColor
import com.lollipop.lnote.util.doAsync
import com.lollipop.lnote.util.onUI
import kotlinx.android.synthetic.main.activity_note_detail_floating.*

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

    override val floatingViewId = R.layout.activity_note_detail_floating
    override val contentViewId = R.layout.activity_note_detail
    override val fullScreenViewId = 0

    private val longEditDialog: LongEditDialog by lazy {
        LongEditDialog(registerProvider, dialogGroup, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolboxFab.setOnClickListener {
            longEditDialog.show("") {
                notify(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_note_detail, menu)
        return true
    }

    override fun onSkinUpdate(info: NoteSkin) {
        super.onSkinUpdate(info)
    }

}
