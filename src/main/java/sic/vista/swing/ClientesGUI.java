package sic.vista.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Desktop;
import java.awt.event.AdjustmentEvent;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Cliente;
import sic.modelo.CuentaCorrienteCliente;
import sic.modelo.SucursalActiva;
import sic.modelo.Localidad;
import sic.modelo.PaginaRespuestaRest;
import sic.modelo.Provincia;
import sic.modelo.Rol;
import sic.modelo.Usuario;
import sic.modelo.UsuarioActivo;
import sic.modelo.criteria.BusquedaCuentaCorrienteClienteCriteria;
import sic.util.ColoresNumerosRenderer;
import sic.util.DecimalesRenderer;
import sic.util.FechasRenderer;
import sic.util.FormatosFechaHora;
import sic.util.Utilidades;

public class ClientesGUI extends JInternalFrame {

    private List<CuentaCorrienteCliente> cuentasCorrienteClienteTotal = new ArrayList<>();
    private List<CuentaCorrienteCliente> cuentasCorrienteClienteParcial = new ArrayList<>();
    private Usuario viajanteSeleccionado;
    private boolean tienePermisoSegunRoles;
    private ModeloTabla modeloTablaDeResultados = new ModeloTabla();
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeInternalFrame = new Dimension(880, 600);
    private static int totalElementosBusqueda;
    private static int NUMERO_PAGINA = 0;

