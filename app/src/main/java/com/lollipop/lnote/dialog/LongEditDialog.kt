package com.lollipop.lnote.dialog

import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.lollipop.lnote.R

/**
 * @author lollipop
 * @date 2020/3/7 14:15
 * 长编辑面板的对话框
 */
class LongEditDialog(selfRegistration: SelfRegistration,
                     group: ViewGroup, override val onceDialog: Boolean):
    TopPanelDialog(selfRegistration, group) {

    override val layoutId = R.layout.fragment_long_edit

    private var textValue: CharSequence = ""

    private var onCompleteListener: ((CharSequence) -> Unit)? = null

    private val inputView: TextView?
        get() {
            return find(R.id.inputText)
        }

    override fun onViewCreate(contentView: View) {
        super.onViewCreate(contentView)
        tryView<View>(R.id.completeBtn) {
            it.setOnClickListener {
                onCompleteListener?.invoke(inputView?.text?:"")
                doHide()
            }
        }
        tryView<View>(R.id.revokeBtn) {
            it.setOnClickListener {
                doHide()
            }
        }
        inputView?.movementMethod = ScrollingMovementMethod.getInstance();
    }

    override fun onReady() {
        super.onReady()
        inputView?.let {
            it.text = textValue
            it.requestFocus()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    override fun onHide() {
        super.onHide()
        onCompleteListener = null
        inputView?.let {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0);
        }
    }

    fun show(value: CharSequence, callback: (CharSequence) -> Unit) {
        textValue = value
        onCompleteListener = callback
        doShow()
    }

    override fun onInsetChange(left: Int, top: Int, right: Int, bottom: Int) {
        contentView?.setPadding(left, top, right, 0)
    }

}