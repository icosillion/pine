package com.icosillion.pine.middleware

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.icosillion.pine.http.Request
import com.icosillion.pine.http.Response
import com.icosillion.pine.responses.JsonProblemResponse

/**
 * Parses incoming JSON data and encodes outgoing JSON
 */
class JsonMiddleware : Middleware {

    override fun before(request: Request, response: Response, next: MiddlewareChain): Response {
        response.headers.putIfAbsent("Content-Type", "application/json")

        if(request.headers.containsKey("Content-Type")
           && request.headers["Content-Type"].equals("application/json")
            && request.body is String
        ) {
            try {
                val parser = JsonParser()
                request.body = parser.parse(request.body as String)
            } catch(ex: JsonParseException) {
                response.merge(JsonProblemResponse(400, "Invalid Json Body"))
                return response
            }
        }

        return next(request, response)
    }

    override fun after(request: Request, response: Response, next: MiddlewareChain): Response {
        if(response.body is JsonObject) {
            val jsonObject = response.body as JsonObject
            response.body = jsonObject.toString()
        } else if(response.body is JsonArray) {
            val jsonArray = response.body as JsonArray
            response.body = jsonArray.toString()
        }

        return next(request, response)
    }
}