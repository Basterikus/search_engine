package com.basterikus.SearchEngine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "config")
public class IndexConfig {
    private List<Map<String, String>> site;
}
