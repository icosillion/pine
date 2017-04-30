package com.icosillion.pine.http

/**
 * Models HTTP methods
 */
enum class Method {

    ANY, GET, POST, PUT, DELETE, HEAD, TRACE, OPTIONS, CONNECT, PATCH;

    companion object Factory {
        fun fromString(strMethod:String):Method? {
            Method.values().forEach { method ->
                if(method.toString().equals(strMethod, true)) {
                    return method;
                }
            }

            return null;
        }
    }
}