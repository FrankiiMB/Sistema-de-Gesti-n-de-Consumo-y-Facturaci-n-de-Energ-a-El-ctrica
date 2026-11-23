package aplication;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class VistaProcesoAlta {

    // VISTA 1A: Formulario del Cliente con CheckBoxes de requisitos (HU-01 y RN-02)
    public Parent vistaPresentarSolicitud() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(25, 35, 25, 35));
        root.setStyle("-fx-background-color: #F4F7FA;");

        Label lblTitulo = new Label("Presentar solicitud de nuevo suministro (HU-01)");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 20));
        lblTitulo.setStyle("-fx-text-fill: #003B73;");

        Label lblSub = new Label("El propietario o inquilino con carta poder presenta su solicitud y marca los documentos adjuntos (RN-02).");
        lblSub.setStyle("-fx-text-fill: #52606D; -fx-font-size: 12px;");

        TitledPane panelDatos = new TitledPane();
        panelDatos.setText("Datos del solicitante y documentos");
        panelDatos.setCollapsible(false);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));
        grid.getColumnConstraints().addAll(new ColumnConstraints(220), new ColumnConstraints(320));

        ComboBox<String> cmbTipoPersona = new ComboBox<>(FXCollections.observableArrayList("NATURAL", "JURIDICA"));
        cmbTipoPersona.setValue("NATURAL");
        cmbTipoPersona.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> cmbTipoDoc = new ComboBox<>(FXCollections.observableArrayList("DNI", "RUC"));
        cmbTipoDoc.setValue("DNI");
        cmbTipoDoc.setMaxWidth(Double.MAX_VALUE);

        TextField txtNumDoc = new TextField();
        txtNumDoc.setPromptText("Ej. 46892310");

        TextField txtNombres = new TextField();
        txtNombres.setPromptText("Nombres y apellidos / Razón social");

        TextField txtDireccion = new TextField();
        txtDireccion.setPromptText("Dirección del predio (Ej. Av. Pacífico 320)");

        ComboBox<String> cmbCondicion = new ComboBox<>(FXCollections.observableArrayList("PROPIETARIO", "INQUILINO"));
        cmbCondicion.setValue("PROPIETARIO");
        cmbCondicion.setMaxWidth(Double.MAX_VALUE);

        CheckBox chkCarta = new CheckBox("Carta de solicitud");
        CheckBox chkAutovaluo = new CheckBox("Autovalúo y acreditación de propiedad");
        CheckBox chkCroquis = new CheckBox("Croquis de ubicación");
        CheckBox chkPoder = new CheckBox("Carta poder legalizada del propietario");
        CheckBox chkArrend = new CheckBox("Arrendamiento legalizado");
        CheckBox chkConst = new CheckBox("Constitución y poder del representante");

        VBox boxDocs = new VBox(6, chkCarta, chkAutovaluo, chkCroquis, chkPoder, chkArrend, chkConst);

        grid.add(new Label("Tipo de persona:"), 0, 0); grid.add(cmbTipoPersona, 1, 0);
        grid.add(new Label("Tipo de documento:"), 0, 1); grid.add(cmbTipoDoc, 1, 1);
        grid.add(new Label("N° de documento:"), 0, 2); grid.add(txtNumDoc, 1, 2);
        grid.add(new Label("Nombres / Razón social:"), 0, 3); grid.add(txtNombres, 1, 3);
        grid.add(new Label("Dirección del predio:"), 0, 4); grid.add(txtDireccion, 1, 4);
        grid.add(new Label("Condición del solicitante:"), 0, 5); grid.add(cmbCondicion, 1, 5);
        grid.add(new Label("Documentos que presenta:"), 0, 6); grid.add(boxDocs, 1, 6);

        panelDatos.setContent(grid);

        HBox botones = new HBox(12);
        Button btnEnviar = new Button("Enviar solicitud");
        btnEnviar.setStyle("-fx-background-color: #198754; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 9 20;");

        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.setStyle("-fx-background-color: #64748B; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 9 20;");

        btnLimpiar.setOnAction(e -> {
            txtNumDoc.clear(); txtNombres.clear(); txtDireccion.clear();
            chkCarta.setSelected(false); chkAutovaluo.setSelected(false); chkCroquis.setSelected(false);
            chkPoder.setSelected(false); chkArrend.setSelected(false); chkConst.setSelected(false);
        });

        btnEnviar.setOnAction(e -> {
            if (txtNumDoc.getText().isBlank() || txtNombres.getText().isBlank() || txtDireccion.getText().isBlank()) {
                new Alert(Alert.AlertType.WARNING, "Complete documento, nombre y dirección del predio.").showAndWait();
                return;
            }
            boolean baseOk = chkCarta.isSelected() && chkAutovaluo.isSelected() && chkCroquis.isSelected();
            boolean inqOk = !"INQUILINO".equals(cmbCondicion.getValue()) || (chkPoder.isSelected() && chkArrend.isSelected());
            String estado = (baseOk && inqOk) ? "PENDIENTE_REVISION" : "OBSERVADA_DOCS (RN-02)";
            String cod = String.format("SOL-%03d", ContextoDemo.listaSolicitudes.size() + 1);

            ContextoDemo.listaSolicitudes.add(new String[]{
                    cod, txtNombres.getText(), cmbTipoDoc.getValue() + ": " + txtNumDoc.getText(),
                    cmbCondicion.getValue(), txtDireccion.getText(), "NO (Libre)", estado
            });
            new Alert(Alert.AlertType.INFORMATION, "Solicitud " + cod + " registrada con estado: " + estado).showAndWait();
            btnLimpiar.fire();
        });

        botones.getChildren().addAll(btnEnviar, btnLimpiar);
        root.getChildren().addAll(lblTitulo, lblSub, panelDatos, botones);
        return root;
    }

    // VISTA 1B: Bandeja de Evaluación, Factibilidad, Contrato e Instalación (HU-02 a HU-07)
    public Parent vistaBandejaAlta() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(25, 35, 25, 35));
        root.setStyle("-fx-background-color: #F4F7FA;");

        Label lblTitulo = new Label("Bandeja de Alta de Nuevo Suministro (HU-02 a HU-07)");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 20));
        lblTitulo.setStyle("-fx-text-fill: #003B73;");

        TableView<String[]> tabla = new TableView<>();
        String[] headers = {"Solicitud", "Solicitante", "Documento", "Condición", "Predio", "Deuda Predio", "Estado Actual"};
        for (int i = 0; i < headers.length; i++) {
            final int idx = i;
            TableColumn<String[], String> col = new TableColumn<>(headers[i]);
            col.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[idx]));
            tabla.getColumns().add(col);
        }
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabla.setItems(FXCollections.observableArrayList(ContextoDemo.listaSolicitudes));

        HBox acciones = new HBox(10);
        ComboBox<String> cmbTarifa = new ComboBox<>(FXCollections.observableArrayList("BT5B Residencial (3.0 kW)", "BT5B Comercial (10.0 kW)"));
        cmbTarifa.setValue("BT5B Residencial (3.0 kW)");

        Button btnValidar = new Button("1. Validar Docs/Predio (Auxiliar)");
        btnValidar.setStyle("-fx-background-color: #004A80; -fx-text-fill: white; -fx-font-weight: bold;");

        Button btnFactibilidad = new Button("2. Aprobar Factibilidad (Sup. Com.)");
        btnFactibilidad.setStyle("-fx-background-color: #0D9488; -fx-text-fill: white; -fx-font-weight: bold;");

        Button btnContrato = new Button("3. Contrato y Cobro (HU-05)");
        btnContrato.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold;");

        Button btnInstalarYHabilitar = new Button("4. Instalar y Habilitar (HU-06/07)");
        btnInstalarYHabilitar.setStyle("-fx-background-color: #198754; -fx-text-fill: white; -fx-font-weight: bold;");

        btnValidar.setOnAction(e -> {
            String[] sel = tabla.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            if (sel[5].startsWith("SÍ")) {
                sel[6] = "RECHAZADA_POR_DEUDA (RN-04)";
                new Alert(Alert.AlertType.ERROR, "RN-04: Predio con deuda pendiente. Trámite bloqueado.").showAndWait();
            } else {
                sel[6] = "PENDIENTE_FACTIBILIDAD";
                new Alert(Alert.AlertType.INFORMATION, "HU-02/03: Documentos y predio conformes.").showAndWait();
            }
            tabla.refresh();
        });

        btnFactibilidad.setOnAction(e -> {
            String[] sel = tabla.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            sel[6] = "FACTIBILIDAD_APROBADA";
            tabla.refresh();
            new Alert(Alert.AlertType.INFORMATION, "HU-04: Factibilidad técnica aprobada.").showAndWait();
        });

        btnContrato.setOnAction(e -> {
            String[] sel = tabla.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            sel[6] = "CONTRATO_PAGADO (" + cmbTarifa.getValue() + ")";
            tabla.refresh();
            new Alert(Alert.AlertType.INFORMATION, "HU-05: Contrato firmado y presupuesto cobrado.").showAndWait();
        });

        btnInstalarYHabilitar.setOnAction(e -> {
            String[] sel = tabla.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            sel[6] = "SUMINISTRO_HABILITADO (Medidor Activo)";
            tabla.refresh();
            new Alert(Alert.AlertType.INFORMATION, "HU-06/07: Acta conforme. Suministro incorporado al cronograma mensual.").showAndWait();
        });

        acciones.getChildren().addAll(cmbTarifa, btnValidar, btnFactibilidad, btnContrato, btnInstalarYHabilitar);
        root.getChildren().addAll(lblTitulo, tabla, acciones);
        return root;
    }
}