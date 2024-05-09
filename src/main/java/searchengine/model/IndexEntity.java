package searchengine.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "indexes")
// @OrderColumn
public class IndexEntity {

//    @EmbeddedId
//    private KeyIndex id;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "page_id", referencedColumnName = "id")
    private PageEntity page;

    @ManyToOne
    @JoinColumn(name = "lemma_id", referencedColumnName = "id")
    private LemmaEntity lemma;
 
//    @Column(columnDefinition = "INT", nullable = false)
    private int rating;

//    public IndeksEntity() {
//    }
//
//    public IndeksEntity(int id, LemmaEntity lemma, PageEntity page) {
//        this.id = id;
//        this.lemma = lemma;
//        this.page = page;
//    }

}
