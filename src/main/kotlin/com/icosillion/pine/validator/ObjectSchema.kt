package com.icosillion.pine.validator

import com.google.gson.JsonArray
import com.google.gson.JsonObject

class ObjectSchema(val rules: List<Pair<String, Any>>) {

    fun validateWithReporting(obj: JsonObject): ValidationResult {
        var isValid = true
        val errors = arrayListOf<ValidationError>()

        rules.forEach { pair ->
            val key = pair.first
            val value = pair.second

            var subObj = obj[key]

            when(value) {
                is Rule -> {
                    val result = value.validateWithReporting(subObj)
                    if(result.isValid.not()) {
                        isValid = false
                        errors.add(ValidationError(
                                field = key,
                                reason = result.errors.first().reason
                        ))
                    }
                }
                is ObjectSchema -> {
                    if(subObj is JsonObject) {
                        val result = value.validateWithReporting(subObj.asJsonObject)

                        if(result.isValid.not()) {
                            isValid = false
                            errors.add(ValidationError(
                                    field = key,
                                    reason = result.errors.first().reason
                            ))
                        }
                    } else {
                        isValid = false
                        errors.add(ValidationError(
                                field = key,
                                reason = "Expected object"
                        ))
                    }
                }
                is ArraySchema -> {
                    if(subObj is JsonArray) {
                        /*
                        if(value.validate(subObj.asJsonArray).not()) {
                            isValid = false
                        }
                        */
                        val result = value.validateWithReporting(subObj.asJsonArray)
                        if(result.isValid.not()) {
                            isValid = false
                            errors.add(ValidationError(
                                    field = key,
                                    reason = result.errors.first().reason
                            ))
                        }
                    }
                }
                else -> {
                    isValid = false
                }
            }
        }

        return ValidationResult(isValid, errors)
    }

    fun validate(obj: JsonObject): Boolean {
        return validateWithReporting(obj).isValid
    }
}