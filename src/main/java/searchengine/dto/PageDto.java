package searchengine.dto;

import lombok.Data;

@Data
public class PageDto {
    private int id;
    private int siteId;
    private String path;
    private int code;
    private String content;
}
