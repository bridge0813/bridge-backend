package com.Bridge.bridge.config;

import com.Bridge.bridge.security.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class AuthConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/users/*", "/searchWords","/project","/projects/category",
                        "/projects", "/projects/mypart", "/project/deadline",
                        "/project/scrap","/projects/apply","/projects/apply/cancel",
                        "/projects/apply/users","/projects/accept","/projects/reject",
                        "/chat/*", "/alarm", "/alarms")
                .excludePathPatterns("/home", "/login/apple");
    }
}
