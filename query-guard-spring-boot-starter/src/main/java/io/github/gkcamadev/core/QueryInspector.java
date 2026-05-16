package io.github.gkcamadev.core;

import javax.sql.DataSource;

import io.github.gkcamadev.config.QueryGuardProperties;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryInspector {

    private static final Logger log = LoggerFactory.getLogger(QueryInspector.class);
    private final QueryGuardProperties properties;

    public QueryInspector(QueryGuardProperties properties) {
        this.properties = properties;
    }

    public void inspect(String sql) {

        if(!properties.isEnabled())
            return;

        try{
           // SQL Parse
           Statement statement = (Statement) CCJSqlParserUtil.parse(sql);

           // Check SELECT *
           if(statement instanceof Select){
               Select select = (Select) statement;
               if(select.getSelectBody() instanceof PlainSelect){
                   PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
                   for(SelectItem item : plainSelect.getSelectItems()) {
                       if (item.toString().equals("*")){
                           log.warn("[QueryGuard] ANTI-PATTERN: 'SELECT *' detected in query! -> [{}]", sql);
                       }
                   }
               }
           }

            // Check DROP TABLE
            if (statement instanceof Drop && properties.isBlockDrop()) {
                log.error("[QueryGuard] CRITICAL: DROP TABLE blocked! -> [{}]", sql);
                throw new SecurityException("QueryGuard blocked a DROP statement: " + sql);
            }

            // Check DELETE without WHERE
            if (statement instanceof Delete && properties.isBlockMassDelete()) {
                Delete delete = (Delete) statement;
                if (delete.getWhere() == null) {
                    log.error("[QueryGuard] CRITICAL: DELETE without WHERE blocked! -> [{}]", sql);
                    throw new SecurityException("QueryGuard blocked a massive DELETE statement: " + sql);
                }
            }

        }catch(Exception e){
            if (e instanceof SecurityException) throw (SecurityException) e;
             log.warn("[QueryGuard] Could not parse SQL, skipping inspection: " + sql);
        }

    }

    private void printWarning(String message) {
        System.out.println("\u001B[33m" + " [QueryGuard WARNING] " + message + "\u001B[0m");
    }

    private void printError(String message) {
        System.out.println("\u001B[31m" + " [QueryGuard ERROR] " + message + "\u001B[0m");
    }

}
