package searchengine.services;

import lombok.Getter;
import org.springframework.stereotype.Service;
import searchengine.config.SnippetSearcherConfiguration;
import searchengine.config.UserAgentList;
import searchengine.services.indexEntityService.IndexEntityService;
import searchengine.services.lemmaService.LemmaService;
import searchengine.services.pageService.PageService;
import searchengine.services.siteService.SiteService;


@Getter
@Service
public class PoolServiceImpl implements PoolService {

    private final SiteService siteService;
    private final PageService pageService;
    private final LemmaService lemmaService;
    private final IndexEntityService indexEntityService;
    private final UserAgentList userAgentList;
    private final SnippetSearcherConfiguration snippetSearcherConfiguration;

    public PoolServiceImpl(SiteService siteService, PageService pageService, LemmaService lemmaService,
                           IndexEntityService indexEntityService, UserAgentList userAgentList, SnippetSearcherConfiguration snippetSearcherConfiguration) {
        this.siteService = siteService;
        this.pageService = pageService;
        this.lemmaService = lemmaService;
        this.indexEntityService = indexEntityService;
        this.userAgentList = userAgentList;
        this.snippetSearcherConfiguration = snippetSearcherConfiguration;
    }
}
