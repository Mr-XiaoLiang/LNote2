package com.lollipop.lnote.util

import android.text.TextUtils
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject


/**
 * @author lollipop
 * @date 2020/2/27 00:23
 * 必应壁纸的工具
 */
object BingWallpaper {

    private const val WALLPAPER_URL_HEAD = "https://cn.bing.com"
    private const val WALLPAPER_URL = "$WALLPAPER_URL_HEAD/HPImageArchive.aspx?format=js&idx=0&n=1"

    private val EMPTY_INFO = WallpaperInfo("", "")

    fun isEmpty(info: WallpaperInfo): Boolean {
        if (EMPTY_INFO == info) {
            return true
        }
        if (TextUtils.isEmpty(info.url)) {
            return true
        }
        if (!info.url.startsWith(WALLPAPER_URL_HEAD)) {
            return true
        }
        return false
    }

    fun isNotEmpty(info: WallpaperInfo): Boolean {
        return !isEmpty(info)
    }

    fun request(resultCallback: (WallpaperInfo) -> Unit) {
        doAsync ({
            onUI {
                resultCallback.invoke(EMPTY_INFO)
            }
        }) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(WALLPAPER_URL).build()
            client.newCall(request).execute().let { response ->
                val result = getUrlFromJson(response.body?.string())
                onUI {
                    resultCallback.invoke(result)
                }
            }
        }
    }

    private fun getUrlFromJson(json: String?): WallpaperInfo {
        json?:return EMPTY_INFO
        if (TextUtils.isEmpty(json)) {
            return EMPTY_INFO
        }
        try {
            JSONObject(json)
                .optJSONArray("images")
                ?.optJSONObject(0)
                ?.let { image ->
                return WallpaperInfo(WALLPAPER_URL_HEAD + image.optString("url"),
                    image.optString("copyright"))
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return EMPTY_INFO
    }

    data class WallpaperInfo(val url: String, val copyright: String)

}