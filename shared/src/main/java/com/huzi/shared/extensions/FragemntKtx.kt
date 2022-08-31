package com.huzi.shared.extensions

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import java.util.concurrent.Executor

@MainThread
fun Fragment.navigate(directions: NavDirections?) {
    try {
        if (directions == null) return
        this.findNavController().navigate(directions)
    } catch (ex: IllegalArgumentException) {
        Log.e("FragmentKtx", "navigate Exception:", ex)
    }
}

@MainThread
fun Fragment.navigate(@IdRes resId: Int, navOptions: NavOptions) {
    try {
        this.findNavController().navigate(resId, null, navOptions)
    } catch (exception: java.lang.IllegalArgumentException) {
        Log.e("FragmentKtx", "navigate Exception:", exception)
    }
}

@MainThread
fun Fragment.navigate(@IdRes resId: Int) {
    try {
        this.findNavController().navigate(resId)
    } catch (exception: java.lang.IllegalArgumentException) {
        Log.e("FragmentKtx", "navigate Exception:", exception)
    }
}

@MainThread
fun Fragment.navigateUp() {
    try {
        this.findNavController().navigateUp()
    } catch (ex: IllegalArgumentException) {
        Log.e("FragmentKtx", "navigateUp Exception:", ex)
    }
}

fun Fragment.changeNavigationBarColor(@ColorRes colorResId: Int) {
    activity?.window?.navigationBarColor = fetchColor(colorResId)
}

val Fragment.mainExecutor: Executor
    get() {
        return ContextCompat.getMainExecutor(requireContext())
    }

private fun showKeyboard(view: View) {
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
}

private fun dismissKeyboard(view: View) {
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.showKeyboard(view: View) {
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
}

fun Fragment.dismissKeyboard(view: View) {
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.fetchColor(@ColorRes colorResId: Int): Int = ContextCompat.getColor(requireContext(), colorResId)

fun Fragment.hasPermissions(vararg permissions: String): Boolean = permissions.all {
    ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
}

fun Fragment.shouldRationale(vararg permissions: String): Boolean {
    for (perm in permissions) {
        if (shouldShowRequestPermissionRationale(perm)) {
            return true
        }
    }
    return false
}

fun Fragment.fetchRationale(vararg permissions: String): Array<String> {
    val result = HashSet<String>()
    permissions.forEach {
        if (shouldShowRequestPermissionRationale(it)) {
            result.add(it)
        }
    }
    return result.toTypedArray()
}

