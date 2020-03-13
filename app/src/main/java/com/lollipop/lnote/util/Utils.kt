package com.lollipop.lnote.util

import android.content.Context
import android.graphics.PointF
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.math.abs
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

fun String.numberFormat(length: Int = 2): String {
    if (this.length >= length) {
        return this
    }
    if (this.length == length - 1) {
        return "0$this"
    }
    val builder = StringBuilder(this)
    while (builder.length < length) {
        builder.insert(0, "0")
    }
    return builder.toString()
}

fun Context.compatColor(id: Int): Int {
    return ContextCompat.getColor(this, id)
}

fun View.compatColor(id: Int): Int {
    return context.compatColor(id)
}

class ClickHelper private constructor(private val view: View) {

    // 连击次数
    private var pointSize = 0
    // 按下时间，以此来判断区分点击和长按
    private var touchDownTime = 0L
    // 最大波动范围（手指抖动范围，规避滑动行为）
    var maxFluctuation = -1
    // 按下位置
    private val touchDownPoint = PointF()
    // 是否激活本次点击
    private var active = false
    // 单次点击允许的最长手指按下时间
    var maxKeepTime = 300L
    // 连击允许的超时时间
    var continuouslyKeepTime = 50L
    // 点击事件任务
    private val clickTask = Runnable {
        callOnClick()
    }

    companion object {
        fun create(view: View): ClickHelper {
            return ClickHelper(view)
        }
    }

    private val clickListenerList = ArrayList<ClickListener>()

    fun onTouchEvent(event: MotionEvent) {
        when(event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // 按下，记录信息
                touchDownTime = System.currentTimeMillis()
                touchDownPoint.set(event.x, event.y)
                active = true
            }
            MotionEvent.ACTION_MOVE -> {
                if (!active) {
                    return
                }
                // 发生移动，检查移动范围
                val x = event.x
                val y = event.y
                if (maxFluctuation > 0 && (abs(x - touchDownPoint.x) > maxFluctuation ||
                            abs(y - touchDownPoint.y) > maxFluctuation)) {
                    reset()
                    return
                }
                if (x < 0 || y < 9 || x > view.width || y > view.height) {
                    reset()
                    return
                }
                // 发生超时，提前清理任务
                val now = System.currentTimeMillis()
                if (now > maxKeepTime) {
                    reset()
                    return
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!active) {
                    return
                }
                val now = System.currentTimeMillis()
                if (now > maxKeepTime) {
                    reset()
                    return
                }
                clickSuccessful()
            }
            MotionEvent.ACTION_CANCEL -> {
                // 触发取消时间，放弃本轮所有计数
                reset()
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                // 多个指头，放弃事件
                reset()
            }
        }
    }

    private fun callOnClick() {
        if (pointSize < 1) {
            return
        }
        clickListenerList.forEach {
            it.onClick(view, pointSize)
        }
        // 事件被消耗，清空
        reset()
    }

    private fun clickSuccessful() {
        active = false
        pointSize++
        view.removeCallbacks(clickTask)
        view.postDelayed(clickTask, maxKeepTime + continuouslyKeepTime)
    }

    private fun reset() {
        pointSize = 0
        view.removeCallbacks(clickTask)
        touchDownTime = 0L
        touchDownPoint.set(0F, 0F)
        active = false
    }

    fun addClickListener(listener: ClickListener) {
        clickListenerList.add(listener)
    }

    interface ClickListener {
        fun onClick(view: View, count: Int)
    }

}
