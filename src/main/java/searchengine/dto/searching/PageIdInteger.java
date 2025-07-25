package searchengine.dto.searching;

import lombok.Data;

//@Getter
//@Setter
@Data
public class PageIdInteger {

    private int pageId;

    public PageIdInteger() {
    }

    public PageIdInteger(int pageId) {
        this.pageId = pageId;
    }

}
