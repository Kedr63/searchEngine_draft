package searchengine.services.indexService.pageParser;

import searchengine.dto.indexing.PageParsed;

import java.io.IOException;

public interface PageParseable {

    /**
     * Получим из {@code URL} пропарсенный HTML Document со status code ответа.
     * Если метод выбросит {@code IOException}, то в {@code catch} блоке класса {@code HtmlRecursiveParser}
     * удалим {@code pageEntity}, который начали добавлять в БД
     *
     * @param url в виде <i><b>https://camper-ural.ru/campers/turist-plus</b></i>
     */
    PageParsed getParsedPage(String url) throws IOException;
}
