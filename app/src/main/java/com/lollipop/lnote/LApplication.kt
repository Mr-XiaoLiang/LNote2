package com.lollipop.lnote

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.lollipop.lnote.skin.ActivitySkin
import com.lollipop.lnote.skin.NoteSkin
import com.lollipop.skin.SkinHelper

/**
 * @author lollipop
 * @date 2020-02-24 00:06
 * 应用上下文
 */
class LApplication: Application(), Application.ActivityLifecycleCallbacks {

    private val skinHelper = SkinHelper<NoteSkin>()

    private val activitySkin = NoteSkin()

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStarted(activity: Activity) {
        if (activity is ActivitySkin) {
            activity.onSkinUpdate(activitySkin)
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (activity is ActivitySkin) {
            skinHelper.unregistered(activity)
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activity is ActivitySkin) {
            skinHelper.registered(activity)
        }
    }

    override fun onActivityResumed(activity: Activity) {

    }

}