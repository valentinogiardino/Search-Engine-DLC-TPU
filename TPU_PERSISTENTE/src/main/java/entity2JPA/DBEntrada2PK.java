package entity2JPA;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class DBEntrada2PK implements Serializable
{
    private static final long serilaVersionUID = 1L;

    private String documento;

    private String termino;

    public String getDocumento()
    {
        return documento;
    }

    public void setDocumento(String documento)
    {
        this.documento = documento;
    }

    public String getTermino()
    {
        return termino;
    }

    public void setTermino(String termino)
    {
        this.termino = termino;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DBEntrada2PK that = (DBEntrada2PK) o;

        if (documento != null ? !documento.equals(that.documento) : that.documento != null) return false;
        if (termino != null ? !termino.equals(that.termino) : that.termino != null) return false;

        return true;
    }

    public DBEntrada2PK(String documento, String termino)
    {
        this.documento = documento;
        this.termino = termino;
    }

    public DBEntrada2PK()
    {
    }

    @Override
    public int hashCode()
    {
        int result = documento != null ? documento.hashCode() : 0;
        result = 31 * result + (termino != null ? termino.hashCode() : 0);
        return result;
    }
}
