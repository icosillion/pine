package com.icosillion.pine.middleware

import com.icosillion.pine.http.Request
import com.icosillion.pine.http.Response

/**
 * Interface for Pine Middleware
 */
interface Middleware {
    fun before(request: Request, response: Response, next: MiddlewareChain): Response
    fun after(request: Request, response: Response, next: MiddlewareChain): Response
}

/**
 * Helper class for sequential middleware execution
 */
class MiddlewareChain(val type: Type, val middlewares: List<Middleware>) {

    enum class Type {
        BEFORE, AFTER
    }

    private val iterator: Iterator<Middleware>

    init {
        this.iterator = middlewares.iterator()
    }

    /**
     * Executes the next middleware item in the chain
     */
    fun next(request: Request, response: Response): Response {
        if (this.iterator.hasNext()) {
            if (type == Type.BEFORE) {
                return this.iterator.next().before(request, response, this)
            } else {
                return this.iterator.next().after(request, response, this)
            }
        }

        return response
    }

    /**
     * Starts the execution of a middleware chain
     */
    fun start(request: Request, response: Response): Response {
        return this.next(request, response)
    }

    /**
     * Enables execution of this object as an alias to the next command
     */
    operator fun invoke(request: Request, response: Response): Response {
        return this.next(request, response)
    }
}