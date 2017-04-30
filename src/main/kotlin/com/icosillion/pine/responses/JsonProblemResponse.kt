package com.icosillion.pine.responses

import com.github.salomonbrys.kotson.jsonObject
import com.icosillion.pine.http.Response

val titles = mapOf(
        //400 Client Errors
        Pair(400, "Bad Request"),
        Pair(401, "Unauthorized"),
        Pair(402, "Payment Required"),
        Pair(403, "Forbidden"),
        Pair(404, "Not Found"),
        Pair(405, "Method Not Allowed"),
        Pair(406, "Not Acceptable"),
        Pair(407, "Proxy Authentication Required"),
        Pair(408, "Request Timeout"),
        Pair(409, "Conflict"),
        Pair(410, "Gone"),
        Pair(411, "Length Required"),
        Pair(412, "Precondition Failed"),
        Pair(413, "Payload Too Large"),
        Pair(414, "URI Too Long"),
        Pair(415, "Unsupported Media Type"),
        Pair(416, "Range Not Satisfiable"),
        Pair(417, "Expectation Failed"),
        Pair(418, "I'm a Teapot"),
        Pair(421, "Misdirected Response"),
        Pair(426, "Upgrade Required"),
        Pair(428, "Precondition Required"),
        Pair(429, "Too Many Requests"),
        Pair(431, "Request Header Fields Too Large"),
        Pair(451, "Unavailable For Legal Reasons"),
        //500 Server Errors
        Pair(500, "Internal Server Error"),
        Pair(501, "Not Implemented"),
        Pair(502, "Bad Gateway"),
        Pair(503, "Service Unavailable"),
        Pair(504, "Gateway Timeout"),
        Pair(505, "HTTP Version Not Supported"),
        Pair(506, "Variant Also Negotiates"),
        Pair(510, "Not Extended"),
        Pair(511, "Network Authentication Required")
)

class JsonProblemResponse(status:Int, detail:String)
    : Response(hashMapOf(Pair("Content-Type", "application/problem+json")), status) {
    init {
        val title = titles.getOrElse(status) { "Unknown" }

        this.body = jsonObject(
                "type" to "http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html",
                "status" to status,
                "title" to title,
                "detail" to detail
        )
    }
}