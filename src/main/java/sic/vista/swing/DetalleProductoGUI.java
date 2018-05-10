package sic.vista.swing;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import sic.modelo.EmpresaActiva;
import sic.modelo.Medida;
import sic.modelo.Producto;
import sic.modelo.Proveedor;
import sic.modelo.Rubro;
import sic.modelo.TipoDeOperacion;
import sic.util.FormatosFechaHora;
import sic.util.FormatterFechaHora;

public class DetalleProductoGUI extends JDialog {

    private Producto productoParaModificar;
    private final TipoDeOperacion operacion;
    private BigDecimal precioDeCosto = BigDecimal.ZERO;
    private BigDecimal gananciaPorcentaje = BigDecimal.ZERO;
    private BigDecimal gananciaNeto = BigDecimal.ZERO;
    private BigDecimal pvp = BigDecimal.ZERO;
    private BigDecimal IVANeto = BigDecimal.ZERO;
    private BigDecimal precioDeLista = BigDecimal.ZERO;    
    private List<Medida> medidas;
    private List<Rubro> rubros;
    private List<Proveedor> proveedores;    
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public DetalleProductoGUI() {
        this.initComponents();
        this.setIcon();        
        this.setListeners();
        operacion = TipoDeOperacion.ALTA;
        lbl_FUM.setVisible(false);
        lbl_FechaUltimaModificacion.setVisible(false);
        lbl_FA.setVisible(false);
        lbl_FechaAlta.setVisible(false);
    }       

    public DetalleProductoGUI(Producto producto) {
        this.initComponents();
        this.setIcon();        
        this.setListeners();
        operacion = TipoDeOperacion.ACTUALIZACION;
        productoParaModificar = producto;        
    }

    private void setListeners() {
        txtPrecioCosto.addPropertyChangeListener("value", new FormattedTextFieldListener());
        txtPVP.addPropertyChangeListener("value", new FormattedTextFieldListener());
        txtGananciaPorcentaje.addPropertyChangeListener("value", new FormattedTextFieldListener());
        txtPrecioLista.addPropertyChangeListener("value", new FormattedTextFieldListener());
    }
    
