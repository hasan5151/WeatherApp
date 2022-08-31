package com.huzi.shared.moshi

import com.squareup.moshi.*

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class NullPrimitive

class NullPrimitiveAdapter {

    @FromJson
    fun fromBooleanJson(@NullPrimitive reader: JsonReader, delegate: JsonAdapter<Boolean>): Boolean {
        if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull<Unit>()
            return false
        }
        return delegate.fromJson(reader)!!
    }

    @ToJson
    fun toBooleanJson(@NullPrimitive name: Boolean): Boolean {
        return name
    }

    @FromJson
    fun fromIntJson(@NullPrimitive reader: JsonReader, delegate: JsonAdapter<Int>): Int {
        if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull<Unit>()
            return 0
        }
        return delegate.fromJson(reader)!!
    }

    @ToJson
    fun toIntJson(@NullPrimitive name: Int): Int {
        return name
    }

    @FromJson
    fun fromLongJson(@NullPrimitive reader: JsonReader, delegate: JsonAdapter<Long>): Long {
        if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull<Unit>()
            return 0L
        }
        return delegate.fromJson(reader)!!
    }

    @ToJson
    fun toLongJson(@NullPrimitive name: Long): Long {
        return name
    }

    @FromJson
    fun fromStringJson(@NullPrimitive reader: JsonReader, delegate: JsonAdapter<String>): String {
        if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull<Unit>()
            return ""
        }
        return delegate.fromJson(reader)!!
    }

    @ToJson
    fun toStringJson(@NullPrimitive name: String): String {
        return name
    }

    @FromJson
    fun fromFloatJson(@NullPrimitive reader: JsonReader, delegate: JsonAdapter<Float>): Float {
        if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull<Unit>()
            return 0.0f
        }
        return delegate.fromJson(reader)!!
    }

    @ToJson
    fun toFloatJson(@NullPrimitive name: Float): Float {
        return name
    }

    @FromJson
    fun fromDoubleJson(@NullPrimitive reader: JsonReader, delegate: JsonAdapter<Double>): Double {
        if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull<Unit>()
            return 0.0
        }
        return delegate.fromJson(reader)!!
    }

    @ToJson
    fun toDoubleJson(@NullPrimitive name: Double): Double {
        return name
    }
}
