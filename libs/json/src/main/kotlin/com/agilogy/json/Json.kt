package com.agilogy.json

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

fun jsonObject(vararg tuples: Pair<String, JsonElement?>): JsonObject =
    JsonObject(tuples.mapNotNull { (key, value) -> if (value == null) null else key to value }.toMap())

fun jsonObject(tuples: Iterable<Pair<String, JsonElement?>>): JsonObject =
    jsonObject(*tuples.toList().toTypedArray())

fun jsonArray(vararg elements: JsonElement?) = JsonArray(elements.asList().filterNotNull())

val Number.json: JsonPrimitive get() = JsonPrimitive(this)
val String.json: JsonPrimitive get() = JsonPrimitive(this)
val Boolean.json: JsonPrimitive get() = JsonPrimitive(this)
val Double.json: JsonPrimitive get() = JsonPrimitive(this)

val Iterable<JsonElement>.json: JsonArray get() = JsonArray(this.toList())

