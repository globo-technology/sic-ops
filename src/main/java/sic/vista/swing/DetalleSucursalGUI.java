package sic.vista.swing;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.CategoriaIVA;
import sic.modelo.Sucursal;
import sic.modelo.TipoDeOperacion;
import sic.modelo.Ubicacion;
import sic.util.FiltroImagenes;
import sic.util.Utilidades;

public class DetalleSucursalGUI extends JDialog {
    
    private byte[] logo = null;
    private boolean cambioLogo = false;
    private final int anchoImagenContainer = 180;
    private final int altoImagenContainer = 135;            
    private Sucursal sucursalModificar;
    private final TipoDeOperacion operacion;
    private Ubicacion ubicacion;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public DetalleSucursalGUI() {
        this.initComponents();
        this.setIcon();        
        operacion = TipoDeOperacion.ALTA;
    }

    public DetalleSucursalGUI(Sucursal sucursal) {
        this.initComponents();
        this.setIcon();        
        operacion = TipoDeOperacion.ACTUALIZACION;
        sucursalModificar = sucursal;
    }
    
    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(DetalleSucursalGUI.class.getResource("/sic/icons/Sucursal_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }   
    
    private void cargarComboBoxCondicionesIVA() {
        cmbCategoriaIVA.removeAllItems();
        for (CategoriaIVA c : CategoriaIVA.values()) {
            cmbCategoriaIVA.addItem(c);
        } 
    }
    
    private void cargarSucursalParaModificar() {
        txt_Nombre.setText(sucursalModificar.getNombre());
        txt_Lema.setText(sucursalModificar.getLema());        
        cmbCategoriaIVA.setSelectedItem(sucursalModificar.getCategoriaIVA());                    
        txtIdFiscal.setValue(sucursalModificar.getIdFiscal());                            
        txtIngresosBrutos.setValue(sucursalModificar.getIngresosBrutos());
        dc_FechaInicioActividad.setDate(java.util.Date
                .from(sucursalModificar.getFechaInicioActividad().atZone(ZoneId.systemDefault())
                        .toInstant()));
        txt_Email.setText(sucursalModificar.getEmail());
        txt_Telefono.setText(sucursalModificar.getTelefono());
        if (sucursalModificar.getUbicacion() != null) {
            lblDetalleUbicacionSucursal.setText(sucursalModificar.getUbicacion().toString());
            this.ubicacion = sucursalModificar.getUbicacion();
        }
        if (sucursalModificar.getLogo() == null || "".equals(sucursalModificar.getLogo())) {
            lbl_Logo.setText("SIN IMAGEN");
            logo = null;
        } else {
            lbl_Logo.setText("");
            Image image = null;
            try {
                URL url = new URL(sucursalModificar.getLogo());
                image = ImageIO.read(url);
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_404_logo"),
                        "Error", JOptionPane.ERROR_MESSAGE);
                this.dispose();
            }
            ImageIcon imagenLogo = new ImageIcon(image);
            ImageIcon logoRedimensionado = new ImageIcon(imagenLogo.getImage()
                    .getScaledInstance(anchoImagenContainer, altoImagenContainer, Image.SCALE_SMOOTH));
            lbl_Logo.setIcon(logoRedimensionado);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelPrincipal = new javax.swing.JPanel();
        lbl_Nombre = new javax.swing.JLabel();
        lbl_Lema = new javax.swing.JLabel();
        lbl_CondicionIVA = new javax.swing.JLabel();
        lbl_CUIP = new javax.swing.JLabel();
        lbl_IngBrutos = new javax.swing.JLabel();
        lbl_FIA = new javax.swing.JLabel();
        txt_Nombre = new javax.swing.JTextField();
        txt_Lema = new javax.swing.JTextField();
        cmbCategoriaIVA = new javax.swing.JComboBox();
        dc_FechaInicioActividad = new com.toedter.calendar.JDateChooser();
        lbl_Logo = new javax.swing.JLabel();
        btn_ExaminarArchivos = new javax.swing.JButton();
        btn_EliminarLogo = new javax.swing.JButton();
        lbl_Email = new javax.swing.JLabel();
        txt_Email = new javax.swing.JTextField();
        lbl_Telefono = new javax.swing.JLabel();
        txtIdFiscal = new javax.swing.JFormattedTextField();
        txtIngresosBrutos = new javax.swing.JFormattedTextField();
        lblAspectRatio = new javax.swing.JLabel();
        lblTamanioMax = new javax.swing.JLabel();
        lblUbicacion = new javax.swing.JLabel();
        btnUbicacion = new javax.swing.JButton();
        txt_Telefono = new javax.swing.JTextField();
        lblDetalleUbicacionSucursal = new javax.swing.JLabel();
        btn_Guardar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        panelPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_Nombre.setForeground(java.awt.Color.red);
        lbl_Nombre.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Nombre.setText("* Nombre:");

        lbl_Lema.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Lema.setText("Lema:");

        lbl_CondicionIVA.setForeground(java.awt.Color.red);
        lbl_CondicionIVA.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_CondicionIVA.setText("* Condición IVA:");

        lbl_CUIP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_CUIP.setText("CUIT:");

        lbl_IngBrutos.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_IngBrutos.setText("Ingresos Brutos:");

        lbl_FIA.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_FIA.setText("Inicio de Actividad:");

        dc_FechaInicioActividad.setDateFormatString("dd/MM/yyyy");

        lbl_Logo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_Logo.setText("SIN IMAGEN");
        lbl_Logo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lbl_Logo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        btn_ExaminarArchivos.setForeground(java.awt.Color.blue);
        btn_ExaminarArchivos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddPicture_16x16.png"))); // NOI18N
        btn_ExaminarArchivos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ExaminarArchivosActionPerformed(evt);
            }
        });

