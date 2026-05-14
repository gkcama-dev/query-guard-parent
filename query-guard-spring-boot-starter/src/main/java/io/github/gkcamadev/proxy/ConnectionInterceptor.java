package io.github.gkcamadev.proxy;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;

import javax.sql.DataSource;
import java.sql.Connection;

public class ConnectionInterceptor {

    private final DataSource realDataSource;

    public ConnectionInterceptor(DataSource realDataSource) {
        this.realDataSource = realDataSource;
    }

    @RuntimeType
    public Connection intercept() throws Exception {
        Connection realConnection = realDataSource.getConnection();
        System.out.println("🔌 [QueryGuard] Connection established and wrapped.");
        return ConnectionProxy.wrap(realConnection);
    }
}
