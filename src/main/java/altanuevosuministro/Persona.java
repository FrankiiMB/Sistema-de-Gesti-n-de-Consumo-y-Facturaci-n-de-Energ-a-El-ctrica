package altanuevosuministro;

public abstract class Persona {

    private String nombres;
    private String apellidos;
    private TipoDocumento tipoDocumento;
    private String numeroDocumentoIdentidad;
    private String direccion;
    private String telefono;
    private TipoPersona tipoPersona;
    private String correo;

    public Persona() {
    }

    public Persona(String nombres, String apellidos, TipoDocumento tipoDocumento,
                   String numeroDocumentoIdentidad, String direccion, String telefono,
                   TipoPersona tipoPersona, String correo) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumentoIdentidad = numeroDocumentoIdentidad;
        this.direccion = direccion;
        this.telefono = telefono;
        this.tipoPersona = tipoPersona;
        this.correo = correo;
    }

    public void actualizarDatosContacto(String telefono, String correo) {
        this.telefono = telefono;
        this.correo = correo;
    }

    public String mostrarDatos() {
        return getNombreCompleto() + " (" + tipoDocumento + ": " + numeroDocumentoIdentidad + ")";
    }

    public boolean validarDocumento() {
        if (numeroDocumentoIdentidad == null || numeroDocumentoIdentidad.isBlank()) {
            return false;
        }
        if (tipoDocumento == TipoDocumento.DNI) {
            return numeroDocumentoIdentidad.matches("\\d{8}");
        }
        if (tipoDocumento == TipoDocumento.RUC) {
            return numeroDocumentoIdentidad.matches("\\d{11}");
        }
        return numeroDocumentoIdentidad.length() >= 8;
    }

    public String getNombreCompleto() {
        return (nombres != null ? nombres : "") + " " + (apellidos != null ? apellidos : "");
    }

    // Getters y Setters
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getNumeroDocumentoIdentidad() { return numeroDocumentoIdentidad; }
    public void setNumeroDocumentoIdentidad(String num) { this.numeroDocumentoIdentidad = num; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public TipoPersona getTipoPersona() { return tipoPersona; }
    public void setTipoPersona(TipoPersona tipoPersona) { this.tipoPersona = tipoPersona; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
}