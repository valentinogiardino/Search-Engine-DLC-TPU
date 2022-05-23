package utn.tpu_persistance.Dominio;

public class DatosTermino {

    private int id;
    private int maxTf;
    private int nr;

    public DatosTermino(int id, int maxTf, int nr) {
        this.id = id;
        this.maxTf = maxTf;
        this.nr = nr;
    }


    public DatosTermino(int id, int maxTf) {
        this.id = id;
        this.maxTf = maxTf;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMaxTf() {
        return maxTf;
    }

    public void setMaxTf(int maxTf) {
        this.maxTf = maxTf;
    }

    public int getNr() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr = nr;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", maxTf=" + maxTf +
                ", nr=" + nr +
                '}';
    }
}
