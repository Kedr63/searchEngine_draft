package searchengine.services.searchService;

import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import searchengine.config.SnippetSearcherConfiguration;
import searchengine.config.UserAgentList;
import searchengine.dto.dtoToBD.LemmaDto;
import searchengine.dto.searching.PageIdInteger;
import searchengine.dto.searching.RelativeRelevanceFloater;
import searchengine.model.IndexEntity;
import searchengine.model.LemmaEntity;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;
import searchengine.repositories.IndexEntityLemmaToPageRepository;
import searchengine.services.PoolServiceImpl;
import searchengine.services.indexEntityService.IndexEntityService;
import searchengine.services.indexEntityService.IndexEntityServiceImpl;
import searchengine.services.lemmaService.LemmaServiceImpl;
import searchengine.services.pageService.PageServiceImp;
import searchengine.services.siteService.SiteServiceImp;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Data
class SearchServiceImpTest {

    IndexEntityLemmaToPageRepository indexEntityLemmaToPageRepository = Mockito.mock(IndexEntityLemmaToPageRepository.class);
    ModelMapper modelMapper = new ModelMapper();
    IndexEntityService indexEntityService = new IndexEntityServiceImpl(indexEntityLemmaToPageRepository, modelMapper);

    PoolServiceImpl poolService = new PoolServiceImpl(Mockito.mock(SiteServiceImp.class), Mockito.mock(PageServiceImp.class),
            Mockito.mock(LemmaServiceImpl.class), indexEntityService, Mockito.mock(UserAgentList.class), Mockito.mock(SnippetSearcherConfiguration.class));

//    PoolServiceImpl poolService = new PoolServiceImpl(Mockito.mock(SiteServiceImp.class), Mockito.mock(PageServiceImp.class),
  //          Mockito.mock(LemmaServiceImpl.class), Mockito.mock(IndexEntityServiceImpl.class), Mockito.mock(UserAgentList.class));

    RelevanceCalculator relevanceCalculator;

    IndexEntity indexEntityOne;
    int indexIdOne;

    IndexEntity indexEntityTwo;
    int indexIdTwo;

    IndexEntity indexEntityThree;
    int indexIdThree;

    IndexEntity indexEntityFour;
    int indexIdFour;

    LemmaEntity lemmaEntity;
    LemmaEntity lemmaEntity2;

    /* здесь будут создаваться (инициализироваться) переменные сверху */
    @BeforeEach // чтоб перед каждым тестом создавалась переменная
    public void setUp() {

        relevanceCalculator = new RelevanceCalculator(poolService);

        SiteEntity siteEntity = new SiteEntity();
        siteEntity.setId(1);
        siteEntity.setName("site.ru");
        siteEntity.setUrl("http://site.ru");

        PageEntity pageEntity = new PageEntity();
        pageEntity.setId(1);
        pageEntity.setSite(siteEntity);
        pageEntity.setPath("/path/to/page");

        PageEntity pageEntity2 = new PageEntity();
        pageEntity2.setId(2);
        pageEntity2.setSite(siteEntity);
        pageEntity2.setPath("/path/to/page2");

        lemmaEntity = new LemmaEntity();
        lemmaEntity.setId(1);
        lemmaEntity.setLemma("дом");
        lemmaEntity.setSite(siteEntity);
        lemmaEntity.setFrequency(5);

        lemmaEntity2 = new LemmaEntity();
        lemmaEntity2.setId(2);
        lemmaEntity2.setLemma("море");
        lemmaEntity2.setSite(siteEntity);
        lemmaEntity2.setFrequency(6);

// _________________________________
        indexIdOne = 1;
        indexEntityOne = new IndexEntity();
        indexEntityOne.setId(indexIdOne);
        indexEntityOne.setPage(pageEntity);
        indexEntityOne.setLemma(lemmaEntity);
        indexEntityOne.setRanting(5);

        indexIdTwo = 2;
        indexEntityTwo = new IndexEntity();
        indexEntityTwo.setId(indexIdTwo);
        indexEntityTwo.setPage(pageEntity2);
        indexEntityTwo.setLemma(lemmaEntity);
        indexEntityTwo.setRanting(7);

        indexIdThree = 3;
        indexEntityThree = new IndexEntity();
        indexEntityThree.setId(indexIdTwo);
        indexEntityThree.setPage(pageEntity);
        indexEntityThree.setLemma(lemmaEntity2);
        indexEntityThree.setRanting(8);

        indexIdFour = 4;
        indexEntityFour = new IndexEntity();
        indexEntityFour.setId(indexIdFour);
        indexEntityFour.setPage(pageEntity2);
        indexEntityFour.setLemma(lemmaEntity2);
        indexEntityFour.setRanting(9);
    }

