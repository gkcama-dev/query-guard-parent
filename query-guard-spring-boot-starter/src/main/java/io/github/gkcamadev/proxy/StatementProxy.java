package io.github.gkcamadev.proxy;

import io.github.gkcamadev.core.QueryInspector;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Statement;

public class StatementProxy {

    public static Statement wrap(Statement realStatement, QueryInspector inspector) {
        try {
            return new ByteBuddy()
                    .subclass(Statement.class)
                    // Match all methods EXCEPT basic Object methods
                    .method(ElementMatchers.any().and(ElementMatchers.not(ElementMatchers.isDeclaredBy(Object.class))))
                    .intercept(InvocationHandlerAdapter.of(new InvocationHandler() {

                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                            boolean isExecute = method.getName().startsWith("execute");
                            String sql = (args != null && args.length > 0 && args[0] instanceof String) ? (String) args[0] : null;

                            if (isExecute && sql != null) {
                                inspector.inspect(sql);
                            }

                            long startTime = System.nanoTime();

                            try {
                                // Original Query -> Database
                                Object result = method.invoke(realStatement, args);

                                // Query Execution Time Calculate
                                if (isExecute && sql != null) {
                                    long endTime = System.nanoTime();
                                    double executionTimeMs = (endTime - startTime) / 1_000_000.0;
                                    System.out.printf("   ⏱️ [QueryGuard] Execution Time: %.3f ms for -> [%s]%n", executionTimeMs, sql);
                                }

                                return result;

                            } catch (InvocationTargetException e) {
                                throw e.getCause();
                            }
                        }
                    }))
                    .make()
                    .load(StatementProxy.class.getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return realStatement;
        }
    }
}
