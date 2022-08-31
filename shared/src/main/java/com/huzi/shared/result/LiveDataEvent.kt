package com.huzi.shared.result

import androidx.lifecycle.Observer

open class SingleEvent<out T>(private val content: T?) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContent(): T? {
        hasBeenHandled = true
        return content
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T? = content

}


/**
 * An [Observer] for [Event]s, simplifying the pattern of checking if the [Event]'s content has
 * already been handled.
 *
 * [onEvent] is *only* called if the [Event]'s contents has not been handled.
 */
class SingleObserver<T>(private val onEvent: (T?) -> Unit) : Observer<SingleEvent<T?>> {

    override fun onChanged(event: SingleEvent<T?>) {
        if (!event.hasBeenHandled) {
            onEvent.invoke(event.getContent())
        }
    }
}