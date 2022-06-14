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
        List<DBDocumentos> listaDocumentosEnBase = documentoRepository.findAll();       //OBTENGO LOS DOCUMENTOS DE LA BASE
        Hashtable<String, DBDocumentos> tablaDocumentosEnBase = new Hashtable<>();
        for (DBDocumentos documentosEnBase:listaDocumentosEnBase) {
            tablaDocumentosEnBase.put(documentosEnBase.getNombre(), documentosEnBase);      //METO LOS DOCUMENTOS EN UNA HASHTABLE
        }
        this.tablaDocumentosEnBase = tablaDocumentosEnBase;
        return tablaDocumentosEnBase;
    }



    @Transactional
    public boolean save2(Hashtable<String, Hashtable<String, Integer>> tablaPosteo, Hashtable<String, Integer> tablaDocumentos, ArrayList<String> lista) throws SQLException {

        boolean exito;

        int nuevoIdDocumento = tablaDocumentosEnBase.size();                            //SE OBTIENE EL PROX ID DOCUMENTO
        int c = nuevoIdDocumento;
        for (String documento: lista)       //POR CADA DOCUMENTO NUEVO A INSERTAR
        {
            DBDocumentos nuevoDoc = new DBDocumentos(documento, c);
            em.persist(nuevoDoc);                                                       //SE PERSISTE EL NUEVO DOCUMENTO
            tablaDocumentos.put(nuevoDoc.getNombre(), nuevoDoc.getIdDocumento());       //SE AGREGA EL DOCUMENTO A UNA HASHTABLE
            c++;                                                                        //SE INCREMENTA EL ID PARA EL SIGUIENTE DOCUMENTO

        }

        List<DBTerminos2> listaTerminosEnBase = em.createQuery("select e from DBTerminos2 e").getResultList();      //SE OBTIENEN LOS TERMINOS YA INSERTADOS EN LA BD
        Hashtable<String, DBTerminos2> tablaTerminos = new Hashtable<>();
        for (DBTerminos2 termino:listaTerminosEnBase) {                                                                //SE PASAN LOS TERMINOS A UNA HASHTABLE
            tablaTerminos.put(termino.getNombre(), termino);
        }

        Set<Map.Entry<String, Hashtable<String, Integer>>> conjuntoDeParesEnTabla = tablaPosteo.entrySet();            //SE OBTIENEN TODOS LOS PARES DE LA TABLA POSTEO

        int cont = 0;
        int idTerm = listaTerminosEnBase.size();            //SE OBTIENE EL ULTIMO ID TERMINO
        for (Map.Entry<String, Hashtable<String, Integer>> par : conjuntoDeParesEnTabla) {                    //SE RECORRE CADA PAR DEL CONJUNTO DE PARES DE LA TABLA POSTEO

            String termino = par.getKey();                      //SE OBTIENE EL TERMINO DEL PAR

            Hashtable<String, Integer> tablaEntradasDelTermino = par.getValue();        //SE OBTIENE LA TABLA DE ENTRADAS DEL TERMINO(DOC, TF)


            int maxTf = 0;
            int nr = tablaEntradasDelTermino.size();
            Set<Map.Entry<String, Integer>> conjuntoDeParesEnTablaDelTermino = tablaEntradasDelTermino.entrySet();      //SE OBTIENEN TODAS LAS ENTRADAS DEL TERMINO

            boolean elTerminoExiste = false;
            for (Map.Entry<String, Integer> parDelTermino : conjuntoDeParesEnTablaDelTermino)   //POR CADA PAR DE LA TABLA DE ENTRADAS DEL TERMINO
            {

                int idDoc = tablaDocumentos.get(parDelTermino.getKey());        //SE OBTIENE EL ID DOC DE ESA ENTRADA. RECORDEMOS QUE CADA ENTRADA TIENE (IdDoc, Tf)


                int tfEntradaActual = parDelTermino.getValue();                 //SE OBTIENE EL TF DE LA ENTRADA ACTUAL
                if( tfEntradaActual > maxTf)
                {
                    maxTf = tfEntradaActual;                                    //SE BUSCA EL MAX TF DEL TERMINO SOLO DE ENTRE LOS DOCUMENTOS NUEVOS INSERTADOS
                }

                if (!tablaTerminos.containsKey(termino)){                   //SI EL TERMINO NO SE ENCUENTRA YA EN LA BASE


                    DBEntradasPosteoPK entradasPosteoPk = new DBEntradasPosteoPK(idDoc, idTerm);        //INSERTO EL TERMINO CON LOS DATOS OBTENIDOS
                    DBEntradasPosteo entradasPosteo = new DBEntradasPosteo(entradasPosteoPk, parDelTermino.getValue());
                    em.persist(entradasPosteo);

                }
                else{                           //SI EL TERMINO YA EXISTE
                    elTerminoExiste = true;     //PRENDO EL FLAG
                    DBTerminos2 terminoQueYaExiste = tablaTerminos.get(termino);    //OBTENDO EL TERMINO QUE YA EXISTE
                    int idActual = terminoQueYaExiste.getId();                      //USO EL ID QUE YA TENIA EL TERMINO

                    DBEntradasPosteoPK entradasPosteoPk = new DBEntradasPosteoPK(idDoc, idActual);      //CREO LA ENTRADA CONSERVANDO EL ID QUE YA POSEIA EL TERMINO
                    DBEntradasPosteo entradasPosteo = new DBEntradasPosteo(entradasPosteoPk, parDelTermino.getValue());
                    em.persist(entradasPosteo);
                }

                cont++;
                if (cont % 100000 == 0) {       //INSERSION EN LOTE
                    em.flush();                 //OBLIGO AL EM A EFECTIVAMENTE PERSISTIR Y VACIAR SU MEMORIA
                    em.clear();

                }

            }
            //LUEGO DE PERSISTIR TODAS LAS ENTRADAS DEL TERMINO, PROCEDO A INSERTAR O ACTUALIZAR EL TERMINO EN CUESTION
            if (!elTerminoExiste){          //SEGUN EL FLAG, SI EL TERMINO NO SE ENCONTRABA EN LA BASE
                DBTerminos2 nuevoTerminoAInsertar = new DBTerminos2(idTerm, termino, maxTf, nr);        //LO INSERTO
                em.persist(nuevoTerminoAInsertar);
                idTerm++;

            }
            else{                           //SI EL TERMINO YA SE ENCONTRABA EN LA BASE
                DBTerminos2 terminoQueYaExiste = tablaTerminos.get(termino);        //OBTENGO SUS DATOS
                int idActual = terminoQueYaExiste.getId();
                int maxTfActual = terminoQueYaExiste.getMaxTf();
                if( maxTf > maxTfActual)                //COMPARO MAX TF ENTRE LOS DOCS YA INSERTADOS Y LOS NUEVOS
                {
                    maxTfActual = maxTf;                //ESTABLEZCO EL MAX TF
                }
                int nrActual = terminoQueYaExiste.getNr();

                DBTerminos2 nuevoTerminoAInsertar = new DBTerminos2(idActual, termino, maxTfActual, nrActual+nr);
                em.merge(nuevoTerminoAInsertar);                //ACTUALIZO EL TERMINO CON SU NUEVO MAX TF SI FUESE EL CASO
                                                                //Y CON SU NUEVO NR, SUMANDO AL ANTERIOR EL NR PARA ESTE NUEVO CONJUNTO DE DOCS.

            }

        }
        exito = true;       //OK!
        return exito;
    }








































   // @Transactional
