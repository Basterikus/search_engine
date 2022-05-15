package com.basterikus.SearchEngine.model;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "word_index")
public class Index {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "page_id")
    private Page page;
    @ManyToOne
    @JoinColumn(name = "lemma_id")
    private Lemma lemma;
    @Column(name = "word_rank")
    private Float wordRank;

    public Index(Page page, Lemma lemma, Float wordRank) {
        this.page = page;
        this.lemma = lemma;
        this.wordRank = wordRank;
    }
}
