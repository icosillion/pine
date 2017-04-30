package com.icosillion.pine

import com.github.salomonbrys.kodein.Kodein
import com.icosillion.pine.annotations.Use
import com.icosillion.pine.annotations.Group
import com.icosillion.pine.annotations.Route
import com.icosillion.pine.annotations.UseNamed
import com.icosillion.pine.exceptions.PathParseException
import com.icosillion.pine.handlers.Handler
import com.icosillion.pine.http.Method
import com.icosillion.pine.http.Request
import com.icosillion.pine.http.Response
import com.icosillion.pine.middleware.Middleware
import com.icosillion.pine.resources.DynamicResource
import com.icosillion.pine.resources.FunctionResource
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import java.net.InetSocketAddress
import java.util.*
import kotlin.reflect.KClass

/**
 * Core Pine Framework
 */
open class Pine(val container: Kodein = Kodein {}) {

    val handlers = arrayListOf<Handler>()
    val resources = arrayListOf<Any>()
    val bundledMiddleware = hashMapOf<String, Array<Middleware>>()
    private val router = Router()

    /**
     * Starts up the HTTP Server
     */
    fun start(host: String = "0.0.0.0", port: Int = 8080) {
        val server = Server(InetSocketAddress(host, port));
        val handler = ServletContextHandler(server, "/")
        handler.addServlet(ServletHolder(PineServlet(this)), "/")

        this.preflight()

        server.start() //TODO catch exceptions from this

        println("Server started at $host:$port")
    }

    /**
     * This method builds internal route structures from resource types so they can be processed quickly by the server
     */
    protected fun preflight() {
        //Process Annotations
        resources.forEach { resource ->

            if (resource is DynamicResource) {
                resource.registerRoutes(this)
            }

            val clazz: KClass<*> = resource.javaClass.kotlin

            //Get Group Middleware
            val resourceMiddleware = arrayListOf<Middleware>()
            clazz.annotations.forEach { annotation ->
                when (annotation) {
                    is Use -> resourceMiddleware.add(annotation.middleware.constructors.first().call())
                    is UseNamed -> resourceMiddleware.addAll(bundledMiddleware[annotation.middleware]!!)
                }
            }

            clazz.members.forEach { function ->
                //Get Path Prefix
                var pathPrefix = ""
                resource.javaClass.kotlin.annotations.forEach { annotation ->
                    if (annotation is Group) {
                        pathPrefix = annotation.path
                    }
                }

                //Validate Path Prefix
                if (!pathPrefix.isEmpty() && !pathPattern.matcher(pathPrefix).matches()) {
                    throw PathParseException("Invalid Group Path - \"" + pathPrefix + "\"")
                }

                if (!pathPrefix.endsWith('/')) {
                    pathPrefix += "/";
                }

                function.annotations.forEach { annotation ->
                    if (annotation is Route) {
                        //Combine Path
                        var path = annotation.path
                        if (path.startsWith('/'))
                            path = path.substring(1)

                        path = pathPrefix + path

                        if (path.length > 1 && path.endsWith('/'))
                            path = path.substring(0, path.length - 1)

                        if (!pathPattern.matcher(path).matches()) {
                            throw PathParseException("Invalid Route Path - \"" + path + "\"")
                        }

                        val middleware = arrayListOf<Middleware>()

                        //Add Group Middleware
                        middleware.addAll(resourceMiddleware)

                        //Get Middleware
                        function.annotations.forEach { annotation ->
                            when (annotation) {
                                is Use -> middleware.add(annotation.middleware.constructors.first().call())
                                is UseNamed -> middleware.addAll(bundledMiddleware[annotation.middleware]!!)
                            }
                        }

                        router.register(
                                path,
                                annotation.methods,
                                annotation.accepts,
                                ReflectedCallableResource(resource, function),
                                middleware
                        )
                    }
                }
            }
        }
    }

    /**
     * Processes a request and generates a response for that request
     */
    fun handleRequest(request: Request): Response {
        return router.route(request)
    }

    /**
     * Registers a new resource
     */
    fun resource(resource: Any) {
        resources.add(resource)
    }

