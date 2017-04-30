package com.icosillion.pine.responses.modifiers

import com.icosillion.pine.http.Response

/**
 * Writes text to the body and sets Content-Type to text/plain
 */
fun Response.withText(text: String): Response {
    this.body = text

    this.headers["Content-Type"] = "text/plain"

    return this
}