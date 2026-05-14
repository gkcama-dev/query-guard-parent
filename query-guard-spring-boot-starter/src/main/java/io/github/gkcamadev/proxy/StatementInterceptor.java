package io.github.gkcamadev.proxy;

import io.github.gkcamadev.core.QueryInspector;
import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import java.sql.Connection;

public class StatementInterceptor {

    private final Connection realConnection;
    private final QueryInspector inspector = new QueryInspector();

    public StatementInterceptor(Connection realConnection) {
        this.realConnection = realConnection;
    }

    @RuntimeType
    public Object intercept(@Argument(0) String sql) throws Exception {
        inspector.inspect(sql);

        return realConnection.prepareStatement(sql);
    }

}