//    public void guardarDocumentos(ArrayList<String> lista, Hashtable<String, Integer> tablaDocumentos)
//    {
//        int c = 0;
//        for (String documento: lista)
//        {
//            DBDocumentos dbDocumento = new DBDocumentos(documento, c);
//            em.persist(dbDocumento);
//            tablaDocumentos.put(dbDocumento.getNombre(), dbDocumento.getIdDocumento());
//            c++;
//        }
//    }
//
//
//    @Transactional
//    public void save(Hashtable<String, Hashtable<String, Integer>> tablaPosteo, Hashtable<String, Integer> tablaDocumentos, ArrayList<String> lista) throws SQLException {
//        guardarDocumentos(lista, tablaDocumentos);
//
//        Set<Map.Entry<String, Hashtable<String, Integer>>> conjuntoDeParesEnTabla = tablaPosteo.entrySet();
//
//        int cont = 0;
//        int idTerm = -1;
//        for (Map.Entry<String, Hashtable<String, Integer>> par : conjuntoDeParesEnTabla) {
//
//            String termino = par.getKey();
//            idTerm++;
//            //DBTerminos dbTerminos = new DBTerminos(termino, idTerm);
//            //-em.persist(dbTerminos);
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
//                em.persist(entradasPosteo);
//                cont++;
//                if (cont % 100000 == 0) {
//                    em.flush();
//                    em.clear();
//
//
//                }
//            }
//            //tablaTerminos.put(termino, new DatosTermino(idTerm, maxTf, nr));
//            DBTerminos2 dbTerminos2 = new DBTerminos2(idTerm, termino, maxTf, nr);
//            em.persist(dbTerminos2);
//        }
//    }







}
