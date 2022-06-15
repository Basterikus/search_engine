package com.basterikus.SearchEngine.repository;

import com.basterikus.SearchEngine.model.Page;
import com.basterikus.SearchEngine.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageRepository extends JpaRepository<Page, Integer> {
    List<Page> findBySite(Site site);
    Long countBySite(Site site);
}
