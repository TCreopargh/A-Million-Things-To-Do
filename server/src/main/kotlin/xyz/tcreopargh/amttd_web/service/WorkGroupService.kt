package xyz.tcreopargh.amttd_web.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import xyz.tcreopargh.amttd_web.entity.EntityUser
import xyz.tcreopargh.amttd_web.entity.EntityWorkGroup
import xyz.tcreopargh.amttd_web.repository.WorkGroupRepository
import java.util.*

@Service
class WorkGroupService : RepositoryServiceBase<EntityWorkGroup, UUID>() {
    @Autowired
    override lateinit var repository: WorkGroupRepository

    fun findAllByUser(user: EntityUser) = repository.findAllByUsers(user)
    fun findByID(code:UUID) = findByIdOrNull(code)
}
