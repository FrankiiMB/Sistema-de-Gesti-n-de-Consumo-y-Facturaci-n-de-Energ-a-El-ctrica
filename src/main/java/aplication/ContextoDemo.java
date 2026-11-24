package aplication;

import java.util.ArrayList;
import java.util.List;

public class ContextoDemo {

    // false = Entra sin validar credenciales (Modo Demo)
    // true  = Valida que el usuario y clave existan para el rol elegido
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

    public static class CuentaUsuario {
        public String codigoUsuario;
        public String clave;
        public String nombreCompleto;
        public String documento;
        public String rolAsignado;

        public CuentaUsuario(String codigoUsuario, String clave, String nombreCompleto, String documento, String rolAsignado) {
            this.codigoUsuario = codigoUsuario;
            this.clave = clave;
            this.nombreCompleto = nombreCompleto;
            this.documento = documento;
            this.rolAsignado = rolAsignado;
        }
    }

    private static ActorItem actorActual;
    private static final List<ActorItem> actores = new ArrayList<>();
    public static final List<CuentaUsuario> cuentasRegistradas = new ArrayList<>();

    // Tablas compartidas en memoria para los 3 procesos
    public static final List<String[]> listaSolicitudes = new ArrayList<>();
    public static final List<String[]> listaLecturasRecibos = new ArrayList<>();
    public static final List<String[]> listaMorosidadCortes = new ArrayList<>();

    static {
        // 1. PRIMERA OPCIÓN: ADMINISTRADOR
        actores.add(new ActorItem("Sistema", "Administrador", "ADM-001", "admin123", "Administrador General"));

        // 2. RESTO DE ACTORES DEL SISTEMA
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

        // Cargar las cuentas iniciales en la tabla de usuarios registrados
        for (ActorItem a : actores) {
            cuentasRegistradas.add(new CuentaUsuario(a.codigo, a.clave, a.nombrePersona, "00000000", a.rol));
        }

        // Datos precargados de los 3 procesos
        listaSolicitudes.add(new String[]{"SOL-001", "María López", "DNI: 45871236", "PROPIETARIO", "Av. Pardo 450 - Chimbote", "NO (Libre)", "PENDIENTE_REVISION"});
        listaSolicitudes.add(new String[]{"SOL-002", "Pedro Quispe", "DNI: 71239845", "INQUILINO", "Jr. Bolognesi 210 - Chimbote", "SÍ (Deuda RN-04)", "PENDIENTE_REVISION"});
        listaSolicitudes.add(new String[]{"SOL-003", "Inversiones Norte SAC", "RUC: 20541236981", "PROPIETARIO", "Urb. Buenos Aires Mz B Lt 4", "NO (Libre)", "FACTIBILIDAD_APROBADA"});

        listaLecturasRecibos.add(new String[]{"SUM-405812", "Carlos Mendoza", "1420.5", "1580.0", "159.5 kWh", "S/ 145.55", "EMITIDO - EN REPARTO"});
        listaLecturasRecibos.add(new String[]{"SUM-405813", "María López", "890.0", "1010.0", "120.0 kWh", "S/ 112.10", "ENTREGADO AL CLIENTE"});

        listaMorosidadCortes.add(new String[]{"SUM-102938", "Carlos Mendoza", "2 meses", "S/ 312.40", "ACTIVO - CANDIDATO A CORTE"});
        listaMorosidadCortes.add(new String[]{"SUM-884712", "Pedro Quispe", "3 meses", "S/ 485.90", "CORTADO (TIPO I - FUSIBLE)"});
    }

    public static CuentaUsuario buscarCuenta(String usuario, String clave, String rol) {
        return cuentasRegistradas.stream()
                .filter(c -> c.rolAsignado.equals(rol)
                        && c.codigoUsuario.equalsIgnoreCase(usuario)
                        && c.clave.equals(clave))
                .findFirst()
                .orElse(null);
    }

    public static ActorItem getActorActual() { return actorActual; }
    public static void setActorActual(ActorItem actor) { actorActual = actor; }
    public static List<ActorItem> getActores() { return actores; }
}