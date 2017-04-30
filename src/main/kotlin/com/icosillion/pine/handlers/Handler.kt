package com.icosillion.pine.handlers

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * A handler is a lower-level way to handle requests. They can be used alongside the regular routing framework.
 * The primary use for handlers are things that need to be streamed to the client, such as serving assets.
 */
interface Handler {
    fun handle(request:HttpServletRequest, response: HttpServletResponse):Boolean
}