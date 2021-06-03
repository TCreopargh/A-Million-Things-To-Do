package xyz.tcreopargh.amttd_web.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import xyz.tcreopargh.amttd_web.entity.EntityUser
import xyz.tcreopargh.amttd_web.repository.UserRepository
import java.util.*

@Service
class UserService : RepositoryServiceBase<EntityUser, UUID>() {
    @Autowired
    override lateinit var repository: UserRepository
    fun findByUsername(username: String) = repository.findByName(username)
    fun findByEmail(email: String) = repository.findByEmailAddress(email)

    @Transactional
    fun update(uuid: UUID, newUser: EntityUser) {
        repository.findById(uuid).orElse(null)?.apply Repo@{
            name = newUser.name
            password = newUser.password
            repository.save(this@Repo)
        }
    }
}