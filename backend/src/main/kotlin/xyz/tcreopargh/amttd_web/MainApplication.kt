package xyz.tcreopargh.amttd_web

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@RestController
@EnableAutoConfiguration(exclude = [DataSourceAutoConfiguration::class])
class MainApplication {
    @RequestMapping("/")
    fun root(): String {
        return "<h1>Hello, World!</h1>"
    }
}
