package com.dlc.searchEngine.services;

import com.dlc.searchEngine.models.entities.DBDocumentos;
import com.dlc.searchEngine.models.entities.DBEntradaPosteo.DBEntradasPosteo;
import com.dlc.searchEngine.models.entities.DBTerminos2;
import com.dlc.searchEngine.models.ResultadoConsulta;
import com.dlc.searchEngine.repositories.DocumentoRepository;
import com.dlc.searchEngine.repositories.EntradaPosteoRepository;
import com.dlc.searchEngine.repositories.TerminoRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@ApplicationScoped      //Persiste durante toda la sesion
@Service
public class ConsultaService {

    //Inyeccion de dependencias
    @Autowired
    private TerminoRepository terminoRepository;
    @Autowired
    private DocumentoRepository documentoRepository;
    @Autowired
    private EntradaPosteoRepository entradaPosteoRepository;

    //Tablas de terminios y documentos
    public Hashtable<String, DBTerminos2> tablaTerminos = new Hashtable<>();
    public Hashtable<Integer, String> tablaDocumentos = new Hashtable<>();

    @Getter@Setter
    private int cantidadAMostrar = 10;      //R por defecto (cantidad de documentos a traer como maximo por termino en la consulta)

    String folderPath = "C:/Users/valen/Desktop/DLC/DLC/TPU/DocumentosTP1/";     //Path del proyecto donde tendremos los archivos


    /**
     * Genera una HashTable con todos los terminos en la BD.
     * LLama terminoRepository para que se encargue del acceso a la BD.
     */
    public List<DBTerminos2> obtenerVocabulario() {

        List<DBTerminos2> listaTerminos = terminoRepository.findAll();

        for (DBTerminos2 termino : listaTerminos) {
            tablaTerminos.put(termino.getNombre(), termino);        //Meto los terminos de la base en una HashTable
        }
        return listaTerminos;

    }

    /**
     * Genera una HashTable con todos los documentos en la BD.
     * LLama documentoRepository para que se encargue del acceso a la BD.
     */
    public List<DBDocumentos> obtenerDocumentos() {

        List<DBDocumentos> listaDocumentos = documentoRepository.findAll();

        for (DBDocumentos documento : listaDocumentos) {
            tablaDocumentos.put(documento.getIdDocumento(), documento.getNombre());
        }
        return listaDocumentos;

    }

    /**
     * Metodo que se encarga del procesamiento de una consulta.
     * @return la lista de los resultados mas relevantes con respecto a la consulta, ordenados por orden de relevancia.
     */
    public List<ResultadoConsulta> procesarConsulta(String consulta) {
        //Valido que tenga las HashTables cargadas, sino las cargo.
        if (tablaTerminos.size() == 0) {
            obtenerVocabulario();
        }
        if (tablaDocumentos.size() == 0) {
            obtenerDocumentos();
        }


        ArrayList<DBTerminos2> listaTerminosEnConsulta = new ArrayList<>();     //ArrayList que contendrà los terminos de la consulta
        Scanner scanner = new Scanner(consulta);
        scanner.useDelimiter("[^\\w]+");

        while (scanner.hasNext()) {
            String nuevoTermino = scanner.next().toLowerCase();
            if (tablaTerminos.containsKey(nuevoTermino)) {
                //Si tengo el termino en la bd, procedo a obtener sus datos (id, maxTf, nr)
                listaTerminosEnConsulta.add(tablaTerminos.get(nuevoTermino));   //Agrego el termino en el ArrayList
            }
        }

        Collections.sort(listaTerminosEnConsulta);      //Ordeno el arrayList por NR (DBTerminos implementa Comparable)
        Hashtable<Integer, Double> tablaResulados = new Hashtable<>(); //Creo HashTable vacia con los resultados


        for (DBTerminos2 dbtermino2 : listaTerminosEnConsulta) {
            //Obtengo las entradas del termino ordenadas de menor a mayor tf
            List<DBEntradasPosteo> listaEntradasDelTermino = entradaPosteoRepository.findByClaveOrderByTfTfDesc(dbtermino2.getId());
            int cont = 0;
            for (DBEntradasPosteo entradaDelTermino : listaEntradasDelTermino) {        //Itero las entradas del termino
                int idDoc = entradaDelTermino.getClave().getDocumento();        //Obtengo el doc de la entrada

                if (!tablaResulados.containsKey(idDoc)) {       //Si el documento no habia aparecido antes, lo agrego e inicializco su IR en 0
                    tablaResulados.put(idDoc, 0.0);
                }
                double irActual = tablaResulados.get(idDoc);    //Obtengo el ir actual del Doc
                double irNuevo = irActual + (entradaDelTermino.getTf() * Math.log(tablaDocumentos.size() / listaEntradasDelTermino.size()));    //Calculo nuevo IR
                tablaResulados.replace(idDoc, irNuevo);         //Actualizo la tabla de resultados con el nuevo IR del doc
                cont++;         //Aumento el contador
                if (cont == cantidadAMostrar) {break;}      //Si iguale la cantidad de documentos a traer por termino, corto.
            }
        }
        Map sortedMap = valueSort(tablaResulados);  //Ordeno los datos de la HashTable

        return mostrarResultados(sortedMap);
        //return sortedMap;

    }

