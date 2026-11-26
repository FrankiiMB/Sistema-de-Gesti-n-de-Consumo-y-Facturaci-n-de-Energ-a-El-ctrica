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

public class VistaProcesoAlta {

    // VISTA 1A: Formulario del Cliente
    public Parent vistaPresentarSolicitud() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(20, 30, 20, 30));

        Label lblTitulo = new Label("Presentar solicitud de nuevo suministro");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 18));

        Label lblSub = new Label("El propietario o inquilino con carta poder presenta su solicitud y marca los documentos adjuntos.");

        TitledPane panelDatos = new TitledPane();
        panelDatos.setText("Datos del solicitante y documentos");
        panelDatos.setCollapsible(false);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));
        grid.getColumnConstraints().addAll(new ColumnConstraints(220), new ColumnConstraints(320));

        UsuarioDTO u = ContextoSistema.getUsuario();
        boolean esCliente = u != null && "Cliente".equalsIgnoreCase(u.categoriaRol);

        ComboBox<String> cmbTipoPersona = new ComboBox<>(FXCollections.observableArrayList("NATURAL", "JURIDICA"));
        cmbTipoPersona.setValue(esCliente && u.tipoPersona != null ? u.tipoPersona : "NATURAL");
        cmbTipoPersona.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> cmbTipoDoc = new ComboBox<>(FXCollections.observableArrayList("DNI", "RUC"));
        cmbTipoDoc.setValue(esCliente && u.tipoDocumento != null ? u.tipoDocumento : "DNI");
        cmbTipoDoc.setMaxWidth(Double.MAX_VALUE);

        TextField txtNumDoc = new TextField(esCliente && u.numeroDocumentoIdentidad != null ? u.numeroDocumentoIdentidad : "");
        txtNumDoc.setPromptText("Ej. 46892310");

        TextField txtNombres = new TextField(esCliente ? u.getNombreCompleto() : "");
        txtNombres.setPromptText("Nombres y apellidos / Razón social");

        TextField txtDireccion = new TextField(esCliente && u.direccion != null ? u.direccion : "");
        txtDireccion.setPromptText("Dirección del predio (Ej. Av. Pacífico 320)");

        ComboBox<String> cmbCondicion = new ComboBox<>(FXCollections.observableArrayList("PROPIETARIO", "INQUILINO"));
        cmbCondicion.setValue(esCliente && u.condicionCliente != null ? u.condicionCliente : "PROPIETARIO");
        cmbCondicion.setMaxWidth(Double.MAX_VALUE);

        if (esCliente) {
            cmbTipoPersona.setDisable(true);
            cmbTipoDoc.setDisable(true);
            txtNumDoc.setEditable(false);
            txtNombres.setEditable(false);
        }

        CheckBox chkCarta = new CheckBox("Carta de solicitud");
        CheckBox chkAutovaluo = new CheckBox("Autovalúo y acreditación de propiedad");
        CheckBox chkCroquis = new CheckBox("Croquis de ubicación");
        CheckBox chkPoder = new CheckBox("Carta poder legalizada del propietario");
        CheckBox chkArrend = new CheckBox("Arrendamiento legalizado");
        CheckBox chkConst = new CheckBox("Constitución y poder del representante");

        VBox boxDocs = new VBox(6, chkCarta, chkAutovaluo, chkCroquis, chkPoder, chkArrend, chkConst);

        grid.add(new Label("Tipo de persona:"), 0, 0);          grid.add(cmbTipoPersona, 1, 0);
        grid.add(new Label("Tipo de documento:"), 0, 1);        grid.add(cmbTipoDoc, 1, 1);
        grid.add(new Label("N° de documento:"), 0, 2);          grid.add(txtNumDoc, 1, 2);
        grid.add(new Label("Nombres / Razón social:"), 0, 3);   grid.add(txtNombres, 1, 3);
        grid.add(new Label("Dirección del predio:"), 0, 4);     grid.add(txtDireccion, 1, 4);
        grid.add(new Label("Condición del solicitante:"), 0, 5);grid.add(cmbCondicion, 1, 5);
        grid.add(new Label("Documentos que presenta:"), 0, 6);  grid.add(boxDocs, 1, 6);

        panelDatos.setContent(grid);

        FlowPane botones = new FlowPane(12, 8);
        Button btnEnviar = new Button("Enviar solicitud");
        Button btnLimpiar = new Button("Limpiar");

        btnLimpiar.setOnAction(e -> {
            if (!esCliente) {
                txtNumDoc.clear();
                txtNombres.clear();
            }
            txtDireccion.clear();
            chkCarta.setSelected(false); chkAutovaluo.setSelected(false); chkCroquis.setSelected(false);
            chkPoder.setSelected(false); chkArrend.setSelected(false); chkConst.setSelected(false);
        });

        btnEnviar.setOnAction(e -> {
            if (txtNumDoc.getText().isBlank() || txtNombres.getText().isBlank() || txtDireccion.getText().isBlank()) {
                mostrarMensaje(Alert.AlertType.WARNING, "Campos incompletos", "Complete documento, nombre y dirección del predio.");
                return;
            }
            boolean baseOk = chkCarta.isSelected() && chkAutovaluo.isSelected() && chkCroquis.isSelected();
            boolean inqOk = !"INQUILINO".equals(cmbCondicion.getValue()) || (chkPoder.isSelected() && chkArrend.isSelected());
            String estado = (baseOk && inqOk) ? "PENDIENTE_REVISION" : "OBSERVADA_DOCS";
            String cod = String.format("SOL-%03d", ContextoSistema.listaSolicitudes.size() + 1);

            ContextoSistema.listaSolicitudes.add(new String[]{
                    cod, txtNombres.getText(), cmbTipoDoc.getValue() + ": " + txtNumDoc.getText(),
                    cmbCondicion.getValue(), txtDireccion.getText(), "NO (Libre)", estado
            });
            mostrarMensaje(Alert.AlertType.INFORMATION, "Solicitud registrada", "Solicitud " + cod + " registrada con estado: " + estado);
            btnLimpiar.fire();
        });

        botones.getChildren().addAll(btnEnviar, btnLimpiar);
        root.getChildren().addAll(lblTitulo, lblSub, panelDatos, botones);
        return root;
    }

    // VISTA 1B: Estado de Solicitudes (Cliente) / Bandeja de Evaluación (Trabajadores según rol)
    public Parent vistaBandejaAlta() {
        UsuarioDTO u = ContextoSistema.getUsuario();
        String rol = (u != null && u.nombreRol != null) ? u.nombreRol : "";
        boolean esCliente = "Cliente".equalsIgnoreCase(u != null ? u.categoriaRol : "");
        boolean esAdmin = "Administrador".equalsIgnoreCase(rol);

        VBox root = new VBox(12);
        root.setPadding(new Insets(20, 30, 20, 30));

        Label lblTitulo = new Label(esCliente
                ? "Estado de Mis Solicitudes de Suministro"
                : "Bandeja de Alta de Nuevo Suministro");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 18));

        Label lblSub = new Label(esCliente
                ? "Consulte el avance de su trámite. Si su expediente presenta observaciones, puede subsanar los documentos."
                : "Operaciones habilitadas estrictamente para el rol: " + rol);

        TableView<String[]> tabla = new TableView<>();
        String[] headers = {"Solicitud", "Solicitante", "Documento", "Condición", "Predio", "Deuda Predio", "Estado Actual"};
        for (int i = 0; i < headers.length; i++) {
            final int idx = i;
            TableColumn<String[], String> col = new TableColumn<>(headers[i]);
            col.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[idx]));
            tabla.getColumns().add(col);
        }
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        if (esCliente && u.numeroDocumentoIdentidad != null) {
            var propias = ContextoSistema.listaSolicitudes.stream()
                    .filter(fila -> fila[2] != null && fila[2].contains(u.numeroDocumentoIdentidad))
                    .toList();
            tabla.setItems(FXCollections.observableArrayList(propias));
        } else {
            tabla.setItems(FXCollections.observableArrayList(ContextoSistema.listaSolicitudes));
        }

        FlowPane acciones = new FlowPane(10, 8);

        if (esCliente) {
            Button btnSubsanar = new Button("Subsanar Documentos Observados");
            btnSubsanar.setOnAction(e -> {
                String[] sel = tabla.getSelectionModel().getSelectedItem();
                if (sel == null) {
                    mostrarMensaje(Alert.AlertType.WARNING, "Selección requerida", "Seleccione una solicitud de la tabla.");
                    return;
                }
                if (!sel[6].contains("OBSERVADA")) {
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Sin observaciones", "Esta solicitud no tiene observaciones pendientes de subsanación.");
                    return;
                }
                sel[6] = "PENDIENTE_REVISION (Subsanado)";
                tabla.refresh();
                mostrarMensaje(Alert.AlertType.INFORMATION, "Subsanación enviada", "Documentos corregidos enviados al Auxiliar Comercial.");
            });
            acciones.getChildren().add(btnSubsanar);
        }

        if (esAdmin || "Auxiliar Comercial".equalsIgnoreCase(rol)) {
            Button btnValidar = new Button("1. Validar Docs y Predio");
            btnValidar.setOnAction(e -> {
                String[] sel = tabla.getSelectionModel().getSelectedItem();
                if (sel == null) return;
                if (sel[5].startsWith("SÍ")) {
                    sel[6] = "RECHAZADA_POR_DEUDA";
                    mostrarMensaje(Alert.AlertType.ERROR, "Trámite bloqueado", "Predio con deuda pendiente.");
                } else {
                    sel[6] = "PENDIENTE_FACTIBILIDAD";
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Validación conforme", "Documentos y predio conformes.");
                }
                tabla.refresh();
            });

            ComboBox<String> cmbTarifa = new ComboBox<>(FXCollections.observableArrayList("BT5B Residencial (3.0 kW)", "BT5B Comercial (10.0 kW)"));
            cmbTarifa.setValue("BT5B Residencial (3.0 kW)");

            Button btnContrato = new Button("3. Contratación y Cobro");
            btnContrato.setOnAction(e -> {
                String[] sel = tabla.getSelectionModel().getSelectedItem();
                if (sel == null) return;
                if (!sel[6].contains("FACTIBILIDAD_APROBADA")) {
                    mostrarMensaje(Alert.AlertType.WARNING, "Requisito previo", "Requiere factibilidad técnica aprobada previamente.");
                    return;
                }
                sel[6] = "CONTRATO_PAGADO (" + cmbTarifa.getValue() + ")";
                tabla.refresh();
                mostrarMensaje(Alert.AlertType.INFORMATION, "Contratación completada", "Contrato firmado y presupuesto cobrado.");
            });

            acciones.getChildren().addAll(btnValidar, cmbTarifa, btnContrato);
        }

        if (esAdmin || "Supervisor de Control Comercial".equalsIgnoreCase(rol)) {
            Button btnFactibilidad = new Button("2. Evaluar y Aprobar Factibilidad");
            btnFactibilidad.setOnAction(e -> {
                String[] sel = tabla.getSelectionModel().getSelectedItem();
                if (sel == null) return;
                sel[6] = "FACTIBILIDAD_APROBADA";
                tabla.refresh();
                mostrarMensaje(Alert.AlertType.INFORMATION, "Factibilidad aprobada", "Factibilidad técnica aprobada.");
            });
            acciones.getChildren().add(btnFactibilidad);
        }

        if (esAdmin || "Supervisor de Control de Mediciones".equalsIgnoreCase(rol)) {
            Button btnProgInst = new Button("4A. Programar Instalación Medidor");
            btnProgInst.setOnAction(e -> {
                String[] sel = tabla.getSelectionModel().getSelectedItem();
                if (sel == null) return;
                if (!sel[6].contains("CONTRATO_PAGADO")) {
                    mostrarMensaje(Alert.AlertType.WARNING, "Pago pendiente", "No se puede programar obra sin presupuesto cobrado.");
                    return;
                }
                sel[6] = "INSTALACION_PROGRAMADA";
                tabla.refresh();
                mostrarMensaje(Alert.AlertType.INFORMATION, "Instalación programada", "Orden enviada al Contratista de instalación.");
            });
            acciones.getChildren().add(btnProgInst);
        }

        if (esAdmin || "Contratista de instalación".equalsIgnoreCase(rol)) {
            Button btnEjecInst = new Button("4B. Instalar Acometida/Medidor y Enviar Acta");
            btnEjecInst.setOnAction(e -> {
                String[] sel = tabla.getSelectionModel().getSelectedItem();
                if (sel == null) return;
                sel[6] = "ACTA_INSTALACION_ENVIADA";
                tabla.refresh();
                mostrarMensaje(Alert.AlertType.INFORMATION, "Acta enviada", "Acta de instalación remitida para inspección.");
            });
            acciones.getChildren().add(btnEjecInst);
        }

        if (esAdmin || "Técnico Supervisor de Mediciones".equalsIgnoreCase(rol)) {
            Button btnHabilitar = new Button("4C. Inspeccionar y Habilitar Suministro");
            btnHabilitar.setOnAction(e -> {
                String[] sel = tabla.getSelectionModel().getSelectedItem();
                if (sel == null) return;
                sel[6] = "SUMINISTRO_HABILITADO (Medidor Activo)";
                tabla.refresh();
                mostrarMensaje(Alert.AlertType.INFORMATION, "Suministro habilitado", "Instalación conforme. Suministro incorporado al cronograma mensual.");
            });
            acciones.getChildren().add(btnHabilitar);
        }

        root.getChildren().addAll(lblTitulo, lblSub, tabla);
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