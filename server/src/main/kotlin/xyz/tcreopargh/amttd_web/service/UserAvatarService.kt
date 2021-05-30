package xyz.tcreopargh.amttd_web.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import xyz.tcreopargh.amttd_web.entity.EntityUserAvatar
import xyz.tcreopargh.amttd_web.repository.UserAvatarRepository
import java.util.*

@Service
class UserAvatarService {
    @Autowired
    private lateinit var repository: UserAvatarRepository
    fun getAll() = repository.findAll()
    fun save(avatar: EntityUserAvatar) = repository.save(avatar)
    fun saveImmediately(avatar: EntityUserAvatar) = repository.saveAndFlush(avatar)
    fun findById(uuid: UUID) = repository.findById(uuid)
    fun findByIdOrNull(uuid: UUID) = repository.findByIdOrNull(uuid)
    fun delete(uuid: UUID) = repository.deleteById(uuid)
}