package xyz.tcreopargh.amttd_web.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import xyz.tcreopargh.amttd_web.entity.EntityAuthToken
import xyz.tcreopargh.amttd_web.entity.EntityUser
import xyz.tcreopargh.amttd_web.repository.TokenRepository
import java.util.*

@Service
class TokenService {
    @Autowired
    private lateinit var tokenRepository: TokenRepository

    fun getAll() = tokenRepository.findAll()

    fun save(token: EntityAuthToken) = tokenRepository.save(token)

    fun saveImmediately(token: EntityAuthToken) = tokenRepository.saveAndFlush(token)

    fun saveAll(tokens: List<EntityAuthToken>) = tokenRepository.saveAll(tokens)

    fun findByUsername(user: EntityUser) = tokenRepository.findByUser(user)

    fun findByToken(token: String) = tokenRepository.findByIdOrNull(token)

    fun remove(token: EntityAuthToken) = tokenRepository.delete(token)
    fun removeExpired() {
        for (token in tokenRepository.findAll()) {
            if (token.isExpired()) {
                tokenRepository.delete(token)
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