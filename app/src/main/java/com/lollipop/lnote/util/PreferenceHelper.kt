package com.lollipop.lnote.util

import android.content.Context
import android.text.TextUtils
import android.util.ArraySet
import android.view.View

/**
 * @author lollipop
 * @date 2020/2/27 01:03
 * 偏好设置项目
 */
object PreferenceHelper {

    inline fun <reified T> Context.save(key: String, value: T) {
        if (TextUtils.isEmpty(key)) {
            return
        }
        val preference = this.getSharedPreferences(key, Context.MODE_PRIVATE).edit()
        when (value) {
            is String -> {
                preference.putString(key, value)
            }
            is Int -> {
                preference.putInt(key, value)
            }
            is Boolean -> {
                preference.putBoolean(key, value)
            }
            is Float -> {
                preference.putFloat(key, value)
            }
            is Double -> {
                preference.putFloat(key, value.toFloat())
            }
            is Long -> {
                preference.putLong(key, value)
            }
            is Set<*> -> {
                if (value.isEmpty()) {
                    preference.putStringSet(key, ArraySet<String>())
                } else {
                    val set = ArraySet<String>()
                    for (v in value) {
                        when (v) {
                            null -> {
                                set.add("")
                            }
                            is String -> {
                                set.add(v)
                            }
                            else -> {
                                set.add(v.toString())
                            }
                        }
                    }
                }
            }
            else -> {
                preference.putString(key, value.toString())
            }
        }
        preference.apply()
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> Context.take(key: String, def: T): T {
        if (TextUtils.isEmpty(key)) {
            return def
        }
        val preference = this.getSharedPreferences(key, Context.MODE_PRIVATE)
        val value = when (def) {
            is String -> {
                preference.getString(key, def)
            }
            is Int -> {
                preference.getInt(key, def)
            }
            is Boolean -> {
                preference.getBoolean(key, def)
            }
            is Float -> {
                preference.getFloat(key, def)
            }
            is Long -> {
                preference.getLong(key, def)
            }
            is Set<*> -> {
                preference.getStringSet(key, def as Set<String>)
            }
            else -> def
        }?:def
        return value as T
    }

    inline fun <reified T> View.save(key: String, value: T) {
        this.context.save(key, value)
    }

    inline fun <reified T> View.take(key: String, value: T): T {
        return this.context.take(key, value)
    }

}