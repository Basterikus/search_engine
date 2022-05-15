package com.basterikus.SearchEngine.repository;

import com.basterikus.SearchEngine.model.Index;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexRepository extends JpaRepository<Index, Integer> {
}