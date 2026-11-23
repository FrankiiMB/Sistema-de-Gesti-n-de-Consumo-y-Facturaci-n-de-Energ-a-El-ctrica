package altanuevosuministro;

public class Medidor {

    private String numeroMedidor;
    private String marca;
    private double lecturaInicial;
    private String estado;
    private double factorMedidor;
    private double lecturaAnterior;
    private double lecturaActual;

    public Medidor(String numeroMedidor, String marca, double factorMedidor) {
        this.numeroMedidor = numeroMedidor;
        this.marca = marca;
        this.factorMedidor = factorMedidor;
        this.estado = "ACTIVO";
    }

    public void registrarLecturaInicial(double lectura) {
        this.lecturaInicial = lectura;
        this.lecturaAnterior = lectura;
        this.lecturaActual = lectura;
    }

    public void actualizarLectura(double valor) {
        this.lecturaAnterior = this.lecturaActual;
        this.lecturaActual = valor;
    }

    public String getNumeroMedidor() { return numeroMedidor; }
    public double getLecturaInicial() { return lecturaInicial; }
}