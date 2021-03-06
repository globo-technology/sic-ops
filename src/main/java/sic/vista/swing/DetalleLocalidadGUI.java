package sic.vista.swing;

import java.math.BigDecimal;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Localidad;

public class DetalleLocalidadGUI extends javax.swing.JDialog {

    private final Localidad localidadSeleccionada;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    
    public DetalleLocalidadGUI(Localidad localidad) {
        initComponents();
        this.setIcon(); 
        localidadSeleccionada = localidad;
    }
    
    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(DetalleSucursalGUI.class.getResource("/sic/icons/EditMap_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlDetalle = new javax.swing.JPanel();
        lblLocalidad = new javax.swing.JLabel();
        lblCodigoPostal = new javax.swing.JLabel();
        lblProvincia = new javax.swing.JLabel();
        lblEnvio = new javax.swing.JLabel();
        lblCostoDeEnvio = new javax.swing.JLabel();
        lblDetalleLocalidad = new javax.swing.JLabel();
        lblDetalleProvincia = new javax.swing.JLabel();
        chkEnvioGratuito = new javax.swing.JCheckBox();
        ftfCostoDeEnvio = new javax.swing.JFormattedTextField();
        txtCodigoPostal = new javax.swing.JTextField();
        btnAceptar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Modificar Localidad");
        setIconImage(null);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        pnlDetalle.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblLocalidad.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblLocalidad.setText("Nombre:");

        lblCodigoPostal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCodigoPostal.setText("Codigo Postal:");

        lblProvincia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblProvincia.setText("Provincia:");

        lblEnvio.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblEnvio.setText("Envío gratuito:");

        lblCostoDeEnvio.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCostoDeEnvio.setText("Costo de envío:");

        lblDetalleLocalidad.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblDetalleProvincia.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        chkEnvioGratuito.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkEnvioGratuitoItemStateChanged(evt);
            }
        });

        ftfCostoDeEnvio.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        ftfCostoDeEnvio.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        javax.swing.GroupLayout pnlDetalleLayout = new javax.swing.GroupLayout(pnlDetalle);
        pnlDetalle.setLayout(pnlDetalleLayout);
        pnlDetalleLayout.setHorizontalGroup(
            pnlDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDetalleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblCostoDeEnvio)
                    .addComponent(lblEnvio)
                    .addComponent(lblProvincia)
                    .addComponent(lblLocalidad)
                    .addComponent(lblCodigoPostal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lblDetalleProvincia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtCodigoPostal, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
                        .addComponent(lblDetalleLocalidad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ftfCostoDeEnvio))
                    .addComponent(chkEnvioGratuito))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlDetalleLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblCodigoPostal, lblCostoDeEnvio, lblEnvio, lblLocalidad, lblProvincia});

        pnlDetalleLayout.setVerticalGroup(
            pnlDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDetalleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtCodigoPostal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCodigoPostal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblLocalidad)
                    .addComponent(lblDetalleLocalidad))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblDetalleProvincia)
                    .addComponent(lblProvincia))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkEnvioGratuito)
                    .addComponent(lblEnvio))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ftfCostoDeEnvio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCostoDeEnvio))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlDetalleLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {ftfCostoDeEnvio, lblDetalleLocalidad, lblDetalleProvincia, txtCodigoPostal});

        btnAceptar.setForeground(java.awt.Color.blue);
        btnAceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btnAceptar.setText("Guardar");
        btnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAceptar)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAceptar)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed
        localidadSeleccionada.setEnvioGratuito(chkEnvioGratuito.isSelected());
        try {
            if (ftfCostoDeEnvio.getText() != null && !ftfCostoDeEnvio.getText().isEmpty()) {
                localidadSeleccionada.setCostoEnvio(new BigDecimal(ftfCostoDeEnvio.getValue().toString()));
            }
            if (txtCodigoPostal.getText() != null && !txtCodigoPostal.getText().isEmpty()) {
                localidadSeleccionada.setCodigoPostal(txtCodigoPostal.getText());
            } else {
                localidadSeleccionada.setCodigoPostal(null);
            }
            RestClient.getRestTemplate().put("/ubicaciones/localidades", this.localidadSeleccionada);
            JOptionPane.showMessageDialog(this, "La Localidad se modificó correctamente!",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnAceptarActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        lblDetalleLocalidad.setText(this.localidadSeleccionada.getNombre());
        lblDetalleProvincia.setText(this.localidadSeleccionada.getNombreProvincia());
        chkEnvioGratuito.setSelected(this.localidadSeleccionada.isEnvioGratuito());
        if (this.localidadSeleccionada.getCodigoPostal() != null) {
            txtCodigoPostal.setText(this.localidadSeleccionada.getCodigoPostal());
        }
        if (this.localidadSeleccionada.getCostoEnvio() != null) {
            ftfCostoDeEnvio.setValue(this.localidadSeleccionada.getCostoEnvio());
        } else {
            ftfCostoDeEnvio.setValue(BigDecimal.ZERO);
        }
        if (this.localidadSeleccionada.isEnvioGratuito()) {
            ftfCostoDeEnvio.setEnabled(false);
        }
    }//GEN-LAST:event_formWindowOpened

    private void chkEnvioGratuitoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkEnvioGratuitoItemStateChanged
        if (chkEnvioGratuito.isSelected()) {
            ftfCostoDeEnvio.setEnabled(false);
        } else {
            ftfCostoDeEnvio.setEnabled(true);
        }
    }//GEN-LAST:event_chkEnvioGratuitoItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JCheckBox chkEnvioGratuito;
    private javax.swing.JFormattedTextField ftfCostoDeEnvio;
    private javax.swing.JLabel lblCodigoPostal;
    private javax.swing.JLabel lblCostoDeEnvio;
    private javax.swing.JLabel lblDetalleLocalidad;
    private javax.swing.JLabel lblDetalleProvincia;
    private javax.swing.JLabel lblEnvio;
    private javax.swing.JLabel lblLocalidad;
    private javax.swing.JLabel lblProvincia;
    private javax.swing.JPanel pnlDetalle;
    private javax.swing.JTextField txtCodigoPostal;
    // End of variables declaration//GEN-END:variables
}
