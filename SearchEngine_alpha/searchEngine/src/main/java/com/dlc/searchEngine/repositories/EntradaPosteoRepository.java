package com.dlc.searchEngine.repositories;


import com.dlc.searchEngine.models.entities.DBEntradaPosteo.DBEntradasPosteo;
import com.dlc.searchEngine.models.entities.DBEntradaPosteo.DBEntradasPosteoPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntradaPosteoRepository extends JpaRepository<DBEntradasPosteo, DBEntradasPosteoPK> {

    //@Query(value = "Select e from DBEntradasPosteo e where e.clave.termino = ?1 order by e.tf DESC", nativeQuery = false)
    @Query(value = "Select * from entradas e where e.id_termino = ?1  order by e.tf DESC", nativeQuery = true)
    List<DBEntradasPosteo> findByClaveOrderByTfTfDesc(Integer idTermino);



}
