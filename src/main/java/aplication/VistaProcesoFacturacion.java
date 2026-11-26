package aplication;

import database.UsuarioDAO.UsuarioDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class VistaProcesoFacturacion {

    // VISTA 2A: Programación y Toma de Lectura en Campo
    public Parent vistaTomaLectura() {
        UsuarioDTO u = ContextoSistema.getUsuario();
        String rol = (u != null && u.nombreRol != null) ? u.nombreRol : "";

        VBox root = new VBox(12);
        root.setPadding(new Insets(20, 30, 20, 30));

        Label lblTitulo = new Label("Cronograma y Toma de Lectura de Medidores");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 18));

        TitledPane panel = new TitledPane();
        panel.setText("Gestión de Lecturas y Rutas (" + rol + ")");
        panel.setCollapsible(false);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        ComboBox<String> cmbRuta = new ComboBox<>(FXCollections.observableArrayList(
                "RUTA-01 (Centro Chimbote - 30 días)",
                "RUTA-02 (Nuevo Chimbote - 30 días)"
        ));
        cmbRuta.setValue("RUTA-01 (Centro Chimbote - 30 días)");

        TextField txtSuministro = new TextField();
        txtSuministro.setPromptText("Ingrese N° de suministro activo");

        TextField txtTitular = new TextField();
        txtTitular.setPromptText("Titular del suministro");

        TextField txtLecturaAnt = new TextField();
        txtLecturaAnt.setPromptText("0.0");

        TextField txtLecturaAct = new TextField();
        txtLecturaAct.setPromptText("0.0");

        ComboBox<String> cmbIncidencia = new ComboBox<>(FXCollections.observableArrayList(
                "NINGUNA (Lectura Accesible)",
                "MEDIDOR INACCESIBLE (Estimación)",
                "MEDIDOR MANIPULADO",
                "CAMBIO DE MEDIDOR"
        ));
        cmbIncidencia.setValue("NINGUNA (Lectura Accesible)");

        grid.add(new Label("Ruta / Periodo:"), 0, 0);          grid.add(cmbRuta, 1, 0);
        grid.add(new Label("N° Suministro:"), 2, 0);           grid.add(txtSuministro, 3, 0);
        grid.add(new Label("Titular:"), 0, 1);                 grid.add(txtTitular, 1, 1);
        grid.add(new Label("Incidencia:"), 2, 1);              grid.add(cmbIncidencia, 3, 1);
        grid.add(new Label("Lectura Anterior (kWh):"), 0, 2);  grid.add(txtLecturaAnt, 1, 2);
        grid.add(new Label("Lectura Actual (kWh):"), 2, 2);    grid.add(txtLecturaAct, 3, 2);

        FlowPane botones = new FlowPane(10, 8);

        if ("Asistente de Facturación".equalsIgnoreCase(rol)) {
            Button btnProgramarRuta = new Button("Programar Cronograma y Orden de Lectura");
            btnProgramarRuta.setOnAction(e ->
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Cronograma generado", "Orden de lectura generada para " + cmbRuta.getValue() + ".")
            );
            botones.getChildren().add(btnProgramarRuta);
        }

        if ("Contratista de Lectura".equalsIgnoreCase(rol)) {
            Button btnRegistrar = new Button("Registrar Lectura / Incidencia en Campo");
            btnRegistrar.setOnAction(e -> {
                if (txtSuministro.getText().isBlank() || txtTitular.getText().isBlank()
                        || txtLecturaAnt.getText().isBlank() || txtLecturaAct.getText().isBlank()) {
                    mostrarMensaje(Alert.AlertType.WARNING, "Campos incompletos", "Complete los datos del suministro y las lecturas.");
                    return;
                }
                try {
                    double ant = Double.parseDouble(txtLecturaAnt.getText().trim());
                    double act = Double.parseDouble(txtLecturaAct.getText().trim());
                    if (act < ant && cmbIncidencia.getValue().startsWith("NINGUNA")) {
                        mostrarMensaje(Alert.AlertType.ERROR, "Lectura inválida", "La lectura actual no puede ser menor a la anterior sin cambio documentado.");
                        return;
                    }
                    double consumo = Math.max(0, act - ant);
                    double total = ((consumo * 0.72) + 8.50) * 1.18;
                    String estado = cmbIncidencia.getValue().startsWith("NINGUNA") ? "LECTURA VALIDADA" : "INCIDENCIA REPORTADA";

                    ContextoSistema.listaLecturasRecibos.add(new String[]{
                            txtSuministro.getText().trim(), txtTitular.getText().trim(),
                            String.valueOf(ant), String.valueOf(act),
                            String.format("%.1f kWh", consumo), String.format("S/ %.2f", total), estado
                    });
                    txtSuministro.clear(); txtTitular.clear(); txtLecturaAnt.clear(); txtLecturaAct.clear();
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Lectura registrada", "Lectura registrada (" + estado + ").");
                } catch (NumberFormatException ex) {
                    mostrarMensaje(Alert.AlertType.WARNING, "Formato inválido", "Ingrese valores numéricos válidos.");
                }
            });
            botones.getChildren().add(btnRegistrar);
        }

        if ("Técnico Supervisor de Mediciones".equalsIgnoreCase(rol)
                || "Supervisor de Control de Mediciones".equalsIgnoreCase(rol)) {
            Button btnValidarLectura = new Button("Validar / Estimar Lectura");
            btnValidarLectura.setOnAction(e ->
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Lectura aprobada", "Lectura verificada y aprobada para pre-facturación.")
            );
            botones.getChildren().add(btnValidarLectura);
        }

        grid.add(botones, 0, 3, 4, 1);
        panel.setContent(grid);
        root.getChildren().addAll(lblTitulo, panel);
        return root;
    }

    // VISTA 2B: Pre-Facturación, Emisión y Reparto (Trabajadores) / Mis Recibos (Cliente)
    public Parent vistaPreFacturacionYReparto() {
        UsuarioDTO u = ContextoSistema.getUsuario();
        String rol = (u != null && u.nombreRol != null) ? u.nombreRol : "";
        boolean esCliente = "Cliente".equalsIgnoreCase(u != null ? u.categoriaRol : "");

        VBox root = new VBox(12);
        root.setPadding(new Insets(20, 30, 20, 30));

        Label lblTitulo = new Label(esCliente
                ? "Mis Recibos de Consumo Eléctrico"
                : "Pre-Facturación, Emisión de Recibos y Reparto");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 18));

        TableView<String[]> tabla = new TableView<>();
        String[] headers = {"Suministro", "Titular", "Lect. Ant.", "Lect. Act.", "Consumo", "Total (inc. IGV/FOSE)", "Estado Facturación / Reparto"};
        for (int i = 0; i < headers.length; i++) {
            final int idx = i;
            TableColumn<String[], String> col = new TableColumn<>(headers[i]);
            col.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[idx]));
            tabla.getColumns().add(col);
        }
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        if (esCliente) {
            var recibosCliente = ContextoSistema.listaLecturasRecibos.stream()
                    .filter(r -> (u.codigoSuministro != null && r[0].equalsIgnoreCase(u.codigoSuministro))
                            || r[1].equalsIgnoreCase(u.getNombreCompleto()))
                    .toList();
            tabla.setItems(FXCollections.observableArrayList(recibosCliente));
        } else {
            tabla.setItems(FXCollections.observableArrayList(ContextoSistema.listaLecturasRecibos));
        }

        FlowPane acciones = new FlowPane(12, 8);

        if ("Analista de Facturación".equalsIgnoreCase(rol)) {
            Button btnEmitir = new Button("Emitir Recibo Oficial");
            btnEmitir.setOnAction(e -> {
                String[] sel = tabla.getSelectionModel().getSelectedItem();
                if (sel == null) return;
                if (sel[6].contains("INCIDENCIA")) {
                    mostrarMensaje(Alert.AlertType.ERROR, "Emisión bloqueada", "No se puede emitir recibo con observaciones sin resolver.");
                    return;
                }
                sel[6] = "RECIBO EMITIDO - ASIGNADO A REPARTO";
                tabla.refresh();
                mostrarMensaje(Alert.AlertType.INFORMATION, "Recibo emitido", "Recibo emitido con cargo fijo, alumbrado, FOSE e IGV 18%.");
            });
            acciones.getChildren().add(btnEmitir);
        }

        if ("Contratista de Reparto".equalsIgnoreCase(rol)) {
            Button btnEntregar = new Button("Registrar Entrega en Ruta");
            btnEntregar.setOnAction(e -> {
                String[] sel = tabla.getSelectionModel().getSelectedItem();
                if (sel == null) return;
                sel[6] = "ENTREGADO AL CLIENTE (CARGO OK)";
                tabla.refresh();
            });
            acciones.getChildren().add(btnEntregar);
        }

        if ("Asistente de Facturación".equalsIgnoreCase(rol)) {
            Button btnFalloReparto = new Button("Reportar Fallo de Reparto");
            btnFalloReparto.setOnAction(e -> {
                String[] sel = tabla.getSelectionModel().getSelectedItem();
                if (sel == null) return;
                sel[6] = "REPARTO FALLIDO - REPROGRAMADO";
                tabla.refresh();
            });
            acciones.getChildren().add(btnFalloReparto);
        }

        root.getChildren().addAll(lblTitulo, tabla);
        if (!acciones.getChildren().isEmpty()) {
            root.getChildren().add(acciones);
        }
        return root;
    }

    private void mostrarMensaje(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}