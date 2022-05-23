package utn.tpu_persistance.Dominio;

import java.util.Comparator;

/**
 * Una clase que representa una entrada de posteo de un término.
 * Se realiza en el marco de la implementación de un Motor de Búsqueda siguiendo el modelo Vectorial.
 * Debido a que sus instancias compondran un TreeSet del conjunto de entradas de posteo para un termino,
 * se tuvo en cuenta la implementacion de las interfaces Comparable y Comparator.
 * @author Valentino Giardino
 * @version Abril de 2022.
 */
public class EntradaPosteo implements Comparable<EntradaPosteo>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //ATRIBUTOS ESENCIALES
    private String documento;           //NOMBRE DEL DOCUMENTO
    private int tf;                     //FRECUENCIA DE APARICION DEL TÉRMINO EN EL DOCUMENTO

    public String getDocumento()
    {
        return documento;
    }

    public void setDocumento(String documento)
    {
        this.documento = documento;
    }

    public int getTf()
    {
        return tf;
    }

    public void setTf(int tf)
    {
        this.tf = tf;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Constructor.
     * @param documento nombre del documento en el que apareció el término.
     * @param tf frecuencia de aparicion del término en el documento.
     */
    public EntradaPosteo(String documento, int tf)
    {
        this.documento = documento;
        this.tf = tf;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Establece una relación de orden entre dos objetos entradaPosteo. La comparación
     * realizada por compareTo() establece lo que se considera el "orden
     * natural" entre dos objetos de la clase EntradaPosteo.
     * @param o el objeto que será comparado contra el objeto actual.
     * @return un valor positivo si el objeto actual es mayor que o, o un valor
     *         negativo si es menor, el valor 0 si ambos objetos se consideran
     *         iguales.
     */
    @Override
    public int compareTo(EntradaPosteo o)
    {
        int resultado=0;
        if (this.tf<o.getTf()) {   resultado = -1;      }
        else if (this.tf>o.getTf()) {    resultado = 1;      }
        return resultado;
    }
    /**
     * @return un Comparator para los objetos de esta clase. El Comparator que
     * se retorna establece una relación de orden descendente basada en la comparación en primera instancia de
     * las tf de las entradasPosteo, y en segunda instancia entre los nombres de los documentos(en forma lexicográfica).
     */
    public static Comparator<EntradaPosteo> descComparator()
    {
        return new DescendentComparator();
    }

    /*
     * Clase interna para gestionar un Comparator que pueda comparar dos objetos EntradaPosteo pero
     * no como lo hace compareTo() (que compara solo los tf) sino comparando los tf y los documentos.
     *
     * Se hace de esta manera ya que los objetos de esta clase estaran contenidos
     * en un TreeSet, siendo una característica de esta estructura la no admision de elementos repetidos.
     * Es lógico pensar que un conjunto de entradas de posteo para un término, podrá tener varias entradas con el mismo
     * tf. Si queremos conservar todos estos, es implorioso comparar tambien los nombres de los documentos.
     *
     * La clase además de interna, es static para poder gestionarla desde un método estático.
     */
    private static class DescendentComparator implements Comparator<EntradaPosteo>
    {
        /**
         * Establece una relación de orden entre dos objetos Student. La
         * comparación realizada por compare() NO establece el "orden natural"
         * entre dos objetos de la clase Student, sino una estrategia basada en
         * otro criterio.
         * @param entrada1 el primer objeto que será comparado.
         * @param entrada2 el segundo objeto que será comparado.
         * @return un valor positivo si el tf de entrada2 es mayor que el de entrada1, o
         *         un valor negativo si el tf de entrada2 es menor que el de entrada1, o
         *         un valor positivo si el nombre del documento de entrada2 es mayor que el de entrada1, o
         *         un valor negativo si el nombre del documento de entrada2 es menor que el de entrada1, o
         *         el valor 0 si ambos objetos tienen el mismo valor de tf y de documento.
         */
        public int compare(EntradaPosteo entrada1, EntradaPosteo entrada2)
        {
            if(entrada1.getTf()!=entrada2.getTf())      //SI LAS ENTRADAS TIENEN DISTINTO TF COMPARA LOS MISMOS
            {
                return entrada2.getTf()-entrada1.getTf(); //CON ESTO TENDREMOS UN ORDEN DESCENDENTE
            }
                                                        //SI LOS TF SON IGUALES COMPARA LOS NOMBRES DE LOS DOCUMENTOS
            return entrada2.documento.compareTo(entrada1.documento);
        }
    }
    /**
     * Redefinición del método heredado desde Object
     * @return el contenido del grafo en forma de String, incluyendo la matriz de cierre transitivo
     */
    @Override
    public String toString()
    {
        return "Documento: "+ this.documento + ", " + "tf: " + this.tf;
    }
}
