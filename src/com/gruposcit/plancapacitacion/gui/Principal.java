/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gruposcit.plancapacitacion.gui;

import com.gruposcit.plancapacitacion.utils.GeneraXml;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.aguilar.swinglib.swing.fl.dialogs.FlDialog;
import org.aguilar.swinglib.swing.renderers.MultiLineCellRenderer;
import org.aguilar.swinglib.utils.EasyEntry;
import org.aguilar.swinglib.utils.EasyMap;
import org.aguilar.swinglib.utils.MultipleFileFilter;

/**
 *
 * @author leofavio_ar
 */
public class Principal extends javax.swing.JFrame {

    private JFileChooser jfc = new JFileChooser(System.getProperty("user.home"));
    private MultipleFileFilter fileFilter = new MultipleFileFilter("Archivo .icc", "icc");
    private static final int ACTUAL = 0;
    private static final int ANTERIOR = 1;
    
    /** Creates new form Principal */
    public Principal() {
        initComponents();
        llenarTipos();
        inicializarActual();
        inicializarAnterior();
        this.setTitle("Herramienta para generar archivo de cursos de capacitación");
        this.setIconImage(new ImageIcon(this.getClass().getResource("/img/gruposcit_logo.png")).getImage());
        jfc.setFileFilter(fileFilter);
        contenedor.setUI(new BasicTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount, int maxTabHeight) {
                return 0;
            }
            @Override
            protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect) {
                
            }
            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                
            }
        });
        ycPeriodo.addPropertyChangeListener("year", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (rbActual.isSelected()) {
                    controlOpciones(Principal.ACTUAL);
                } else if (rbAnterior.isSelected()) {
                    controlOpciones(Principal.ANTERIOR);
                }
            }
        });
        btnAgregarHandler();
        llenarAreas();
        limpiar();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    private void llenarAreas() {
        liAreas.setModel(new DefaultListModel());
    }
    private void inicializarActual() {
        actual.setDataProvider(
                new ArrayList<Map>(), 
                new String[] {"tipo", "nombre", "feini", "fefin", "areas", "num_per"}, 
                new String[] {"Tipo", "Nombre", "Fecha ini.", "Fecha fin.", "Áreas", "Núm. pers."});
        actual.setColumnCellRenderer(new MultiLineCellRenderer(), new int[] {1, 4});
    }
    private void inicializarAnterior() {
        anterior.setDataProvider(
                new ArrayList<Map>(), 
                new String[] {"tipo", "nombre", "feini", "fefin", "areas", "num_per", "documento"}, 
                new String[] {"Tipo", "Nombre", "Fecha ini.", "Fecha fin.", "Áreas", "Núm. pers.", "Documento"});
        anterior.setColumnCellRenderer(new MultiLineCellRenderer(), new int[] {1, 4});
    }
    private void llenarTipos() {
        ArrayList<Map> al = new ArrayList<>();
        al.add(EasyMap.crearMap(new EasyEntry("id", "01"), new EasyEntry("valor", "Curso")));
        al.add(EasyMap.crearMap(new EasyEntry("id", "02"), new EasyEntry("valor", "Seminario")));
        al.add(EasyMap.crearMap(new EasyEntry("id", "03"), new EasyEntry("valor", "Diplomado")));
        al.add(EasyMap.crearMap(new EasyEntry("id", "04"), new EasyEntry("valor", "Taller")));
        al.add(EasyMap.crearMap(new EasyEntry("id", "05"), new EasyEntry("valor", "Conferencias")));
        al.add(EasyMap.crearMap(new EasyEntry("id", "06"), new EasyEntry("valor", "Foros")));
        al.add(EasyMap.crearMap(new EasyEntry("id", "07"), new EasyEntry("valor", "Especialidades")));
        al.add(EasyMap.crearMap(new EasyEntry("id", "08"), new EasyEntry("valor", "Maestrías")));
        al.add(EasyMap.crearMap(new EasyEntry("id", "09"), new EasyEntry("valor", "Cursos en Línea")));
        cbTipo.setDataProvider(al, "valor");
    }
    private void agregar() {
        if (cbMesInicio.getSelectedIndex() == 0) {
            FlDialog.showFullWarningDialog("Debe seleccionar un mes de inicio...");
            return;
        }
        if (cbMesFin.getSelectedIndex() == 0) {
            FlDialog.showFullWarningDialog("Debe seleccionar un mes de finalización...");
            return;
        }
        if (liAreas.getModel().getSize() == 0) {
            FlDialog.showFullWarningDialog("Debe agregar por lo menos un área capacitada...");
            return;
        }
        if (txtNombre.getText().trim().equals("")) {
            FlDialog.showFullWarningDialog("Escriba el nombre de la capacitación...");
            return;
        }
        if (rbAnterior.isSelected() && txtDocumento.getText().trim().equals("")) {
            FlDialog.showFullWarningDialog("Escriba el nombre del documento emitido...");
            return;
        }
        if (rbActual.isSelected()) {
            agregarActual();
        } else if (rbAnterior.isSelected()) {
            agregarAnterior();
        }
        FlDialog.showFullInformationDialog("Se ha agregado correctamente el registro.");
        limpiar();
    }
    private void agregarActual() {
        actual.addRow(EasyMap.crearMap(
                new EasyEntry("tipo", cbTipo.getSelectedMap().get("id").toString()),
                new EasyEntry("nombre", txtNombre.getText().trim()),
                new EasyEntry("feini", String.format("%s-%02d", String.valueOf(ycAnoInicio.getYear()), cbMesInicio.getSelectedIndex())),
                new EasyEntry("fefin", String.format("%s-%02d", String.valueOf(ycAnoFin.getYear()), cbMesFin.getSelectedIndex())),
                new EasyEntry("areas", liAreas.getModel().getSize() > 1 ? join(((DefaultListModel)liAreas.getModel()).toArray(), ",") : ""),
                new EasyEntry("num_per", txtPersonas.getText().trim())
        ));
    }
    private void agregarAnterior() {
        anterior.addRow(EasyMap.crearMap(
                new EasyEntry("tipo", cbTipo.getSelectedMap().get("id").toString()),
                new EasyEntry("nombre", txtNombre.getText().trim()),
                new EasyEntry("feini", String.format("%s-%02d", String.valueOf(ycAnoInicio.getYear()), cbMesInicio.getSelectedIndex())),
                new EasyEntry("fefin", String.format("%s-%02d", String.valueOf(ycAnoFin.getYear()), cbMesFin.getSelectedIndex())),
                new EasyEntry("areas", liAreas.getModel().getSize() > 1 ? join(((DefaultListModel)liAreas.getModel()).toArray(), ",") : ""),
                new EasyEntry("num_per", txtPersonas.getText().trim()),
                new EasyEntry("documento", txtDocumento.getText().trim())
        ));
    }
    private void limpiar() {
        cbTipo.setSelectedIndex(0);
        txtNombre.setText("");
        cbMesInicio.setSelectedIndex(0);
        cbMesFin.setSelectedIndex(0);
//        ycPeriodo.setYear(Calendar.getInstance().get(Calendar.YEAR) - 1);
        rbAnterior.setSelected(true);
        txtAreas.setText("");
        ((DefaultListModel)liAreas.getModel()).removeAllElements();
        txtPersonas.setText("1");
        txtDocumento.setText("");
    }
    private void exportar() {
        if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                new GeneraXml(
                        jfc.getSelectedFile().getCanonicalPath(), 
                        txtOrgano.getText().trim(), 
                        txtSujeto.getText().trim(), 
                        String.valueOf(ycPeriodo.getYear()), 
                        (ArrayList<Map>)actual.getDataProvider(), 
                        (ArrayList<Map>)anterior.getDataProvider());
            } catch (IOException ex) {
                Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TransformerException ex) {
                Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    private void agregarArea() {
        ((DefaultListModel)liAreas.getModel()).addElement(txtAreas.getText().trim());
        txtAreas.setText("");
        txtAreas.requestFocusInWindow();
    }
    private void eliminarArea() {
        ((DefaultListModel)liAreas.getModel()).removeElementAt(liAreas.getSelectedIndex());
    }
    private String join(Object[] lista, String separador) {
        StringBuilder b = new StringBuilder();
        for(Object obj: lista) { 
            b.append(separador).append(obj.toString());
        }
        return b.toString().substring(separador.length());
    }
    private void controlOpciones(int opcion) {
        switch (opcion) {
            case Principal.ACTUAL:
                ycAnoInicio.setYear(ycPeriodo.getYear() + 1);
                ycAnoFin.setYear(ycPeriodo.getYear() + 1);
                txtDocumento.setEditable(false);
                break;
            case Principal.ANTERIOR:
                ycAnoInicio.setYear(ycPeriodo.getYear());
                ycAnoFin.setYear(ycPeriodo.getYear());
                txtDocumento.setEditable(true);
                break;
        }
    }
    private void btnAnteriorHandler() {
        contenedor.setSelectedIndex(1);
        btnAnterior.setSelected(true);
        btnActual.setSelected(false);
        btnAgregar.setSelected(false);
        btnGenerales.setSelected(false);
    }
    private void btnActualHandler() {
        contenedor.setSelectedIndex(2);
        btnAnterior.setSelected(false);
        btnActual.setSelected(true);
        btnAgregar.setSelected(false);
        btnGenerales.setSelected(false);
    }
    private void btnAgregarHandler() {
        contenedor.setSelectedIndex(0);
        btnAnterior.setSelected(false);
        btnActual.setSelected(false);
        btnAgregar.setSelected(true);
        btnGenerales.setSelected(false);
    }
    private void btnGeneralesHandler() {
        contenedor.setSelectedIndex(3);
        btnAnterior.setSelected(false);
        btnActual.setSelected(false);
        btnAgregar.setSelected(false);
        btnGenerales.setSelected(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgTipo = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jToolBar2 = new javax.swing.JToolBar();
        btnAgregar = new javax.swing.JButton();
        btnAnterior = new javax.swing.JButton();
        btnActual = new javax.swing.JButton();
        btnGenerales = new javax.swing.JButton();
        contenedor = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        cbTipo = new org.aguilar.swinglib.swing.fl.FlComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtNombre = new org.aguilar.swinglib.swing.fl.FlStringField();
        txtAreas = new org.aguilar.swinglib.swing.fl.FlStringField();
        txtDocumento = new org.aguilar.swinglib.swing.fl.FlStringField();
        txtPersonas = new org.aguilar.swinglib.swing.fl.FlStringField();
        jButton3 = new javax.swing.JButton();
        cbMesInicio = new javax.swing.JComboBox<>();
        ycAnoInicio = new com.toedter.calendar.JYearChooser();
        cbMesFin = new javax.swing.JComboBox<>();
        ycAnoFin = new com.toedter.calendar.JYearChooser();
        jPanel5 = new javax.swing.JPanel();
        rbAnterior = new javax.swing.JRadioButton();
        rbActual = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        liAreas = new javax.swing.JList<>();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        anterior = new org.aguilar.swinglib.swing.fl.FlTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        actual = new org.aguilar.swinglib.swing.fl.FlTable();
        jPanel6 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        txtOrgano = new org.aguilar.swinglib.swing.fl.FlStringField();
        jLabel21 = new javax.swing.JLabel();
        txtSujeto = new org.aguilar.swinglib.swing.fl.FlStringField();
        jLabel22 = new javax.swing.JLabel();
        ycPeriodo = new com.toedter.calendar.JYearChooser();
        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerLocation(150);
        jSplitPane1.setDividerSize(0);

        jToolBar2.setBackground(new java.awt.Color(255, 255, 255));
        jToolBar2.setFloatable(false);
        jToolBar2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar2.setRollover(true);

        btnAgregar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnAgregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/agregar.png"))); // NOI18N
        btnAgregar.setText("Agregar registro");
        btnAgregar.setFocusable(false);
        btnAgregar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAgregar.setMaximumSize(new java.awt.Dimension(150, 70));
        btnAgregar.setMinimumSize(new java.awt.Dimension(150, 0));
        btnAgregar.setOpaque(false);
        btnAgregar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });
        jToolBar2.add(btnAgregar);

        btnAnterior.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnAnterior.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/lista.png"))); // NOI18N
        btnAnterior.setText("Año anterior");
        btnAnterior.setFocusable(false);
        btnAnterior.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAnterior.setMaximumSize(new java.awt.Dimension(150, 70));
        btnAnterior.setMinimumSize(new java.awt.Dimension(150, 0));
        btnAnterior.setOpaque(false);
        btnAnterior.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnteriorActionPerformed(evt);
            }
        });
        jToolBar2.add(btnAnterior);

        btnActual.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnActual.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/lista.png"))); // NOI18N
        btnActual.setText("Año actual");
        btnActual.setFocusable(false);
        btnActual.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnActual.setMaximumSize(new java.awt.Dimension(150, 70));
        btnActual.setMinimumSize(new java.awt.Dimension(150, 0));
        btnActual.setOpaque(false);
        btnActual.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnActual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualActionPerformed(evt);
            }
        });
        jToolBar2.add(btnActual);

        btnGenerales.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnGenerales.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/generales.png"))); // NOI18N
        btnGenerales.setText("Datos generales");
        btnGenerales.setFocusable(false);
        btnGenerales.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGenerales.setMaximumSize(new java.awt.Dimension(150, 70));
        btnGenerales.setMinimumSize(new java.awt.Dimension(150, 0));
        btnGenerales.setOpaque(false);
        btnGenerales.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGenerales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGeneralesActionPerformed(evt);
            }
        });
        jToolBar2.add(btnGenerales);

        jSplitPane1.setLeftComponent(jToolBar2);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText("Tipo de capacitación:");

        cbTipo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel4.setText("Nombre de capacitación:");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel5.setText("Fecha de inicio:");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel6.setText("Fecha de finalización:");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel7.setText("Áreas capacitadas:");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel8.setText("Total de personas:");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel9.setText("Documento emitido:");

        txtNombre.setUpperCase(true);
        txtNombre.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        txtAreas.setUpperCase(true);
        txtAreas.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtAreas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAreasKeyReleased(evt);
            }
        });

        txtDocumento.setUpperCase(true);
        txtDocumento.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        txtPersonas.setDefaultText("1");
        txtPersonas.setOnlyDigits(true);
        txtPersonas.setUpperCase(true);
        txtPersonas.setDefaultTextOnLostFocusIfNull(true);
        txtPersonas.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jButton3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/add.png"))); // NOI18N
        jButton3.setText("Agregar registro");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        cbMesInicio.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cbMesInicio.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione un mes...", "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE" }));

        ycAnoInicio.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        cbMesFin.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cbMesFin.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione un mes...", "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE" }));

        ycAnoFin.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Período a reportar", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel5.setOpaque(false);

        bgTipo.add(rbAnterior);
        rbAnterior.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        rbAnterior.setText("Año anterior");
        rbAnterior.setOpaque(false);
        rbAnterior.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbAnteriorItemStateChanged(evt);
            }
        });

        bgTipo.add(rbActual);
        rbActual.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        rbActual.setText("Año actual");
        rbActual.setOpaque(false);
        rbActual.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbActualItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbAnterior)
                .addGap(18, 18, 18)
                .addComponent(rbActual)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(rbAnterior)
                .addComponent(rbActual))
        );

        liAreas.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        liAreas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                liAreasKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(liAreas);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 51, 255));
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/add.png"))); // NOI18N
        jLabel10.setText("<html><u>Agregar área</u></html>");
        jLabel10.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel10MouseClicked(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 51, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/delete.png"))); // NOI18N
        jLabel11.setText("<html><u>Eliminar seleccionado</u></html>");
        jLabel11.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel11MouseClicked(evt);
            }
        });

        jLabel12.setForeground(new java.awt.Color(204, 0, 51));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel12.setText("Sólo aplica para reportar capacitaciones del año anterior");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jButton3)
                                    .addComponent(txtNombre, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtDocumento, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtPersonas, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                        .addComponent(cbMesInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ycAnoInicio, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(cbMesFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(ycAnoFin, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                            .addComponent(txtAreas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
                                    .addComponent(cbTipo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 702, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(cbMesInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(ycAnoInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cbMesFin)
                    .addComponent(ycAnoFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(txtAreas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtPersonas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3)
                .addContainerGap(36, Short.MAX_VALUE))
        );

        contenedor.addTab("nuevo", jPanel3);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Informe de capacitación (año anterior)");

        anterior.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(anterior);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 709, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                .addContainerGap())
        );

        contenedor.addTab("anterior", jPanel2);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Plan de capacitación (año en curso)");

        actual.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane3.setViewportView(actual);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 709, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                .addContainerGap())
        );

        contenedor.addTab("actual", jPanel4);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel20.setText("Órgano regulador:");

        txtOrgano.setUpperCase(true);
        txtOrgano.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtOrgano.setPlaceHolderText("Ej. 01-002");

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel21.setText("Clave sujeto obligado:");

        txtSujeto.setUpperCase(true);
        txtSujeto.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtSujeto.setPlaceHolderText("Ej. 89-123");

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel22.setText("Período informado:");

        ycPeriodo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtOrgano, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(ycPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 493, Short.MAX_VALUE))
                            .addComponent(txtSujeto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(txtOrgano, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(txtSujeto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ycPeriodo, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(439, Short.MAX_VALUE))
        );

        contenedor.addTab("generales", jPanel6);

        jSplitPane1.setRightComponent(contenedor);

        jPanel1.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/exportar.png"))); // NOI18N
        jButton1.setText("Exportar a archivo icc");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setMaximumSize(new java.awt.Dimension(120, 41));
        jButton1.setOpaque(false);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/informacion.png"))); // NOI18N
        jButton4.setText("Acerca de...");
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setMaximumSize(new java.awt.Dimension(120, 41));
        jButton4.setOpaque(false);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton4);
        jToolBar1.add(jSeparator1);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar.png"))); // NOI18N
        jButton2.setText("Cerrar");
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setMaximumSize(new java.awt.Dimension(120, 41));
        jButton2.setOpaque(false);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton2);

        jPanel1.add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 886, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnteriorActionPerformed
        btnAnteriorHandler();
    }//GEN-LAST:event_btnAnteriorActionPerformed

    private void btnActualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualActionPerformed
        btnActualHandler();
    }//GEN-LAST:event_btnActualActionPerformed

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        btnAgregarHandler();
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        agregar();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void btnGeneralesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGeneralesActionPerformed
        btnGeneralesHandler();
    }//GEN-LAST:event_btnGeneralesActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        exportar();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked
        agregarArea();
    }//GEN-LAST:event_jLabel10MouseClicked

    private void txtAreasKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAreasKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            agregarArea();
        }
    }//GEN-LAST:event_txtAreasKeyReleased

    private void liAreasKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_liAreasKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            eliminarArea();
        }
    }//GEN-LAST:event_liAreasKeyReleased

    private void jLabel11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel11MouseClicked
        eliminarArea();
    }//GEN-LAST:event_jLabel11MouseClicked

    private void rbAnteriorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbAnteriorItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            controlOpciones(Principal.ANTERIOR);
        }
    }//GEN-LAST:event_rbAnteriorItemStateChanged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        new AcercaDe();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void rbActualItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbActualItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            controlOpciones(Principal.ACTUAL);
        }
    }//GEN-LAST:event_rbActualItemStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Principal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.aguilar.swinglib.swing.fl.FlTable actual;
    private org.aguilar.swinglib.swing.fl.FlTable anterior;
    private javax.swing.ButtonGroup bgTipo;
    private javax.swing.JButton btnActual;
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnAnterior;
    private javax.swing.JButton btnGenerales;
    private javax.swing.JComboBox<String> cbMesFin;
    private javax.swing.JComboBox<String> cbMesInicio;
    private org.aguilar.swinglib.swing.fl.FlComboBox cbTipo;
    private javax.swing.JTabbedPane contenedor;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JList<String> liAreas;
    private javax.swing.JRadioButton rbActual;
    private javax.swing.JRadioButton rbAnterior;
    private org.aguilar.swinglib.swing.fl.FlStringField txtAreas;
    private org.aguilar.swinglib.swing.fl.FlStringField txtDocumento;
    private org.aguilar.swinglib.swing.fl.FlStringField txtNombre;
    private org.aguilar.swinglib.swing.fl.FlStringField txtOrgano;
    private org.aguilar.swinglib.swing.fl.FlStringField txtPersonas;
    private org.aguilar.swinglib.swing.fl.FlStringField txtSujeto;
    private com.toedter.calendar.JYearChooser ycAnoFin;
    private com.toedter.calendar.JYearChooser ycAnoInicio;
    private com.toedter.calendar.JYearChooser ycPeriodo;
    // End of variables declaration//GEN-END:variables

}
