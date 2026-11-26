package aplication;

import database.UsuarioDAO.UsuarioDTO;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DashboardView {

    private final MainApp app;
    private final BorderPane layoutPrincipal = new BorderPane();

    public DashboardView(MainApp app) {
        this.app = app;
    }

    public Parent getView() {
        UsuarioDTO u = ContextoSistema.getUsuario();

        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(10, 18, 10, 18));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: rgb(16, 59, 96);");

        Label lblMarca = new Label("HIDRANDINA S.A.");
        lblMarca.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblMarca.setStyle("-fx-text-fill: #FFFFFF;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblUsuarioInfo = new Label(u.getNombreCompleto() + "  (" + u.nombreRol + ")");
        lblUsuarioInfo.setStyle("-fx-text-fill: #E2E8F0;");

        Button btnSalir = new Button("Cerrar Sesión");
        btnSalir.setStyle("-fx-background-color: #DC2626; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-background-radius: 5;");
        btnSalir.setOnAction(e -> {
            ContextoSistema.cerrarSesion();
            app.mostrarLogin();
        });

        topBar.getChildren().addAll(lblMarca, spacer, lblUsuarioInfo);

        if (u.tienePerfilTrabajador && u.tienePerfilCliente) {
            boolean estaEnCliente = "Cliente".equalsIgnoreCase(u.categoriaRol);
            Button btnCambiarPerfil = new Button(estaEnCliente ? "Cambiar a Vista Trabajador" : "Cambiar a Vista Cliente");
            btnCambiarPerfil.setOnAction(e -> {
                if (estaEnCliente) u.activarModoTrabajador();
                else u.activarModoCliente();
                app.mostrarDashboard();
            });
            topBar.getChildren().add(btnCambiarPerfil);
        }

        topBar.getChildren().add(btnSalir);

        VBox contenedorSuperior = new VBox(topBar, new Separator());
        layoutPrincipal.setTop(contenedorSuperior);

        VBox menuLateral = new VBox(6);
        menuLateral.setPrefWidth(240);
        menuLateral.setPadding(new Insets(15, 12, 15, 12));

        construirMenuPorRol(menuLateral, u);

        HBox contenedorIzquierdo = new HBox(menuLateral, new Separator(Orientation.VERTICAL));
        layoutPrincipal.setLeft(contenedorIzquierdo);

        return layoutPrincipal;
    }

    private void construirMenuPorRol(VBox menu, UsuarioDTO u) {
        String rol = u.nombreRol != null ? u.nombreRol : "";
        boolean esCliente = "Cliente".equalsIgnoreCase(u.categoriaRol);
        boolean esAdmin = "Administrador".equalsIgnoreCase(rol);

        //Perfil Administrador para creación de cuentas de trabajadores
        if (esAdmin) {
            agregarSeccion(menu, "ADMINISTRACIÓN");
            Button btnUsuarios = crearBotonMenu("Gestión de Trabajadores", () -> layoutPrincipal.setCenter(new VistaAdminUsuarios().vistaGestionTrabajadores()));
            menu.getChildren().add(btnUsuarios);
            layoutPrincipal.setCenter(new VistaAdminUsuarios().vistaGestionTrabajadores());
            return;
        }

        VistaProcesoAlta vistaAlta = new VistaProcesoAlta();
        VistaProcesoFacturacion vistaFact = new VistaProcesoFacturacion();
        VistaProcesoMorosidad vistaMor = new VistaProcesoMorosidad();

        //Perfil Cliente
        if (esCliente) {
            agregarSeccion(menu, "PORTAL DEL CLIENTE");
            Button btnSol = crearBotonMenu("Solicitar Nuevo Suministro", () -> layoutPrincipal.setCenter(vistaAlta.vistaPresentarSolicitud()));
            Button btnEst = crearBotonMenu("Estado de Mis Solicitudes", () -> layoutPrincipal.setCenter(vistaAlta.vistaBandejaAlta()));
            Button btnRec = crearBotonMenu("Mis Recibos Eléctricos", () -> layoutPrincipal.setCenter(vistaFact.vistaPreFacturacionYReparto()));
            Button btnPag = crearBotonMenu("Pagos y Convenios", () -> layoutPrincipal.setCenter(vistaMor.vistaPagosYReconexion()));
            Button btnPer = crearBotonMenu("Mi Perfil y Contacto", () -> layoutPrincipal.setCenter(new VistaPerfilCliente().vistaActualizarDatos()));

            menu.getChildren().addAll(btnSol, btnEst, btnRec, btnPag, new Separator(), btnPer);
            layoutPrincipal.setCenter(vistaAlta.vistaPresentarSolicitud());
            return;
        }

        //Alta de Nuevo Suministro
        if (rol.equals("Auxiliar Comercial") || rol.equals("Supervisor de Control Comercial")
                || rol.equals("Supervisor de Control de Mediciones") || rol.equals("Contratista de instalación")
                || rol.equals("Técnico Supervisor de Mediciones")) {
            agregarSeccion(menu, "ALTA DE SUMINISTRO");
            Button btnBandejaAlta = crearBotonMenu("Bandeja de Alta Suministro", () -> layoutPrincipal.setCenter(vistaAlta.vistaBandejaAlta()));
            menu.getChildren().add(btnBandejaAlta);
            if (layoutPrincipal.getCenter() == null) layoutPrincipal.setCenter(vistaAlta.vistaBandejaAlta());
        }

        //Lectura, Facturación y Reparto
        if (rol.equals("Asistente de Facturación") || rol.equals("Contratista de Lectura")
                || rol.equals("Técnico Supervisor de Mediciones") || rol.equals("Supervisor de Control de Mediciones")) {
            agregarSeccion(menu, "LECTURA Y FACTURACIÓN");
            Button btnLectura = crearBotonMenu("Cronograma y Lecturas", () -> layoutPrincipal.setCenter(vistaFact.vistaTomaLectura()));
            menu.getChildren().add(btnLectura);
            if (layoutPrincipal.getCenter() == null) layoutPrincipal.setCenter(vistaFact.vistaTomaLectura());
        }

        if (rol.equals("Analista de Facturación") || rol.equals("Asistente de Facturación")
                || rol.equals("Contratista de Reparto")) {
            if (menu.getChildren().stream().noneMatch(n -> n instanceof Label l && l.getText().equals("2. LECTURA Y FACTURACIÓN"))) {
                agregarSeccion(menu, "LECTURA Y FACTURACIÓN");
            }
            Button btnPrefact = crearBotonMenu("Pre-Facturación y Reparto", () -> layoutPrincipal.setCenter(vistaFact.vistaPreFacturacionYReparto()));
            menu.getChildren().add(btnPrefact);
            if (layoutPrincipal.getCenter() == null) layoutPrincipal.setCenter(vistaFact.vistaPreFacturacionYReparto());
        }

        // Morosidad, Corte y Reconexión
        if (rol.equals("Supervisor de Cobranzas") || rol.equals("Supervisor de Control Comercial")
                || rol.equals("Contratista ejecutor de corte y reconexión") || rol.equals("Técnico Supervisor Comercial")) {
            agregarSeccion(menu, "MOROSIDAD Y CORTES");
            Button btnCortes = crearBotonMenu("Monitor Morosidad y Cortes", () -> layoutPrincipal.setCenter(vistaMor.vistaMonitorMorosidadYCortes()));
            menu.getChildren().add(btnCortes);
            if (layoutPrincipal.getCenter() == null) layoutPrincipal.setCenter(vistaMor.vistaMonitorMorosidadYCortes());
        }

        if (rol.equals("Supervisor de Cobranzas") || rol.equals("Entidad Recaudadora / CAR")
                || rol.equals("Asistente de Recaudación") || rol.equals("Contratista ejecutor de corte y reconexión")
                || rol.equals("Técnico Supervisor Comercial")) {
            if (menu.getChildren().stream().noneMatch(n -> n instanceof Label l && l.getText().equals("MOROSIDAD Y CORTES"))) {
                agregarSeccion(menu, "MOROSIDAD Y CORTES");
            }
            Button btnReconexion = crearBotonMenu("Recaudación y Reconexión", () -> layoutPrincipal.setCenter(vistaMor.vistaPagosYReconexion()));
            menu.getChildren().add(btnReconexion);
            if (layoutPrincipal.getCenter() == null) layoutPrincipal.setCenter(vistaMor.vistaPagosYReconexion());
        }
    }

    private void agregarSeccion(VBox menu, String titulo) {
        Label lbl = new Label(titulo);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 11));
        VBox.setMargin(lbl, new Insets(8, 0, 2, 0));
        menu.getChildren().add(lbl);
    }

    private Button crearBotonMenu(String texto, Runnable accion) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setOnAction(e -> accion.run());
        return btn;
    }
}