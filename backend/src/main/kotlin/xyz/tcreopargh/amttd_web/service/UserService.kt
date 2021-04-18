package xyz.tcreopargh.amttd_web.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import xyz.tcreopargh.amttd_web.entity.EntityUser
import xyz.tcreopargh.amttd_web.repository.UserRepository
import java.util.*


@Service
class UserService {
    @Autowired
    private lateinit var userRepository: UserRepository
    fun getAllUsers() = userRepository.findAll()
    fun save(user: EntityUser) = userRepository.save(user)
    fun saveImmediately(user: EntityUser) = userRepository.saveAndFlush(user)
    fun saveAll(users: List<EntityUser>) = userRepository.saveAll(users)
    fun findByUsername(username: String) = userRepository.findByName(username)

    @Transactional
    fun update(uuid: UUID, newUser: EntityUser) {
        userRepository.findById(uuid).orElse(null)?.apply Repo@{
            name = newUser.name
            password = newUser.password
            userRepository.save(this@Repo)
        }
    }

    fun deleteById(uuid: UUID) = userRepository.deleteById(uuid)
    fun delete(user: EntityUser) = userRepository.delete(user)
}