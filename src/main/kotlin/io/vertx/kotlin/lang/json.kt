package io.vertx.kotlin.lang.json

import io.vertx.core.json.*

public fun JsonObject(vararg fields : Pair<String, Any?>) : JsonObject = JsonObject(mapOf(*fields))
public fun JsonObject(fields : Iterable<Pair<String, Any?>>) : JsonObject = JsonObject(mapOf(*fields.toList().copyToArray()))
public fun JsonArray(vararg values : Any?) : JsonArray = JsonArray(listOf(*values))
public fun JsonArray(values : Iterable<Any?>) : JsonArray = JsonArray(listOf(*values.toList().copyToArray()))

public class Json

public fun Json.object_(vararg fields : Pair<String, Any?>) : JsonObject = JsonObject(*fields)
public fun Json.object_(fields : Iterable<Pair<String, Any?>>) : JsonObject = JsonObject(*fields.toList().copyToArray())
public fun Json.object_(fields : Map<String, Any?>) : JsonObject = JsonObject(fields)
public fun Json.array_(vararg values : Any?) : JsonArray = JsonArray(*values)
public fun Json.array_(values : Iterable<Any?>) : JsonArray = JsonArray(*values.toList().copyToArray())
public fun Json.array_(values : List<Any?>) : JsonArray = JsonArray(values)

public inline fun <T> json_(block : Json.() -> T) : T = Json().block()

[suppress("UNCHECKED_CAST")]
public fun <T> JsonObject.get(key : String) : T = getValue(key) as T

fun main(args: Array<String>) {
    val j = json_ {
        object_ (
                "a" to 1,
                "b" to object_ (
                        "field1" to "value1"
                ),
                "c" to array_("1", "2", "3")
        )
    }

    println(j)
    println(j["a"] : Any)
}