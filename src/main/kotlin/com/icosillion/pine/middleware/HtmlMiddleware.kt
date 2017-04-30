package com.icosillion.pine.middleware

import com.icosillion.pine.http.Request
import com.icosillion.pine.http.Response

/**
 * Provides a very simple HTML middleware layer
 */
class HtmlMiddleware : Middleware {
    override fun before(request: Request, response: Response, next: MiddlewareChain): Response {
        response.headers.putIfAbsent("Content-Type", "text/html")

        return next(request, response)
    }

    override fun after(request: Request, response: Response, next: MiddlewareChain): Response {
        return next(request, response)
    }
}