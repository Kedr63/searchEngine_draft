package searchengine.dto;

import lombok.Data;
import searchengine.model.StatusIndex;

import java.time.LocalDateTime;

@Data
public class SiteDto {

    private int id;
    private String url;
    private String name;
    private StatusIndex statusIndex;
    private LocalDateTime statusTime;


    public SiteDto() {

    }
}
