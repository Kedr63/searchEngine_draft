package searchengine.model;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity(name = "page")  // проиндексированные страницы сайта
@Table(indexes = {@Index(name = "PATH_INDEX", columnList = "path")})
public class PageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;

  //  @ManyToOne (cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE})// add (fetch = FetchType.LAZY)
//   @ManyToOne (cascade = {CascadeType.REFRESH})
//    @JoinColumn(name = "site_id")
//    private SiteEntity site;

    @ManyToOne
    @JoinColumn(name = "site_id", referencedColumnName = "id")
    private SiteEntity site;

   @Column(columnDefinition = "VARCHAR(255)", nullable = false, unique = true)
  // @Column(columnDefinition = "TEXT(100)", nullable = false, unique = true)
    private String path;

    @Column(columnDefinition = "INT", nullable = false)
    private int code;

    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    //  @Column(columnDefinition = "TEXT", nullable = false) // попробую TEXT чтоб не получить /java heap space/ -1
    private String content;

    public void setId(int id) {
        this.id = id;
    }

    public SiteEntity getSiteEntity() {
        return site;
    }

    public void setSiteEntity(SiteEntity site) {
        this.site = site;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
