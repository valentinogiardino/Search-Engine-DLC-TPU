package com.dlc.searchEngine.repositories;

import com.dlc.searchEngine.models.entities.DBDocumentos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentoRepository extends JpaRepository<DBDocumentos, Integer> {

}
