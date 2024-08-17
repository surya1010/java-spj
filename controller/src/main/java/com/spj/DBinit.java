package com.spj;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
public class DBinit {

  private final JdbcTemplate jdbcTemplate;

  public DBinit(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Bean
  @ConditionalOnProperty(prefix = "app", name = "db.init.enabled", havingValue = "true")
  public CommandLineRunner demoCommandLineRunner() {
    return args -> {

      try {
        System.out.println("Running dummy data insert to db.....");

        // Create tables
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS tickets (" +
            "id BIGSERIAL PRIMARY KEY, " +
            "name VARCHAR(255) NOT NULL, " +
            "total_quantity BIGINT NOT NULL, " +
            "available_quantity BIGINT NOT NULL, " +
            "is_available INT NOT NULL DEFAULT 0)"); // Menambahkan default value

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS ticket_bookings (" +
            "id BIGSERIAL PRIMARY KEY, " +
            "ticket_id BIGINT NOT NULL, " +
            "quantity INT NOT NULL, " +
            "booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (ticket_id) REFERENCES tickets(id))");

        // Create indices
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_ticket_id ON ticket_bookings(ticket_id)");

        // Insert initial data (optional)
        jdbcTemplate.update("INSERT INTO tickets (name, total_quantity, available_quantity, is_available) VALUES (?, ?, ?, ?)",
            "Concert A", 1000, 1000, 1); // Menyertakan nilai untuk is_available
        jdbcTemplate.update("INSERT INTO tickets (name, total_quantity, available_quantity, is_available) VALUES (?, ?, ?, ?)",
            "Concert B", 2000, 2000, 1); // Menyertakan nilai untuk is_available

      } catch (Exception e) {
        System.err.println("Error executing CommandLineRunner: " + e.getMessage());
        e.printStackTrace();
      }

    };
  }
}



