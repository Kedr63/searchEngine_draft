package searchengine.dto.statistics;

import lombok.Data;

@Data
// генерирует геттеры для всех полей, сеттеры для всех непостоянных полей, методы toString, equals и hashCode,
// а также конструктор, который инициализирует все конечные поля и непостоянные поля без инициализатора, отмеченные @NonNull.
public class TotalStatistics {
    private int sites;
    private int pages;
    private int lemmas;
    private boolean indexing;
}
