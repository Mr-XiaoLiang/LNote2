package com.lollipop.lnote.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.math.min

/**
 * @author lollipop
 * @date 2020-02-25 00:20
 */

private const val LOG_TAG = "Lollipop"
private const val MAX_LOG = 512

fun Any.log(value: String) {
    logger()(value)
}

fun Any.logger(tag: String = ""): (String) -> Unit {
    val keyword = myName() + if (tag.isEmpty()) {
        ""
    } else {
        "-$tag"
    }
    return { value ->
        langLog(value) { subStr ->
            Log.d(LOG_TAG, "$keyword: $subStr")
        }
    }
}

private fun langLog(value: String, run: (String) -> Unit) {
    if (value.length <= MAX_LOG) {
        run(value)
        return
    }
    var index = 0
    val count = value.length
    while (index < count) {
        val length = min(index + MAX_LOG, count)
        run(value.substring(index, index + length))
        index += length
    }
}

fun Any.myName(): String {
    return this::class.java.simpleName
}

fun Throwable.stackTrace(): String {
    val outputStream = ByteArrayOutputStream()
    val printWriter = PrintWriter(outputStream)
    this.printStackTrace(printWriter)
    return outputStream.toString()
}

fun Float.range(min: Float, max: Float): Float {
    if (this < min) {
        return min
    }
    if (this > max) {
        return max
    }
    return this
}

private val threadPool: Executor by lazy {
    Executors.newCachedThreadPool()
}

private val mainThread: Handler by lazy {
    Handler(Looper.getMainLooper())
}

fun <T: Any> T.doAsync(error: ((Throwable) -> Unit)? = null,
                run: T.() -> Unit) {
    threadPool.execute {
        try {
            run.invoke(this)
        } catch (e: Throwable) {
            error?.invoke(e)
        }
    }
}

fun <T: Any> T.onUI(error: ((Throwable) -> Unit)? = null,
                  run: T.() -> Unit) {
    mainThread.post {
        try {
            run.invoke(this)
        } catch (e: Throwable) {
            error?.invoke(e)
        }
    }
}
