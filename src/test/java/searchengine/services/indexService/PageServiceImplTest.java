package searchengine.services.indexService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import searchengine.dto.dtoToBD.PageDto;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;
import searchengine.repositories.PageRepository;
import searchengine.services.pageService.PageService;
import searchengine.services.pageService.PageServiceImp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PageServiceImplTest {

    private final PageRepository pageRepository = Mockito.mock(PageRepository.class); // будет иммитировать настоящий репо
    private final ModelMapper modelMapper= new ModelMapper();
    private final PageService pageService = new PageServiceImp(pageRepository, modelMapper);


    @Test
    @DisplayName("Test getPageById")
    public void testGetPageDtoById() {
        int pageId = 1;
        PageEntity pageEntity = new PageEntity();
        pageEntity.setId(pageId);
        pageEntity.setCode(200);
        pageEntity.setPath("/some/path");
       // pageRepository.save(pageEntity);  так уже здесь не пишем (не save), т.к. \pageEntity\ уже окажется в \pageRepository\
        when(pageRepository.findById(pageId)).thenReturn(Optional.of(pageEntity)); // когда вызывается метод findById - тогда он должен вернуть \Optional.of(page)\
        PageDto pageDto = pageService.getPageDtoById(pageId);
        assertEquals(pageId, pageDto.getId());
        verify(pageRepository, times(1)).findById(pageId); // проверяем что метод был вызван 1 раз с аргументом \pageId\,
        // и убеждаемся что реально был вызван метод


    }

    @Test
    @DisplayName("Test getAllPage")
    public void testGetAllPage() {
        List<PageEntity> pageEntities = new ArrayList<>();
        PageEntity pageEntity = new PageEntity();
        pageEntity.setId(1);
        pageEntity.setCode(200);
        pageEntity.setPath("/some/path");
        pageEntities.add(pageEntity);
        when(pageRepository.findAll()).thenReturn(pageEntities);
        Collection<PageEntity> pageEntityList = pageService.getAllPageEntities();
        assertEquals(pageEntities.size(), pageEntityList.size());
        verify(pageRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test getIdPageEntity")
    public void testGetIdPageEntity() {
        int pageId = 1;
        PageEntity pageEntity = new PageEntity();
        pageEntity.setId(pageId);
        pageEntity.setCode(200);
        pageEntity.setPath("/some/path");
        int siteId = 2;
        SiteEntity siteEntity = new SiteEntity();
        siteEntity.setId(siteId);
        when(pageRepository.findIdByPageUrlAndIdSite(pageEntity.getPath(), siteId)).thenReturn(Optional.of(pageId));
        int pageDto = pageService.getIdPageEntity(pageEntity.getPath(), siteEntity.getId());
        assertEquals(pageId, pageDto);
        verify(pageRepository, times(1)).findIdByPageUrlAndIdSite(pageEntity.getPath(), siteId);
    }

    @Test
    @DisplayName("Test deletePageById")
    public void testDeletePageById() {
        int pageId = 1;
        pageService.deletePageById(pageId);
        verify(pageRepository, times(1)).deleteById(pageId); // здесь просто убеждаемся что у
        // репозитория вызывается данный метод
    }


    /* Принцип работы у всех тестов одинаковый
    * 1. Задаем сначала модель
    * 2. Настраиваем поведение Mock-объекта
    * 3. Выполняем тестируемый метод
    * 4. И проверяем ответ
    *
    * Unit - это базовый уроневь тестирования. Для проверки взаимодействия должны использовать ИНТЕГРАЦИОННОЕ ТЕСТИРОВАНИЕ,
    * для этого создадим еще один класс */
}
