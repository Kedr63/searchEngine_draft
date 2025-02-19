package searchengine.services.indexService;

import searchengine.model.SiteEntity;
import searchengine.services.PoolService;

public class PageParser extends HtmlParser{

    public PageParser(String url, SiteEntity siteEntity, PoolService poolService) {
        super(url, siteEntity, poolService);
    }


}
