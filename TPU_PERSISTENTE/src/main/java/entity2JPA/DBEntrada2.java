package entity2JPA;

import entityJPA.DBEntradasPosteoPK;

import javax.persistence.*;

@Entity
@Table(name = "entradasposteo", schema = "dlc_tpu")
public class DBEntrada2
{

    @EmbeddedId
    private DBEntrada2PK clave;
    @Basic
    @Column(name = "tf")
    private int tf;


    public int getTf()
    {
        return tf;
    }

    public void setTf(int tf)
    {
        this.tf = tf;
    }

    public DBEntrada2()
    {
    }

    public DBEntrada2(DBEntrada2PK clave, int tf)
    {
        this.clave = clave;
        this.tf = tf;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DBEntrada2 that = (DBEntrada2) o;

        if (clave != null ? !clave.equals(that.clave) : that.clave != null) return false;
        if (tf != that.tf) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = clave != null ? clave.hashCode() : 0;
        result = 31 * result + tf;
        return result;
    }
}
