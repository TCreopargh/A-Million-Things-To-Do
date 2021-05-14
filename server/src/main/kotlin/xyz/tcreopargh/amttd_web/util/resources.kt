package xyz.tcreopargh.amttd_web.util

import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xyz.tcreopargh.amttd_web.AMTTD
import kotlin.random.Random


val gson: Gson = Gson()
val random: Random = Random.Default

val logger: Logger by lazy {
    return@lazy LoggerFactory.getLogger(AMTTD::class.java)
}
