package io.github.gkcamadev.config;

import io.github.gkcamadev.proxy.DataSourceProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import javax.sql.DataSource;

public class DataSourcePostProcessor  implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSource) {
            System.out.println("DEBUG: Wrapping DataSource bean: " + beanName);
            return DataSourceProxy.wrap((DataSource) bean);
        }
        return bean;
    }
}
