package aplication;

import java.util.ArrayList;
import java.util.List;

public class ContextoDemo {

    // false = Ingresa solo eligiendo el actor en el ComboBox (Modo Demo / Sustentación)
    // true  = Exige escribir usuario/código y clave ("1234")
    public static boolean VALIDAR_CREDENCIALES = false;

    public static class ActorItem {
        public String categoria;
        public String rol;
        public String codigo;
        public String clave;
        public String nombrePersona;

        public ActorItem(String categoria, String rol, String codigo, String clave, String nombrePersona) {
            this.categoria = categoria;
            this.rol = rol;
            this.codigo = codigo;
            this.clave = clave;
            this.nombrePersona = nombrePersona;
        }

        @Override
        public String toString() {
            return categoria + "  ·  " + rol;
        }
    }

    private static ActorItem actorActual;
    private static final List<ActorItem> actores = new ArrayList<>();

    // Tablas compartidas en memoria para los 3 procesos
    public static final List<String[]> listaSolicitudes = new ArrayList<>();
    public static final List<String[]> listaLecturasRecibos = new ArrayList<>();
    public static final List<String[]> listaMorosidadCortes = new ArrayList<>();

    static {
        // Los 15 actores del sistema
        actores.add(new ActorItem("Cliente", "Cliente / Titular del suministro", "C-0001", "1234", "Carlos Mendoza"));
        actores.add(new ActorItem("Trabajador", "Auxiliar Comercial", "T-0001", "1234", "Ana Torres"));
        actores.add(new ActorItem("Trabajador", "Supervisor de Control Comercial", "T-0002", "1234", "Luis Paredes"));
        actores.add(new ActorItem("Trabajador", "Supervisor de Control de Mediciones", "T-0003", "1234", "Jorge Ríos"));
        actores.add(new ActorItem("Trabajador", "Técnico Supervisor de Mediciones", "T-0004", "1234", "Marco Salazar"));
        actores.add(new ActorItem("Contratista", "Contratista de instalación", "K-0001", "1234", "ElectroInstalaciones SAC"));
        actores.add(new ActorItem("Trabajador", "Asistente de Facturación", "T-0005", "1234", "Elena Gómez"));
        actores.add(new ActorItem("Contratista", "Contratista de Lectura", "K-0002", "1234", "Lecturas Norte SAC"));
        actores.add(new ActorItem("Trabajador", "Analista de Facturación", "T-0006", "1234", "Roberto Díaz"));
        actores.add(new ActorItem("Contratista", "Contratista de Reparto", "K-0003", "1234", "Mensajería Express"));
        actores.add(new ActorItem("Trabajador", "Supervisor de Cobranzas", "T-0007", "1234", "Sofía Castro"));
        actores.add(new ActorItem("Trabajador", "Técnico Supervisor Comercial", "T-0008", "1234", "Héctor Silva"));
        actores.add(new ActorItem("Contratista", "Contratista ejecutor de corte y reconexión", "K-0004", "1234", "Servicios Campo SAC"));
        actores.add(new ActorItem("Trabajador", "Asistente de Recaudación", "T-0009", "1234", "Pedro Vega"));
        actores.add(new ActorItem("Externo", "Entidad Recaudadora / CAR", "E-0001", "1234", "Agente BCP / Yape CAR"));

        // Datos precargados - Proceso 1: Alta de Nuevo Suministro
        listaSolicitudes.add(new String[]{"SOL-001", "María López", "DNI: 45871236", "PROPIETARIO", "Av. Pardo 450 - Chimbote", "NO (Libre)", "PENDIENTE_REVISION"});
        listaSolicitudes.add(new String[]{"SOL-002", "Pedro Quispe", "DNI: 71239845", "INQUILINO", "Jr. Bolognesi 210 - Chimbote", "SÍ (Deuda RN-04)", "PENDIENTE_REVISION"});
        listaSolicitudes.add(new String[]{"SOL-003", "Inversiones Norte SAC", "RUC: 20541236981", "PROPIETARIO", "Urb. Buenos Aires Mz B Lt 4", "NO (Libre)", "FACTIBILIDAD_APROBADA"});

        // Datos precargados - Proceso 2: Lectura, Facturación y Reparto
        listaLecturasRecibos.add(new String[]{"SUM-405812", "Carlos Mendoza", "1420.5", "1580.0", "159.5 kWh", "S/ 145.55", "EMITIDO - EN REPARTO"});
        listaLecturasRecibos.add(new String[]{"SUM-405813", "María López", "890.0", "1010.0", "120.0 kWh", "S/ 112.10", "ENTREGADO AL CLIENTE"});
        listaLecturasRecibos.add(new String[]{"SUM-405814", "Comercial Chimbote", "3200.0", "3200.0", "0.0 kWh", "S/ 10.03", "OBSERVADO (INCIDENCIA)"});

        // Datos precargados - Proceso 3: Morosidad, Corte y Reconexión
        listaMorosidadCortes.add(new String[]{"SUM-102938", "Carlos Mendoza", "2 meses", "S/ 312.40", "ACTIVO - CANDIDATO A CORTE"});
        listaMorosidadCortes.add(new String[]{"SUM-884712", "Pedro Quispe", "3 meses", "S/ 485.90", "CORTADO (TIPO I - FUSIBLE)"});
        listaMorosidadCortes.add(new String[]{"SUM-554120", "María López", "1 mes", "S/ 98.50", "EN SEGUIMIENTO (< 2 MESES)"});
    }

    public static ActorItem getActorActual() { return actorActual; }
    public static void setActorActual(ActorItem actor) { actorActual = actor; }
    public static List<ActorItem> getActores() { return actores; }
}