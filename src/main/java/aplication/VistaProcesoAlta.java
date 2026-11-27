package aplication;

import altanuevosuministro.*;
import database.AltaSuministroDAO;
import database.AltaSuministroDAO.SolicitudFila;
import database.UsuarioDAO.UsuarioDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VistaProcesoAlta {

    private final AltaSuministroDAO altaDAO = new AltaSuministroDAO();

    // =========================================================================
    // VISTA 1A: Presentar Solicitud de Nuevo Suministro (HU-01)
    // =========================================================================
    public Parent vistaPresentarSolicitud() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(20, 30, 20, 30));

        Label lblTitulo = new Label("Presentar solicitud de nuevo suministro");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 18));

        Label lblSub = new Label("El propietario o inquilino con carta poder presenta su solicitud y marca los documentos adjuntos (RN-01 / RN-02).");

        TitledPane panelDatos = new TitledPane();
        panelDatos.setText("Datos del solicitante, predio y documentos");
        panelDatos.setCollapsible(false);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));
        grid.getColumnConstraints().addAll(new ColumnConstraints(230), new ColumnConstraints(340));

        UsuarioDTO u = ContextoSistema.getUsuario();
        boolean esCliente = u != null && "Cliente".equalsIgnoreCase(u.categoriaRol);

        ComboBox<TipoPersona> cmbTipoPersona = new ComboBox<>(FXCollections.observableArrayList(TipoPersona.values()));
        cmbTipoPersona.setValue(esCliente && "JURIDICA".equalsIgnoreCase(u.tipoPersona) ? TipoPersona.JURIDICA : TipoPersona.NATURAL);
        cmbTipoPersona.setMaxWidth(Double.MAX_VALUE);

        ComboBox<TipoDocumento> cmbTipoDoc = new ComboBox<>(FXCollections.observableArrayList(TipoDocumento.DNI, TipoDocumento.RUC));
        cmbTipoDoc.setValue(esCliente && "RUC".equalsIgnoreCase(u.tipoDocumento) ? TipoDocumento.RUC : TipoDocumento.DNI);
        cmbTipoDoc.setMaxWidth(Double.MAX_VALUE);

        TextField txtNumDoc = new TextField(esCliente && u.numeroDocumentoIdentidad != null ? u.numeroDocumentoIdentidad : "");
        txtNumDoc.setPromptText("Ej. 87654321");

        DatePicker dpVigenciaDoc = new DatePicker(LocalDate.now().plusYears(3));
        dpVigenciaDoc.setMaxWidth(Double.MAX_VALUE);

        TextField txtNombres = new TextField(esCliente && u.nombres != null ? u.nombres : "");
        txtNombres.setPromptText("Nombres o Razón social");

        TextField txtApellidos = new TextField(esCliente && u.apellidos != null ? u.apellidos : "");
        txtApellidos.setPromptText("Apellidos");

        TextField txtDireccion = new TextField(esCliente && u.direccion != null ? u.direccion : "");
        txtDireccion.setPromptText("Dirección del predio");

        ComboBox<CondicionCliente> cmbCondicion = new ComboBox<>(FXCollections.observableArrayList(CondicionCliente.values()));
        cmbCondicion.setValue(esCliente && "INQUILINO".equalsIgnoreCase(u.condicionCliente)
                ? CondicionCliente.INQUILINO : CondicionCliente.PROPIETARIO);
        cmbCondicion.setMaxWidth(Double.MAX_VALUE);

        if (esCliente) {
            cmbTipoPersona.setDisable(true);
            cmbTipoDoc.setDisable(true);
            txtNumDoc.setEditable(false);
            txtNombres.setEditable(false);
            txtApellidos.setEditable(false);
        }

        CheckBox chkCarta = new CheckBox("Carta de solicitud");
        CheckBox chkTitulo = new CheckBox("Autovalúo y acreditación de propiedad");
        CheckBox chkCroquis = new CheckBox("Croquis de ubicación");
        CheckBox chkPoder = new CheckBox("Carta poder legalizada del propietario (Obligatorio inquilino)");
        CheckBox chkArrend = new CheckBox("Contrato de arrendamiento legalizado (Obligatorio inquilino)");

        VBox boxDocs = new VBox(6, chkCarta, chkTitulo, chkCroquis, chkPoder, chkArrend);

        grid.add(new Label("Tipo de persona:"), 0, 0);          grid.add(cmbTipoPersona, 1, 0);
        grid.add(new Label("Tipo de documento:"), 0, 1);        grid.add(cmbTipoDoc, 1, 1);
        grid.add(new Label("N° de documento:"), 0, 2);          grid.add(txtNumDoc, 1, 2);
        grid.add(new Label("Vigencia documento hasta:"), 0, 3); grid.add(dpVigenciaDoc, 1, 3);
        grid.add(new Label("Nombres / Razón social:"), 0, 4);   grid.add(txtNombres, 1, 4);
        grid.add(new Label("Apellidos:"), 0, 5);                grid.add(txtApellidos, 1, 5);
        grid.add(new Label("Dirección del predio:"), 0, 6);     grid.add(txtDireccion, 1, 6);
        grid.add(new Label("Condición del solicitante:"), 0, 7);grid.add(cmbCondicion, 1, 7);
        grid.add(new Label("Documentos que presenta:"), 0, 8);  grid.add(boxDocs, 1, 8);

        panelDatos.setContent(grid);

        FlowPane botones = new FlowPane(12, 8);
        Button btnEnviar = new Button("Enviar solicitud");
        Button btnLimpiar = new Button("Limpiar");

        btnLimpiar.setOnAction(e -> {
            if (!esCliente) {
                txtNumDoc.clear();
                txtNombres.clear();
                txtApellidos.clear();
            }
            txtDireccion.clear();
            dpVigenciaDoc.setValue(LocalDate.now().plusYears(3));
            chkCarta.setSelected(false);
            chkTitulo.setSelected(false);
            chkCroquis.setSelected(false);
            chkPoder.setSelected(false);
            chkArrend.setSelected(false);
        });

        btnEnviar.setOnAction(e -> {
            if (txtNumDoc.getText().isBlank() || txtNombres.getText().isBlank() || txtDireccion.getText().isBlank()) {
                mostrarMensaje(Alert.AlertType.WARNING, "Campos incompletos", "Complete documento, nombre y dirección del predio.");
                return;
            }

            Cliente cliente = new Cliente(
                    txtNombres.getText().trim(),
                    txtApellidos.getText().trim(),
                    cmbTipoDoc.getValue(),
                    txtNumDoc.getText().trim(),
                    txtDireccion.getText().trim(),
                    u != null && u.telefono != null ? u.telefono : "",
                    cmbTipoPersona.getValue(),
                    u != null && u.correo != null ? u.correo : "",
                    cmbCondicion.getValue()
            );
            cliente.setCartaPoderLegalizada(chkPoder.isSelected());
            cliente.setContratoArrendamientoLegalizado(chkArrend.isSelected());

            String codPredio = "PRED-" + (int) (Math.random() * 90000 + 10000);
            Predio predio = new Predio(codPredio, txtDireccion.getText().trim(), false, false);

            List<Documento> docs = new ArrayList<>();
            boolean docVigente = dpVigenciaDoc.getValue() != null && !dpVigenciaDoc.getValue().isBefore(LocalDate.now());
            docs.add(new Documento(cmbTipoDoc.getValue(), docVigente ? "PRESENTADO" : "OBSERVADO", txtNumDoc.getText().trim()));

            if (chkCarta.isSelected())  docs.add(new Documento(TipoDocumento.CARTA_SOLICITUD, "PRESENTADO", "carta_solicitud.pdf"));
            if (chkTitulo.isSelected()) docs.add(new Documento(TipoDocumento.TITULO_PROPIEDAD, "PRESENTADO", "titulo_autovaluo.pdf"));
            if (chkCroquis.isSelected())docs.add(new Documento(TipoDocumento.CROQUIS, "PRESENTADO", "croquis.pdf"));
            if (chkPoder.isSelected())  docs.add(new Documento(TipoDocumento.CARTA_PODER, "PRESENTADO", "carta_poder.pdf"));
            if (chkArrend.isSelected()) docs.add(new Documento(TipoDocumento.CONTRATO_ARRENDAMIENTO, "PRESENTADO", "arrendamiento.pdf"));

            SolicitudSuministro solicitud = cliente.solicitarSuministro(predio, docs);

            try {
                altaDAO.registrarSolicitud(solicitud, esCliente ? u.idPersona : 0);
                mostrarMensaje(Alert.AlertType.INFORMATION, "Solicitud enviada",
                        "Solicitud " + solicitud.getIdSolicitud() + " registrada.\n\nSolicitud enviada a: Auxiliar Comercial");
                btnLimpiar.fire();
            } catch (SQLException ex) {
                mostrarMensaje(Alert.AlertType.ERROR, "Error SQL Server", "No se pudo guardar la solicitud: " + ex.getMessage());
            }
        });

        botones.getChildren().addAll(btnEnviar, btnLimpiar);
        root.getChildren().addAll(lblTitulo, lblSub, panelDatos, botones);
        return root;
    }

    // =========================================================================
    // VISTA 1B: Bandeja de Alta de Nuevo Suministro (HU-01 a HU-07)
    // =========================================================================
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
        root.getChildren().add(lblTitulo);

        TableView<SolicitudFila> tabla = new TableView<>();
        tabla.setPrefHeight(260);

        TableColumn<SolicitudFila, String> colCod = new TableColumn<>("Solicitud");
        colCod.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().solicitud.getIdSolicitud()));

        TableColumn<SolicitudFila, String> colSol = new TableColumn<>("Solicitante");
        colSol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().solicitud.getCliente().getNombreCompleto().trim()));

        TableColumn<SolicitudFila, String> colDoc = new TableColumn<>("Documento");
        colDoc.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().solicitud.getCliente().getTipoDocumento() + ": " + d.getValue().solicitud.getCliente().getNumeroDocumentoIdentidad()));

        TableColumn<SolicitudFila, String> colCond = new TableColumn<>("Condición");
        colCond.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().solicitud.getCliente().getTipoCliente().name()));

        TableColumn<SolicitudFila, String> colPredio = new TableColumn<>("Predio");
        colPredio.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().solicitud.getPredio().getDireccion()));

        TableColumn<SolicitudFila, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().solicitud.getEstado().name()));

        TableColumn<SolicitudFila, String> colEtapa = new TableColumn<>("Etapa / Observación");
        colEtapa.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().solicitud.getDetalleEtapa()));

        tabla.getColumns().addAll(colCod, colSol, colDoc, colCond, colPredio, colEstado, colEtapa);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // Deseleccionar al volver a hacer clic sobre la fila ya seleccionada
        tabla.setRowFactory(tv -> {
            TableRow<SolicitudFila> row = new TableRow<>();
            row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                final int index = row.getIndex();
                if (!row.isEmpty() && index >= 0 && index < tabla.getItems().size()
                        && tabla.getSelectionModel().isSelected(index)) {
                    tabla.getSelectionModel().clearSelection();
                    event.consume();
                }
            });
            return row;
        });

        // =====================================================================
        // PANEL DE DETALLE CON ESTILO NATIVO JAVAFX (Oculto sin selección)
        // =====================================================================
        TitledPane panelDetalle = new TitledPane();
        panelDetalle.setText("Resumen de Inspección");
        panelDetalle.setCollapsible(false);
        panelDetalle.setVisible(false);
        panelDetalle.setManaged(false);

        TitledPane paneDocs = new TitledPane();
        paneDocs.setText("Expediente y Documentación Requerida");
        paneDocs.setCollapsible(false);
        HBox.setHgrow(paneDocs, Priority.ALWAYS);

        VBox listaDocsVisual = new VBox(6);
        listaDocsVisual.setPadding(new Insets(10, 12, 10, 12));
        paneDocs.setContent(listaDocsVisual);

        TitledPane panePredio = new TitledPane();
        panePredio.setText("Verificación del Predio");
        panePredio.setCollapsible(false);
        panePredio.setPrefWidth(310);

        GridPane gridPredio = new GridPane();
        gridPredio.setHgap(12);
        gridPredio.setVgap(6);
        gridPredio.setPadding(new Insets(10, 12, 10, 12));

        Label valCodPredio = new Label();
        Label valDeudaPredio = new Label();
        Label valSumActivo = new Label();
        Label valFactibilidad = new Label();

        gridPredio.add(new Label("Código Predio:"), 0, 0);     gridPredio.add(valCodPredio, 1, 0);
        gridPredio.add(new Label("Deuda Pendiente:"), 0, 1);   gridPredio.add(valDeudaPredio, 1, 1);
        gridPredio.add(new Label("Suministro Activo:"), 0, 2); gridPredio.add(valSumActivo, 1, 2);
        gridPredio.add(new Label("Factibilidad:"), 0, 3);      gridPredio.add(valFactibilidad, 1, 3);
        panePredio.setContent(gridPredio);

        HBox contenedorSubpaneles = new HBox(14, paneDocs, panePredio);
        contenedorSubpaneles.setPadding(new Insets(10));
        panelDetalle.setContent(contenedorSubpaneles);

        FlowPane acciones = new FlowPane(10, 8);

        Button btnSubsanar = new Button("Subsanar Documentos Observados");
        Button btnValidar = new Button("1. Validar Docs y Predio");
        Button btnObservarManual = new Button("Observar Expediente");
        ComboBox<String> cmbTarifa = new ComboBox<>(FXCollections.observableArrayList("BT5B Residencial", "BT5B Comercial"));
        cmbTarifa.setValue("BT5B Residencial");
        TextField txtPotencia = new TextField("3.0");
        txtPotencia.setPrefWidth(60);
        TextField txtPropietarioLegal = new TextField();
        txtPropietarioLegal.setPrefWidth(175);
        txtPropietarioLegal.setPromptText("Propietario (si es inquilino)");
        Button btnContrato = new Button("3. Firmar Contrato y Cobrar");

        Button btnFactOk = new Button("2A. Aprobar Factibilidad");
        Button btnFactNo = new Button("2B. Rechazar Factibilidad");
        Button btnProgInst = new Button("4A. Programar Instalación Medidor");
        Button btnEjecInst = new Button("4B. Instalar Acometida/Medidor y Enviar Acta");
        Button btnHabilitar = new Button("4C. Inspeccionar Conforme y Habilitar");
        Button btnNoConforme = new Button("4C. Observar Obra (Reprogramar)");

        Runnable actualizarEstadoControles = () -> {
            SolicitudFila fila = tabla.getSelectionModel().getSelectedItem();

            if (fila == null) {
                panelDetalle.setVisible(false);
                panelDetalle.setManaged(false);

                btnSubsanar.setDisable(true);
                btnValidar.setDisable(true);
                btnObservarManual.setDisable(true);
                cmbTarifa.setDisable(true);
                txtPotencia.setDisable(true);
                txtPropietarioLegal.setDisable(true);
                btnContrato.setDisable(true);
                btnFactOk.setDisable(true);
                btnFactNo.setDisable(true);
                btnProgInst.setDisable(true);
                btnEjecInst.setDisable(true);
                btnHabilitar.setDisable(true);
                btnNoConforme.setDisable(true);
                return;
            }

            SolicitudSuministro sol = fila.solicitud;
            Cliente cli = sol.getCliente();
            Expediente exp = sol.getExpediente();
            Predio pre = sol.getPredio();
            EstadoSolicitud est = sol.getEstado();
            String etapa = sol.getDetalleEtapa() != null ? sol.getDetalleEtapa() : "";

            panelDetalle.setText("Resumen de Inspección — " + sol.getIdSolicitud()
                    + "  |  Expediente: " + exp.getIdExpediente()
                    + "  |  Condición: " + cli.getTipoCliente().name());

            listaDocsVisual.getChildren().clear();
            List<TipoDocumento> requeridos = new ArrayList<>();
            requeridos.add(cli.getTipoDocumento());
            requeridos.add(TipoDocumento.CARTA_SOLICITUD);
            requeridos.add(TipoDocumento.TITULO_PROPIEDAD);
            requeridos.add(TipoDocumento.CROQUIS);
            if (cli.getTipoCliente() == CondicionCliente.INQUILINO) {
                requeridos.add(TipoDocumento.CARTA_PODER);
                requeridos.add(TipoDocumento.CONTRATO_ARRENDAMIENTO);
            }

            for (TipoDocumento req : requeridos) {
                Optional<Documento> docOpt = exp.getDocumentos().stream()
                        .filter(d -> d.getTipo() == req)
                        .findFirst();

                HBox itemDoc = new HBox(8);
                itemDoc.setAlignment(Pos.CENTER_LEFT);

                Label lblIcono = new Label();
                Label lblNombreDoc = new Label(formatearNombreDocumento(req));
                Label lblEstadoDoc = new Label();

                if (docOpt.isPresent()) {
                    Documento doc = docOpt.get();
                    if (doc.validar()) {
                        lblIcono.setText("✔");
                        lblIcono.setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold;");
                        lblEstadoDoc.setText("Presentado");
                        lblEstadoDoc.setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold;");
                    } else {
                        lblIcono.setText("⚠");
                        lblIcono.setStyle("-fx-text-fill: #D97706; -fx-font-weight: bold;");
                        lblEstadoDoc.setText("Observado / Vencido");
                        lblEstadoDoc.setStyle("-fx-text-fill: #D97706; -fx-font-weight: bold;");
                    }
                } else {
                    lblIcono.setText("✖");
                    lblIcono.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
                    lblEstadoDoc.setText("Faltante");
                    lblEstadoDoc.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
                }

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                itemDoc.getChildren().addAll(lblIcono, lblNombreDoc, spacer, lblEstadoDoc);
                listaDocsVisual.getChildren().add(itemDoc);
            }

            valCodPredio.setText(pre.getCodigoPredio());
            valCodPredio.setStyle("-fx-font-weight: bold;");

            valDeudaPredio.setText(pre.isTieneDeudaPendiente() ? "SÍ (Bloqueado)" : "No registra");
            valDeudaPredio.setStyle(pre.isTieneDeudaPendiente()
                    ? "-fx-text-fill: #DC2626; -fx-font-weight: bold;"
                    : "-fx-text-fill: #16A34A; -fx-font-weight: bold;");

            valSumActivo.setText(pre.isTieneSuministroActivo() ? "SÍ (Ya activo)" : "Ninguno");
            valSumActivo.setStyle(pre.isTieneSuministroActivo()
                    ? "-fx-text-fill: #DC2626; -fx-font-weight: bold;"
                    : "-fx-text-fill: #16A34A; -fx-font-weight: bold;");

            valFactibilidad.setText(pre.isEsFactible() ? "Viable" : "No viable");
            valFactibilidad.setStyle(pre.isEsFactible()
                    ? "-fx-text-fill: #0284C7; -fx-font-weight: bold;"
                    : "-fx-text-fill: #DC2626; -fx-font-weight: bold;");

            panelDetalle.setVisible(true);
            panelDetalle.setManaged(true);

            // Reglas de habilitación exacta por etapa
            boolean sinSuministroAsignado = cli.getCodigoSuministro() == null || cli.getCodigoSuministro().isBlank();
            boolean enRevisionAuxiliar = est == EstadoSolicitud.PENDIENTE
                    && (etapa.startsWith("1.") || etapa.startsWith("1B."));
            boolean enFactibilidad = est == EstadoSolicitud.PENDIENTE && etapa.startsWith("2.");
            boolean listoParaContrato = est == EstadoSolicitud.APROBADA && fila.idContrato == null;
            boolean listoParaProgramar = est == EstadoSolicitud.APROBADA && fila.idContrato != null
                    && (fila.idOrdenInstalacion == null || "NO_CONFORME".equals(fila.estadoInstalacion));
            boolean listoParaInstalar = fila.idOrdenInstalacion != null
                    && "PROGRAMADA".equals(fila.estadoInstalacion) && fila.idActa == null;
            boolean listoParaInspeccion = fila.idOrdenInstalacion != null && fila.idActa != null
                    && ("EJECUTADA".equals(fila.estadoInstalacion) || sinSuministroAsignado);

            btnSubsanar.setDisable(est != EstadoSolicitud.OBSERVADA);

            btnValidar.setDisable(!enRevisionAuxiliar);
            btnObservarManual.setDisable(!enRevisionAuxiliar);

            cmbTarifa.setDisable(!listoParaContrato);
            txtPotencia.setDisable(!listoParaContrato);
            txtPropietarioLegal.setDisable(!listoParaContrato || cli.getTipoCliente() != CondicionCliente.INQUILINO);
            btnContrato.setDisable(!listoParaContrato);

            btnFactOk.setDisable(!enFactibilidad);
            btnFactNo.setDisable(!enFactibilidad);

            btnProgInst.setDisable(!listoParaProgramar);
            btnEjecInst.setDisable(!listoParaInstalar);

            btnHabilitar.setDisable(!listoParaInspeccion);
            btnNoConforme.setDisable(!listoParaInspeccion);
        };

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, ant, nue) -> actualizarEstadoControles.run());

        Runnable recargarTabla = () -> {
            String filtro = (esCliente && u != null) ? u.numeroDocumentoIdentidad : null;
            tabla.setItems(FXCollections.observableArrayList(altaDAO.listarSolicitudes(filtro)));
            tabla.getSelectionModel().clearSelection();
            actualizarEstadoControles.run();
        };
        recargarTabla.run();

        // CLIENTE: Subsanar documentos observados
        if (esCliente) {
            btnSubsanar.setOnAction(e -> {
                SolicitudFila fila = tabla.getSelectionModel().getSelectedItem();
                if (fila == null) return;
                SolicitudSuministro sel = fila.solicitud;
                Cliente c = sel.getCliente();
                List<Documento> corregidos = new ArrayList<>();
                corregidos.add(new Documento(c.getTipoDocumento(), "PRESENTADO", c.getNumeroDocumentoIdentidad()));
                corregidos.add(new Documento(TipoDocumento.CARTA_SOLICITUD, "PRESENTADO", "carta_subsanada.pdf"));
                corregidos.add(new Documento(TipoDocumento.TITULO_PROPIEDAD, "PRESENTADO", "titulo_subsanado.pdf"));
                corregidos.add(new Documento(TipoDocumento.CROQUIS, "PRESENTADO", "croquis_subsanado.pdf"));
                if (c.getTipoCliente() == CondicionCliente.INQUILINO) {
                    corregidos.add(new Documento(TipoDocumento.CARTA_PODER, "PRESENTADO", "poder_subsanado.pdf"));
                    corregidos.add(new Documento(TipoDocumento.CONTRATO_ARRENDAMIENTO, "PRESENTADO", "arrend_subsanado.pdf"));
                }
                try {
                    altaDAO.subsanarDocumentos(sel, corregidos);
                    recargarTabla.run();
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Subsanación enviada",
                            "Documentos corregidos registrados.\n\nSolicitud enviada a: Auxiliar Comercial");
                } catch (SQLException ex) {
                    mostrarMensaje(Alert.AlertType.ERROR, "Error SQL Server", ex.getMessage());
                }
            });
            acciones.getChildren().add(btnSubsanar);
        }

        // AUXILIAR COMERCIAL
        if (esAdmin || "Auxiliar Comercial".equalsIgnoreCase(rol)) {
            AuxiliarComercial auxiliar = new AuxiliarComercial(
                    u != null && u.codigoUsuario != null ? u.codigoUsuario : "AXC-001", "Auxiliar Comercial");

            btnValidar.setOnAction(e -> {
                SolicitudFila fila = tabla.getSelectionModel().getSelectedItem();
                if (fila == null) return;
                SolicitudSuministro sel = fila.solicitud;
                Cliente cli = sel.getCliente();
                Expediente exp = sel.getExpediente();

                try {
                    boolean docIdentidadValido = cli.validarDocumento() && exp.getDocumentos().stream()
                            .anyMatch(d -> d.getTipo() == cli.getTipoDocumento() && d.validar());

                    if (!docIdentidadValido) {
                        altaDAO.actualizarEstadoYFactibilidad(sel.getIdSolicitud(), sel.getPredio().getCodigoPredio(),
                                EstadoSolicitud.OBSERVADA, "OBSERVADA (RN-01): DNI/RUC inválido o vencido.", null, false);
                        recargarTabla.run();
                        mostrarMensaje(Alert.AlertType.WARNING, "Solicitud observada (RN-01)",
                                "Documento de identidad inválido o vencido.\n\nSolicitud devuelta a: Cliente / Titular del suministro");
                        return;
                    }

                    if (!auxiliar.verificarDocumentacionRequisitos(exp, cli.getTipoCliente())) {
                        altaDAO.actualizarEstadoYFactibilidad(sel.getIdSolicitud(), sel.getPredio().getCodigoPredio(),
                                EstadoSolicitud.OBSERVADA, "OBSERVADA (RN-02/03): Faltan requisitos de " + cli.getTipoCliente(), null, false);
                        recargarTabla.run();
                        mostrarMensaje(Alert.AlertType.WARNING, "Solicitud observada (RN-02 / RN-03)",
                                "Faltan documentos obligatorios para " + cli.getTipoCliente() + ".\n\nSolicitud devuelta a: Cliente / Titular del suministro");
                        return;
                    }

                    if (!auxiliar.verificarPredio(sel.getPredio())) {
                        altaDAO.actualizarEstadoYFactibilidad(sel.getIdSolicitud(), sel.getPredio().getCodigoPredio(),
                                EstadoSolicitud.RECHAZADA, "RECHAZADA (RN-04): Predio con deuda pendiente o suministro activo.", null, true);
                        recargarTabla.run();
                        mostrarMensaje(Alert.AlertType.ERROR, "Solicitud rechazada (RN-04)",
                                "Predio con deuda pendiente o suministro activo.\n\nNotificación enviada a: Cliente / Titular del suministro");
                        return;
                    }

                    altaDAO.actualizarEstadoYFactibilidad(sel.getIdSolicitud(), sel.getPredio().getCodigoPredio(),
                            EstadoSolicitud.PENDIENTE, "2. Expediente conforme - Derivado a Factibilidad Técnica", null, true);
                    recargarTabla.run();
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Expediente conforme",
                            "Documentación y predio validados.\n\nSolicitud enviada a: Supervisor de Control Comercial");
                } catch (SQLException ex) {
                    mostrarMensaje(Alert.AlertType.ERROR, "Error SQL Server", ex.getMessage());
                }
            });

            btnObservarManual.setOnAction(e -> {
                SolicitudFila fila = tabla.getSelectionModel().getSelectedItem();
                if (fila == null) return;
                SolicitudSuministro sel = fila.solicitud;

                TextInputDialog dialog = new TextInputDialog("Documento ilegible o incompleto");
                dialog.setTitle("Observar Documentación");
                dialog.setHeaderText("Solicitar subsanación para " + sel.getIdSolicitud());
                dialog.setContentText("Motivo de la observación:");

                Optional<String> motivoOpt = dialog.showAndWait();
                if (motivoOpt.isPresent() && !motivoOpt.get().isBlank()) {
                    try {
                        altaDAO.actualizarEstadoYFactibilidad(sel.getIdSolicitud(), sel.getPredio().getCodigoPredio(),
                                EstadoSolicitud.OBSERVADA, "OBSERVADA: " + motivoOpt.get().trim(), null, false);
                        recargarTabla.run();
                        mostrarMensaje(Alert.AlertType.WARNING, "Subsanación solicitada",
                                "Observación registrada.\n\nSolicitud devuelta a: Cliente / Titular del suministro");
                    } catch (SQLException ex) {
                        mostrarMensaje(Alert.AlertType.ERROR, "Error SQL Server", ex.getMessage());
                    }
                }
            });

            btnContrato.setOnAction(e -> {
                SolicitudFila fila = tabla.getSelectionModel().getSelectedItem();
                if (fila == null) return;
                SolicitudSuministro sel = fila.solicitud;

                try {
                    double potencia = Double.parseDouble(txtPotencia.getText().trim());
                    String tarifa = cmbTarifa.getValue();

                    if (tarifa.contains("Residencial") && (potencia <= 0 || potencia > 10.0)) {
                        mostrarMensaje(Alert.AlertType.WARNING, "HU-05", "BT5B Residencial admite hasta 10 kW.");
                        return;
                    }
                    if (potencia <= 0 || potencia > 50.0) {
                        mostrarMensaje(Alert.AlertType.WARNING, "HU-05", "Potencia inválida (0.1 a 50 kW).");
                        return;
                    }

                    String titularInquilino = null;
                    if (sel.getCliente().getTipoCliente() == CondicionCliente.INQUILINO) {
                        if (txtPropietarioLegal.getText().isBlank()) {
                            mostrarMensaje(Alert.AlertType.WARNING, "HU-05", "Ingrese el nombre del Propietario legal como titular.");
                            return;
                        }
                        titularInquilino = txtPropietarioLegal.getText().trim();
                    }

                    Contrato contrato = new Contrato("CTR-" + sel.getIdSolicitud(), tarifa, potencia);
                    double presupuesto = tarifa.contains("Comercial") ? 650.00 : 320.00;

                    if (contrato.validarPotenciaTarifa() && auxiliar.cobrarPresupuesto(presupuesto)) {
                        auxiliar.firmarContrato(contrato);
                        altaDAO.registrarContrato(sel, fila.idCliente, contrato, titularInquilino,
                                "3. Contrato firmado y presupuesto cobrado (" + tarifa + " - " + potencia + " kW)");
                        recargarTabla.run();
                        mostrarMensaje(Alert.AlertType.INFORMATION, "Contratación completada",
                                "Contrato " + contrato.getIdContrato() + " firmado y presupuesto cobrado.\n\nTarea enviada a: Supervisor de Control de Mediciones");
                    }
                } catch (NumberFormatException ex) {
                    mostrarMensaje(Alert.AlertType.ERROR, "Formato inválido", "Potencia numérica inválida.");
                } catch (SQLException ex) {
                    mostrarMensaje(Alert.AlertType.ERROR, "Error SQL Server", ex.getMessage());
                }
            });

            acciones.getChildren().addAll(btnValidar, btnObservarManual, cmbTarifa, txtPotencia, txtPropietarioLegal, btnContrato);
        }

        // SUPERVISOR DE CONTROL COMERCIAL
        if (esAdmin || "Supervisor de Control Comercial".equalsIgnoreCase(rol)) {
            SupervisorControlComercial supComercial = new SupervisorControlComercial();

            btnFactOk.setOnAction(e -> {
                SolicitudFila fila = tabla.getSelectionModel().getSelectedItem();
                if (fila == null) return;
                SolicitudSuministro sel = fila.solicitud;
                try {
                    supComercial.registrarResultadoFactibilidad(sel, 1);
                    altaDAO.actualizarEstadoYFactibilidad(sel.getIdSolicitud(), sel.getPredio().getCodigoPredio(),
                            EstadoSolicitud.APROBADA, "2A. Factibilidad APROBADA (" + LocalDate.now() + ") - Listo para contratación",
                            true, true);
                    recargarTabla.run();
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Factibilidad aprobada",
                            "Factibilidad técnica aprobada.\n\nSolicitud enviada a: Auxiliar Comercial");
                } catch (SQLException ex) {
                    mostrarMensaje(Alert.AlertType.ERROR, "Error SQL Server", ex.getMessage());
                }
            });

            btnFactNo.setOnAction(e -> {
                SolicitudFila fila = tabla.getSelectionModel().getSelectedItem();
                if (fila == null) return;
                SolicitudSuministro sel = fila.solicitud;
                try {
                    supComercial.registrarResultadoFactibilidad(sel, 0);
                    altaDAO.actualizarEstadoYFactibilidad(sel.getIdSolicitud(), sel.getPredio().getCodigoPredio(),
                            EstadoSolicitud.RECHAZADA, "2B. NO VIABLE (" + LocalDate.now() + "): Sin red eléctrica disponible.",
                            false, true);
                    recargarTabla.run();
                    mostrarMensaje(Alert.AlertType.ERROR, "Solicitud No Viable (RN-05)",
                            "Factibilidad técnica rechazada.\n\nNotificación enviada a: Cliente / Titular del suministro");
                } catch (SQLException ex) {
                    mostrarMensaje(Alert.AlertType.ERROR, "Error SQL Server", ex.getMessage());
                }
            });

            acciones.getChildren().addAll(btnFactOk, btnFactNo);
        }

        // SUPERVISOR DE CONTROL DE MEDICIONES
        if (esAdmin || "Supervisor de Control de Mediciones".equalsIgnoreCase(rol)) {
            btnProgInst.setOnAction(e -> {
                SolicitudFila fila = tabla.getSelectionModel().getSelectedItem();
                if (fila == null) return;
                try {
                    altaDAO.programarOrdenInstalacion(fila.solicitud.getIdSolicitud(),
                            "4A. Instalación PROGRAMADA - Orden enviada al contratista");
                    recargarTabla.run();
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Instalación programada",
                            "Orden de instalación generada.\n\nTarea enviada a: Contratista de instalación");
                } catch (SQLException ex) {
                    mostrarMensaje(Alert.AlertType.ERROR, "Error SQL Server", ex.getMessage());
                }
            });
            acciones.getChildren().add(btnProgInst);
        }

        // CONTRATISTA DE INSTALACIÓN
        if (esAdmin || "Contratista de instalación".equalsIgnoreCase(rol)) {
            ContratistaInstalacion contratista = new ContratistaInstalacion("Contratista de Instalación");

            btnEjecInst.setOnAction(e -> {
                SolicitudFila fila = tabla.getSelectionModel().getSelectedItem();
                if (fila == null) return;
                try {
                    OrdenInstalacion orden = new OrdenInstalacion(fila.solicitud, contratista);
                    ActaInstalacion acta = contratista.ejecutarInstalacionAcometidaMedidor(orden);
                    altaDAO.registrarActaInstalacion(fila.solicitud.getIdSolicitud(), fila.idOrdenInstalacion, acta,
                            "4B. Obra EJECUTADA - Acta " + acta.getIdActa() + " (" + acta.getNumeroMedidorInstalado() + ")");
                    recargarTabla.run();
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Acta de instalación enviada",
                            "Acta " + acta.getIdActa() + " (Medidor " + acta.getNumeroMedidorInstalado() + ") registrada.\n\nTarea enviada a: Técnico Supervisor de Mediciones");
                } catch (SQLException ex) {
                    mostrarMensaje(Alert.AlertType.ERROR, "Error SQL Server", ex.getMessage());
                }
            });
            acciones.getChildren().add(btnEjecInst);
        }

        // TÉCNICO SUPERVISOR DE MEDICIONES
        if (esAdmin || "Técnico Supervisor de Mediciones".equalsIgnoreCase(rol)) {
            btnHabilitar.setOnAction(e -> {
                SolicitudFila fila = tabla.getSelectionModel().getSelectedItem();
                if (fila == null) return;
                try {
                    String numSuministro = altaDAO.habilitarSuministroEnBD(
                            fila.solicitud,
                            fila.idCliente,
                            fila.idOrdenInstalacion,
                            fila.numeroMedidorActa,
                            0.0,
                            null
                    );

                    recargarTabla.run();
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Suministro habilitado",
                            "Suministro " + numSuministro + " activo (Medidor " + fila.numeroMedidorActa + ").\n\nCronograma mensual enviado a: Asistente de Facturación");
                } catch (Exception ex) {
                    mostrarMensaje(Alert.AlertType.ERROR, "Error al habilitar suministro", ex.getMessage());
                }
            });

            btnNoConforme.setOnAction(e -> {
                SolicitudFila fila = tabla.getSelectionModel().getSelectedItem();
                if (fila == null) return;
                try {
                    altaDAO.registrarInspeccionNoConforme(fila.solicitud.getIdSolicitud(), fila.idOrdenInstalacion,
                            "NO CONFORME (RN-06): Se exige reprogramar instalación (Paso 4A)");
                    recargarTabla.run();
                    mostrarMensaje(Alert.AlertType.WARNING, "Obra No Conforme (RN-06)",
                            "Inspección rechazada; se requiere reprogramar la obra.\n\nTarea devuelta a: Supervisor de Control de Mediciones");
                } catch (SQLException ex) {
                    mostrarMensaje(Alert.AlertType.ERROR, "Error SQL Server", ex.getMessage());
                }
            });

            acciones.getChildren().addAll(btnHabilitar, btnNoConforme);
        }

        root.getChildren().addAll(tabla, panelDetalle);
        if (!acciones.getChildren().isEmpty()) {
            root.getChildren().add(acciones);
        }
        return root;
    }

    private String formatearNombreDocumento(TipoDocumento tipo) {
        return switch (tipo) {
            case DNI -> "Documento Nacional de Identidad (DNI)";
            case RUC -> "Registro Único de Contribuyentes (RUC)";
            case CARTA_SOLICITUD -> "Carta de Solicitud";
            case TITULO_PROPIEDAD -> "Autovalúo y Acreditación de Propiedad";
            case CROQUIS -> "Croquis de Ubicación del Predio";
            case CARTA_PODER -> "Carta Poder Legalizada del Propietario";
            case CONTRATO_ARRENDAMIENTO -> "Contrato de Arrendamiento Legalizado";
        };
    }

    private void mostrarMensaje(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}