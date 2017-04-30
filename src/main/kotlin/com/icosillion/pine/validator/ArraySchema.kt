package com.icosillion.pine.validator

import com.google.gson.JsonArray

class ArraySchema(val schema: Any) {

    fun validateWithReporting(array: JsonArray): ValidationResult {
        var isValid = true
        val errors = arrayListOf<ValidationError>()

        when(schema) {
            is ObjectSchema -> {
                array.forEachIndexed { i, value ->
                    if(value.isJsonObject.not()) {
                        isValid = false
                        errors.add(ValidationError(
                                field = null,
                                reason = "Element $i is not a JSON Object"
                        ))
                        return@forEachIndexed
                    }

                    val result = schema.validateWithReporting(value.asJsonObject)
                    if(result.isValid.not()) {
                        isValid = false
                        errors.add(ValidationError(
                                field = null,
                                reason = "Element $i failed to pass object schema"
                        ))
                        errors.addAll(result.errors)
                        return@forEachIndexed
                    }
                }
            }
            is ArraySchema -> {
                array.forEachIndexed { i, value ->
                    if(value.isJsonArray.not()) {
                        isValid = false
                        errors.add(ValidationError(
                                field = null,
                                reason = "Element $i is not an array"
                        ))
                        return@forEachIndexed
                    }

                    val result = schema.validateWithReporting(value.asJsonArray)
                    if(result.isValid.not()) {
                        isValid = false
                        errors.add(ValidationError(
                                field = null,
                                reason = "Element $i failed to pass array schema"
                        ))
                        errors.addAll(result.errors)
                        return@forEachIndexed
                    }
                }
            }
            is Rule -> {
                array.forEachIndexed { i, value ->
                    val result = schema.validateWithReporting(value)
                    if(result.isValid.not()) {
                        isValid = false
                        errors.add(ValidationError(
                                field = null,
                                reason = "Element $i failed to pass rule"
                        ))
                        errors.addAll(result.errors)
                        return@forEachIndexed
                    }
                }
            }
            else -> {
                isValid = false
                errors.add(ValidationError(
                        field = null,
                        reason = "Invalid data type passed to array schema"
                ))
            }
        }

        return ValidationResult(isValid, errors)
    }

    fun validate(array: JsonArray): Boolean {
        return validateWithReporting(array).isValid
    }
}