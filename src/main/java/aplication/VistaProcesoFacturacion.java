package aplication;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class VistaProcesoFacturacion {

    // VISTA 2A: Programación y Toma de Lectura en Campo (HU-08 a HU-11)
    public Parent vistaTomaLectura() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(25, 35, 25, 35));
        root.setStyle("-fx-background-color: #F4F7FA;");

        Label lblTitulo = new Label("Cronograma y Toma de Lectura de Medidores (HU-08 a HU-11)");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 20));
        lblTitulo.setStyle("-fx-text-fill: #003B73;");

        TitledPane panel = new TitledPane();
        panel.setText("Registro de Lectura / Incidencia en Campo (Contratista de Lectura)");
        panel.setCollapsible(false);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        ComboBox<String> cmbRuta = new ComboBox<>(FXCollections.observableArrayList("RUTA-01 (Centro Chimbote - 30 días)", "RUTA-02 (Nuevo Chimbote - 30 días)"));
        cmbRuta.setValue("RUTA-01 (Centro Chimbote - 30 días)");

        TextField txtSuministro = new TextField("SUM-405815");
        TextField txtTitular = new TextField("Jorge Ramírez");
        TextField txtLecturaAnt = new TextField("2100.0");
        TextField txtLecturaAct = new TextField("2265.0");

        ComboBox<String> cmbIncidencia = new ComboBox<>(FXCollections.observableArrayList(
                "NINGUNA (Lectura Accesible)", "MEDIDOR INACCESIBLE (Estimación RN-10)", "MEDIDOR MANIPULADO", "CAMBIO DE MEDIDOR (RN-11)"
        ));
        cmbIncidencia.setValue("NINGUNA (Lectura Accesible)");

        Button btnRegistrar = new Button("Registrar Lectura en Ciclo");
        btnRegistrar.setStyle("-fx-background-color: #198754; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 18;");

        btnRegistrar.setOnAction(e -> {
            try {
                double ant = Double.parseDouble(txtLecturaAnt.getText());
                double act = Double.parseDouble(txtLecturaAct.getText());
                if (act < ant && cmbIncidencia.getValue().startsWith("NINGUNA")) {
                    new Alert(Alert.AlertType.ERROR, "RN-11: La lectura actual no puede ser menor a la anterior sin cambio documentado.").showAndWait();
                    return;
                }
                double consumo = Math.max(0, act - ant);
                double total = ((consumo * 0.72) + 8.50) * 1.18;
                String estado = cmbIncidencia.getValue().startsWith("NINGUNA") ? "LECTURA VALIDADA" : "INCIDENCIA REPORTADA";

                ContextoDemo.listaLecturasRecibos.add(new String[]{
                        txtSuministro.getText(), txtTitular.getText(),
                        String.valueOf(ant), String.valueOf(act),
                        String.format("%.1f kWh", consumo), String.format("S/ %.2f", total), estado
                });
                new Alert(Alert.AlertType.INFORMATION, "HU-09: Lectura registrada (" + estado + ").").showAndWait();
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.WARNING, "Ingrese valores numéricos válidos.").showAndWait();
            }
        });

        grid.add(new Label("Ruta / Periodo (RN-08):"), 0, 0); grid.add(cmbRuta, 1, 0);
        grid.add(new Label("N° Suministro:"), 2, 0); grid.add(txtSuministro, 3, 0);
        grid.add(new Label("Titular:"), 0, 1); grid.add(txtTitular, 1, 1);
        grid.add(new Label("Incidencia (RN-10):"), 2, 1); grid.add(cmbIncidencia, 3, 1);
        grid.add(new Label("Lectura Anterior (kWh):"), 0, 2); grid.add(txtLecturaAnt, 1, 2);
        grid.add(new Label("Lectura Actual (kWh):"), 2, 2); grid.add(txtLecturaAct, 3, 2);
        grid.add(btnRegistrar, 3, 3);

        panel.setContent(grid);
        root.getChildren().addAll(lblTitulo, panel);
        return root;
    }

    // VISTA 2B: Pre-Facturación, Emisión de Recibos y Reparto (HU-12 a HU-14)
    public Parent vistaPreFacturacionYReparto() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(25, 35, 25, 35));
        root.setStyle("-fx-background-color: #F4F7FA;");

        Label lblTitulo = new Label("Pre-Facturación, Emisión de Recibos y Reparto (HU-12 a HU-14)");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 20));
        lblTitulo.setStyle("-fx-text-fill: #003B73;");

        TableView<String[]> tabla = new TableView<>();
        String[] headers = {"Suministro", "Titular", "Lect. Ant.", "Lect. Act.", "Consumo", "Total (inc. IGV/FOSE)", "Estado Facturación / Reparto"};
        for (int i = 0; i < headers.length; i++) {
            final int idx = i;
            TableColumn<String[], String> col = new TableColumn<>(headers[i]);
            col.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[idx]));
            tabla.getColumns().add(col);
        }
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabla.setItems(FXCollections.observableArrayList(ContextoDemo.listaLecturasRecibos));

        HBox acciones = new HBox(12);
        Button btnEmitir = new Button("Emitir Recibo Oficial (HU-13)");
        btnEmitir.setStyle("-fx-background-color: #004A80; -fx-text-fill: white; -fx-font-weight: bold;");

        Button btnEntregar = new Button("Registrar Entrega en Ruta (HU-14)");
        btnEntregar.setStyle("-fx-background-color: #198754; -fx-text-fill: white; -fx-font-weight: bold;");

        Button btnFalloReparto = new Button("Reportar Fallo de Reparto (RN-14)");
        btnFalloReparto.setStyle("-fx-background-color: #D97706; -fx-text-fill: white; -fx-font-weight: bold;");

        btnEmitir.setOnAction(e -> {
            String[] sel = tabla.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            if (sel[6].contains("INCIDENCIA")) {
                new Alert(Alert.AlertType.ERROR, "HU-13: No se puede emitir recibo con observaciones sin resolver.").showAndWait();
                return;
            }
            sel[6] = "RECIBO EMITIDO - ASIGNADO A REPARTO";
            tabla.refresh();
            new Alert(Alert.AlertType.INFORMATION, "Recibo emitido con cargo fijo, alumbrado, FOSE e IGV 18% (RN-12/13).").showAndWait();
        });

        btnEntregar.setOnAction(e -> {
            String[] sel = tabla.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            sel[6] = "ENTREGADO AL CLIENTE (CARGO OK)";
            tabla.refresh();
        });

        btnFalloReparto.setOnAction(e -> {
            String[] sel = tabla.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            sel[6] = "REPARTO FALLIDO - REPROGRAMADO (RN-14)";
            tabla.refresh();
        });

        acciones.getChildren().addAll(btnEmitir, btnEntregar, btnFalloReparto);
        root.getChildren().addAll(lblTitulo, tabla, acciones);
        return root;
    }
}