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
    @JoinColumn(name = "page_id", columnDefinition = "INT", nullable = false)
    private PageEntity page;

    @ManyToOne
    @JoinColumn(name = "lemma_id", columnDefinition = "INT", nullable = false)
    private LemmaEntity lemma;

    @Column(columnDefinition = "FLOAT", nullable = false)
    private float ranting; // было int
}
