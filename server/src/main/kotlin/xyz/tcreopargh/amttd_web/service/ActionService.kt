package xyz.tcreopargh.amttd_web.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import xyz.tcreopargh.amttd_web.entity.EntityAction
import xyz.tcreopargh.amttd_web.repository.ActionRepository
import java.util.*

@Service
class ActionService : RepositoryServiceBase<EntityAction, UUID>() {
    @Autowired
    override lateinit var repository: ActionRepository
}