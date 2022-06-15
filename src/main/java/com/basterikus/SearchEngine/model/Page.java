package com.basterikus.SearchEngine.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "page")
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String path;
    private Integer statusCode;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;
    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;

    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL)
    private List<Index> index;

    public Page(String path, int statusCode, String content, Site site) {
        this.path = path;
        this.statusCode = statusCode;
        this.content = content;
        this.site = site;
    }
}
