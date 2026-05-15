package io.github.gkcamadev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
        @Bean
        public CommandLineRunner run (JdbcTemplate jdbcTemplate){
            return args -> {
                System.out.println("🚀 [SampleApp] Starting database operations...\n");

                System.out.println("✅ [SampleApp] Creating table and inserting initial data...");
                jdbcTemplate.execute("CREATE TABLE students (id INT, name VARCHAR(255))");
                jdbcTemplate.execute("INSERT INTO students VALUES (1, 'Kamal')");
                jdbcTemplate.execute("INSERT INTO students VALUES (2, 'Nimal')");
                jdbcTemplate.execute("INSERT INTO students VALUES (3, 'Sunil')");

                System.out.println("\n✅ [SampleApp] Executing safe query (Specific columns)...");
                jdbcTemplate.queryForList("SELECT name FROM students WHERE id = 1");

                System.out.println("\n⚠️ [SampleApp] Executing bad practice query (SELECT *)...");
                jdbcTemplate.queryForList("SELECT * FROM students");

                System.out.println("\n✅ [SampleApp] Executing safe DELETE (With WHERE clause)...");
                jdbcTemplate.execute("DELETE FROM students WHERE id = 3");

                System.out.println("\n💀 [SampleApp] Executing malicious DELETE query (No WHERE clause)...");
                try {
                    jdbcTemplate.execute("DELETE FROM students");
                    System.out.println("❌ [SampleApp] Oops! The query ran successfully. QueryGuard failed!");
                } catch (Exception e) {
                    System.out.println("🛡️ [SampleApp] SUCCESS! Query was blocked by QueryGuard: " + e.getMessage());
                }

                System.out.println("\n☢️ [SampleApp] Executing catastrophic DROP TABLE query...");
                try {
                    jdbcTemplate.execute("DROP TABLE students");
                    System.out.println("❌ [SampleApp] Oops! Table dropped. QueryGuard failed!");
                } catch (Exception e) {
                    System.out.println("🛡️ [SampleApp] SUCCESS! Query was blocked by QueryGuard: " + e.getMessage());
                }

                System.out.println("\n🎉 [SampleApp] All database operations completed. Database is still safe!");

            };
        }
}