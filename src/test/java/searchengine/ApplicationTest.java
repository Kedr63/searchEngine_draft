package searchengine;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.model.LemmaEntity;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;
import searchengine.repositories.IndexEntityLemmaToPageRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

/* Такой интеграционный тест запустит наше приложение в реальном контексте Spring и сгенерирует случайный порт для веб-сервиса */
/* Также необходима БД, но представьте что вы передали проект кому то другому и для запуска тестов ему придется устанавливать БД
*  у себя, что не очень удобно. Есть решение в виде контейнера Docker. Для наших тестов нужен будет контейнер с базой PostgreSql
* и добавим очередную зависимость от специальных PostgreSql-контейнеров */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private LemmaRepository lemmaRepository;

    @Autowired
    private IndexEntityLemmaToPageRepository indexEntityLemmaToPageRepository;

    TestRestTemplate template = new TestRestTemplate();  // для осуществления запросов по HTTP

    // создадим экземпляр контейнера который использует Docker контейнер PostgreSql v.14
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14");
    /* Такая реализация (см.выше) позволит запустить реальную БД postgreSQL в изолированной среде для интеграционного тестирования */

    @BeforeAll
    public static void beforeAll(){
        postgres.start();
    }

    @AfterAll
    public static void afterAll(){
        postgres.stop();
    }

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    /* создадим метод который будет заполнять БД*/
    @BeforeEach
    public void fillingDataBase(){
        SiteEntity siteEntity = new SiteEntity();
        siteRepository.save(siteEntity);
        PageEntity pageEntity = new PageEntity();
        pageEntity.setPath("/some/path/");
        pageEntity.setCode(200);
        pageEntity.setSite(siteEntity);
        pageRepository.save(pageEntity);
        LemmaEntity lemmaEntity = new LemmaEntity();
        lemmaEntity.setLemma("путешествие");
        lemmaEntity.setFrequency(5);
        lemmaRepository.save(lemmaEntity);
    }

    @AfterEach
    public void clearingDataBase(){
        siteRepository.deleteAll();
    }

    @Test
    public void testStatistics(){
        ResponseEntity<StatisticsResponse> response = template.getRestTemplate()
                .getForEntity("http://localhost:" + port + "/api/statistics", StatisticsResponse.class);
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());

    }








}
