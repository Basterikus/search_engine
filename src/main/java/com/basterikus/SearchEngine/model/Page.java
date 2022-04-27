package com.basterikus.SearchEngine.model;

import com.mysql.cj.protocol.ColumnDefinition;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    String path;
    int statusCode;
    @Column(columnDefinition="MEDIUMTEXT")
    String content;

    public Page(String path, int statusCode, String content) {
        this.path = path;
        this.statusCode = statusCode;
        this.content = content;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
