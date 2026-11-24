package aplication;

import database.UsuarioDAO.UsuarioDTO;
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
        UsuarioDTO actor = SesionActual.getUsuario();

        // Barra superior
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(10, 20, 10, 20));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #003B73;");

        Label lblApp = new Label("SISTEMA DE GESTIÓN DE CONSUMO Y FACTURACIÓN ELÉCTRICA");
        lblApp.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblActor = new Label(actor.getNombreCompleto() + "  |  " + actor.categoriaRol + " · " + actor.nombreRol);
        lblActor.setStyle("-fx-text-fill: #D9E2EC; -fx-font-size: 12px;");

        Button btnSalir = new Button("Cerrar Sesión");
        btnSalir.setStyle("-fx-background-color: #D64545; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnSalir.setOnAction(e -> {
            SesionActual.setUsuario(null);
            app.mostrarLogin();
        });

        topBar.getChildren().addAll(lblApp, spacer, lblActor, btnSalir);
        layout.setTop(topBar);

        VBox sideMenu = new VBox(2);
        sideMenu.setPrefWidth(275);
        sideMenu.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #D9E2EC; -fx-border-width: 0 1 0 0;");

        switch (actor.nombreRol) {
            case "Administrador" -> {
                VistaAdminUsuarios vAdmin = new VistaAdminUsuarios();
                agregarSeccion(sideMenu, "ADMINISTRACIÓN DEL SISTEMA");
                Button b1 = agregarItem(sideMenu, "Crear cuentas de trabajadores", () -> setCentroScroll(vAdmin.vistaGestionTrabajadores()));
                activarYMostrar(b1, vAdmin.vistaGestionTrabajadores());
            }
            case "Cliente / Titular del suministro" -> {
                VistaProcesoAlta v1 = new VistaProcesoAlta();
                agregarSeccion(sideMenu, "1. ALTA DE NUEVO SUMINISTRO");
                Button b1 = agregarItem(sideMenu, "Presentar solicitud (HU-01)", () -> setCentroScroll(v1.vistaPresentarSolicitud()));
                activarYMostrar(b1, v1.vistaPresentarSolicitud());
            }
            default -> {
                // Aquí van los demás roles que ya tienes configurados en tu switch anterior
                VistaProcesoAlta v1 = new VistaProcesoAlta();
                agregarSeccion(sideMenu, "BANDEJA DE TRABAJO");
                Button b1 = agregarItem(sideMenu, "Gestión asignada al rol", () -> setCentroScroll(v1.vistaBandejaAlta()));
                activarYMostrar(b1, v1.vistaBandejaAlta());
            }
        }

        layout.setLeft(sideMenu);
        return layout;
    }

    private void activarYMostrar(Button btn, Parent vista) {
        for (Button b : botonesMenu) {
            b.setStyle("-fx-background-color: transparent; -fx-text-fill: #003B73; -fx-padding: 10 16; -fx-background-radius: 0; -fx-cursor: hand;");
        }
        btn.setStyle("-fx-background-color: #004A80; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 16; -fx-background-radius: 0; -fx-cursor: hand;");
        ScrollPane scroll = new ScrollPane(vista);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #F4F7FA; -fx-background: #F4F7FA;");
        layout.setCenter(scroll);
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
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #003B73; -fx-padding: 10 16; -fx-background-radius: 0; -fx-cursor: hand;");
        btn.setOnAction(e -> activarYMostrar(btn, getVistaPorTexto(texto)));
        botonesMenu.add(btn);
        menu.getChildren().add(btn);
        return btn;
    }

    private Parent getVistaPorTexto(String texto) {
        if (texto.contains("trabajadores")) return new VistaAdminUsuarios().vistaGestionTrabajadores();
        if (texto.contains("Presentar")) return new VistaProcesoAlta().vistaPresentarSolicitud();
        return new VistaProcesoAlta().vistaBandejaAlta();
    }
}