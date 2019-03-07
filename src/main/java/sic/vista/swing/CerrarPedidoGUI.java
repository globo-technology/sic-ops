package sic.vista.swing;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Cliente;
import sic.modelo.EmpresaActiva;
import sic.modelo.Localidad;
import sic.modelo.NuevoPedido;
import sic.modelo.Pedido;
import sic.modelo.Provincia;
import sic.modelo.Ubicacion;
import sic.modelo.UsuarioActivo;

public class CerrarPedidoGUI extends JDialog {

    private final NuevoPedido nuevoPedido;
    private final Cliente cliente;
    private Localidad localidadSeleccionada;
    private Pedido pedido;
    private Ubicacion ubicacionAModificar;
    private boolean actualizacionExitosa = false;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public CerrarPedidoGUI(NuevoPedido nuevoPedido, Cliente cliente) {
        this.nuevoPedido = nuevoPedido;
        this.cliente = cliente;
        this.ubicacionAModificar = this.cliente.getUbicacionEnvio();
        initComponents();
    }

    public CerrarPedidoGUI(Pedido pedido, Cliente cliente) {
        this.nuevoPedido = null;
        this.cliente = cliente;
        this.pedido = pedido;
        this.ubicacionAModificar = this.cliente.getUbicacionEnvio();
        initComponents();
    }
    
