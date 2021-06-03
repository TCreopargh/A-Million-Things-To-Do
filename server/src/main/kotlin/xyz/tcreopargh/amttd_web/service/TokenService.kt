package xyz.tcreopargh.amttd_web.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import xyz.tcreopargh.amttd_web.entity.EntityAuthToken
import xyz.tcreopargh.amttd_web.entity.EntityUser
import xyz.tcreopargh.amttd_web.repository.TokenRepository
import java.util.*

@Service
class TokenService : RepositoryServiceBase<EntityAuthToken, String>() {
    @Autowired
    override lateinit var repository: TokenRepository

    fun findByUser(user: EntityUser) = repository.findByUser(user)

    fun findByToken(token: String) = findByIdOrNull(token)

    fun remove(token: EntityAuthToken) = delete(token)
    fun removeExpired() {
        for (token in repository.findAll()) {
            if (token.isExpired()) {
                repository.delete(token)
            }
        }
    }

    fun verify(token: String, userId: UUID): Boolean {
        val authToken = findByToken(token)
        if (authToken?.isValid() == true && authToken.user?.uuid == userId) {
            return true
        }
        return false
    }

    fun verify(authToken: EntityAuthToken, userId: UUID): Boolean {
        return authToken.isValid() && authToken.user?.uuid == userId
    }
}