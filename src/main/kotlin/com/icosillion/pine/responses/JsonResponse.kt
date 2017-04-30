package com.icosillion.pine.responses

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.icosillion.pine.http.Response

/**
 * Simple JSON Response
 */
class JsonResponse(status:Int = 200)
    : Response(hashMapOf(Pair("Content-Type", "application/json")), status, "{}") {

    constructor(jsonObject: JsonObject, status:Int = 200) : this(status) {
        this.body = jsonObject.toString()
    }

    constructor(jsonArray: JsonArray, status:Int = 200) : this(status) {
        this.body = jsonArray.toString()
    }
}