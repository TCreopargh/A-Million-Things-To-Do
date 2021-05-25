package xyz.tcreopargh.amttd_web.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import xyz.tcreopargh.amttd_web.entity.EntityAuthToken
import xyz.tcreopargh.amttd_web.entity.EntityInvitationCode
import xyz.tcreopargh.amttd_web.entity.EntityUser
import xyz.tcreopargh.amttd_web.repository.InvitationCodeRepository
import xyz.tcreopargh.amttd_web.repository.TokenRepository
import java.util.*

@Service
class InvitationCodeService {
    @Autowired
    private lateinit var repository: InvitationCodeRepository

    fun getAll() = repository.findAll()

    fun save(code: EntityInvitationCode) = repository.save(code)

    fun saveImmediately(code: EntityInvitationCode) = repository.saveAndFlush(code)

    fun saveAll(codes: List<EntityInvitationCode>) = repository.saveAll(codes)

    fun findByUser(user: EntityUser) = repository.findByUser(user)

    fun findByCode(code: String) = repository.findByIdOrNull(code)

    fun remove(code: EntityInvitationCode) = repository.delete(code)

    fun removeExpired() {
        for (code in repository.findAll()) {
            if (code.isExpired()) {
                repository.delete(code)
            }
        }
    }
}