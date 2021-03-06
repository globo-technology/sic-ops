package sic.vista.swing;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Caja;
import sic.modelo.SucursalActiva;
import sic.modelo.FormaDePago;
import sic.modelo.Gasto;
import sic.modelo.EstadoCaja;
import sic.modelo.MovimientoCaja;
import sic.modelo.TipoDeComprobante;
import sic.util.ColoresNumerosRenderer;
import sic.util.FechasRenderer;
import sic.util.FormatosFechaHora;
import sic.util.Utilidades;

public class DetalleCajaGUI extends JInternalFrame {

    private ModeloTabla modeloTablaBalance = new ModeloTabla();
    private ModeloTabla modeloTablaResumen = new ModeloTabla();
    private final HashMap<Long, List<MovimientoCaja>> movimientos = new HashMap<>();
    private Long idFormaDePagoSeleccionada;
    private Caja caja;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeInternalFrame = new Dimension(880, 600);    

    public DetalleCajaGUI(Caja caja) {
        this.initComponents();
        this.caja = caja;        
    }  
    
    private void limpiarTablaResumen() {
        modeloTablaResumen = new ModeloTabla(); 
        tbl_Resumen.setModel(modeloTablaResumen);
        this.setColumnasTablaResumen();
    }
    
    private void limpiarTablaMovimientos() {
        modeloTablaBalance = new ModeloTabla(); 
        tbl_Movimientos.setModel(modeloTablaBalance);
        this.setColumnasTablaMovimientos();
    }

    private void setColumnasTablaMovimientos() {
        //nombres de columnas
        String[] encabezados = new String[3];
        encabezados[0] = "Concepto";
        encabezados[1] = "Fecha";
        encabezados[2] = "Monto";
        modeloTablaBalance.setColumnIdentifiers(encabezados);
        tbl_Movimientos.setModel(modeloTablaBalance);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaBalance.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = LocalDateTime.class;
        tipos[2] = BigDecimal.class;
        modeloTablaBalance.setClaseColumnas(tipos);
        tbl_Movimientos.getTableHeader().setReorderingAllowed(false);
        tbl_Movimientos.getTableHeader().setResizingAllowed(true);
        //Tamanios de columnas
        tbl_Movimientos.getColumnModel().getColumn(0).setPreferredWidth(200);
        tbl_Movimientos.getColumnModel().getColumn(1).setPreferredWidth(5);
        //Renderers
        tbl_Movimientos.getColumnModel().getColumn(2).setCellRenderer(new ColoresNumerosRenderer());
        tbl_Movimientos.getColumnModel().getColumn(1).setCellRenderer(new FechasRenderer(FormatosFechaHora.FORMATO_FECHAHORA_HISPANO));
    }

