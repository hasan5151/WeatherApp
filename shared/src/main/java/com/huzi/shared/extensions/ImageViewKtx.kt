package com.huzi.shared.extensions

import android.content.res.Resources
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.huzi.shared.R

fun ImageView.showActionAnim(show: Boolean, callback: ((show: Boolean) -> Unit)? = null) {
    if (!show) {
        if (this.visibility == View.GONE || this.visibility == View.INVISIBLE)
            return
        if (!this.isEnabled)
            return
    }
    val animRes = if (show) R.anim.btn_action_in else R.anim.btn_action_out
    val animation = AnimationUtils.loadAnimation(context, animRes)
    animation.setAnimationListener(
            onStart = {
                if (show) {
                    this.visibility = View.VISIBLE
                }
                this.isEnabled = false
            },
            onEnd = {
                this.isEnabled = true
                this.visible(show)
                callback?.invoke(show)
            }
    )
    if (this.isEnabled) {
        this.startAnimation(animation)
    } else {
        //If last animation not finished wait until it will finish and start
        val currentAnim = this.animation
        currentAnim.doOnEnd {
            this.startAnimation(animation)
        }
    }


}
fun ImageView.setImageFromString(resourceName: String){
    try {
        val id: Int = resources.getIdentifier("a${resourceName}", "drawable", context.packageName)
        setImageResource(id)
    }catch (e : Resources.NotFoundException){
        e.printStackTrace()
    }

}

