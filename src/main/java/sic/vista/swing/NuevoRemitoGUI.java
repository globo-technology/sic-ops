package sic.vista.swing;

import java.awt.Desktop;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.ParseException;
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
import sic.modelo.FacturaVenta;
import sic.modelo.NuevoRemito;
import sic.modelo.Remito;
import sic.modelo.TipoBulto;
import sic.modelo.Transportista;

public class NuevoRemitoGUI extends JDialog {

    private final List<FacturaVenta> facturaVenta;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private boolean remitoEmitido = false;

    public NuevoRemitoGUI(List<FacturaVenta> facturaVenta) {
        this.initComponents();
        this.setIcon();
        this.facturaVenta = facturaVenta;
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(BuscarProductosGUI.class.getResource("/sic/icons/SIC_16_square.png"));
        this.setIconImage(iconoVentana.getImage());
    }
    
    private void cargarTransportistas() {
        cmbTransportista.removeAllItems();
        List<Transportista> transportes = Arrays.asList(RestClient.getRestTemplate()
                .getForObject("/transportistas",
                        Transportista[].class));
        transportes.stream().forEach(t -> {
            cmbTransportista.addItem(t);
        });
    }

    private TipoBulto[] construirArrayTiposDeBulto() {
        List<TipoBulto> tiposDeBulto = new ArrayList<>();
        if (chkCaja.isSelected() && ftxtCantCaja.getValue() != null) {
            tiposDeBulto.add(TipoBulto.CAJA);
        }
        if (chkBolsa.isSelected() && ftxtCantBolsa.getValue() != null) {
            tiposDeBulto.add(TipoBulto.BOLSA);
        }
        if (chkRollo.isSelected() && ftxtCantRollo.getValue() != null) {
            tiposDeBulto.add(TipoBulto.ROLLO);
        }
        if (chkTacho.isSelected() && ftxtCantTacho.getValue() != null) {
            tiposDeBulto.add(TipoBulto.TACHO);
        }
        if (chkSobre.isSelected() && ftxtCantSobre.getValue() != null) {
            tiposDeBulto.add(TipoBulto.SOBRE);
        }
        if (chkAtado.isSelected() && ftxtCantAtado.getValue() != null) {
            tiposDeBulto.add(TipoBulto.ATADO);
        }
        if (chkPack.isSelected() && ftxtCantPack.getValue() != null) {
            tiposDeBulto.add(TipoBulto.PACK);
        }
        if (chkBalde.isSelected() && ftxtCantBalde.getValue() != null) {
            tiposDeBulto.add(TipoBulto.BALDE);
        }
        TipoBulto[] arrayTipoBulto = new TipoBulto[tiposDeBulto.size()];
        tiposDeBulto.toArray(arrayTipoBulto);
        return arrayTipoBulto;
    }

    private BigDecimal[] construirArrayCantidad() {
        List<BigDecimal> cantidades = new ArrayList<>();
        if (chkCaja.isSelected() && ftxtCantCaja.getValue() != null) {
            cantidades.add(new BigDecimal(ftxtCantCaja.getValue().toString()));
        }
        if (chkBolsa.isSelected() && ftxtCantBolsa.getValue() != null) {
            cantidades.add(new BigDecimal(ftxtCantBolsa.getValue().toString()));
        }
        if (chkRollo.isSelected() && ftxtCantRollo.getValue() != null) {
            cantidades.add(new BigDecimal(ftxtCantRollo.getValue().toString()));
        }
        if (chkTacho.isSelected() && ftxtCantTacho.getValue() != null) {
            cantidades.add(new BigDecimal(ftxtCantTacho.getValue().toString()));
        }
        if (chkSobre.isSelected() && ftxtCantSobre.getValue() != null) {
            cantidades.add(new BigDecimal(ftxtCantSobre.getValue().toString()));
        }
        if (chkAtado.isSelected() && ftxtCantAtado.getValue() != null) {
            cantidades.add(new BigDecimal(ftxtCantAtado.getValue().toString()));
        }
        if (chkPack.isSelected() && ftxtCantPack.getValue() != null) {
            cantidades.add(new BigDecimal(ftxtCantPack.getValue().toString()));
        }
        if (chkBalde.isSelected() && ftxtCantBalde.getValue() != null) {
            cantidades.add(new BigDecimal(ftxtCantBalde.getValue().toString()));
        }
        BigDecimal[] arrayCantidades = new BigDecimal[cantidades.size()];
        cantidades.toArray(arrayCantidades);
        return arrayCantidades;
    }

