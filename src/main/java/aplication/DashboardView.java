package aplication;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardView {

    private final MainApp app;
    private final BorderPane layout = new BorderPane();
    private final List<Button> botonesMenu = new ArrayList<>();

    public DashboardView(MainApp app) {
        this.app = app;
    }

    public Parent getView() {
        var actor = ContextoDemo.getActorActual();

        // Barra superior
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(10, 20, 10, 20));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #003B73;");

        Label lblApp = new Label("SISTEMA DE GESTIÓN DE CONSUMO Y FACTURACIÓN ELÉCTRICA");
        lblApp.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblActor = new Label(actor.nombrePersona + "  |  " + actor.toString());
        lblActor.setStyle("-fx-text-fill: #D9E2EC; -fx-font-size: 12px;");

        Button btnSalir = new Button("Cambiar Actor / Salir");
        btnSalir.setStyle("-fx-background-color: #D64545; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnSalir.setOnAction(e -> app.mostrarLogin());

        topBar.getChildren().addAll(lblApp, spacer, lblActor, btnSalir);
        layout.setTop(topBar);

        // Menú lateral izquierdo con los 3 procesos
        VBox sideMenu = new VBox(2);
        sideMenu.setPrefWidth(265);
        sideMenu.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #D9E2EC; -fx-border-width: 0 1 0 0;");

        VistaProcesoAlta v1 = new VistaProcesoAlta();
        VistaProcesoFacturacion v2 = new VistaProcesoFacturacion();
        VistaProcesoMorosidad v3 = new VistaProcesoMorosidad();

        // PROCESO 1
        agregarSeccion(sideMenu, "1. ALTA DE NUEVO SUMINISTRO");
        Button btnP1Solicitar = agregarItem(sideMenu, "Presentar solicitud (HU-01)", () -> setCentroScroll(v1.vistaPresentarSolicitud()));
        Button btnP1Bandeja = agregarItem(sideMenu, "Evaluación, contrato e inst. (HU-02..07)", () -> setCentroScroll(v1.vistaBandejaAlta()));

        // PROCESO 2
        agregarSeccion(sideMenu, "2. LECTURA Y FACTURACIÓN");
        Button btnP2Lectura = agregarItem(sideMenu, "Cronograma y toma lectura (HU-08..11)", () -> setCentroScroll(v2.vistaTomaLectura()));
        Button btnP2Factura = agregarItem(sideMenu, "Pre-facturación y reparto (HU-12..14)", () -> setCentroScroll(v2.vistaPreFacturacionYReparto()));

        // PROCESO 3
        agregarSeccion(sideMenu, "3. MOROSIDAD, CORTE Y RECONEXIÓN");
        Button btnP3Cortes = agregarItem(sideMenu, "Monitor morosidad y cortes (HU-15..17)", () -> setCentroScroll(v3.vistaMonitorMorosidadYCortes()));
        Button btnP3Pagos = agregarItem(sideMenu, "Pagos CAR y reconexión (HU-18..21)", () -> setCentroScroll(v3.vistaPagosYReconexion()));

        // Selección automática de pantalla inicial según el rol elegido
        String rol = actor.rol;
        if (rol.contains("Cliente")) {
            activarBoton(btnP1Solicitar);
            setCentroScroll(v1.vistaPresentarSolicitud());
        } else if (rol.contains("Auxiliar") || rol.contains("Control Comercial") || rol.contains("Control de Mediciones")
                || rol.contains("instalación") || rol.contains("Técnico Supervisor de Mediciones")) {
            activarBoton(btnP1Bandeja);
            setCentroScroll(v1.vistaBandejaAlta());
        } else if (rol.contains("Lectura")) {
            activarBoton(btnP2Lectura);
            setCentroScroll(v2.vistaTomaLectura());
        } else if (rol.contains("Facturación") || rol.contains("Reparto")) {
            activarBoton(btnP2Factura);
            setCentroScroll(v2.vistaPreFacturacionYReparto());
        } else if (rol.contains("Cobranzas") || rol.contains("corte") || rol.contains("Técnico Supervisor Comercial")) {
            activarBoton(btnP3Cortes);
            setCentroScroll(v3.vistaMonitorMorosidadYCortes());
        } else {
            activarBoton(btnP3Pagos);
            setCentroScroll(v3.vistaPagosYReconexion());
        }

        layout.setLeft(sideMenu);
        return layout;
    }

    private void setCentroScroll(Parent vista) {
        ScrollPane scroll = new ScrollPane(vista);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #F4F7FA; -fx-background: #F4F7FA;");
        layout.setCenter(scroll);
    }

    private void agregarSeccion(VBox menu, String titulo) {
        Label lbl = new Label(titulo);
        lbl.setStyle("-fx-text-fill: #829AB1; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 14 14 4 14;");
        menu.getChildren().add(lbl);
    }

    private Button agregarItem(VBox menu, String texto, Runnable accion) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        estiloNormal(btn);
        btn.setOnAction(e -> {
            activarBoton(btn);
            accion.run();
        });
        botonesMenu.add(btn);
        menu.getChildren().add(btn);
        return btn;
    }

    private void activarBoton(Button activo) {
        for (Button b : botonesMenu) estiloNormal(b);
        activo.setStyle("-fx-background-color: #004A80; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 16; -fx-background-radius: 0; -fx-cursor: hand;");
    }

    private void estiloNormal(Button b) {
        b.setStyle("-fx-background-color: transparent; -fx-text-fill: #003B73; -fx-padding: 10 16; -fx-background-radius: 0; -fx-cursor: hand;");
    }
}