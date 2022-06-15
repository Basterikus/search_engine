package com.basterikus.SearchEngine.repository;

import com.basterikus.SearchEngine.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteRepository extends JpaRepository<Site, Integer> {
    Site findByUrl(String url);
}
