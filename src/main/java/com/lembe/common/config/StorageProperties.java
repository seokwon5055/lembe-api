package com.lembe.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {

    private String type = "local";   // local | ncloud
    private Local local = new Local();

    @Getter
    @Setter
    public static class Local {
        private String basePath = "./uploads";
        private String baseUrl = "http://localhost:8080/files";
    }
}
