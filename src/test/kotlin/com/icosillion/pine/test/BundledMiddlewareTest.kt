package com.icosillion.pine.test

import com.icosillion.pine.Pine
import com.icosillion.pine.annotations.Route
import com.icosillion.pine.annotations.UseNamed
import com.icosillion.pine.http.Method
import com.icosillion.pine.http.Request
import com.icosillion.pine.http.Response
import com.icosillion.pine.middleware.HtmlMiddleware
import com.icosillion.pine.middleware.Middleware
import com.icosillion.pine.middleware.MiddlewareChain
import com.icosillion.pine.responses.modifiers.withText
import com.icosillion.pine.test.harness.TestablePine
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.*
import org.junit.Assert.*

class CatsMiddleware : Middleware {

    override fun before(request: Request, response: Response, next: MiddlewareChain): Response {
        return next(request, response)
    }

    override fun after(request: Request, response: Response, next: MiddlewareChain): Response {
        response.body = response.body as String + " with cats!"

        return next(request, response)
    }
}

class TestResource {

    @Route("/")
    @UseNamed("test")
    fun handle(request: Request, response: Response): Response {
        response.withText("Hello world")

        return response
    }
}

private fun setup(): Pine {
    val pine = TestablePine()
    pine.bundledMiddleware("test", arrayOf(HtmlMiddleware(), CatsMiddleware()))
    pine.resource(TestResource())

    pine.testingPreflight()

    return pine
}

class BundledMiddlewareTest : Spek({
    describe("A simple middleware bundle") {
        val pine = setup()

        it("should apply a middleware bundle to a response") {
            val response = pine.handleRequest(Request(mapOf(), "", mapOf(), "/", Method.GET))

            assertEquals("Hello world with cats!", response.body)
            assertEquals("text/plain", response.headers["Content-Type"])
        }
    }
})