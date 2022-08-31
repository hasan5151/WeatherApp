package com.huzi.shared.extensions

import android.os.Build
import android.text.Html
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes

fun TextView.setHtmlText(@StringRes resId: Int) {
    this.context?.let { cnt ->
        val htmlString = cnt.getString(resId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.text = Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            this.text = Html.fromHtml(htmlString)
        }
    }
}

fun TextView.setHtmlText(@StringRes resId: Int, vararg args: Any) {
    this.context?.let { cnt ->
        val htmlString = cnt.getString(resId, *args)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.text = Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            this.text = Html.fromHtml(htmlString)
        }
    }
}

fun TextView.setColorRes(@ColorRes color: Int) = setTextColor(context.fetchColor(color))