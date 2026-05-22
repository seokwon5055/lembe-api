package com.lembe.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final StorageProperties storageProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 로컬 파일 서빙: GET /files/** → ./uploads/**
        String basePath = storageProperties.getLocal().getBasePath();
        String location = basePath.startsWith("/") ? "file:" + basePath + "/"
                                                    : "file:./" + basePath.replaceFirst("^\\./", "") + "/";
        registry.addResourceHandler("/files/**")
                .addResourceLocations(location);
    }
}
