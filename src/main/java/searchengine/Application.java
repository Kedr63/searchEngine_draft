package searchengine;

import org.apache.logging.log4j.LogManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;
import searchengine.config.MyLogger;
import searchengine.config.SitesList;

import java.util.List;

@SpringBootApplication
@PropertySource("classpath:custom.properties")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        MyLogger.logger = LogManager.getLogger();

    }
}
