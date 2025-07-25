package searchengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@PropertySource("classpath:custom.properties")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

//        ApplicationContext context = SpringApplication.run(Application.class, args);
//        SnippetSearcherConfiguration snippetSearcherConfiguration = context.getBean(SnippetSearcherConfiguration.class);
//        System.out.println("outputLimiter - " + snippetSearcherConfiguration.getOutputLimiter());
//        System.out.println("regex " + snippetSearcherConfiguration.getRegexSearcher());
//
//        PoolService poolService = context.getBean(PoolService.class);
//        SnippetSearcher snippetSearcher = new SnippetSearcherRegexImpl(poolService);
//        System.out.println(23);


      //  MyLogger.logger = LogManager.getLogger();

    }
}
