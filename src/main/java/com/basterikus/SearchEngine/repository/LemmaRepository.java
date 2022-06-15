package com.basterikus.SearchEngine.repository;

import com.basterikus.SearchEngine.model.Lemma;
import com.basterikus.SearchEngine.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LemmaRepository extends JpaRepository<Lemma, Integer> {
    Lemma findByLemma(String lemma);
    List<Lemma> findBySite(Site site);
    Long countBySite(Site site);
}
