package utn.tpu_persistance.Dominio;

public class ResultadoConsulta {
    private String nombreDoc;
    private int ir;

    public ResultadoConsulta(String nombreDoc, int ir) {
        this.nombreDoc = nombreDoc;
        this.ir = ir;
    }

    public String getNombreDoc() {
        return nombreDoc;
    }

    public void setNombreDoc(String nombreDoc) {
        this.nombreDoc = nombreDoc;
    }

    public int getIr() {
        return ir;
    }

    public void setIr(int ir) {
        this.ir = ir;
    }
}
