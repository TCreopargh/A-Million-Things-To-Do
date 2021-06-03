package xyz.tcreopargh.amttd_web.service

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import java.util.*

abstract class RepositoryServiceBase<Entity : Any, ID : Any> {

    protected abstract val repository: JpaRepository<Entity, ID>

    fun getAll(): List<Entity> = repository.findAll()
    fun save(entity: Entity): Entity = repository.save(entity)
    fun saveAll(entities: List<Entity>): List<Entity> = repository.saveAll(entities)
    fun saveImmediately(entity: Entity): Entity = repository.saveAndFlush(entity)
    fun findById(id: ID): Optional<Entity> = repository.findById(id)
    fun findByIdOrNull(id: ID): Entity? = repository.findByIdOrNull(id)
    fun delete(entity: Entity) = repository.delete(entity)
    fun deleteById(id: ID) = repository.deleteById(id)

}