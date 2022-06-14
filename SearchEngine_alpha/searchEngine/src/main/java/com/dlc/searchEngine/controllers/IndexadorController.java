package com.dlc.searchEngine.controllers;

import com.dlc.searchEngine.services.IndexadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.List;

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


    @PostMapping("/guardar2")
    public ResponseEntity<String> subirArchivos(@RequestParam("files") List<MultipartFile> files) throws SQLException {
        try {
            boolean exito = indexadorService.saveFile(files);
            if (exito){
                return ResponseEntity.status(HttpStatus.OK).body("Archivos cargados correcatamente");
            }
            else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrio un error durante la insercion");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrio un error al subir el archivo");
        }

    }

}
