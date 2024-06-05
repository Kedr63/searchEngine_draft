package searchengine;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

    LuceneMorphology luceneMorph = new RussianLuceneMorphology();

    static String text = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "\n" +
            "<head>\n" +
            "  <meta charset=\"UTF-8\">\n" +
            "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
            "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "  <title>Lagoona</title>\n" +
            "  <script sdsdsd sdsdsd Скрипт></script>\n" +
            "</head>\n" +
            "\n" +
            "<body>\n" +
            "  <header class=\"header\">\n" +
            "    <div class=\"container\">\n" +
            "\n" +
            "      <div class=\"header-top\">\n" +
            "        <a href=\"#\" class=\"header-logo\">\n" +
            "          <img src=\"img/logo.svg\" alt=\"logo\">\n" +
            "        </a>\n" +
            "        <address class=\"header-contact\">\n" +
            "          <a href=\"tel:+74953225448\" class=\"header-contact-item\">+7&nbsp;495 322-54-48</a>\n" +
            "        </address>\n" +
            "        <a href=\"#\" class=\"header-account\">\n" +
            "          <span class=\"header-account-icon\">\n" +
            "            <svg aria-label=\"presentation\" width=\"21\" height=\"12\" viewBox=\"0 0 21 12\" fill=\"none\"\n" +
            "              xmlns=\"http://www.w3.org/2000/svg\">\n" +
            "              <path d=\"M10.59 1.41L14.17 5H0V7H14.17L10.58 10.59L12 12L18 6L12 0L10.59 1.41ZM19 0V12H21V0H19Z\"\n" +
            "                fill=\"#CC9933\" />\n" +
            "            </svg>\n" +
            "          </span>\n" +
            "          <span class=\"header-account-text\">\n" +
            "            Личный кабинет\n" +
            "          </span>\n" +
            "        </a>\n" +
            "      </div>\n" +
            "\n" +
            "      <div class=\"header-navbar\">\n" +
            "        <nav class=\"header-nav\">\n" +
            "          <ul class=\"header-list\">\n" +
            "            <li class=\"header-list__item\">\n" +
            "              <a href=\"#about-us\" class=\"header-link\">\n" +
            "                О&nbsp;нас\n" +
            "              </a>\n" +
            "            </li>\n" +
            "            <li class=\"header-list__item\">\n" +
            "              <a href=\"#services\" class=\"header-link\">\n" +
            "                Услуги\n" +
            "              </a>\n" +
            "            </li>\n" +
            "            <li class=\"header-list__item\">\n" +
            "              <a href=\"#advantages\" class=\"header-link\">\n" +
            "                Преимущества\n" +
            "              </a>\n" +
            "            </li>\n" +
            "            <li class=\"header-list__item\">\n" +
            "              <a href=\"#location\" class=\"header-link\">\n" +
            "                Размещение\n" +
            "              </a>\n" +
            "            </li>\n" +
            "            <!-- Здесь будет ссылка на страницу (Блог) -->\n" +
            "            <li class=\"header-list__item\">\n" +
            "              <a href=\"#\" class=\"header-link\">\n" +
            "                Блог\n" +
            "              </a>\n" +
            "            </li>\n" +
            "            <li class=\"header-list__item\">\n" +
            "              <a href=\"#contacts\" class=\"header-link\">\n" +
            "                Контакты\n" +
            "              </a>\n" +
            "            </li>\n" +
            "          </ul>\n" +
            "        </nav>\n" +
            "        <button onclick=\"window.location.href='#want-tour'\" class=\"btn header-btn\">\n" +
            "          Хочу тур\n" +
            "        </button>\n" +
            "        <button class=\"btn header-btn\">\n" +
            "          Обратный звонок\n" +
            "        </button>\n" +
            "      </div>\n" +
            "\n" +
            "    </div>\n" +
            "  </header>\n" +
            "\n" +
            "  <main class=\"main\">\n" +
            "    <section class=\"promos\">\n" +
            "      <div class=\"container\">\n" +
            "        <h2 class=\"section-title promos-title\">\n" +
            "          Спецпредложения\n" +
            "        </h2>\n" +
            "        <!-- Так как карточки разных размеров можно будет сделать столбцами: первый столбец - две небольшие карточки,\n" +
            "          второй столбец -большая карточка\n" +
            "        фоновые картинки будут background-image -->\n" +
            "        <div class=\"promos-list\">\n" +
            "          <article class=\"promos-item\">\n" +
            "            <h3 class=\"promos-item-title\">\n" +
            "              Мальдивские острова\n" +
            "            </h3>\n" +
            "            <p class=\"promos-item-price\">\n" +
            "              от&nbsp;55&nbsp;000&nbsp;₽\n" +
            "            </p>\n" +
            "            <a href=\"#\" class=\"promos-item-link\">\n" +
            "              <span class=\"promos-link-text\">\n" +
            "                Подробнее\n" +
            "              </span>\n" +
            "              <span class=\"promos-link-icon\">\n" +
            "                <svg aria-label=\"presentation\" width=\"11\" height=\"18\" viewBox=\"0 0 11 18\" fill=\"none\"\n" +
            "                  xmlns=\"http://www.w3.org/2000/svg\">\n" +
            "                  <path d=\"M1.49998 1.00002L9.27815 8.7782L1.49998 16.5564\" stroke=\"#F0BF5F\" stroke-width=\"2\" />\n" +
            "                </svg>\n" +
            "              </span>\n" +
            "            </a>\n" +
            "          </article>\n" +
            "          <article class=\"promos-item\">\n" +
            "            <h3 class=\"promos-item-title\">\n" +
            "              Горящий тур на&nbsp;остров Крит\n" +
            "            </h3>\n" +
            "            <p class=\"promos-item-price\">\n" +
            "              от&nbsp;30&nbsp;000&nbsp;₽\n" +
            "            </p>\n" +
            "            <a href=\"#\" class=\"promos-item-link\">\n" +
            "              <span class=\"promos-link-text\">\n" +
            "                Подробнее\n" +
            "              </span>\n" +
            "              <span class=\"promos-link-icon\">\n" +
            "                <svg aria-label=\"presentation\" width=\"11\" height=\"18\" viewBox=\"0 0 11 18\" fill=\"none\"\n" +
            "                  xmlns=\"http://www.w3.org/2000/svg\">\n" +
            "                  <path d=\"M1.49998 1.00002L9.27815 8.7782L1.49998 16.5564\" stroke=\"#F0BF5F\" stroke-width=\"2\" />\n" +
            "                </svg>\n" +
            "              </span>\n" +
            "            </a>\n" +
            "          </article>\n" +
            "          <article class=\"promos-item item-big\">\n" +
            "            <h3 class=\"promos-item-title\">\n" +
            "              Номера категории люкс\n" +
            "            </h3>\n" +
            "            <p class=\"promos-item-price\">\n" +
            "              от&nbsp;5&nbsp;000&nbsp;₽\n" +
            "            </p>\n" +
            "            <a href=\"#\" class=\"promos-item-link\">\n" +
            "              <span class=\"promos-link-text\">\n" +
            "                Подробнее\n" +
            "              </span>\n" +
            "              <span class=\"promos-link-icon\">\n" +
            "                <svg aria-label=\"presentation\" width=\"11\" height=\"18\" viewBox=\"0 0 11 18\" fill=\"none\"\n" +
            "                  xmlns=\"http://www.w3.org/2000/svg\">\n" +
            "                  <path d=\"M1.49998 1.00002L9.27815 8.7782L1.49998 16.5564\" stroke=\"#F0BF5F\" stroke-width=\"2\" />\n" +
            "                </svg>\n" +
            "              </span>\n" +
            "            </a>\n" +
            "          </article>\n" +
            "        </div>\n" +
            "      </div>\n" +
            "    </section>\n" +
            "\n" +
            "    <section id=\"about-us\" class=\"about-us\">\n" +
            "      <div class=\"container about-us-container\">\n" +
            "        <h2 class=\"section-title about-us-title\">\n" +
            "          О&nbsp;нас\n" +
            "        </h2>\n" +
            "        <p class=\"about-us-descr\">\n" +
            "          Идейные соображения высшего порядка, а&nbsp;также сложившаяся структура организации влечёт за&nbsp;собой\n" +
            "          процесс внедрения и&nbsp;модернизации системы обучения кадров, соответствует насущным потребностям. Идейные\n" +
            "          соображения высшего порядка, а&nbsp;также дальнейшее развитие различных форм деятельности представляет собой\n" +
            "          интересный эксперимент проверки системы обучения кадров, соответствует насущным потребностям. Равным образом\n" +
            "          сложившаяся структура организации требуют определения и&nbsp;уточнения существенных финансовых\n" +
            "          и&nbsp;административных условий.\n" +
            "        </p>\n" +
            "      </div>\n" +
            "    </section>\n" +
            "    <section id=\"services\" class=\"services\">\n" +
            "      <div class=\"container\">\n" +
            "        <h2 class=\"section-title services-title\">\n" +
            "          Услуги\n" +
            "        </h2>\n" +
            "        <ul class=\"services-list\">\n" +
            "          <li class=\"services-item\">\n" +
            "            <div class=\"services-card-content\">\n" +
            "              <a href=\"#\" class=\"services-card-content__link\">\n" +
            "                <h3 class=\"services-card-content__title\">\n" +
            "                  <span class=\"services-card-content__title-text\">\n" +
            "                    Эксклюзивное обслуживание\n" +
            "                  </span>\n" +
            "                  <span class=\"services-card-content__title-icon\">\n" +
            "                    <svg aria-label=\"presentation\" width=\"10\" height=\"18\" viewBox=\"0 0 10 18\" fill=\"none\"\n" +
            "                      xmlns=\"http://www.w3.org/2000/svg\">\n" +
            "                      <path d=\"M0.778175 1.00002L8.55635 8.7782L0.778175 16.5564\" stroke=\"#CC9933\" stroke-width=\"2\" />\n" +
            "                    </svg>\n" +
            "                  </span>\n" +
            "                </h3>\n" +
            "              </a>\n" +
            "              <p class=\"services-card-content__descr\">\n" +
            "                Равным образом постоянный количественный рост и&nbsp;сфера нашей активности\n" +
            "              </p>\n" +
            "            </div>\n" +
            "          </li>\n" +
            "          <li class=\"services-item\">\n" +
            "            <div class=\"services-card-content\">\n" +
            "              <a href=\"#\" class=\"services-card-content__link\">\n" +
            "                <h3 class=\"services-card-content__title\">\n" +
            "                  <span class=\"services-card-content__title-text\">\n" +
            "                    Аренда банкетных залов\n" +
            "                  </span>\n" +
            "                  <span class=\"services-card-content__title-icon\">\n" +
            "                    <svg aria-label=\"presentation\" width=\"10\" height=\"18\" viewBox=\"0 0 10 18\" fill=\"none\"\n" +
            "                      xmlns=\"http://www.w3.org/2000/svg\">\n" +
            "                      <path d=\"M0.778175 1.00002L8.55635 8.7782L0.778175 16.5564\" stroke=\"#CC9933\" stroke-width=\"2\" />\n" +
            "                    </svg>\n" +
            "                  </span>\n" +
            "                </h3>\n" +
            "              </a>\n" +
            "              <p class=\"services-card-content__descr\">\n" +
            "                Значимость этих проблем настолько очевидна, что дальнейшее развитие различных форм\n" +
            "              </p>\n" +
            "            </div>\n" +
            "          </li>\n" +
            "          <li class=\"services-item\">\n" +
            "            <div class=\"services-card-content\">\n" +
            "              <a href=\"#\" class=\"services-card-content__link\">\n" +
            "                <h3 class=\"services-card-content__title\">\n" +
            "                  <span class=\"services-card-content__title-text\">\n" +
            "                    Сауны, бассейны, бани, фитнес-залы\n" +
            "                  </span>\n" +
            "                  <span class=\"services-card-content__title-icon\">\n" +
            "                    <svg aria-label=\"presentation\" width=\"10\" height=\"18\" viewBox=\"0 0 10 18\" fill=\"none\"\n" +
            "                      xmlns=\"http://www.w3.org/2000/svg\">\n" +
            "                      <path d=\"M0.778175 1.00002L8.55635 8.7782L0.778175 16.5564\" stroke=\"#CC9933\" stroke-width=\"2\" />\n" +
            "                    </svg>\n" +
            "                  </span>\n" +
            "                </h3>\n" +
            "              </a>\n" +
            "              <p class=\"services-card-content__descr\">\n" +
            "                Не&nbsp;следует, однако забывать, что начало повседневной работы по&nbsp;формированию позиции\n" +
            "              </p>\n" +
            "            </div>\n" +
            "          </li>\n" +
            "          <li class=\"services-item\">\n" +
            "            <div class=\"services-card-content\">\n" +
            "              <a href=\"#\" class=\"services-card-content__link\">\n" +
            "                <h3 class=\"services-card-content__title\">\n" +
            "                  <span class=\"services-card-content__title-text\">\n" +
            "                    Охраняемые автомобильные стоянки\n" +
            "                  </span>\n" +
            "                  <span class=\"services-card-content__title-icon\">\n" +
            "                    <svg aria-label=\"presentation\" width=\"10\" height=\"18\" viewBox=\"0 0 10 18\" fill=\"none\"\n" +
            "                      xmlns=\"http://www.w3.org/2000/svg\">\n" +
            "                      <path d=\"M0.778175 1.00002L8.55635 8.7782L0.778175 16.5564\" stroke=\"#CC9933\" stroke-width=\"2\" />\n" +
            "                    </svg>\n" +
            "                  </span>\n" +
            "                </h3>\n" +
            "              </a>\n" +
            "              <p class=\"services-card-content__descr\">\n" +
            "                Не&nbsp;следует, однако забывать, что начало повседневной работы по&nbsp;формированию позиции\n" +
            "              </p>\n" +
            "            </div>\n" +
            "          </li>\n" +
            "        </ul>\n" +
            "      </div>\n" +
            "    </section>\n" +
            "    <section id=\"advantages\" class=\"advantages\">\n" +
            "      <div class=\"container\">\n" +
            "        <h2 class=\"section-title advantages-title\">\n" +
            "          Преимущества\n" +
            "        </h2>\n" +
            "        <!-- Иллюстрации будут вставлены с помощью свойства background-image в классах advantages-image  -->\n" +
            "        <ul class=\"advantages-list\">\n" +
            "          <li class=\"advantages-item advantages-image-1\">\n" +
            "            <p class=\"advantages-descr\">\n" +
            "              Идейные соображения высшего порядка,\n" +
            "              а&nbsp;также постоянный количественный рост\n" +
            "            </p>\n" +
            "          </li>\n" +
            "          <li class=\"advantages-item advantages-image-2\">\n" +
            "            <p class=\"advantages-descr\">\n" +
            "              Значимость этих проблем настолько очевидна, что вопрос остаётся открытым\n" +
            "            </p>\n" +
            "          </li>\n" +
            "          <li class=\"advantages-item advantages-image-3\">\n" +
            "            <p class=\"advantages-descr\">\n" +
            "              Таким образом реализация плановых заданий играет важную роль для понимания\n" +
            "            </p>\n" +
            "          </li>\n" +
            "          <li class=\"advantages-item advantages-image-4\">\n" +
            "            <p class=\"advantages-descr\">\n" +
            "              Повседневная практика показывает, что сложившаяся структура организации\n" +
            "            </p>\n" +
            "          </li>\n" +
            "          <li class=\"advantages-item advantages-image-5\">\n" +
            "            <p class=\"advantages-descr\">\n" +
            "              Равным образом рамки и&nbsp;место обучения кадров способствует подготовки сотрудника\n" +
            "            </p>\n" +
            "          </li>\n" +
            "          <li class=\"advantages-item advantages-image-6\">\n" +
            "            <p class=\"advantages-descr\">\n" +
            "              Консультация с&nbsp;активом влечёт за&nbsp;собой процесс внедрения услуг нашего сервиса\n" +
            "            </p>\n" +
            "          </li>\n" +
            "          <li class=\"advantages-item advantages-image-7\">\n" +
            "            <p class=\"advantages-descr\">\n" +
            "              Повседневная практика показывает, что дальнейшее развитие различных\n" +
            "            </p>\n" +
            "          </li>\n" +
            "          <li class=\"advantages-item advantages-image-8\">\n" +
            "            <p class=\"advantages-descr\">\n" +
            "              Значимость этого настолько очевидна, что консультация наших экспертов помогает\n" +
            "            </p>\n" +
            "          </li>\n" +
            "        </ul>\n" +
            "      </div>\n" +
            "    </section>\n" +
            "    <section id=\"location\" class=\"location\">\n" +
            "      <div class=\"container\">\n" +
            "        <h2 class=\"section-title location-title\">\n" +
            "          Размещение\n" +
            "        </h2>\n" +
            "        <ul class=\"location-list\">\n" +
            "          <li class=\"location-item-card\">\n" +
            "            <img src=\"/img/location-otel_1.jpg\" alt=\"Вид отеля\" class=\"location-item-image\">\n" +
            "            <div class=\"location-item-price\">\n" +
            "              <span class=\"location-item-price__from\">\n" +
            "                от\n" +
            "              </span>\n" +
            "              <span class=\"location-item-price__numbers\">\n" +
            "                4&nbsp;698&nbsp;₽\n" +
            "              </span>\n" +
            "              <span class=\"location-item-price__period\">\n" +
            "                / ночь\n" +
            "              </span>\n" +
            "            </div>\n" +
            "            <div class=\"location-item-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"Звездность отеля 4 из 5\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-empty.png\" alt=\"\" class=\"image-star\">\n" +
            "            </div>\n" +
            "            <span class=\"location-item-otel\">\n" +
            "              Lagoona espa&ntilde;ola\n" +
            "            </span>\n" +
            "            <span class=\"location-item-position\">\n" +
            "              Барселона, Испания\n" +
            "            </span>\n" +
            "            <button class=\"btn location-btn\">\n" +
            "              Номера\n" +
            "            </button>\n" +
            "          </li>\n" +
            "          <li class=\"location-item-card\">\n" +
            "            <img src=\"/img/location-otel_2.jpg\" alt=\"Вид отеля\" class=\"location-item-image\">\n" +
            "            <div class=\"location-item-price\">\n" +
            "              <span class=\"location-item-price__from\">\n" +
            "                от\n" +
            "              </span>\n" +
            "              <span class=\"location-item-price__numbers\">\n" +
            "                5&nbsp;148&nbsp;₽\n" +
            "              </span>\n" +
            "              <span class=\"location-item-price__period\">\n" +
            "                / ночь\n" +
            "              </span>\n" +
            "            </div>\n" +
            "            <div class=\"location-item-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"Звездность отеля 4 из 5\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-empty.png\" alt=\"\" class=\"image-star\">\n" +
            "            </div>\n" +
            "            <span class=\"location-item-otel\">\n" +
            "              Lagoona son t&iacute;picas\n" +
            "            </span>\n" +
            "            <span class=\"location-item-position\">\n" +
            "              Барселона, Испания\n" +
            "            </span>\n" +
            "            <button class=\"btn location-btn\">\n" +
            "              Номера\n" +
            "            </button>\n" +
            "          </li>\n" +
            "          <li class=\"location-item-card\">\n" +
            "            <img src=\"/img/location-otel_3.jpg\" alt=\"Вид отеля\" class=\"location-item-image\">\n" +
            "            <div class=\"location-item-price\">\n" +
            "              <span class=\"location-item-price__from\">\n" +
            "                от\n" +
            "              </span>\n" +
            "              <span class=\"location-item-price__numbers\">\n" +
            "                6&nbsp;254&nbsp;₽\n" +
            "              </span>\n" +
            "              <span class=\"location-item-price__period\">\n" +
            "                / ночь\n" +
            "              </span>\n" +
            "            </div>\n" +
            "            <div class=\"location-item-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"Звездность отеля 5 из 5\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "            </div>\n" +
            "            <span class=\"location-item-otel\">\n" +
            "              Lagoona de&nbsp;la&nbsp;guerra\n" +
            "            </span>\n" +
            "            <span class=\"location-item-position\">\n" +
            "              Барселона, Испания\n" +
            "            </span>\n" +
            "            <button class=\"btn location-btn\">\n" +
            "              Номера\n" +
            "            </button>\n" +
            "          </li>\n" +
            "          <li class=\"location-item-card\">\n" +
            "            <img src=\"/img/location-otel_4.jpg\" alt=\"Вид отеля\" class=\"location-item-image\">\n" +
            "            <div class=\"location-item-price\">\n" +
            "              <span class=\"location-item-price__from\">\n" +
            "                от\n" +
            "              </span>\n" +
            "              <span class=\"location-item-price__numbers\">\n" +
            "                6&nbsp;320&nbsp;₽\n" +
            "              </span>\n" +
            "              <span class=\"location-item-price__period\">\n" +
            "                / ночь\n" +
            "              </span>\n" +
            "            </div>\n" +
            "            <div class=\"location-item-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"Звездность отеля 5 из 5\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "            </div>\n" +
            "            <span class=\"location-item-otel\">\n" +
            "              Lagoona mentira piadosa\n" +
            "            </span>\n" +
            "            <span class=\"location-item-position\">\n" +
            "              Барселона, Испания\n" +
            "            </span>\n" +
            "            <button class=\"btn location-btn\">\n" +
            "              Номера\n" +
            "            </button>\n" +
            "          </li>\n" +
            "          <li class=\"location-item-card\">\n" +
            "            <img src=\"/img/location-otel_5.jpg\" alt=\"Вид отеля\" class=\"location-item-image\">\n" +
            "            <div class=\"location-item-price\">\n" +
            "              <span class=\"location-item-price__from\">\n" +
            "                от\n" +
            "              </span>\n" +
            "              <span class=\"location-item-price__numbers\">\n" +
            "                6&nbsp;457&nbsp;₽\n" +
            "              </span>\n" +
            "              <span class=\"location-item-price__period\">\n" +
            "                / ночь\n" +
            "              </span>\n" +
            "            </div>\n" +
            "            <div class=\"location-item-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"Звездность отеля 4 из 5\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-empty.png\" alt=\"\" class=\"image-star\">\n" +
            "            </div>\n" +
            "            <span class=\"location-item-otel\">\n" +
            "              Lagoona empujar la&nbsp;creatividad\n" +
            "            </span>\n" +
            "            <span class=\"location-item-position\">\n" +
            "              Барселона, Испания\n" +
            "            </span>\n" +
            "            <button class=\"btn location-btn\">\n" +
            "              Номера\n" +
            "            </button>\n" +
            "          </li>\n" +
            "          <li class=\"location-item-card\">\n" +
            "            <img src=\"/img/location-otel_6.jpg\" alt=\"Вид отеля\" class=\"location-item-image\">\n" +
            "            <div class=\"location-item-price\">\n" +
            "              <span class=\"location-item-price__from\">\n" +
            "                от\n" +
            "              </span>\n" +
            "              <span class=\"location-item-price__numbers\">\n" +
            "                6&nbsp;320&nbsp;₽\n" +
            "              </span>\n" +
            "              <span class=\"location-item-price__period\">\n" +
            "                / ночь\n" +
            "              </span>\n" +
            "            </div>\n" +
            "            <div class=\"location-item-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"Звездность отеля 4 из 5\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-empty.png\" alt=\"\" class=\"image-star\">\n" +
            "            </div>\n" +
            "            <span class=\"location-item-otel\">\n" +
            "              Lagoona el&nbsp;retorno\n" +
            "            </span>\n" +
            "            <span class=\"location-item-position\">\n" +
            "              Барселона, Испания\n" +
            "            </span>\n" +
            "            <button class=\"btn location-btn\">\n" +
            "              Номера\n" +
            "            </button>\n" +
            "          </li>\n" +
            "          <li class=\"location-item-card\">\n" +
            "            <img src=\"/img/location-otel_7.jpg\" alt=\"Вид отеля\" class=\"location-item-image\">\n" +
            "            <div class=\"location-item-price\">\n" +
            "              <span class=\"location-item-price__from\">\n" +
            "                от\n" +
            "              </span>\n" +
            "              <span class=\"location-item-price__numbers\">\n" +
            "                6&nbsp;505&nbsp;₽\n" +
            "              </span>\n" +
            "              <span class=\"location-item-price__period\">\n" +
            "                / ночь\n" +
            "              </span>\n" +
            "            </div>\n" +
            "            <div class=\"location-item-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"Звездность отеля 5 из 5\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "            </div>\n" +
            "            <span class=\"location-item-otel\">\n" +
            "              Lagoona mentira piadosa\n" +
            "            </span>\n" +
            "            <span class=\"location-item-position\">\n" +
            "              Барселона, Испания\n" +
            "            </span>\n" +
            "            <button class=\"btn location-btn\">\n" +
            "              Номера\n" +
            "            </button>\n" +
            "          </li>\n" +
            "          <li class=\"location-item-card\">\n" +
            "            <img src=\"/img/location-otel_8.jpg\" alt=\"Вид отеля\" class=\"location-item-image\">\n" +
            "            <div class=\"location-item-price\">\n" +
            "              <span class=\"location-item-price__from\">\n" +
            "                от\n" +
            "              </span>\n" +
            "              <span class=\"location-item-price__numbers\">\n" +
            "                6&nbsp;824&nbsp;₽\n" +
            "              </span>\n" +
            "              <span class=\"location-item-price__period\">\n" +
            "                / ночь\n" +
            "              </span>\n" +
            "            </div>\n" +
            "            <div class=\"location-item-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"Звездность отеля 4 из 5\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-fill.png\" alt=\"\" class=\"image-star\">\n" +
            "              <img src=\"/img/star-empty.png\" alt=\"\" class=\"image-star\">\n" +
            "            </div>\n" +
            "            <span class=\"location-item-otel\">\n" +
            "              Lagoona suerte sigui&oacute; siempre\n" +
            "            </span>\n" +
            "            <span class=\"location-item-position\">\n" +
            "              Барселона, Испания\n" +
            "            </span>\n" +
            "            <button class=\"btn location-btn\">\n" +
            "              Номера\n" +
            "            </button>\n" +
            "          </li>\n" +
            "          <li class=\"location-item-more\">\n" +
            "            <a href=\"#\" class=\"location-item-more__link location-icon\">\n" +
            "              Посмотреть все варианты\n" +
            "            </a>\n" +
            "          </li>\n" +
            "        </ul>\n" +
            "      </div>\n" +
            "    </section>\n" +
            "    <section id=\"want-tour\" class=\"want-tour\">\n" +
            "      <div class=\"container\">\n" +
            "        <h2 class=\"section-title want-tour-title\">\n" +
            "          Хочу тур\n" +
            "        </h2>\n" +
            "      </div>\n" +
            "    </section>\n" +
            "\n" +
            "    <section id=\"contacts\" class=\"contacts\">\n" +
            "      <div class=\"container\">\n" +
            "        <h2 class=\"section-title contacts-title\">\n" +
            "          Контакты\n" +
            "        </h2>\n" +
            "      </div>\n" +
            "    </section>\n" +
            "  </main>\n" +
            "\n" +
            "\n" +
            "</body>\n" +
            "\n" +
            "</html>\n";

    public Test() throws IOException {
    }

    Map<String, Integer> getLemmaFromText(String text) throws IOException {
        HashMap<String, Integer> map = new HashMap<>();

        String regex = ">([^0-9<>]+)</";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text.toLowerCase());
        while (matcher.find()) {
            String textInnerTag = matcher.group(1).replaceAll("&[a-z]+;", " ").trim();
            if (!textInnerTag.isBlank()){
                List<String> wordsText = Arrays.stream(textInnerTag.split("[^а-яёА-Я]")).toList();
                for (String word : wordsText) {
                    if (!word.isBlank() && !isOfficialPartsSpeech(word)) {
                        int count = 1;
                        List<String> listLemmas = luceneMorph.getNormalForms(word);
                        //  System.out.println(listLemmas);

                        String lemma = listLemmas.get(0);
                        if (map.containsKey(lemma)) {
                            map.put(lemma, map.get(lemma) + count);
                        } else
                            map.put(lemma, 1);
                    }
                }
            }
        }
        return map;
    }

    boolean isOfficialPartsSpeech (String word) throws IOException
    {  // является служебной Частью Речи
        List<String> valuesForChecking = List.of("МЕЖД", "ПРЕДЛ", "СОЮЗ");
        List<String> stringList = luceneMorph.getMorphInfo(word);
        boolean result = false;
        for (String value : valuesForChecking) {
            result = stringList.stream().anyMatch(w -> w.contains(value));
            if (result) {
                break;
            }
        }
        return result;
    }

    public static void main(String[] args) throws IOException {

//        List<String> wordBaseForms =
//                luceneMorph.getNormalForms("лес");
//        wordBaseForms.forEach(System.out::println);



//        List<String> wordBaseForms =
//                luceneMorph.getMorphInfo("лес");

        // wordBaseForms.forEach(System.out::println);
        LuceneMorphology luceneMorphology = new RussianLuceneMorphology();

        Long start = System.currentTimeMillis();


        Test test = new Test();
        Map<String, Integer> lemmaMap = test.getLemmaFromText(text);

        for (Map.Entry<String, Integer> entry : lemmaMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }



//        List<String> stringList = new ArrayList<>();
//        String regex = ">([^0-9><]+)</";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(text.toLowerCase());
//        while (matcher.find()) {
//            String innerTag = matcher.group(1).replaceAll("&[a-z]+;", " ").trim();
//            if (!innerTag.isBlank()){
//                String[] arrayString = innerTag.split("[^а-яё]");
//                stringList.addAll(Arrays.stream(arrayString).toList());
//            }
//           // System.out.println(innerTag);
//        }
//        for (String string : stringList) {
//            System.out.println(string);
//        }

        System.out.println("Время выполнения:" + (System.currentTimeMillis() - start));









//        public static LemmaEntity createLemma (String word, SiteEntity site) throws IOException {
//            LemmaEntity lemmaEntity = new LemmaEntity();
//            lemmaEntity.setLemma(word);
//            lemmaEntity.setSite(site);
//            lemmaEntity.setFrequency(1);
//            return lemmaEntity;
//        }

//        Map<String, Integer> lemmaMap = getLemmaFromText(text);
//        for (Map.Entry<String, Integer> entry : lemmaMap.entrySet()) {
//            System.out.println(entry.getKey() + ": " + entry.getValue());
//        }

    }


}
