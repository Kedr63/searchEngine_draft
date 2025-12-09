package searchengine.exceptions;

import searchengine.services.indexService.IndexServiceImp;

/**
 * {@code IllegalMethodException} может быть брошен в методе {@link IndexServiceImp IndexingResponse startIndexing()}.
 * Это <i>исключение</i> перехватит класс {@code DefaultAdvice} с <i>аннотацией</i> {@code @ControllerAdvice}, обработает и вернет
 * {@code IndexingResponse} для метода {@code startIndexing()}
 * <p>Класс {@code DefaultAdvice} с аннотацией {@code @ControllerAdvice} следит глобально при работе
 * приложения за выбрасываемыми исключениями, которые прописаны в этом классе.</p>
 * @see <a href = "https://docs.oracle.com/en/java/javase/22/docs/specs/javadoc/doc-comment-spec.html"> javaDocOracle</a>
 * @see <a href ="https://www.baeldung.com/javadoc">Introduction to Javadoc</a>
 */
public class IllegalMethodException extends RuntimeException{

    public IllegalMethodException(String message) {
        super(message);
    }
}
