package searchengine.services.indexService;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.config.UserAgentList;
import searchengine.dto.indexing.IndexResponse;
import searchengine.dto.indexing.IndexResponseError;
import searchengine.dto.indexing.UtilitiesIndexing;
import searchengine.exceptions.IllegalMethodException;
import searchengine.exceptions.UtilityException;
import searchengine.model.SiteEntity;
import searchengine.model.StatusIndex;
import searchengine.services.PageService;
import searchengine.services.SiteService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service //при этой аннотации получим бины (объекты) прописанные в полях (но только если их добавим в конструктор)
@Getter
@Setter
public class IndexServiceImp implements IndexService {

    private final SiteService siteService;

    private final PageService pageService;

    private final SitesList sitesList;

    private final UserAgentList userAgentList;

    @Value("${errorIndexingNotRunning}")
    private String errorIndexingNotRunning;

    @Value("${errorIndexingAlreadyRunning}")
    private String errorIndexingAlreadyRunning;

    protected static final Object lock = new Object();
    protected static final Object lock2 = new Object();

    protected static long countOfHtmlParser = 1;
    //  protected static long counter = 0;
    protected static int coreAmount = Runtime.getRuntime().availableProcessors();

    protected static String tmp = "Return";


    public IndexServiceImp(SiteService siteService, PageService pageService, SitesList sitesList, UserAgentList userAgentList) {
        this.siteService = siteService;
        this.pageService = pageService;
        this.sitesList = sitesList;
        this.userAgentList = userAgentList;
    }


    @Override
    public ResponseEntity<IndexResponse> startIndexing() {
        if (UtilitiesIndexing.executionOfMethodStartIndexing) {
            IndexResponseError indexResponseError = new IndexResponseError(false, errorIndexingAlreadyRunning);
            return new ResponseEntity<>(indexResponseError, HttpStatus.FORBIDDEN);
        }

        //   UtilitiesIndexing.StopStartIndexing = true; // при запущенной индексации это значение - true
        UtilitiesIndexing.executionOfMethodStartIndexing = true; // при запущенной индексации это значение - true

        long start = System.currentTimeMillis();

        siteService.deleteAll();

        int sizeSitesList = sitesList.getSites().size();
        ExecutorService executorService = Executors.newFixedThreadPool(sizeSitesList);
        List<Future<ResponseEntity<IndexResponse>>> futureList = new ArrayList<>();

        //   futureList = new ArrayList<>();
        //  ResponseEntity<IndexResponse> indexResponseEntity= null;

        for (int i = 0; i < sizeSitesList; i++) {
            Site site = sitesList.getSites().get(i);

            SiteEntity siteEntity = createSiteEntity(site);
            siteService.saveSiteEntity(siteEntity);

            ExecutorServiceForParsingSiteEntity executorServiceForParsingSiteEntity = new ExecutorServiceForParsingSiteEntity(siteEntity, this);
            Future<ResponseEntity<IndexResponse>> futureResponseEntity = executorService.submit(executorServiceForParsingSiteEntity);

            futureList.add(futureResponseEntity);
        }

        List<ResponseEntity<IndexResponse>> responseEntityList = getResponseEntityListOfIndexingFromFutureList(futureList);
        //  UtilitiesIndexing.StopStartIndexing = false;
        return getResultIndexingResponseEntity(responseEntityList);

    }


    @Override
    public ResponseEntity<IndexResponse> stopIndexing() {

        if (!UtilitiesIndexing.executionOfMethodStartIndexing) {
            throw new IllegalMethodException(errorIndexingNotRunning);
        }

        UtilitiesIndexing.stopStartIndexing = true;

       return UtilitiesIndexing.waitForCompleteStartIndexingAndTerminateStopIndexing();
    }


    private SiteEntity createSiteEntity(Site site) {
        SiteEntity siteEntity = new SiteEntity();
        siteEntity.setStatus(StatusIndex.INDEXING);
        siteEntity.setStatusTime(LocalDateTime.now());
        siteEntity.setUrl(site.getUrl());
        siteEntity.setName(site.getName());

        return siteEntity;
    }


    private String getShortMessageOfException(Exception e) {
        StringBuilder builder = new StringBuilder();
        String[] arrayMessage = e.getMessage().split(":");
        List<String> stringList = Arrays.stream(arrayMessage).collect(Collectors.toList());
        builder.append(stringList.get(stringList.size() - 2)).append(" - ");
        builder.append(stringList.get(stringList.size() - 1));
        return builder.toString();
    }


    private List<ResponseEntity<IndexResponse>> getResponseEntityListOfIndexingFromFutureList(List<Future<ResponseEntity<IndexResponse>>> futureList) {
        List<ResponseEntity<IndexResponse>> responseEntityList = new ArrayList<>();
        ResponseEntity<IndexResponse> indexResponseEntity;
        for (Future<ResponseEntity<IndexResponse>> indexResponseFuture : futureList) {
            try {
                indexResponseEntity = indexResponseFuture.get();
                responseEntityList.add(indexResponseEntity);
                Logger.getLogger(IndexServiceImp.class.getName()).info("    indexResponseFuture.get() = ");
            } catch (InterruptedException e) {
                //  Logger.getLogger(IndexServiceImp.class.getName()).info("    indexResponseFuture.get()   - in catch block -1");
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                Logger.getLogger(IndexServiceImp.class.getName()).info("  after catch block   -- ExecutionException e");
                indexResponseEntity = new ResponseEntity<>(new IndexResponseError(false, UtilityException.getShortMessageOfException(e)), HttpStatus.BAD_REQUEST);
                responseEntityList.add(indexResponseEntity);
            }
        }
        return responseEntityList;
    }


    private ResponseEntity<IndexResponse> getResultIndexingResponseEntity(List<ResponseEntity<IndexResponse>> responseEntityList) {
        int sizeList = responseEntityList.size();
        Logger.getLogger(IndexServiceImp.class.getName()).info("size list = " + responseEntityList.size());

        if (!responseEntityList.isEmpty()) {
            int numberIndexOfResultFalse = -1;

            // цикл ниже если будет получать boolean=false - то значение изменится на положительное
            for (int i = 0; i < sizeList; i++) {
                if (!Objects.requireNonNull(responseEntityList.get(i).getBody()).isResult()) {
                    numberIndexOfResultFalse = i;
                }
            }
            if (numberIndexOfResultFalse != -1) {
                Logger.getLogger(IndexServiceImp.class.getName()).info("перед ---- responseEntityList.get(numberIndexOfResultFalse)");
                //  return responseEntityList.get(numberIndexOfResultFalse);

                double totalTime = computeTimeExecution();
                Logger.getLogger(IndexServiceImp.class.getName()).info("totalTime = " + totalTime);

                UtilitiesIndexing.isDoneStartIndexing();
                return responseEntityList.get(numberIndexOfResultFalse);
            } else {
                Logger.getLogger(IndexServiceImp.class.getName()).info("перед ====  return responseEntityList.get(sizeList -1)");

                double totalTime = computeTimeExecution();
                Logger.getLogger(IndexServiceImp.class.getName()).info("totalTime = " + totalTime);

                UtilitiesIndexing.isDoneStartIndexing();
                return responseEntityList.get(sizeList - 1);
            }

        } else {
            double totalTime = computeTimeExecution();
            Logger.getLogger(IndexServiceImp.class.getName()).info("totalTime = " + totalTime);
            return new ResponseEntity<>(new IndexResponseError(false, "что то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private double computeTimeExecution() {
        long end = System.currentTimeMillis();
        long start = 0;
        return (double) (end - start) / 60_000;
    }
}



