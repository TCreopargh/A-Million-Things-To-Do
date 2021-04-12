package xyz.tcreopargh.amttd_web.account

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.util.*
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class LoginHandler {
    @RequestMapping("/login", method = [RequestMethod.POST])
    fun resolveLogin(request: HttpServletRequest, response: HttpServletResponse) {
        val body = request.reader.readAndClose()
        val jsonObject : JsonObject? = JsonParser.parseString(body) as? JsonObject
        val password = jsonObject?.get("password")?.asString
        val username = jsonObject?.get("username")?.asString
        logger.info("Received login JSON: $body")
        val jsonResponse = if (password == "123456" && username == "hello") {
            jsonObjectOf(
                "success" to true,
                "username" to username,
                "uuid" to UUID.randomUUID(),
                "token" to random.nextString(128)
            )
        } else {
            jsonObjectOf(
                "success" to false,
                "reason" to "Password Incorrect"
            )
        }
        response.writer.printlnAndClose(jsonResponse.toString())
    }
}