    public ClientesGUI() {
        this.initComponents();
        sp_Resultados.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            int va = scrollBar.getVisibleAmount() + 10;
            if (scrollBar.getValue() >= (scrollBar.getMaximum() - va)) {
                if (cuentasCorrienteClienteTotal.size() >= 10) {
                    NUMERO_PAGINA += 1;
                    buscar();
                }
            }
        });
    }

    private void cargarComboBoxProvincias() {
        cmbProvincia.removeAllItems();
        try {
            List<Provincia> provincias = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/ubicaciones/provincias",
                            Provincia[].class)));
            provincias.stream().forEach((p) -> {
                cmbProvincia.addItem(p);
            });
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarComboBoxLocalidadesDeLaProvincia() {
        cmbLocalidad.removeAllItems();
        try {
            List<Localidad> Localidades = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/ubicaciones/localidades/provincias/" + ((Provincia) cmbProvincia.getSelectedItem()).getIdProvincia(),
                            Localidad[].class)));
            Localidad localidadTodas = new Localidad();
            localidadTodas.setNombre("Todas");
            cmbLocalidad.addItem(localidadTodas);
            Localidades.stream().forEach((l) -> {
                cmbLocalidad.addItem(l);
            });
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setColumnas() {
        //nombres de columnas
        String[] encabezados = new String[17];
        encabezados[0] = "Predeterminado";
        encabezados[1] = "Nº Cliente";
        encabezados[2] = "CUIT o DNI";
        encabezados[3] = "R. Social o Nombre";
        encabezados[4] = "Nombre Fantasia";
        encabezados[5] = "Compra a Plazo";
        encabezados[6] = "Saldo C/C";
        encabezados[7] = "Ultimo Movimiento C/C";
        encabezados[8] = "Compra Mín.";
        encabezados[9] = "Credencial";
        encabezados[10] = "Viajante";
        encabezados[11] = "Condicion IVA";
        encabezados[12] = "Telefono";
        encabezados[13] = "Contacto";
        encabezados[14] = "Email";
        encabezados[15] = "Fecha Alta";
        encabezados[16] = "Ubicacion";
        modeloTablaDeResultados.setColumnIdentifiers(encabezados);
        tbl_Resultados.setModel(modeloTablaDeResultados);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaDeResultados.getColumnCount()];
        tipos[0] = Boolean.class;
        tipos[1] = String.class;
        tipos[2] = String.class;
        tipos[3] = String.class;
        tipos[4] = String.class;
        tipos[5] = Boolean.class;
        tipos[6] = BigDecimal.class;
        tipos[7] = LocalDateTime.class;
        tipos[8] = BigDecimal.class;
        tipos[9] = String.class;
        tipos[10] = String.class;
        tipos[11] = String.class;
        tipos[12] = String.class;
        tipos[13] = String.class;
        tipos[14] = String.class;
        tipos[15] = LocalDateTime.class;
        tipos[16] = String.class;
        modeloTablaDeResultados.setClaseColumnas(tipos);
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);
        tbl_Resultados.getTableHeader().setResizingAllowed(true);
        //tamanios de columnas
        tbl_Resultados.getColumnModel().getColumn(0).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(1).setPreferredWidth(80);
        tbl_Resultados.getColumnModel().getColumn(2).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(3).setPreferredWidth(250);
        tbl_Resultados.getColumnModel().getColumn(4).setPreferredWidth(250);
        tbl_Resultados.getColumnModel().getColumn(5).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(6).setPreferredWidth(110);
        tbl_Resultados.getColumnModel().getColumn(7).setPreferredWidth(150);
        tbl_Resultados.getColumnModel().getColumn(8).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(9).setPreferredWidth(250);
        tbl_Resultados.getColumnModel().getColumn(10).setPreferredWidth(250);
        tbl_Resultados.getColumnModel().getColumn(11).setPreferredWidth(250);
        tbl_Resultados.getColumnModel().getColumn(12).setPreferredWidth(150);
        tbl_Resultados.getColumnModel().getColumn(13).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(14).setPreferredWidth(250);
        tbl_Resultados.getColumnModel().getColumn(15).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(16).setPreferredWidth(400);
        //renderers
        tbl_Resultados.getColumnModel().getColumn(6).setCellRenderer(new ColoresNumerosRenderer());
        tbl_Resultados.getColumnModel().getColumn(7).setCellRenderer(new FechasRenderer(FormatosFechaHora.FORMATO_FECHAHORA_HISPANO));
        tbl_Resultados.getColumnModel().getColumn(8).setCellRenderer(new DecimalesRenderer());
        tbl_Resultados.getColumnModel().getColumn(15).setCellRenderer(new FechasRenderer(FormatosFechaHora.FORMATO_FECHA_HISPANO));
    }

    private void cargarResultadosAlTable() {
        cuentasCorrienteClienteParcial.stream().map(c -> {
            Object[] fila = new Object[17];
            fila[0] = c.getCliente().isPredeterminado();
            fila[1] = c.getCliente().getNroCliente();
            fila[2] = c.getCliente().getIdFiscal();
            fila[3] = c.getCliente().getNombreFiscal();
            fila[4] = c.getCliente().getNombreFantasia();
            fila[5] = c.getCliente().isPuedeComprarAPlazo();
            fila[6] = c.getSaldo();
            fila[7] = c.getFechaUltimoMovimiento();
            fila[8] = c.getCliente().getMontoCompraMinima();
            fila[9] = c.getCliente().getNombreCredencial();
            fila[10] = c.getCliente().getNombreViajante();
            fila[11] = c.getCliente().getCategoriaIVA();
            fila[12] = c.getCliente().getTelefono();
            fila[13] = c.getCliente().getContacto();
            fila[14] = c.getCliente().getEmail();
            fila[15] = c.getCliente().getFechaAlta();
            fila[16] = c.getCliente().getUbicacionFacturacion();
            return fila;
        }).forEach(fila -> {
            modeloTablaDeResultados.addRow(fila);
        });
        tbl_Resultados.setModel(modeloTablaDeResultados);
        lbl_cantResultados.setText(totalElementosBusqueda + " clientes encontrados");
    }

    private void resetScroll() {
        NUMERO_PAGINA = 0;
        cuentasCorrienteClienteTotal.clear();
        cuentasCorrienteClienteParcial.clear();
        Point p = new Point(0, 0);
        sp_Resultados.getViewport().setViewPosition(p);
    }

    private void limpiarJTable() {
        modeloTablaDeResultados = new ModeloTabla();
        tbl_Resultados.setModel(modeloTablaDeResultados);
        this.setColumnas();
    }

    private void cambiarEstadoEnabledComponentes(boolean status) {
        chkCriteria.setEnabled(status);
        if (status == true && chkCriteria.isSelected() == true) {
            txtCriteria.setEnabled(true);
        } else {
            txtCriteria.setEnabled(false);
        }
        chk_Ubicacion.setEnabled(status);
        if (status == true && chk_Ubicacion.isSelected() == true) {
            cmbProvincia.setEnabled(true);
            cmbLocalidad.setEnabled(true);
        } else {
            cmbProvincia.setEnabled(false);
            cmbLocalidad.setEnabled(false);
        }
        btn_Buscar.setEnabled(status);
        btnCuentaCorriente.setEnabled(status);
        btn_Nuevo.setEnabled(status);
        btn_Eliminar.setEnabled(status);
        btn_Modificar.setEnabled(status);
        btn_setPredeterminado.setEnabled(status);
        tbl_Resultados.setEnabled(status);
        sp_Resultados.setEnabled(status);
        tbl_Resultados.requestFocus();
    }

    private void limpiarYBuscar() {
        this.resetScroll();
        this.limpiarJTable();
        this.buscar();
    }

    private void buscar() {
        this.cambiarEstadoEnabledComponentes(false);
        BusquedaCuentaCorrienteClienteCriteria criteria = BusquedaCuentaCorrienteClienteCriteria.builder().build();
        if (chkCriteria.isSelected()) {
            criteria.setNombreFiscal(txtCriteria.getText().trim());
            criteria.setNombreFantasia(txtCriteria.getText().trim());
            criteria.setNroDeCliente(txtCriteria.getText().trim());
        }
        if (chkViajante.isSelected() && viajanteSeleccionado != null) {
            criteria.setIdViajante(viajanteSeleccionado.getIdUsuario());
        }
        if (chk_Ubicacion.isSelected()) {
            criteria.setIdProvincia(((Provincia) (cmbProvincia.getSelectedItem())).getIdProvincia());
            if (!((Localidad) cmbLocalidad.getSelectedItem()).getNombre().equals("Todas")) {
                criteria.setIdLocalidad(((Localidad) cmbLocalidad.getSelectedItem()).getIdLocalidad());
            }
        }
        int seleccionOrden = cmbOrden.getSelectedIndex();
        switch (seleccionOrden) {
            case 0:
                criteria.setOrdenarPor("cliente.nombreFiscal");
                break;
            case 1:
                criteria.setOrdenarPor("cliente.fechaAlta");
                break;
            case 2:
                criteria.setOrdenarPor("cliente.nombreFantasia");
                break;
            case 3:
                criteria.setOrdenarPor("saldo");
                break;
            case 4:
                criteria.setOrdenarPor("fechaUltimoMovimiento");
                break;
            case 5:
                criteria.setOrdenarPor("cliente.montoCompraMinima");
                break;
        }
        int seleccionDireccion = cmbSentido.getSelectedIndex();
        switch (seleccionDireccion) {
            case 0:
                criteria.setSentido("ASC");
                break;
            case 1:
                criteria.setSentido("DESC");
                break;
        }
        criteria.setPagina(NUMERO_PAGINA);
        try {
            HttpEntity<BusquedaCuentaCorrienteClienteCriteria> requestEntity = new HttpEntity<>(criteria);
            PaginaRespuestaRest<CuentaCorrienteCliente> response = RestClient.getRestTemplate()
                    .exchange("/cuentas-corriente/clientes/busqueda/criteria", HttpMethod.POST, requestEntity,
                            new ParameterizedTypeReference<PaginaRespuestaRest<CuentaCorrienteCliente>>() {
                    })
                    .getBody();
            totalElementosBusqueda = response.getTotalElements();
            cuentasCorrienteClienteParcial = response.getContent();
            cuentasCorrienteClienteTotal.addAll(cuentasCorrienteClienteParcial);
            this.cargarResultadosAlTable();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        this.cambiarEstadoEnabledComponentes(true);
        this.cambiarEstadoDeComponentesSegunRolUsuario();
    }

    private void cambiarEstadoDeComponentesSegunRolUsuario() {
        List<Rol> rolesDeUsuarioActivo = UsuarioActivo.getInstance().getUsuario().getRoles();
        if (rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)) {
            btn_Eliminar.setEnabled(true);
        } else {
            btn_Eliminar.setEnabled(false);
        }
        if (rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)
                || rolesDeUsuarioActivo.contains(Rol.ENCARGADO)) {
            btn_setPredeterminado.setEnabled(true);
        } else {
            btn_setPredeterminado.setEnabled(false);
        }
        if (rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)
                || rolesDeUsuarioActivo.contains(Rol.ENCARGADO)
                || rolesDeUsuarioActivo.contains(Rol.VENDEDOR)) {
            chkViajante.setEnabled(true);
            btnBuscarViajante.setEnabled(false);
            tienePermisoSegunRoles = true;
        } else {
            chkViajante.setEnabled(false);
            btnBuscarViajante.setEnabled(false);
            tienePermisoSegunRoles = false;
        }
    }
    
    private void exportar() {
        BusquedaCuentaCorrienteClienteCriteria criteria = BusquedaCuentaCorrienteClienteCriteria.builder().build();
        if (chkCriteria.isSelected()) {
            criteria.setNombreFiscal(txtCriteria.getText().trim());
            criteria.setNombreFantasia(txtCriteria.getText().trim());
            criteria.setNroDeCliente(txtCriteria.getText().trim());
        }
        if (chkViajante.isSelected() && viajanteSeleccionado != null) {
            criteria.setIdViajante(viajanteSeleccionado.getIdUsuario());
        }
        if (chk_Ubicacion.isSelected()) {
            criteria.setIdProvincia(((Provincia) (cmbProvincia.getSelectedItem())).getIdProvincia());
            if (!((Localidad) cmbLocalidad.getSelectedItem()).getNombre().equals("Todas")) {
                criteria.setIdLocalidad(((Localidad) cmbLocalidad.getSelectedItem()).getIdLocalidad());
            }
        }
        int seleccionOrden = cmbOrden.getSelectedIndex();
        switch (seleccionOrden) {
            case 0:
                criteria.setOrdenarPor("cliente.nombreFiscal");
                break;
            case 1:
                criteria.setOrdenarPor("cliente.fechaAlta");
                break;
            case 2:
                criteria.setOrdenarPor("cliente.nombreFantasia");
                break;
            case 3:
                criteria.setOrdenarPor("saldo");
                break;
            case 4:
                criteria.setOrdenarPor("fechaUltimoMovimiento");
                break;
            case 5:
                criteria.setOrdenarPor("cliente.montoCompraMinima");
                break;
        }
        int seleccionDireccion = cmbSentido.getSelectedIndex();
        switch (seleccionDireccion) {
            case 0:
                criteria.setSentido("ASC");
                break;
            case 1:
                criteria.setSentido("DESC");
                break;
        }
        criteria.setPagina(NUMERO_PAGINA);
        ExportGUI exportGUI = new ExportGUI(criteria, true);
        exportGUI.setModal(true);
        exportGUI.setLocationRelativeTo(this);
        exportGUI.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelFiltros = new javax.swing.JPanel();
        chkCriteria = new javax.swing.JCheckBox();
        txtCriteria = new javax.swing.JTextField();
        chk_Ubicacion = new javax.swing.JCheckBox();
        cmbProvincia = new javax.swing.JComboBox();
        cmbLocalidad = new javax.swing.JComboBox();
        btn_Buscar = new javax.swing.JButton();
        lbl_cantResultados = new javax.swing.JLabel();
        chkViajante = new javax.swing.JCheckBox();
        txtViajante = new javax.swing.JTextField();
        btnBuscarViajante = new javax.swing.JButton();
        panelResultados = new javax.swing.JPanel();
        sp_Resultados = new javax.swing.JScrollPane();
        tbl_Resultados = new javax.swing.JTable();
        btn_Nuevo = new javax.swing.JButton();
        btn_Modificar = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();
        btn_setPredeterminado = new javax.swing.JButton();
        btnCuentaCorriente = new javax.swing.JButton();
        btnExportar = new javax.swing.JButton();
        panelOrden = new javax.swing.JPanel();
        cmbOrden = new javax.swing.JComboBox<>();
        cmbSentido = new javax.swing.JComboBox<>();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Administrar Clientes");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Client_16x16.png"))); // NOI18N
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });

        panelFiltros.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

        chkCriteria.setText("Nº Cliente o Nombre:");
        chkCriteria.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkCriteriaItemStateChanged(evt);
            }
        });

        txtCriteria.setEnabled(false);
        txtCriteria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCriteriaActionPerformed(evt);
            }
        });

        chk_Ubicacion.setText("Ubicación:");
        chk_Ubicacion.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_UbicacionItemStateChanged(evt);
            }
        });

        cmbProvincia.setEnabled(false);
        cmbProvincia.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbProvinciaItemStateChanged(evt);
            }
        });

        cmbLocalidad.setEnabled(false);

        btn_Buscar.setForeground(java.awt.Color.blue);
        btn_Buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btn_Buscar.setText("Buscar");
        btn_Buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_BuscarActionPerformed(evt);
            }
        });

        lbl_cantResultados.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        chkViajante.setText("Viajante:");
        chkViajante.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkViajanteItemStateChanged(evt);
            }
        });

        txtViajante.setEditable(false);
        txtViajante.setEnabled(false);
        txtViajante.setOpaque(false);

        btnBuscarViajante.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btnBuscarViajante.setEnabled(false);
        btnBuscarViajante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarViajanteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelFiltrosLayout = new javax.swing.GroupLayout(panelFiltros);
        panelFiltros.setLayout(panelFiltrosLayout);
        panelFiltrosLayout.setHorizontalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addComponent(btn_Buscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_cantResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(chkViajante, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chkCriteria, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chk_Ubicacion, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbLocalidad, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbProvincia, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(panelFiltrosLayout.createSequentialGroup()
                                .addComponent(txtViajante, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE)
                                .addGap(0, 0, 0)
                                .addComponent(btnBuscarViajante))
                            .addComponent(txtCriteria))))
                .addContainerGap())
        );
        panelFiltrosLayout.setVerticalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkCriteria)
                    .addComponent(txtCriteria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkViajante)
                    .addComponent(txtViajante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarViajante))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chk_Ubicacion)
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addComponent(cmbProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbLocalidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btn_Buscar)
                    .addComponent(lbl_cantResultados, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelFiltrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmbLocalidad, cmbProvincia});

        panelFiltrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnBuscarViajante, txtViajante});

        panelResultados.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultados"));

        tbl_Resultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Resultados.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tbl_Resultados.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sp_Resultados.setViewportView(tbl_Resultados);

        btn_Nuevo.setForeground(java.awt.Color.blue);
        btn_Nuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddClient_16x16.png"))); // NOI18N
        btn_Nuevo.setText("Nuevo");
        btn_Nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevoActionPerformed(evt);
            }
        });

        btn_Modificar.setForeground(java.awt.Color.blue);
        btn_Modificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EditClient_16x16.png"))); // NOI18N
        btn_Modificar.setText("Modificar");
        btn_Modificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ModificarActionPerformed(evt);
            }
        });

        btn_Eliminar.setForeground(java.awt.Color.blue);
        btn_Eliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/DeleteClient_16x16.png"))); // NOI18N
        btn_Eliminar.setText("Eliminar");
        btn_Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarActionPerformed(evt);
            }
        });

        btn_setPredeterminado.setForeground(java.awt.Color.blue);
        btn_setPredeterminado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/ClientArrow_16x16.png"))); // NOI18N
        btn_setPredeterminado.setText("Marcar Predeterminado");
        btn_setPredeterminado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_setPredeterminadoActionPerformed(evt);
            }
        });

        btnCuentaCorriente.setForeground(java.awt.Color.blue);
        btnCuentaCorriente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/CC_16x16.png"))); // NOI18N
        btnCuentaCorriente.setText("Cuenta Corriente");
        btnCuentaCorriente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCuentaCorrienteActionPerformed(evt);
            }
        });

        btnExportar.setForeground(java.awt.Color.blue);
        btnExportar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Export_16x16.png"))); // NOI18N
        btnExportar.setText("Exportar");
        btnExportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelResultadosLayout = new javax.swing.GroupLayout(panelResultados);
        panelResultados.setLayout(panelResultadosLayout);
        panelResultadosLayout.setHorizontalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_Resultados)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelResultadosLayout.createSequentialGroup()
                        .addComponent(btn_Nuevo)
                        .addGap(0, 0, 0)
                        .addComponent(btn_Modificar)
                        .addGap(0, 0, 0)
                        .addComponent(btn_Eliminar))
                    .addGroup(panelResultadosLayout.createSequentialGroup()
                        .addComponent(btn_setPredeterminado)
                        .addGap(0, 0, 0)
                        .addComponent(btnCuentaCorriente)
                        .addGap(0, 0, 0)
                        .addComponent(btnExportar)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCuentaCorriente, btnExportar, btn_Eliminar, btn_Modificar, btn_Nuevo, btn_setPredeterminado});

        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btn_setPredeterminado)
                    .addComponent(btnCuentaCorriente)
                    .addComponent(btnExportar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btn_Eliminar)
                    .addComponent(btn_Modificar)
                    .addComponent(btn_Nuevo)))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCuentaCorriente, btn_Eliminar, btn_Modificar, btn_Nuevo, btn_setPredeterminado});

        panelOrden.setBorder(javax.swing.BorderFactory.createTitledBorder("Ordenar Por"));

        cmbOrden.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "R. Social o Nombre", "Fecha Alta", "Nombre Fantasia", "Saldo C/C", "Ultimo Movimiento C/C", "Compra Mín." }));
        cmbOrden.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbOrdenItemStateChanged(evt);
            }
        });

        cmbSentido.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ascendente", "Descendente" }));
        cmbSentido.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbSentidoItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout panelOrdenLayout = new javax.swing.GroupLayout(panelOrden);
        panelOrden.setLayout(panelOrdenLayout);
        panelOrdenLayout.setHorizontalGroup(
            panelOrdenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOrdenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelOrdenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbOrden, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbSentido, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelOrdenLayout.setVerticalGroup(
            panelOrdenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOrdenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cmbOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cmbSentido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(457, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chkCriteriaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkCriteriaItemStateChanged
        if (chkCriteria.isSelected() == true) {
            txtCriteria.setEnabled(true);
            txtCriteria.requestFocus();
        } else {
            txtCriteria.setEnabled(false);
        }
    }//GEN-LAST:event_chkCriteriaItemStateChanged

    private void chk_UbicacionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_UbicacionItemStateChanged
        if (chk_Ubicacion.isSelected()) {
            this.cargarComboBoxProvincias();
            cmbProvincia.setEnabled(true);
            cmbLocalidad.setEnabled(true);
            cmbProvincia.requestFocus();
        } else {
            cmbProvincia.removeAllItems();
            cmbProvincia.setEnabled(false);
            cmbLocalidad.setEnabled(false);
            cmbLocalidad.removeAllItems();
        }
    }//GEN-LAST:event_chk_UbicacionItemStateChanged

    private void cmbProvinciaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbProvinciaItemStateChanged
        if (cmbProvincia.getItemCount() > 0) {
            if (!cmbProvincia.getSelectedItem().toString().equals("Todas")) {
                cargarComboBoxLocalidadesDeLaProvincia();
            } else {
                cmbLocalidad.removeAllItems();
                Localidad localidadTodas = new Localidad();
                localidadTodas.setNombre("Todas");
                cmbLocalidad.addItem(localidadTodas);
            }
        } else {
            cmbLocalidad.removeAllItems();
        }
    }//GEN-LAST:event_cmbProvinciaItemStateChanged

    private void btn_BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarActionPerformed
        this.limpiarYBuscar();
    }//GEN-LAST:event_btn_BuscarActionPerformed

    private void btn_NuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoActionPerformed
        DetalleClienteGUI gui_DetalleCliente = new DetalleClienteGUI();
        gui_DetalleCliente.setModal(true);
        gui_DetalleCliente.setLocationRelativeTo(this);
        gui_DetalleCliente.setVisible(true);
        this.limpiarYBuscar();
    }//GEN-LAST:event_btn_NuevoActionPerformed

    private void btn_EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            String mensaje = "¿Esta seguro que desea eliminar el cliente: "
                    + cuentasCorrienteClienteTotal.get(indexFilaSeleccionada).getCliente() + "?"
                    + "\nEl usuario " + cuentasCorrienteClienteTotal.get(indexFilaSeleccionada).getCliente().getNombreCredencial()
                    + " será desvinculado del cliente.";
            int respuesta = JOptionPane.showConfirmDialog(this, mensaje,
                    "Eliminar", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    RestClient.getRestTemplate().delete("/clientes/" + cuentasCorrienteClienteTotal.get(indexFilaSeleccionada).getCliente().getIdCliente());
                    this.resetScroll();
                    this.limpiarJTable();
                    this.buscar();
                } catch (RestClientResponseException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (ResourceAccessException ex) {
                    LOGGER.error(ex.getMessage());
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_btn_EliminarActionPerformed

    private void btn_ModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ModificarActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            DetalleClienteGUI gui_DetalleCliente = new DetalleClienteGUI(cuentasCorrienteClienteTotal.get(indexFilaSeleccionada).getCliente());
            gui_DetalleCliente.setModal(true);
            gui_DetalleCliente.setLocationRelativeTo(this);
            gui_DetalleCliente.setVisible(true);
            this.limpiarYBuscar();
        }
    }//GEN-LAST:event_btn_ModificarActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        try {
            this.setSize(sizeInternalFrame);
            this.setColumnas();
            this.setMaximum(true);
            if (tienePermisoSegunRoles) {
                boolean existeClientePredeterminado = RestClient.getRestTemplate()
                        .getForObject("/clientes/existe-predeterminado/sucursales/"
                                + SucursalActiva.getInstance().getSucursal().getIdSucursal(), boolean.class);
                if (!existeClientePredeterminado) {
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_no_existe_cliente_predeterminado"),
                            "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
            this.cambiarEstadoDeComponentesSegunRolUsuario();
        } catch (PropertyVetoException ex) {
            String msjError = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(msjError + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, msjError, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_formInternalFrameOpened

    private void btn_setPredeterminadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_setPredeterminadoActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            try {
                int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
                Cliente cliente = RestClient.getRestTemplate()
                        .getForObject("/clientes/" + cuentasCorrienteClienteTotal.get(indexFilaSeleccionada).getCliente().getIdCliente(), Cliente.class);
                if (cliente != null) {
                    RestClient.getRestTemplate().put("/clientes/" + cliente.getIdCliente() + "/predeterminado", null);
                    btn_BuscarActionPerformed(evt);
                } else {
                    JOptionPane.showInternalMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_no_se_encontro_cliente_predeterminado"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showInternalMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_seleccionar_cliente"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_setPredeterminadoActionPerformed

    private void btnCuentaCorrienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCuentaCorrienteActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            Cliente cliente = RestClient.getRestTemplate()
                    .getForObject("/clientes/" + cuentasCorrienteClienteTotal.get(indexFilaSeleccionada).getCliente().getIdCliente(), Cliente.class);
            JInternalFrame gui;
            if (cliente != null) {
                gui = new CuentaCorrienteGUI(cliente);
                gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                        getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
                getDesktopPane().add(gui);
                gui.setVisible(true);
                try {
                    gui.setSelected(true);
                } catch (PropertyVetoException ex) {
                    String msjError = "No se pudo seleccionar la ventana requerida.";
                    LOGGER.error(msjError + " - " + ex.getMessage());
                    JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_btnCuentaCorrienteActionPerformed

    private void chkViajanteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkViajanteItemStateChanged
        if (chkViajante.isSelected()) {
            btnBuscarViajante.setEnabled(true);
            btnBuscarViajante.requestFocus();
            txtViajante.setEnabled(true);
        } else {
            btnBuscarViajante.setEnabled(false);
            txtViajante.setEnabled(false);
        }
    }//GEN-LAST:event_chkViajanteItemStateChanged

    private void txtCriteriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCriteriaActionPerformed
        btn_BuscarActionPerformed(null);
    }//GEN-LAST:event_txtCriteriaActionPerformed

    private void btnBuscarViajanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarViajanteActionPerformed
        Rol[] rolesParaFiltrar = new Rol[]{Rol.VIAJANTE};
        BuscarUsuariosGUI buscarUsuariosGUI = new BuscarUsuariosGUI(rolesParaFiltrar, "Buscar Viajante");
        buscarUsuariosGUI.setModal(true);
        buscarUsuariosGUI.setLocationRelativeTo(this);
        buscarUsuariosGUI.setVisible(true);
        if (buscarUsuariosGUI.getUsuarioSeleccionado() != null) {
            viajanteSeleccionado = buscarUsuariosGUI.getUsuarioSeleccionado();
            txtViajante.setText(viajanteSeleccionado.toString());
        }
    }//GEN-LAST:event_btnBuscarViajanteActionPerformed

    private void cmbSentidoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSentidoItemStateChanged
        this.limpiarYBuscar();
    }//GEN-LAST:event_cmbSentidoItemStateChanged

    private void cmbOrdenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbOrdenItemStateChanged
        this.limpiarYBuscar();
    }//GEN-LAST:event_cmbOrdenItemStateChanged

    private void btnExportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarActionPerformed
        if (!cuentasCorrienteClienteTotal.isEmpty()) {
            if (Desktop.isDesktopSupported()) {
                this.exportar();
            } else {
                JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_plataforma_no_soportada"),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnExportarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarViajante;
    private javax.swing.JButton btnCuentaCorriente;
    private javax.swing.JButton btnExportar;
    private javax.swing.JButton btn_Buscar;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JButton btn_Modificar;
    private javax.swing.JButton btn_Nuevo;
    private javax.swing.JButton btn_setPredeterminado;
    private javax.swing.JCheckBox chkCriteria;
    private javax.swing.JCheckBox chkViajante;
    private javax.swing.JCheckBox chk_Ubicacion;
    private javax.swing.JComboBox cmbLocalidad;
    private javax.swing.JComboBox<String> cmbOrden;
    private javax.swing.JComboBox cmbProvincia;
    private javax.swing.JComboBox<String> cmbSentido;
    private javax.swing.JLabel lbl_cantResultados;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelOrden;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JScrollPane sp_Resultados;
    private javax.swing.JTable tbl_Resultados;
    private javax.swing.JTextField txtCriteria;
    private javax.swing.JTextField txtViajante;
    // End of variables declaration//GEN-END:variables
}
