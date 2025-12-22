package searchengine.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "site")
public class SiteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;

  /** сайту нет смысла знать о своих страницах,
   * а вот страницам нужно знать какому сайту они принадлежат (однонаправленное отношение)*/
 //   @OneToMany(mappedBy = "site")  // https://sky.pro/wiki/java/ispolzovanie-mapped-by-v-jpa-i-hibernate-obyasnenie/
  //  private List<PageEntity> pageEntities;

    @Enumerated(EnumType.STRING)
    private StatusIndex status;

    @Column(columnDefinition = "DATETIME", nullable = false)
    private LocalDateTime statusTime;

    @Column(columnDefinition = "TEXT(100)")
    private String lastError;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String url;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String name;

}
