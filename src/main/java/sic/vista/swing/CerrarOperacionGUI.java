package sic.vista.swing;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Cliente;
import sic.modelo.CuentaCorrienteCliente;
import sic.modelo.Factura;
import sic.modelo.FacturaVenta;
import sic.modelo.FormaDePago;
import sic.modelo.NuevaFacturaVenta;
import sic.modelo.NuevoRenglonPedido;
import sic.modelo.Pedido;
import sic.modelo.PedidoDTO;
import sic.modelo.RenglonFactura;
import sic.modelo.SucursalActiva;
import sic.modelo.TipoDeComprobante;
import sic.modelo.TipoDeEnvio;
import sic.modelo.UsuarioActivo;

public class CerrarOperacionGUI extends JDialog {

    private boolean exito;
    private boolean facturaAutorizada = false;
    private final NuevaFacturaVenta nuevaFacturaVenta;
    private final BigDecimal totalFactura;
    private PedidoDTO nuevoPedido;
    private final Pedido pedido;
    private final Cliente cliente;
    private final ModeloTabla modeloTabla;
    private final HotKeysHandler keyHandler = new HotKeysHandler();
    private int[] indicesParaDividir = null;
    private final List<Long> idsFormasDePago = new ArrayList<>();
    private final List<BigDecimal> montos = new ArrayList<>();
    private boolean dividir = false;
    private BigDecimal totalPedido;
    private BigDecimal total;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public CerrarOperacionGUI(NuevaFacturaVenta nuevaFacturaVenta, Pedido pedido, BigDecimal totalFactura, ModeloTabla modeloTabla) {
        super.setModal(true);
        this.nuevaFacturaVenta = nuevaFacturaVenta;
        this.totalFactura = totalFactura;
        this.pedido = pedido;
        this.modeloTabla = modeloTabla;
        this.cliente = pedido.getCliente();
        this.initComponents();
        this.setIcon();
        this.setListeners();
    }

    public CerrarOperacionGUI(PedidoDTO nuevoPedido, BigDecimal totalPedido, Cliente cliente) {
        this.nuevoPedido = nuevoPedido;
        this.cliente = cliente;
        this.nuevaFacturaVenta = null;
        this.totalFactura = null;
        this.pedido = null;
        this.modeloTabla = null;
        this.totalPedido = totalPedido;
        this.initComponents();
        this.setIcon();
    }

    public CerrarOperacionGUI(Pedido pedido, Cliente cliente) {
        this.nuevoPedido = null;
        this.cliente = cliente;
        this.pedido = pedido;
        this.nuevaFacturaVenta = null;
        this.totalFactura = null;
        this.modeloTabla = null;
        this.initComponents();
        this.setIcon();
    }

