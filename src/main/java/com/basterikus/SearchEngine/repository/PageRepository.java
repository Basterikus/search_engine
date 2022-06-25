package com.basterikus.SearchEngine.repository;

import com.basterikus.SearchEngine.model.Lemma;
import com.basterikus.SearchEngine.model.Page;
import com.basterikus.SearchEngine.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PageRepository extends JpaRepository<Page, Integer> {
    List<Page> findBySite(Site site);
    Long countBySite(Site site);

    @Query(value = "SELECT p FROM Page p " +
            "JOIN Index i ON p = i.page " +
            "WHERE i.lemma IN :lemmas GROUP BY i.page HAVING COUNT(*) > 1")
    List<Page> findByLemmaList(@Param("lemmas") Collection<Lemma> lemmaList);

    @Query(value = "SELECT p FROM Page p JOIN Index i ON p = i.page WHERE i.lemma IN :lemmas")
    List<Page> findByLemma(@Param("lemmas") Collection<Lemma> lemmaList);
}