    // Clase interna para manejar el cambio de value de los JFormattedTextFields
    class FormattedTextFieldListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            Object source = evt.getSource();
            if (source == txtPrecioCosto) {
                txtPrecioCostoActionPerformed(null);
            } else if (source == txtPVP) {
                txtPVPActionPerformed(null);
            } else if (source == txtGananciaPorcentaje) {
                txtGananciaPorcentajeActionPerformed(null);
            } else if (source == txtPrecioLista) {
                txtPrecioListaActionPerformed(null);
            }
        }    
    }
    
    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(DetalleProductoGUI.class.getResource("/sic/icons/Product_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_Guardar = new javax.swing.JButton();
        tp_Tabs = new javax.swing.JTabbedPane();
        panelGeneral = new javax.swing.JPanel();
        panel1 = new javax.swing.JPanel();
        lbl_Codigo = new javax.swing.JLabel();
        lbl_Descripcion = new javax.swing.JLabel();
        txt_Descripcion = new javax.swing.JTextField();
        txt_Codigo = new javax.swing.JTextField();
        lbl_Rubro = new javax.swing.JLabel();
        cmb_Rubro = new javax.swing.JComboBox();
        btn_Rubros = new javax.swing.JButton();
        lbl_Proveedor = new javax.swing.JLabel();
        cmb_Proveedor = new javax.swing.JComboBox();
        btn_NuevoProveedor = new javax.swing.JButton();
        btn_Medidas = new javax.swing.JButton();
        cmb_Medida = new javax.swing.JComboBox();
        lbl_Medida = new javax.swing.JLabel();
        panel2 = new javax.swing.JPanel();
        lbl_PrecioCosto = new javax.swing.JLabel();
        lbl_Ganancia = new javax.swing.JLabel();
        lbl_PrecioLista = new javax.swing.JLabel();
        txtPrecioCosto = new javax.swing.JFormattedTextField();
        txtGananciaPorcentaje = new javax.swing.JFormattedTextField();
        txtPrecioLista = new javax.swing.JFormattedTextField();
        lbl_IVA = new javax.swing.JLabel();
        txtIVANeto = new javax.swing.JFormattedTextField();
        txtGananciaNeto = new javax.swing.JFormattedTextField();
        lbl_PVP = new javax.swing.JLabel();
        txtPVP = new javax.swing.JFormattedTextField();
        cmbIVAPorcentaje = new javax.swing.JComboBox();
        panel3 = new javax.swing.JPanel();
        chk_Ilimitado = new javax.swing.JCheckBox();
        lbl_Cantidad = new javax.swing.JLabel();
        txt_Cantidad = new javax.swing.JFormattedTextField();
        txt_CantMinima = new javax.swing.JFormattedTextField();
        lbl_CantMinima = new javax.swing.JLabel();
        lbl_VentaMinima = new javax.swing.JLabel();
        txt_VentaMinima = new javax.swing.JFormattedTextField();
        panelPropiedades = new javax.swing.JPanel();
        panel5 = new javax.swing.JPanel();
        lbl_Ven = new javax.swing.JLabel();
        lbl_Estanteria = new javax.swing.JLabel();
        txt_Estanteria = new javax.swing.JTextField();
        lbl_Estante = new javax.swing.JLabel();
        txt_Estante = new javax.swing.JTextField();
        dc_Vencimiento = new com.toedter.calendar.JDateChooser();
        lbl_Nota = new javax.swing.JLabel();
        lbl_FA = new javax.swing.JLabel();
        lbl_FUM = new javax.swing.JLabel();
        lbl_FechaUltimaModificacion = new javax.swing.JLabel();
        lbl_FechaAlta = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt_Nota = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        btn_Guardar.setForeground(java.awt.Color.blue);
        btn_Guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btn_Guardar.setText("Guardar");
        btn_Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_GuardarActionPerformed(evt);
            }
        });

        panel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_Codigo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Codigo.setText("Código:");

        lbl_Descripcion.setForeground(java.awt.Color.red);
        lbl_Descripcion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Descripcion.setText("* Descripción:");

        lbl_Rubro.setForeground(java.awt.Color.red);
        lbl_Rubro.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Rubro.setText("* Rubro:");

        btn_Rubros.setForeground(java.awt.Color.blue);
        btn_Rubros.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddBlock.png"))); // NOI18N
        btn_Rubros.setText("Nuevo");
        btn_Rubros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_RubrosActionPerformed(evt);
            }
        });

        lbl_Proveedor.setForeground(java.awt.Color.red);
        lbl_Proveedor.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Proveedor.setText("* Proveedor:");

        btn_NuevoProveedor.setForeground(java.awt.Color.blue);
        btn_NuevoProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddProviderBag_16x16.png"))); // NOI18N
        btn_NuevoProveedor.setText("Nuevo");
        btn_NuevoProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevoProveedorActionPerformed(evt);
            }
        });

        btn_Medidas.setForeground(java.awt.Color.blue);
        btn_Medidas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddRuler_16x16.png"))); // NOI18N
        btn_Medidas.setText("Nueva");
        btn_Medidas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_MedidasActionPerformed(evt);
            }
        });

        lbl_Medida.setForeground(java.awt.Color.red);
        lbl_Medida.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Medida.setText("* Unidad de Medida:");

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_Codigo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Medida, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Proveedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Rubro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Descripcion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmb_Rubro, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmb_Proveedor, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmb_Medida, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt_Descripcion)
                    .addComponent(txt_Codigo))
                .addGap(0, 0, 0)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_NuevoProveedor)
                    .addComponent(btn_Medidas)
                    .addComponent(btn_Rubros))
                .addContainerGap())
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Codigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Codigo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Descripcion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmb_Rubro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Rubros, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Rubro))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmb_Proveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_NuevoProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Proveedor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_Medidas, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmb_Medida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Medida))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Medidas, btn_NuevoProveedor, btn_Rubros, cmb_Medida, cmb_Proveedor, cmb_Rubro});

        panel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_PrecioCosto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_PrecioCosto.setText("Precio de Costo:");

        lbl_Ganancia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Ganancia.setText("Ganancia (%):");

        lbl_PrecioLista.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_PrecioLista.setText("Precio de Lista:");

        txtPrecioCosto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txtPrecioCosto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPrecioCosto.setText("0");
        txtPrecioCosto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPrecioCostoFocusGained(evt);
            }
        });
        txtPrecioCosto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPrecioCostoActionPerformed(evt);
            }
        });

        txtGananciaPorcentaje.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txtGananciaPorcentaje.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtGananciaPorcentaje.setText("0");
        txtGananciaPorcentaje.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtGananciaPorcentajeFocusGained(evt);
            }
        });
        txtGananciaPorcentaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGananciaPorcentajeActionPerformed(evt);
            }
        });

        txtPrecioLista.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txtPrecioLista.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPrecioLista.setText("0");
        txtPrecioLista.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPrecioListaFocusGained(evt);
            }
        });
        txtPrecioLista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPrecioListaActionPerformed(evt);
            }
        });

        lbl_IVA.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_IVA.setText("I.V.A. (%):");

        txtIVANeto.setEditable(false);
        txtIVANeto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txtIVANeto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIVANeto.setText("0");
        txtIVANeto.setFocusable(false);

        txtGananciaNeto.setEditable(false);
        txtGananciaNeto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txtGananciaNeto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtGananciaNeto.setText("0");
        txtGananciaNeto.setFocusable(false);

        lbl_PVP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_PVP.setText("Precio Venta Público:");

        txtPVP.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txtPVP.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPVP.setText("0");
        txtPVP.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPVPFocusGained(evt);
            }
        });
        txtPVP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPVPActionPerformed(evt);
            }
        });

        cmbIVAPorcentaje.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "10.5", "21" }));
        cmbIVAPorcentaje.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbIVAPorcentajeItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_PVP, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                    .addComponent(lbl_IVA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Ganancia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_PrecioCosto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_PrecioLista, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmbIVAPorcentaje, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtGananciaPorcentaje))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtPrecioLista, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                    .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtIVANeto, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(txtGananciaNeto)
                        .addComponent(txtPrecioCosto)
                        .addComponent(txtPVP, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)))
                .addContainerGap())
        );

        panel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtGananciaNeto, txtIVANeto, txtPVP, txtPrecioCosto, txtPrecioLista});

        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_PrecioCosto)
                    .addComponent(txtPrecioCosto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Ganancia)
                    .addComponent(txtGananciaPorcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtGananciaNeto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_PVP)
                    .addComponent(txtPVP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtIVANeto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbIVAPorcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_PrecioLista)
                    .addComponent(txtPrecioLista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.CENTER, panel2Layout.createSequentialGroup()
                .addGap(86, 86, 86)
                .addComponent(lbl_IVA)
                .addGap(36, 36, 36))
        );

        panel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtGananciaNeto, txtGananciaPorcentaje, txtIVANeto, txtPVP, txtPrecioCosto, txtPrecioLista});

        panel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        chk_Ilimitado.setText("Sin Límite");
        chk_Ilimitado.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        chk_Ilimitado.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_IlimitadoItemStateChanged(evt);
            }
        });

        lbl_Cantidad.setForeground(java.awt.Color.red);
        lbl_Cantidad.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Cantidad.setText("* Cantidad:");

        txt_Cantidad.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_Cantidad.setText("0");
        txt_Cantidad.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_CantidadFocusGained(evt);
            }
        });

        txt_CantMinima.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_CantMinima.setText("0");
        txt_CantMinima.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_CantMinimaFocusGained(evt);
            }
        });

        lbl_CantMinima.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_CantMinima.setText("Cantidad Mínima:");

        lbl_VentaMinima.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_VentaMinima.setText("Venta Mínima:");

        txt_VentaMinima.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_VentaMinima.setText("0");
        txt_VentaMinima.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_VentaMinimaFocusGained(evt);
            }
        });

        javax.swing.GroupLayout panel3Layout = new javax.swing.GroupLayout(panel3);
        panel3.setLayout(panel3Layout);
        panel3Layout.setHorizontalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel3Layout.createSequentialGroup()
                        .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(chk_Ilimitado)
                            .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(lbl_CantMinima, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lbl_Cantidad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txt_Cantidad, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                            .addComponent(txt_CantMinima)))
                    .addGroup(panel3Layout.createSequentialGroup()
                        .addComponent(lbl_VentaMinima, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_VentaMinima, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lbl_CantMinima, lbl_Cantidad, lbl_VentaMinima});

        panel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txt_CantMinima, txt_Cantidad, txt_VentaMinima});

        panel3Layout.setVerticalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chk_Ilimitado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Cantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Cantidad))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_CantMinima, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_CantMinima))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_VentaMinima)
                    .addComponent(txt_VentaMinima, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelGeneralLayout = new javax.swing.GroupLayout(panelGeneral);
        panelGeneral.setLayout(panelGeneralLayout);
        panelGeneralLayout.setHorizontalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelGeneralLayout.createSequentialGroup()
                        .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 30, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelGeneralLayout.setVerticalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        tp_Tabs.addTab("General", panelGeneral);

        panel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_Ven.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Ven.setText("Vencimiento:");

        lbl_Estanteria.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Estanteria.setText("Estantería:");

        lbl_Estante.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Estante.setText("Estante:");

        dc_Vencimiento.setDateFormatString("dd/MM/yyyy");

        lbl_Nota.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Nota.setText("Nota ó Comentario:");

        lbl_FA.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_FA.setText("Fecha de Alta:");

        lbl_FUM.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_FUM.setText("Última Modificación:");

        lbl_FechaUltimaModificacion.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_FechaAlta.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txt_Nota.setColumns(20);
        txt_Nota.setLineWrap(true);
        txt_Nota.setRows(5);
        jScrollPane1.setViewportView(txt_Nota);

        javax.swing.GroupLayout panel5Layout = new javax.swing.GroupLayout(panel5);
        panel5.setLayout(panel5Layout);
        panel5Layout.setHorizontalGroup(
            panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel5Layout.createSequentialGroup()
                        .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panel5Layout.createSequentialGroup()
                                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(lbl_Estante, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lbl_Nota, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(16, 16, 16))
                            .addGroup(panel5Layout.createSequentialGroup()
                                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lbl_Ven)
                                    .addComponent(lbl_Estanteria))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dc_Vencimiento, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_Estanteria, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_Estante, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panel5Layout.createSequentialGroup()
                        .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lbl_FA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_FUM))
                        .addGap(12, 12, 12)
                        .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lbl_FechaAlta, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
                            .addComponent(lbl_FechaUltimaModificacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        panel5Layout.setVerticalGroup(
            panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Ven)
                    .addComponent(dc_Vencimiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Estanteria)
                    .addComponent(txt_Estanteria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Estante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Estante))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_Nota)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_FUM)
                    .addComponent(lbl_FechaUltimaModificacion, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_FA)
                    .addComponent(lbl_FechaAlta, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(67, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelPropiedadesLayout = new javax.swing.GroupLayout(panelPropiedades);
        panelPropiedades.setLayout(panelPropiedadesLayout);
        panelPropiedadesLayout.setHorizontalGroup(
            panelPropiedadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPropiedadesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(132, Short.MAX_VALUE))
        );
        panelPropiedadesLayout.setVerticalGroup(
            panelPropiedadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPropiedadesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tp_Tabs.addTab("Propiedades", panelPropiedades);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tp_Tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 592, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_Guardar)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tp_Tabs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_Guardar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cargarProductoParaModificar() {
        txt_Codigo.setText(productoParaModificar.getCodigo());
        txt_Descripcion.setText(productoParaModificar.getDescripcion());
        txt_Nota.setText(productoParaModificar.getNota());
        cmb_Medida.setSelectedItem(productoParaModificar.getNombreMedida());
        chk_Ilimitado.setSelected(productoParaModificar.isIlimitado());
        txt_Cantidad.setValue(productoParaModificar.getCantidad());
        txt_CantMinima.setValue(productoParaModificar.getCantMinima());
        txt_VentaMinima.setValue(productoParaModificar.getVentaMinima());
        cmb_Rubro.setSelectedItem(productoParaModificar.getNombreRubro());
        cmb_Proveedor.setSelectedItem(productoParaModificar.getRazonSocialProveedor());
        FormatterFechaHora formateador = new FormatterFechaHora(FormatosFechaHora.FORMATO_FECHAHORA_HISPANO);
        lbl_FechaUltimaModificacion.setText(formateador.format(productoParaModificar.getFechaUltimaModificacion()));
        lbl_FechaAlta.setText(formateador.format(productoParaModificar.getFechaAlta()));
        dc_Vencimiento.setDate(productoParaModificar.getFechaVencimiento());
        txt_Estanteria.setText(productoParaModificar.getEstanteria());
        txt_Estante.setText(productoParaModificar.getEstante());
        precioDeCosto = productoParaModificar.getPrecioCosto();
        txtPrecioCosto.setValue(precioDeCosto);
        gananciaPorcentaje = productoParaModificar.getGanancia_porcentaje();
        txtGananciaPorcentaje.setValue(gananciaPorcentaje);
        gananciaNeto = productoParaModificar.getGanancia_neto();
        txtGananciaNeto.setValue(gananciaNeto);
        pvp = productoParaModificar.getPrecioVentaPublico();
        txtPVP.setValue(pvp);
        cmbIVAPorcentaje.setSelectedItem(productoParaModificar.getIva_porcentaje().stripTrailingZeros());
        IVANeto = productoParaModificar.getIva_neto();
        txtIVANeto.setValue(IVANeto);
        precioDeLista = productoParaModificar.getPrecioLista();
        txtPrecioLista.setValue(precioDeLista);
    }

    private void prepararComponentes() {
        txt_Cantidad.setValue(BigDecimal.ZERO);
        txt_CantMinima.setValue(BigDecimal.ZERO);
        txt_VentaMinima.setValue(BigDecimal.ONE);
        txtPrecioCosto.setValue(BigDecimal.ZERO);
        txtPVP.setValue(BigDecimal.ZERO);
        txtIVANeto.setValue(BigDecimal.ZERO);
        txtGananciaPorcentaje.setValue(BigDecimal.ZERO);
        txtGananciaNeto.setValue(BigDecimal.ZERO);
        txtPrecioLista.setValue(BigDecimal.ZERO);
    }

    private void cargarMedidas() {
        cmb_Medida.removeAllItems();
        try {
            medidas = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                .getForObject("/medidas/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                Medida[].class)));
            medidas.stream().forEach(m -> cmb_Medida.addItem(m.getNombre()));
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarRubros() {
        cmb_Rubro.removeAllItems();
        try {
            rubros = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                .getForObject("/rubros/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                Rubro[].class)));
            rubros.stream().forEach(r -> cmb_Rubro.addItem(r.getNombre()));
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarProveedores() {
        cmb_Proveedor.removeAllItems();
        try {
            proveedores = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                .getForObject("/proveedores/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                Proveedor[].class)));
            proveedores.stream().forEach(p -> cmb_Proveedor.addItem(p.getRazonSocial()));
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarYRecargarComponentes() {
        txt_Codigo.setText("");
        txt_Descripcion.setText("");
        txt_Cantidad.setValue(BigDecimal.ZERO);
        txt_CantMinima.setValue(BigDecimal.ZERO);
        txt_VentaMinima.setValue(BigDecimal.ZERO);
        chk_Ilimitado.setSelected(false);
        txtPrecioCosto.setValue(BigDecimal.ZERO);
        txtPVP.setValue(BigDecimal.ZERO);
        txtIVANeto.setValue(BigDecimal.ZERO);
        txtGananciaPorcentaje.setValue(BigDecimal.ZERO);
        txtGananciaNeto.setValue(BigDecimal.ZERO);
        txtPrecioLista.setValue(BigDecimal.ZERO);
        txt_Estanteria.setText("");
        txt_Estante.setText("");
        txt_Nota.setText("");
        dc_Vencimiento.setDate(null);
    }
    
    private void calcularGananciaPorcentaje() {
        pvp = new BigDecimal(txtPVP.getValue().toString());
        gananciaPorcentaje = RestClient.getRestTemplate()
                .getForObject("/productos/ganancia-porcentaje?"
                        + "precioCosto=" + new BigDecimal(txtPrecioCosto.getValue().toString())
                        + "&pvp=" + pvp,
                        BigDecimal.class);
        txtGananciaPorcentaje.setValue(gananciaPorcentaje);
    }
    
    private void calcularGananciaNeto() {
        gananciaNeto = RestClient.getRestTemplate()
                .getForObject("/productos/ganancia-neto?"
                        + "precioCosto=" + new BigDecimal(txtPrecioCosto.getValue().toString())
                        + "&gananciaPorcentaje=" + gananciaPorcentaje,
                        BigDecimal.class);
        txtGananciaNeto.setValue(gananciaNeto);
    }
    
    private void calcularPVP() {
        pvp = RestClient.getRestTemplate()
                .getForObject("/productos/pvp?"
                        + "precioCosto=" + new BigDecimal(txtPrecioCosto.getValue().toString())
                        + "&gananciaPorcentaje=" + gananciaPorcentaje,
                        BigDecimal.class);
        txtPVP.setValue(pvp);
    }
    
    private void calcularIVANeto() {
        IVANeto = RestClient.getRestTemplate()
                .getForObject("/productos/iva-neto?"
                        + "pvp=" + pvp
                        + "&ivaPorcentaje=" + new BigDecimal(cmbIVAPorcentaje.getSelectedItem().toString()),
                        BigDecimal.class);
        txtIVANeto.setValue(IVANeto);
    }
    
    private void calcularPrecioLista() {
        precioDeLista = RestClient.getRestTemplate()
                .getForObject("/productos/precio-lista?"
                        + "pvp=" + pvp
                        + "&ivaPorcentaje=" + new BigDecimal(cmbIVAPorcentaje.getSelectedItem().toString())
                        + "&impInternoPorcentaje=0",
                        BigDecimal.class);
        txtPrecioLista.setValue(precioDeLista);
    }
    
    private void calcularGananciaSegunPrecioDeLista() {      
        gananciaPorcentaje = RestClient.getRestTemplate()
                .getForObject("/productos/ganancia-porcentaje?ascendente=true"
                        + "&precioDeLista=" + new BigDecimal(txtPrecioLista.getValue().toString())
                        + "&precioDeListaAnterior=" + precioDeLista
                        + "&pvp=" + pvp
                        + "&ivaPorcentaje=" + new BigDecimal(cmbIVAPorcentaje.getSelectedItem().toString())
                        + "&impInternoPorcentaje=" + BigDecimal.ZERO
                        + "&precioCosto=" + precioDeCosto,
                        BigDecimal.class);
        txtGananciaPorcentaje.setValue(gananciaPorcentaje);
    }
    
    private void btn_GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GuardarActionPerformed
        Long idMedida = null;
        Long idRubro = null;
        Long idProveedor = null;
        boolean ejecutarOperacion = true;
        String mensajeError = "";
        for (Medida m : medidas) {
            if (m.getNombre().equals(cmb_Medida.getSelectedItem())) {
                idMedida = m.getId_Medida();
            }
        }
        for (Rubro r : rubros) {
            if (r.getNombre().equals(cmb_Rubro.getSelectedItem())) {
                idRubro = r.getId_Rubro();
            }
        }
        for (Proveedor p : proveedores) {
            if (p.getRazonSocial().equals(cmb_Proveedor.getSelectedItem())) {
                idProveedor = p.getId_Proveedor();
            }
        }
        if (idMedida == null) {
            mensajeError = mensajeError.concat(ResourceBundle.getBundle("Mensajes").getString("mensaje_producto_vacio_medida") + "\n");
            ejecutarOperacion = false;
        }
        if (idRubro == null) {
            mensajeError = mensajeError.concat(ResourceBundle.getBundle("Mensajes").getString("mensaje_producto_vacio_rubro") + "\n");
            ejecutarOperacion = false;
        }
        if (idProveedor == null) {
            mensajeError = mensajeError.concat(ResourceBundle.getBundle("Mensajes").getString("mensaje_producto_vacio_proveedor") + "\n");
            ejecutarOperacion = false;
        }
        if (ejecutarOperacion) {
            try {
                if (operacion == TipoDeOperacion.ALTA) {
                    Producto producto = new Producto();
                    producto.setCodigo(txt_Codigo.getText());
                    producto.setDescripcion(txt_Descripcion.getText().trim());
                    producto.setCantidad(new BigDecimal(txt_Cantidad.getValue().toString()));
                    producto.setCantMinima(new BigDecimal(txt_CantMinima.getValue().toString()));
                    producto.setVentaMinima(new BigDecimal(txt_VentaMinima.getValue().toString()));
                    producto.setPrecioCosto(new BigDecimal(txtPrecioCosto.getValue().toString()));
                    producto.setGanancia_porcentaje(new BigDecimal(txtGananciaPorcentaje.getValue().toString()));
                    producto.setGanancia_neto(new BigDecimal(txtGananciaNeto.getValue().toString()));
                    producto.setPrecioVentaPublico(new BigDecimal(txtPVP.getValue().toString()));
                    producto.setIva_porcentaje(new BigDecimal(cmbIVAPorcentaje.getSelectedItem().toString()));
                    producto.setIva_neto(new BigDecimal(txtIVANeto.getValue().toString()));
                    producto.setImpuestoInterno_porcentaje(BigDecimal.ZERO);
                    producto.setImpuestoInterno_neto(BigDecimal.ZERO);
                    producto.setPrecioLista(new BigDecimal(txtPrecioLista.getValue().toString()));
                    producto.setIlimitado(chk_Ilimitado.isSelected());
                    producto.setFechaUltimaModificacion(new Date());
                    producto.setEstanteria(txt_Estanteria.getText().trim());
                    producto.setEstante(txt_Estante.getText().trim());
                    producto.setNota(txt_Nota.getText().trim());
                    producto.setFechaAlta(new Date());
                    producto.setFechaVencimiento(dc_Vencimiento.getDate());
                    RestClient.getRestTemplate().postForObject("/productos?idMedida=" + idMedida + "&idRubro=" + idRubro
                            + "&idProveedor=" + idProveedor + "&idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                            producto, Producto.class);
                    LOGGER.warn("El producto " + producto + " se guardó correctamente");
                    int respuesta = JOptionPane.showConfirmDialog(this,
                            "El producto se guardó correctamente.\n¿Desea dar de alta otro producto?",
                            "Aviso", JOptionPane.YES_NO_OPTION);
                    this.limpiarYRecargarComponentes();
                    if (respuesta == JOptionPane.NO_OPTION) {
                        this.dispose();
                    }
                }

                if (operacion == TipoDeOperacion.ACTUALIZACION) {
                    productoParaModificar.setCodigo(txt_Codigo.getText());
                    productoParaModificar.setDescripcion(txt_Descripcion.getText().trim());
                    productoParaModificar.setCantidad(new BigDecimal(txt_Cantidad.getValue().toString()));
                    productoParaModificar.setCantMinima(new BigDecimal(txt_CantMinima.getValue().toString()));
                    productoParaModificar.setCantidad(new BigDecimal(txt_Cantidad.getValue().toString()));
                    productoParaModificar.setCantMinima(new BigDecimal(txt_CantMinima.getValue().toString()));
                    productoParaModificar.setVentaMinima(new BigDecimal(txt_VentaMinima.getValue().toString()));
                    productoParaModificar.setPrecioCosto(new BigDecimal(txtPrecioCosto.getValue().toString()));
                    productoParaModificar.setGanancia_porcentaje(new BigDecimal(txtGananciaPorcentaje.getValue().toString()));
                    productoParaModificar.setGanancia_neto(new BigDecimal(txtGananciaNeto.getValue().toString()));
                    productoParaModificar.setPrecioVentaPublico(new BigDecimal(txtPVP.getValue().toString()));
                    productoParaModificar.setIva_porcentaje(new BigDecimal(cmbIVAPorcentaje.getSelectedItem().toString()));
                    productoParaModificar.setIva_neto(new BigDecimal(txtIVANeto.getValue().toString()));
                    productoParaModificar.setImpuestoInterno_porcentaje(BigDecimal.ZERO);
                    productoParaModificar.setImpuestoInterno_neto(BigDecimal.ZERO);
                    productoParaModificar.setPrecioLista(new BigDecimal(txtPrecioLista.getValue().toString()));
                    productoParaModificar.setIlimitado(chk_Ilimitado.isSelected());
                    productoParaModificar.setFechaUltimaModificacion(new Date());
                    productoParaModificar.setEstanteria(txt_Estanteria.getText().trim());
                    productoParaModificar.setEstante(txt_Estante.getText().trim());
                    productoParaModificar.setNota(txt_Nota.getText().trim());
                    productoParaModificar.setFechaVencimiento(dc_Vencimiento.getDate());
                    RestClient.getRestTemplate().put("/productos?idMedida=" + idMedida + "&idRubro=" + idRubro
                            + "&idProveedor=" + idProveedor + "&idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                            productoParaModificar);
                    LOGGER.warn("El producto " + productoParaModificar + " se modificó correctamente");
                    JOptionPane.showMessageDialog(this, "El producto se modificó correctamente.",
                            "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
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
            JOptionPane.showMessageDialog(this, mensajeError, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_GuardarActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.prepararComponentes();
        this.cargarMedidas();
        this.cargarRubros();
        this.cargarProveedores();        
        if (operacion == TipoDeOperacion.ALTA) {
            this.setTitle("Nuevo Producto");
        } else if (operacion == TipoDeOperacion.ACTUALIZACION) {
            this.setTitle("Modificar Producto");
            this.cargarProductoParaModificar();
        }
    }//GEN-LAST:event_formWindowOpened

    private void txt_CantMinimaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_CantMinimaFocusGained
        SwingUtilities.invokeLater(() -> {
            txt_CantMinima.selectAll();
        });
    }//GEN-LAST:event_txt_CantMinimaFocusGained

    private void txt_CantidadFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_CantidadFocusGained
        SwingUtilities.invokeLater(() -> {
            txt_Cantidad.selectAll();
        });
    }//GEN-LAST:event_txt_CantidadFocusGained

    private void chk_IlimitadoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_IlimitadoItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            txt_Cantidad.setEnabled(false);
            lbl_Cantidad.setForeground(Color.LIGHT_GRAY);
            txt_CantMinima.setEnabled(false);
            lbl_CantMinima.setForeground(Color.LIGHT_GRAY);
            txt_VentaMinima.setEnabled(false);
            lbl_VentaMinima.setForeground(Color.LIGHT_GRAY);
        } else {
            txt_Cantidad.setEnabled(true);
            lbl_Cantidad.setForeground(Color.RED);
            txt_CantMinima.setEnabled(true);
            lbl_CantMinima.setForeground(Color.BLACK);
            txt_VentaMinima.setEnabled(true);
            lbl_VentaMinima.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_chk_IlimitadoItemStateChanged

    private void cmbIVAPorcentajeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbIVAPorcentajeItemStateChanged
        try {
            this.calcularIVANeto();
            this.calcularPrecioLista();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_cmbIVAPorcentajeItemStateChanged

    private void txtPVPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPVPActionPerformed
        try {            
            pvp = new BigDecimal(txtPVP.getValue().toString());
            this.calcularGananciaPorcentaje();
            this.calcularGananciaNeto();
            this.calcularPVP();
            this.calcularIVANeto();
            this.calcularPrecioLista();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_txtPVPActionPerformed

    private void txtPVPFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPVPFocusGained
        SwingUtilities.invokeLater(() -> {
            txtPVP.selectAll();
        });
    }//GEN-LAST:event_txtPVPFocusGained

    private void txtGananciaPorcentajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGananciaPorcentajeActionPerformed
        try {           
            gananciaPorcentaje = new BigDecimal(txtGananciaPorcentaje.getValue().toString());
            this.calcularGananciaNeto();
            this.calcularPVP();
            this.calcularIVANeto();
            this.calcularPrecioLista();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_txtGananciaPorcentajeActionPerformed

    private void txtGananciaPorcentajeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtGananciaPorcentajeFocusGained
        SwingUtilities.invokeLater(() -> {
            txtGananciaPorcentaje.selectAll();
        });
    }//GEN-LAST:event_txtGananciaPorcentajeFocusGained

    private void txtPrecioCostoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioCostoActionPerformed
        try {
            precioDeCosto = new BigDecimal(txtPrecioCosto.getValue().toString());
            this.calcularGananciaNeto();
            this.calcularPVP();
            this.calcularIVANeto();
            this.calcularPrecioLista();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_txtPrecioCostoActionPerformed

    private void txtPrecioCostoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPrecioCostoFocusGained
        SwingUtilities.invokeLater(() -> {
            txtPrecioCosto.selectAll();
        });
    }//GEN-LAST:event_txtPrecioCostoFocusGained

    private void btn_MedidasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_MedidasActionPerformed
        DetalleMedidaGUI gui_DetalleMedida = new DetalleMedidaGUI();
        gui_DetalleMedida.setModal(true);
        gui_DetalleMedida.setLocationRelativeTo(this);
        gui_DetalleMedida.setVisible(true);
        this.cargarMedidas();
    }//GEN-LAST:event_btn_MedidasActionPerformed

    private void btn_NuevoProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoProveedorActionPerformed
        DetalleProveedorGUI gui_DetalleProveedor = new DetalleProveedorGUI();
        gui_DetalleProveedor.setModal(true);
        gui_DetalleProveedor.setLocationRelativeTo(this);
        gui_DetalleProveedor.setVisible(true);
        this.cargarProveedores();
    }//GEN-LAST:event_btn_NuevoProveedorActionPerformed

    private void btn_RubrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_RubrosActionPerformed
        DetalleRubroGUI gui_DetalleRubro = new DetalleRubroGUI();
        gui_DetalleRubro.setModal(true);
        gui_DetalleRubro.setLocationRelativeTo(this);
        gui_DetalleRubro.setVisible(true);
        this.cargarRubros();
    }//GEN-LAST:event_btn_RubrosActionPerformed

    private void txtPrecioListaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioListaActionPerformed
        try {
            this.calcularGananciaSegunPrecioDeLista();
            this.calcularGananciaNeto();
            this.calcularPVP();
            this.calcularIVANeto();
            this.calcularPrecioLista();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_txtPrecioListaActionPerformed

    private void txtPrecioListaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPrecioListaFocusGained
        SwingUtilities.invokeLater(() -> {
            txtPrecioLista.selectAll();
        });
    }//GEN-LAST:event_txtPrecioListaFocusGained

    private void txt_VentaMinimaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_VentaMinimaFocusGained
        SwingUtilities.invokeLater(() -> {
            txt_VentaMinima.selectAll();
        });
    }//GEN-LAST:event_txt_VentaMinimaFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Guardar;
    private javax.swing.JButton btn_Medidas;
    private javax.swing.JButton btn_NuevoProveedor;
    private javax.swing.JButton btn_Rubros;
    private javax.swing.JCheckBox chk_Ilimitado;
    private javax.swing.JComboBox cmbIVAPorcentaje;
    private javax.swing.JComboBox cmb_Medida;
    private javax.swing.JComboBox cmb_Proveedor;
    private javax.swing.JComboBox cmb_Rubro;
    private com.toedter.calendar.JDateChooser dc_Vencimiento;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl_CantMinima;
    private javax.swing.JLabel lbl_Cantidad;
    private javax.swing.JLabel lbl_Codigo;
    private javax.swing.JLabel lbl_Descripcion;
    private javax.swing.JLabel lbl_Estante;
    private javax.swing.JLabel lbl_Estanteria;
    private javax.swing.JLabel lbl_FA;
    private javax.swing.JLabel lbl_FUM;
    private javax.swing.JLabel lbl_FechaAlta;
    private javax.swing.JLabel lbl_FechaUltimaModificacion;
    private javax.swing.JLabel lbl_Ganancia;
    private javax.swing.JLabel lbl_IVA;
    private javax.swing.JLabel lbl_Medida;
    private javax.swing.JLabel lbl_Nota;
    private javax.swing.JLabel lbl_PVP;
    private javax.swing.JLabel lbl_PrecioCosto;
    private javax.swing.JLabel lbl_PrecioLista;
    private javax.swing.JLabel lbl_Proveedor;
    private javax.swing.JLabel lbl_Rubro;
    private javax.swing.JLabel lbl_Ven;
    private javax.swing.JLabel lbl_VentaMinima;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panel2;
    private javax.swing.JPanel panel3;
    private javax.swing.JPanel panel5;
    private javax.swing.JPanel panelGeneral;
    private javax.swing.JPanel panelPropiedades;
    private javax.swing.JTabbedPane tp_Tabs;
    private javax.swing.JFormattedTextField txtGananciaNeto;
    private javax.swing.JFormattedTextField txtGananciaPorcentaje;
    private javax.swing.JFormattedTextField txtIVANeto;
    private javax.swing.JFormattedTextField txtPVP;
    private javax.swing.JFormattedTextField txtPrecioCosto;
    private javax.swing.JFormattedTextField txtPrecioLista;
    private javax.swing.JFormattedTextField txt_CantMinima;
    private javax.swing.JFormattedTextField txt_Cantidad;
    private javax.swing.JTextField txt_Codigo;
    private javax.swing.JTextField txt_Descripcion;
    private javax.swing.JTextField txt_Estante;
    private javax.swing.JTextField txt_Estanteria;
    private javax.swing.JTextArea txt_Nota;
    private javax.swing.JFormattedTextField txt_VentaMinima;
    // End of variables declaration//GEN-END:variables
 
}
