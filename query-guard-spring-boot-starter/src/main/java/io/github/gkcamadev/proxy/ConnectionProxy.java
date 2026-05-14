package io.github.gkcamadev.proxy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import java.sql.Connection;

public class ConnectionProxy {

    public static Connection wrap(Connection realConnection) {
        try {
            return new ByteBuddy()
                    .subclass(Connection.class)
                    .method(ElementMatchers.named("prepareStatement")
                            .or(ElementMatchers.named("createStatement"))
                            .or(ElementMatchers.named("prepareCall")))
                    .intercept(MethodDelegation.to(new StatementInterceptor(realConnection)))
                    .make()
                    .load(Connection.class.getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (Exception e) {
            return realConnection;
        }
    }

}
