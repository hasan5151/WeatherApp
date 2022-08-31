package com.huzi.shared.extensions

import android.view.animation.Animation

/**
 * Set an action which will be invoked when the animation has ended.
 *
 * @return the [Animation.AnimationListener] set to the Animation
 */

inline fun Animation.doOnEnd(crossinline action: (animation: Animation) -> Unit) =
        setAnimationListener(onEnd = action)

/**
 * Set an action which will be invoked when the animation has started.
 *
 * @return the  [Animation.AnimationListener] set to the Animation
 * @see Animation.start
 */

inline fun Animation.doOnStart(crossinline action: (animation: Animation) -> Unit) =
        setAnimationListener(onStart = action)


/**
 * Add an action which will be invoked when the animation has repeated.
 *
 * @return the [Animation.AnimationListener]set to the Animator
 */

inline fun Animation.doOnRepeat(crossinline action: (animation: Animation) -> Unit) =
        setAnimationListener(onRepeat = action)


/**
 * Set listener to this Animation using the provided actions.
 *
 * @return the [Animation.AnimationListener] added to the Animation
 */
inline fun Animation.setAnimationListener(
        crossinline onEnd: (animation: Animation) -> Unit = {},
        crossinline onStart: (animation: Animation) -> Unit = {},
        crossinline onRepeat: (animation: Animation) -> Unit = {}
): Animation.AnimationListener {
    val listener = object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation) = onRepeat(animation)
        override fun onAnimationEnd(animation: Animation) = onEnd(animation)
        override fun onAnimationStart(animation: Animation) = onStart(animation)
    }
    setAnimationListener(listener)
    return listener
}