package xyz.tcreopargh.amttd_web.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import xyz.tcreopargh.amttd_web.entity.EntityUser
import xyz.tcreopargh.amttd_web.entity.EntityWorkGroup
import xyz.tcreopargh.amttd_web.repository.WorkGroupRepository
import java.util.*

@Service
class WorkGroupService {
    @Autowired
    private lateinit var repository: WorkGroupRepository

    fun getAll() = repository.findAll()
    fun save(workGroup: EntityWorkGroup) = repository.save(workGroup)
    fun saveImmediately(workGroup: EntityWorkGroup) = repository.saveAndFlush(workGroup)
    fun findAllByUser(user: EntityUser) = repository.findAllByUsers(user)
    fun findById(uuid: UUID) = repository.findById(uuid)
    fun findByIdOrNull(uuid: UUID) = repository.findByIdOrNull(uuid)
    fun delete(uuid: UUID) = repository.deleteById(uuid)
}