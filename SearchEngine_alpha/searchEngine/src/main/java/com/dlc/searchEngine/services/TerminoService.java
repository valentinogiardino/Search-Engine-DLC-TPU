package com.dlc.searchEngine.services;

import com.dlc.searchEngine.models.entities.DBTerminos2;
import com.dlc.searchEngine.repositories.TerminoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TerminoService {

    @Autowired
    private TerminoRepository terminoRepository;

    public List<DBTerminos2> getTerminos() {
        return terminoRepository.findAll();

    }
}
