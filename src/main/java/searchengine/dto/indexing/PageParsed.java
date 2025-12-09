package searchengine.dto.indexing;

import lombok.Getter;
import lombok.Setter;
import org.jsoup.nodes.Document;

/**
 * Хранит в себе результат парсинга страницы сайта с помощью библиотеки Jsoup.
 * А именно HTML Document и статус кода Response  */
@Setter
@Getter
public class PageParsed {
    private Document doc;
    private int code;

    public PageParsed() {
    }

    public PageParsed(Document doc, int code) {
        this.doc = doc;
        this.code = code;
    }
}
