package com.basterikus.SearchEngine.model;

import com.mysql.cj.protocol.ColumnDefinition;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String path;
    private int statusCode;
    @Column(columnDefinition="MEDIUMTEXT")
    private String content;

    public Page(String path, int statusCode, String content) {
        this.path = path;
        this.statusCode = statusCode;
        this.content = content;
    }
}
