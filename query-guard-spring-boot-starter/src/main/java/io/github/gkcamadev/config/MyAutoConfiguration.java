package io.github.gkcamadev.config;

import io.github.gkcamadev.core.QueryInspector;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
// Enable New Properties
@EnableConfigurationProperties(QueryGuardProperties.class)
public class MyAutoConfiguration {

    @Bean
    public QueryInspector queryInspector(QueryGuardProperties properties) {
        return new QueryInspector(properties);
    }

    @Bean
    public static DataSourcePostProcessor dataSourcePostProcessor(QueryInspector inspector) {
        return new DataSourcePostProcessor(inspector);
    }

}
