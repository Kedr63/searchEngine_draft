package searchengine.services.indexService;

import searchengine.config.UserAgentList;
import searchengine.services.IndexEntityService;
import searchengine.services.LemmaService;
import searchengine.services.PageService;
import searchengine.services.SiteService;

public interface PoolService {

    SiteService getSiteService();
    PageService getPageService();
    LemmaService getLemmaService();
    IndexEntityService getIndexEntityService();
    UserAgentList getUserAgentList();
}
