package xyz.tcreopargh.amttd_web.controller

import org.springframework.beans.factory.annotation.Autowired
import xyz.tcreopargh.amttd_web.api.exception.AmttdException
import xyz.tcreopargh.amttd_web.entity.EntityTodoEntry
import xyz.tcreopargh.amttd_web.entity.EntityUser
import xyz.tcreopargh.amttd_web.entity.EntityWorkGroup
import xyz.tcreopargh.amttd_web.service.*
import java.util.*
import javax.servlet.http.HttpServletRequest

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
    protected fun verifyUser(request: HttpServletRequest, userId: UUID? = null): EntityUser {
        val requestUser = getUserFromSession(request)
        if (userId == null) {
            if (requestUser == null) throw AmttdException(AmttdException.ErrorCode.LOGIN_REQUIRED)
        } else {
            if (requestUser?.uuid != userId) throw AmttdException(AmttdException.ErrorCode.UNAUTHORIZED_OPERATION)
        }
        return requestUser
    }

    @Throws(AmttdException::class)
    protected fun verifyWorkgroup(request: HttpServletRequest, groupId: UUID?): Pair<EntityWorkGroup, EntityUser> {
        if (groupId == null) {
            throw AmttdException(AmttdException.ErrorCode.INVALID_JSON)
        }
        val user = getUserFromSession(request) ?: throw AmttdException(AmttdException.ErrorCode.AUTHENTICATION_ERROR)
        val group = workGroupService.findByIdOrNull(groupId)
        if (group?.users?.contains(user) != true) throw AmttdException(AmttdException.ErrorCode.UNAUTHORIZED_OPERATION)
        return Pair(group, user)
    }


    @Throws(AmttdException::class)
    protected fun verifyWorkgroup(
        request: HttpServletRequest,
        groupId: UUID?,
        userId: UUID?,
        assertLeader: Boolean = false
    ): Pair<EntityWorkGroup, EntityUser> {
        if (groupId == null || userId == null) {
            throw AmttdException(AmttdException.ErrorCode.INVALID_JSON)
        }
        val user = verifyUser(request, userId)
        val group = workGroupService.findByIdOrNull(groupId)
        if (assertLeader && group?.leader?.getId() != user.getId()) {
            throw AmttdException(AmttdException.ErrorCode.INSUFFICIENT_PERMISSION)
        }
        if (group?.users?.contains(user) != true) throw AmttdException(AmttdException.ErrorCode.UNAUTHORIZED_OPERATION)
        return Pair(group, user)
    }

    @Throws(AmttdException::class)
    protected fun verifyTodoEntry(
        request: HttpServletRequest,
        entryId: UUID?
    ): Triple<EntityTodoEntry, EntityWorkGroup, EntityUser> {
        if (entryId == null) {
            throw AmttdException(AmttdException.ErrorCode.INVALID_JSON)
        }
        val entry = todoEntryService.findByIdOrNull(entryId)
        val pair = verifyWorkgroup(
            request,
            entry?.parent?.groupId ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
        )
        return Triple(entry, pair.first, pair.second)
    }

    @Throws(AmttdException::class)
    protected fun verifyTodoEntry(
        request: HttpServletRequest,
        entryId: UUID?,
        userId: UUID?,
        assertLeader: Boolean = false
    ): Triple<EntityTodoEntry, EntityWorkGroup, EntityUser> {
        if (entryId == null || userId == null) {
            throw AmttdException(AmttdException.ErrorCode.INVALID_JSON)
        }
        val entry = todoEntryService.findByIdOrNull(entryId)
        val pair = verifyWorkgroup(
            request,
            entry?.parent?.groupId ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND),
            userId,
            assertLeader
        )
        return Triple(entry, pair.first, pair.second)
    }

    protected fun getUserFromSession(request: HttpServletRequest): EntityUser? {
        val session = request.session
        val uuid = session.getAttribute("uuid")?.toString()
        val token = session.getAttribute("token")?.toString()
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