    /**
     * Registers a new handler
     */
    fun handler(handler: Handler) {
        handlers.add(handler)
    }

    /**
     * Registers a new route using a function handler
     */
    fun function(method: Method, path: String, function: (Request, Response) -> Response) {
        this.resource(FunctionResource(method, path, function))
    }

    /**
     * Registers a new GET route which will be handled by the passed function
     */
    fun get(path: String, function: (Request, Response) -> Response) {
        this.function(Method.GET, path, function)
    }

    /**
     * Registers a new POST route which will be handled by the passed function
     */
    fun post(path: String, function: (Request, Response) -> Response) {
        this.function(Method.POST, path, function)
    }

    /**
     * Registers a new POST route which will be handled by the passed function
     */
    fun put(path: String, function: (Request, Response) -> Response) {
        this.function(Method.PUT, path, function)
    }

    /**
     * Registers a new PATCH route which will be handled by the passed function
     */
    fun patch(path: String, function: (Request, Response) -> Response) {
        this.function(Method.PATCH, path, function)
    }

    /**
     * Registers a new DELETE route which will be handled by the passed function
     */
    fun delete(path: String, function: (Request, Response) -> Response) {
        this.function(Method.DELETE, path, function)
    }

    /**
     * Registers a new HEAD route which will be handled by the passed function
     */
    fun head(path: String, function: (Request, Response) -> Response) {
        this.function(Method.HEAD, path, function)
    }

    /**
     * Registers a new TRACE route which will be handled by the passed function
     */
    fun trace(path: String, function: (Request, Response) -> Response) {
        this.function(Method.TRACE, path, function)
    }

    /**
     * Registers a new OPTIONS route which will be handled by the passed function
     */
    fun options(path: String, function: (Request, Response) -> Response) {
        this.function(Method.OPTIONS, path, function)
    }

    /**
     * Registers a new CONNECT route which will be handled by the passed function
     */
    fun connect(path: String, function: (Request, Response) -> Response) {
        this.function(Method.CONNECT, path, function)
    }

    /**
     * Registers a new route which will match any verb. This route will be handled by the passed function
     */
    fun any(path: String, function: (Request, Response) -> Response) {
        this.function(Method.ANY, path, function)
    }

    /**
     * Registers a new route with the router
     */
    fun route(callable: CallableResource,
              path: String,
              methods: ArrayList<Method> = arrayListOf(Method.GET),
              accepts: ArrayList<String> = arrayListOf("application/json"),
              middleware: ArrayList<Middleware> = arrayListOf()
    ) {
        //Fetch any Resource-level Middleware
        if (callable is ReflectedCallableResource) {
            val clazz: KClass<*> = callable.obj.javaClass.kotlin
            clazz.annotations.forEach { annotation ->
                when (annotation) {
                    is Use -> middleware.add(annotation.middleware.constructors.first().call())
                    is UseNamed -> middleware.addAll(bundledMiddleware[annotation.middleware]!!)
                }
            }
        }

        //Get Path Prefix
        if (callable is ReflectedCallableResource) {
            var pathPrefix = ""
            callable.obj.javaClass.kotlin.annotations.forEach { annotation ->
                if (annotation is Group) {
                    pathPrefix = annotation.path
                }
            }

            //Validate Path Prefix
            if (!pathPrefix.isEmpty() && !pathPattern.matcher(pathPrefix).matches()) {
                throw PathParseException("Invalid Group Path - \"" + pathPrefix + "\"")
            }

            if (!pathPrefix.endsWith('/')) {
                pathPrefix += "/";
            }
        }

        //Add route
        router.register(path, methods.toTypedArray(), accepts.toTypedArray(), callable, middleware)
    }

    /**
     * Adds a global named middleware to the stack, which will be applied to all routes
     */
    fun namedMiddleware(name: String, middleware: Middleware) {
        bundledMiddleware.put(name, arrayOf(middleware))
    }

    /**
     * Adds a global middleware bundle to the stack, which will be applied to all routes
     */
    fun bundledMiddleware(name: String, middleware: Array<Middleware>) {
        bundledMiddleware.put(name, middleware)
    }
}