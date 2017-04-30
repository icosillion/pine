package com.icosillion.pine.middleware

import com.icosillion.pine.http.Request
import com.icosillion.pine.http.Response
import com.icosillion.pine.responses.JsonProblemResponse
import java.util.*

/**
 * Provides HTTP Basic Authentication
 */
class BasicAuthMiddleware(
        val realm: String = "API",
        private val authFunction: (username: String, password: String) -> Boolean
) : Middleware {

    override fun before(request: Request, response: Response, next: MiddlewareChain): Response {
        if(request.headers.containsKey("Authorization")) {
            var authHeader = request.headers["Authorization"]!!
            if(!authHeader.startsWith("Basic ")) {
                return notAuthedResponse(response)
            }

            authHeader = authHeader.substring("Basic ".length)
            authHeader = authHeader.trim()

            //Decode
            authHeader = String(Base64.getDecoder().decode(authHeader))

            //Split
            val authBits = authHeader.split(":")
            if(authBits.size != 2) {
                notAuthedResponse(response)
            }

            val username = authBits[0]
            val password = authBits[1]

            //Pass through auth function
            if(!authFunction(username, password)) {
                return notAuthedResponse(response)
            }

            //Set Request Storage Entries
            request.storage["auth:username"] = username
            request.storage["auth:password"] = password
        } else {
            return notAuthedResponse(response)
        }

        return next(request, response)
    }

    override fun after(request: Request, response: Response, next: MiddlewareChain): Response {
        return next(request, response)
    }

    private fun notAuthedResponse(response: Response): Response {
        response.headers["WWW-Authenticate"] = "Basic realm=\"$realm\""
        response.merge(JsonProblemResponse(401, "Unauthorized"))

        return response
    }

}