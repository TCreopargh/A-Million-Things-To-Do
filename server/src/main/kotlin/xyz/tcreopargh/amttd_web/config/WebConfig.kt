package xyz.tcreopargh.amttd_web.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.servlet.MultipartConfigFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.unit.DataSize
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import xyz.tcreopargh.amttd_web.component.AuthenticationInterceptor
import javax.servlet.MultipartConfigElement


@Configuration
class WebConfig : WebMvcConfigurer {
    @Autowired
    private lateinit var authenticationInterceptor: AuthenticationInterceptor

    @Bean
    fun multipartConfigElement(): MultipartConfigElement? {
        val factory = MultipartConfigFactory()
        factory.setMaxFileSize(DataSize.ofMegabytes(2))
        factory.setMaxRequestSize(DataSize.ofMegabytes(5))
        return factory.createMultipartConfig()
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authenticationInterceptor).excludePathPatterns(
            "/*/*.eot",
            "/*/*.svg",
            "/*/*.woff2",
            "/*/*.woff",
            "/*/*.ttf",
            "/*/*.html",
            "/*/*.htm",
            "/*/*.css",
            "/*/*.js",
            "/*/*.png",
            "/*/*.jpg",
            "/*/*.jpeg",
            "/*/*.ico"
        )
    }
}