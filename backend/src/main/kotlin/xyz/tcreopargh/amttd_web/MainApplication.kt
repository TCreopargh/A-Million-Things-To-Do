package xyz.tcreopargh.amttd_web

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.sql.DataSource

@SpringBootApplication
@RestController
@EnableAutoConfiguration
@EnableJpaRepositories
class MainApplication {

    @Autowired
    val mainDataSource: DataSource? = null

    @RequestMapping("/")
    fun root(): String {
        return "<h1>Hello, World!</h1>"
    }
}
