package altanuevosuministro;

import java.time.LocalDate;
import lecturafacturacionyrepartoderecibos.Recibo;
import lecturafacturacionyrepartoderecibos.CalculoConsumo;
import java.util.ArrayList;
import java.util.List;
import morosidadcorteyreconexion.EstadoSuministro;

public class Suministro {

    private String numeroSuministro;
    private Cliente clienteTitular;
    private Predio predio;
    private Medidor medidor;
    private String opcionTarifaria;
    private LocalDate fechaInicioContrato;
    private List<Recibo> listaRecibos = new ArrayList<>();
    private List<CalculoConsumo> historialConsumos = new ArrayList<>();
    private EstadoSuministro estadoSuministro;
    private double acumuladoDeudaVencida;

    public Suministro(String numeroSuministro, Cliente clienteTitular, Predio predio,
                      Medidor medidor, String opcionTarifaria, LocalDate fechaInicioContrato) {
        this.numeroSuministro = numeroSuministro;
        this.clienteTitular = clienteTitular;
        this.predio = predio;
        this.medidor = medidor;
        this.opcionTarifaria = opcionTarifaria;
        this.fechaInicioContrato = fechaInicioContrato;
    }

    public void activarSuministroEnCronograma() {
        System.out.println("Suministro " + numeroSuministro + " incorporado al cronograma mensual.");
    }

    public List<Double> obtenerHistorialConsumo(int meses) { return new ArrayList<>(); }
    public void asociarRecibo(Recibo recibo) { if (recibo != null) listaRecibos.add(recibo); }
    public boolean verificarDeudaPendiente() { return acumuladoDeudaVencida > 0; }
    public void actualizarEstadoCuenta(String nuevoEstado) { }
    public String getNumeroSuministro() { return numeroSuministro; }
}
