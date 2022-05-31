package com.dlc.searchEngine.repositories;

import com.dlc.searchEngine.models.entities.DBDocumentos;
import com.dlc.searchEngine.models.entities.DBEntradaPosteo.DBEntradasPosteo;
import com.dlc.searchEngine.models.entities.DBEntradaPosteo.DBEntradasPosteoPK;
import com.dlc.searchEngine.models.entities.DBTerminos2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.util.*;

@Repository
public class IndexadorRepository {

    @Autowired
    private DocumentoRepository documentoRepository;

    @PersistenceContext
    private EntityManager em;

    private Hashtable<String, DBDocumentos> tablaDocumentosEnBase;


    public Hashtable<String, DBDocumentos> obtenerDocumentos()
    {
        List<DBDocumentos> listaDocumentosEnBase = documentoRepository.findAll();
        Hashtable<String, DBDocumentos> tablaDocumentosEnBase = new Hashtable<>();
        for (DBDocumentos documentosEnBase:listaDocumentosEnBase) {
            tablaDocumentosEnBase.put(documentosEnBase.getNombre(), documentosEnBase);
        }
        this.tablaDocumentosEnBase = tablaDocumentosEnBase;
        return tablaDocumentosEnBase;
    }


    @Transactional
    public void guardarDocumentos(ArrayList<String> lista, Hashtable<String, Integer> tablaDocumentos)
    {
        int c = 0;
        for (String documento: lista)
        {
            DBDocumentos dbDocumento = new DBDocumentos(documento, c);
            em.persist(dbDocumento);
            tablaDocumentos.put(dbDocumento.getNombre(), dbDocumento.getIdDocumento());
            c++;
        }
    }


    @Transactional
    public void save(Hashtable<String, Hashtable<String, Integer>> tablaPosteo, Hashtable<String, Integer> tablaDocumentos, ArrayList<String> lista) throws SQLException {
        guardarDocumentos(lista, tablaDocumentos);

        Set<Map.Entry<String, Hashtable<String, Integer>>> conjuntoDeParesEnTabla = tablaPosteo.entrySet();

        int cont = 0;
        int idTerm = -1;
        for (Map.Entry<String, Hashtable<String, Integer>> par : conjuntoDeParesEnTabla) {

            String termino = par.getKey();
            idTerm++;
            //DBTerminos dbTerminos = new DBTerminos(termino, idTerm);
            //-em.persist(dbTerminos);

            Hashtable<String, Integer> tablaEntradasDelTermino = par.getValue();
            int maxTf = 0;
            int nr = tablaEntradasDelTermino.size();
            Set<Map.Entry<String, Integer>> conjuntoDeParesEnTablaDelTermino = tablaEntradasDelTermino.entrySet();
            for (Map.Entry<String, Integer> parDelTermino : conjuntoDeParesEnTablaDelTermino)   //POR CADA PAR DE LA TABLA
            {
                int tfEntradaActual = parDelTermino.getValue();
                if (tfEntradaActual > maxTf) {
                    maxTf = tfEntradaActual;
                }
                int idDoc = tablaDocumentos.get(parDelTermino.getKey());
                DBEntradasPosteoPK entradasPosteoPk = new DBEntradasPosteoPK(idDoc, idTerm);
                DBEntradasPosteo entradasPosteo = new DBEntradasPosteo(entradasPosteoPk, parDelTermino.getValue());
                em.persist(entradasPosteo);
                cont++;
                if (cont % 100000 == 0) {
                    em.flush();
                    em.clear();


                }
            }
            //tablaTerminos.put(termino, new DatosTermino(idTerm, maxTf, nr));
            DBTerminos2 dbTerminos2 = new DBTerminos2(idTerm, termino, maxTf, nr);
            em.persist(dbTerminos2);
        }
    }
    @Transactional
    public boolean save2(Hashtable<String, Hashtable<String, Integer>> tablaPosteo, Hashtable<String, Integer> tablaDocumentos, ArrayList<String> lista) throws SQLException {
        //guardarDocumentos(lista, tablaDocumentos);

        boolean exito = false;

        int nuevoIdDocumento = tablaDocumentosEnBase.size();
        int c = nuevoIdDocumento;
        for (String documento: lista)
        {
            DBDocumentos nuevoDoc = new DBDocumentos(documento, c);
            em.persist(nuevoDoc);
            tablaDocumentos.put(nuevoDoc.getNombre(), nuevoDoc.getIdDocumento());
            c++;

        }

        List<DBTerminos2> listaTerminosEnBase = em.createQuery("select e from DBTerminos2 e").getResultList();
        Hashtable<String, DBTerminos2> tablaTerminos = new Hashtable<>();
        for (DBTerminos2 termino:listaTerminosEnBase) {
            tablaTerminos.put(termino.getNombre(), termino);
        }

        Set<Map.Entry<String, Hashtable<String, Integer>>> conjuntoDeParesEnTabla = tablaPosteo.entrySet();

        int cont = 0;
        int idTerm = listaTerminosEnBase.size();
        for (Map.Entry<String, Hashtable<String, Integer>> par : conjuntoDeParesEnTabla) {

            String termino = par.getKey();
            idTerm++;
            Hashtable<String, Integer> tablaEntradasDelTermino = par.getValue();



            int maxTf = 0;
            int nr = tablaEntradasDelTermino.size();
            Set<Map.Entry<String, Integer>> conjuntoDeParesEnTablaDelTermino = tablaEntradasDelTermino.entrySet();

            boolean elTerminoExiste = false;
            for (Map.Entry<String, Integer> parDelTermino : conjuntoDeParesEnTablaDelTermino)   //POR CADA PAR DE LA TABLA
            {

                int idDoc = tablaDocumentos.get(parDelTermino.getKey());


                int tfEntradaActual = parDelTermino.getValue();
                if( tfEntradaActual > maxTf)
                {
                    maxTf = tfEntradaActual;
                }
                if (!tablaTerminos.containsKey(termino)){

                    DBEntradasPosteoPK entradasPosteoPk = new DBEntradasPosteoPK(idDoc, idTerm);
                    DBEntradasPosteo entradasPosteo = new DBEntradasPosteo(entradasPosteoPk, parDelTermino.getValue());
                    em.persist(entradasPosteo);
                }
                else{
                    elTerminoExiste = true;
                    idTerm = idTerm -1;
                    DBTerminos2 terminoQueYaExiste = tablaTerminos.get(termino);
                    int idActual = terminoQueYaExiste.getId();

                    DBEntradasPosteoPK entradasPosteoPk = new DBEntradasPosteoPK(idDoc, idActual);
                    DBEntradasPosteo entradasPosteo = new DBEntradasPosteo(entradasPosteoPk, parDelTermino.getValue());
                    em.persist(entradasPosteo);
                }

                cont++;
                if (cont % 100000 == 0) {
                    em.flush();
                    em.clear();

                }

            }
            if (!elTerminoExiste){
                DBTerminos2 nuevoTerminoAInsertar = new DBTerminos2(idTerm, termino, maxTf, nr);
                em.persist(nuevoTerminoAInsertar);

            }
            else{
                idTerm = idTerm -1;
                DBTerminos2 terminoQueYaExiste = tablaTerminos.get(termino);
                int idActual = terminoQueYaExiste.getId();
                int maxTfActual = terminoQueYaExiste.getMaxTf();
                if( maxTf > maxTfActual)
                {
                    maxTfActual = maxTf;
                }
                int nrActual = terminoQueYaExiste.getNr();

                DBTerminos2 nuevoTerminoAInsertar = new DBTerminos2(idActual, termino, maxTfActual, nrActual+nr);
                em.merge(nuevoTerminoAInsertar);

            }

        }
        exito = true;
        return exito;
    }


