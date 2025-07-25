package searchengine.dto.dtoToBD;

import lombok.Data;

@Data
//@Getter
//@Setter
//@Builder
public class PageDto {
    private int id;
    private int siteId;
    private String path;
    private int code;
    private String content;

    public PageDto(){
    }

}
