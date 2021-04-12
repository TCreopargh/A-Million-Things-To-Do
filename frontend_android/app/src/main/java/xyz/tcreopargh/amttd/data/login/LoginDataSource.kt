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

    fun loginWithAuthToken(uuid: UUID, authToken: String): LoginResult<LocalUser> {
        // TODO: handle login with auth token here
        val arr = authToken.split("-", limit = 2)
        val fakeUserName = arr[0]
        val fakeUser = LocalUser(fakeUserName, uuid, authToken)
        return LoginResult.Success(fakeUser)
    }

    private fun sendRequest(request: Request): LoginResult<LocalUser> {
        try {
            val response = AMTTD.okHttpClient.newCall(request).execute()
            val jsonObject: JsonObject =
                JsonParser.parseString(response.body?.string()) as? JsonObject
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