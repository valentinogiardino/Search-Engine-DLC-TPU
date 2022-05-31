package com.dlc.searchEngine.repositories;

import com.dlc.searchEngine.models.entities.DBTerminos2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TerminoRepository extends JpaRepository<DBTerminos2, Integer> {

}
