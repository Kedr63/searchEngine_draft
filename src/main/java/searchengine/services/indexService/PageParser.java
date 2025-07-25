package searchengine.services.indexService;

import searchengine.dto.dtoToBD.SiteDto;
import searchengine.services.PoolService;

public class PageParser extends HtmlParser{

    public PageParser(String url, SiteDto siteDto, PoolService poolService) {
        super(url, siteDto, poolService);
    }


}
