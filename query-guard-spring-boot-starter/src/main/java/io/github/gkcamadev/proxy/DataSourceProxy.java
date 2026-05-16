package io.github.gkcamadev.proxy;

import io.github.gkcamadev.core.QueryInspector;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;

public class DataSourceProxy {

    public static DataSource wrap(DataSource realDataSource, QueryInspector inspector) {
        try {
            return new ByteBuddy()
                    .subclass(DataSource.class)
                    .method(ElementMatchers.any())
                    .intercept(InvocationHandlerAdapter.of(new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            if (method.getName().equals("getConnection")) {
                                System.out.println(" [QueryGuard] Intercepted: getConnection");
                                Connection realConnection = (Connection) method.invoke(realDataSource, args);
                                // ConnectionProxy එකට Inspector එක පාස් කරනවා!
                                return ConnectionProxy.wrap(realConnection, inspector);
                            }
                            try {
                                return method.invoke(realDataSource, args);
                            } catch (InvocationTargetException e) {
                                throw e.getCause();
                            }
                        }
                    }))
                    .make()
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
