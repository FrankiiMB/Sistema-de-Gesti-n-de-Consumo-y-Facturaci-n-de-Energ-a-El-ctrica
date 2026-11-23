package altanuevosuministro;

import java.util.ArrayList;
import java.util.Collection;

public class Predio implements IValidable {

    private String codigoPredio;
    private String direccion;
    private boolean tieneDeudaPendiente;
    private boolean tieneSuministroActivo;
    private boolean esFactible;
    private Collection<Suministro> suministro = new ArrayList<>();

    public Predio(String codigoPredio, String direccion, boolean tieneDeudaPendiente, boolean tieneSuministroActivo) {
        this.codigoPredio = codigoPredio;
        this.direccion = direccion;
        this.tieneDeudaPendiente = tieneDeudaPendiente;
        this.tieneSuministroActivo = tieneSuministroActivo;
        this.esFactible = true;
    }

    // Regla RN-04: Libre de deuda y sin suministro activo
    @Override
    public boolean validar() {
        return !tieneDeudaPendiente && !tieneSuministroActivo;
    }

    public String getCodigoPredio() { return codigoPredio; }
    public String getDireccion() { return direccion; }
    public boolean isTieneDeudaPendiente() { return tieneDeudaPendiente; }
    public void setTieneDeudaPendiente(boolean tieneDeudaPendiente) { this.tieneDeudaPendiente = tieneDeudaPendiente; }
    public boolean isTieneSuministroActivo() { return tieneSuministroActivo; }
    public boolean isEsFactible() { return esFactible; }
    public void setEsFactible(boolean esFactible) { this.esFactible = esFactible; }
    public Collection<Suministro> getSuministro() { return suministro; }
}