package com.azizi.app.store.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "redis")
@Data
public class RedisProperties {

    private String host;
    private Integer port;

}
