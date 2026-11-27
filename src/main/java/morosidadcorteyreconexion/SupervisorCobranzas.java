package morosidadcorteyreconexion;

import altanuevosuministro.Suministro;
import altanuevosuministro.Trabajador;
import lecturafacturacionyrepartoderecibos.Recibo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupervisorCobranzas extends Trabajador {

    public SupervisorCobranzas(String codigoTrabajador, String cargo) {
        super(codigoTrabajador, cargo);
    }

    public SupervisorCobranzas(String codigoTrabajador, String cargo, String nombres, String apellidos, String dni) {
        super(codigoTrabajador, cargo, nombres, apellidos, dni);
    }

    private final Map<Suministro, List<Recibo>> carteraRecibos = new HashMap<>();
    private final Map<Suministro, ConvenioPago> conveniosActivos = new HashMap<>();
    private final Map<Suministro, Boolean> suministrosCortados = new HashMap<>();

    public void registrarReciboEnCartera(Suministro s, Recibo r) {
        if (s != null && r != null) {
            carteraRecibos.computeIfAbsent(s, k -> new ArrayList<>()).add(r);
        }
    }

    public boolean analizarMorosidad(Suministro s) {
        if (s == null) {
            return false;
        }
        List<Recibo> recibos = carteraRecibos.getOrDefault(s, List.of());
        int facturasVencidas = 0;
        for (Recibo r : recibos) {
            boolean noPagado = r.getEstado() == null || !r.getEstado().name().equalsIgnoreCase("PAGADO");
            boolean vencido = r.getFechaVencimiento() != null && LocalDate.now().isAfter(r.getFechaVencimiento());
            if (noPagado && vencido) {
                r.calcularInteresesYMoras();
                facturasVencidas++;
            }
        }
        // HU-15 / HU-16: Procede corte con 2 o más facturaciones vencidas
        return facturasVencidas >= 2;
    }

    public OrdenCorte gestionarCorteServicio(Suministro s) {
        if (s == null) {
            return null;
        }
        // HU-16: Si ya está cortado, no se programa un nuevo corte
        if (Boolean.TRUE.equals(suministrosCortados.get(s))) {
            return null;
        }
        if (!analizarMorosidad(s)) {
            return null;
        }
        OrdenCorte orden = new OrdenCorte(s, OrdenCorte.resolverTipoCorte(0));
        return orden;
    }

    public boolean verificarPagosAntesDeCorte(OrdenCorte o) {
        if (o == null) {
            return false;
        }
        Suministro s = o.getSuministro();
        if (s == null) {
            return !o.isAnulada();
        }
        // RN-16: Si el cliente regularizó o suscribió convenio antes del corte, se anula automáticamente
        ConvenioPago conv = conveniosActivos.get(s);
        if (conv != null && conv.validarVigencia()) {
            anularOrdenCorte(o);
            return false;
        }
        if (!analizarMorosidad(s)) {
            anularOrdenCorte(o);
            return false;
        }
        return !o.isAnulada();
    }

    public OrdenReconexion generarOrdenReconexion(Suministro s) {
        if (s == null) {
            return null;
        }
        // RN-21: Solo procede reconexión con deuda regularizada (sin morosidad) o convenio vigente
        ConvenioPago conv = conveniosActivos.get(s);
        boolean tieneConvenioValido = conv != null && conv.validarVigencia();
        boolean sinDeudaVencida = !analizarMorosidad(s);

        if (!tieneConvenioValido && !sinDeudaVencida) {
            return null;
        }
        suministrosCortados.put(s, false);
        return new OrdenReconexion(s, true);
    }

    public void anularOrdenCorte(OrdenCorte o) {
        if (o != null) {
            o.anularPorPagoPrevio();
        }
    }

    public boolean aprobarConvenio(ConvenioPago c) {
        if (c == null || c.getMontoFinanciado() <= 0 || c.getCuotasTotales() <= 0) {
            return false;
        }
        c.setVigente(true);
        return c.validarVigencia();
    }

    public void asociarConvenioASuministro(Suministro s, ConvenioPago c) {
        if (s != null && aprobarConvenio(c)) {
            conveniosActivos.put(s, c);
        }
    }

    public void marcarSuministroCortado(Suministro s, boolean cortado) {
        if (s != null) {
            suministrosCortados.put(s, cortado);
        }
    }
}
