package entityJPA;

import javax.persistence.*;

@Entity
@Table(name = "terminos", schema = "dlc_tpu")
public class DBTerminos
{
    @Id
    @Column(name = "id")
    private int idTermino;
    @Basic
    @Column(name = "nombre")
    private String nombre;

    public int getIdTermino()
    {
        return idTermino;
    }

    public void setIdTermino(int idTermino)
    {
        this.idTermino = idTermino;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public DBTerminos(String nombre, int id)
    {
        this.nombre = nombre;
        this.idTermino = id;
    }

    public DBTerminos()
    {
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DBTerminos that = (DBTerminos) o;

        if (idTermino != that.idTermino) return false;
        if (nombre != null ? !nombre.equals(that.nombre) : that.nombre != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = idTermino;
        result = 31 * result + (nombre != null ? nombre.hashCode() : 0);
        return result;
    }
}
