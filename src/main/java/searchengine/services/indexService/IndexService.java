package searchengine.services.indexService;

import searchengine.dto.dtoToBD.PageDtoSingle;
import searchengine.dto.indexing.IndexingResponse;


public interface IndexService {

    IndexingResponse startIndexing();

    /** Вызов этого метода в классе {@code HtmlRecursiveParser} поменяет флаг на {@code stopIndexing = true} и рекурсия
     *  начнет останавливаться и {@code join}-ы будут ждать результатов и возвращать результаты наверх
     *  в класс {@code TaskForkJoinPoolForParsingSite}*/
    IndexingResponse stopIndexing();

    IndexingResponse indexSinglePage(PageDtoSingle pageDtoSingle);

}
