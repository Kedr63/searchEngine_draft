package searchengine.services.indexService;

import org.springframework.stereotype.Service;
import searchengine.config.ErrorMessageConfig;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.dtoToBD.PageDtoSingle;
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

@Service
public class IndexServiceImp implements IndexService {

    private final PoolService poolService;
    private final SitesList sitesList;
    private final ErrorMessageConfig errorMessageConfig;

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
        //TODO убрать потом
        TimerExecution.startTime = System.currentTimeMillis();

        UtilitiesIndexing.indexingInProgress = true; // при запущенной индексации это значение - true
        cascadeDeletionAllEntities();
        int sizeSitesList = sitesList.getSites().size();
        ExecutorService executorService = Executors.newFixedThreadPool(sizeSitesList);
        List<Future<IndexingResponse>> futureList = new ArrayList<>();

        for (int i = 0; i < sizeSitesList; i++) {
            Site site = sitesList.getSites().get(i);
            SiteDto siteDto = createSiteDto(site);
            siteDto = poolService.getSiteService().saveSiteDto(siteDto);

            TaskForkJoinPoolForParsingSite taskForkJoinPoolForParsingSite = new TaskForkJoinPoolForParsingSite(siteDto, poolService, errorMessageConfig);
            Future<IndexingResponse> futureResponseEntity = executorService.submit(taskForkJoinPoolForParsingSite);

            futureList.add(futureResponseEntity);
        }

        List<IndexingResponse> IndexingResponseList = UtilitiesIndexing.getIndexingResponseListFromFutureList(futureList);
        executorService.shutdown();
        UtilitiesIndexing.finishStartIndexing();
        return UtilitiesIndexing.getTotalResultIndexingResponse(IndexingResponseList);
    }


    @Override
    public IndexingResponse stopIndexing() {

        if (!UtilitiesIndexing.indexingInProgress) {
            throw new IllegalMethodException(errorMessageConfig.getIndexingNotProgressError());
        }
        UtilitiesIndexing.stopIndexing = true;

        return UtilitiesIndexing.waitForCompleteStartIndexingAndTerminateStopIndexing();
    }

    @Override
    public IndexingResponse indexSinglePage(PageDtoSingle pageDtoSingle) {
        String urlPage = pageDtoSingle.getUrl();
        PageService pageService = poolService.getPageService();
        SiteService siteService = poolService.getSiteService();
        String regex = "(https://[^/]+)([^,\\s]+)";
        String domainPartOfAddressUrl = urlPage.replaceAll(regex, "$1");

        if (sitesList.getSites()
                .stream()
                .noneMatch(s -> s.getUrl().equals(domainPartOfAddressUrl))) {
            throw new NoSuchSiteException(errorMessageConfig.getErrorMatchingSiteUrlOfSiteList());
        }

        SiteDto siteDto;
        if (siteService.getSiteDtoByUrl(domainPartOfAddressUrl).isPresent()) {
            siteDto = siteService.getSiteDtoByUrl(domainPartOfAddressUrl).get();
        } else throw new NoSuchSiteException(errorMessageConfig.getErrorIncompleteIndexing());

        String pageLocalUrl = urlPage.replaceAll(regex, "$2");

        if (pageService.isPresentPageEntityWithThatPath(pageLocalUrl, siteDto.getId())) {
            int idPageEntity = pageService.getIdPageEntity(pageLocalUrl, siteDto.getId());
            cascadeDeletionPageEntity(idPageEntity);
        }
        UtilitiesIndexing.startSinglePageIndexing(); // метод поменяет флаг на TRUE для объектов HtmlRecursiveParser
        // чтоб пропарсить только одну страницу
        HtmlRecursiveParser htmlRecursiveParser = new HtmlRecursiveParser(urlPage, siteDto, poolService);
        htmlRecursiveParser.compute();
        UtilitiesIndexing.finishIndexingSinglePage(); // повернем флаг на место как был, после \indexPage(String page)\


        return new IndexingResponse(true);
    }

    private SiteDto createSiteDto(Site site) {
        SiteDto siteDto = new SiteDto();
        siteDto.setStatusIndex(StatusIndex.INDEXING);
        siteDto.setStatusTime(LocalDateTime.now());
        siteDto.setUrl(site.getUrl());
        siteDto.setName(site.getName());
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

    /** Каскадно удаляет все сущности из БД начиная с дочерних до родительских*/
    private void cascadeDeletionAllEntities() {
        poolService.getIndexEntityService().deleteAllIndexEntity();
        poolService.getLemmaService().deleteAllLemmaEntities();
        poolService.getPageService().deleteAllPageEntity();
        poolService.getSiteService().deleteAllSiteEntity();
    }

    /** Каскадно удаляет сущности из БД которые связаны с определенной страницей через id,
     * начиная с дочерних до родительской страницы (включительно):
     * <p>-> находит список {@code id} лемм удаляемой страницы в таблице {@code 'index_search'}.</p>
     * <p>-> удаляет все {@code IndexEntity} удаляемой страницы.</p>
     * <p>-> обновляет в таблице {@code 'lemma'} поле {@code frequency} (количество страниц на сайте, на которых лемма встречается).</p>
     * <p>-> удаляет строку страницы в таблице {@code 'page'} БД.</p>
     * */
    private void cascadeDeletionPageEntity(int idPageEntity) {
        List<Integer> idLemmaList = poolService.getIndexEntityService().getIdLemmaByPageId(idPageEntity);
        poolService.getIndexEntityService().deleteIndexEntityWherePageId(idPageEntity);
        poolService.getLemmaService().updateLemmaFrequency(idLemmaList);
        poolService.getPageService().deletePageById(idPageEntity);
    }


}



