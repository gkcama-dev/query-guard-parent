package io.github.gkcamadev.core;

import javax.sql.DataSource;

public class QueryInspector {

    public void inspect(String sql) {
        if(sql.toUpperCase().contains("SELECT *")){
            printWarning("Anti-pattern detected: Avoid using 'SELECT *'. Specific columns are better for performance.");
        }
    }

    private void printWarning(String message) {
        System.out.println("\u001B[33m" + " [QueryGuard WARNING] " + message + "\u001B[0m");
    }

}
