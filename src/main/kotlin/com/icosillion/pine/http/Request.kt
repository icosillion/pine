package com.icosillion.pine.http

import java.util.*

/**
 * Models an HTTP Request
 */
data class Request(var queryParameters:Map<String, String>, var body:Any, var headers:Map<String, String>,
                   var path:String, var method:Method,
                   var pathParameters: HashMap<String, String> = hashMapOf()) {
    var storage = hashMapOf<String, Any>()
}