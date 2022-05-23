package utn.tpu_persistance.Dominio;

import entity2JPA.DBEntrada2;
import entity2JPA.DBEntrada2PK;
import entity2JPA.DBTerminos2;
import entityJPA.DBDocumentos;
import entityJPA.DBEntradasPosteo;
import entityJPA.DBEntradasPosteoPK;
import entityJPA.DBTerminos;
import org.hibernate.cfg.Configuration;
//import org.hibernate.cfg.Configuration;

import javax.persistence.*;
import javax.swing.text.html.parser.Entity;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;


/**
 * Clase para representar un Gestor de tablas posteo.
 * Se realiza en el marco de la implementación de un Motor de Búsqueda siguiendo el modelo Vectorial.
 * Se incorporan métodos que permiten leer archivos desde un path, crear y poblar la tabla de posteo, mostrarla
 * y guardarla en una Base de Datos Relacional.
 * La tabla de posteo se implementa mediante una HashTable de la siguiente forma:
 *  -Como Key un String término.
 *  -Como Value un TreeSet de objetos EntradaPosteo(String documento, int tf).
 * De esta forma tendremos asociado cada término con sus Entradas de posteo, garantizando ademas el orden de
 * las mismas en orden descendente de tf.
 * @author Valentino Giardino.
 * @version Abril de 2022.
 */
public class Gestor2
{
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //ATRIBUTOS ESENCIALES
    private Hashtable<String, Hashtable<String, Integer>> tablaPosteo;//HASHTABLE PARA CONTENER LA TABLA DE POSTEO
    private String path;                                          //DIRECCION DE LA CARPETA DE LOS DOCUMENTOS
    private String documento;                                     //GUARDA EL NOMBRE DEL DOCUMENTO QUE SE ESTA PROCESANDO
    private ArrayList<String> lista= new ArrayList();
    private Hashtable<String, Integer> tablaDocumentos = new Hashtable<>();
    private Hashtable<String, DatosTermino> tablaTerminos = new Hashtable<>();

    public Hashtable<String, Hashtable<String, Integer>> getTablaPosteo()
    {
        return tablaPosteo;
    }