    public static <K, V extends Comparable<V>> Map<K, V>
    valueSort(final Map<K, V> map) {
        // Static Method with return type Map and
        // extending comparator class which compares values
        // associated with two keys
        Comparator<K> valueComparator = new Comparator<K>() {

            // return comparison results of values of
            // two keys
            public int compare(K k1, K k2) {
                int comp = map.get(k2).compareTo(
                        map.get(k1));
                if (comp == 0)
                    return 1;
                else
                    return comp;
            }

        };

        // SortedMap created using the comparator
        Map<K, V> sorted = new TreeMap<K, V>(valueComparator);

        sorted.putAll(map);

        return sorted;
    }


    //Genera una lista de Resultados Consulta segun los resultados obtenidos de la consulta
    private List<ResultadoConsulta> mostrarResultados(Map<Integer, Double> tablaResultados) {
        Set<Map.Entry<Integer, Double>> pares = tablaResultados.entrySet();

        List<ResultadoConsulta> listaResultados = new ArrayList<>();
        for (Map.Entry<Integer, Double> par : pares)   //POR CADA PAR DE LA TABLA
        {
            String nombreDoc = tablaDocumentos.get(par.getKey());
            listaResultados.add(new ResultadoConsulta(par.getKey(), nombreDoc, par.getValue()));

        }
        return listaResultados;
    }


    public List<ResultadoConsulta> showFiles(String consulta) {
        List<ResultadoConsulta> listaResultadosConsulta = procesarConsulta(consulta);
        for (ResultadoConsulta resultadoConsulta: listaResultadosConsulta) {
            String nombre = resultadoConsulta.getNombreDoc();
            String resumen = obtenerResumen(folderPath+nombre);
            resultadoConsulta.setResumen(resumen);
        }
        return listaResultadosConsulta;
    }


    //Obtiene las primeras dos lineas del archivo recibido como parametro.
    public String obtenerResumen(String path) {
        try {
            BufferedReader lector = new BufferedReader(new FileReader(path));
            StringBuilder cadena = new StringBuilder();
            String line = null;

            int c = 0;
            while ((line = lector.readLine()) != null) {
                cadena.append(line);
                cadena.append(" ");
                c++;
                if (c==2){break;}

            }
            lector.close();
            String contenido = cadena.toString();
            return contenido;
        } catch (FileNotFoundException e) {
            return e.getMessage();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return e.getMessage();
        }
    }

    //Segun el modo recibido como parametro, muestra o descarga el archivo
    public void show(String fileName, HttpServletResponse response, String modo) {

        if (fileName.indexOf(".doc") > -1) response.setContentType("application/msword");
        if (fileName.indexOf(".docx") > -1) response.setContentType("application/msword");
        if (fileName.indexOf(".xls") > -1) response.setContentType("application/vnd.ms-excel");
        if (fileName.indexOf(".csv") > -1) response.setContentType("application/vnd.ms-excel");
        if (fileName.indexOf(".ppt") > -1) response.setContentType("application/ppt");
        if (fileName.indexOf(".pdf") > -1) response.setContentType("application/pdf");
        if (fileName.indexOf(".zip") > -1) response.setContentType("application/zip");
        response.setHeader("Content-Disposition", modo+"; filename=" + fileName);
        response.setHeader("Content-Transfer-Encoding", "binary");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
            FileInputStream fis = new FileInputStream(folderPath + fileName);
            int len;
            byte[] buf = new byte[1024];
            while ((len = fis.read(buf)) > 0) {
                bos.write(buf, 0, len);
            }
            bos.close();
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }






}