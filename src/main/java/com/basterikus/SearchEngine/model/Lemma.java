package com.basterikus.SearchEngine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Lemma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String lemma;
    private Integer frequency;
    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;

    @OneToMany(mappedBy = "lemma", cascade = CascadeType.ALL)
    private List<Index> index;

    public Lemma(String lemma, int frequency, Site site) {
        this.lemma = lemma;
        this.frequency = frequency;
        this.site = site;
    }
}
