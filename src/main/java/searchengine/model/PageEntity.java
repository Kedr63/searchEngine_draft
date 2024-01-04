package searchengine.model;

import javax.persistence.*;

@Entity(name = "page")  // проиндексированные страницы сайта
@Table(indexes = {@Index(name = "PATH_INDEX", columnList = "path")})
public class PageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY) // add (fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private SiteEntity site;

   @Column(columnDefinition = "VARCHAR(255)", nullable = false, unique = true)
    private String path;

    @Column(columnDefinition = "INT", nullable = false)
    private int code;

    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SiteEntity getSiteEntity() {
        return site;
    }

    public SiteEntity getSite() {
        return site;
    }

    public void setSiteEntity(SiteEntity site) {
        this.site = site;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
