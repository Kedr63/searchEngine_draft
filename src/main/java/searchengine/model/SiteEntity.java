package searchengine.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "site")
public class SiteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", nullable = false)
    private int id;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    private List<PageEntity> pageEntities;

    @Enumerated(EnumType.STRING)
   // @Column(columnDefinition = "ENUM")
    private StatusIndex status;

    @Column(columnDefinition = "DATETIME", nullable = false)
    private LocalDateTime statusTime;

    @Column(columnDefinition = "TEXT")
    private String lastError;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String url;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String name;

    public int getId() {
        return id;
    }

    public StatusIndex getStatus() {
        return status;
    }

    public void setStatus(StatusIndex status) {
        this.status = status;
    }

    public LocalDateTime getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(LocalDateTime statusTime) {
        this.statusTime = statusTime;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PageEntity> getPages() {
        return pageEntities;
    }

    public void setPages(List<PageEntity> pageEntities) {
        this.pageEntities = pageEntities;
    }
}