    private void cargarProvincias() {
        try {
            cmbProvinciasBusqueda.removeAllItems();
            cmbProvinciasBusqueda.addItem(null);
            List<Provincia> provincias = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/ubicaciones/provincias", Provincia[].class)));
            provincias.stream().forEach(p -> {
                cmbProvinciasBusqueda.addItem(p);
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

    private void cargarLocalidadesDeLaProvincia(Provincia provincia) {
        try {
            cmbLocalidad.removeAllItems();
            cmbLocalidad.addItem(null);
            if (provincia != null) {
                List<Localidad> localidades = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                        .getForObject("/ubicaciones/localidades/provincias/" + provincia.getId_Provincia(),
                                Localidad[].class)));
                localidades.stream().forEach(l -> cmbLocalidad.addItem(l));
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

    private void seleccionarProvinciaYProvinciaDeCliente() {
        if (this.cliente.getUbicacionEnvio() != null) {
            if (this.cliente.getUbicacionEnvio().getIdProvincia() != null) {
                Provincia provinciaASeleccionar = RestClient.getRestTemplate().getForObject("/ubicaciones/provincias/" + this.cliente.getUbicacionEnvio().getIdProvincia(), Provincia.class);
                cmbProvinciasBusqueda.setSelectedItem(provinciaASeleccionar);
            }
            if (this.cliente.getUbicacionEnvio().getIdLocalidad() != null) {
                Localidad localidadASeleccionar = RestClient.getRestTemplate().getForObject("/ubicaciones/localidades/" + this.cliente.getUbicacionEnvio().getIdLocalidad(), Localidad.class);
                cmbLocalidad.setSelectedItem(localidadASeleccionar);
            }
            localidadSeleccionada = (Localidad) cmbLocalidad.getSelectedItem();
        }
    }

    private void lanzarReportePedido(Pedido pedido) {
        if (Desktop.isDesktopSupported()) {
            try {
                byte[] reporte = RestClient.getRestTemplate()
                        .getForObject("/pedidos/" + pedido.getId_Pedido() + "/reporte", byte[].class);
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
    
    public boolean isActualizacionExitosa() {
        return this.actualizacionExitosa;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PanelCerrarPedido = new javax.swing.JPanel();
        lblCalle = new javax.swing.JLabel();
        txtCalle = new javax.swing.JTextField();
        lblNumero = new javax.swing.JLabel();
        ftfNumero = new javax.swing.JFormattedTextField();
        lblPiso = new javax.swing.JLabel();
        txtPiso = new javax.swing.JFormattedTextField();
        lblDepartamento = new javax.swing.JLabel();
        txtDepartamento = new javax.swing.JTextField();
        lblDescripcion = new javax.swing.JLabel();
        txtDescripcion = new javax.swing.JTextField();
        lblLatitud = new javax.swing.JLabel();
        ftfLatitud = new javax.swing.JFormattedTextField();
        lblLongitud = new javax.swing.JLabel();
        ftfLongitud = new javax.swing.JFormattedTextField();
        txtCodigoPostal = new javax.swing.JTextField();
        lblCodigoPostal = new javax.swing.JLabel();
        lblLocalidades = new javax.swing.JLabel();
        cmbLocalidad = new javax.swing.JComboBox<>();
        lblProvincia = new javax.swing.JLabel();
        cmbProvinciasBusqueda = new javax.swing.JComboBox<>();
        btnCerrarPedido = new javax.swing.JButton();
        chkEnviarUbicacionFacturacion = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cerrar Pedido");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        PanelCerrarPedido.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblCalle.setForeground(java.awt.Color.red);
        lblCalle.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCalle.setText("* Calle:");

        lblNumero.setForeground(java.awt.Color.red);
        lblNumero.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblNumero.setText("* Número:");

        ftfNumero.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        lblPiso.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPiso.setText("Piso:");

        txtPiso.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        lblDepartamento.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDepartamento.setText("Departamento:");

        lblDescripcion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDescripcion.setText("Descripción:");

        lblLatitud.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblLatitud.setText("Latitud:");

        ftfLatitud.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##0.#######"))));

        lblLongitud.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblLongitud.setText("Longitud:");

        ftfLongitud.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##0.#######"))));

        txtCodigoPostal.setEditable(false);

        lblCodigoPostal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCodigoPostal.setText("Código Postal:");

        lblLocalidades.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblLocalidades.setText("Localidad:");

        cmbLocalidad.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbLocalidadItemStateChanged(evt);
            }
        });

        lblProvincia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblProvincia.setText("Provincia:");

        cmbProvinciasBusqueda.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbProvinciasBusquedaItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout PanelCerrarPedidoLayout = new javax.swing.GroupLayout(PanelCerrarPedido);
        PanelCerrarPedido.setLayout(PanelCerrarPedidoLayout);
        PanelCerrarPedidoLayout.setHorizontalGroup(
            PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelCerrarPedidoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelCerrarPedidoLayout.createSequentialGroup()
                        .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblCodigoPostal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblProvincia, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblLocalidades, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbProvinciasBusqueda, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbLocalidad, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtCodigoPostal, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(PanelCerrarPedidoLayout.createSequentialGroup()
                        .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelCerrarPedidoLayout.createSequentialGroup()
                                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblDepartamento)
                                    .addComponent(lblDescripcion)
                                    .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(lblPiso)
                                        .addComponent(lblNumero))
                                    .addComponent(lblCalle))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(ftfNumero, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtPiso, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtDepartamento, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtCalle, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(PanelCerrarPedidoLayout.createSequentialGroup()
                                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblLatitud)
                                    .addComponent(lblLongitud))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ftfLatitud, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ftfLongitud, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        PanelCerrarPedidoLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblCalle, lblCodigoPostal, lblDepartamento, lblDescripcion, lblLatitud, lblLocalidades, lblLongitud, lblNumero, lblPiso, lblProvincia});

        PanelCerrarPedidoLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {ftfLatitud, ftfLongitud, ftfNumero, txtCalle, txtCodigoPostal, txtDepartamento, txtDescripcion, txtPiso});

        PanelCerrarPedidoLayout.setVerticalGroup(
            PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelCerrarPedidoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCalle)
                    .addComponent(txtCalle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNumero)
                    .addComponent(ftfNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPiso)
                    .addComponent(txtPiso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDepartamento)
                    .addComponent(txtDepartamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDescripcion)
                    .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cmbProvinciasBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProvincia))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cmbLocalidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLocalidades))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtCodigoPostal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCodigoPostal, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLatitud)
                    .addComponent(ftfLatitud, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLongitud)
                    .addComponent(ftfLongitud, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PanelCerrarPedidoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblCalle, lblCodigoPostal, lblDepartamento, lblDescripcion, lblLatitud, lblLocalidades, lblLongitud, lblNumero, lblPiso, lblProvincia});

        PanelCerrarPedidoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {ftfLatitud, ftfLongitud, ftfNumero, txtCalle, txtCodigoPostal, txtDepartamento, txtDescripcion, txtPiso});

        btnCerrarPedido.setForeground(java.awt.Color.blue);
        btnCerrarPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btnCerrarPedido.setText("Cerrar Pedido");
        btnCerrarPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarPedidoActionPerformed(evt);
            }
        });

        chkEnviarUbicacionFacturacion.setText("Enviar a la dirección de Facturación");
        chkEnviarUbicacionFacturacion.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkEnviarUbicacionFacturacionItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(chkEnviarUbicacionFacturacion))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(PanelCerrarPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCerrarPedido)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(chkEnviarUbicacionFacturacion)
                .addGap(7, 7, 7)
                .addComponent(PanelCerrarPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCerrarPedido)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        if (this.cliente.getUbicacionEnvio() != null) {
            txtCalle.setText(this.cliente.getUbicacionEnvio().getCalle());
            ftfNumero.setText(this.cliente.getUbicacionEnvio().getNumero().toString());
            if (this.cliente.getUbicacionEnvio().getPiso() != null) {
                txtPiso.setText(this.cliente.getUbicacionEnvio().getPiso().toString());
            }
            if (this.cliente.getUbicacionEnvio().getDepartamento() != null) {
                txtDepartamento.setText(this.cliente.getUbicacionEnvio().getDepartamento());
            }
            if (this.cliente.getUbicacionEnvio().getDescripcion() != null) {
                txtDescripcion.setText(this.cliente.getUbicacionEnvio().getDescripcion());
            }
            if (this.cliente.getUbicacionEnvio().getLatitud() != null) {
                ftfLatitud.setText(this.cliente.getUbicacionEnvio().getLatitud().toString());
            }
            if (this.cliente.getUbicacionEnvio().getLongitud() != null) {
                ftfLongitud.setText(this.cliente.getUbicacionEnvio().getLongitud().toString());
            }
        } else {
            this.cliente.setUbicacionEnvio(new Ubicacion());
        }
        this.cargarProvincias();
        this.cargarLocalidadesDeLaProvincia((Provincia) cmbProvinciasBusqueda.getSelectedItem());
        this.seleccionarProvinciaYProvinciaDeCliente();
    }//GEN-LAST:event_formWindowOpened

    private void btnCerrarPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarPedidoActionPerformed
        if (txtCalle.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_ubicacion_calle_vacia"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            if (ftfNumero.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_ubicacion_numero_vacio"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                cliente.getUbicacionEnvio().setCalle(txtCalle.getText());
                cliente.getUbicacionEnvio().setNumero(Integer.valueOf(ftfNumero.getText()));
                if (!txtPiso.getText().isEmpty()) {
                    cliente.getUbicacionEnvio().setPiso(Integer.valueOf(txtPiso.getText()));
                }
                if (!txtDepartamento.getText().isEmpty()) {
                    cliente.getUbicacionEnvio().setDepartamento(txtDepartamento.getText());
                }
                if (!txtDescripcion.getText().isEmpty()) {
                    cliente.getUbicacionEnvio().setDescripcion(txtDescripcion.getText());
                }
                if (!ftfLatitud.getText().isEmpty()) {
                    cliente.getUbicacionEnvio().setLatitud(Double.valueOf(ftfLatitud.getText()));
                }
                if (!ftfLongitud.getText().isEmpty()) {
                    cliente.getUbicacionEnvio().setLongitud(Double.valueOf(ftfLongitud.getText()));
                }
                if (cliente.getUbicacionEnvio().getIdUbicacion() == 0L) {
                    RestClient.getRestTemplate().postForObject("/ubicaciones/clientes/" + cliente.getId_Cliente() + "/envio", cliente.getUbicacionEnvio(), Ubicacion.class);
                } else {
                    RestClient.getRestTemplate().put("/ubicaciones", cliente.getUbicacionEnvio());
                }
                if (this.localidadSeleccionada != null) {
                    ubicacionAModificar.setNombreLocalidad(localidadSeleccionada.getNombre());
                    ubicacionAModificar.setIdLocalidad(localidadSeleccionada.getId_Localidad());
                    ubicacionAModificar.setCodigoPostal(localidadSeleccionada.getCodigoPostal());
                    ubicacionAModificar.setNombreProvincia(localidadSeleccionada.getNombreProvincia());
                    ubicacionAModificar.setIdProvincia(localidadSeleccionada.getIdProvincia());
                }
                try {
                    if (cliente.getUbicacionEnvio() == null && ubicacionAModificar != null) {
                        RestClient.getRestTemplate().postForObject("/ubicaciones/clientes/" + cliente.getId_Cliente() + "/envio", ubicacionAModificar, Ubicacion.class);
                    } else if (cliente.getUbicacionEnvio() != null && ubicacionAModificar != null) {
                        RestClient.getRestTemplate().put("/ubicaciones", ubicacionAModificar);
                    }
                    if (nuevoPedido != null) {
                        Pedido p = RestClient.getRestTemplate().postForObject("/pedidos?idEmpresa="
                                + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                                + "&idUsuario=" + UsuarioActivo.getInstance().getUsuario().getId_Usuario()
                                + "&idCliente=" + cliente.getId_Cliente()
                                + "&usarUbicacionDeFacturacion=" + chkEnviarUbicacionFacturacion.isSelected(), nuevoPedido, Pedido.class);
                        int reply = JOptionPane.showConfirmDialog(this,
                                ResourceBundle.getBundle("Mensajes").getString("mensaje_reporte"),
                                "Aviso", JOptionPane.YES_NO_OPTION);
                        if (reply == JOptionPane.YES_OPTION) {
                            this.lanzarReportePedido(p);
                        }
                    } else {
                        RestClient.getRestTemplate().put("/pedidos?idEmpresa="
                                + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                                + "&idUsuario=" + UsuarioActivo.getInstance().getUsuario().getId_Usuario()
                                + "&idCliente=" + cliente.getId_Cliente()
                                + "&usarUbicacionDeFacturacion=" + chkEnviarUbicacionFacturacion.isSelected(), pedido);
                        this.actualizacionExitosa = true;
                        JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_pedido_actualizado"),
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
                this.dispose();
            }
        }
    }//GEN-LAST:event_btnCerrarPedidoActionPerformed

    private void chkEnviarUbicacionFacturacionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkEnviarUbicacionFacturacionItemStateChanged
        lblCalle.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        lblDepartamento.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        lblDescripcion.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        lblLatitud.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        lblLongitud.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        lblNumero.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        lblPiso.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        txtCalle.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        txtDepartamento.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        txtDescripcion.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        txtPiso.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        ftfLatitud.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        ftfLongitud.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        ftfNumero.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        lblProvincia.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        cmbProvinciasBusqueda.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        lblLocalidades.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        cmbLocalidad.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        lblCodigoPostal.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        txtCodigoPostal.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
                
    }//GEN-LAST:event_chkEnviarUbicacionFacturacionItemStateChanged

    private void cmbLocalidadItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbLocalidadItemStateChanged
        localidadSeleccionada = (Localidad) cmbLocalidad.getSelectedItem();
        if (localidadSeleccionada != null) {
            if (this.cliente.getUbicacionEnvio() == null) {
                this.cliente.setUbicacionEnvio(new Ubicacion());
            }
            txtCodigoPostal.setText(localidadSeleccionada.getCodigoPostal());
            ubicacionAModificar.setNombreLocalidad(localidadSeleccionada.getNombre());
            ubicacionAModificar.setIdLocalidad(localidadSeleccionada.getId_Localidad());
            ubicacionAModificar.setCodigoPostal(localidadSeleccionada.getCodigoPostal());
            ubicacionAModificar.setNombreProvincia(localidadSeleccionada.getNombreProvincia());
            ubicacionAModificar.setIdProvincia(localidadSeleccionada.getIdProvincia());
        }
    }//GEN-LAST:event_cmbLocalidadItemStateChanged

    private void cmbProvinciasBusquedaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbProvinciasBusquedaItemStateChanged
        this.cargarLocalidadesDeLaProvincia((Provincia) cmbProvinciasBusqueda.getSelectedItem());
    }//GEN-LAST:event_cmbProvinciasBusquedaItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelCerrarPedido;
    private javax.swing.JButton btnCerrarPedido;
    private javax.swing.JCheckBox chkEnviarUbicacionFacturacion;
    private javax.swing.JComboBox<Localidad> cmbLocalidad;
    private javax.swing.JComboBox<Provincia> cmbProvinciasBusqueda;
    private javax.swing.JFormattedTextField ftfLatitud;
    private javax.swing.JFormattedTextField ftfLongitud;
    private javax.swing.JFormattedTextField ftfNumero;
    private javax.swing.JLabel lblCalle;
    private javax.swing.JLabel lblCodigoPostal;
    private javax.swing.JLabel lblDepartamento;
    private javax.swing.JLabel lblDescripcion;
    private javax.swing.JLabel lblLatitud;
    private javax.swing.JLabel lblLocalidades;
    private javax.swing.JLabel lblLongitud;
    private javax.swing.JLabel lblNumero;
    private javax.swing.JLabel lblPiso;
    private javax.swing.JLabel lblProvincia;
    private javax.swing.JTextField txtCalle;
    private javax.swing.JTextField txtCodigoPostal;
    private javax.swing.JTextField txtDepartamento;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JFormattedTextField txtPiso;
    // End of variables declaration//GEN-END:variables
}