    private void lanzarReporteRemito(long idRemito) throws IOException {
        int reply = JOptionPane.showConfirmDialog(this,
                ResourceBundle.getBundle("Mensajes").getString("mensaje_reporte"),
                "Aviso", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            if (Desktop.isDesktopSupported()) {
                byte[] reporte = RestClient.getRestTemplate()
                        .getForObject("/remitos/" + idRemito + "/reporte", byte[].class);
                File f = new File(System.getProperty("user.home") + "/Remito.pdf");
                Files.write(f.toPath(), reporte);
                Desktop.getDesktop().open(f);
            } else {
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes")
                                .getString("mensaje_error_plataforma_no_soportada"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void calcularCantidadDeBultos() {
        ftxtCantBultos.setValue((ftxtCantCaja.isEnabled() && ftxtCantCaja.getText().trim() != null ? new BigDecimal(ftxtCantCaja.getText().trim()) : BigDecimal.ZERO)
                .add((ftxtCantBolsa.isEnabled() && ftxtCantBolsa.getText().trim() != null ? new BigDecimal(ftxtCantBolsa.getText().trim()) : BigDecimal.ZERO))
                .add((ftxtCantRollo.isEnabled() && ftxtCantRollo.getText().trim() != null ? new BigDecimal(ftxtCantRollo.getText().trim()) : BigDecimal.ZERO))
                .add((ftxtCantTacho.isEnabled() && ftxtCantTacho.getText().trim() != null ? new BigDecimal(ftxtCantTacho.getText().trim()) : BigDecimal.ZERO))
                .add((ftxtCantSobre.isEnabled() && ftxtCantSobre.getText().trim() != null ? new BigDecimal(ftxtCantSobre.getText().trim()) : BigDecimal.ZERO))
                .add((ftxtCantAtado.isEnabled() && ftxtCantAtado.getText().trim() != null ? new BigDecimal(ftxtCantAtado.getText().trim()) : BigDecimal.ZERO))
                .add((ftxtCantBalde.isEnabled() && ftxtCantBalde.getText().trim() != null ? new BigDecimal(ftxtCantBalde.getText().trim()) : BigDecimal.ZERO))
                .add((ftxtCantPack.isEnabled() && ftxtCantPack.getText().trim() != null ? new BigDecimal(ftxtCantPack.getText().trim()) : BigDecimal.ZERO)));
    }
    
    public boolean isExito() {
        return remitoEmitido;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlRenglones = new javax.swing.JPanel();
        chkCaja = new javax.swing.JCheckBox();
        chkBolsa = new javax.swing.JCheckBox();
        chkRollo = new javax.swing.JCheckBox();
        chkTacho = new javax.swing.JCheckBox();
        chkAtado = new javax.swing.JCheckBox();
        chkSobre = new javax.swing.JCheckBox();
        chkPack = new javax.swing.JCheckBox();
        ftxtCantCaja = new javax.swing.JFormattedTextField();
        ftxtCantBolsa = new javax.swing.JFormattedTextField();
        ftxtCantRollo = new javax.swing.JFormattedTextField();
        ftxtCantTacho = new javax.swing.JFormattedTextField();
        ftxtCantSobre = new javax.swing.JFormattedTextField();
        ftxtCantAtado = new javax.swing.JFormattedTextField();
        ftxtCantPack = new javax.swing.JFormattedTextField();
        lblBultos = new javax.swing.JLabel();
        ftxtCantBultos = new javax.swing.JFormattedTextField();
        sepRenglones = new javax.swing.JSeparator();
        lblUn1 = new javax.swing.JLabel();
        lblUn2 = new javax.swing.JLabel();
        lblUn3 = new javax.swing.JLabel();
        lblUn4 = new javax.swing.JLabel();
        lblUn5 = new javax.swing.JLabel();
        lblUn6 = new javax.swing.JLabel();
        lblUn7 = new javax.swing.JLabel();
        chkBalde = new javax.swing.JCheckBox();
        ftxtCantBalde = new javax.swing.JFormattedTextField();
        lblUn8 = new javax.swing.JLabel();
        pnpPesoVolumen = new javax.swing.JPanel();
        ftxtPeso = new javax.swing.JFormattedTextField();
        ftxtVolumen = new javax.swing.JFormattedTextField();
        lblPeso = new javax.swing.JLabel();
        lblVolumen = new javax.swing.JLabel();
        pnlDetalles = new javax.swing.JPanel();
        lblTransportista = new javax.swing.JLabel();
        cmbTransportista = new javax.swing.JComboBox<>();
        lblCostoDeEnvio = new javax.swing.JLabel();
        ftfCostoDeEnvio = new javax.swing.JFormattedTextField();
        lblObservaciones = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAreaObservaciones = new javax.swing.JTextArea();
        lblTotalFacturas = new javax.swing.JLabel();
        lblTotalF = new javax.swing.JLabel();
        lblAceptar = new javax.swing.JButton();

        setModal(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        pnlRenglones.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        chkCaja.setText("Caja");
        chkCaja.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkCajaItemStateChanged(evt);
            }
        });

        chkBolsa.setText("Bolsa");
        chkBolsa.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkBolsaItemStateChanged(evt);
            }
        });

