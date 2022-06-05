package com.dlc.searchEngine.controllers;

import com.dlc.searchEngine.services.IndexadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@RequestMapping("/indexar")
public class IndexadorController {

    @Autowired
    private IndexadorService indexadorService;


    @PostMapping("/guardar")
    public String getTerminos(@RequestBody String path) throws SQLException {
        indexadorService.indexarDocumentos(path);
        boolean exito = indexadorService.save();
        if (exito) {return "Insercion realizada";}
        return "Hubo un error en la insersion";

    }

}
