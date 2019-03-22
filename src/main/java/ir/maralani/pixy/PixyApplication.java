package ir.maralani.pixy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = "ir.maralani")
@SpringBootApplication
@EnableJpaRepositories
@EnableConfigurationProperties
public class PixyApplication {

    public static void main(String[] args) {

        SpringApplication.run(PixyApplication.class, args);
    }

}