    public boolean isExito() {
        return exito;
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(CerrarOperacionGUI.class.getResource("/sic/icons/SIC_24_square.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void lanzarReportePedido(Pedido pedido) {
        if (Desktop.isDesktopSupported()) {
            try {
                byte[] reporte = RestClient.getRestTemplate()
                        .getForObject("/pedidos/" + pedido.getIdPedido() + "/reporte", byte[].class);
                File f = new File(System.getProperty("user.home") + "/Pedido.pdf");
                Files.write(f.toPath(), reporte);
                Desktop.getDesktop().open(f);
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_IOException"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_plataforma_no_soportada"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setListeners() {
        chk_FormaDePago1.addKeyListener(keyHandler);
        chk_FormaDePago2.addKeyListener(keyHandler);
        chk_FormaDePago3.addKeyListener(keyHandler);
        cmb_FormaDePago1.addKeyListener(keyHandler);
        cmb_FormaDePago2.addKeyListener(keyHandler);
        cmb_FormaDePago3.addKeyListener(keyHandler);
        txt_MontoPago1.addKeyListener(keyHandler);
        txt_MontoPago2.addKeyListener(keyHandler);
        txt_MontoPago3.addKeyListener(keyHandler);
        btnFinalizar.addKeyListener(keyHandler);
    }

    private void lanzarReporteFactura(Factura factura, String nombreReporte) {
        if (Desktop.isDesktopSupported()) {
            try {
                byte[] reporte = RestClient.getRestTemplate()
                        .getForObject("/facturas/ventas/" + factura.getIdFactura() + "/reporte", byte[].class);
                File f = new File(System.getProperty("user.home") + "/" + nombreReporte + ".pdf");
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

    private void cargarFormasDePago() {
        cmb_FormaDePago1.removeAllItems();
        cmb_FormaDePago2.removeAllItems();
        cmb_FormaDePago3.removeAllItems();
        List<FormaDePago> formasDePago = Arrays.asList(RestClient.getRestTemplate()
                .getForObject("/formas-de-pago", FormaDePago[].class));
        formasDePago.stream().map(fdp -> {
            cmb_FormaDePago1.addItem(fdp);
            return fdp;
        }).map(fdp -> {
            cmb_FormaDePago2.addItem(fdp);
            return fdp;
        }).forEach(fdp -> {
            cmb_FormaDePago3.addItem(fdp);
        });
    }

    private void setEstadoFormasDePago() {
        FormaDePago formaDePagoPredeterminada = RestClient.getRestTemplate()
                .getForObject("/formas-de-pago/predeterminada", FormaDePago.class);
        cmb_FormaDePago1.setSelectedItem(formaDePagoPredeterminada);
        cmb_FormaDePago1.setEnabled(false);
        txt_MontoPago1.setEnabled(false);
        cmb_FormaDePago2.setSelectedItem(formaDePagoPredeterminada);
        txt_MontoPago2.setEnabled(false);
        cmb_FormaDePago2.setEnabled(false);
        cmb_FormaDePago3.setSelectedItem(formaDePagoPredeterminada);
        cmb_FormaDePago3.setEnabled(false);
        txt_MontoPago3.setEnabled(false);
    }

    private void finalizarVenta() {
        this.armarMontosConFormasDePago();
        List<TipoDeComprobante> tiposAutorizables
                = Arrays.asList(
                        TipoDeComprobante.FACTURA_A, TipoDeComprobante.FACTURA_B, TipoDeComprobante.FACTURA_C);
        try {
            if (idsFormasDePago.isEmpty() == false) {
                Long[] formasDePago = new Long[idsFormasDePago.size()];
                formasDePago = idsFormasDePago.toArray(formasDePago);
                nuevaFacturaVenta.setIdsFormaDePago(formasDePago);
                BigDecimal[] montosPagos = new BigDecimal[montos.size()];
                montosPagos = montos.toArray(montosPagos);
                nuevaFacturaVenta.setMontos(montosPagos);
            }
            if (this.pedido != null && this.pedido.getIdPedido() != 0) {
                nuevaFacturaVenta.setIdPedido(this.pedido.getIdPedido());
            }
            if (dividir) {
                nuevaFacturaVenta.setIndices(indicesParaDividir);
                List<FacturaVenta> facturasDivididas = Arrays.asList(RestClient.getRestTemplate()
                        .postForObject("/facturas/ventas/pedidos/" + this.pedido.getIdPedido(), nuevaFacturaVenta, FacturaVenta[].class));
                facturasDivididas.forEach(fv -> {
                    fv.setRenglones(Arrays.asList(RestClient.getRestTemplate()
                            .getForObject("/facturas/" + fv.getIdFactura() + "/renglones",
                                    RenglonFactura[].class)));
                });
                exito = true;
                facturasDivididas.stream().filter(facturaVenta -> tiposAutorizables.contains(facturaVenta.getTipoComprobante()))
                        .forEach(facturaVenta -> {
                            if (facturaVenta.getCae() != 0L) {
                                facturaAutorizada = true;
                            }
                        });
                if (facturaAutorizada) {
                    int reply = JOptionPane.showConfirmDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_reporte"),
                            "Aviso", JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        int indice = facturasDivididas.size();
                        for (int i = 0; i < indice; i++) {
                            if (facturasDivididas.size() == 2 && !facturasDivididas.get(i).getRenglones().isEmpty()) {
                                if (i == 0) {
                                    this.lanzarReporteFactura(facturasDivididas.get(i), "ComprobanteX");
                                } else {
                                    this.lanzarReporteFactura(facturasDivididas.get(i), "Factura");
                                }
                            } else if (facturasDivididas.size() == 1 && !facturasDivididas.get(i).getRenglones().isEmpty()) {
                                this.lanzarReporteFactura(facturasDivididas.get(i), "Factura");
                            }
                        }
                    }
                    this.dispose();
                }
            } else {
                FacturaVenta facturaGuardada = Arrays.asList(RestClient.getRestTemplate().postForObject("/facturas/ventas/pedidos/" + this.pedido.getIdPedido(), nuevaFacturaVenta, FacturaVenta[].class)).get(0);
                exito = true;
                if (tiposAutorizables.contains(facturaGuardada.getTipoComprobante()) && facturaGuardada.getCae() != 0L) {
                    facturaAutorizada = true;
                }
                if (facturaAutorizada
                        || facturaGuardada.getTipoComprobante() == TipoDeComprobante.FACTURA_X
                        || facturaGuardada.getTipoComprobante() == TipoDeComprobante.FACTURA_Y
                        || facturaGuardada.getTipoComprobante() == TipoDeComprobante.PRESUPUESTO) {
                    int reply = JOptionPane.showConfirmDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_reporte"),
                            "Aviso", JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        this.lanzarReporteFactura(facturaGuardada, "Factura");
                    }
                    this.dispose();
                }
            }
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            exito = false;
            this.dispose();
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void armarMontosConFormasDePago() {
        montos.clear();
        idsFormasDePago.clear();
        if (chk_FormaDePago1.isSelected() && chk_FormaDePago1.isEnabled()) {
            montos.add(new BigDecimal(txt_MontoPago1.getValue().toString()));
            idsFormasDePago.add(((FormaDePago) cmb_FormaDePago1.getSelectedItem()).getIdFormaDePago());
        }
        if (chk_FormaDePago2.isSelected() && chk_FormaDePago2.isEnabled()) {
            montos.add(new BigDecimal(txt_MontoPago2.getValue().toString()));
            idsFormasDePago.add(((FormaDePago) cmb_FormaDePago2.getSelectedItem()).getIdFormaDePago());
        }
        if (chk_FormaDePago3.isSelected() && chk_FormaDePago3.isEnabled()) {
            montos.add(new BigDecimal(txt_MontoPago3.getValue().toString()));
            idsFormasDePago.add(((FormaDePago) cmb_FormaDePago3.getSelectedItem()).getIdFormaDePago());
        }
    }

    private void agregarPagosAlPedido() {
        this.armarMontosConFormasDePago();
        if (idsFormasDePago.isEmpty() == false) {
            Long[] formasDePago = new Long[idsFormasDePago.size()];
            formasDePago = idsFormasDePago.toArray(formasDePago);
            nuevoPedido.setIdsFormaDePago(formasDePago);
            BigDecimal[] montosPagos = new BigDecimal[montos.size()];
            montosPagos = montos.toArray(montosPagos);
            nuevoPedido.setMontos(montosPagos);
        }
    }

    /**
     * Clase interna para manejar las hotkeys
     */
    class HotKeysHandler extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent evt) {
            if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                dispose();
            }

            if (evt.getKeyCode() == KeyEvent.VK_ENTER && evt.getSource() == btnFinalizar) {
                btnFinalizarActionPerformed(null);
            }
        }
    };

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        btnFinalizar = new javax.swing.JButton();
        panelMedio = new javax.swing.JPanel();
        chk_FormaDePago1 = new javax.swing.JCheckBox();
        cmb_FormaDePago1 = new javax.swing.JComboBox();
        txt_MontoPago1 = new javax.swing.JFormattedTextField();
        chk_FormaDePago2 = new javax.swing.JCheckBox();
        cmb_FormaDePago2 = new javax.swing.JComboBox();
        txt_MontoPago2 = new javax.swing.JFormattedTextField();
        chk_FormaDePago3 = new javax.swing.JCheckBox();
        cmb_FormaDePago3 = new javax.swing.JComboBox();
        txt_MontoPago3 = new javax.swing.JFormattedTextField();
        lblSaldoCC = new javax.swing.JLabel();
        txtSaldoCC = new javax.swing.JLabel();
        panelInferior = new javax.swing.JPanel();
        rbRetiroEnSucursal = new javax.swing.JRadioButton();
        rbDomicilio = new javax.swing.JRadioButton();
        lblDetalleEnvio = new javax.swing.JLabel();
        cmbEnvio = new javax.swing.JComboBox<>();
        lblDividido = new javax.swing.JLabel();
        panelSuperior = new javax.swing.JPanel();
        lbl_Vendor = new javax.swing.JLabel();
        lbl_Vendedor = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cerrar Operación");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        btnFinalizar.setForeground(java.awt.Color.blue);
        btnFinalizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btnFinalizar.setText("Finalizar");
        btnFinalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizarActionPerformed(evt);
            }
        });

        panelMedio.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        chk_FormaDePago1.setText("Forma de Pago #1:");
        chk_FormaDePago1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_FormaDePago1ItemStateChanged(evt);
            }
        });

        txt_MontoPago1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_MontoPago1.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        txt_MontoPago1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_MontoPago1FocusGained(evt);
            }
        });

        chk_FormaDePago2.setText("Forma de Pago #2:");
        chk_FormaDePago2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_FormaDePago2ItemStateChanged(evt);
            }
        });

        txt_MontoPago2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_MontoPago2.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        txt_MontoPago2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_MontoPago2FocusGained(evt);
            }
        });

        chk_FormaDePago3.setText("Forma de Pago #3:");
        chk_FormaDePago3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_FormaDePago3ItemStateChanged(evt);
            }
        });

        txt_MontoPago3.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_MontoPago3.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        txt_MontoPago3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_MontoPago3FocusGained(evt);
            }
        });

        lblSaldoCC.setText("Saldo CC del Cliente $:");

        txtSaldoCC.setForeground(java.awt.Color.green);

        javax.swing.GroupLayout panelMedioLayout = new javax.swing.GroupLayout(panelMedio);
        panelMedio.setLayout(panelMedioLayout);
        panelMedioLayout.setHorizontalGroup(
            panelMedioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMedioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMedioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMedioLayout.createSequentialGroup()
                        .addGroup(panelMedioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chk_FormaDePago1)
                            .addComponent(chk_FormaDePago2)
                            .addComponent(chk_FormaDePago3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelMedioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmb_FormaDePago2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmb_FormaDePago3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmb_FormaDePago1, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelMedioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txt_MontoPago2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_MontoPago1)
                            .addComponent(txt_MontoPago3)))
                    .addGroup(panelMedioLayout.createSequentialGroup()
                        .addComponent(lblSaldoCC, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSaldoCC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelMedioLayout.setVerticalGroup(
            panelMedioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMedioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMedioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtSaldoCC, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSaldoCC))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelMedioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_MontoPago1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmb_FormaDePago1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_FormaDePago1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMedioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_FormaDePago2)
                    .addComponent(cmb_FormaDePago2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_MontoPago2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMedioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_MontoPago3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmb_FormaDePago3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_FormaDePago3))
                .addContainerGap())
        );

        panelMedioLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txt_MontoPago1, txt_MontoPago3});

        panelInferior.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        buttonGroup1.add(rbRetiroEnSucursal);
        rbRetiroEnSucursal.setText("Retiro en sucursal");
        rbRetiroEnSucursal.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbRetiroEnSucursalItemStateChanged(evt);
            }
        });

        buttonGroup1.add(rbDomicilio);
        rbDomicilio.setText("Envio a domicilio:");
        rbDomicilio.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbDomicilioItemStateChanged(evt);
            }
        });

        lblDetalleEnvio.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDetalleEnvio.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        lblDetalleEnvio.setEnabled(false);

        cmbEnvio.setEnabled(false);
        cmbEnvio.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbEnvioItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout panelInferiorLayout = new javax.swing.GroupLayout(panelInferior);
        panelInferior.setLayout(panelInferiorLayout);
        panelInferiorLayout.setHorizontalGroup(
            panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInferiorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInferiorLayout.createSequentialGroup()
                        .addGroup(panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(rbDomicilio)
                            .addComponent(rbRetiroEnSucursal))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbEnvio, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(lblDetalleEnvio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        panelInferiorLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {rbDomicilio, rbRetiroEnSucursal});

        panelInferiorLayout.setVerticalGroup(
            panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInferiorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbRetiroEnSucursal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbDomicilio)
                    .addComponent(cmbEnvio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDetalleEnvio, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblDividido.setForeground(new java.awt.Color(29, 156, 37));
        lblDividido.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDividido.setText("ATENCION: El comprobante será dividido!");

        panelSuperior.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_Vendor.setText("Vendedor:");

        lbl_Vendedor.setForeground(new java.awt.Color(29, 156, 37));

        javax.swing.GroupLayout panelSuperiorLayout = new javax.swing.GroupLayout(panelSuperior);
        panelSuperior.setLayout(panelSuperiorLayout);
        panelSuperiorLayout.setHorizontalGroup(
            panelSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSuperiorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_Vendor, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_Vendedor, javax.swing.GroupLayout.PREFERRED_SIZE, 602, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );
        panelSuperiorLayout.setVerticalGroup(
            panelSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSuperiorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Vendedor)
                    .addComponent(lbl_Vendor))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelSuperiorLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lbl_Vendedor, lbl_Vendor});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblDividido)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFinalizar))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(panelSuperior, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelMedio, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelInferior, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelSuperior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelMedio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelInferior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFinalizar)
                    .addComponent(lblDividido, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        BigDecimal saldoCC = BigDecimal.ZERO;
        try {
            DecimalFormat dFormat = new DecimalFormat("##,##0.##");
            saldoCC = RestClient.getRestTemplate()
                    .getForObject("/cuentas-corriente/clientes/"
                            + this.cliente.getIdCliente(), CuentaCorrienteCliente.class).getSaldo().setScale(2, RoundingMode.HALF_UP);
            txtSaldoCC.setText(dFormat.format(saldoCC));
            if (saldoCC.compareTo(BigDecimal.ZERO) < 0) {
                txtSaldoCC.setForeground(Color.RED);
            } else if (saldoCC.compareTo(BigDecimal.ZERO) >= 0) {
                txtSaldoCC.setForeground(new Color(29,156,37));
            }
            if ((this.nuevoPedido != null || this.pedido != null) && this.nuevaFacturaVenta == null) {
                lblDividido.setVisible(false);           
                rbRetiroEnSucursal.setEnabled(SucursalActiva.getInstance().getSucursal().getConfiguracionSucursal().isPuntoDeRetiro());
                rbRetiroEnSucursal.setSelected(SucursalActiva.getInstance().getSucursal().getConfiguracionSucursal().isPuntoDeRetiro());  
                rbDomicilio.setSelected(!SucursalActiva.getInstance().getSucursal().getConfiguracionSucursal().isPuntoDeRetiro());  
            } else {
                panelInferior.setEnabled(false);
                rbRetiroEnSucursal.setEnabled(false);
                rbDomicilio.setEnabled(false);
            }
            this.cargarFormasDePago();
            this.setEstadoFormasDePago();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        lbl_Vendedor.setText(UsuarioActivo.getInstance().getUsuario().toString());
        if (pedido != null) {
            total = this.pedido.getTotal();
        } else {
            total = totalFactura != null ? totalFactura : totalPedido;
        }
        if (saldoCC.compareTo(BigDecimal.ZERO) > 0) {
            total = saldoCC.subtract(total);
            txt_MontoPago1.setValue(total.compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.ZERO : total.abs());
        } else {
            total = total.add(saldoCC.abs());
            txt_MontoPago1.setValue(total);
        }
        txt_MontoPago2.setValue(0);
        txt_MontoPago3.setValue(0);
        if (modeloTabla != null) {
            indicesParaDividir = new int[this.modeloTabla.getRowCount()];
            if ((indicesParaDividir.length > 0)
                    && (nuevaFacturaVenta.getTipoDeComprobante() == TipoDeComprobante.FACTURA_A
                    || nuevaFacturaVenta.getTipoDeComprobante() == TipoDeComprobante.FACTURA_B
                    || nuevaFacturaVenta.getTipoDeComprobante() == TipoDeComprobante.FACTURA_C)) {
                int j = 0;
                boolean tieneRenglonesMarcados = false;
                for (int i = 0; i < this.modeloTabla.getRowCount(); i++) {
                    if ((boolean) this.modeloTabla.getValueAt(i, 0)) {
                        indicesParaDividir[j] = i;
                        j++;
                        tieneRenglonesMarcados = true;
                    }
                }
                if (indicesParaDividir.length != 0 && tieneRenglonesMarcados) {
                    dividir = true;
                }
            }
        }
        if (dividir) {
            chk_FormaDePago1.setEnabled(false);
            chk_FormaDePago2.setEnabled(false);
            chk_FormaDePago3.setEnabled(false);
            cmb_FormaDePago1.setEnabled(false);
            cmb_FormaDePago2.setEnabled(false);
            cmb_FormaDePago3.setEnabled(false);
            txt_MontoPago1.setEnabled(false);
            txt_MontoPago2.setEnabled(false);
            txt_MontoPago3.setEnabled(false);
        } else {
            lblDividido.setText("");
        }
    }//GEN-LAST:event_formWindowOpened

    private void btnFinalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizarActionPerformed
        try {
            TipoDeEnvio tipoDeEnvio = null;
            if (this.nuevoPedido != null || this.pedido != null) {
                if (rbRetiroEnSucursal.isSelected()) {
                    tipoDeEnvio = TipoDeEnvio.RETIRO_EN_SUCURSAL;
                }
                if (rbDomicilio.isSelected()) {
                    switch (cmbEnvio.getSelectedIndex()) {
                        case 0:
                            tipoDeEnvio = TipoDeEnvio.USAR_UBICACION_FACTURACION;
                            break;
                        case 1:
                            tipoDeEnvio = TipoDeEnvio.USAR_UBICACION_ENVIO;
                            break;
                        default:
                            tipoDeEnvio = TipoDeEnvio.RETIRO_EN_SUCURSAL;
                            break;
                    }
                }
            }
            if (dividir || pedido != null) {
                if (nuevaFacturaVenta != null) {
                    this.finalizarVenta();
                } else {
                    this.cerrarPedido(tipoDeEnvio);
                }
            } else {
                BigDecimal totalPagos = BigDecimal.ZERO;
                if (chk_FormaDePago1.isSelected() && chk_FormaDePago1.isEnabled()) {
                    totalPagos = totalPagos.add(new BigDecimal(txt_MontoPago1.getValue().toString()));
                }
                if (chk_FormaDePago2.isSelected() && chk_FormaDePago2.isEnabled()) {
                    totalPagos = totalPagos.add(new BigDecimal(txt_MontoPago2.getValue().toString()));
                }
                if (chk_FormaDePago3.isSelected() && chk_FormaDePago3.isEnabled()) {
                    totalPagos = totalPagos.add(new BigDecimal(txt_MontoPago3.getValue().toString()));
                }
                if (pedido != null) {
                    total = totalFactura != null ? totalFactura.setScale(2, RoundingMode.FLOOR) : totalPedido.setScale(2, RoundingMode.FLOOR);
                }
                BigDecimal saldoCC = RestClient.getRestTemplate()
                        .getForObject("/cuentas-corriente/clientes/"
                                + this.cliente.getIdCliente(), CuentaCorrienteCliente.class).getSaldo().setScale(2, RoundingMode.DOWN);
                if (totalPagos.add(saldoCC).compareTo(total.setScale(2, RoundingMode.DOWN)) < 0) {
                    int reply = JOptionPane.showConfirmDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_montos_insuficientes"),
                            "Aviso", JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        if (this.nuevaFacturaVenta != null) {
                            this.finalizarVenta();
                        } else if (tipoDeEnvio != null) {
                            this.cerrarPedido(tipoDeEnvio);
                        }
                    }
                } else {
                    if (this.nuevaFacturaVenta != null) {
                        this.finalizarVenta();
                    } else if (tipoDeEnvio != null) {
                        this.cerrarPedido(tipoDeEnvio);
                    }
                }
            }
        } catch (RestClientResponseException ex) {
            if (nuevoPedido != null) {
                nuevoPedido.setMontos(null);
                nuevoPedido.setIdsFormaDePago(null);
            }
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnFinalizarActionPerformed

    private void cerrarPedido(TipoDeEnvio tipoDeEnvio) {
        if (pedido == null) { 
            nuevoPedido.setIdCliente(cliente.getIdCliente());
            nuevoPedido.setTipoDeEnvio(tipoDeEnvio);
            nuevoPedido.setIdSucursal(SucursalActiva.getInstance().getSucursal().getIdSucursal());
            this.agregarPagosAlPedido();
            Pedido p = RestClient.getRestTemplate().postForObject("/pedidos", nuevoPedido, Pedido.class);
            this.exito = true;
            int reply = JOptionPane.showConfirmDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_reporte"),
                    "Aviso", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                this.lanzarReportePedido(p);
            }
        } else {
            nuevoPedido = new PedidoDTO();
            nuevoPedido.setIdPedido(pedido.getIdPedido());
            nuevoPedido.setObservaciones(pedido.getObservaciones());
            nuevoPedido.setRecargoPorcentaje(pedido.getRecargoPorcentaje());
            nuevoPedido.setDescuentoPorcentaje(pedido.getDescuentoPorcentaje());
            nuevoPedido.setIdSucursal(SucursalActiva.getInstance().getSucursal().getIdSucursal());
            List<NuevoRenglonPedido> nuevosRenglonesPedido = new ArrayList();
            pedido.getRenglones().forEach(r -> nuevosRenglonesPedido.add(
                    new NuevoRenglonPedido(r.getIdProductoItem(), r.getCantidad())));
            nuevoPedido.setRenglones(nuevosRenglonesPedido);
            nuevoPedido.setTipoDeEnvio(tipoDeEnvio);
            this.agregarPagosAlPedido();
            RestClient.getRestTemplate().put("/pedidos", nuevoPedido);
            this.exito = true;
            JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_pedido_actualizado"),
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
        }
        this.dispose();
    }

    private void txt_MontoPago1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_MontoPago1FocusGained
        SwingUtilities.invokeLater(() -> {
            txt_MontoPago1.selectAll();
        });
    }//GEN-LAST:event_txt_MontoPago1FocusGained

    private void txt_MontoPago3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_MontoPago3FocusGained
        SwingUtilities.invokeLater(() -> {
            txt_MontoPago3.selectAll();
        });
    }//GEN-LAST:event_txt_MontoPago3FocusGained

    private void chk_FormaDePago1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_FormaDePago1ItemStateChanged
        cmb_FormaDePago1.setEnabled(chk_FormaDePago1.isSelected());
        txt_MontoPago1.setEnabled(chk_FormaDePago1.isSelected());
    }//GEN-LAST:event_chk_FormaDePago1ItemStateChanged

    private void chk_FormaDePago2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_FormaDePago2ItemStateChanged
        cmb_FormaDePago2.setEnabled(chk_FormaDePago2.isSelected());
        txt_MontoPago2.setEnabled(chk_FormaDePago2.isSelected());
    }//GEN-LAST:event_chk_FormaDePago2ItemStateChanged

    private void chk_FormaDePago3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_FormaDePago3ItemStateChanged
        cmb_FormaDePago3.setEnabled(chk_FormaDePago3.isSelected());
        txt_MontoPago3.setEnabled(chk_FormaDePago3.isSelected());
    }//GEN-LAST:event_chk_FormaDePago3ItemStateChanged

    private void txt_MontoPago2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_MontoPago2FocusGained
        SwingUtilities.invokeLater(() -> {
            txt_MontoPago2.selectAll();
        });
    }//GEN-LAST:event_txt_MontoPago2FocusGained

    private void rbRetiroEnSucursalItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbRetiroEnSucursalItemStateChanged
        cmbEnvio.setEnabled(!rbRetiroEnSucursal.isSelected());
        cmbEnvio.removeAllItems();
        lblDetalleEnvio.setEnabled(!rbRetiroEnSucursal.isSelected());
    }//GEN-LAST:event_rbRetiroEnSucursalItemStateChanged

    private void rbDomicilioItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbDomicilioItemStateChanged
        cmbEnvio.setEnabled(true);
        if (this.cliente.getUbicacionFacturacion() != null) {
            cmbEnvio.addItem("Usar ubicación de facturación");
        }
        if (this.cliente.getUbicacionEnvio() != null) {
            cmbEnvio.addItem("Usar ubicación de envío");
        }
        if (cmbEnvio.getItemCount() > 0) {
            lblDetalleEnvio.setEnabled(true);
        }
    }//GEN-LAST:event_rbDomicilioItemStateChanged

    private void cmbEnvioItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbEnvioItemStateChanged
        switch (cmbEnvio.getSelectedIndex()) {
            case 0:
                lblDetalleEnvio.setText(this.cliente.getUbicacionFacturacion().toString());
                break;
            case 1:
                lblDetalleEnvio.setText(this.cliente.getUbicacionEnvio().toString());
                break;
            default:
                lblDetalleEnvio.setText("");
                break;
        }
    }//GEN-LAST:event_cmbEnvioItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFinalizar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox chk_FormaDePago1;
    private javax.swing.JCheckBox chk_FormaDePago2;
    private javax.swing.JCheckBox chk_FormaDePago3;
    private javax.swing.JComboBox<String> cmbEnvio;
    private javax.swing.JComboBox cmb_FormaDePago1;
    private javax.swing.JComboBox cmb_FormaDePago2;
    private javax.swing.JComboBox cmb_FormaDePago3;
    private javax.swing.JLabel lblDetalleEnvio;
    private javax.swing.JLabel lblDividido;
    private javax.swing.JLabel lblSaldoCC;
    private javax.swing.JLabel lbl_Vendedor;
    private javax.swing.JLabel lbl_Vendor;
    private javax.swing.JPanel panelInferior;
    private javax.swing.JPanel panelMedio;
    private javax.swing.JPanel panelSuperior;
    private javax.swing.JRadioButton rbDomicilio;
    private javax.swing.JRadioButton rbRetiroEnSucursal;
    private javax.swing.JLabel txtSaldoCC;
    private javax.swing.JFormattedTextField txt_MontoPago1;
    private javax.swing.JFormattedTextField txt_MontoPago2;
    private javax.swing.JFormattedTextField txt_MontoPago3;
    // End of variables declaration//GEN-END:variables
}
