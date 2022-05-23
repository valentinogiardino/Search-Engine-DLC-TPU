package utn.tpu_persistance.Dominio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class Programa {

    public static void main(String[] args) throws FileNotFoundException, SQLException, IOException
    {
        String path = "E:\\UTN-FRC\\Cuarto Año\\DLC\\TPU\\DocumentosTP1\\"; //ESPECIFICAR DIRECCION DE CARPETA DE DOCUMENTOS

        Gestor2 gestor = new Gestor2(path);//SE INSTANCIA AL GESTOR TABLA POSTEO

        long inicio = System.currentTimeMillis();   //SE TOMA EL TIEMPO DE INICIO

        //gestor.indexarDocumentos();              //SE LE PIDE AL GESTOR QUE LEA LOS ARCHIVOS Y GENERE LA TABLA DE POSTEO
        //gestor.mostrarTablaPosteo();             //SE LE PIDE AL GESTOR QUE MUESTRE LA TABLA

        long inicioInsersion = System.currentTimeMillis();
        //gestor.save7();
        gestor.obtenerVocabulario();


        int cantidadR = 10;
        String consulta = "quixote";

        gestor.procesarConsulta(consulta, 10);


        //gestor.mostrarVocabulario();

        long fin = System.currentTimeMillis();      //SE TOMA EL TIEMPO FINAL
        double tiempo = (double) ((fin - inicio)/1000); //SE CALCULA LA DEMORA
        double tiempoInsercion = (double) ((fin - inicioInsersion)/1000); //SE CALCULA LA DEMORA


        System.out.println("Tiempo procesamiento: " + tiempo + " segundos"); //SE MUESTRA EL TIEMPO DE PROCESAMIENTO
        System.out.println("Tiempo insercion: " + tiempoInsercion + " segundos");
    }
//    public static void main(String[] args) throws FileNotFoundException, SQLException, IOException
//    {
//        String path = "E:\\UTN-FRC\\Cuarto Año\\DLC\\TPU\\DocumentosTP1\\"; //ESPECIFICAR DIRECCION DE CARPETA DE DOCUMENTOS
//
//        GestorTablaPosteo gestor = new GestorTablaPosteo(path);//SE INSTANCIA AL GESTOR TABLA POSTEO
//
//        long inicio = System.currentTimeMillis();   //SE TOMA EL TIEMPO DE INICIO
//
//        gestor.indexarDocumentos();              //SE LE PIDE AL GESTOR QUE LEA LOS ARCHIVOS Y GENERE LA TABLA DE POSTEO
//        gestor.mostrarTablaPosteo();             //SE LE PIDE AL GESTOR QUE MUESTRE LA TABLA
//
//        long inicioInsersion = System.currentTimeMillis();
//        //gestor.save6();           //NO IMPLEMENTADO TODAVIA
//
//        long fin = System.currentTimeMillis();      //SE TOMA EL TIEMPO FINAL
//        double tiempo = (double) ((fin - inicio)/1000); //SE CALCULA LA DEMORA
//        double tiempoInsercion = (double) ((fin - inicioInsersion)/1000); //SE CALCULA LA DEMORA
//
//
//        System.out.println("Tiempo procesamiento: " + tiempo + " segundos"); //SE MUESTRA EL TIEMPO DE PROCESAMIENTO
//        System.out.println("Tiempo insercion: " + tiempoInsercion + " segundos");
//    }

}