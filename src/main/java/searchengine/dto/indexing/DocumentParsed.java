package searchengine.dto.indexing;

import org.jsoup.nodes.Document;

public class DocumentParsed {

    private Document doc;
    private int code;

    public DocumentParsed(Document doc, int code) {
        this.doc = doc;
        this.code = code;
    }

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
