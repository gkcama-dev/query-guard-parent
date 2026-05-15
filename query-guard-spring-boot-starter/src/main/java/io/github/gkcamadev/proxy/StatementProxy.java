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

    public static Statement wrap(Statement realStatement) {
        try {
            return new ByteBuddy()
                    .subclass(Statement.class)
                    // Match all methods EXCEPT basic Object methods
                    .method(ElementMatchers.any().and(ElementMatchers.not(ElementMatchers.isDeclaredBy(Object.class))))
                    .intercept(InvocationHandlerAdapter.of(new InvocationHandler() {
                        private final QueryInspector inspector = new QueryInspector();

                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                            // Check if method is executed, executeQuery, or executeUpdate AND has SQL string
                            if (method.getName().startsWith("execute") && args != null && args.length > 0 && args[0] instanceof String) {
                                String sql = (String) args[0];
                                inspector.inspect(sql);
                            }

                            try {
                                // Call the real method
                                return method.invoke(realStatement, args);
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
