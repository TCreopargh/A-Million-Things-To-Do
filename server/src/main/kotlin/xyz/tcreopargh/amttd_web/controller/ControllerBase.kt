package xyz.tcreopargh.amttd_web.controller

import org.springframework.beans.factory.annotation.Autowired
import xyz.tcreopargh.amttd_web.service.TodoEntryService
import xyz.tcreopargh.amttd_web.service.TokenService
import xyz.tcreopargh.amttd_web.service.UserService
import xyz.tcreopargh.amttd_web.service.WorkGroupService

abstract class ControllerBase {
    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var tokenService: TokenService

    @Autowired
    lateinit var workGroupService: WorkGroupService

    @Autowired
    lateinit var todoEntryService: TodoEntryService

    protected fun isUserNameValid(username: String?): Boolean {
        return username?.matches(Regex("^([\\u4e00-\\u9fa5]{2,3})|([A-Za-z0-9_ ]{3,32})|([a-zA-Z0-9_ \\u4e00-\\u9fa5]{3,32})\$")) == true && username.trim() == username
    }

    protected fun isPasswordValid(password: String?): Boolean {
        return password?.length in 6..1024
    }

    protected fun isEmailValid(email: String?): Boolean {
        return email?.matches("^\\S+@\\S+\\.\\S+\$".toRegex()) == true
    }
}
