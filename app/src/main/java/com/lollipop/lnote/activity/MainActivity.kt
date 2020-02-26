package com.lollipop.lnote.activity

import com.lollipop.lnote.R
import com.lollipop.lnote.skin.NoteSkin

/**
 * 主页的Activity
 * @author Lollipop
 */
class MainActivity : BaseActivity() {
    override val floatingViewId = 0
    override val contentViewId = R.layout.activity_main

    override fun onSkinUpdate(info: NoteSkin) {
    }

}
