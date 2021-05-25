package xyz.tcreopargh.amttd_web.repository

import org.springframework.data.jpa.repository.JpaRepository
import xyz.tcreopargh.amttd_web.entity.EntityInvitationCode
import xyz.tcreopargh.amttd_web.entity.EntityUser
import xyz.tcreopargh.amttd_web.entity.EntityWorkGroup

interface InvitationCodeRepository : JpaRepository<EntityInvitationCode, String> {
    fun findByUser(user: EntityUser): List<EntityInvitationCode>
    fun findByWorkGroup(workGroup: EntityWorkGroup): List<EntityInvitationCode>
}