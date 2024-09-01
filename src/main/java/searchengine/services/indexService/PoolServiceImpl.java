package searchengine.services.indexService;

import org.springframework.stereotype.Service;
import searchengine.config.UserAgentList;
import searchengine.services.IndexEntityService;
import searchengine.services.LemmaService;
import searchengine.services.PageService;
import searchengine.services.SiteService;

@Service
public class PoolServiceImpl implements PoolService {

    private final SiteService siteService;
    private final PageService pageService;
    private final LemmaService lemmaService;
    private final IndexEntityService indexEntityService;
    private final UserAgentList userAgentList;

    public PoolServiceImpl(SiteService siteService, PageService pageService, LemmaService lemmaService,
                           IndexEntityService indexEntityService, UserAgentList userAgentList) {
        this.siteService = siteService;
        this.pageService = pageService;
        this.lemmaService = lemmaService;
        this.indexEntityService = indexEntityService;
        this.userAgentList = userAgentList;
    }

    @Override
    public SiteService getSiteService() {
        return siteService;
    }

    @Override
    public PageService getPageService() {
        return pageService;
    }

    @Override
    public LemmaService getLemmaService() {
        return lemmaService;
    }

    @Override
    public IndexEntityService getIndexEntityService() {
        return indexEntityService;
    }

    @Override
    public UserAgentList getUserAgentList() {
        return userAgentList;
    }
}
