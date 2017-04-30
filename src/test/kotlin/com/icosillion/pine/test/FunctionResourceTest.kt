package com.icosillion.pine.test

import com.icosillion.pine.Pine
import com.icosillion.pine.http.Method
import com.icosillion.pine.http.Request
import com.icosillion.pine.http.Response
import com.icosillion.pine.responses.modifiers.withText
import com.icosillion.pine.test.harness.TestablePine
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.*
import org.junit.Assert.*

private fun setup(): Pine {
    val pine = TestablePine()

    pine.get("/test", fun(request, response): Response {
        return response.withText("This is only a test")
    })

    pine.testingPreflight()

    return pine
}

class FunctionResourceTest : Spek({
    describe("A functional resource") {
        val pine = setup()

        it("Should respond to a GET request to /test") {
            val response = pine.handleRequest(Request(mapOf(), "", mapOf(), "/test", Method.GET, hashMapOf()))

            assertEquals(200, response.status)
            assertEquals("This is only a test", response.body)
            assertEquals("text/plain", response.headers["Content-Type"]!!)
        }

        it("Should not respond to other routes") {
            val response = pine.handleRequest(Request(mapOf(), "", mapOf(), "/", Method.GET, hashMapOf()))

            assertEquals(404, response.status)
            assertNotEquals("This is only a test", response.body)
        }
    }
})