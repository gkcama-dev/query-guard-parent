package io.github.gkcamadev.proxy;

import io.github.gkcamadev.core.QueryInspector;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

import java.sql.Connection;
import java.sql.Statement;
import java.lang.reflect.Method;

public class StatementInterceptor {

    private final Connection realConnection;
    private final QueryInspector inspector = new QueryInspector();

    public StatementInterceptor(Connection realConnection) {
        this.realConnection = realConnection;
    }

    @RuntimeType
    public Object intercept(@Origin Method method, @AllArguments Object[] args) throws Exception {

        // Inspect SQL if it's prepareStatement
        if (args != null && args.length > 0 && args[0] instanceof String) {
            String sql = (String) args[0];
            inspector.inspect(sql);
        }

        // Call real method (createStatement or prepareStatement)
        Object result = method.invoke(realConnection, args);

        // If the result is a Statement, wrap it to catch execute(sql) later
        if (result instanceof Statement) {
            return StatementProxy.wrap((Statement) result);
        }

        return result;
    }
}
