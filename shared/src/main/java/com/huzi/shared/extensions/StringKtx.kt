package com.huzi.shared.extensions

fun String.contains(vararg items: String, ignoreCase: Boolean = true): Boolean {
    for (item in items) {
        if (contains(item, ignoreCase))
            return true
    }
    return false
}