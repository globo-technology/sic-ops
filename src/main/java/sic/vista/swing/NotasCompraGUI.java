package sic.vista.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.EmpresaActiva;
import sic.modelo.Factura;
import sic.modelo.Movimiento;
import sic.modelo.Nota;
import sic.modelo.PaginaRespuestaRest;
import sic.modelo.Rol;
import sic.modelo.TipoDeComprobante;
import sic.modelo.UsuarioActivo;
import sic.util.DecimalesRenderer;
import sic.util.FechasRenderer;
import sic.util.FormatosFechaHora;
import sic.util.Utilidades;

public class NotasCompraGUI extends JInternalFrame {

    private ModeloTabla modeloTablaNotas = new ModeloTabla();
    private List<Nota> notasTotal = new ArrayList<>();
    private List<Nota> notasParcial = new ArrayList<>();    
    private boolean tienePermisoSegunRoles;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeInternalFrame = new Dimension(970, 600);
    private static int totalElementosBusqueda;
    private static int NUMERO_PAGINA = 0;    

    public NotasCompraGUI() {
        this.initComponents();
        sp_Resultados.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            int va = scrollBar.getVisibleAmount() + 10;
            if (scrollBar.getValue() >= (scrollBar.getMaximum() - va)) {
                if (notasTotal.size() >= 10) {
                    NUMERO_PAGINA += 1;
                    buscar(false);
                }
            }
        });
    }

    private String getUriCriteria() {
        String uriCriteria = "idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                + "&movimiento=" + Movimiento.COMPRA;
        if (chk_Fecha.isSelected()) {
            uriCriteria += "&desde=" + dc_FechaDesde.getDate().getTime()
                    + "&hasta=" + dc_FechaHasta.getDate().getTime();
        }
        if (chk_NumNota.isSelected()) {
            uriCriteria += "&nroNota=" + Long.valueOf(txt_NroNota.getText())
                    + "&nroSerie=" + Long.valueOf(txt_SerieNota.getText());
        }
        if (chk_TipoNota.isSelected()) {
            uriCriteria += "&tipoDeComprobante=" + ((TipoDeComprobante) cmb_TipoNota.getSelectedItem()).name();
        }
        uriCriteria += "&pagina=" + NUMERO_PAGINA;
        return uriCriteria;
    }

    private void setColumnas() {
        //nombres de columnas
        String[] encabezados = new String[8];
        encabezados[0] = "CAE";
        encabezados[1] = "Fecha Nota";
        encabezados[2] = "Tipo";
        encabezados[3] = "Nº Nota";
        encabezados[4] = "Proveedor";
        encabezados[5] = "Usuario";
        encabezados[6] = "Total";        
        encabezados[7] = "Motivo";
        modeloTablaNotas.setColumnIdentifiers(encabezados);
        tbl_Resultados.setModel(modeloTablaNotas);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaNotas.getColumnCount()];
        tipos[0] = Object.class;
        tipos[1] = Date.class;
        tipos[2] = TipoDeComprobante.class;
        tipos[3] = String.class;
        tipos[4] = String.class;
        tipos[5] = String.class;
        tipos[6] = BigDecimal.class;        
        tipos[7] = String.class;
        modeloTablaNotas.setClaseColumnas(tipos);
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);
        tbl_Resultados.getTableHeader().setResizingAllowed(true);
        //tamanios de columnas
        tbl_Resultados.getColumnModel().getColumn(0).setMinWidth(120);
        tbl_Resultados.getColumnModel().getColumn(0).setMaxWidth(120);
        tbl_Resultados.getColumnModel().getColumn(0).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(1).setMinWidth(90);
        tbl_Resultados.getColumnModel().getColumn(1).setMaxWidth(90);
        tbl_Resultados.getColumnModel().getColumn(1).setPreferredWidth(90);
        tbl_Resultados.getColumnModel().getColumn(2).setMinWidth(140);
        tbl_Resultados.getColumnModel().getColumn(2).setMaxWidth(140);
        tbl_Resultados.getColumnModel().getColumn(2).setPreferredWidth(140);
        tbl_Resultados.getColumnModel().getColumn(3).setMinWidth(100);
        tbl_Resultados.getColumnModel().getColumn(3).setMaxWidth(100);
        tbl_Resultados.getColumnModel().getColumn(3).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(4).setPreferredWidth(250);
        tbl_Resultados.getColumnModel().getColumn(5).setPreferredWidth(250);        
        tbl_Resultados.getColumnModel().getColumn(6).setMinWidth(120);
        tbl_Resultados.getColumnModel().getColumn(6).setMaxWidth(120);
        tbl_Resultados.getColumnModel().getColumn(6).setPreferredWidth(120);        
        tbl_Resultados.getColumnModel().getColumn(7).setMinWidth(500);
        //render para los tipos de datos
        tbl_Resultados.setDefaultRenderer(BigDecimal.class, new DecimalesRenderer());
        tbl_Resultados.getColumnModel().getColumn(1).setCellRenderer(new FechasRenderer(FormatosFechaHora.FORMATO_FECHA_HISPANO));        
    }

    private void buscar(boolean calcularResultados) {
        this.cambiarEstadoEnabledComponentes(false);
        String uriCriteria = getUriCriteria();
        try {
            PaginaRespuestaRest<Nota> response = RestClient.getRestTemplate()
                    .exchange("/notas/busqueda/criteria?" + uriCriteria, HttpMethod.GET, null,
                            new ParameterizedTypeReference<PaginaRespuestaRest<Nota>>() {
                    })
                    .getBody();
            totalElementosBusqueda = response.getTotalElements();
            notasParcial = response.getContent();
            notasTotal.addAll(notasParcial);
            this.cargarResultadosAlTable();
            if (calcularResultados && tienePermisoSegunRoles) this.calcularResultados(uriCriteria);            
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

    private void cambiarEstadoEnabledComponentes(boolean status) {
        chk_Fecha.setEnabled(status);
        if (status == true && chk_Fecha.isSelected() == true) {
            dc_FechaDesde.setEnabled(true);
            dc_FechaHasta.setEnabled(true);
        } else {
            dc_FechaDesde.setEnabled(false);
            dc_FechaHasta.setEnabled(false);
        }
        chk_NumNota.setEnabled(status);
        if (status == true && chk_NumNota.isSelected() == true) {
            txt_SerieNota.setEnabled(true);
            txt_NroNota.setEnabled(true);
        } else {
            txt_SerieNota.setEnabled(false);
            txt_NroNota.setEnabled(false);
        }
        chk_TipoNota.setEnabled(status);
        if (status == true && chk_TipoNota.isSelected() == true) {
            cmb_TipoNota.setEnabled(true);
        } else {
            cmb_TipoNota.setEnabled(false);
        }
        chk_NumNota.setEnabled(status);
        if (status == true && chk_NumNota.isSelected() == true) {
            txt_NroNota.setEnabled(true);
        } else {
            txt_NroNota.setEnabled(false);
        }
        btn_Buscar.setEnabled(status);
        btn_VerDetalle.setEnabled(status);
        tbl_Resultados.setEnabled(status);
        sp_Resultados.setEnabled(status);
        tbl_Resultados.requestFocus();
    }

    private void cargarResultadosAlTable() {
        notasParcial.stream().map(nota -> {
            Object[] fila = new Object[8];
            fila[0] = nota.getCAE() == 0 ? "" : nota.getCAE();
            fila[1] = nota.getFecha();
            fila[2] = nota.getTipoComprobante();
            fila[3] = nota.getSerie() + " - " + nota.getNroNota();
            fila[4] = nota.getRazonSocialProveedor();
            fila[5] = nota.getNombreUsuario();            
            fila[6] = nota.getTotal();            
            fila[7] = nota.getMotivo();
            return fila;
        }).forEach(fila -> {
            modeloTablaNotas.addRow(fila);
        });
        tbl_Resultados.setModel(modeloTablaNotas);
        lbl_cantResultados.setText(totalElementosBusqueda + " notas encontradas");
    }

    private void resetScroll() {
        NUMERO_PAGINA = 0;
        notasTotal.clear();
        notasParcial.clear();
        notasTotal.clear();
        notasParcial.clear();
        Point p = new Point(0, 0);
        sp_Resultados.getViewport().setViewPosition(p);
    }

    private void limpiarJTable() {
        modeloTablaNotas = new ModeloTabla();
        tbl_Resultados.setModel(modeloTablaNotas);
        this.setColumnas();
    }

    private void cargarTiposDeNota() {
        try {
            TipoDeComprobante[] tiposDeComprobantes = RestClient.getRestTemplate()
                    .getForObject("/notas/tipos/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                            TipoDeComprobante[].class);
            for (int i = 0; tiposDeComprobantes.length > i; i++) {
                cmb_TipoNota.addItem(tiposDeComprobantes[i]);
            }
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cambiarEstadoDeComponentesSegunRolUsuario() {
        List<Rol> rolesDeUsuarioActivo = UsuarioActivo.getInstance().getUsuario().getRoles();
        if (rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)
                || rolesDeUsuarioActivo.contains(Rol.ENCARGADO)
                || rolesDeUsuarioActivo.contains(Rol.VENDEDOR)) {
            tienePermisoSegunRoles = true;
            lbl_TotalIVANotasDebito.setVisible(true);
            lbl_TotalIVANotasCredito.setVisible(true);
            lbl_TotalNotasDebito.setVisible(true);
            lbl_TotalNotasCredito.setVisible(true);
            txt_ResultTotalIVANotaDebito.setVisible(true);
            txt_ResultTotalIVANotaCredito.setVisible(true);
            txt_ResultTotalDebito.setVisible(true);
            txt_ResultTotalCredito.setVisible(true);
        } else {
            tienePermisoSegunRoles = false;
            lbl_TotalIVANotasDebito.setVisible(false);
            lbl_TotalIVANotasCredito.setVisible(false);
            lbl_TotalNotasDebito.setVisible(false);
            lbl_TotalNotasCredito.setVisible(false);
            txt_ResultTotalIVANotaDebito.setVisible(false);
            txt_ResultTotalIVANotaCredito.setVisible(false);
            txt_ResultTotalDebito.setVisible(false);
            txt_ResultTotalCredito.setVisible(false);
        }
    }

    private void calcularResultados(String uriCriteria) {
        txt_ResultTotalIVANotaDebito.setValue(RestClient.getRestTemplate()
                .getForObject("/notas/total-iva-debito/criteria?" + uriCriteria, BigDecimal.class));
        txt_ResultTotalIVANotaCredito.setValue(RestClient.getRestTemplate()
                .getForObject("/notas/total-iva-credito/criteria?" + uriCriteria, BigDecimal.class));
        txt_ResultTotalDebito.setValue(RestClient.getRestTemplate()
                .getForObject("/notas/total-debito/criteria?" + uriCriteria, BigDecimal.class));
        txt_ResultTotalCredito.setValue(RestClient.getRestTemplate()
                .getForObject("/notas/total-credito/criteria?" + uriCriteria, BigDecimal.class));
    }

    private void verFacturaCompra() {
        int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
        if (tbl_Resultados.getSelectedRow() != -1 && isNotaCredito(notasTotal.get(indexFilaSeleccionada).getTipoComprobante())) {
            FacturasCompraGUI gui_facturaCompra = new FacturasCompraGUI();
            gui_facturaCompra.setLocation(getDesktopPane().getWidth() / 2 - gui_facturaCompra.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui_facturaCompra.getHeight() / 2);
            getDesktopPane().add(gui_facturaCompra);
            Factura factura = RestClient.getRestTemplate()
                    .getForObject("/facturas/" + notasTotal.get(indexFilaSeleccionada).getIdFacturaCompra(), Factura.class);
            gui_facturaCompra.setVisible(true);
            gui_facturaCompra.buscarPorSerieYNroFactura(factura.getNumSerie(), factura.getNumFactura(),
                    factura.getTipoComprobante(), notasTotal.get(indexFilaSeleccionada).getIdProveedor());
            try {
                gui_facturaCompra.setSelected(true);
            } catch (PropertyVetoException ex) {
                String mensaje = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(mensaje + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean isNotaCredito(TipoDeComprobante tipoDeComprobante) {
        switch (tipoDeComprobante) {
            case NOTA_CREDITO_A:
            case NOTA_CREDITO_B:
            case NOTA_CREDITO_C:
            case NOTA_CREDITO_PRESUPUESTO:
            case NOTA_CREDITO_X:
            case NOTA_CREDITO_Y:
                return true;
            default:
                return false;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelResultados = new javax.swing.JPanel();
        sp_Resultados = new javax.swing.JScrollPane();
        tbl_Resultados = new javax.swing.JTable();
        btn_VerDetalle = new javax.swing.JButton();
        txt_ResultTotalCredito = new javax.swing.JFormattedTextField();
        txt_ResultTotalIVANotaCredito = new javax.swing.JFormattedTextField();
        lbl_TotalIVANotasCredito = new javax.swing.JLabel();
        lbl_TotalNotasCredito = new javax.swing.JLabel();
        btnVerFactura = new javax.swing.JButton();
        txt_ResultTotalIVANotaDebito = new javax.swing.JFormattedTextField();
        lbl_TotalIVANotasDebito = new javax.swing.JLabel();
        lbl_TotalNotasDebito = new javax.swing.JLabel();
        txt_ResultTotalDebito = new javax.swing.JFormattedTextField();
        panelFiltros = new javax.swing.JPanel();
        btn_Buscar = new javax.swing.JButton();
        lbl_cantResultados = new javax.swing.JLabel();
        chk_Fecha = new javax.swing.JCheckBox();
        dc_FechaDesde = new com.toedter.calendar.JDateChooser();
        dc_FechaHasta = new com.toedter.calendar.JDateChooser();
        chk_NumNota = new javax.swing.JCheckBox();
        txt_SerieNota = new javax.swing.JFormattedTextField();
        separador = new javax.swing.JLabel();
        txt_NroNota = new javax.swing.JFormattedTextField();
        chk_TipoNota = new javax.swing.JCheckBox();
        cmb_TipoNota = new javax.swing.JComboBox();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/SIC_16_square.png"))); // NOI18N
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        panelResultados.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultados"));

        tbl_Resultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Resultados.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tbl_Resultados.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);
        sp_Resultados.setViewportView(tbl_Resultados);

        btn_VerDetalle.setForeground(java.awt.Color.blue);
        btn_VerDetalle.setText("Ver Detalle");
        btn_VerDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_VerDetalleActionPerformed(evt);
            }
        });

        txt_ResultTotalCredito.setEditable(false);
        txt_ResultTotalCredito.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_ResultTotalCredito.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txt_ResultTotalIVANotaCredito.setEditable(false);
        txt_ResultTotalIVANotaCredito.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_ResultTotalIVANotaCredito.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        lbl_TotalIVANotasCredito.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_TotalIVANotasCredito.setText("Total IVA Credito:");

        lbl_TotalNotasCredito.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_TotalNotasCredito.setText("Total Credito:");

        btnVerFactura.setForeground(java.awt.Color.blue);
        btnVerFactura.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/PedidoFacturas_16x16.png"))); // NOI18N
        btnVerFactura.setText("Ver Factura");
        btnVerFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerFacturaActionPerformed(evt);
            }
        });

        txt_ResultTotalIVANotaDebito.setEditable(false);
        txt_ResultTotalIVANotaDebito.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_ResultTotalIVANotaDebito.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        lbl_TotalIVANotasDebito.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_TotalIVANotasDebito.setText("Total IVA Debito:");

        lbl_TotalNotasDebito.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_TotalNotasDebito.setText("Total Debito:");

        txt_ResultTotalDebito.setEditable(false);
        txt_ResultTotalDebito.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_ResultTotalDebito.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout panelResultadosLayout = new javax.swing.GroupLayout(panelResultados);
        panelResultados.setLayout(panelResultadosLayout);
        panelResultadosLayout.setHorizontalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_Resultados)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(btn_VerDetalle)
                .addGap(0, 0, 0)
                .addComponent(btnVerFactura)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 138, Short.MAX_VALUE)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_TotalIVANotasDebito, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_TotalNotasDebito, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txt_ResultTotalDebito)
                    .addComponent(txt_ResultTotalIVANotaDebito, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_TotalIVANotasCredito, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_TotalNotasCredito, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txt_ResultTotalCredito)
                    .addComponent(txt_ResultTotalIVANotaCredito, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnVerFactura, btn_VerDetalle});

        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelResultadosLayout.createSequentialGroup()
                            .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lbl_TotalIVANotasCredito)
                                .addComponent(txt_ResultTotalIVANotaCredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lbl_TotalNotasCredito)
                                .addComponent(txt_ResultTotalCredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(panelResultadosLayout.createSequentialGroup()
                            .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lbl_TotalIVANotasDebito)
                                .addComponent(txt_ResultTotalIVANotaDebito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lbl_TotalNotasDebito)
                                .addComponent(txt_ResultTotalDebito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn_VerDetalle)
                        .addComponent(btnVerFactura))))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnVerFactura, btn_VerDetalle});

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lbl_TotalIVANotasCredito, lbl_TotalNotasCredito});

        panelFiltros.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

        btn_Buscar.setForeground(java.awt.Color.blue);
        btn_Buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btn_Buscar.setText("Buscar");
        btn_Buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_BuscarActionPerformed(evt);
            }
        });

        lbl_cantResultados.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        chk_Fecha.setText("Fecha Nota:");
        chk_Fecha.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_FechaItemStateChanged(evt);
            }
        });

        dc_FechaDesde.setDateFormatString("dd/MM/yyyy");
        dc_FechaDesde.setEnabled(false);

        dc_FechaHasta.setDateFormatString("dd/MM/yyyy");
        dc_FechaHasta.setEnabled(false);

        chk_NumNota.setText("Nº de Nota:");
        chk_NumNota.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_NumNotaItemStateChanged(evt);
            }
        });

        txt_SerieNota.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_SerieNota.setText("0");
        txt_SerieNota.setEnabled(false);
        txt_SerieNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_SerieNotaActionPerformed(evt);
            }
        });
        txt_SerieNota.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_SerieNotaKeyTyped(evt);
            }
        });

        separador.setFont(new java.awt.Font("DejaVu Sans", 0, 15)); // NOI18N
        separador.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        separador.setText("-");

        txt_NroNota.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_NroNota.setText("0");
        txt_NroNota.setEnabled(false);
        txt_NroNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_NroNotaActionPerformed(evt);
            }
        });
        txt_NroNota.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_NroNotaKeyTyped(evt);
            }
        });

        chk_TipoNota.setText("Tipo de Nota:");
        chk_TipoNota.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_TipoNotaItemStateChanged(evt);
            }
        });

        cmb_TipoNota.setEnabled(false);

        javax.swing.GroupLayout panelFiltrosLayout = new javax.swing.GroupLayout(panelFiltros);
        panelFiltros.setLayout(panelFiltrosLayout);
        panelFiltrosLayout.setHorizontalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addComponent(btn_Buscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_cantResultados, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE))
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addComponent(chk_TipoNota)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmb_TipoNota, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(chk_NumNota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chk_Fecha, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panelFiltrosLayout.createSequentialGroup()
                                .addComponent(txt_SerieNota, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(separador, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_NroNota))
                            .addGroup(panelFiltrosLayout.createSequentialGroup()
                                .addComponent(dc_FechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dc_FechaHasta, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelFiltrosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {chk_Fecha, chk_NumNota, chk_TipoNota});

        panelFiltrosLayout.setVerticalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Fecha)
                    .addComponent(dc_FechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dc_FechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_SerieNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(separador)
                    .addComponent(txt_NroNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_NumNota))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chk_TipoNota)
                    .addComponent(cmb_TipoNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_cantResultados, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Buscar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelResultados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void chk_FechaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_FechaItemStateChanged
        if (chk_Fecha.isSelected() == true) {
            dc_FechaDesde.setEnabled(true);
            dc_FechaHasta.setEnabled(true);
            dc_FechaDesde.requestFocus();
        } else {
            dc_FechaDesde.setEnabled(false);
            dc_FechaHasta.setEnabled(false);
        }
}//GEN-LAST:event_chk_FechaItemStateChanged

    private void btn_BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarActionPerformed
        this.resetScroll();
        this.limpiarJTable();
        this.buscar(true);
}//GEN-LAST:event_btn_BuscarActionPerformed

    private void chk_NumNotaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_NumNotaItemStateChanged
        if (chk_NumNota.isSelected() == true) {
            txt_NroNota.setEnabled(true);
            txt_SerieNota.setEnabled(true);
            txt_SerieNota.requestFocus();
        } else {
            txt_NroNota.setEnabled(false);
            txt_SerieNota.setEnabled(false);
        }
    }//GEN-LAST:event_chk_NumNotaItemStateChanged

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        try {
            this.setSize(sizeInternalFrame);
            this.setColumnas();
            this.setMaximum(true);
            dc_FechaDesde.setDate(new Date());
            dc_FechaHasta.setDate(new Date());
            this.cambiarEstadoDeComponentesSegunRolUsuario();
            this.setTitle("Administrar Notas de Compra");
        } catch (PropertyVetoException ex) {
            String mensaje = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(mensaje + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }        
    }//GEN-LAST:event_formInternalFrameOpened

    private void chk_TipoNotaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_TipoNotaItemStateChanged
        if (chk_TipoNota.isSelected() == true) {
            cmb_TipoNota.setEnabled(true);
            this.cargarTiposDeNota();
            cmb_TipoNota.requestFocus();
        } else {
            cmb_TipoNota.setEnabled(false);
            cmb_TipoNota.removeAllItems();
        }
    }//GEN-LAST:event_chk_TipoNotaItemStateChanged

    private void txt_SerieNotaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_SerieNotaKeyTyped
        Utilidades.controlarEntradaSoloNumerico(evt);
    }//GEN-LAST:event_txt_SerieNotaKeyTyped

    private void txt_NroNotaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_NroNotaKeyTyped
        Utilidades.controlarEntradaSoloNumerico(evt);
    }//GEN-LAST:event_txt_NroNotaKeyTyped

    private void txt_SerieNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_SerieNotaActionPerformed
        btn_BuscarActionPerformed(null);
    }//GEN-LAST:event_txt_SerieNotaActionPerformed

    private void txt_NroNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_NroNotaActionPerformed
        btn_BuscarActionPerformed(null);
    }//GEN-LAST:event_txt_NroNotaActionPerformed

    private void btn_VerDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_VerDetalleActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            if (tbl_Resultados.getSelectedRow() != -1 && tbl_Resultados.getSelectedRowCount() == 1) {
                int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
                long idNota = notasTotal.get(indexFilaSeleccionada).getIdNota();
                if (isNotaCredito(notasTotal.get(indexFilaSeleccionada).getTipoComprobante())) {
                    DetalleNotaCreditoGUI detalleNotaCreditoGUI = new DetalleNotaCreditoGUI(idNota);
                    detalleNotaCreditoGUI.setLocationRelativeTo(this);
                    detalleNotaCreditoGUI.setVisible(true);
                } else {
                    DetalleNotaDebitoGUI detalleNotaDebitoGUI = new DetalleNotaDebitoGUI(idNota);
                    detalleNotaDebitoGUI.setLocationRelativeTo(this);
                    detalleNotaDebitoGUI.setVisible(true);
                }
            }
        }
    }//GEN-LAST:event_btn_VerDetalleActionPerformed

    private void btnVerFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerFacturaActionPerformed
        this.verFacturaCompra();
    }//GEN-LAST:event_btnVerFacturaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnVerFactura;
    private javax.swing.JButton btn_Buscar;
    private javax.swing.JButton btn_VerDetalle;
    private javax.swing.JCheckBox chk_Fecha;
    private javax.swing.JCheckBox chk_NumNota;
    private javax.swing.JCheckBox chk_TipoNota;
    private javax.swing.JComboBox cmb_TipoNota;
    private com.toedter.calendar.JDateChooser dc_FechaDesde;
    private com.toedter.calendar.JDateChooser dc_FechaHasta;
    private javax.swing.JLabel lbl_TotalIVANotasCredito;
    private javax.swing.JLabel lbl_TotalIVANotasDebito;
    private javax.swing.JLabel lbl_TotalNotasCredito;
    private javax.swing.JLabel lbl_TotalNotasDebito;
    private javax.swing.JLabel lbl_cantResultados;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JLabel separador;
    private javax.swing.JScrollPane sp_Resultados;
    private javax.swing.JTable tbl_Resultados;
    private javax.swing.JFormattedTextField txt_NroNota;
    private javax.swing.JFormattedTextField txt_ResultTotalCredito;
    private javax.swing.JFormattedTextField txt_ResultTotalDebito;
    private javax.swing.JFormattedTextField txt_ResultTotalIVANotaCredito;
    private javax.swing.JFormattedTextField txt_ResultTotalIVANotaDebito;
    private javax.swing.JFormattedTextField txt_SerieNota;
    // End of variables declaration//GEN-END:variables
}
