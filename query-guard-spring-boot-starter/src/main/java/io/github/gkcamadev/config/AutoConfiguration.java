package io.github.gkcamadev.config;

import org.springframework.context.annotation.Bean;

public class AutoConfiguration {

    @Bean
    public  DataSourcePostProcessor dataSourcePostProcessor(){
        return new DataSourcePostProcessor();
    }

}
