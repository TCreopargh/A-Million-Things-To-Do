package xyz.tcreopargh.amttd_web.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import xyz.tcreopargh.amttd_web.component.AuthenticationInterceptor


@Configuration
class WebConfig : WebMvcConfigurer {
    @Autowired
    private lateinit var authenticationInterceptor: AuthenticationInterceptor

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