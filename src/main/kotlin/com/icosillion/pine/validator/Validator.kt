package com.icosillion.pine.validator

fun objectSchema(vararg values: Pair<String, Any>): ObjectSchema {
    return ObjectSchema(values.asList())
}

fun arraySchema(schema: ObjectSchema): ArraySchema {
    return ArraySchema(schema)
}

fun arraySchema(rule: Rule): ArraySchema {
    return ArraySchema(rule)
}