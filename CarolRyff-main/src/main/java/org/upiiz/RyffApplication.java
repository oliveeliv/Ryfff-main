package org.upiiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RyffApplication {
    public static void main(String[] args) {
        System.setProperty("spring.datasource.url", "jdbc:mysql://avnadmin:AVNS_jkFIqSyj0Xb6hVBRxeO@mysql-2a4665bf-frodriguezo2401-cc7a.a.aivencloud.com:16664/defaultdb?ssl-mode=REQUIRED");
        System.setProperty("spring.datasource.username", "avnadmin");
        System.setProperty("spring.datasource.password", "AVNS_jkFIqSyj0Xb6hVBRxeO");
        System.setProperty("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");

        System.setProperty("spring.datasource.hikari.maximum-pool-size", "5");
        System.setProperty("spring.datasource.hikari.connection-timeout", "20000");

        System.setProperty("spring.jpa.hibernate.ddl-auto", "update");

        System.setProperty("spring.jpa.show-sql", "false");

        SpringApplication.run(RyffApplication.class, args);
    }
}