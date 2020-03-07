package com.lollipop.lnote.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lollipop.lnote.R
import com.lollipop.lnote.info.NoteOverviewInfo
import com.lollipop.lnote.list.NoteOverviewAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_floating.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

/**
 * 主页的Activity
 * @author Lollipop
 */
class MainActivity : BaseActivity(),
    SwipeRefreshLayout.OnRefreshListener{
    override val floatingViewId = R.layout.activity_main_floating
    override val contentViewId = R.layout.activity_main
    override val fullScreenViewId = 0

    private var pageIndex = 0

    private val adapter = NoteOverviewAdapter {
        onNoteInfoClick(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isShowBack = false
        noteList.adapter = adapter
        noteList.layoutManager = LinearLayoutManager(
            this, RecyclerView.VERTICAL, false)
        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary)
        refreshLayout.setOnRefreshListener(this)
        bindFloatingDate()
        onRefresh()
        var isAlert = false
        addNoteBtn.setOnClickListener {
            if (isAlert) {
                alert("测试测试测试", R.drawable.ic_add_black_24dp, "Undo", {
                    Toast.makeText(this, "Haha", Toast.LENGTH_SHORT).show()
                })
            } else {
                notify("测试内容")
            }
            isAlert = !isAlert
        }
    }

    private fun bindFloatingDate() {
        val timerView: View = findViewById(R.id.floatingListTimer)
        val dayView: TextView = findViewById(R.id.dayView)
        val monthView: TextView = findViewById(R.id.monthView)
        noteList.addOnScrollListener(FloatingDateListener(timerView, dayView, monthView, adapter) {
            loadMoreData()
        })
    }

    private fun onNoteInfoClick(info: NoteOverviewInfo) {
        NoteDetailActivity.start(this, info)
    }

    override fun onRefresh() {
        if (isLoading) {
            return
        }
        startLoading()
        refreshLayout.isRefreshing = true
        pageIndex = 0
        val infoList = ArrayList<NoteOverviewInfo>()
        val random = Random()
        for (index in 1..30) {
            val json = JSONObject()
            json.put("TITLE", "TITLE$index")
            json.put("OVERVIEW", "OVERVIEW, OVERVIEW, OVERVIEW, OVERVIEW, OVERVIEW,OVERVIEW$index")
            json.put("EXPENDITURE", index % 2 == 0)
            json.put("INCOME", index % 3 == 0)
            json.put("ALERT", index % 4 == 0)
            infoList.add(NoteOverviewInfo(index, 20200301 + index / 3, random.nextInt(), json.toString()))
        }
        adapter.reset(infoList)
        stopLoading()
        refreshLayout.isRefreshing = false
    }

    private fun loadMoreData() {
        if (isLoading) {
            return
        }
        startLoading()
        pageIndex ++
        val infoList = ArrayList<NoteOverviewInfo>()
        val random = Random()
        for (index in 1..30) {
            val json = JSONObject()
            json.put("LABEL_COLOR", random.nextInt())
            json.put("TITLE", "TITLE$index")
            json.put("OVERVIEW", "OVERVIEW, OVERVIEW, OVERVIEW, OVERVIEW, OVERVIEW,OVERVIEW$index")
            json.put("EXPENDITURE", index % 2 == 0)
            json.put("INCOME", index % 3 == 0)
            json.put("ALERT", index % 4 == 0)
            infoList.add(NoteOverviewInfo(index, 20200301 + index / 3, random.nextInt(), json.toString()))
        }
        adapter.addData(infoList)
        stopLoading()
    }

    private class FloatingDateListener(
        private val timerView: View,
        private val dayView: TextView,
        private val monthView: TextView,
        private val adapter: NoteOverviewAdapter,
        private val loadMore: () -> Unit): RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            (recyclerView.layoutManager as? LinearLayoutManager)?.let { layoutManager ->
                val lastItem = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (lastItem < 1) {
                    hideTimer()
                    return
                }
                val fastInfo = adapter.getItem(lastItem - 1)?:return hideTimer()
                timerView.visibility = View.VISIBLE
                dayView.text = fastInfo.dayStr
                monthView.text = fastInfo.monthStr
                if (adapter.isShowDate(lastItem)) {
                    val top = layoutManager.findViewByPosition(lastItem)?.top?:0
                    val height = timerView.height * 1F
                    timerView.translationY = if (top > height) { 0F } else { top - height }
                } else {
                    timerView.translationY = 0F
                }

                if (adapter.itemCount - layoutManager.findLastVisibleItemPosition() < 5) {
                    loadMore()
                }
            }
        }

        private fun hideTimer() {
            timerView.visibility = View.INVISIBLE
        }

    }

}
