/**
 * @author TCreopargh
 */
@file:Suppress("unused")

package xyz.tcreopargh.amttd_web.util

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.io.PrintWriter
import java.io.Reader
import java.util.*
import kotlin.random.Random

fun generateToken() = random.nextString(64)

fun generateInvitationCode() = random.nextString(8)

fun Random.nextString(
    length: Int,
    dictionary: String = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
): String {
    return StringBuilder().apply {
        repeat(length) {
            append(dictionary.random())
        }
    }.toString()
}

fun <T> T.toJson(): JsonElement {
    return gson.toJsonTree(this, object : TypeToken<T>() {}.type)
}

fun <T> T.toJsonString(): String {
    return gson.toJson(this, object : TypeToken<T>() {}.type)
}

fun JsonObject.map(vararg args: Pair<String, Any>) {
    for (pair in args) {
        when (pair.second) {
            is Boolean     -> addProperty(pair.first, pair.second as Boolean)
            is String      -> addProperty(pair.first, pair.second as String)
            is Number      -> addProperty(pair.first, pair.second as Number)
            is Char        -> addProperty(pair.first, pair.second as Char)
            is Enum<*>     -> addProperty(pair.first, pair.second.toString())
            is UUID        -> addProperty(pair.first, pair.second.toString())
            is JsonElement -> add(pair.first, pair.second as JsonElement)
            else           -> add(pair.first, pair.second.toJson())
        }
    }
}

fun jsonObjectOf(vararg args: Pair<String, Any>): JsonObject {
    return JsonObject().apply {
        map(*args)
    }
}

fun jsonArrayOf(vararg args: Any): JsonArray {
    return JsonArray().apply {
        for (element in args) {
            when (element) {
                is Boolean     -> this.add(element)
                is String      -> this.add(element)
                is Number      -> this.add(element)
                is Char        -> this.add(element)
                is Enum<*>     -> this.add(element.toString())
                is UUID        -> this.add(element.toString())
                is JsonElement -> this.add(element)
                else           -> this.add(element.toJson())
            }
        }
    }
}

fun PrintWriter.printAndClose(str: String) {
    print(str)
    flush()
    close()
}

fun PrintWriter.printlnAndClose(str: String) {
    println(str)
    flush()
    close()
}

fun Reader.readAndClose(): String {
    val str = this.readText()
    this.close()
    return str
}

