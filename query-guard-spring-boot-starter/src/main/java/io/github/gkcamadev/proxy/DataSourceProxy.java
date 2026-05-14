package io.github.gkcamadev.proxy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import javax.sql.DataSource;

public class DataSourceProxy {

    public static DataSource wrap(DataSource realDataSource) {

        try{

            return new ByteBuddy()
                    .subclass(DataSource.class)
                    .method(ElementMatchers.named("getConnection"))
                    .intercept(MethodDelegation.to(new ConnectionInterceptor(realDataSource)))
                    .make()
                    .load(DataSource.class.getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor()
                    .newInstance();

        }catch (Exception e){
            return realDataSource;
        }

    }

}
