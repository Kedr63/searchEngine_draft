package searchengine.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "error-message")
public class ErrorMessageConfig {
    private String indexingNotProgressError;
    private String errorIndexingAlreadyRunning;
    private String errorMatchingSiteUrlOfSiteList;
    private String errorIncompleteIndexing;
    private String errorIndexingStopUser;
}
