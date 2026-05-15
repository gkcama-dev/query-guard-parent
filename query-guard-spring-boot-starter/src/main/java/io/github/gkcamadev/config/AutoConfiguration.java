package io.github.gkcamadev.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoConfiguration {

    @Bean
    public static DataSourcePostProcessor dataSourcePostProcessor(){
        return new DataSourcePostProcessor();
    }

}
