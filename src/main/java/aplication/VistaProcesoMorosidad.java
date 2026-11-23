package aplication;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class VistaProcesoMorosidad {

    // VISTA 3A: Monitor de Morosidad y Ejecución de Cortes (HU-15 a HU-17)
    public Parent vistaMonitorMorosidadYCortes() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(25, 35, 25, 35));
        root.setStyle("-fx-background-color: #F4F7FA;");

        Label lblTitulo = new Label("Monitor de Morosidad y Órdenes de Corte (HU-15 a HU-17)");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 20));
        lblTitulo.setStyle("-fx-text-fill: #003B73;");

        TableView<String[]> tabla = new TableView<>();
        String[] headers = {"Suministro", "Titular", "Meses Vencidos", "Deuda + Mora (15%)", "Estado del Servicio"};
        for (int i = 0; i < headers.length; i++) {
            final int idx = i;
            TableColumn<String[], String> col = new TableColumn<>(headers[i]);
            col.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[idx]));
            tabla.getColumns().add(col);
        }
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabla.setItems(FXCollections.observableArrayList(ContextoDemo.listaMorosidadCortes));

        HBox barra = new HBox(12);
        ComboBox<String> cmbTipoCorte = new ComboBox<>(FXCollections.observableArrayList(
                "Tipo I (Fusible)", "Tipo II (Caja de Medición)", "Tipo III (Línea Aérea / Acometida)"
        ));
        cmbTipoCorte.setValue("Tipo I (Fusible)");

        Button btnProgramarCorte = new Button("Programar y Ejecutar Corte (HU-16 / HU-17)");
        btnProgramarCorte.setStyle("-fx-background-color: #D64545; -fx-text-fill: white; -fx-font-weight: bold;");

        Button btnAuditarCorte = new Button("Auditar Corte en Campo (Téc. Comercial)");
        btnAuditarCorte.setStyle("-fx-background-color: #004A80; -fx-text-fill: white; -fx-font-weight: bold;");

        btnProgramarCorte.setOnAction(e -> {
            String[] sel = tabla.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            if (sel[2].startsWith("1") || sel[2].startsWith("0")) {
                new Alert(Alert.AlertType.WARNING, "HU-15/16: El corte solo procede con 2 o más meses de deuda vencida.").showAndWait();
                return;
            }
            sel[4] = "CORTADO (" + cmbTipoCorte.getValue() + ")";
            tabla.refresh();
            new Alert(Alert.AlertType.INFORMATION, "RN-17/18: Corte ejecutado y cargo administrativo cargado a la cuenta.").showAndWait();
        });

        btnAuditarCorte.setOnAction(e -> {
            String[] sel = tabla.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            sel[4] = sel[4] + " [AUDITADO OK]";
            tabla.refresh();
        });

        barra.getChildren().addAll(new Label("Secuencia progresiva (RN-17):"), cmbTipoCorte, btnProgramarCorte, btnAuditarCorte);
        root.getChildren().addAll(lblTitulo, tabla, barra);
        return root;
    }

    // VISTA 3B: Entidad Recaudadora CAR, Convenios y Reconexión (HU-18 a HU-21)
    public Parent vistaPagosYReconexion() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(25, 35, 25, 35));
        root.setStyle("-fx-background-color: #F4F7FA;");

        Label lblTitulo = new Label("Recaudación CAR, Convenios y Reconexión (HU-18 a HU-21)");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 20));
        lblTitulo.setStyle("-fx-text-fill: #003B73;");

        TableView<String[]> tabla = new TableView<>();
        String[] headers = {"Suministro", "Titular", "Meses Vencidos", "Deuda Total", "Estado del Servicio"};
        for (int i = 0; i < headers.length; i++) {
            final int idx = i;
            TableColumn<String[], String> col = new TableColumn<>(headers[i]);
            col.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[idx]));
            tabla.getColumns().add(col);
        }
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabla.setItems(FXCollections.observableArrayList(ContextoDemo.listaMorosidadCortes));

        HBox acciones = new HBox(12);
        Button btnPagoTotal = new Button("1. Registrar Pago Total CAR y Conciliar (HU-18/19)");
        btnPagoTotal.setStyle("-fx-background-color: #0D9488; -fx-text-fill: white; -fx-font-weight: bold;");

        Button btnConvenio = new Button("2. Suscribir Convenio de Pago (HU-18)");
        btnConvenio.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold;");

        Button btnReconectar = new Button("3. Autorizar y Ejecutar Reconexión (HU-20/21)");
        btnReconectar.setStyle("-fx-background-color: #198754; -fx-text-fill: white; -fx-font-weight: bold;");

        btnPagoTotal.setOnAction(e -> {
            String[] sel = tabla.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            sel[2] = "0 meses";
            sel[3] = "S/ 0.00";
            sel[4] = "DEUDA REGULARIZADA - LISTO RECONEXIÓN";
            tabla.refresh();
            new Alert(Alert.AlertType.INFORMATION, "RN-20: Pago total conciliado en tiempo real.").showAndWait();
        });

        btnConvenio.setOnAction(e -> {
            String[] sel = tabla.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            sel[4] = "CONVENIO VIGENTE - LISTO RECONEXIÓN";
            tabla.refresh();
            new Alert(Alert.AlertType.INFORMATION, "RN-21: Convenio de pago suscrito y aprobado.").showAndWait();
        });

        btnReconectar.setOnAction(e -> {
            String[] sel = tabla.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            if (!sel[4].contains("LISTO RECONEXIÓN")) {
                new Alert(Alert.AlertType.ERROR, "RN-21: No se puede reconectar sin pago total o convenio vigente.").showAndWait();
                return;
            }
            sel[4] = "ACTIVO (RECONECTADO)";
            tabla.refresh();
            new Alert(Alert.AlertType.INFORMATION, "HU-21: Servicio restituido y cargo por reconexión agendado para el siguiente ciclo.").showAndWait();
        });

        acciones.getChildren().addAll(btnPagoTotal, btnConvenio, btnReconectar);
        root.getChildren().addAll(lblTitulo, tabla, acciones);
        return root;
    }
}