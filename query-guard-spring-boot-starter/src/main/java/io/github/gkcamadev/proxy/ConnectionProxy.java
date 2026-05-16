package io.github.gkcamadev.proxy;

import io.github.gkcamadev.core.QueryInspector;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;
import java.sql.Connection;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ConnectionProxy {

    public static Connection wrap(Connection realConnection, QueryInspector inspector) {
        try {
            return new ByteBuddy()
                    .subclass(Connection.class)
                    // Intercept query methods (prepareStatement, createStatement, prepareCall)
                    .method(ElementMatchers.nameStartsWith("prepare").or(ElementMatchers.nameStartsWith("create")))
                    .intercept(MethodDelegation.to(new StatementInterceptor(realConnection, inspector)))
                    // Forward ALL OTHER methods directly to the real connection using InvocationHandler
                    .method(ElementMatchers.any().and(ElementMatchers.not(ElementMatchers.nameStartsWith("prepare").or(ElementMatchers.nameStartsWith("create")))))
                    .intercept(InvocationHandlerAdapter.of(new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            try {
                                return method.invoke(realConnection, args);
                            } catch (InvocationTargetException e) {
                                throw e.getCause();
                            }
                        }
                    }))
                    .make()
                    .load(ConnectionProxy.class.getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return realConnection;
        }
    }

}
