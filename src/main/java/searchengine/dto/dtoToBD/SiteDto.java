package searchengine.dto.dtoToBD;

import lombok.Data;
import searchengine.model.StatusIndex;

import java.time.LocalDateTime;

@Data
public class SiteDto {

    private int id;
    private String url;
    private String name;
    //  private List<PageDto> pageDtoList;    TODO: delete field
    private StatusIndex statusIndex;
    private LocalDateTime statusTime;
    private String lastError;


    public SiteDto() {
    }

}
