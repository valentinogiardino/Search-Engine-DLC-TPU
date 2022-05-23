package entityJPA;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "entradas", schema = "dlc_tpu")
public class DBEntradasPosteo
{
    @EmbeddedId
    private DBEntradasPosteoPK clave;
    @Basic
    @Column(name = "tf")
    private int tf;

    public DBEntradasPosteoPK getClave()
    {
        return clave;
    }

    public void setClave(DBEntradasPosteoPK clave)
    {
        this.clave = clave;
    }

    public int getTf()
    {
        return tf;
    }

    public void setTf(int tf)
    {
        this.tf = tf;
    }

    public DBEntradasPosteo(DBEntradasPosteoPK clave, int tf)
    {
        this.clave = clave;
        this.tf = tf;
    }

    public DBEntradasPosteo()
    {
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DBEntradasPosteo that = (DBEntradasPosteo) o;

        if (clave != that.clave) return false;
        if (tf != that.tf) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = clave.hashCode();
        result = 31 * result + tf;
        return result;
    }

}
