package com.dlc.searchEngine.controllers;

import com.dlc.searchEngine.services.IndexadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@CrossOrigin
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
