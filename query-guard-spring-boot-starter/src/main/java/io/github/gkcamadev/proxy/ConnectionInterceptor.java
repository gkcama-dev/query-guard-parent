package io.github.gkcamadev.proxy;

import io.github.gkcamadev.core.QueryInspector;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.lang.reflect.Method;

public class ConnectionInterceptor {

    private final DataSource realDataSource;
    private final QueryInspector inspector;

    public ConnectionInterceptor(DataSource realDataSource, QueryInspector inspector) {
        this.realDataSource = realDataSource;
        this.inspector = inspector;
    }

    @RuntimeType
    public Connection intercept(@Origin Method method,@AllArguments Object[] args) throws Exception {

        System.out.println(" [QueryGuard] Intercepted: " + method.getName());

        // Call original method (getConnection)
        Connection realConnection = (Connection) method.invoke(realDataSource, args);

        // Wrap the connection
        return ConnectionProxy.wrap(realConnection, inspector);
    }
}
