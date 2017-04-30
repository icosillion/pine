package com.icosillion.pine

import com.icosillion.pine.annotations.Route
import com.icosillion.pine.responses.JsonProblemResponse
import com.icosillion.pine.http.Method
import com.icosillion.pine.http.Request
import com.icosillion.pine.http.Response
import com.icosillion.pine.middleware.Middleware
import com.icosillion.pine.middleware.MiddlewareChain
import java.util.regex.Pattern

val pathPattern = Pattern.compile("(?:/((:[a-zA-Z0-9]+)|\\*|[a-zA-Z0-9\\-\\._~]*))+")!!

/**
 * Provides a structure for describing and matching routes
 */
class RouteDefinition(var path: String, var methods: Array<Method> = arrayOf(Method.GET),
                      var accepts: Array<String> = arrayOf("application/json"),
                      var middleware: List<Middleware> = arrayListOf()) {
    constructor(route: Route) : this(route.path, route.methods, route.accepts)

    private val sections: List<String>

    init {
        this.sections = splitPath(this.path)
    }

    //TODO Add accept support
    /**
     * Checks if a given path matches this definition
     */
    fun matches(path: String, method: Method): Boolean {
        val path = if (path.length > 1 && path.endsWith("/")) path.substring(0, path.length - 1) else path

        val otherSections = splitPath(path)
        if (this.sections.count() != otherSections.count()) {
            return false;
        }

        if (!methods.contains(Method.ANY) && !methods.contains(method)) {
            return false;
        }

        sections.forEachIndexed { i, section ->
            val otherSection = otherSections[i]
            if (section != "/*" && !section.startsWith("/:")) {
                if (section != otherSection) {
                    return false
                }
            }
        }

        return true;
    }

    /**
     * Extracts variables from a given path using the route template
     */
    fun getPathParameters(path: String): Map<String, String> {
        val parameters = hashMapOf<String, String>()

        val dataSections = splitPath(path)
        sections.forEachIndexed { i, section ->
            if (section.startsWith("/:")) {
                val key = section.substring(2)
                val value = dataSections[i].substring(1)
                parameters.put(key, value)
            }
        }

        return parameters
    }

    /**
     * Splits path into sections separated by /
     */
    private fun splitPath(path: String): List<String> {
        val sections = arrayListOf<String>()

        if (path.length == 0)
            return sections

        val pathChars = path.toCharArray()
        var sectionContent = ""
        pathChars.forEach { c ->
            if (c == '/' && sectionContent.isNotEmpty()) {
                sections.add(sectionContent)
                sectionContent = "/"
            } else {
                sectionContent += c
            }
        }

        if (sectionContent.isNotEmpty()) {
            sections.add(sectionContent)
        }

        return sections
    }
}

/**
 * Provides routing and execution for requests
 */
class Router {

    private val routes = hashMapOf<RouteDefinition, CallableResource>()
    var errorHandler: (Request, Response, Exception) -> Response = fun (request, response, ex): Response {
        println("An exception has been thrown for request '${request.path}'")
        ex.printStackTrace()
        return JsonProblemResponse(500, "FATAL_EXCEPTION")
    }

    var noRouteMatchedHandler: (request: Request) -> Response = fun(request): Response {
        return JsonProblemResponse(404, "No Route Matched")
    }

    /**
     * Registers a new route with the router
     */
    fun register(path: String, methods: Array<Method>, accepts: Array<String>, callable: CallableResource,
                 middleware: List<Middleware> = arrayListOf()
    ) {
        routes.put(RouteDefinition(path, methods, accepts, middleware), callable)
    }

    /**
     * Routes and executes a request
     */
    fun route(request: Request): Response {
        var routed = false
        var response: Response = Response()


        routes.forEach routesLoop@ { routeDefinition, callable ->
            if (routeDefinition.matches(request.path, request.method)) {
                routed = true

                //Inject path parameters
                request.pathParameters.putAll(routeDefinition.getPathParameters(request.path))

                //Call
                try {
                    //Call Before Middleware
                    response = MiddlewareChain(MiddlewareChain.Type.BEFORE, routeDefinition.middleware).start(request, response)

                    //Call Resource
                    callable.call(request, response)

                    //Call After Middleware
                    response = MiddlewareChain(MiddlewareChain.Type.AFTER, routeDefinition.middleware).start(request, response)
                } catch(ex: Exception) {
                    response = errorHandler(request, response, ex)
                    return@routesLoop
                }
            }
        }

        if (!routed) {
            return noRouteMatchedHandler(request)
        }

        return response
    }
}