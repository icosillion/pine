package com.icosillion.pine.responses.modifiers

import com.google.gson.JsonElement
import com.icosillion.pine.http.Response

/**
 * Encodes and writes JSON to the body and sets the Content-Type header to application/json
 */
fun Response.withJson(json: JsonElement): Response {

    this.headers["Content-Type"] = "application/json"
    this.body = json.toString()

    return this
}

/**
 * Writes JSON to the body and sets the Content-Type header to application/json
 */
fun Response.withJson(json: String): Response {

    this.headers["Content-Type"] = "application/json"
    this.body = json

    return this
}