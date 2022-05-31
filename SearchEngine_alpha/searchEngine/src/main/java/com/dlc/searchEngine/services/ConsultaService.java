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

@ApplicationScoped
@Service
public class ConsultaService {

    @Autowired
    private TerminoRepository terminoRepository;
    @Autowired
    private DocumentoRepository documentoRepository;
    @Autowired
    private EntradaPosteoRepository entradaPosteoRepository;


    public Hashtable<String, DBTerminos2> tablaTerminos = new Hashtable<>();
    public Hashtable<Integer, String> tablaDocumentos = new Hashtable<>();

    @Getter@Setter
    private int cantidadAMostrar = 10;

    String folderPath = "E:/UTN-FRC/Cuarto Año/DLC/TPU/DocumentosTP1/";



    public List<DBTerminos2> obtenerVocabulario() {

        List<DBTerminos2> listaTerminos = terminoRepository.findAll();

        for (DBTerminos2 termino : listaTerminos) {
            tablaTerminos.put(termino.getNombre(), termino);
        }
        return listaTerminos;

    }


    public List<DBDocumentos> obtenerDocumentos() {

        List<DBDocumentos> listaDocumentos = documentoRepository.findAll();

        for (DBDocumentos documento : listaDocumentos) {
            tablaDocumentos.put(documento.getIdDocumento(), documento.getNombre());
        }
        return listaDocumentos;

    }


    public List<ResultadoConsulta> procesarConsulta(String consulta) {
        if (tablaTerminos.size() == 0) {
            obtenerVocabulario();
        }
            if (tablaDocumentos.size() == 0) {
                obtenerDocumentos();
            }
        ArrayList<DBTerminos2> listaTerminosEnConsulta = new ArrayList<>();
        Scanner scanner = new Scanner(consulta);
        scanner.useDelimiter("[^\\w]+");

        while (scanner.hasNext()) {
            String nuevoTermino = scanner.next().toLowerCase();
            if (tablaTerminos.containsKey(nuevoTermino)) {
                listaTerminosEnConsulta.add(tablaTerminos.get(nuevoTermino));
            }
        }

        Collections.sort(listaTerminosEnConsulta);
        Hashtable<Integer, Double> tablaResulados = new Hashtable<>();


        for (DBTerminos2 dbtermino2 : listaTerminosEnConsulta) {
            List<DBEntradasPosteo> listaEntradasDelTermino = entradaPosteoRepository.findByClaveOrderByTfTfDesc(dbtermino2.getId());
            int cont = 0;
            for (DBEntradasPosteo entradaDelTermino : listaEntradasDelTermino) {
                int idDoc = entradaDelTermino.getClave().getDocumento();

                if (!tablaResulados.containsKey(idDoc)) {
                    tablaResulados.put(idDoc, 0.0);
                }
                double irActual = tablaResulados.get(idDoc);
                double irNuevo = irActual + (entradaDelTermino.getTf() * Math.log(tablaDocumentos.size() / listaEntradasDelTermino.size()));
                tablaResulados.replace(idDoc, irNuevo);
                cont++;
                if (cont == cantidadAMostrar) {break;}
            }
        }
        Map sortedMap = valueSort(tablaResulados);

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

    private List<ResultadoConsulta> mostrarResultados(Map<Integer, Double> tablaResultados) {
        Set<Map.Entry<Integer, Double>> pares = tablaResultados.entrySet();

        List<ResultadoConsulta> listaResultados = new ArrayList<>();
        for (Map.Entry<Integer, Double> par : pares)   //POR CADA PAR DE LA TABLA
        {
            String nombreDoc = tablaDocumentos.get(par.getKey());
            listaResultados.add(new ResultadoConsulta(par.getKey(), nombreDoc, par.getValue()));
            //System.out.println(par.getKey() + "->" + par.getValue());
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