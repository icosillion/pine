package com.icosillion.pine.helpers

import com.google.gson.Gson

private val gson = Gson()

/**
 * Simple GSON wrapper to provide easy JSON operations
 */
class Json {
    companion object {

        /**
         * Encodes an object to a JSON String
         */
        fun encode(obj:Any): String {
            return gson.toJson(obj)
        }

        /**
         * Decodes a JSON Object String to a Map
         */
        fun decodeObject(json:String): Map<String, Any> {
            return gson.fromJson(json, Map::class.java) as Map<String, Any>
        }

        /**
         * Decodes a JSON Array String to a List
         */
        fun decodeArray(json:String): List<Any> {
            return gson.fromJson(json, List::class.java) as List<Any>
        }
    }
}