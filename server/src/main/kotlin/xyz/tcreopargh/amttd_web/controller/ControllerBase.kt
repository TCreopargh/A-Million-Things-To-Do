package xyz.tcreopargh.amttd_web.controller

import org.springframework.beans.factory.annotation.Autowired
import xyz.tcreopargh.amttd_web.common.exception.AmttdException
import xyz.tcreopargh.amttd_web.entity.EntityUser
import xyz.tcreopargh.amttd_web.service.*
import java.util.*
import javax.servlet.http.HttpServletRequest
import kotlin.streams.toList

/**
 * @author TCreopargh
 *
 * Base class for controllers.
 *
 * Inject service layer here.
 */
abstract class ControllerBase {
    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var tokenService: TokenService

    @Autowired
    lateinit var workGroupService: WorkGroupService

    @Autowired
    lateinit var todoEntryService: TodoEntryService

    @Autowired
    lateinit var actionService: ActionService

    @Autowired
    lateinit var taskService: TaskService

    @Autowired
    lateinit var invitationCodeService: InvitationCodeService

    @Autowired
    lateinit var userAvatarService: UserAvatarService

    fun isEmailValid(email: String?): Boolean {
        return email?.matches("^\\S+@\\S+\\.\\S+\$".toRegex()) == true && email.length < 80
    }

    fun isUsernameValid(username: String?): Boolean {
        return username?.matches(Regex("^([a-zA-Z0-9_ ]|[^\\x00-\\xff]){1,32}\$")) == true && username.trim() == username && username.length < 64
    }

    fun isPasswordValid(password: String?): Boolean {
        return password?.length in 6..128 && password?.matches(Regex("^[\\x00-\\xff]{6,128}\$")) == true
    }


    /**
     * Verify the user with the given user ID
     * If no parameter is passed, only checks if the user has logged in
     */
    @Throws(AmttdException::class)
    protected fun verifyUser(request: HttpServletRequest, userId: UUID? = null) {
        if (userId == null) {
            if (getUserFromSession(request) == null) throw AmttdException(AmttdException.ErrorCode.LOGIN_REQUIRED)
        } else {
            if (getUserFromSession(request)?.uuid != userId) throw AmttdException(AmttdException.ErrorCode.UNAUTHORIZED_OPERATION)
        }
    }

    @Throws(AmttdException::class)
    protected fun verifyWorkgroup(request: HttpServletRequest, groupId: UUID?) {
        if (groupId == null) {
            throw AmttdException(AmttdException.ErrorCode.INVALID_JSON)
        }
        val userId = getUserFromSession(request)?.uuid
        if (workGroupService.findByIdOrNull(groupId)?.users?.stream()?.filter {
                it.uuid == userId
            }?.toList()
                ?.isNotEmpty() != true
        ) throw AmttdException(AmttdException.ErrorCode.UNAUTHORIZED_OPERATION)
    }

    @Throws(AmttdException::class)
    protected fun verifyTodoEntry(request: HttpServletRequest, entryId: UUID?) {
        if (entryId == null) {
            throw AmttdException(AmttdException.ErrorCode.INVALID_JSON)
        }
        verifyWorkgroup(request, todoEntryService.findByIdOrNull(entryId)?.parent?.groupId)
    }

    protected fun getUserFromSession(request: HttpServletRequest): EntityUser? {

        val uuid = request.session.getAttribute("uuid")?.toString()
        val token = request.session.getAttribute("token")?.toString()
        val uuidObject = try {
            token!!
            UUID.fromString(uuid)
        } catch (e: Exception) {
            return null
        }
        var user = userService.findByIdOrNull(uuidObject)
        if (!tokenService.verify(token, uuidObject)) {
            user = null
        }
        return user
    }
}
