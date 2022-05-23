package entityJPA;

import javax.persistence.*;

@Entity
@Table(name = "documentos", schema = "dlc_tpu")
public class DBDocumentos
{
    @Id
    @Column(name = "id")
    private int idDocumento;
    @Basic
    @Column(name = "nombre")
    private String nombre;

    public int getIdDocumento()
    {
        return idDocumento;
    }

    public void setIdDocumento(int idDocumento)
    {
        this.idDocumento = idDocumento;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public DBDocumentos(String nombre, int id)
    {
        this.nombre = nombre;
        this.idDocumento = id;
    }

    public DBDocumentos()
    {
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DBDocumentos that = (DBDocumentos) o;

        if (idDocumento != that.idDocumento) return false;
        if (nombre != null ? !nombre.equals(that.nombre) : that.nombre != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = idDocumento;
        result = 31 * result + (nombre != null ? nombre.hashCode() : 0);
        return result;
    }
}
