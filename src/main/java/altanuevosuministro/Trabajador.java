package altanuevosuministro;

public class Trabajador extends Persona {

    private String codigoTrabajador;
    private String cargo;
    private String clave = "123"; // Clave por defecto para pruebas

    public Trabajador(String codigoTrabajador, String cargo) {
        super();
        this.codigoTrabajador = codigoTrabajador;
        this.cargo = cargo;
    }

    public Trabajador(String codigoTrabajador, String cargo, String nombres, String apellidos, String dni) {
        super(nombres, apellidos, TipoDocumento.DNI, dni, "", "", TipoPersona.NATURAL, "");
        this.codigoTrabajador = codigoTrabajador;
        this.cargo = cargo;
    }

    public boolean verificarCredenciales() {
        return codigoTrabajador != null && !codigoTrabajador.isBlank();
    }

    public boolean verificarCredenciales(String usuarioIngresado, String claveIngresada) {
        return codigoTrabajador.equalsIgnoreCase(usuarioIngresado) && clave.equals(claveIngresada);
    }

    public String getCodigoTrabajador() { return codigoTrabajador; }
    public void setCodigoTrabajador(String codigoTrabajador) { this.codigoTrabajador = codigoTrabajador; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    @Override
    public String toString() {
        return "Trabajador  ·  " + cargo;
    }
}