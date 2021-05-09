package xyz.tcreopargh.amttd.data.login

import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.Request
import xyz.tcreopargh.amttd.AMTTD
import xyz.tcreopargh.amttd.data.exception.LoginFailedException
import xyz.tcreopargh.amttd.user.LocalUser
import xyz.tcreopargh.amttd.util.jsonObjectOf
import xyz.tcreopargh.amttd.util.rootUrl
import xyz.tcreopargh.amttd.util.toRequestBody
import xyz.tcreopargh.amttd.util.withPath
import java.io.IOException
import java.util.*


/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String): LoginResult<LocalUser> {
        // TODO: acquire UUID and authToken from server
        val loginRequest = Request.Builder()
            .post(
                jsonObjectOf(
                    "username" to username,
                    "password" to password
                ).toRequestBody()
            ).url(rootUrl.withPath("/login"))
            .build()
        return sendRequest(loginRequest)
    }

    fun register(username: String, password: String): LoginResult<LocalUser> {
        val registerRequest = Request.Builder()
            .post(
                jsonObjectOf(
                    "username" to username,
                    "password" to password
                ).toRequestBody()
            ).url(rootUrl.withPath("/register"))
            .build()
        return sendRequest(registerRequest)

    }

    fun loginWithAuthToken(uuid: UUID, authToken: String): LoginResult<LocalUser> {
        val loginRequest = Request.Builder()
            .post(
                jsonObjectOf(
                    "uuid" to uuid,
                    "token" to authToken
                ).toRequestBody()
            ).url(rootUrl.withPath("/login"))
            .build()
        return sendRequest(loginRequest)
    }

    private fun sendRequest(request: Request): LoginResult<LocalUser> {
        try {
            val response = AMTTD.okHttpClient.newCall(request).execute()
            val body = response.body?.string()
            Log.i(AMTTD.logTag, "Received response body from login: $body")
            val jsonObject: JsonObject =
                JsonParser.parseString(body) as? JsonObject
                    ?: throw IOException("Invalid JSON!")
            if (jsonObject.get("success")?.asBoolean == true) {
                val username = jsonObject.get("username")?.asString
                val uuid = UUID.fromString(jsonObject.get("uuid").asString)
                val authToken = jsonObject.get("token")?.asString
                if (username != null && uuid != null) {
                    val user = LocalUser(
                        username = username,
                        uuid = uuid,
                        authToken = authToken
                    )
                    return LoginResult.Success(user)
                } else {
                    throw IllegalArgumentException("Invalid JSON arguments!")
                }
            } else {
                throw LoginFailedException(jsonObject.get("reason").asString)
            }
        } catch (e: IOException) {
            Log.e(AMTTD.logTag, "Login Error: ", e)
            return LoginResult.Error(e)
        } catch (e: IllegalArgumentException) {
            Log.e(AMTTD.logTag, "Login Error: ", e)
            return LoginResult.Error(e)
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}