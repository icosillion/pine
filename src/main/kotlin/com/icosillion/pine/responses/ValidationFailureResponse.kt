package com.icosillion.pine.responses

import com.github.salomonbrys.kotson.jsonArray
import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.JsonObject
import com.icosillion.pine.http.Response
import com.icosillion.pine.validator.ValidationResult

/**
 * Provides a simple JSON Problem response for validation failures
 */
class ValidationFailureResponse(validationResult: ValidationResult, status: Int = 400)
: Response(hashMapOf(Pair("Content-Type", "application/problem+json")), status) {
    init {
        //TODO Ensure validation result has failed
        this.merge(JsonProblemResponse(400, "A schema has failed to pass validation"))
        val jsonBody = this.body as JsonObject
        val jsonErrors = jsonArray()
        validationResult.errors.forEach { error ->
            jsonErrors.add(jsonObject(
                    "field" to error.field,
                    "reason" to error.reason
            ))
        }
        jsonBody.add("errors", jsonErrors)
    }
}