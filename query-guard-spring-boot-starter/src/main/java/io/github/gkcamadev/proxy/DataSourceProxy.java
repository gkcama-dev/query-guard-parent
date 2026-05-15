package io.github.gkcamadev.proxy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import javax.sql.DataSource;

public class DataSourceProxy {

    public static DataSource wrap(DataSource realDataSource) {
        try {
            return new ByteBuddy()
                    .subclass(DataSource.class)
                    // Match all methods EXCEPT basic Object methods (e.g., equals, hashCode, toString)
                    .method(ElementMatchers.any().and(ElementMatchers.not(ElementMatchers.isDeclaredBy(Object.class))))
                    .intercept(MethodDelegation.to(realDataSource))
                    // Intercept ONLY getConnection methods
                    .method(ElementMatchers.named("getConnection"))
                    .intercept(MethodDelegation.to(new ConnectionInterceptor(realDataSource)))
                    .make()
                    // Safe class loading
                    .load(DataSourceProxy.class.getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return realDataSource;
        }
    }
}
