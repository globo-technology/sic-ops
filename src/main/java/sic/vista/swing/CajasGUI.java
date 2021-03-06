package sic.vista.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Caja;
import sic.modelo.SucursalActiva;
import sic.modelo.Usuario;
import sic.modelo.PaginaRespuestaRest;
import sic.modelo.Rol;
import sic.modelo.UsuarioActivo;
import sic.modelo.criteria.BusquedaCajaCriteria;
import sic.util.ColoresEstadosRenderer;
import sic.util.DecimalesRenderer;
import sic.util.FechasRenderer;
import sic.util.FormatosFechaHora;
import sic.util.Utilidades;

public class CajasGUI extends JInternalFrame {

    private ModeloTabla modeloTablaCajas = new ModeloTabla();
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private List<Caja> cajasTotal = new ArrayList<>();
    private List<Caja> cajasParcial = new ArrayList<>();
    private Usuario usuarioSeleccionadoApertura;
    private Usuario usuarioSeleccionadoCierre;
    private static int totalElementosBusqueda;
    private static int NUMERO_PAGINA = 0;    
    private final Dimension sizeInternalFrame = new Dimension(880, 600);

    public CajasGUI() {
        this.initComponents();        
        sp_Cajas.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            int va = scrollBar.getVisibleAmount() + 10;
            if (scrollBar.getValue() >= (scrollBar.getMaximum() - va)) {
                if (cajasTotal.size() >= 10) {
                    NUMERO_PAGINA += 1;
                    buscar(false);
                }
            }
        });
    }

    private void setColumnasCaja() {       
        //nombres de columnas
        String[] encabezados = new String[8];
        encabezados[0] = "Estado";
        encabezados[1] = "Fecha Apertura";
        encabezados[2] = "Fecha Cierre";
        encabezados[3] = "Usuario Apertura";
        encabezados[4] = "Usuario de Cierre";
        encabezados[5] = "Apertura";
        encabezados[6] = "Saldo Sistema"; 
        encabezados[7] = "Saldo Real";
        modeloTablaCajas.setColumnIdentifiers(encabezados);
        tbl_Cajas.setModel(modeloTablaCajas);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaCajas.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = LocalDateTime.class;
        tipos[2] = LocalDateTime.class;
        tipos[3] = String.class;
        tipos[4] = String.class;
        tipos[5] = BigDecimal.class;
        tipos[6] = BigDecimal.class;
        tipos[7] = BigDecimal.class;
        modeloTablaCajas.setClaseColumnas(tipos);
        tbl_Cajas.getTableHeader().setReorderingAllowed(false);
        tbl_Cajas.getTableHeader().setResizingAllowed(true);
        //tamanios de columnas
        tbl_Cajas.getColumnModel().getColumn(0).setPreferredWidth(0);
        tbl_Cajas.getColumnModel().getColumn(1).setPreferredWidth(60);
        tbl_Cajas.getColumnModel().getColumn(2).setPreferredWidth(60);
        tbl_Cajas.getColumnModel().getColumn(3).setPreferredWidth(80);
        tbl_Cajas.getColumnModel().getColumn(4).setPreferredWidth(80);
        tbl_Cajas.getColumnModel().getColumn(5).setPreferredWidth(25);
        tbl_Cajas.getColumnModel().getColumn(6).setPreferredWidth(20);
        tbl_Cajas.getColumnModel().getColumn(7).setPreferredWidth(20);
        //renderers
        tbl_Cajas.setDefaultRenderer(BigDecimal.class, new DecimalesRenderer());
        tbl_Cajas.getColumnModel().getColumn(1).setCellRenderer(new FechasRenderer(FormatosFechaHora.FORMATO_FECHAHORA_HISPANO));
        tbl_Cajas.getColumnModel().getColumn(2).setCellRenderer(new FechasRenderer(FormatosFechaHora.FORMATO_FECHAHORA_HISPANO));
    }

    private void buscar(boolean calcularResultados) {
        this.cambiarEstadoEnabledComponentes(false);
        BusquedaCajaCriteria criteria = BusquedaCajaCriteria.builder().build();
        criteria.setIdSucursal(SucursalActiva.getInstance().getSucursal().getIdSucursal());
        if (chk_Fecha.isSelected()) {
            criteria.setFechaDesde((dc_FechaDesde.getDate() != null)
                    ? LocalDateTime.ofInstant(dc_FechaDesde.getDate().toInstant(), ZoneId.systemDefault())
                    : null);
            criteria.setFechaHasta((dc_FechaHasta.getDate() != null)
                    ? LocalDateTime.ofInstant(dc_FechaHasta.getDate().toInstant(), ZoneId.systemDefault())
                    : null);
        }
        if (chk_UsuarioApertura.isSelected() && usuarioSeleccionadoApertura != null) criteria.setIdUsuarioApertura(usuarioSeleccionadoApertura.getIdUsuario());
        if (chk_UsuarioCierre.isSelected() && usuarioSeleccionadoCierre != null) criteria.setIdUsuarioCierre(usuarioSeleccionadoCierre.getIdUsuario());
        criteria.setPagina(NUMERO_PAGINA);
        try {
            HttpEntity<BusquedaCajaCriteria> requestEntity = new HttpEntity<>(criteria);
            PaginaRespuestaRest<Caja> response = RestClient.getRestTemplate()
                    .exchange("/cajas/busqueda/criteria", HttpMethod.POST, requestEntity,
                            new ParameterizedTypeReference<PaginaRespuestaRest<Caja>>() {})
                    .getBody();
            totalElementosBusqueda = response.getTotalElements();
            cajasParcial = response.getContent();
            cajasTotal.addAll(cajasParcial);
            this.cargarResultadosAlTable();
            if (calcularResultados) {
                ftxt_TotalSistema.setValue(RestClient.getRestTemplate()
                        .postForObject("/cajas/saldo-sistema", criteria, BigDecimal.class));
                ftxt_TotalReal.setValue(RestClient.getRestTemplate()
                        .postForObject("/cajas/saldo-real", criteria, BigDecimal.class));
            }
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
        chk_UsuarioApertura.setEnabled(status);
        if (status == true && chk_UsuarioApertura.isSelected() == true) {
            txtUsuarioApertura.setEnabled(true);
            btnBuscarUsuariosApertura.setEnabled(true);
        } else {
            txtUsuarioApertura.setEnabled(false);
            btnBuscarUsuariosApertura.setEnabled(false);
        }
        if (status == true && chk_UsuarioCierre.isSelected() == true) {
            txtUsuarioCierre.setEnabled(true);
            btnBuscarUsuariosCierre.setEnabled(true);
        } else {
            txtUsuarioCierre.setEnabled(false);
            btnBuscarUsuariosCierre.setEnabled(false);
        }
        btn_buscar.setEnabled(status);        
        tbl_Cajas.setEnabled(status);
        sp_Cajas.setEnabled(status);
        btn_AbrirCaja.setEnabled(status);
        btn_eliminarCaja.setEnabled(status);
        btn_verDetalle.setEnabled(status);
        tbl_Cajas.requestFocus();
    }

    private void cargarResultadosAlTable() {
        cajasParcial.stream().map(caja -> {
            Object[] fila = new Object[8];
            fila[0] = caja.getEstado();
            fila[1] = caja.getFechaApertura();
            if (caja.getFechaCierre() != null) fila[2] = caja.getFechaCierre();
            fila[3] = caja.getNombreUsuarioAbreCaja();            
            fila[4] = caja.getNombreUsuarioCierraCaja();
            fila[5] = caja.getSaldoApertura();
            fila[6] = caja.getSaldoSistema();
            fila[7] = caja.getSaldoReal();
            return fila;
        }).forEachOrdered(modeloTablaCajas::addRow);
        tbl_Cajas.setModel(modeloTablaCajas);
        tbl_Cajas.getColumnModel().getColumn(0).setCellRenderer(new ColoresEstadosRenderer());
        lbl_cantidadMostrar.setText(totalElementosBusqueda + " Cajas encontradas");
    }

    private void limpiarResultados() {
        NUMERO_PAGINA = 0;
        cajasTotal.clear();
        cajasParcial.clear();
        Point p = new Point(0, 0);
        sp_Cajas.getViewport().setViewPosition(p);
        modeloTablaCajas = new ModeloTabla();
        tbl_Cajas.setModel(modeloTablaCajas);
        this.setColumnasCaja();
    }

    private void abrirNuevaCaja() {
        boolean ultimaCajaAbierta = RestClient.getRestTemplate()
                .getForObject("/cajas/sucursales/" + SucursalActiva.getInstance().getSucursal().getIdSucursal() + "/ultima-caja-abierta",
                        boolean.class);
        if (!ultimaCajaAbierta) {
            String saldoApertura = JOptionPane.showInputDialog(this,
                    "Saldo Apertura: \n", "Abrir Caja", JOptionPane.QUESTION_MESSAGE);
            if (saldoApertura != null) {
                try {
                    RestClient.getRestTemplate().postForObject("/cajas/apertura/sucursales/" + SucursalActiva.getInstance().getSucursal().getIdSucursal()
                            + "?saldoApertura=" + new BigDecimal(saldoApertura), null, Caja.class);
                } catch (RestClientResponseException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (ResourceAccessException ex) {
                    LOGGER.error(ex.getMessage());
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } catch (NumberFormatException ex) {
                    LOGGER.error(ex.getMessage());
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_error_formato_numero"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
                this.limpiarResultados();
                this.buscar(true);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_caja_anterior_abierta"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cambiarEstadoDeComponentesSegunRolUsuario() {
        List<Rol> rolesDeUsuarioActivo = UsuarioActivo.getInstance().getUsuario().getRoles();
        if (rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)) {
            btn_eliminarCaja.setEnabled(true);
        } else {
            btn_eliminarCaja.setEnabled(false);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_Filtros = new javax.swing.JPanel();
        chk_Fecha = new javax.swing.JCheckBox();
        dc_FechaDesde = new com.toedter.calendar.JDateChooser();
        dc_FechaHasta = new com.toedter.calendar.JDateChooser();
        btn_buscar = new javax.swing.JButton();
        chk_UsuarioApertura = new javax.swing.JCheckBox();
        lbl_cantidadMostrar = new javax.swing.JLabel();
        chk_UsuarioCierre = new javax.swing.JCheckBox();
        txtUsuarioApertura = new javax.swing.JTextField();
        txtUsuarioCierre = new javax.swing.JTextField();
        btnBuscarUsuariosApertura = new javax.swing.JButton();
        btnBuscarUsuariosCierre = new javax.swing.JButton();
        pnl_Cajas = new javax.swing.JPanel();
        sp_Cajas = new javax.swing.JScrollPane();
        tbl_Cajas = new javax.swing.JTable();
        btn_AbrirCaja = new javax.swing.JButton();
        btn_verDetalle = new javax.swing.JButton();
        btn_eliminarCaja = new javax.swing.JButton();
        lbl_TotalSistema = new javax.swing.JLabel();
        ftxt_TotalSistema = new javax.swing.JFormattedTextField();
        lbl_TotalCierre = new javax.swing.JLabel();
        ftxt_TotalReal = new javax.swing.JFormattedTextField();
        btn_ReabrirCaja = new javax.swing.JButton();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Administrar Cajas");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Caja_16x16.png"))); // NOI18N
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                CajasGUI.this.internalFrameOpened(evt);
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

        pnl_Filtros.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

        chk_Fecha.setText("Fecha:");
        chk_Fecha.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_FechaItemStateChanged(evt);
            }
        });

        dc_FechaDesde.setDateFormatString("dd/MM/yyyy");
        dc_FechaDesde.setEnabled(false);

        dc_FechaHasta.setDateFormatString("dd/MM/yyyy");
        dc_FechaHasta.setEnabled(false);

        btn_buscar.setForeground(java.awt.Color.blue);
        btn_buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btn_buscar.setText("Buscar");
        btn_buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_buscarActionPerformed(evt);
            }
        });

        chk_UsuarioApertura.setText("Usuario Apertura:");
        chk_UsuarioApertura.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_UsuarioAperturaItemStateChanged(evt);
            }
        });

        lbl_cantidadMostrar.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        chk_UsuarioCierre.setText("Usuario Cierre:");
        chk_UsuarioCierre.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_UsuarioCierreItemStateChanged(evt);
            }
        });

        txtUsuarioApertura.setEditable(false);
        txtUsuarioApertura.setEnabled(false);
        txtUsuarioApertura.setOpaque(false);

        txtUsuarioCierre.setEditable(false);
        txtUsuarioCierre.setEnabled(false);
        txtUsuarioCierre.setOpaque(false);

        btnBuscarUsuariosApertura.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btnBuscarUsuariosApertura.setEnabled(false);
        btnBuscarUsuariosApertura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarUsuariosAperturaActionPerformed(evt);
            }
        });

        btnBuscarUsuariosCierre.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btnBuscarUsuariosCierre.setEnabled(false);
        btnBuscarUsuariosCierre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarUsuariosCierreActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_FiltrosLayout = new javax.swing.GroupLayout(pnl_Filtros);
        pnl_Filtros.setLayout(pnl_FiltrosLayout);
        pnl_FiltrosLayout.setHorizontalGroup(
            pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_FiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_FiltrosLayout.createSequentialGroup()
                        .addComponent(btn_buscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_cantidadMostrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnl_FiltrosLayout.createSequentialGroup()
                        .addGroup(pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chk_UsuarioApertura)
                            .addComponent(chk_Fecha)
                            .addComponent(chk_UsuarioCierre))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnl_FiltrosLayout.createSequentialGroup()
                                .addComponent(dc_FechaDesde, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dc_FechaHasta, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))
                            .addGroup(pnl_FiltrosLayout.createSequentialGroup()
                                .addGroup(pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtUsuarioApertura)
                                    .addComponent(txtUsuarioCierre))
                                .addGap(0, 0, 0)
                                .addGroup(pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnBuscarUsuariosCierre, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnBuscarUsuariosApertura, javax.swing.GroupLayout.Alignment.TRAILING))))))
                .addGap(6, 6, 6))
        );

        pnl_FiltrosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {chk_Fecha, chk_UsuarioApertura, chk_UsuarioCierre});

        pnl_FiltrosLayout.setVerticalGroup(
            pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_FiltrosLayout.createSequentialGroup()
                .addGroup(pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Fecha)
                    .addComponent(dc_FechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dc_FechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_UsuarioApertura)
                    .addComponent(txtUsuarioApertura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarUsuariosApertura))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_UsuarioCierre)
                    .addComponent(txtUsuarioCierre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarUsuariosCierre))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_buscar)
                    .addComponent(lbl_cantidadMostrar))
                .addContainerGap())
        );

        pnl_FiltrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_buscar, lbl_cantidadMostrar});

        pnl_FiltrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnBuscarUsuariosApertura, txtUsuarioApertura});

        pnl_FiltrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnBuscarUsuariosCierre, txtUsuarioCierre});

        pnl_Cajas.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultados"));

        tbl_Cajas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Cajas.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sp_Cajas.setViewportView(tbl_Cajas);

        btn_AbrirCaja.setForeground(java.awt.Color.blue);
        btn_AbrirCaja.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AbrirCaja_16x16.png"))); // NOI18N
        btn_AbrirCaja.setText("Abrir Nueva");
        btn_AbrirCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AbrirCajaActionPerformed(evt);
            }
        });

        btn_verDetalle.setForeground(java.awt.Color.blue);
        btn_verDetalle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/target_16x16.png"))); // NOI18N
        btn_verDetalle.setText("Ver Detalle");
        btn_verDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_verDetalleActionPerformed(evt);
            }
        });

        btn_eliminarCaja.setForeground(java.awt.Color.blue);
        btn_eliminarCaja.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Cancel_16x16.png"))); // NOI18N
        btn_eliminarCaja.setText("Eliminar");
        btn_eliminarCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_eliminarCajaActionPerformed(evt);
            }
        });

        lbl_TotalSistema.setText("Total Sistema:");

        ftxt_TotalSistema.setEditable(false);
        ftxt_TotalSistema.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        ftxt_TotalSistema.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        lbl_TotalCierre.setText("Total Real:");

        ftxt_TotalReal.setEditable(false);
        ftxt_TotalReal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        ftxt_TotalReal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        btn_ReabrirCaja.setForeground(java.awt.Color.blue);
        btn_ReabrirCaja.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/ReAbrirCaja_16x16.png"))); // NOI18N
        btn_ReabrirCaja.setText("Reabrir");
        btn_ReabrirCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ReabrirCajaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_CajasLayout = new javax.swing.GroupLayout(pnl_Cajas);
        pnl_Cajas.setLayout(pnl_CajasLayout);
        pnl_CajasLayout.setHorizontalGroup(
            pnl_CajasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_CajasLayout.createSequentialGroup()
                .addComponent(btn_AbrirCaja)
                .addGap(0, 0, 0)
                .addComponent(btn_ReabrirCaja)
                .addGap(0, 0, 0)
                .addComponent(btn_verDetalle)
                .addGap(0, 0, 0)
                .addComponent(btn_eliminarCaja)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnl_CajasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_CajasLayout.createSequentialGroup()
                        .addComponent(lbl_TotalSistema)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ftxt_TotalSistema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_CajasLayout.createSequentialGroup()
                        .addComponent(lbl_TotalCierre)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ftxt_TotalReal, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addComponent(sp_Cajas, javax.swing.GroupLayout.DEFAULT_SIZE, 888, Short.MAX_VALUE)
        );

        pnl_CajasLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_AbrirCaja, btn_ReabrirCaja, btn_eliminarCaja, btn_verDetalle});

        pnl_CajasLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lbl_TotalCierre, lbl_TotalSistema});

        pnl_CajasLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {ftxt_TotalReal, ftxt_TotalSistema});

        pnl_CajasLayout.setVerticalGroup(
            pnl_CajasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_CajasLayout.createSequentialGroup()
                .addComponent(sp_Cajas, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_CajasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnl_CajasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn_verDetalle)
                        .addComponent(btn_eliminarCaja)
                        .addComponent(btn_AbrirCaja)
                        .addComponent(btn_ReabrirCaja))
                    .addGroup(pnl_CajasLayout.createSequentialGroup()
                        .addGroup(pnl_CajasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbl_TotalSistema)
                            .addComponent(ftxt_TotalSistema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)
                        .addGroup(pnl_CajasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbl_TotalCierre)
                            .addComponent(ftxt_TotalReal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );

        pnl_CajasLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_AbrirCaja, btn_ReabrirCaja, btn_eliminarCaja, btn_verDetalle});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_Cajas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_Filtros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_Filtros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_Cajas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chk_FechaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_FechaItemStateChanged
        //Pregunta el estado actual del checkBox
        if (chk_Fecha.isSelected() == true) {
            dc_FechaDesde.setEnabled(true);
            dc_FechaHasta.setEnabled(true);          
            dc_FechaDesde.requestFocus();
        } else {
            dc_FechaDesde.setEnabled(false);
            dc_FechaHasta.setEnabled(false);
        }
    }//GEN-LAST:event_chk_FechaItemStateChanged

    private void btn_buscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_buscarActionPerformed
        this.limpiarResultados();
        this.buscar(true);        
    }//GEN-LAST:event_btn_buscarActionPerformed
    
    private void btn_verDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_verDetalleActionPerformed
        if (tbl_Cajas.getSelectedRow() != -1) {
            int indice = Utilidades.getSelectedRowModelIndice(tbl_Cajas);
            try {
                Caja caja = RestClient.getRestTemplate()
                        .getForObject("/cajas/ " + this.cajasTotal.get(indice).getIdCaja(), Caja.class);
                JInternalFrame iFrameCaja = new DetalleCajaGUI(caja);
                iFrameCaja.setLocation(getDesktopPane().getWidth() / 2 - iFrameCaja.getWidth() / 2,
                        getDesktopPane().getHeight() / 2 - iFrameCaja.getHeight() / 2);
                getDesktopPane().add(iFrameCaja);
                iFrameCaja.setVisible(true);
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_verDetalleActionPerformed

    private void btn_eliminarCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_eliminarCajaActionPerformed
        if (tbl_Cajas.getSelectedRow() != -1) {
            int confirmacionEliminacion = JOptionPane.showConfirmDialog(this,
                    "¿Esta seguro que desea eliminar la caja seleccionada?",
                    "Eliminar", JOptionPane.YES_NO_OPTION);
            try {
                if (confirmacionEliminacion == JOptionPane.YES_OPTION) {
                    int indiceDelModel = Utilidades.getSelectedRowModelIndice(tbl_Cajas);
                    RestClient.getRestTemplate().delete("/cajas/" + this.cajasTotal.get(indiceDelModel).getIdCaja());
                }
                this.limpiarResultados();
                this.buscar(true);
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_eliminarCajaActionPerformed

    private void chk_UsuarioAperturaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_UsuarioAperturaItemStateChanged
        if (chk_UsuarioApertura.isSelected() == true) {
            txtUsuarioApertura.setEnabled(true);
            btnBuscarUsuariosApertura.setEnabled(true);
        } else {
            txtUsuarioApertura.setEnabled(false);
            btnBuscarUsuariosApertura.setEnabled(false);
        }
    }//GEN-LAST:event_chk_UsuarioAperturaItemStateChanged

    private void btn_AbrirCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AbrirCajaActionPerformed
        this.abrirNuevaCaja();
    }//GEN-LAST:event_btn_AbrirCajaActionPerformed

    private void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_internalFrameOpened
        this.setSize(sizeInternalFrame);
        this.setColumnasCaja();
        txtUsuarioApertura.setEnabled(false);
        btnBuscarUsuariosApertura.setEnabled(false);
        txtUsuarioCierre.setEnabled(false);
        btnBuscarUsuariosCierre.setEnabled(false);
        dc_FechaDesde.setDate(new Date());
        dc_FechaHasta.setDate(new Date());
        this.cambiarEstadoDeComponentesSegunRolUsuario();
        try {
            this.setMaximum(true);            
        } catch (PropertyVetoException ex) {
            String mensaje = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(mensaje + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_internalFrameOpened

    private void chk_UsuarioCierreItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_UsuarioCierreItemStateChanged
        if (chk_UsuarioCierre.isSelected() == true) {
            txtUsuarioCierre.setEnabled(true);
            btnBuscarUsuariosCierre.setEnabled(true);
        } else {
            txtUsuarioCierre.setEnabled(false);
            btnBuscarUsuariosCierre.setEnabled(false);
        }
    }//GEN-LAST:event_chk_UsuarioCierreItemStateChanged

    private void btn_ReabrirCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ReabrirCajaActionPerformed
        if (tbl_Cajas.getSelectedRow() != -1) {
            int indice = Utilidades.getSelectedRowModelIndice(tbl_Cajas);
            String monto = JOptionPane.showInputDialog(this,
                    "Saldo Apertura: \n", "Reabrir Caja", JOptionPane.QUESTION_MESSAGE);
            if (monto != null) {
                try {
                    RestClient.getRestTemplate().put("/cajas/" + this.cajasTotal.get(indice).getIdCaja() + "/reapertura?monto=" + new BigDecimal(monto), null);
                } catch (RestClientResponseException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (ResourceAccessException ex) {
                    LOGGER.error(ex.getMessage());
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } catch (NumberFormatException ex) {
                    LOGGER.error(ex.getMessage());
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_error_formato_numero"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
                this.limpiarResultados();
                this.buscar(true);
            }
        }
    }//GEN-LAST:event_btn_ReabrirCajaActionPerformed

    private void btnBuscarUsuariosAperturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarUsuariosAperturaActionPerformed
        Rol[] rolesParaFiltrar = new Rol[]{Rol.ADMINISTRADOR, Rol.ENCARGADO};
        BuscarUsuariosGUI buscarUsuariosGUI = new BuscarUsuariosGUI(rolesParaFiltrar, "Buscar Usuario");
        buscarUsuariosGUI.setModal(true);
        buscarUsuariosGUI.setLocationRelativeTo(this);
        buscarUsuariosGUI.setVisible(true);
        if (buscarUsuariosGUI.getUsuarioSeleccionado() != null) {
            usuarioSeleccionadoApertura = buscarUsuariosGUI.getUsuarioSeleccionado();
            txtUsuarioApertura.setText(usuarioSeleccionadoApertura.toString());
        }
    }//GEN-LAST:event_btnBuscarUsuariosAperturaActionPerformed

    private void btnBuscarUsuariosCierreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarUsuariosCierreActionPerformed
        Rol[] rolesParaFiltrar = new Rol[]{Rol.ADMINISTRADOR, Rol.ENCARGADO};
        BuscarUsuariosGUI buscarUsuariosGUI = new BuscarUsuariosGUI(rolesParaFiltrar, "Buscar Usuario");
        buscarUsuariosGUI.setModal(true);
        buscarUsuariosGUI.setLocationRelativeTo(this);
        buscarUsuariosGUI.setVisible(true);
        if (buscarUsuariosGUI.getUsuarioSeleccionado() != null) {
            usuarioSeleccionadoCierre = buscarUsuariosGUI.getUsuarioSeleccionado();
            txtUsuarioCierre.setText(usuarioSeleccionadoCierre.toString());
        }
    }//GEN-LAST:event_btnBuscarUsuariosCierreActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarUsuariosApertura;
    private javax.swing.JButton btnBuscarUsuariosCierre;
    private javax.swing.JButton btn_AbrirCaja;
    private javax.swing.JButton btn_ReabrirCaja;
    private javax.swing.JButton btn_buscar;
    private javax.swing.JButton btn_eliminarCaja;
    private javax.swing.JButton btn_verDetalle;
    private javax.swing.JCheckBox chk_Fecha;
    private javax.swing.JCheckBox chk_UsuarioApertura;
    private javax.swing.JCheckBox chk_UsuarioCierre;
    private com.toedter.calendar.JDateChooser dc_FechaDesde;
    private com.toedter.calendar.JDateChooser dc_FechaHasta;
    private javax.swing.JFormattedTextField ftxt_TotalReal;
    private javax.swing.JFormattedTextField ftxt_TotalSistema;
    private javax.swing.JLabel lbl_TotalCierre;
    private javax.swing.JLabel lbl_TotalSistema;
    private javax.swing.JLabel lbl_cantidadMostrar;
    private javax.swing.JPanel pnl_Cajas;
    private javax.swing.JPanel pnl_Filtros;
    private javax.swing.JScrollPane sp_Cajas;
    private javax.swing.JTable tbl_Cajas;
    private javax.swing.JTextField txtUsuarioApertura;
    private javax.swing.JTextField txtUsuarioCierre;
    // End of variables declaration//GEN-END:variables

}
