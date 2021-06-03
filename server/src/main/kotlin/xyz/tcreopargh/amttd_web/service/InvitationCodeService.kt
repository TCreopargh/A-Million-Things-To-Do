package xyz.tcreopargh.amttd_web.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import xyz.tcreopargh.amttd_web.entity.EntityInvitationCode
import xyz.tcreopargh.amttd_web.entity.EntityUser
import xyz.tcreopargh.amttd_web.repository.InvitationCodeRepository

@Service
class InvitationCodeService : RepositoryServiceBase<EntityInvitationCode, String>() {
    @Autowired
    override lateinit var repository: InvitationCodeRepository
    fun findByUser(user: EntityUser) = repository.findByUser(user)

    fun findByCode(code: String) = findByIdOrNull(code)

    fun remove(code: EntityInvitationCode) = repository.delete(code)

    fun removeExpired() {
        for (code in repository.findAll()) {
            if (code.isExpired()) {
                repository.delete(code)
            }
        }
    }
}