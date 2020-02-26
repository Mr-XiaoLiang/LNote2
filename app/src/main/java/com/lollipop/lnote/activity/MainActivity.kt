package com.lollipop.lnote.activity

import com.bumptech.glide.Glide
import com.lollipop.lnote.R
import com.lollipop.lnote.skin.NoteSkin
import kotlinx.android.synthetic.main.activity_base.*

class MainActivity : BaseActivity() {
    override val floatingViewId = 0
    override val contentViewId = R.layout.activity_main

    override fun onStart() {
        super.onStart()
        val url = "https://cn.bing.com/th?id=OHR.AcadiaSunrise_ZH-CN5619713848_UHD.jpg&amp;rf=LaDigue_UHD.jpg&amp;pid=hp&amp;w=1920&amp;h=1080&amp;rs=1&amp;c=4"
        Glide.with(this).load(url).into(headBg)
//        Glide.with(this).load(url).
    }

    override fun onSkinUpdate(info: NoteSkin) {
    }

}
