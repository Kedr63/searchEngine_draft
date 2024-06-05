package searchengine.dto.indexing;

import lombok.Getter;
import lombok.Setter;
import org.jsoup.nodes.Document;

@Setter
@Getter
public class DocumentParsed {

    private Document doc;
    private int code;
   // private String errorMessage;

    public DocumentParsed() {
    }

    public DocumentParsed(Document doc, int code) {
        this.doc = doc;
        this.code = code;
    }

    //    public String getErrorMessage() {
//        return errorMessage;
//    }
//
//    public void setErrorMessage(String errorMessage) {
//        this.errorMessage = errorMessage;
//    }
}
