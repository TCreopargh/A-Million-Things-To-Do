package xyz.tcreopargh.amttd_web.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import xyz.tcreopargh.amttd_web.entity.EntityAction
import xyz.tcreopargh.amttd_web.repository.ActionRepository
import java.util.*

@Service
class ActionService {
    @Autowired
    private lateinit var repository: ActionRepository
    fun getAll() = repository.findAll()
    fun save(entry: EntityAction) = repository.save(entry)
    fun saveImmediately(entry: EntityAction) = repository.saveAndFlush(entry)
    fun findById(uuid: UUID) = repository.findById(uuid)
    fun findByIdOrNull(uuid: UUID) = repository.findByIdOrNull(uuid)
    fun delete(uuid: UUID) = repository.deleteById(uuid)
}