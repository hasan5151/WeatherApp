package com.huzi.shared.extensions

import android.view.Menu
import android.view.MenuItem
import androidx.annotation.IdRes

/** Returns `true` if [itemId] is found in this menu. */
fun Menu.containsId(@IdRes itemId: Int): Boolean {
    for (index in 0 until size()) {
        if (getItem(index).itemId == itemId) {
            return true
        }
    }
    return false
}