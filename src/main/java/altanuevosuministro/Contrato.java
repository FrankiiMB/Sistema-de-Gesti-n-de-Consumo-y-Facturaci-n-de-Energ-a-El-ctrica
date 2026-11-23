package altanuevosuministro;

import java.util.Date;

public class Contrato {

    private String idContrato;
    private Date fechaInicio;
    private Date fechaTermino;
    private String opcionTarifaria;
    private double potenciaContratada;

    public Contrato(String idContrato, String opcionTarifaria, double potenciaContratada) {
        this.idContrato = idContrato;
        this.fechaInicio = new Date();
        this.opcionTarifaria = opcionTarifaria;
        this.potenciaContratada = potenciaContratada;
    }

    public boolean validarPotenciaTarifa() {
        return opcionTarifaria != null && !opcionTarifaria.isBlank() && potenciaContratada > 0;
    }

    public String getIdContrato() { return idContrato; }
    public String getOpcionTarifaria() { return opcionTarifaria; }
    public double getPotenciaContratada() { return potenciaContratada; }
}