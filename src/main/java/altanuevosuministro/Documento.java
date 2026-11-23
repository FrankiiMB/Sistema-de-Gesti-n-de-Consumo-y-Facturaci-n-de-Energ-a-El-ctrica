package altanuevosuministro;

public class Documento implements IValidable {

    private Enum tipo;
    private String estadoValidacion;
    private String rutaArchivo;

    public Documento(Enum tipo, String estadoValidacion, String rutaArchivo) {
        this.tipo = tipo;
        this.estadoValidacion = estadoValidacion;
        this.rutaArchivo = rutaArchivo;
    }

    @Override
    public boolean validar() {
        return "VALIDADO".equalsIgnoreCase(estadoValidacion) || "PRESENTADO".equalsIgnoreCase(estadoValidacion);
    }

    public void marcarComoObservado() {
        this.estadoValidacion = "OBSERVADO";
    }

    public Enum getTipo() { return tipo; }
    public String getEstadoValidacion() { return estadoValidacion; }
    public void setEstadoValidacion(String estadoValidacion) { this.estadoValidacion = estadoValidacion; }
    public String getRutaArchivo() { return rutaArchivo; }
}