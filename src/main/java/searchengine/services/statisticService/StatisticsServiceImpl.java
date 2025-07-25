package searchengine.services.statisticService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.dtoToBD.SiteDto;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.services.PoolService;
import searchengine.services.siteService.SiteService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
// добавляет конструктор с аргументами, соответствующими неинициализированным final-полям класса
public class StatisticsServiceImpl implements StatisticsService {

    private final PoolService poolService;
    private final SitesList sites;

    @Override
    public StatisticsResponse getStatistics() {

        TotalStatistics total = new TotalStatistics();
        total.setSites(sites.getSites().size());
        //  total.setIndexing(true);

        List<DetailedStatisticsItem> statisticsItems = new ArrayList<>();
        List<Site> sitesList = sites.getSites();
        for (Site site : sitesList) {
            DetailedStatisticsItem item = new DetailedStatisticsItem();
            item.setName(site.getName());
            item.setUrl(site.getUrl());

            SiteService siteService = poolService.getSiteService();
            //   int idSiteEntity = siteService.getIdSiteEntityByUrl(site.getUrl());
            if (siteService.getSiteDtoByUrl(site.getUrl()).isPresent()) {
                SiteDto siteDto = siteService.getSiteDtoByUrl(site.getUrl()).get();
                int idSiteDto = siteDto.getId();
                int pages = poolService.getPageService().getCountPagesOfSite(idSiteDto);
              //  int pages = siteDto.getPageDtoList().size();
                int lemmas = poolService.getLemmaService().getCountLemmasOfSite(idSiteDto);
                item.setPages(pages);
                item.setLemmas(lemmas);
                item.setStatus(String.valueOf(siteDto.getStatusIndex()));
                item.setError(siteDto.getLastError());
                item.setStatusTime(siteDto.getStatusTime());

                //   SiteDto siteDto = siteService.getSiteDto();
                total.setPages(total.getPages() + pages);
                total.setLemmas(total.getLemmas() + lemmas);
            }
            else { // site еще не индексирован
                item.setPages(0);
                item.setLemmas(0);
            }
            statisticsItems.add(item);
        }

        for (DetailedStatisticsItem item : statisticsItems) {
            if (item.getError() != null) {
                total.setIndexing(false);
                break;
            } else total.setIndexing(true);
        }

        StatisticsResponse statisticsResponse = new StatisticsResponse();
        StatisticsData data = new StatisticsData();
        data.setTotal(total);
        data.setDetailed(statisticsItems);
        statisticsResponse.setStatistics(data);
        statisticsResponse.setResult(total.isIndexing());

        return statisticsResponse;
    }
}
