package com.icosillion.pine

import com.icosillion.pine.http.Method
import com.icosillion.pine.http.Request
import org.apache.commons.io.IOUtils
import java.util.*
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * A Servlet which can be loaded into a Java Application Server
 */
class PineServlet(private val pine:Pine) : HttpServlet() {

    override fun service(req: HttpServletRequest?, resp: HttpServletResponse?) {
        if(req == null || resp == null)
            return

        //Pass to any additional handlers
        pine.handlers.forEach { handler ->
            //If this request has been handled, finish.
            if(handler.handle(req, resp)) {
                return@forEach
            }
        }

        //Parse Query Parameters
        val params = hashMapOf<String, String>()
        req.parameterMap.forEach { param ->
            params.put(param.key, param.value[0])
        }

        //TODO Handle Proper Encoding
        //TODO Handle Max Body Length
        val body = IOUtils.toString(req.inputStream)

        val headers = hashMapOf<String, String>()
        req.headerNames.asSequence().forEach { key ->
            headers.put(key, req.getHeader(key))
        }

        //Route
        val method = Method.fromString(req.method) ?: Method.ANY
        val pineRequest = Request(params as Map<String, String>, body, headers, req.requestURI, method)

        val pineResponse = pine.handleRequest(pineRequest)

        resp.status = pineResponse.status
        resp.contentType = pineResponse.headers.getOrElse("Content-Type") { "application/json" }

        //Setup Response Headers
        pineResponse.headers.forEach { header ->
            resp.addHeader(header.key, header.value)
        }

        val writer = resp.writer
        writer.write(pineResponse.body.toString())
        writer.close()
    }
}