    private void setColumnasTablaResumen() {
        //nombres de columnas
        String[] encabezados = new String[4];
        encabezados[0] = "idFormaDePago";
        encabezados[1] = "Forma de Pago";
        encabezados[2] = "Afecta la Caja";
        encabezados[3] = "Total";
        modeloTablaResumen.setColumnIdentifiers(encabezados);
        tbl_Resumen.setModel(modeloTablaResumen);        
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaResumen.getColumnCount()];
        tipos[0] = Long.class;
        tipos[1] = String.class;
        tipos[2] = Boolean.class;
        tipos[3] = BigDecimal.class;
        modeloTablaResumen.setClaseColumnas(tipos);
        tbl_Resumen.getTableHeader().setReorderingAllowed(false);
        tbl_Resumen.getTableHeader().setResizingAllowed(true);
        //Tamanios de columnas
        tbl_Resumen.getColumnModel().getColumn(0).setPreferredWidth(200);
        tbl_Resumen.getColumnModel().getColumn(1).setPreferredWidth(5);
        //Renderer
        tbl_Resumen.setDefaultRenderer(BigDecimal.class, new ColoresNumerosRenderer());
    }

    private void cargarTablaResumen() {
        try {
            this.caja = RestClient.getRestTemplate().getForObject("/cajas/" + this.caja.getIdCaja(), Caja.class);
            Object[] renglonSaldoApertura = new Object[4];
            renglonSaldoApertura[0] = 0L;
            renglonSaldoApertura[1] = "Saldo Apertura";
            renglonSaldoApertura[2] = true;
            renglonSaldoApertura[3] = caja.getSaldoApertura();
            modeloTablaResumen.addRow(renglonSaldoApertura);            
            Map<Long, BigDecimal> totalesPorFormasDePago = RestClient.getRestTemplate()
                    .exchange("/cajas/" + this.caja.getIdCaja() + "/totales-formas-de-pago",
                            HttpMethod.GET, null, new ParameterizedTypeReference<Map<Long, BigDecimal>>() {})
                    .getBody();
            totalesPorFormasDePago.keySet().stream().map(idFormaDePago -> {
                FormaDePago fdp = RestClient.getRestTemplate().getForObject("/formas-de-pago/" + idFormaDePago, FormaDePago.class);
                Object[] fila = new Object[4];
                fila[0] = fdp.getIdFormaDePago();
                fila[1] = fdp.getNombre();
                fila[2] = fdp.isAfectaCaja();
                fila[3] = totalesPorFormasDePago.get(idFormaDePago);
                return fila;
            }).forEachOrdered(modeloTablaResumen::addRow);                                            
            tbl_Resumen.setModel(modeloTablaResumen);
            tbl_Resumen.removeColumn(tbl_Resumen.getColumnModel().getColumn(0));
            totalesPorFormasDePago.keySet()
                    .forEach(idFormaDePago -> {
                        List<MovimientoCaja> movimientosFormaDePago = Arrays.asList(RestClient.getRestTemplate()
                                .getForObject("/cajas/" + caja.getIdCaja() + "/movimientos?idFormaDePago=" + idFormaDePago, MovimientoCaja[].class));
                        movimientos.put(idFormaDePago, movimientosFormaDePago);
                    });
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }
      
    private void cargarTablaMovimientos(long idFormaDePago) {
        this.limpiarTablaMovimientos();
        if (idFormaDePago != 0) {
            idFormaDePagoSeleccionada = idFormaDePago;
            Object[] renglonMovimiento = new Object[3];
            movimientos.get(idFormaDePago).forEach(m -> {
                renglonMovimiento[0] = m.getConcepto();
                renglonMovimiento[1] = m.getFecha();
                renglonMovimiento[2] = m.getMonto();
                modeloTablaBalance.addRow(renglonMovimiento);
            });
            tbl_Movimientos.setModel(modeloTablaBalance);
        }
    }
  
    private void cargarResultados() {   
        ftxt_TotalAfectaCaja.setValue(RestClient.getRestTemplate()
                .getForObject("/cajas/" + caja.getIdCaja() + "/saldo-afecta-caja", BigDecimal.class));
        if (caja.getEstado().equals(EstadoCaja.CERRADA)) {
            ftxt_TotalSistema.setValue(caja.getSaldoSistema());
        } else {
            ftxt_TotalSistema.setValue(RestClient.getRestTemplate()
                .getForObject("/cajas/" + caja.getIdCaja() + "/saldo-sistema", BigDecimal.class));
        }
        if (((BigDecimal) ftxt_TotalAfectaCaja.getValue()).compareTo(BigDecimal.ZERO) > 0) {
            ftxt_TotalAfectaCaja.setBackground(Color.GREEN);
        }
        if (((BigDecimal) ftxt_TotalAfectaCaja.getValue()).compareTo(BigDecimal.ZERO) < 0) {
            ftxt_TotalAfectaCaja.setBackground(Color.PINK);
        }
        if (((BigDecimal) ftxt_TotalSistema.getValue()).compareTo(BigDecimal.ZERO) < 0) {
            ftxt_TotalSistema.setBackground(Color.PINK);
        }
        if (((BigDecimal) ftxt_TotalSistema.getValue()).compareTo(BigDecimal.ZERO) > 0) {
            ftxt_TotalSistema.setBackground(Color.GREEN);
        }
    }

    private void limpiarYCargarTablas() {
        this.limpiarTablaResumen();
        this.cargarTablaResumen();
        this.limpiarTablaMovimientos();
        this.cargarResultados(); 
        this.cambiarMensajeEstadoCaja();
    }

    private void lanzarReporteRecibo(long idRecibo) {
        if (Desktop.isDesktopSupported()) {
            try {
                byte[] reporte = RestClient.getRestTemplate()
                        .getForObject("/recibos/" + idRecibo + "/reporte", 
                                byte[].class);
                File f = new File(System.getProperty("user.home") + "/Factura.pdf");
                Files.write(f.toPath(), reporte);
                Desktop.getDesktop().open(f);
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_IOException"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_plataforma_no_soportada"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_Resumen = new javax.swing.JPanel();
        sp_TablaResumen = new javax.swing.JScrollPane();
        tbl_Resumen = new javax.swing.JTable();
        sp_Tabla = new javax.swing.JScrollPane();
        tbl_Movimientos = new javax.swing.JTable();
        lbl_movimientos = new javax.swing.JLabel();
        btn_VerDetalle = new javax.swing.JButton();
        btn_EliminarGasto = new javax.swing.JButton();
        lbl_estadoEstatico = new javax.swing.JLabel();
        lbl_estadoDinamico = new javax.swing.JLabel();
        btn_CerrarCaja = new javax.swing.JButton();
        btn_AgregarGasto = new javax.swing.JButton();
        lbl_TotalSistema = new javax.swing.JLabel();
        lbl_totalAfectaCaja = new javax.swing.JLabel();
        ftxt_TotalAfectaCaja = new javax.swing.JFormattedTextField();
        ftxt_TotalSistema = new javax.swing.JFormattedTextField();
        btn_Refresh = new javax.swing.JButton();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Caja_16x16.png"))); // NOI18N
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

        pnl_Resumen.setBorder(javax.swing.BorderFactory.createTitledBorder("Resumen General"));

        tbl_Resumen.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Resumen.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbl_Resumen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_ResumenMouseClicked(evt);
            }
        });
        tbl_Resumen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbl_ResumenKeyPressed(evt);
            }
        });
        sp_TablaResumen.setViewportView(tbl_Resumen);

        tbl_Movimientos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sp_Tabla.setViewportView(tbl_Movimientos);

        lbl_movimientos.setText("Movimientos por Forma de Pago (Seleccione una de la lista superior)");

        btn_VerDetalle.setForeground(java.awt.Color.blue);
        btn_VerDetalle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/target_16x16.png"))); // NOI18N
        btn_VerDetalle.setText("Ver Detalle");
        btn_VerDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_VerDetalleActionPerformed(evt);
            }
        });

        btn_EliminarGasto.setForeground(java.awt.Color.blue);
        btn_EliminarGasto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/CoinsDel_16x16.png"))); // NOI18N
        btn_EliminarGasto.setText("Eliminar Gasto");
        btn_EliminarGasto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarGastoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_ResumenLayout = new javax.swing.GroupLayout(pnl_Resumen);
        pnl_Resumen.setLayout(pnl_ResumenLayout);
        pnl_ResumenLayout.setHorizontalGroup(
            pnl_ResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_TablaResumen, javax.swing.GroupLayout.DEFAULT_SIZE, 849, Short.MAX_VALUE)
            .addComponent(sp_Tabla, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(pnl_ResumenLayout.createSequentialGroup()
                .addGroup(pnl_ResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_movimientos)
                    .addGroup(pnl_ResumenLayout.createSequentialGroup()
                        .addComponent(btn_VerDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_EliminarGasto)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pnl_ResumenLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_EliminarGasto, btn_VerDetalle});

        pnl_ResumenLayout.setVerticalGroup(
            pnl_ResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_ResumenLayout.createSequentialGroup()
                .addComponent(sp_TablaResumen, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_movimientos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_Tabla, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_ResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_VerDetalle)
                    .addComponent(btn_EliminarGasto)))
        );

        pnl_ResumenLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_EliminarGasto, btn_VerDetalle});

        lbl_estadoEstatico.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_estadoEstatico.setText("Estado:");

        lbl_estadoDinamico.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        btn_CerrarCaja.setForeground(java.awt.Color.blue);
        btn_CerrarCaja.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/CerrarCaja_16x16.png"))); // NOI18N
        btn_CerrarCaja.setText("Cerrar Caja");
        btn_CerrarCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_CerrarCajaActionPerformed(evt);
            }
        });

        btn_AgregarGasto.setForeground(java.awt.Color.blue);
        btn_AgregarGasto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/CoinsAdd_16x16.png"))); // NOI18N
        btn_AgregarGasto.setText("Nuevo Gasto");
        btn_AgregarGasto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AgregarGastoActionPerformed(evt);
            }
        });

        lbl_TotalSistema.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_TotalSistema.setText("Total Sistema:");

        lbl_totalAfectaCaja.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_totalAfectaCaja.setText("Total afecta Caja:");

        ftxt_TotalAfectaCaja.setEditable(false);
        ftxt_TotalAfectaCaja.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        ftxt_TotalAfectaCaja.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ftxt_TotalAfectaCaja.setText("0");
        ftxt_TotalAfectaCaja.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        ftxt_TotalSistema.setEditable(false);
        ftxt_TotalSistema.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        ftxt_TotalSistema.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ftxt_TotalSistema.setText("0");
        ftxt_TotalSistema.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        btn_Refresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Refresh_16x16.png"))); // NOI18N
        btn_Refresh.setFocusable(false);
        btn_Refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_RefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_AgregarGasto)
                        .addGap(0, 0, 0)
                        .addComponent(btn_CerrarCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lbl_TotalSistema, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ftxt_TotalSistema, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lbl_totalAfectaCaja)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ftxt_TotalAfectaCaja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(pnl_Resumen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbl_estadoEstatico)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_estadoDinamico, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_Refresh)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {ftxt_TotalAfectaCaja, ftxt_TotalSistema});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_AgregarGasto, btn_CerrarCaja});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_estadoDinamico, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_estadoEstatico, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Refresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_Resumen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_totalAfectaCaja, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(ftxt_TotalAfectaCaja, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btn_AgregarGasto)
                    .addComponent(btn_CerrarCaja)
                    .addComponent(lbl_TotalSistema, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ftxt_TotalSistema))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {ftxt_TotalAfectaCaja, ftxt_TotalSistema, lbl_TotalSistema, lbl_totalAfectaCaja});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_CerrarCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_CerrarCajaActionPerformed
        if (caja != null) {
            if (caja.getEstado() == EstadoCaja.ABIERTA) {
                try {
                    String monto = JOptionPane.showInputDialog(this,
                            "Saldo Sistema: " + new DecimalFormat("#.##").format(RestClient.getRestTemplate()
                                    .getForObject("/cajas/" + caja.getIdCaja() + "/saldo-sistema", BigDecimal.class))
                            + "\nSaldo Real:", "Cerrar Caja", JOptionPane.QUESTION_MESSAGE);
                    if (monto != null) {
                        RestClient.getRestTemplate().put("/cajas/" + caja.getIdCaja() + "/cierre?"
                                + "monto=" + new BigDecimal(monto),
                                Caja.class);
                        this.dispose();
                    }
                } catch (NumberFormatException e) {
                    LOGGER.error(e.getMessage());
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_error_formato_numero"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } catch (RestClientResponseException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (ResourceAccessException ex) {
                    LOGGER.error(ex.getMessage());
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_caja_cerrada"),
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_CerrarCajaActionPerformed

    private void btn_VerDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_VerDetalleActionPerformed
        if (tbl_Movimientos.getSelectedRow() != -1) {
            long id = this.movimientos.get(idFormaDePagoSeleccionada).get(Utilidades.getSelectedRowModelIndice(tbl_Movimientos)).getIdMovimiento();
            TipoDeComprobante tipoDeComprobante = this.movimientos.get(idFormaDePagoSeleccionada).get(Utilidades.getSelectedRowModelIndice(tbl_Movimientos)).getTipoComprobante();
            try {
                if (tipoDeComprobante.equals(TipoDeComprobante.RECIBO)) {
                    this.lanzarReporteRecibo(id);
                }
                if (tipoDeComprobante.equals(TipoDeComprobante.GASTO)) {
                    Gasto gasto = RestClient.getRestTemplate().getForObject("/gastos/" + id, Gasto.class);
                    String mensaje = "En Concepto de: " + gasto.getConcepto()
                            + "\nMonto: " + gasto.getMonto() + "\nUsuario: " + gasto.getNombreUsuario();
                    JOptionPane.showMessageDialog(this, mensaje, "Resumen de Gasto", JOptionPane.INFORMATION_MESSAGE);
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
    }//GEN-LAST:event_btn_VerDetalleActionPerformed

    private void btn_AgregarGastoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AgregarGastoActionPerformed
        try {
            if (RestClient.getRestTemplate().getForObject("/cajas/sucursales/"
                    + SucursalActiva.getInstance().getSucursal().getIdSucursal() + "/ultima-caja-abierta", boolean.class)) {
                List<FormaDePago> formasDePago = Arrays.asList(RestClient.getRestTemplate().getForObject("/formas-de-pago", FormaDePago[].class));
                AgregarGastoGUI agregarGasto = new AgregarGastoGUI(formasDePago);
                agregarGasto.setModal(true);
                agregarGasto.setLocationRelativeTo(null);
                agregarGasto.setVisible(true);
                this.limpiarYCargarTablas();
            } else if (this.caja.getEstado().equals(EstadoCaja.CERRADA)) {
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_caja_cerrada"),
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
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
    }//GEN-LAST:event_btn_AgregarGastoActionPerformed

    private void btn_EliminarGastoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarGastoActionPerformed
        if (tbl_Movimientos.getSelectedRow() != -1) {
            long idMovimientoTabla = this.movimientos.get(idFormaDePagoSeleccionada).get(Utilidades.getSelectedRowModelIndice(tbl_Movimientos)).getIdMovimiento();
            TipoDeComprobante tipoDeComprobante = this.movimientos.get(idFormaDePagoSeleccionada).get(Utilidades.getSelectedRowModelIndice(tbl_Movimientos)).getTipoComprobante();
            try {
                if (tipoDeComprobante.equals(TipoDeComprobante.GASTO) && RestClient.getRestTemplate().getForObject("/cajas/sucursales/"
                        + SucursalActiva.getInstance().getSucursal().getIdSucursal() + "/ultima-caja-abierta", boolean.class)) {
                    int confirmacionEliminacion = JOptionPane.showConfirmDialog(this,
                            "¿Esta seguro que desea eliminar el gasto seleccionado?",
                            "Eliminar", JOptionPane.YES_NO_OPTION);
                    if (confirmacionEliminacion == JOptionPane.YES_OPTION) {
                        try {
                            RestClient.getRestTemplate().delete("/gastos/" + idMovimientoTabla);
                            this.limpiarYCargarTablas();
                        } catch (RestClientResponseException ex) {
                            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        } catch (ResourceAccessException ex) {
                            LOGGER.error(ex.getMessage());
                            JOptionPane.showMessageDialog(this,
                                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else if (this.caja.getEstado().equals(EstadoCaja.CERRADA)) {
                    JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_caja_cerrada"),
                            "Aviso", JOptionPane.INFORMATION_MESSAGE);
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
    }//GEN-LAST:event_btn_EliminarGastoActionPerformed

    private void tbl_ResumenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_ResumenMouseClicked
        this.cargarTablaMovimientos((long) tbl_Resumen.getModel().getValueAt(Utilidades.getSelectedRowModelIndice(tbl_Resumen), 0));
    }//GEN-LAST:event_tbl_ResumenMouseClicked

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        this.setSize(sizeInternalFrame);
        this.setTitle("Arqueo de Caja - Apertura: " + FormatosFechaHora.formatoFecha(this.caja.getFechaApertura(), FormatosFechaHora.FORMATO_FECHAHORA_HISPANO));
        this.cambiarMensajeEstadoCaja();
        this.limpiarYCargarTablas();
        try {
            this.setMaximum(true);
        } catch (PropertyVetoException ex) {
            String mensaje = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(mensaje + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_formInternalFrameOpened

    private void cambiarMensajeEstadoCaja() {
        switch (caja.getEstado()) {
            case ABIERTA: {
                lbl_estadoDinamico.setText("Abierta");
                lbl_estadoDinamico.setForeground(Color.GREEN);
                break;
            }
            case CERRADA: {
                lbl_estadoDinamico.setText("Cerrada");
                lbl_estadoDinamico.setForeground(Color.RED);
                break;
            }
        }
    }
    
    private void tbl_ResumenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbl_ResumenKeyPressed
        int row = tbl_Resumen.getSelectedRow();
        if (row != -1) {
            row = Utilidades.getSelectedRowModelIndice(tbl_Resumen);
            if ((evt.getKeyCode() == KeyEvent.VK_UP) && row > 0) {
                row--;
            }
            if ((evt.getKeyCode() == KeyEvent.VK_DOWN) && (row + 1) < tbl_Resumen.getRowCount()) {
                row++;
            }
            try {
                if (row != 0) {
                    this.cargarTablaMovimientos((long) tbl_Resumen.getModel().getValueAt(row, 0));
                } else {
                    this.limpiarTablaMovimientos();
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
    }//GEN-LAST:event_tbl_ResumenKeyPressed

    private void btn_RefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_RefreshActionPerformed
        this.limpiarYCargarTablas();
    }//GEN-LAST:event_btn_RefreshActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_AgregarGasto;
    private javax.swing.JButton btn_CerrarCaja;
    private javax.swing.JButton btn_EliminarGasto;
    private javax.swing.JButton btn_Refresh;
    private javax.swing.JButton btn_VerDetalle;
    private javax.swing.JFormattedTextField ftxt_TotalAfectaCaja;
    private javax.swing.JFormattedTextField ftxt_TotalSistema;
    private javax.swing.JLabel lbl_TotalSistema;
    private javax.swing.JLabel lbl_estadoDinamico;
    private javax.swing.JLabel lbl_estadoEstatico;
    private javax.swing.JLabel lbl_movimientos;
    private javax.swing.JLabel lbl_totalAfectaCaja;
    private javax.swing.JPanel pnl_Resumen;
    private javax.swing.JScrollPane sp_Tabla;
    private javax.swing.JScrollPane sp_TablaResumen;
    javax.swing.JTable tbl_Movimientos;
    private javax.swing.JTable tbl_Resumen;
    // End of variables declaration//GEN-END:variables

}
