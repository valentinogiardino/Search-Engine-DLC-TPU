package com.dlc.searchEngine.controllers;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;

import com.dlc.searchEngine.models.entities.DBTerminos2;
import com.dlc.searchEngine.models.ResultadoConsulta;
import com.dlc.searchEngine.services.ConsultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/consulta")
public class ConsultaController {
    @Autowired
    private ConsultaService consultaService;


    @RequestMapping("/{consulta}")
    public List<ResultadoConsulta> prueba(@PathVariable String consulta) throws FileNotFoundException {
        return consultaService.showFiles(consulta);
    }

    @RequestMapping("/file/{fileName}")
    @ResponseBody
    public void show(@PathVariable("fileName") String fileName, HttpServletResponse response) {

        consultaService.show(fileName, response, "attachment");
    }


    @RequestMapping("/file/mostrar/{fileName}")
    @ResponseBody
    public void show2(@PathVariable("fileName") String fileName, HttpServletResponse response) {

        consultaService.show(fileName, response, "inline");
    }



    @RequestMapping("/inicializar")
    @EventListener(ApplicationReadyEvent.class)
    public Hashtable<String, DBTerminos2> getTerminos(){
        consultaService.obtenerVocabulario();
        consultaService.obtenerDocumentos();
        return consultaService.tablaTerminos;
    }


    @PostMapping("/setR/{r}")
    public int setR(@PathVariable("r") Integer r){
        consultaService.setCantidadAMostrar(r);
        return consultaService.getCantidadAMostrar();
    }




}