package xyz.tcreopargh.amttd_web.controller.account

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.common.bean.request.RegisterRequest
import xyz.tcreopargh.amttd_web.common.bean.response.LoginResponse
import javax.servlet.http.HttpServletRequest

@RestController
class UserController {
    @PostMapping(
        "/user/rename",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun changeUsername(request: HttpServletRequest, @RequestBody registerBody: RegisterRequest): LoginResponse {
        TODO("Not implemented")
    }
}