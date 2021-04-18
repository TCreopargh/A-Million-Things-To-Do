package xyz.tcreopargh.amttd_web.controller.account

import org.springframework.beans.factory.annotation.Autowired
import xyz.tcreopargh.amttd_web.service.TokenService
import xyz.tcreopargh.amttd_web.service.UserService

open class AuthenticationController {
    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var tokenService: TokenService

    protected fun isUserNameValid(username: String?): Boolean {
        return username?.matches(Regex("^([\\u4e00-\\u9fa5]{2,3})|([A-Za-z0-9_]{3,32})|([a-zA-Z0-9_\\u4e00-\\u9fa5]{3,32})\$")) == true
    }

    protected fun isPasswordValid(password: String?): Boolean {
        return password?.length in 6..1024
    }
}
