package com.icosillion.pine.responses

import com.icosillion.pine.http.Response

/**
 * Simple Text Response
 */
class TextResponse(val content: String)
    : Response(hashMapOf(Pair("Content-Type", "text/plain")), 200, content)