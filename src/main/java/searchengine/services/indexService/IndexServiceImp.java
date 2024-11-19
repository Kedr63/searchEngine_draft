package searchengine.services.indexService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.ResultResponseError;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.exceptions.IllegalMethodException;
import searchengine.exceptions.IncompleteIndexingException;
import searchengine.exceptions.NoSuchSiteException;
import searchengine.exceptions.UtilityException;
import searchengine.model.SiteEntity;
import searchengine.model.StatusIndex;
import searchengine.services.PageService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
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

    @Value("${errorIndexingNotRunning}")
    private String indexingNotProgressError;

    @Value("${errorIndexingAlreadyRunning}")
    private String errorIndexingAlreadyRunning;

    @Value("${errorMatchingSiteUrlOfSiteList}")
    private String errorMatchingSiteUrlOfSiteList;

    @Value("${errorIncompleteIndexing}")
    private String errorIncompleteIndexing;

    protected static long countOfHtmlParser = 1;

    protected static int coreAmount = Runtime.getRuntime().availableProcessors();

    protected static String tmp = "Return";

    long start;


    public IndexServiceImp(PoolService poolService, SitesList sitesList) {
        this.poolService = poolService;
        this.sitesList = sitesList;
    }

    @Override
    public IndexingResponse startIndexing() {
        if (UtilitiesIndexing.indexingInProgress) {
            throw new IllegalMethodException(errorIndexingAlreadyRunning);
        }

        //   UtilitiesIndexing.StopStartIndexing = true; // при запущенной индексации это значение - true
        UtilitiesIndexing.indexingInProgress = true; // при запущенной индексации это значение - true

        start = System.currentTimeMillis();

        cascadeDeletionAllEntities();

        int sizeSitesList = sitesList.getSites().size();
        ExecutorService executorService = Executors.newFixedThreadPool(sizeSitesList);
        List<Future<IndexingResponse>> futureList = new ArrayList<>();

        for (int i = 0; i < sizeSitesList; i++) {
            Site site = sitesList.getSites().get(i);

            SiteEntity siteEntity = createSiteEntity(site);
            poolService.getSiteService().saveSiteEntity(siteEntity);

            ExecutorServiceForParsingSite executorServiceForParsingSite = new ExecutorServiceForParsingSite(siteEntity, poolService);
            Future<IndexingResponse> futureResponseEntity = executorService.submit(executorServiceForParsingSite);

            futureList.add(futureResponseEntity);
        }

        executorService.shutdown();
        List<IndexingResponse> IndexingResponseList = getIndexingResponseListFromFutureList(futureList);
        return getResultIndexingResponse(IndexingResponseList);

    }


    @Override
    public IndexingResponse stopIndexing() {

        if (!UtilitiesIndexing.indexingInProgress) {
            throw new IllegalMethodException(indexingNotProgressError);
        }

        UtilitiesIndexing.stopStartIndexingMethod = true; // в классе HtmlParser поменяется флаг и рекурсия начнет останавливаться
        // и join-ы будут ждать результатов и возвращать рузультаты наверх в класс ExecutorServiceForParsingSite

        return UtilitiesIndexing.waitForCompleteStartIndexingAndTerminateStopIndexing();
    }

    @Override
    public IndexingResponse indexSinglePage(String page) {
        String decodedUrl = URLDecoder.decode(page, StandardCharsets.UTF_8);
        PageService pageService = poolService.getPageService();
        String regex = "(https://[^,\\s/]+)([^,\\s]+)";
       // String pageUrl = page.getUrl();
        String domainPartOfAddressUrl = decodedUrl.replaceAll(regex, "$1");

        if (sitesList.getSites()
                .stream()
                .noneMatch(s -> s.getUrl().equals(domainPartOfAddressUrl))) {
            throw new NoSuchSiteException(errorMatchingSiteUrlOfSiteList);
        }

        SiteEntity siteEntity = poolService.getSiteService().getSiteEntityByUrl(domainPartOfAddressUrl);
        String pageLocalUrl = decodedUrl.replaceAll(regex, "$2");

        if (pageService.isPresentPageEntityWithThatPath(pageLocalUrl, siteEntity.getId())) {
            int idPageEntity = pageService.getIdPageEntity(pageLocalUrl, siteEntity.getId());
            cascadeDeletionPageEntities(idPageEntity);
            Logger.getLogger(IndexServiceImp.class.getName()).info("delete Page cascade - " + pageLocalUrl);
        }
        UtilitiesIndexing.isStartSinglePageIndexing(); // метод поменяет флаг на TRUE для экземпляра HtmlParser
        // чтоб пропарсить только одну страницу
        HtmlParser htmlParser = new HtmlParser(decodedUrl, siteEntity, poolService);
        htmlParser.compute();
        UtilitiesIndexing.isDoneIndexingSinglePage(); // повернем флаг на место как был, после \indexPage(String page)\


        return new IndexingResponse(true);
    }


    private SiteEntity createSiteEntity(Site site) {
        SiteEntity siteEntity = new SiteEntity();
        siteEntity.setStatus(StatusIndex.INDEXING);
        siteEntity.setStatusTime(LocalDateTime.now());
        siteEntity.setUrl(site.getUrl());
        siteEntity.setName(site.getName());
       // siteEntity.setLastError("");

        return siteEntity;
    }


    private String getShortMessageOfException(Exception e) {
        StringBuilder builder = new StringBuilder();
        String[] arrayMessage = e.getMessage().split(":");
        List<String> stringList = Arrays.stream(arrayMessage).toList();
        builder.append(stringList.get(stringList.size() - 2)).append(" - ");
        builder.append(stringList.get(stringList.size() - 1));
        return builder.toString();
    }


    private List<IndexingResponse> getIndexingResponseListFromFutureList(List<Future<IndexingResponse>> futureList) {
        List<IndexingResponse> indexingResponseList = new ArrayList<>();
        IndexingResponse indexingResponse;
        for (Future<IndexingResponse> indexResponseFuture : futureList) {
            try {
                indexingResponse = indexResponseFuture.get(); // если в HtmlParser выбросим RuntimeException(ex) в методе \saveLastErrorInSiteEntity\,
                // ExecutorService wrapper (обернет) RuntimeException in Future и метод \get\ выдаст исключение
                // и здесь поймаем как (ExecutionException e) и обработаем ниже
                indexingResponseList.add(indexingResponse);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                Logger.getLogger(IndexServiceImp.class.getName()).info("  after catch block   -- ExecutionException e - " + e.getCause());
                indexingResponse = new ResultResponseError(false, UtilityException.getShortMessageOfException(e));
                indexingResponseList.add(indexingResponse);
            }
        }
        return indexingResponseList;
    }


    private IndexingResponse getResultIndexingResponse(List<IndexingResponse> indexingResponseList) {
        int sizeList = indexingResponseList.size();
        boolean hasIndexingResponseHavingValueFalse = false;
        String error = "";

        if (!indexingResponseList.isEmpty()) {
           // int numberIndexHavingValueFalse = 0;

            // цикл ниже: если будет получать boolean=false - то значение изменится на положительное
            for (int i = 0; i < sizeList; i++) {
                if (!(indexingResponseList.get(i)).isResult()) {
                    hasIndexingResponseHavingValueFalse = true;
                    ResultResponseError responseError = (ResultResponseError) indexingResponseList.get(i);
                    error = responseError.getError();
                  //  numberIndexHavingValueFalse = i;
                }
            }

            double totalTime;
            if (hasIndexingResponseHavingValueFalse) {
                UtilitiesIndexing.isDoneStartIndexing();

                totalTime = computeTimeExecution();
                Logger.getLogger(IndexServiceImp.class.getName()).info("totalTime haveError = " + totalTime);

                throw new IncompleteIndexingException(errorIncompleteIndexing + ": " + error);
               // return indexingResponseList.get(numberIndexHavingValueFalse);
            } else {

                UtilitiesIndexing.isDoneStartIndexing();

                totalTime = computeTimeExecution();
                Logger.getLogger(IndexServiceImp.class.getName()).info("totalTime = " + totalTime);

                return indexingResponseList.get(sizeList - 1);
            }


        } else {
            double totalTime = computeTimeExecution();
            Logger.getLogger(IndexServiceImp.class.getName()).info("totalTime = " + totalTime);
            return new ResultResponseError(false, "что то пошло не так");
        }
    }


    private void cascadeDeletionAllEntities() {
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


    private double computeTimeExecution() {
        long end = System.currentTimeMillis();
        // long start = 0;
        return (double) (end - start) / 60_000;
    }

}



