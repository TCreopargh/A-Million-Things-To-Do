package xyz.tcreopargh.amttd.util

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.net.URL

/**
 * @author TCreopargh
 */

// TODO: Change this to the website URL when the back end is deployed

// For loopback address on local machine (Not the emulator), use IP 10.0.2.2
val rootUrl = URL("http://10.0.2.2:8080")

val mediaTypeJson: MediaType? = "application/json; charset=utf-8".toMediaTypeOrNull()