        chkRollo.setText("Rollo");
        chkRollo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkRolloItemStateChanged(evt);
            }
        });

        chkTacho.setText("Tacho");
        chkTacho.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkTachoItemStateChanged(evt);
            }
        });

        chkAtado.setText("Atado");
        chkAtado.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkAtadoItemStateChanged(evt);
            }
        });

        chkSobre.setText("Sobre");
        chkSobre.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkSobreItemStateChanged(evt);
            }
        });

        chkPack.setText("Pack");
        chkPack.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkPackItemStateChanged(evt);
            }
        });

        ftxtCantCaja.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        ftxtCantCaja.setEnabled(false);
        ftxtCantCaja.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ftxtCantCajaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                ftxtCantCajaFocusLost(evt);
            }
        });
        ftxtCantCaja.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ftxtCantCajaKeyTyped(evt);
            }
        });

        ftxtCantBolsa.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        ftxtCantBolsa.setEnabled(false);
        ftxtCantBolsa.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ftxtCantBolsaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                ftxtCantBolsaFocusLost(evt);
            }
        });
        ftxtCantBolsa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ftxtCantBolsaKeyTyped(evt);
            }
        });

        ftxtCantRollo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        ftxtCantRollo.setEnabled(false);
        ftxtCantRollo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ftxtCantRolloFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                ftxtCantRolloFocusLost(evt);
            }
        });
        ftxtCantRollo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ftxtCantRolloKeyTyped(evt);
            }
        });

        ftxtCantTacho.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        ftxtCantTacho.setEnabled(false);
        ftxtCantTacho.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ftxtCantTachoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                ftxtCantTachoFocusLost(evt);
            }
        });
        ftxtCantTacho.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ftxtCantTachoKeyTyped(evt);
            }
        });

        ftxtCantSobre.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        ftxtCantSobre.setEnabled(false);
        ftxtCantSobre.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ftxtCantSobreFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                ftxtCantSobreFocusLost(evt);
            }
        });
        ftxtCantSobre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ftxtCantSobreKeyTyped(evt);
            }
        });

        ftxtCantAtado.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        ftxtCantAtado.setEnabled(false);
        ftxtCantAtado.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ftxtCantAtadoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                ftxtCantAtadoFocusLost(evt);
            }
        });
        ftxtCantAtado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ftxtCantAtadoKeyTyped(evt);
            }
        });

        ftxtCantPack.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        ftxtCantPack.setEnabled(false);
        ftxtCantPack.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ftxtCantPackFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                ftxtCantPackFocusLost(evt);
            }
        });
        ftxtCantPack.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ftxtCantPackKeyTyped(evt);
            }
        });

        lblBultos.setText("Total de Bultos:");

        ftxtCantBultos.setEditable(false);
        ftxtCantBultos.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        ftxtCantBultos.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ftxtCantBultosFocusGained(evt);
            }
        });
        ftxtCantBultos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ftxtCantBultosKeyTyped(evt);
            }
        });

        lblUn1.setText("UN");

        lblUn2.setText("UN");

        lblUn3.setText("UN");

        lblUn4.setText("UN");

        lblUn5.setText("UN");

        lblUn6.setText("UN");

        lblUn7.setText("UN");

        chkBalde.setText("Balde");
        chkBalde.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkBaldeItemStateChanged(evt);
            }
        });

        ftxtCantBalde.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        ftxtCantBalde.setEnabled(false);
        ftxtCantBalde.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ftxtCantBaldeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                ftxtCantBaldeFocusLost(evt);
            }
        });
        ftxtCantBalde.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ftxtCantBaldeKeyTyped(evt);
            }
        });

        lblUn8.setText("UN");

        javax.swing.GroupLayout pnlRenglonesLayout = new javax.swing.GroupLayout(pnlRenglones);
        pnlRenglones.setLayout(pnlRenglonesLayout);
        pnlRenglonesLayout.setHorizontalGroup(
            pnlRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRenglonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlRenglonesLayout.createSequentialGroup()
                        .addGroup(pnlRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkBolsa)
                            .addComponent(chkRollo)
                            .addComponent(chkTacho)
                            .addComponent(chkSobre)
                            .addComponent(chkAtado)
                            .addComponent(chkPack)
                            .addComponent(chkCaja))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(ftxtCantBolsa, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ftxtCantRollo, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ftxtCantTacho, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ftxtCantSobre, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ftxtCantAtado, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ftxtCantPack, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ftxtCantCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblUn1)
                            .addComponent(lblUn2)
                            .addComponent(lblUn3)
                            .addComponent(lblUn4)
                            .addComponent(lblUn5)
                            .addComponent(lblUn6)
                            .addComponent(lblUn7)))
                    .addGroup(pnlRenglonesLayout.createSequentialGroup()
                        .addComponent(lblBultos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ftxtCantBultos))
                    .addComponent(sepRenglones)
                    .addGroup(pnlRenglonesLayout.createSequentialGroup()
                        .addComponent(chkBalde)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ftxtCantBalde, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblUn8)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlRenglonesLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {chkAtado, chkBalde, chkBolsa, chkCaja, chkPack, chkRollo, chkSobre, chkTacho});

        pnlRenglonesLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {ftxtCantAtado, ftxtCantBalde, ftxtCantBolsa, ftxtCantCaja, ftxtCantPack, ftxtCantRollo, ftxtCantSobre, ftxtCantTacho});

        pnlRenglonesLayout.setVerticalGroup(
            pnlRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRenglonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ftxtCantCaja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCaja)
                    .addComponent(lblUn1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkBolsa)
                    .addComponent(ftxtCantBolsa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUn2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkRollo)
                    .addComponent(ftxtCantRollo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUn3))
                .addGap(5, 5, 5)
                .addGroup(pnlRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkTacho)
                    .addComponent(ftxtCantTacho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUn4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkSobre)
                    .addComponent(ftxtCantSobre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUn5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkAtado)
                    .addComponent(ftxtCantAtado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUn6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkPack)
                    .addComponent(ftxtCantPack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUn7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkBalde)
                    .addComponent(ftxtCantBalde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUn8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sepRenglones, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblBultos)
                    .addComponent(ftxtCantBultos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlRenglonesLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {chkAtado, chkBalde, chkBolsa, chkCaja, chkPack, chkRollo, chkSobre, chkTacho});

        pnlRenglonesLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {ftxtCantAtado, ftxtCantBalde, ftxtCantBolsa, ftxtCantCaja, ftxtCantPack, ftxtCantRollo, ftxtCantSobre, ftxtCantTacho});

        pnpPesoVolumen.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        ftxtPeso.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        ftxtPeso.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ftxtPesoFocusGained(evt);
            }
        });
        ftxtPeso.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ftxtPesoKeyTyped(evt);
            }
        });

        ftxtVolumen.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        ftxtVolumen.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ftxtVolumenFocusGained(evt);
            }
        });
        ftxtVolumen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ftxtVolumenKeyTyped(evt);
            }
        });

        lblPeso.setText("Peso Total (Kg):");

        lblVolumen.setText("Volumen Total (m3):");

        javax.swing.GroupLayout pnpPesoVolumenLayout = new javax.swing.GroupLayout(pnpPesoVolumen);
        pnpPesoVolumen.setLayout(pnpPesoVolumenLayout);
        pnpPesoVolumenLayout.setHorizontalGroup(
            pnpPesoVolumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnpPesoVolumenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnpPesoVolumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblPeso)
                    .addComponent(lblVolumen))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnpPesoVolumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ftxtVolumen)
                    .addComponent(ftxtPeso))
                .addContainerGap())
        );

        pnpPesoVolumenLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblPeso, lblVolumen});

        pnpPesoVolumenLayout.setVerticalGroup(
            pnpPesoVolumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnpPesoVolumenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnpPesoVolumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ftxtPeso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPeso))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnpPesoVolumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblVolumen)
                    .addComponent(ftxtVolumen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        pnpPesoVolumenLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblPeso, lblVolumen});

        pnlDetalles.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblTransportista.setText("Transportista:");

        lblCostoDeEnvio.setText("Costo de Envio ($):");

        ftfCostoDeEnvio.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        ftfCostoDeEnvio.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ftfCostoDeEnvioFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                ftfCostoDeEnvioFocusLost(evt);
            }
        });
        ftfCostoDeEnvio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ftfCostoDeEnvioKeyTyped(evt);
            }
        });

        lblObservaciones.setText("Observaciones");

        txtAreaObservaciones.setColumns(20);
        txtAreaObservaciones.setRows(5);
        jScrollPane1.setViewportView(txtAreaObservaciones);

        lblTotalFacturas.setText("Total Facturas ($):");

        javax.swing.GroupLayout pnlDetallesLayout = new javax.swing.GroupLayout(pnlDetalles);
        pnlDetalles.setLayout(pnlDetallesLayout);
        pnlDetallesLayout.setHorizontalGroup(
            pnlDetallesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDetallesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDetallesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                    .addGroup(pnlDetallesLayout.createSequentialGroup()
                        .addGroup(pnlDetallesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblCostoDeEnvio, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                            .addComponent(lblObservaciones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblTransportista, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                            .addComponent(lblTotalFacturas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlDetallesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbTransportista, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ftfCostoDeEnvio)
                            .addComponent(lblTotalF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        pnlDetallesLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblCostoDeEnvio, lblObservaciones, lblTransportista});

        pnlDetallesLayout.setVerticalGroup(
            pnlDetallesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDetallesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDetallesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cmbTransportista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTransportista))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetallesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblTotalFacturas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTotalF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetallesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ftfCostoDeEnvio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCostoDeEnvio))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblObservaciones)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlDetallesLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblCostoDeEnvio, lblObservaciones, lblTransportista});

        pnlDetallesLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {ftfCostoDeEnvio, lblTotalF});

        lblAceptar.setForeground(java.awt.Color.blue);
        lblAceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        lblAceptar.setText("Aceptar");
        lblAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lblAceptarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblAceptar))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(pnlRenglones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(pnpPesoVolumen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlDetalles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnpPesoVolumen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlDetalles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlRenglones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblAceptar)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ftxtCantCajaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxtCantCajaFocusGained
        SwingUtilities.invokeLater(() -> {
            ftxtCantCaja.selectAll();
        });
    }//GEN-LAST:event_ftxtCantCajaFocusGained

    private void ftxtCantCajaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ftxtCantCajaKeyTyped
        if (evt.getKeyChar() == KeyEvent.VK_MINUS) {
            evt.consume();
        }
    }//GEN-LAST:event_ftxtCantCajaKeyTyped

    private void ftxtCantBolsaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxtCantBolsaFocusGained
        SwingUtilities.invokeLater(() -> {
            ftxtCantBolsa.selectAll();
        });
    }//GEN-LAST:event_ftxtCantBolsaFocusGained

    private void ftxtCantBolsaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ftxtCantBolsaKeyTyped
        if (evt.getKeyChar() == KeyEvent.VK_MINUS) {
            evt.consume();
        }
    }//GEN-LAST:event_ftxtCantBolsaKeyTyped

    private void ftxtCantRolloFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxtCantRolloFocusGained
        SwingUtilities.invokeLater(() -> {
            ftxtCantRollo.selectAll();
        });
    }//GEN-LAST:event_ftxtCantRolloFocusGained

    private void ftxtCantRolloKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ftxtCantRolloKeyTyped
        if (evt.getKeyChar() == KeyEvent.VK_MINUS) {
            evt.consume();
        }
    }//GEN-LAST:event_ftxtCantRolloKeyTyped

    private void ftxtCantTachoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxtCantTachoFocusGained
        SwingUtilities.invokeLater(() -> {
            ftxtCantTacho.selectAll();
        });
    }//GEN-LAST:event_ftxtCantTachoFocusGained

    private void ftxtCantTachoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ftxtCantTachoKeyTyped
        if (evt.getKeyChar() == KeyEvent.VK_MINUS) {
            evt.consume();
        }
    }//GEN-LAST:event_ftxtCantTachoKeyTyped

    private void ftxtCantSobreFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxtCantSobreFocusGained
        SwingUtilities.invokeLater(() -> {
            ftxtCantSobre.selectAll();
        });
    }//GEN-LAST:event_ftxtCantSobreFocusGained

    private void ftxtCantSobreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ftxtCantSobreKeyTyped
        if (evt.getKeyChar() == KeyEvent.VK_MINUS) {
            evt.consume();
        }
    }//GEN-LAST:event_ftxtCantSobreKeyTyped

    private void ftxtCantAtadoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxtCantAtadoFocusGained
        SwingUtilities.invokeLater(() -> {
            ftxtCantAtado.selectAll();
        });
    }//GEN-LAST:event_ftxtCantAtadoFocusGained

    private void ftxtCantAtadoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ftxtCantAtadoKeyTyped
        if (evt.getKeyChar() == KeyEvent.VK_MINUS) {
            evt.consume();
        }
    }//GEN-LAST:event_ftxtCantAtadoKeyTyped

    private void ftxtCantPackFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxtCantPackFocusGained
        SwingUtilities.invokeLater(() -> {
            ftxtCantPack.selectAll();
        });
    }//GEN-LAST:event_ftxtCantPackFocusGained

    private void ftxtCantPackKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ftxtCantPackKeyTyped
        if (evt.getKeyChar() == KeyEvent.VK_MINUS) {
            evt.consume();
        }
    }//GEN-LAST:event_ftxtCantPackKeyTyped

    private void ftxtCantBultosFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxtCantBultosFocusGained
        SwingUtilities.invokeLater(() -> {
            ftxtCantBultos.selectAll();
        });
    }//GEN-LAST:event_ftxtCantBultosFocusGained

    private void ftxtCantBultosKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ftxtCantBultosKeyTyped
        if (evt.getKeyChar() == KeyEvent.VK_MINUS) {
            evt.consume();
        }
    }//GEN-LAST:event_ftxtCantBultosKeyTyped

    private void ftxtPesoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxtPesoFocusGained
        SwingUtilities.invokeLater(() -> {
            ftxtPeso.selectAll();
        });
    }//GEN-LAST:event_ftxtPesoFocusGained

    private void ftxtPesoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ftxtPesoKeyTyped
        if (evt.getKeyChar() == KeyEvent.VK_MINUS) {
            evt.consume();
        }
    }//GEN-LAST:event_ftxtPesoKeyTyped

    private void ftxtVolumenFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxtVolumenFocusGained
        SwingUtilities.invokeLater(() -> {
            ftxtCantCaja.selectAll();
        });
    }//GEN-LAST:event_ftxtVolumenFocusGained

    private void ftxtVolumenKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ftxtVolumenKeyTyped
        if (evt.getKeyChar() == KeyEvent.VK_MINUS) {
            evt.consume();
        }
    }//GEN-LAST:event_ftxtVolumenKeyTyped

    private void ftfCostoDeEnvioFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftfCostoDeEnvioFocusGained
        SwingUtilities.invokeLater(() -> {
            ftfCostoDeEnvio.selectAll();
        });
    }//GEN-LAST:event_ftfCostoDeEnvioFocusGained

    private void ftfCostoDeEnvioKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ftfCostoDeEnvioKeyTyped
        if (evt.getKeyChar() == KeyEvent.VK_MINUS) {
            evt.consume();
        }
    }//GEN-LAST:event_ftfCostoDeEnvioKeyTyped

    private void lblAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lblAceptarActionPerformed
        long[] idsFacturas = new long[facturaVenta.size()];
        for (int i = 0; i < facturaVenta.size(); i++) {
            idsFacturas[i] = facturaVenta.get(i).getIdFactura();
        }
        NuevoRemito nuevoRemito = NuevoRemito.builder()
                .idFacturaVenta(idsFacturas)
                .cantidadPorBulto(this.construirArrayCantidad())
                .tiposDeBulto(this.construirArrayTiposDeBulto())
                .idTransportista(((Transportista) cmbTransportista.getSelectedItem()).getIdTransportista())
                .pesoTotalEnKg(ftxtPeso.getValue() != null ? new BigDecimal(ftxtPeso.getValue().toString()) : null)
                .volumenTotalEnM3(ftxtVolumen.getValue() != null ? new BigDecimal(ftxtVolumen.getValue().toString()) : null)
                .costoDeEnvio(ftfCostoDeEnvio.getValue() != null ? new BigDecimal(ftfCostoDeEnvio.getValue().toString()) : null)
                .observaciones(txtAreaObservaciones.getText())
                .build();
        try {
            Remito remitoCreado = RestClient.getRestTemplate().postForObject("/remitos",
                    nuevoRemito, Remito.class);
            this.lanzarReporteRemito(remitoCreado.getIdRemito());
            remitoEmitido = true;
            this.dispose();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_IOException"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_lblAceptarActionPerformed

    private void chkCajaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkCajaItemStateChanged
        if (chkCaja.isSelected()) {
            ftxtCantCaja.setEnabled(true);
        } else {
            ftxtCantCaja.setEnabled(false);
        }
        this.calcularCantidadDeBultos();
    }//GEN-LAST:event_chkCajaItemStateChanged

    private void chkBolsaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkBolsaItemStateChanged
        if (chkBolsa.isSelected()) {
            ftxtCantBolsa.setEnabled(true);
        } else {
            ftxtCantBolsa.setEnabled(false);
        }
        this.calcularCantidadDeBultos();
    }//GEN-LAST:event_chkBolsaItemStateChanged

    private void chkRolloItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkRolloItemStateChanged
        if (chkRollo.isSelected()) {
            ftxtCantRollo.setEnabled(true);
        } else {
            ftxtCantRollo.setEnabled(false);
        }
        this.calcularCantidadDeBultos();
    }//GEN-LAST:event_chkRolloItemStateChanged

    private void chkTachoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkTachoItemStateChanged
        if (chkTacho.isSelected()) {
            ftxtCantTacho.setEnabled(true);
        } else {
            ftxtCantTacho.setEnabled(false);
        }
        this.calcularCantidadDeBultos();
    }//GEN-LAST:event_chkTachoItemStateChanged

    private void chkSobreItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkSobreItemStateChanged
        if (chkSobre.isSelected()) {
            ftxtCantSobre.setEnabled(true);
        } else {
            ftxtCantSobre.setEnabled(false);
        }
        this.calcularCantidadDeBultos();
    }//GEN-LAST:event_chkSobreItemStateChanged

    private void chkAtadoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkAtadoItemStateChanged
        if (chkAtado.isSelected()) {
            ftxtCantAtado.setEnabled(true);
        } else {
            ftxtCantAtado.setEnabled(false);
        }
        this.calcularCantidadDeBultos();
    }//GEN-LAST:event_chkAtadoItemStateChanged

    private void chkPackItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkPackItemStateChanged
        if (chkPack.isSelected()) {
            ftxtCantPack.setEnabled(true);
        } else {
            ftxtCantPack.setEnabled(false);
        }
        this.calcularCantidadDeBultos();
    }//GEN-LAST:event_chkPackItemStateChanged

    private void ftxtCantCajaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxtCantCajaFocusLost
        try {
            ftxtCantCaja.commitEdit();
            this.calcularCantidadDeBultos();
        } catch (ParseException ex) {
        }
    }//GEN-LAST:event_ftxtCantCajaFocusLost

    private void ftxtCantBolsaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxtCantBolsaFocusLost
        try {
            ftxtCantBolsa.commitEdit();
            this.calcularCantidadDeBultos();
        } catch (ParseException ex) {
        }
    }//GEN-LAST:event_ftxtCantBolsaFocusLost

    private void ftxtCantRolloFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxtCantRolloFocusLost
        try {
            ftxtCantRollo.commitEdit();
            this.calcularCantidadDeBultos();
        } catch (ParseException ex) {
        }
    }//GEN-LAST:event_ftxtCantRolloFocusLost

    private void ftxtCantTachoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxtCantTachoFocusLost
        try {
            ftxtCantTacho.commitEdit();
            this.calcularCantidadDeBultos();
        } catch (ParseException ex) {
        }
    }//GEN-LAST:event_ftxtCantTachoFocusLost

    private void ftxtCantSobreFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxtCantSobreFocusLost
        try {
            ftxtCantSobre.commitEdit();
            this.calcularCantidadDeBultos();
        } catch (ParseException ex) {
        }
    }//GEN-LAST:event_ftxtCantSobreFocusLost

    private void ftxtCantAtadoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxtCantAtadoFocusLost
        try {
            ftxtCantAtado.commitEdit();
            this.calcularCantidadDeBultos();
        } catch (ParseException ex) {
        }
    }//GEN-LAST:event_ftxtCantAtadoFocusLost

    private void ftxtCantPackFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxtCantPackFocusLost
        try {
            ftxtCantPack.commitEdit();
            this.calcularCantidadDeBultos();
        } catch (ParseException ex) {
        }
    }//GEN-LAST:event_ftxtCantPackFocusLost

    private void ftfCostoDeEnvioFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftfCostoDeEnvioFocusLost
        try {
            ftfCostoDeEnvio.commitEdit();
        } catch (ParseException ex) {
        }
    }//GEN-LAST:event_ftfCostoDeEnvioFocusLost

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.cargarTransportistas();
        if (facturaVenta.size() == 1) {
            this.setTitle("Nuevo Remito de " + facturaVenta.get(0).getTipoComprobante() + " Nº " + facturaVenta.get(0).getNumSerie() + " - " + facturaVenta.get(0).getNumFactura());
        } else {
            this.setTitle("Nuevo Remito de varias Facturas");
        }
        ftxtCantCaja.setValue(BigDecimal.ONE);
        ftxtCantBolsa.setValue(BigDecimal.ONE);
        ftxtCantRollo.setValue(BigDecimal.ONE);
        ftxtCantTacho.setValue(BigDecimal.ONE);
        ftxtCantSobre.setValue(BigDecimal.ONE);
        ftxtCantAtado.setValue(BigDecimal.ONE);
        ftxtCantPack.setValue(BigDecimal.ONE);
        ftxtCantBalde.setValue(BigDecimal.ONE);
        ftxtCantBultos.setValue(BigDecimal.ZERO);
        ftfCostoDeEnvio.setValue(BigDecimal.ZERO);
        DecimalFormat dFormat = new DecimalFormat("##,##0.##");
        lblTotalF.setText(dFormat.format(facturaVenta.stream().map(FacturaVenta::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add)));
    }//GEN-LAST:event_formWindowOpened

    private void chkBaldeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkBaldeItemStateChanged
        if (chkBalde.isSelected()) {
            ftxtCantBalde.setEnabled(true);
        } else {
            ftxtCantBalde.setEnabled(false);
        }
        this.calcularCantidadDeBultos();
    }//GEN-LAST:event_chkBaldeItemStateChanged

    private void ftxtCantBaldeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxtCantBaldeFocusGained
        SwingUtilities.invokeLater(() -> {
            ftxtCantBalde.selectAll();
        });
    }//GEN-LAST:event_ftxtCantBaldeFocusGained

    private void ftxtCantBaldeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxtCantBaldeFocusLost
        try {
            ftxtCantBalde.commitEdit();
            this.calcularCantidadDeBultos();
        } catch (ParseException ex) {
        }
    }//GEN-LAST:event_ftxtCantBaldeFocusLost

    private void ftxtCantBaldeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ftxtCantBaldeKeyTyped
        if (evt.getKeyChar() == KeyEvent.VK_MINUS) {
            evt.consume();
        }
    }//GEN-LAST:event_ftxtCantBaldeKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkAtado;
    private javax.swing.JCheckBox chkBalde;
    private javax.swing.JCheckBox chkBolsa;
    private javax.swing.JCheckBox chkCaja;
    private javax.swing.JCheckBox chkPack;
    private javax.swing.JCheckBox chkRollo;
    private javax.swing.JCheckBox chkSobre;
    private javax.swing.JCheckBox chkTacho;
    private javax.swing.JComboBox<Transportista> cmbTransportista;
    private javax.swing.JFormattedTextField ftfCostoDeEnvio;
    private javax.swing.JFormattedTextField ftxtCantAtado;
    private javax.swing.JFormattedTextField ftxtCantBalde;
    private javax.swing.JFormattedTextField ftxtCantBolsa;
    private javax.swing.JFormattedTextField ftxtCantBultos;
    private javax.swing.JFormattedTextField ftxtCantCaja;
    private javax.swing.JFormattedTextField ftxtCantPack;
    private javax.swing.JFormattedTextField ftxtCantRollo;
    private javax.swing.JFormattedTextField ftxtCantSobre;
    private javax.swing.JFormattedTextField ftxtCantTacho;
    private javax.swing.JFormattedTextField ftxtPeso;
    private javax.swing.JFormattedTextField ftxtVolumen;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton lblAceptar;
    private javax.swing.JLabel lblBultos;
    private javax.swing.JLabel lblCostoDeEnvio;
    private javax.swing.JLabel lblObservaciones;
    private javax.swing.JLabel lblPeso;
    private javax.swing.JLabel lblTotalF;
    private javax.swing.JLabel lblTotalFacturas;
    private javax.swing.JLabel lblTransportista;
    private javax.swing.JLabel lblUn1;
    private javax.swing.JLabel lblUn2;
    private javax.swing.JLabel lblUn3;
    private javax.swing.JLabel lblUn4;
    private javax.swing.JLabel lblUn5;
    private javax.swing.JLabel lblUn6;
    private javax.swing.JLabel lblUn7;
    private javax.swing.JLabel lblUn8;
    private javax.swing.JLabel lblVolumen;
    private javax.swing.JPanel pnlDetalles;
    private javax.swing.JPanel pnlRenglones;
    private javax.swing.JPanel pnpPesoVolumen;
    private javax.swing.JSeparator sepRenglones;
    private javax.swing.JTextArea txtAreaObservaciones;
    // End of variables declaration//GEN-END:variables

}