        btn_EliminarLogo.setForeground(java.awt.Color.blue);
        btn_EliminarLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/RemovePicture_16x16.png"))); // NOI18N
        btn_EliminarLogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarLogoActionPerformed(evt);
            }
        });

        lbl_Email.setForeground(java.awt.Color.red);
        lbl_Email.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Email.setText("* Email:");

        lbl_Telefono.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Telefono.setText("Teléfono:");

        txtIdFiscal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#"))));
        txtIdFiscal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtIdFiscalFocusLost(evt);
            }
        });

        txtIngresosBrutos.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#"))));
        txtIngresosBrutos.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtIngresosBrutosFocusLost(evt);
            }
        });

        lblAspectRatio.setForeground(java.awt.Color.gray);
        lblAspectRatio.setText("Relacion de aspecto 4:3");

        lblTamanioMax.setForeground(java.awt.Color.gray);
        lblTamanioMax.setText("Maximo 1MB");

        lblUbicacion.setForeground(java.awt.Color.red);
        lblUbicacion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblUbicacion.setText("* Ubicación:");

        btnUbicacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EditMap_16x16.png"))); // NOI18N
        btnUbicacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbicacionActionPerformed(evt);
            }
        });

        lblDetalleUbicacionSucursal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDetalleUbicacionSucursal.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lbl_Nombre, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                            .addComponent(lbl_Lema, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelPrincipalLayout.createSequentialGroup()
                                .addComponent(lbl_Logo, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btn_ExaminarArchivos)
                                    .addComponent(btn_EliminarLogo)
                                    .addComponent(lblTamanioMax)
                                    .addComponent(lblAspectRatio)))
                            .addComponent(txt_Lema, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txt_Nombre, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                        .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lbl_Telefono, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_Email, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_CondicionIVA, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_CUIP, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_IngBrutos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_FIA, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblUbicacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                                .addComponent(lblDetalleUbicacionSucursal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(0, 0, 0)
                                .addComponent(btnUbicacion))
                            .addComponent(txtIdFiscal, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtIngresosBrutos, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(dc_FechaInicioActividad, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_Email, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txt_Telefono, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cmbCategoriaIVA, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addComponent(btn_ExaminarArchivos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_EliminarLogo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTamanioMax)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblAspectRatio))
                    .addComponent(lbl_Logo, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Nombre)
                    .addComponent(txt_Nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Lema)
                    .addComponent(txt_Lema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_CondicionIVA)
                    .addComponent(cmbCategoriaIVA, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_CUIP)
                    .addComponent(txtIdFiscal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_IngBrutos)
                    .addComponent(txtIngresosBrutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_FIA)
                    .addComponent(dc_FechaInicioActividad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Email)
                    .addComponent(txt_Email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Telefono)
                    .addComponent(txt_Telefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnUbicacion)
                    .addComponent(lblUbicacion)
                    .addComponent(lblDetalleUbicacionSucursal, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnUbicacion, lblDetalleUbicacionSucursal});

        btn_Guardar.setForeground(java.awt.Color.blue);
        btn_Guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btn_Guardar.setText("Guardar");
        btn_Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_GuardarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_Guardar)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_Guardar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GuardarActionPerformed
        try {
            String mensaje = "";
            if (operacion == TipoDeOperacion.ALTA) {
                Sucursal sucursal = new Sucursal();
                sucursal.setNombre(txt_Nombre.getText().trim());
                sucursal.setLema(txt_Lema.getText().trim());                
                sucursal.setCategoriaIVA((CategoriaIVA) cmbCategoriaIVA.getSelectedItem());
                sucursal.setIdFiscal((Long) txtIdFiscal.getValue());
                sucursal.setIngresosBrutos((Long) txtIngresosBrutos.getValue());
                if (dc_FechaInicioActividad.getDate() != null) {
                    sucursal.setFechaInicioActividad(Instant.ofEpochMilli(dc_FechaInicioActividad.getDate().getTime())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime());
                }
                sucursal.setEmail(txt_Email.getText().trim());
                sucursal.setTelefono(txt_Telefono.getText().trim());
                sucursal.setUbicacion(this.ubicacion);
                sucursal = RestClient.getRestTemplate().postForObject("/sucursales", sucursal, Sucursal.class);
                mensaje = "La Sucursal " + txt_Nombre.getText().trim() + " se guardó correctamente.";
                if (logo == null) {
                    sucursal.setLogo("");
                } else {
                    sucursal.setLogo(RestClient.getRestTemplate()
                            .postForObject("/sucursales/" + sucursal.getIdSucursal() + "/logo", logo, String.class));
                    RestClient.getRestTemplate().put("/sucursales", sucursal);
                }
            }
            if (operacion == TipoDeOperacion.ACTUALIZACION) {
                sucursalModificar.setNombre(txt_Nombre.getText().trim());
                sucursalModificar.setLema(txt_Lema.getText().trim());                
                sucursalModificar.setCategoriaIVA((CategoriaIVA) cmbCategoriaIVA.getSelectedItem());
                sucursalModificar.setIdFiscal((Long) txtIdFiscal.getValue());
                sucursalModificar.setIngresosBrutos((Long) txtIngresosBrutos.getValue());               
                sucursalModificar.setFechaInicioActividad(Instant.ofEpochMilli(dc_FechaInicioActividad.getDate().getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime());
                sucursalModificar.setEmail(txt_Email.getText().trim());
                sucursalModificar.setTelefono(txt_Telefono.getText().trim());
                sucursalModificar.setUbicacion(this.ubicacion);
                if (cambioLogo && logo != null) {
                    sucursalModificar.setLogo(RestClient.getRestTemplate()
                            .postForObject("/sucursales/" + sucursalModificar.getIdSucursal() + "/logo", logo, String.class));
                } else if (cambioLogo && logo == null) {
                    sucursalModificar.setLogo(null);
                }
                RestClient.getRestTemplate().put("/sucursales", sucursalModificar);
                mensaje = "La Sucursal " + txt_Nombre.getText().trim() + " se modificó correctamente.";
            }
            LOGGER.warn(mensaje);
            JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();        
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {            
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_GuardarActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.cargarComboBoxCondicionesIVA();
        if (operacion == TipoDeOperacion.ACTUALIZACION) {
            this.setTitle("Modificar Sucursal");
            this.cargarSucursalParaModificar();
        } else {
            this.setTitle("Nueva Sucursal");
        }
    }//GEN-LAST:event_formWindowOpened

    private void btn_EliminarLogoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarLogoActionPerformed
        lbl_Logo.setIcon(null);
        lbl_Logo.setText("SIN IMAGEN");
        logo = null;
        cambioLogo = true;
    }//GEN-LAST:event_btn_EliminarLogoActionPerformed

    private void btn_ExaminarArchivosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ExaminarArchivosActionPerformed
        JFileChooser menuElegirLogo = new JFileChooser();
        menuElegirLogo.setAcceptAllFileFilterUsed(false);
        menuElegirLogo.addChoosableFileFilter(new FiltroImagenes());
        try {
            if (menuElegirLogo.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                if (Utilidades.esTamanioValido(menuElegirLogo.getSelectedFile(), 1024000)) {
                    File file = menuElegirLogo.getSelectedFile();
                    logo = Utilidades.convertirFileIntoByteArray(file);
                    ImageIcon logoProvisional = new ImageIcon(menuElegirLogo.getSelectedFile().getAbsolutePath());
                    ImageIcon logoRedimensionado = new ImageIcon(logoProvisional.getImage()
                            .getScaledInstance(anchoImagenContainer, altoImagenContainer, Image.SCALE_SMOOTH));
                    lbl_Logo.setIcon(logoRedimensionado);
                    lbl_Logo.setText("");
                    cambioLogo = true;
                } else {
                    JOptionPane.showMessageDialog(this, "El tamaño del archivo seleccionado supera el límite de 1MB.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    logo = null;
                }
            } else {
                logo = null;
            }
        } catch (IOException ex) {
            String mensaje = ResourceBundle.getBundle("Mensajes").getString("mensaje_error_IOException");
            LOGGER.error(mensaje + " - " + ex.getMessage());
            JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_ExaminarArchivosActionPerformed

    private void txtIdFiscalFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIdFiscalFocusLost
        if (txtIdFiscal.getText().equals("")) txtIdFiscal.setValue(null);
    }//GEN-LAST:event_txtIdFiscalFocusLost

    private void txtIngresosBrutosFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIngresosBrutosFocusLost
        if (txtIngresosBrutos.getText().equals("")) txtIngresosBrutos.setValue(null);
    }//GEN-LAST:event_txtIngresosBrutosFocusLost

    private void btnUbicacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbicacionActionPerformed
        DetalleUbicacionGUI guiDetalleUbicacion = new DetalleUbicacionGUI(this.ubicacion, "Ubicación Sucursal");
        guiDetalleUbicacion.setModal(true);
        guiDetalleUbicacion.setLocationRelativeTo(this);
        guiDetalleUbicacion.setVisible(true);
        if (guiDetalleUbicacion.getUbicacionModificada() != null) {
            this.ubicacion = guiDetalleUbicacion.getUbicacionModificada();
            lblDetalleUbicacionSucursal.setText(this.ubicacion.toString());
        }
    }//GEN-LAST:event_btnUbicacionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnUbicacion;
    private javax.swing.JButton btn_EliminarLogo;
    private javax.swing.JButton btn_ExaminarArchivos;
    private javax.swing.JButton btn_Guardar;
    private javax.swing.JComboBox cmbCategoriaIVA;
    private com.toedter.calendar.JDateChooser dc_FechaInicioActividad;
    private javax.swing.JLabel lblAspectRatio;
    private javax.swing.JLabel lblDetalleUbicacionSucursal;
    private javax.swing.JLabel lblTamanioMax;
    private javax.swing.JLabel lblUbicacion;
    private javax.swing.JLabel lbl_CUIP;
    private javax.swing.JLabel lbl_CondicionIVA;
    private javax.swing.JLabel lbl_Email;
    private javax.swing.JLabel lbl_FIA;
    private javax.swing.JLabel lbl_IngBrutos;
    private javax.swing.JLabel lbl_Lema;
    private javax.swing.JLabel lbl_Logo;
    private javax.swing.JLabel lbl_Nombre;
    private javax.swing.JLabel lbl_Telefono;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JFormattedTextField txtIdFiscal;
    private javax.swing.JFormattedTextField txtIngresosBrutos;
    private javax.swing.JTextField txt_Email;
    private javax.swing.JTextField txt_Lema;
    private javax.swing.JTextField txt_Nombre;
    private javax.swing.JTextField txt_Telefono;
    // End of variables declaration//GEN-END:variables
}
