package io.github.gkcamadev.core;

import javax.sql.DataSource;

public class QueryInspector {

    public void inspect(String sql) {

        String upperSql = sql.toUpperCase().trim();

        // Rule 1: SELECT * (Warning)
        if(upperSql.contains("SELECT *")){
            printWarning("Anti-pattern detected: Avoid using 'SELECT *'. Specific columns are better for performance.");
        }

        // Rule 2: DROP TABLE (Block)
        if (upperSql.contains("DROP TABLE")) {
            printError("CRITICAL DANGER: 'DROP TABLE' statement detected and blocked!");
            throw new SecurityException("QueryGuard blocked a DROP TABLE statement: " + sql);
        }

        // Rule 3: Blocked: DELETE statement requires a WHERE clause.
        if (upperSql.startsWith("DELETE FROM") && !upperSql.contains("WHERE")) {
            printError("CRITICAL DANGER: 'DELETE' statement without 'WHERE' clause detected and blocked!");
            throw new SecurityException("QueryGuard blocked a massive DELETE statement: " + sql);
        }
    }

    private void printWarning(String message) {
        System.out.println("\u001B[33m" + " [QueryGuard WARNING] " + message + "\u001B[0m");
    }

    private void printError(String message) {
        System.out.println("\u001B[31m" + " [QueryGuard ERROR] " + message + "\u001B[0m");
    }

}
