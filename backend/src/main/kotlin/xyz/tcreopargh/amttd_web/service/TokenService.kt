package xyz.tcreopargh.amttd_web.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import xyz.tcreopargh.amttd_web.entity.EntityUser
import xyz.tcreopargh.amttd_web.entity.AuthToken
import xyz.tcreopargh.amttd_web.repository.TokenRepository

@Service
class TokenService {
    @Autowired
    private lateinit var tokenRepository: TokenRepository
    fun getAll() = tokenRepository.findAll()
    fun save(token: AuthToken) = tokenRepository.save(token)
    fun saveImmediately(token: AuthToken) = tokenRepository.saveAndFlush(token)
    fun saveAll(tokens: List<AuthToken>) = tokenRepository.saveAll(tokens)
    fun findByUsername(user: EntityUser) = tokenRepository.findByUser(user)
    fun removeExpired() {
        for(token in tokenRepository.findAll()) {
            if(token.isExpired()) {
                tokenRepository.delete(token)
            }
        }
    }
}