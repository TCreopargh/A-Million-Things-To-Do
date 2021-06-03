package xyz.tcreopargh.amttd_web.service

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import java.util.*

/**
 * @author TCreopargh
 * Base class for services that are based on a repository.
 */
abstract class RepositoryServiceBase<Entity : Any, ID : Any> {

    protected abstract val repository: JpaRepository<Entity, ID>

    open fun getAll(): List<Entity> = repository.findAll()
    open fun save(entity: Entity): Entity = repository.save(entity)
    open fun saveAll(entities: List<Entity>): List<Entity> = repository.saveAll(entities)
    open fun saveImmediately(entity: Entity): Entity = repository.saveAndFlush(entity)
    open fun findById(id: ID): Optional<Entity> = repository.findById(id)
    open fun findByIdOrNull(id: ID): Entity? = repository.findByIdOrNull(id)
    open fun delete(entity: Entity) = repository.delete(entity)
    open fun deleteById(id: ID) = repository.deleteById(id)

}