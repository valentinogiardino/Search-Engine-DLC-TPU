package com.dlc.searchEngine.models.entities;

import javax.persistence.*;
import javax.persistence.Entity;

@Entity
@Table(name = "terminos2", schema = "dlc_tpu")
public class DBTerminos2 implements Comparable<DBTerminos2>
{
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "nombre")
    private String nombre;
    @Basic
    @Column(name = "max_tf")
    private Integer maxTf;
    @Basic
    @Column(name = "nr")
    private Integer nr;


    public DBTerminos2(int id, String nombre, Integer maxTf, Integer nr) {
        this.id = id;
        this.nombre = nombre;
        this.maxTf = maxTf;
        this.nr = nr;
    }

    public DBTerminos2(){}

    public DBTerminos2(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public DBTerminos2(int id, String nombre, Integer maxTf) {
        this.id = id;
        this.nombre = nombre;
        this.maxTf = maxTf;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getMaxTf() {
        return maxTf;
    }

    public void setMaxTf(Integer maxTf) {
        this.maxTf = maxTf;
    }

    public Integer getNr() {
        return nr;
    }

    public void setNr(Integer nr) {
        this.nr = nr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DBTerminos2 that = (DBTerminos2) o;

        if (id != that.id) return false;
        if (nombre != null ? !nombre.equals(that.nombre) : that.nombre != null) return false;
        if (maxTf != null ? !maxTf.equals(that.maxTf) : that.maxTf != null) return false;
        if (nr != null ? !nr.equals(that.nr) : that.nr != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (nombre != null ? nombre.hashCode() : 0);
        result = 31 * result + (maxTf != null ? maxTf.hashCode() : 0);
        result = 31 * result + (nr != null ? nr.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(DBTerminos2 o)
    {
        int resultado=0;
        if (this.getNr()<o.getNr()) {   resultado = -1;      }
        else if (this.getNr()>o.getNr()) {    resultado = 1;      }
        return resultado;
    }

    @Override
    public String toString() {
        return "DBTerminos2{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", maxTf=" + maxTf +
                ", nr=" + nr +
                '}';
    }
}
