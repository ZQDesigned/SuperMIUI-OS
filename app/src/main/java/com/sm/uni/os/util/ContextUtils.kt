package com.sm.uni.os.util

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

/**
 * 创建一个toast
 *
 * @param textResId 要显示的文本的资源ID
 */
fun Context.toast(@StringRes textResId: Int) {
    Toast.makeText(this, textResId, Toast.LENGTH_SHORT).show()
}

/**
 * 创建一个toast
 *
 * @param text 要显示的文本
 */
fun Context.toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}