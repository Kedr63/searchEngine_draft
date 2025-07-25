package searchengine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "snippet-searcher")
public class SnippetSearcherConfiguration {
    private int outputLimiter;
    private String regexSearcher;
}

/** Этот класс создал для эксперимента, чтоб в конфигурационном файле \application.yaml\ задать переменные для сниппетов,
 *  а не в class SnippetSearcherRegexImpl implements SnippetSearcher */
