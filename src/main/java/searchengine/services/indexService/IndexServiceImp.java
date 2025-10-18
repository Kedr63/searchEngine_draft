package searchengine.services.indexService;

import org.springframework.stereotype.Service;
import searchengine.config.ErrorMessageConfig;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.dtoToBD.SiteDto;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.exceptions.IllegalMethodException;
import searchengine.exceptions.NoSuchSiteException;
import searchengine.model.StatusIndex;
import searchengine.services.PoolService;
import searchengine.services.pageService.PageService;
import searchengine.services.siteService.SiteService;
import searchengine.services.utility.TimerExecution;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

@Service //при этой аннотации получим бины (объекты) прописанные в полях (но только если их добавим в конструктор)
//@Getter
//@Setter
public class IndexServiceImp implements IndexService {

    private final PoolService poolService;

    private final SitesList sitesList;

    private final ErrorMessageConfig errorMessageConfig;

   /* @Value("${errorIndexingNotRunning}")
    private String indexingNotProgressError;

    @Value("${errorIndexingAlreadyRunning}")
    private String errorIndexingAlreadyRunning;

    @Value("${errorMatchingSiteUrlOfSiteList}")
    private String errorMatchingSiteUrlOfSiteList;

    @Value("${errorIncompleteIndexing}")
    private String errorIncompleteIndexing;*/

    protected static long countOfHtmlParser = 1;

    protected static int coreAmount = Runtime.getRuntime().availableProcessors();

    protected static String tmp = "Return";

 //   long start;


    public IndexServiceImp(PoolService poolService, SitesList sitesList, ErrorMessageConfig errorMessageConfig) {
        this.poolService = poolService;
        this.sitesList = sitesList;
        this.errorMessageConfig = errorMessageConfig;
    }

    @Override
    public IndexingResponse startIndexing() {
        if (UtilitiesIndexing.indexingInProgress) {
            throw new IllegalMethodException(errorMessageConfig.getErrorIndexingAlreadyRunning());
        }

        UtilitiesIndexing.indexingInProgress = true; // при запущенной индексации это значение - true

        TimerExecution.startTime = System.currentTimeMillis();

        cascadeDeletionAllEntities();

        int sizeSitesList = sitesList.getSites().size();
        ExecutorService executorService = Executors.newFixedThreadPool(sizeSitesList);
        List<Future<IndexingResponse>> futureList = new ArrayList<>();

        for (int i = 0; i < sizeSitesList; i++) {
            Site site = sitesList.getSites().get(i);

            SiteDto siteDto = createSiteDto(site);
            siteDto = poolService.getSiteService().saveSiteDto(siteDto);

            ExecutorServiceForParsingSite executorServiceForParsingSite = new ExecutorServiceForParsingSite(siteDto, poolService);
            Future<IndexingResponse> futureResponseEntity = executorService.submit(executorServiceForParsingSite);

            futureList.add(futureResponseEntity);
        }

        executorService.shutdown();
        List<IndexingResponse> IndexingResponseList = UtilitiesIndexing.getIndexingResponseListFromFutureList(futureList);
        return UtilitiesIndexing.getTotalResultIndexingResponse(IndexingResponseList);
    }


    @Override
    public IndexingResponse stopIndexing() {

        if (!UtilitiesIndexing.indexingInProgress) {
            throw new IllegalMethodException(errorMessageConfig.getIndexingNotProgressError());
        }

        UtilitiesIndexing.stopStartIndexingMethod = true; // в классе HtmlParser поменяется флаг и рекурсия начнет останавливаться
        // и join-ы будут ждать результатов и возвращать результаты наверх в класс ExecutorServiceForParsingSite

        return UtilitiesIndexing.waitForCompleteStartIndexingAndTerminateStopIndexing();
    }

    @Override
    public IndexingResponse indexSinglePage(String page) {
        PageService pageService = poolService.getPageService();
        SiteService siteService = poolService.getSiteService();
        String regex = "(https://[^/]+)([^,\\s]+)"; // было "(https://[^,\s/]+)([^,\s]+)"
        String domainPartOfAddressUrl = page.replaceAll(regex, "$1");

        if (sitesList.getSites()
                .stream()
                .noneMatch(s -> s.getUrl().equals(domainPartOfAddressUrl))) {
            throw new NoSuchSiteException(errorMessageConfig.getErrorMatchingSiteUrlOfSiteList());
        }

        SiteDto siteDto;
        if (siteService.getSiteDtoByUrl(domainPartOfAddressUrl).isPresent()){
            siteDto = siteService.getSiteDtoByUrl(domainPartOfAddressUrl).get();
        } else throw new NoSuchSiteException(errorMessageConfig.getErrorIncompleteIndexing());


        String pageLocalUrl = page.replaceAll(regex, "$2");

        if (pageService.isPresentPageEntityWithThatPath(pageLocalUrl, siteDto.getId())) {
            int idPageEntity = pageService.getIdPageEntity(pageLocalUrl, siteDto.getId());
            cascadeDeletionPageEntities(idPageEntity);
            Logger.getLogger(IndexServiceImp.class.getName()).info("delete Page cascade - " + pageLocalUrl);
        }
        UtilitiesIndexing.isStartSinglePageIndexing(); // метод поменяет флаг на TRUE для экземпляра HtmlParser
        // чтоб пропарсить только одну страницу
        HtmlParser htmlParser = new HtmlParser(page, siteDto, poolService);
        htmlParser.compute();
        UtilitiesIndexing.isDoneIndexingSinglePage(); // повернем флаг на место как был, после \indexPage(String page)\


        return new IndexingResponse(true);
    }


    private SiteDto createSiteDto(Site site) {
        SiteDto siteDto = new SiteDto();
        siteDto.setStatusIndex(StatusIndex.INDEXING);
        siteDto.setStatusTime(LocalDateTime.now());
        siteDto.setUrl(site.getUrl());
        siteDto.setName(site.getName());
       // siteEntity.setLastError("");

        return siteDto;
    }


    private String getShortMessageOfException(Exception e) {
        StringBuilder builder = new StringBuilder();
        String[] arrayMessage = e.getMessage().split(":");
        List<String> stringList = Arrays.stream(arrayMessage).toList();
        builder.append(stringList.get(stringList.size() - 2)).append(" - ");
        builder.append(stringList.get(stringList.size() - 1));
        return builder.toString();
    }


    private void cascadeDeletionAllEntities() {  // каскадно удаляем сущности начиная с дочерних до родительских
        poolService.getIndexEntityService().deleteAllIndexEntity();
        poolService.getLemmaService().deleteAllLemmaEntities();
        poolService.getPageService().deleteAllPageEntity();
        poolService.getSiteService().deleteAllSiteEntity();
    }

    private void cascadeDeletionPageEntities(int idPageEntity) {
        List<Integer> idLemmaList = poolService.getIndexEntityService().getIdLemmaByPageId(idPageEntity);
        poolService.getIndexEntityService().deleteIndexEntityWherePageId(idPageEntity);
        poolService.getLemmaService().updateLemmaFrequency(idLemmaList);
        // poolService.getLemmaService().deleteLemmaEntityById(idLemmaList);
        poolService.getPageService().deletePageById(idPageEntity);
    }


}



