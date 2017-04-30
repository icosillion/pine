package com.icosillion.pine.responses.modifiers

import com.icosillion.pine.http.Response

/**
 * Writes HTML to the body and sets the Content-Type header to text/html
 */
fun Response.withHtml(html: String): Response {
    this.body = html

    this.headers["Content-Type"] = "text/html"

    return this
}