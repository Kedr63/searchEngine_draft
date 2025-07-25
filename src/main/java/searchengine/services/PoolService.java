package searchengine.services;

import searchengine.config.SnippetSearcherConfiguration;
import searchengine.config.UserAgentList;
import searchengine.services.indexEntityService.IndexEntityService;
import searchengine.services.lemmaService.LemmaService;
import searchengine.services.pageService.PageService;
import searchengine.services.siteService.SiteService;

public interface PoolService {

    SiteService getSiteService();
    PageService getPageService();
    LemmaService getLemmaService();
    IndexEntityService getIndexEntityService();
    UserAgentList getUserAgentList();

    SnippetSearcherConfiguration getSnippetSearcherConfiguration();
}
