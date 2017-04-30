package com.icosillion.pine.helpers

import com.google.gson.JsonElement

/**
 * Provides an interface that classes can use to declare themselves serializable to JSON
 */
interface JsonSerializable {

    fun jsonSerialize(): JsonElement
}