    //    public void guardarDocumentos()
//    {
//        int c = 0;
//        for (String documento: this.lista)
//        {
//            DBDocumentos dbDocumento = new DBDocumentos(documento, c);
//            documentoRepository.save(dbDocumento);
//            this.tablaDocumentos.put(dbDocumento.getNombre(), dbDocumento.getIdDocumento());
//            c++;
//        }
//    }
//
//
//
//    public void save() throws SQLException {
//
//
//        guardarDocumentos();
//
//        Set<Map.Entry<String, Hashtable<String, Integer>>> conjuntoDeParesEnTabla = tablaPosteo.entrySet();
//
//        int cont = 0;
//        int idTerm = -1;
//
//
//        List<DBEntradasPosteo> listaEntradas = new ArrayList<>();
//        for (Map.Entry<String, Hashtable<String, Integer>> par : conjuntoDeParesEnTabla) {
//
//
//            String termino = par.getKey();
//            idTerm++;
//
//            Hashtable<String, Integer> tablaEntradasDelTermino = par.getValue();
//            int maxTf = 0;
//            int nr = tablaEntradasDelTermino.size();
//            Set<Map.Entry<String, Integer>> conjuntoDeParesEnTablaDelTermino = tablaEntradasDelTermino.entrySet();
//            for (Map.Entry<String, Integer> parDelTermino : conjuntoDeParesEnTablaDelTermino)   //POR CADA PAR DE LA TABLA
//            {
//                int tfEntradaActual = parDelTermino.getValue();
//                if (tfEntradaActual > maxTf) {
//                    maxTf = tfEntradaActual;
//                }
//                int idDoc = tablaDocumentos.get(parDelTermino.getKey());
//                DBEntradasPosteoPK entradasPosteoPk = new DBEntradasPosteoPK(idDoc, idTerm);
//                DBEntradasPosteo entradasPosteo = new DBEntradasPosteo(entradasPosteoPk, parDelTermino.getValue());
//                listaEntradas.add(entradasPosteo);
//                cont++;
//
//                if (cont % 100000 == 0) {
//
//                    entradaPosteoRepository.saveAllAndFlush(listaEntradas);
//                    //entradaPosteoRepository.flush();
//                    listaEntradas.clear();
//                }
//            }
//
//            //tablaTerminos.put(termino, new DatosTermino(idTerm, maxTf, nr));
//            DBTerminos2 dbTerminos2 = new DBTerminos2(idTerm, termino, maxTf, nr);
//            terminoRepository.save(dbTerminos2);
//        }
//        entradaPosteoRepository.saveAll(listaEntradas);
//    }



}
