package com.icosillion.pine.http

import java.util.*

/**
 * Models an HTTP Response
 */
open class Response(var headers:HashMap<String, String> = hashMapOf(),
                           var status:Int = 200,
                           var body:Any = "")
{

    var storage = hashMapOf<String, Any>()

    fun merge(response: Response) {
        headers.putAll(response.headers)
        status = response.status
        body = response.body
    }

    fun withHeader(key: String, value: String): Response {
        headers.put(key, value)

        return this
    }

    fun withStatus(status: Int): Response {
        this.status = status

        return this
    }
}