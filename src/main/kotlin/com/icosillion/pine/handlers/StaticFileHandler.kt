package com.icosillion.pine.handlers

import com.icosillion.pine.RouteDefinition
import com.icosillion.pine.http.Method
import net.sf.jmimemagic.Magic
import net.sf.jmimemagic.MagicMatchNotFoundException
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileReader
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * A Handler that can stream files to clients
 */
class StaticFileHandler(var endpoint:String, var directory:String) : Handler {

    init {
        if(!endpoint.endsWith("/")) {
            endpoint += "/"
        }

        if(directory.isEmpty()) {
            directory = File("").absolutePath
        }

        if(!directory.startsWith("/")) {
            directory = File("").absolutePath + "/" + directory
        }

        if(!directory.endsWith("/")) {
            directory += "/"
        }
    }

    override fun handle(request: HttpServletRequest, response: HttpServletResponse): Boolean {

        val route = RouteDefinition(endpoint + "*", arrayOf(Method.GET), arrayOf("*"))
        val method = Method.fromString(request.method) ?: Method.GET

        if(route.matches(request.requestURI, method)) {
            val path = request.requestURI.substring(endpoint.length)
            val file = File(directory + path)
            val absPath = file.absolutePath

            //Funny stuff is happening, abort!
            if(!absPath.startsWith(directory))
                return false

            //Check file exists
            if(!file.exists())
                return false

            //Setup MIME Type
            try {
                response.contentType = Magic.getMagicMatch(file, true).mimeType
            } catch(ex:MagicMatchNotFoundException) {
                response.contentType = "application/octet-stream"
            }

            //Copy file to output
            FileReader(file).use { reader ->
                IOUtils.copy(reader, response.outputStream)
            }

            return true
        }

        return false
    }
}