package aplication;

import database.UsuarioDAO.UsuarioDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class VistaProcesoMorosidad {

    // VISTA 3A: Monitor de Morosidad y Órdenes de Corte (HU-15 a HU-17)
    public Parent vistaMonitorMorosidadYCortes() {
        UsuarioDTO u = ContextoSistema.getUsuario();
        String rol = (u != null && u.nombreRol != null) ? u.nombreRol : "";
        boolean esAdmin = "Administrador".equalsIgnoreCase(rol);

        VBox root = new VBox(12);
        root.setPadding(new Insets(20, 30, 20, 30));

        Label lblTitulo = new Label("Monitor de Morosidad y Órdenes de Corte (HU-15 a HU-17)");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 18));

        TableView<String[]> tabla = new TableView<>();
        String[] headers = {"Suministro", "Titular", "Meses Vencidos", "Deuda + Mora (15%)", "Estado del Servicio"};
        for (int i = 0; i < headers.length; i++) {
            final int idx = i;
            TableColumn<String[], String> col = new TableColumn<>(headers[i]);
            col.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[idx]));
            tabla.getColumns().add(col);
        }
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabla.setItems(FXCollections.observableArrayList(ContextoSistema.listaMorosidadCortes));

        FlowPane barra = new FlowPane(12, 8);
        barra.setAlignment(Pos.CENTER_LEFT);

        if (esAdmin || "Supervisor de Cobranzas".equalsIgnoreCase(rol)
                || "Supervisor de Control Comercial".equalsIgnoreCase(rol)
                || "Contratista ejecutor de corte y reconexión".equalsIgnoreCase(rol)) {

            ComboBox<String> cmbTipoCorte = new ComboBox<>(FXCollections.observableArrayList(
                    "Tipo I (Fusible)", "Tipo II (Caja de Medición)", "Tipo III (Línea Aérea / Acometida)"
            ));
            cmbTipoCorte.setValue("Tipo I (Fusible)");

            String textoBtn = "Supervisor de Cobranzas".equalsIgnoreCase(rol)
                    ? "Programar Orden de Corte (HU-16)"
                    : "Ejecutar Corte en Campo (HU-17)";

            Button btnCorte = new Button(textoBtn);
            btnCorte.setOnAction(e -> {
                String[] sel = tabla.getSelectionModel().getSelectedItem();
                if (sel == null) return;
                if (sel[2].startsWith("1") || sel[2].startsWith("0")) {
                    mostrarMensaje(Alert.AlertType.WARNING, "Condición no cumplida", "HU-15/16: El corte solo procede con 2 o más meses de deuda vencida.");
                    return;
                }
                sel[4] = "CORTADO (" + cmbTipoCorte.getValue() + ")";
                tabla.refresh();
                mostrarMensaje(Alert.AlertType.INFORMATION, "Orden procesada", "RN-17/18: Orden procesada y cargo administrativo cargado a la cuenta.");
            });

            barra.getChildren().addAll(new Label("Secuencia progresiva (RN-17):"), cmbTipoCorte, btnCorte);
        }

        if (esAdmin || "Técnico Supervisor Comercial".equalsIgnoreCase(rol)) {
            Button btnAuditarCorte = new Button("Auditar Corte en Campo (CU-46)");
            btnAuditarCorte.setOnAction(e -> {
                String[] sel = tabla.getSelectionModel().getSelectedItem();
                if (sel == null) return;
                sel[4] = sel[4] + " [AUDITADO OK]";
                tabla.refresh();
            });
            barra.getChildren().add(btnAuditarCorte);
        }

        root.getChildren().addAll(lblTitulo, tabla);
        if (!barra.getChildren().isEmpty()) {
            root.getChildren().add(barra);
        }
        return root;
    }

    // VISTA 3B: Pagos, Convenios y Reconexión (HU-18 a HU-21)
    public Parent vistaPagosYReconexion() {
        UsuarioDTO u = ContextoSistema.getUsuario();
        String rol = (u != null && u.nombreRol != null) ? u.nombreRol : "";
        boolean esCliente = "Cliente".equalsIgnoreCase(u != null ? u.categoriaRol : "");
        boolean esAdmin = "Administrador".equalsIgnoreCase(rol);

        VBox root = new VBox(12);
        root.setPadding(new Insets(20, 30, 20, 30));

        Label lblTitulo = new Label(esCliente
                ? "Pagos de Deuda y Solicitud de Convenio (HU-18)"
                : "Recaudación CAR, Convenios y Reconexión (HU-18 a HU-21)");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 18));

        TableView<String[]> tabla = new TableView<>();
        String[] headers = {"Suministro", "Titular", "Meses Vencidos", "Deuda Total", "Estado del Servicio"};
        for (int i = 0; i < headers.length; i++) {
            final int idx = i;
            TableColumn<String[], String> col = new TableColumn<>(headers[i]);
            col.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[idx]));
            tabla.getColumns().add(col);
        }
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        if (esCliente) {
            var cuentasCliente = ContextoSistema.listaMorosidadCortes.stream()
                    .filter(c -> (u.codigoSuministro != null && c[0].equalsIgnoreCase(u.codigoSuministro))
                            || c[1].equalsIgnoreCase(u.getNombreCompleto()))
                    .toList();
            tabla.setItems(FXCollections.observableArrayList(cuentasCliente));
        } else {
            tabla.setItems(FXCollections.observableArrayList(ContextoSistema.listaMorosidadCortes));
        }

        FlowPane acciones = new FlowPane(12, 8);

        if (esAdmin || esCliente || "Entidad Recaudadora / CAR".equalsIgnoreCase(rol)
                || "Asistente de Recaudación".equalsIgnoreCase(rol)) {

            String labelPago = esCliente
                    ? "Realizar Pago Total en CAR (HU-18)"
                    : "1. Registrar / Conciliar Pago Total CAR (HU-18/19)";

            Button btnPagoTotal = new Button(labelPago);
            btnPagoTotal.setOnAction(e -> {
                String[] sel = tabla.getSelectionModel().getSelectedItem();
                if (sel == null) return;
                sel[2] = "0 meses";
                sel[3] = "S/ 0.00";
                sel[4] = "DEUDA REGULARIZADA - LISTO RECONEXIÓN";
                tabla.refresh();
                mostrarMensaje(Alert.AlertType.INFORMATION, "Pago registrado", "RN-20: Pago total registrado y conciliado.");
            });
            acciones.getChildren().add(btnPagoTotal);
        }

        if (esAdmin || esCliente || "Supervisor de Cobranzas".equalsIgnoreCase(rol)) {
            String labelConv = esCliente
                    ? "Solicitar Convenio de Pago (HU-18)"
                    : "2. Aprobar Convenio de Pago (HU-18)";

            Button btnConvenio = new Button(labelConv);
            btnConvenio.setOnAction(e -> {
                String[] sel = tabla.getSelectionModel().getSelectedItem();
                if (sel == null) return;
                sel[4] = "CONVENIO VIGENTE - LISTO RECONEXIÓN";
                tabla.refresh();
                mostrarMensaje(Alert.AlertType.INFORMATION, "Convenio aprobado", "RN-21: Convenio de pago suscrito y aprobado.");
            });
            acciones.getChildren().add(btnConvenio);
        }

        if (esAdmin || "Supervisor de Cobranzas".equalsIgnoreCase(rol)
                || "Contratista ejecutor de corte y reconexión".equalsIgnoreCase(rol)
                || "Técnico Supervisor Comercial".equalsIgnoreCase(rol)) {

            Button btnReconectar = new Button("3. Autorizar y Ejecutar Reconexión (HU-20/21)");
            btnReconectar.setOnAction(e -> {
                String[] sel = tabla.getSelectionModel().getSelectedItem();
                if (sel == null) return;
                if (!sel[4].contains("LISTO RECONEXIÓN")) {
                    mostrarMensaje(Alert.AlertType.ERROR, "Reconexión bloqueada", "RN-21: No se puede reconectar sin pago total o convenio vigente.");
                    return;
                }
                sel[4] = "ACTIVO (RECONECTADO)";
                tabla.refresh();
                mostrarMensaje(Alert.AlertType.INFORMATION, "Servicio restituido", "HU-21: Servicio restituido y cargo por reconexión agendado para el siguiente ciclo.");
            });
            acciones.getChildren().add(btnReconectar);
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