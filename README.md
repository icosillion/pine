![Pine Framework](docs/assets/PineBanner.png?raw=true "Pine Framework")

Version 0.1

> Please note that before 1.0 there are no backwards compatibility or stability guarantees.
If you need a stable base it is recommended that you peg your dependencies to a specific release.

Pine is a [Kotlin](https://kotlinlang.org/) framework for developing interactive web applications and APIs.
It grew out of a desire to develop APIs very quickly in Kotlin without the typical boilerplace that
is prevalent in most web frameworks for the JVM.

## Quickstart Guide

To setup a minimal Pine project, you first need to create a new [Kotlin](https://kotlinlang.org/) project with
[Gradle](https://gradle.org/) support. It is easiest to do this through [IntelliJ](https://www.jetbrains.com/idea/)'s
"New Project" wizard.

Once you have your base Kotlin project setup, add Pine to your Gradle dependencies. You can find these in the
`build.gradle` file in your project root.

```groovy
repositories {
    maven {
        url "https://maven.icosillion.com/artifactory/open-source/"
    }
}

dependencies {
    compile 'com.icosillion.pine:pine:0.1'
}
```

Once Gradle has finished pulling down all of the Pine dependencies, you can add a new package, for example
`com.example.mypineproject`. After this namespace has been created, you can add your main file to it.

```kotlin
import com.icosillion.pine.Pine
import com.icosillion.pine.annotations.Route
import com.icosillion.pine.http.Request
import com.icosillion.pine.http.Response
import com.icosillion.pine.responses.modifiers.withText

class IndexResource {

    @Route("/")
    fun root(request: Request, response: Response): Response {
        return response.withText("Hello World!")
    }
}

fun main(args: Array<String>) {
    val pine = Pine()

    pine.resource(IndexResource())

    pine.start()
}
```

Now you have everything you need to get started on your first Pine project!