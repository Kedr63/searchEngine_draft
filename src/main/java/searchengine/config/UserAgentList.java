package searchengine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "user-agents-settings") // прописанные в файле 'application.yaml' UserAgent-ы появятся в этом объекте
public class UserAgentList {

    private List<UserAgent> userAgents;

    public UserAgentList(List<UserAgent> userAgents) {
        this.userAgents = userAgents;
    }
}
