package xyz.tcreopargh.amttd_web.util

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xyz.tcreopargh.amttd_web.AMTTD
import java.io.PrintWriter
import java.io.Reader
import kotlin.random.Random

val random: Random = Random.Default

val logger: Logger by lazy {
    return@lazy LoggerFactory.getLogger(AMTTD::class.java)
}

fun generateToken() = random.nextString(128)

fun Random.nextString(
    length: Int,
    dictionary: String = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
): String {
    return StringBuilder().apply {
        repeat(length) {
            append(dictionary.random())
        }
    }.toString()
}

fun JsonObject.map(vararg args: Pair<String, Any>) {
    for (pair in args) {
        when (pair.second) {
            is Boolean     -> addProperty(pair.first, pair.second as Boolean)
            is String      -> addProperty(pair.first, pair.second as String)
            is Number      -> addProperty(pair.first, pair.second as Number)
            is Char        -> addProperty(pair.first, pair.second as Char)
            is JsonElement -> add(pair.first, pair.second as JsonElement)
            else           -> addProperty(pair.first, pair.second.toString())
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
                is JsonElement -> this.add(element)
                else           -> this.add(element.toString())
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

