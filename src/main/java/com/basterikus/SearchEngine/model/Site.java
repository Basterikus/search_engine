package com.basterikus.SearchEngine.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "site")
public class Site {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "status_time")
    private Date statusTime;
    @Column(name = "last_error")
    private String lastError;
    private String url;
    private String name;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    private List<Page> page;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    private List<Lemma> lemma;
}
