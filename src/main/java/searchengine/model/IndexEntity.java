package searchengine.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "index_search")

public class IndexEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "page_id", referencedColumnName = "id")
    private PageEntity pageEntity;

    @ManyToOne
    @JoinColumn(name = "lemma_id", referencedColumnName = "id")
    private LemmaEntity lemma;
 
    @Column(columnDefinition = "FLOAT", nullable = false)
    private int ranting;

}
