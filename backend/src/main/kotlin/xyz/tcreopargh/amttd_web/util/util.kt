package xyz.tcreopargh.amttd_web.util

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xyz.tcreopargh.amttd_web.MainApplication
import java.io.PrintWriter
import java.io.Reader
import kotlin.random.Random

val random: Random = Random.Default

val logger: Logger by lazy {
    return@lazy LoggerFactory.getLogger(MainApplication::class.java)
}

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
            is Boolean -> addProperty(pair.first, pair.second as Boolean)
            is String -> addProperty(pair.first, pair.second as String)
            is Number -> addProperty(pair.first, pair.second as Number)
            is Char -> addProperty(pair.first, pair.second as Char)
            is JsonElement -> add(pair.first, pair.second as JsonElement)
            else -> addProperty(pair.first, pair.second.toString())
        }
    }
}

fun jsonObjectOf(vararg args: Pair<String, Any>): JsonObject {
    return JsonObject().apply {
        map(*args)
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

