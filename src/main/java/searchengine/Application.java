package searchengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:custom.properties")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

      //  MyLogger.logger = LogManager.getLogger();

    }
}
