package searchengine.services.statisticService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.services.SiteService;
import searchengine.services.indexService.PoolService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final PoolService poolService;
    private final SitesList sites;

    @Override
    public ResponseEntity<StatisticsResponse> getStatistics() {
     //   String[] statuses = { "INDEXED", "FAILED", "INDEXING" };
//        String[] errors = {
//                "Ошибка индексации: главная страница сайта не доступна",
//                "Ошибка индексации: сайт не доступен",
//                ""};

        TotalStatistics total = new TotalStatistics();
        total.setSites(sites.getSites().size());
      //  total.setIndexing(true);

        List<DetailedStatisticsItem> statisticsItems = new ArrayList<>();
        List<Site> sitesList = sites.getSites();
        for (Site site : sitesList) {
            SiteService siteService = poolService.getSiteService();
            int idSiteEntity = siteService.getIdSiteEntityByUrl(site.getUrl());

            DetailedStatisticsItem item = new DetailedStatisticsItem();
            item.setName(site.getName());
            item.setUrl(site.getUrl());
            int pages = poolService.getPageService().getCountPagesOfSite(idSiteEntity);
            int lemmas = poolService.getLemmaService().getCountLemmasOfSite(idSiteEntity);
            item.setPages(pages);
            item.setLemmas(lemmas);
            item.setStatus(String.valueOf(siteService.getSiteEntity(idSiteEntity).getStatus()));
            item.setError(siteService.getSiteEntity(idSiteEntity).getLastError());
            item.setStatusTime(siteService.getSiteEntity(idSiteEntity).getStatusTime());
            total.setPages(total.getPages() + pages);
            total.setLemmas(total.getLemmas() + lemmas);
            if (item.getError()==null){
                total.setIndexing(true);
            }
            statisticsItems.add(item);
        }

        StatisticsResponse statisticsResponse = new StatisticsResponse();
        StatisticsData data = new StatisticsData();
        data.setTotal(total);
        data.setDetailed(statisticsItems);
        statisticsResponse.setStatistics(data);
        statisticsResponse.setResult(true);
        return new ResponseEntity<>(statisticsResponse, HttpStatus.OK);
    }
}
