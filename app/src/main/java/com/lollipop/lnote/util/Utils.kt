package com.lollipop.lnote.util

import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
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
