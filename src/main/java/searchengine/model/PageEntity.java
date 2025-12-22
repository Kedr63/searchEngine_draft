package searchengine.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "page")
@Table(indexes = {@Index(name = "PATH_INDEX", columnList = "path")})
public class PageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "site_id", referencedColumnName = "id")
    private SiteEntity site;

   @Column(columnDefinition = "VARCHAR(255)", nullable = false, unique = false) // уберем unique = true, т.к. у сайтов пути могут быть
   // одинаковые, например ("/")
    private String path;

    @Column(columnDefinition = "INT", nullable = false)
    private int code;

    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    private String content;
}
