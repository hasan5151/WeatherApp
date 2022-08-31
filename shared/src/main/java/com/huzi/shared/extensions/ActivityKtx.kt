package com.huzi.shared.extensions

import android.app.Activity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.huzi.shared.R
import java.lang.IllegalStateException

fun Activity.fetchColor(@ColorRes colorResId: Int): Int = ContextCompat.getColor(this, colorResId)

fun Activity.showToast(@StringRes toastMessage: Int, isError: Boolean = true) {
    try {
        val layout =
            layoutInflater.inflate(R.layout.per_custom_msg, findViewById(R.id.cons), false)
        val text = layout.findViewById<TextView>(R.id.textView)
        val view = layout.findViewById<View>(R.id.view)
        if (isError) {
            view.setBackgroundResource(R.drawable.xml_half_circle_red)
        } else
            view.setBackgroundResource(R.drawable.xml_half_circle)

        text.text = getString(toastMessage)
        val toast = Toast(this)
        if (isError) {
            toast.duration = Toast.LENGTH_LONG
        } else
            toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        if (!isFinishing)
            toast.show()

    } catch (e: IllegalStateException) {
        e.printStackTrace()
    }
}