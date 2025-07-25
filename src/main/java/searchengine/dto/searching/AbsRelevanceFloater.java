package searchengine.dto.searching;

import lombok.Data;

//@Getter
//@Setter
@Data
public class AbsRelevanceFloater implements Comparable<AbsRelevanceFloater> {
    private float rankValue;

    public AbsRelevanceFloater() {
    }

    public AbsRelevanceFloater(float rankValue) {
        this.rankValue = rankValue;
    }

    @Override
    public int compareTo(AbsRelevanceFloater o) {
        return Float.compare(this.rankValue, o.rankValue);
    }
}
