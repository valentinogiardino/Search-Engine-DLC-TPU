package entityJPA;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class DBEntradasPosteoPK implements Serializable
{
    private static final long serilaVersionUID = 1L;
    @Column(name = "idDoc")
    private int documento;
    @Column(name = "idTermino")
    private int termino;

    public int getDocumento()
    {
        return documento;
    }

    public void setDocumento(int documento)
    {
        this.documento = documento;
    }

    public int getTermino()
    {
        return termino;
    }

    public void setTermino(int termino)
    {
        this.termino = termino;
    }

    public DBEntradasPosteoPK(int documento, int termino)
    {
        this.documento = documento;
        this.termino = termino;
    }

    public DBEntradasPosteoPK()
    {
    }
    

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DBEntradasPosteoPK that = (DBEntradasPosteoPK) o;

        if (documento != that.documento) return false;
        if (termino != that.termino) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = documento;
        result = 31 * result + termino;
        return result;
    }


}
