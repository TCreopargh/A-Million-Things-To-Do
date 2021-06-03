package xyz.tcreopargh.amttd_web.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import xyz.tcreopargh.amttd_web.entity.EntityUserAvatar
import xyz.tcreopargh.amttd_web.repository.UserAvatarRepository
import java.util.*

@Service
class UserAvatarService : RepositoryServiceBase<EntityUserAvatar, UUID>() {
    @Autowired
    override lateinit var repository: UserAvatarRepository
}