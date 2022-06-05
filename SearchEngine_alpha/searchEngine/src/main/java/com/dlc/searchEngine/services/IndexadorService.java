package com.dlc.searchEngine.services;

import com.dlc.searchEngine.models.entities.DBDocumentos;
import com.dlc.searchEngine.models.entities.DBTerminos2;
import com.dlc.searchEngine.repositories.DocumentoRepository;
import com.dlc.searchEngine.repositories.EntradaPosteoRepository;
import com.dlc.searchEngine.repositories.IndexadorRepository;
import com.dlc.searchEngine.repositories.TerminoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class IndexadorService {

    private Hashtable<String, Hashtable<String, Integer>> tablaPosteo = new Hashtable<>();//HASHTABLE PARA CONTENER LA TABLA DE POSTEO
    private String path = "E:\\UTN-FRC\\Cuarto Año\\DLC\\TPU\\nuevosDocs\\";
    private String documento = "";
    private ArrayList<String> lista= new ArrayList();
    private Hashtable<String, Integer> tablaDocumentos = new Hashtable<>();
    private Hashtable<String, DBTerminos2> tablaTerminos = new Hashtable<>();


    @Autowired
    private IndexadorRepository indexadorRepository;
    @Autowired
    private ConsultaService consultaService;




    public void indexarDocumentos(String path1)
    {
        tablaPosteo.clear();
        this.path = path1;
        Hashtable<String, DBDocumentos> tablaDocumentosEnBase = indexadorRepository.obtenerDocumentos();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(path))) {
            for (Path file: stream) {
                String documento = file.getFileName().toString();
                if (!tablaDocumentosEnBase.containsKey(documento)){
                    this.documento = documento;
                    this.lista.add(documento);
                    procesarDocumento();
                }
            }
        } catch (IOException | DirectoryIteratorException ex) {
            System.err.println(ex);
        }
        System.out.println(LocalDateTime.now());
    }
    /**
     * Efectúa la lectura de un archivo, según el path y el documento que la entidad tiene seteados
     * Se establece el delimitador de términos.
     * Por cada archivo se invoca a un método para efectuar su procesamiento.
     */
    private void procesarDocumento()
    {
        Scanner archivo = null;
        try
        {
            archivo = new Scanner(new BufferedReader(new FileReader(path + documento)));

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        archivo.useDelimiter("[^\\w]+");                        //ESTABLECE UNA EXPRESION REGULAR PARA DELIMITAR TERMINOS.
        while (archivo.hasNext())                           //MIENTRAS EL ARCHIVO TENGA UN SIGUIENTE TERMINO
        {
            String termino = archivo.next().toLowerCase();  //TODAS LAS LETRAS SE PASAN A MINUSCULAS
            cargarEntradasDelTermino(termino);
        }
        archivo.close();                                    //SE CIERRA EL ARCHIVO
    }
    /**
     * Genera las entradas en la tabla de posteo para un término
     * @param termino el elemento del vocabulario para el que se crean sus entradas de posteo
     */
    private void cargarEntradasDelTermino(String termino)
    {
        if (tablaPosteo.containsKey(termino))  //SI EL TERMINO YA FUE PROCESADO ALGUNA VEZ****************

        {                                           //TODAS LAS ENTRADAS DE POSTEO QUE YA POSEE CARGADAS EL TERMINO
            Hashtable<String, Integer> conjuntoEntradasPosteo = tablaPosteo.get(termino);
            if (conjuntoEntradasPosteo.containsKey(documento))
            {
                conjuntoEntradasPosteo.replace(documento, conjuntoEntradasPosteo.get(documento)+1);
            }
            else
            {
                crearNuevaEntradaParaElTermino(conjuntoEntradasPosteo); //SE CREA Y AGREGA LA NUEVA ENTRADA
            }

        } else                                   //SI EL TERMINO NUNCA ANTES FUE PROCESADO******************
        {
            agregarTerminoEnTablaPosteo(termino);       //SE PROCEDE A HACERLO POR PRIMERA VEZ
        }
    }

    private void crearNuevaEntradaParaElTermino(Hashtable<String, Integer> conjuntoEntradasPosteo )
    {
        conjuntoEntradasPosteo.put(documento, 1);                          //SE AGREGA LA ENTRADA AL CONJUNTO
    }
    /**
     * Ante el procesamiento de un nuevo término, se crea su respectivo conjunto de entradas de posteo,
     * se agrega una nueva entrada al conjunto y
     * se inserta el par (termino; conjunto entradas de posteo) a la Tabla de posteo.
     *
     * Al crear el Treeset, seteamos el criterio para comparar  y ordenar entradas
     * En este caso descCompartor devuelve una instancia de DescendentComparator con su particular implementaciÓn
     * del método compare
     * @param termino el nuevo termino que se esta procesando por primera vez.
     */
    private void agregarTerminoEnTablaPosteo(String termino)
    {
        //EL NUEVO CONJUNTO DE ENTRADAS DE POSTEO
        Hashtable<String, Integer> nuevoConjuntoEntradasPosteo = new Hashtable<>();

        crearNuevaEntradaParaElTermino(nuevoConjuntoEntradasPosteo);     //SE CREA UNA NUEVA ENTRADA DE POSTEO
        // QUE SE AGREGA AL NUEVO CONJUNTO

        tablaPosteo.put(termino, nuevoConjuntoEntradasPosteo);           //SE AGREGA EL NUEVO PAR A LA TABLA DE POSTEO
    }
    /**
     * Se iteran los pares de elementos de la HashTable y se muestran en consola.
     */
    public void mostrarTablaPosteo() {                                                            //CONJUNTO DE PARES DE ELEMENTOS DE LA TABLA DE POSTEO
        Set<Map.Entry<String, Hashtable<String, Integer>>> entities = tablaPosteo.entrySet();

        for (Map.Entry<String, Hashtable<String, Integer>> entity : entities)   //POR CADA PAR DE LA TABLA
        {
            Hashtable<String, Integer> conjuntoEntradasDelPar = entity.getValue();
            Set<Map.Entry<String, Integer>> entities2 = conjuntoEntradasDelPar.entrySet();

            StringBuffer sb = new StringBuffer("{\n\t\t");         //SE ITERA CADA CONJUNTO DE ENTRADAS

            for (Map.Entry<String, Integer> entity2 : entities2)   //POR CADA PAR DE LA TABLA
            {

                sb.append("\n\t\t("+ entity2.getKey() + ";" + entity2.getValue() + ")");

            }
            sb.append("}");
            System.out.println(entity.getKey() + "=" + sb);

        }
        System.out.println("Cantidad terminos: " + entities.size()); //MUESTRA LA CANTIDAD DE TERMINOS
    }




    public boolean save() throws SQLException {
        boolean exito = indexadorRepository.save2(tablaPosteo, tablaDocumentos, lista);
        consultaService.obtenerDocumentos();
        consultaService.obtenerVocabulario();
        return exito;
    }



}