    @Test
    void shouldFindIndexEntityInIndexEntityLemmaToPageRepositoryByPoolServiceTest() throws NoSuchElementException{

        // ДАНО: пусть метод репозитория \findById\ вернет \Optional.of(indexEntityOne)\
        when(indexEntityLemmaToPageRepository.findById(indexIdOne)).thenReturn(Optional.of(indexEntityOne));

        // у сервиса вызываем метод в котором работает метод \findById\ из ДАНО
        Optional<IndexEntity> indexEntityGiven = poolService.getIndexEntityService().getIndexEntityById(indexIdOne);

        // и если сервис нормально отрабатывает, то доказываем равенство ожидаемого indexId с полученным indexEntityGiven
        assertEquals(indexIdOne, Optional.of(indexEntityGiven.get().getId()));
        verify(indexEntityLemmaToPageRepository, times(1)).findById(indexIdOne); // проверяем что метод был
        // вызван 1 раз с аргументом, и убеждаемся что реально был вызван метод
    }

    @Test
    void VerifyCalculateRelativeRelevanceTest() {

        Set<PageIdInteger> pageIdForSearchingSet = new HashSet<>();
        PageIdInteger pageIdInteger_1 = new PageIdInteger(1);
        PageIdInteger pageIdInteger_2 = new PageIdInteger(2);

        pageIdForSearchingSet.add(pageIdInteger_1);
        pageIdForSearchingSet.add(pageIdInteger_2);

        Set<LemmaDto> lemmaDtoSetSortedByAscendingFrequency = new LinkedHashSet<>();
        LemmaDto lemmaDto = modelMapper.map(lemmaEntity, LemmaDto.class);
        LemmaDto lemmaDto2 = modelMapper.map(lemmaEntity2, LemmaDto.class);

        lemmaDtoSetSortedByAscendingFrequency.add(lemmaDto);
        lemmaDtoSetSortedByAscendingFrequency.add(lemmaDto2);

        RelativeRelevanceFloater relativeRelevanceFloaterOfPageWithId_1_Expected = new RelativeRelevanceFloater(0.8125F);
        RelativeRelevanceFloater relativeRelevanceFloaterOfPageWithId_2_Expected = new RelativeRelevanceFloater(1);

        /* по странице с id=1 абс. релевантность лемм будет 5+8 = 13
        *  по странице с id=2 абс. релевантность лемм будет 7+9 = 16 */
        /* отн. рел. pageIdInteger_1 Rrel = 13 / 16 = 0,8125
        *  отн. рел. pageIdInteger_2 Rrel = 16 / 16 = 1 */
        given(poolService.getIndexEntityService().getRankByLemmaIdAndPageId(lemmaDto.getId(), pageIdInteger_1.getPageId()))
                .willReturn(indexEntityOne.getRanting()); // rank = 5
        given(poolService.getIndexEntityService().getRankByLemmaIdAndPageId(lemmaDto2.getId(), pageIdInteger_1.getPageId()))
                .willReturn(indexEntityThree.getRanting());  // rank = 8
        given(poolService.getIndexEntityService().getRankByLemmaIdAndPageId(lemmaDto.getId(), pageIdInteger_2.getPageId()))
                .willReturn(indexEntityTwo.getRanting()); // rank = 7
        given(indexEntityService.getRankByLemmaIdAndPageId(lemmaDto2.getId(), pageIdInteger_2.getPageId()))
                .willReturn(indexEntityFour.getRanting()); // rank = 9


        Map<PageIdInteger, RelativeRelevanceFloater> pageToRelativeRelevanceMapSortedByDescendingRelevance
                = relevanceCalculator.getCalculatedPageRelevance(pageIdForSearchingSet, lemmaDtoSetSortedByAscendingFrequency);

        // проверим получение значений релевантности и порядок сортировки полученой Map
        int sizeMapWhenProperlySortedExpected = 2;

        int sizeMapActual = 0;
        for (Map.Entry<PageIdInteger, RelativeRelevanceFloater> entry : pageToRelativeRelevanceMapSortedByDescendingRelevance.entrySet()) {
            int pageId = entry.getKey().getPageId();
            if (pageId == pageIdInteger_2.getPageId()){
                assertEquals(relativeRelevanceFloaterOfPageWithId_2_Expected, pageToRelativeRelevanceMapSortedByDescendingRelevance.get(pageIdInteger_2));
                sizeMapActual++;
            }
            if (pageId == pageIdInteger_1.getPageId()){
                assertEquals(relativeRelevanceFloaterOfPageWithId_1_Expected, pageToRelativeRelevanceMapSortedByDescendingRelevance.get(pageIdInteger_1));
                sizeMapActual++;
            }
        }
        assertEquals(sizeMapWhenProperlySortedExpected, sizeMapActual);
    }
}