    public void setTablaPosteo(Hashtable<String, Hashtable<String, Integer>> tablaPosteo)
    {
        this.tablaPosteo = tablaPosteo;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getDocumento()
    {
        return documento;
    }

    public void setDocumento(String documento)
    {
        this.documento = documento;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Constructor.
     * Crea la tabla de posteo como una HashTable vacia, inicializada en 11.
     * @param path la dirección de la carpeta donde se encuentran los documentos a procesar.
     */
    public Gestor2(String path)
    {
        this.tablaPosteo = new Hashtable<>(11);
        this.path = path;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Lee cada archivo contenidos en el path.
     * Al momento de leer un archivo se setea en el atributo de la entidad su nombre, simplemente por comodidad.
     * Por cada archivo se invoca a un método para efectuar su procesamiento.
     */
    public void indexarDocumentos()
    {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(path))) {
            for (Path file: stream) {
                this.documento = file.getFileName().toString();

                this.lista.add(documento);
                procesarDocumento();
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
    /**
     * Aumenta la tf de una entrada de posteo.
     * Debido a que el conjunto de entradas está implementado mediante un TreeSet, para mantener el orden no se puede
     * modificar directamente el atributo de la entrada, sino que:
     *  -Se crea una nueva entrada con los datos actualizados
     *  -Se elimina la entrada original
     *  -Se agrega la entrada actualizada
     * @param conjuntoEntradasPosteo el TreeSet que contiene la entrada a actualizar
     * @param entradaPosteo la entrada de posteo cuyo tf se necesita aumentar
     */
    private void aumentarTf(TreeSet<EntradaPosteo> conjuntoEntradasPosteo, EntradaPosteo entradaPosteo)
    {
        int tfActual = entradaPosteo.getTf();                    //SE GUARDA REFERENCIA A LA TF ACTUAL
        conjuntoEntradasPosteo.remove(entradaPosteo);            //SE REMUEVE LA ENTRADA ORIGINAL

        EntradaPosteo nuevaEntradaPosteo = new EntradaPosteo(documento, tfActual+1); //SE CREA UNA NUEVA TF QUE
        // INCREMENTA LA TF ORIGINAL

        conjuntoEntradasPosteo.add(nuevaEntradaPosteo);          //SE AGREGA LA ENTRADA ACTUALIZADA

        //entradaPosteo.setTf(entradaPosteo.getTf() + 1);        //EJEMPLO DE COMO NO HACERLO
    }
    /**
     * Crea una nueva entrada y la agrega al conjunto de entradas de posteo del término
     * @param conjuntoEntradasPosteo el Treeset que contiene todas las entradas de posteo del término
     */
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

    public void save6() throws SQLException {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("Persist");
        EntityManager em = entityManagerFactory.createEntityManager();
        guardarDocumentos(em);
        EntityTransaction transaction = em.getTransaction();


        Set<Map.Entry<String, Hashtable<String, Integer>>> entities = tablaPosteo.entrySet();

        try {
            int cont = 0;
            int idTerm = -1;
            transaction.begin();
            for (Map.Entry<String, Hashtable<String, Integer>> entity : entities) {

                String termino = entity.getKey();
                idTerm++;
                DBTerminos dbTerminos = new DBTerminos(termino, idTerm);
                em.persist(dbTerminos);

                Hashtable<String, Integer> conjuntoEntradasDelPar = entity.getValue();
                Set<Map.Entry<String, Integer>> entities2 = conjuntoEntradasDelPar.entrySet();
                for (Map.Entry<String, Integer> entity2 : entities2)   //POR CADA PAR DE LA TABLA
                {
                    int idDoc = tablaDocumentos.get(entity2.getKey());
                    DBEntradasPosteoPK entradasPosteoPk = new DBEntradasPosteoPK(idDoc, idTerm);
                    DBEntradasPosteo entradasPosteo = new DBEntradasPosteo(entradasPosteoPk, entity2.getValue());
                    em.persist(entradasPosteo);
                    cont++;
                    if (cont % 1000 == 0)
                    {
                        em.flush();
                        em.clear();
                        transaction.commit();
                        transaction.begin();

                    }
                }
            }
            transaction.commit();

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            em.close();
            entityManagerFactory.close();

        }

    }
    public void guardarDocumentos(EntityManager em)
    {
        int c = 0;
        for (String documento: this.lista)
        {
            DBDocumentos dbDocumento = new DBDocumentos(documento, c);
            em.persist(dbDocumento);
            this.tablaDocumentos.put(dbDocumento.getNombre(), dbDocumento.getIdDocumento());
            c++;
        }
    }



    public void save7() throws SQLException {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("Persist");
        EntityManager em = entityManagerFactory.createEntityManager();
        guardarDocumentos(em);
        EntityTransaction transaction = em.getTransaction();


        Set<Map.Entry<String, Hashtable<String, Integer>>> conjuntoDeParesEnTabla = tablaPosteo.entrySet();

        try {
            int cont = 0;
            int idTerm = -1;
            transaction.begin();
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
                    if( tfEntradaActual > maxTf)
                    {
                        maxTf = tfEntradaActual;
                    }
                    int idDoc = tablaDocumentos.get(parDelTermino.getKey());
                    DBEntradasPosteoPK entradasPosteoPk = new DBEntradasPosteoPK(idDoc, idTerm);
                    DBEntradasPosteo entradasPosteo = new DBEntradasPosteo(entradasPosteoPk, parDelTermino.getValue());
                    em.persist(entradasPosteo);
                    cont++;
                    if (cont % 5000 == 0) {
                        em.flush();
                        em.clear();
                        transaction.commit();
                        transaction.begin();

                    }
                }
                //tablaTerminos.put(termino, new DatosTermino(idTerm, maxTf, nr));
                DBTerminos2 dbTerminos2 = new DBTerminos2(idTerm, termino, maxTf, nr);
                em.persist(dbTerminos2);
            }
            transaction.commit();

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            em.close();
            entityManagerFactory.close();

        }
    }

    public void obtenerVocabulario()
    {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("Persist");
        EntityManager em = entityManagerFactory.createEntityManager();
        Query query = em.createQuery( "Select t from DBTerminos2 t");
        //Query query = em.createNativeQuery( "Select * from terminos2");
        List<DBTerminos2> listaVoc = query.getResultList();
        for (DBTerminos2 termino:listaVoc)
        {
         tablaTerminos.put(termino.getNombre(), new DatosTermino(termino.getId(), termino.getMaxTf(), termino.getNr()));
        }

    }
    public void mostrarVocabulario()
    {
        Set<Map.Entry<String, DatosTermino>> pares = tablaTerminos.entrySet();

        for (Map.Entry<String, DatosTermino> par : pares)   //POR CADA PAR DE LA TABLA
        {
            System.out.println(par.getKey() + "->" + par.getValue());
        }
    }


    public void procesarConsulta(String consulta, int r)
    {
        ArrayList<DBTerminos2> listaTerminosEnConsulta = new ArrayList<>();
        Scanner scanner = new Scanner(consulta);
        scanner.useDelimiter("[^\\w]+");

        while (scanner.hasNext())
        {
            String nuevoTermino = scanner.next();
            if (tablaTerminos.containsKey(nuevoTermino))
            {
                DatosTermino datosTermino = tablaTerminos.get(nuevoTermino);
                DBTerminos2 dbTerminos2 = new DBTerminos2(datosTermino.getId(), nuevoTermino, datosTermino.getMaxTf(), datosTermino.getNr());
                listaTerminosEnConsulta.add(dbTerminos2);
            }
        }
        Collections.sort(listaTerminosEnConsulta);
        Hashtable<Integer, Double> tablaResulados = new Hashtable<>();
        for (DBTerminos2 dbtermino2:listaTerminosEnConsulta)
        {
            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("Persist");
            EntityManager em = entityManagerFactory.createEntityManager();
            Query query = em.createQuery( "Select e from DBEntradasPosteo e where e.clave.termino = :idTermino order by e.tf DESC");
            query.setParameter("idTermino", dbtermino2.getId());
            List<DBEntradasPosteo> listaEntradasDelTermino = query.getResultList();
            int cont = 0;
            for (DBEntradasPosteo entradaDelTermino:listaEntradasDelTermino)
            {
                int idDoc = entradaDelTermino.getClave().getDocumento();

                if (!tablaResulados.containsKey(idDoc))
                {
                    tablaResulados.put(idDoc, 0.0);
                }
                double irActual = tablaResulados.get(idDoc);
                double irNuevo = irActual + (entradaDelTermino.getTf() * Math.log(593/listaEntradasDelTermino.size()));
                tablaResulados.replace(idDoc, irNuevo);
                cont++;
                //if (cont == r) {break;}
            }
        }
        mostrarResultados(tablaResulados);
    }

    private void mostrarResultados(Hashtable<Integer, Double> tablaResultados)
    {
        Set<Map.Entry<Integer, Double>> pares = tablaResultados.entrySet();

        for (Map.Entry<Integer, Double> par : pares)   //POR CADA PAR DE LA TABLA
        {
            System.out.println(par.getKey() + "->" + par.getValue());
        }
    }
















    public void save8() throws SQLException {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("Persist");
        EntityManager em = entityManagerFactory.createEntityManager();
        guardarDocumentos(em);
        EntityTransaction transaction = em.getTransaction();


        Set<Map.Entry<String, Hashtable<String, Integer>>> entities = tablaPosteo.entrySet();

        try {
            int cont = 0;
            int idTerm = -1;
            transaction.begin();
            for (Map.Entry<String, Hashtable<String, Integer>> entity : entities) {

                String termino = entity.getKey();
                idTerm++;
                DBTerminos dbTerminos = new DBTerminos(termino, idTerm);
                em.persist(dbTerminos);

                Hashtable<String, Integer> conjuntoEntradasDelPar = entity.getValue();
                int maxTf = 0;
                int nr = conjuntoEntradasDelPar.size();
                Set<Map.Entry<String, Integer>> entities2 = conjuntoEntradasDelPar.entrySet();
                for (Map.Entry<String, Integer> entity2 : entities2)   //POR CADA PAR DE LA TABLA
                {
                    int tfEntradaActual = entity2.getValue();
                    if( tfEntradaActual > maxTf)
                    {
                        maxTf = tfEntradaActual;
                    }
                    int idDoc = tablaDocumentos.get(entity2.getKey());
                    DBEntradasPosteoPK entradasPosteoPk = new DBEntradasPosteoPK(idDoc, idTerm);
                    DBEntradasPosteo entradasPosteo = new DBEntradasPosteo(entradasPosteoPk, entity2.getValue());
                    em.persist(entradasPosteo);
                    cont++;
                    if (cont % 5000 == 0) {
                        em.flush();
                        em.clear();
                        transaction.commit();
                        transaction.begin();

                    }
                }
                tablaTerminos.put(termino, new DatosTermino(idTerm, maxTf, nr));
                DBTerminos2 dbTerminos2 = new DBTerminos2(idTerm, termino, maxTf, nr);
                em.persist(dbTerminos2);
            }
            transaction.commit();

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            em.close();
            entityManagerFactory.close();

        }
    }

//    public void save(Set<Map.Entry<String, Integer>> entities, String documento) throws SQLException
//    {
//        try (
//                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dlc_tpu", "usu1", "dlc_tpu2022");
//                PreparedStatement statement = connection.prepareStatement("insert into terminos(termino, documento, tf) values (?,?,?) ");
//        ) {
//            int i = 0;
//            for (Map.Entry<String, Integer> entity : entities) {
//                statement.setString(1, entity.getKey());
//                statement.setString(2, documento);
//                statement.setString(3, entity.getValue().toString());
//
//                statement.addBatch();
//                i++;
//
//                if (i % 1000 == 0 || i == entities.size()) {
//                    statement.executeBatch(); // Se ejecuta en lotes de 1000
//                }
//            }
//        }
//    }
//
//
//
//
//
//
//    public void save5() throws SQLException {
//        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("Persist");
//        EntityManager em = entityManagerFactory.createEntityManager();
//        EntityTransaction transaction = em.getTransaction();
//
//
//        Set<Map.Entry<String, TreeSet<EntradaPosteo>>> entities = tablaPosteo.entrySet();
//
//        try {
//            int cont = 0;
//            transaction.begin();
//            for (Map.Entry<String, TreeSet<EntradaPosteo>> entity : entities) {
//                TreeSet<EntradaPosteo> conjuntoEntradasDelPar = entity.getValue();
//                Iterator<EntradaPosteo> iterator = conjuntoEntradasDelPar.iterator();
//                String termino = entity.getKey();
//
//                while (iterator.hasNext()) {
//                    EntradaPosteo entrada = iterator.next();
//
//
//                    DBEntrada2PK entrada2Pk = new DBEntrada2PK(entrada.getDocumento(), termino);
//                    DBEntrada2 entradaDB2 = new DBEntrada2(entrada2Pk, entrada.getTf());
//                    em.persist(entradaDB2);
//                    cont++;
//                    if (cont % 1000 == 0) {
//                        em.flush();
//                        em.clear();
//                        transaction.commit();
//                        transaction.begin();
//                    }
//                }
//
//            }
//            transaction.commit();
//
//        } finally {
//            if (transaction.isActive()) {
//                transaction.rollback();
//            }
//            em.close();
//            entityManagerFactory.close();
//
//        }
//
//    }
//
//
//
//
//    public void guardarDocumentos(EntityManager em)
//    {
//        //EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("Persist");
//        //EntityManager em = entityManagerFactory.createEntityManager();
//        int c = 0;
//        for (String documento: this.lista)
//        {
//            DBDocumentos dbDocumento = new DBDocumentos(documento, c);
//            em.persist(dbDocumento);
//            this.tablaDocumentos.put(dbDocumento.getNombre(), dbDocumento.getIdDocumento());
//            c++;
//        }
//
//
//
//    }
//
//    public void save6(int batch) throws SQLException {
//        Configuration config = new Configuration();
//        config.configure("src/main/resources/META-INF/persistence.xml");
//        config.setProperty("hibernate.jdbc.batch_size", String.valueOf(batch));
//        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("Persist");
//        EntityManager em = entityManagerFactory.createEntityManager();
//        em.createQuery("DELETE FROM DBDocumentos").executeUpdate();
//        em.createQuery("DELETE FROM DBTerminos ").executeUpdate();
//        em.createQuery("DELETE FROM DBEntradasPosteo ").executeUpdate();
//        guardarDocumentos(em);
//        EntityTransaction transaction = em.getTransaction();
//
//
//        Set<Map.Entry<String, TreeSet<EntradaPosteo>>> entities = tablaPosteo.entrySet();
//
//        try {
//            int cont = 0;
//            int idTerm = -1;
//            transaction.begin();
//            for (Map.Entry<String, TreeSet<EntradaPosteo>> entity : entities) {
//                TreeSet<EntradaPosteo> conjuntoEntradasDelPar = entity.getValue();
//                Iterator<EntradaPosteo> iterator = conjuntoEntradasDelPar.iterator();
//                String termino = entity.getKey();
//                idTerm++;
//                DBTerminos dbTerminos = new DBTerminos(termino, idTerm);
//                em.persist(dbTerminos);
//                while (iterator.hasNext()) {
//                    EntradaPosteo entrada = iterator.next();
//
//                    int idDoc = tablaDocumentos.get(entrada.getDocumento());
//
//
//                    DBEntradasPosteoPK entradasPosteoPk = new DBEntradasPosteoPK(idDoc, idTerm);
//
//                    DBEntradasPosteo entradasPosteo = new DBEntradasPosteo(entradasPosteoPk, entrada.getTf());
//                    em.persist(entradasPosteo);
//                    cont++;
//                    if (cont % batch == 0) {
//                        em.flush();
//                        em.clear();
//                        transaction.commit();
//                        transaction.begin();
//                    }
//                }
//
//
//            }
//            transaction.commit();
//
//        } finally {
//            if (transaction.isActive()) {
//                transaction.rollback();
//            }
//            em.close();
//            entityManagerFactory.close();
//
//        }
//
//    }
}

