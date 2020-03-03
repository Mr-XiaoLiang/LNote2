package com.lollipop.lnote.activity

import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.lollipop.lnote.R
import com.lollipop.lnote.skin.NoteSkin
import com.lollipop.lnote.util.BingWallpaper
import com.lollipop.lnote.util.GlideBlurTransformation
import com.lollipop.lnote.util.NotificationHelper
import com.lollipop.lnote.util.PreferenceHelper.save
import com.lollipop.lnote.util.PreferenceHelper.take
import com.lollipop.lnote.util.range
import com.lollipop.skin.SkinProvider
import kotlinx.android.synthetic.main.activity_base.*


/**
 * @author lollipop
 * @date 2020-02-24 00:07
 * 基础的Activity
 */
abstract class BaseActivity: AppCompatActivity(), SkinProvider<NoteSkin> {

    companion object {
        private const val KEY_LAST_WALLPAPER = "KEY_LAST_WALLPAPER"
        private const val KEY_LAST_WALLPAPER_INFO = "KEY_LAST_WALLPAPER_INFO"
    }

    /**
     * 悬浮层的View的ID
     */
    protected abstract val floatingViewId: Int

    /**
     * 内容层的View的ID
     */
    protected abstract val contentViewId: Int

    /**
     * 内容框架的View的ID
     */
    private val layoutId = R.layout.activity_base

    /**
     * 展示返回按钮
     */
    protected var isShowBack = true

    private var toolbarHeight = 0

    private var windowInset = Rect()

    protected var isLoading = false
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        initScaffold()
    }

    override fun onStart() {
        super.onStart()
        supportActionBar?.setDisplayHomeAsUpEnabled(isShowBack)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 初始化脚手架
     */
    private fun initScaffold() {
        if (contentViewId != 0) {
            layoutInflater.inflate(contentViewId, contentGroup, true)
        }
        if (floatingViewId != 0) {
            layoutInflater.inflate(floatingViewId, floatingGroup, true)
        }
        setSupportActionBar(toolbar)
        initInsetCallback(rootGroup)
        bindHeadImages()
        toolbar.post {
            toolbarHeight = toolbar.height
        }
        loadHeadWallpaper()
        contentLoading.putColorForRes(R.color.colorPrimary,
            R.color.colorAccent, R.color.toolbarIcon)
//        NotificationHelper(fullScreenGroup)
    }

    private fun loadHeadWallpaper() {
        // 先设置默认的
        loadWallpaper(take(KEY_LAST_WALLPAPER, ""))
        wallpaperCopyrightView.text = take(KEY_LAST_WALLPAPER_INFO, "")
        // 加载新的
        BingWallpaper.request {
            if (BingWallpaper.isNotEmpty(it)) {
                save(KEY_LAST_WALLPAPER, it.url)
                save(KEY_LAST_WALLPAPER_INFO, it.copyright)
                wallpaperCopyrightView.text = it.copyright
                loadWallpaper(it.url)
            }
        }
    }

    private fun loadWallpaper(url: String) {
        if (TextUtils.isEmpty(url)) {
            wallpaperView.setImageDrawable(null)
            blurWallpaperView.setImageDrawable(null)
            return
        }
        Glide.with(this).load(url).into(wallpaperView)
        Glide.with(this).load(url).apply(
            RequestOptions.bitmapTransform(GlideBlurTransformation(this))
        ).into(blurWallpaperView)
    }

    private fun bindHeadImages() {
        appBarLayout.addOnOffsetChangedListener(
            AppBarLayout.BaseOnOffsetChangedListener<AppBarLayout> {
                layout, verticalOffset ->
                val maxOffset = layout.totalScrollRange - toolbarHeight - windowInset.top
                blurWallpaperView.alpha = (verticalOffset * -1F / maxOffset).range(0F, 1F)
        })
    }

    private fun initInsetCallback(group: View) {
        val attributes = window.attributes
        attributes.systemUiVisibility = (
                attributes.systemUiVisibility or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.clearFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = 0
        window.navigationBarColor = 0

        group.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        group.fitsSystemWindows = true
        group.setOnApplyWindowInsetsListener { _, insets ->
            onInsetsChange(insets.systemWindowInsetLeft, insets.systemWindowInsetTop,
                insets.systemWindowInsetRight, insets.systemWindowInsetBottom)
            insets.consumeSystemWindowInsets()
        }
    }

    protected open fun onInsetsChange(left: Int, top: Int, right: Int, bottom: Int) {
        windowInset.set(left, top, right, bottom)
        toolbar.layoutParams = (toolbar.layoutParams as
                CollapsingToolbarLayout.LayoutParams).apply {
            setMargins(left, top, right, 0)
        }
        contentGroup.setPadding(left, 0, right, 0)
        floatingGroup.setPadding(left, top, right, bottom)
    }

    protected fun startLoading() {
        isLoading = true
        contentLoading.show()
    }

    protected fun stopLoading() {
        isLoading = false
        contentLoading.hide()
    }

    override fun onSkinUpdate(info: NoteSkin) {
        contentLoading.putColorForRes(R.color.colorPrimary, R.color.colorAccent)
    }

}