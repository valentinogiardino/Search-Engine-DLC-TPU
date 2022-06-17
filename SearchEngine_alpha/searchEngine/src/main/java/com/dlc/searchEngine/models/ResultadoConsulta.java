package com.dlc.searchEngine.models;

import java.io.File;
/**
 * Clase para representar los resultados a una consulta.
 * La consulta mostrara como resultado el nombre del documento, su Indice de relevancia(IR) y un pequeño resumen
 * del contenido del documento.
 */
public class ResultadoConsulta {
    private int id;
    private String nombreDoc;
    private double ir;
    private String resumen;

    public ResultadoConsulta(int id, String nombreDoc, double ir) {
        this.id = id;
        this.nombreDoc = nombreDoc;
        this.ir = ir;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreDoc() {
        return nombreDoc;
    }

    public void setNombreDoc(String nombreDoc) {
        this.nombreDoc = nombreDoc;
    }

    public double getIr() {
        return ir;
    }

    public void setIr(double ir) {
        this.ir = ir;
    }


    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }
}
