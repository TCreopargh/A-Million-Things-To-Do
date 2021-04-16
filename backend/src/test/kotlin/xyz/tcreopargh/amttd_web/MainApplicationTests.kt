package xyz.tcreopargh.amttd_web

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import xyz.tcreopargh.amttd_web.repository.UserRepository

@SpringBootTest
class MainApplicationTests {

    @Autowired
    var userRepository: UserRepository? = null

    @Test
    fun contextLoads() {
    }

    @Test
    fun testQuery() {
        assertTrue(userRepository?.findByUsername("Wang")?.size ?: 0 > 0)
    }

}
