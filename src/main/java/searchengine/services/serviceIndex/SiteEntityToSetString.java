package searchengine.services.serviceIndex;

import lombok.Getter;
import lombok.Setter;
import searchengine.config.Site;
import searchengine.model.SiteEntity;

import java.util.Set;

@Getter
@Setter
public class SiteEntityToSetString {
    private SiteEntity siteEntity;
    private Set<String> stringSet;

    public SiteEntityToSetString() {

    }
}
