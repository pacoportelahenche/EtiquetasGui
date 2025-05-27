/*
 * Copyright (C) 2017 Francisco Portela Henche
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.pacoportela.elco;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * Esta clase crea una interfaz para imprimir etiquetas que tengan de una a 
 * cinco lineas de texto. Permite modificar el tamaño de la fuente, la rotación
 * del texto, la expansión horizontal y vertical del texto y la inversión de
 * los colores de frente y fondo. Utiliza el lenguaje EPL propio de las 
 * impresoras Zebra más antiguas.
 *
 * @author Francisco Portela Henche (21/03/17)
 */
public class EtiquetasGUI extends javax.swing.JFrame {
    ImageIcon icono;
    String NOMBRE_IMPRESORA = "ZDesigner LP 2844";

    /**
     * Constructor. Crea una nueva interfaz EtiquetasGUI.
     */
    public EtiquetasGUI() {
        URL url = this.getClass().getResource("recursos/zebra.jpeg");
        icono = new ImageIcon(url);
        initComponents();
        setBotonPorDefecto();
    }
    
    private void setBotonPorDefecto(){
        this.getRootPane().setDefaultButton(this.botonImprimir);
    }

    /**
     * Método que crea la etiqueta que vamos a imprimir en la impresora Zebra.
     */
    private void hacerEtiqueta() {
        // posicion 'x' cartesiana de inicio.
        int posx = 50;
        // posicion 'y' cartesiana de inicio.
        int posy = 5;
        // lista que contiene los datos de los campos de texto.
        List<String> listaDatos;
        listaDatos = getTextos();
        // obtenemos el tamaño de la fuente.
        String fuente = getFuente();
        // obtenemos la rotacion.
        String rotacion = getRotacion();
        // obtenemos la expansión horizontal del texto.
        String xhor = getExpansionHorizontal();
        // obtenemos la expansión vertical del texto.
        String xver = getExpansionVertical();
        // obtenemos el formato del texto.
        String formato = getFormatoTexto();
        // creamos la etiqueta con todos los datos.
        StringBuilder eti = new StringBuilder();
        /* El formato de la etiqueta en lenguaje EPL es el siguiente:
        - Primero se abre la etiqueta con 'N' que limpia el buffer de la imagen
          donde se va a dibujar la etiqueta y salto de linea.
        - Luego se pone la instrucción a ejecutar, en nuestro caso es
          A (texto ASCII).
        - Después viene la posición x e y.
        - A continuación la rotación del texto.
        - Después el tamaño de la fuente.
        - Luego la expansión horizontal y vertical del texto.
        - Después el formato de texto (normal ó invertido).
        - A continuación el texto entrecomillado y un salto de linea.
        - Por último se cierra la etiqueta con 'P' instrucción imprimir,
          el numero de copias y un salto de linea.
         */
        eti.append("N\n");// comando para limpiar el buffer de impresión
        for (String linea : listaDatos) {
            if (linea.length() > 0) {
                eti.append("A")// comando para texto ASCII
                        .append(Integer.toString(posx))// posición x
                        .append(",")
                        .append(Integer.toString(posy))// posición y
                        .append(",")
                        .append(rotacion)// rotación
                        .append(",")
                        .append(fuente)// tamaño fuente
                        .append(",")
                        .append(xhor)// expansión texto horizontal
                        .append(",")
                        .append(xver)// expansión texto vertical
                        .append(",")
                        .append(formato)// formato (normal ó invertido)
                        .append(",")
                        .append("\"")
                        .append(linea)// texto a imprimir entre comillas
                        .append("\"\n");// cierra comillas y salto de linea.
                // aumentamos la posición 'y' en cada nueva linea. 
                posy += 50;
            }
        }
        // recogemos el número de copias que queremos imprimir.
        int copias = Integer.valueOf(this.tfCopias.getText());
        eti.append("P")// comando Imprimir 'P'
                .append(copias)// número de copias
                .append("\n");// salto de linea
        // pasamos el string a una matriz de bytes.
        byte[] matrizBytes = eti.toString().getBytes();
        // imprimimos la etiqueta.
        imprimirEtiqueta(matrizBytes);
    }

    /**
     * Este método obtiene los textos de los campos de texto y los devuelve en
     * una lista.
     *
     * @return una lista con todos los textos de los campos de texto.
     */
    private List<String> getTextos() {
        List<String> listaDatos = new ArrayList<>();
        listaDatos.add(this.tfLinea1.getText());
        listaDatos.add(this.tfLinea2.getText());
        listaDatos.add(this.tfLinea3.getText());
        listaDatos.add(this.tfLinea4.getText());
        listaDatos.add(this.tfLinea5.getText());
        return listaDatos;
    }

    /**
     * Este método devuelve el tamaño de la fuente a utilizar.
     *
     * @return un String con el tamaño de la fuente.
     */
    private String getFuente() {
        String fuente = (String) this.comboFuente.getSelectedItem();
        fuente = String.valueOf(fuente.charAt(0));
        return fuente;
    }

    /**
     * Este método devuelve la rotación del texto. Puede ser '0' sin rotación ó
     * '1' rotación de 90 grados.
     *
     * @return un String con la rotación del texto.
     */
    private String getRotacion() {
        String rotacion = (String) this.comboRotacion.getSelectedItem();
        rotacion = String.valueOf(rotacion.charAt(0));
        return rotacion;
    }

    /**
     * Este método devuelve la expansión horizontal del texto.
     *
     * @return un String con la expansión horizontal del texto.
     */
    private String getExpansionHorizontal() {
        String xhor = (String) this.comboHorizontal.getSelectedItem();
        xhor = String.valueOf(xhor.charAt(0));
        return xhor;
    }

    /**
     * Este método devuelve la rotación vertical del texto.
     *
     * @return un String con la expansión vertical del texto.
     */
    private String getExpansionVertical() {
        String xver = (String) this.comboVertical.getSelectedItem();
        xver = String.valueOf(xver.charAt(0));
        return xver;
    }

    /**
     * Este método devuelve el formato del texto. Puede ser 'N' normal ó 'R'
     * reverse (invertido, colores de fondo y frente cambiados).
     *
     * @return un String con el formato del texto.
     */
    private String getFormatoTexto() {
        String formato = (String) this.comboImagen.getSelectedItem();
        formato = String.valueOf(formato.charAt(0));
        return formato;
    }

    /**
     * Este método comprueba si todos los campos de texto están vacios.
     *
     * @return un boolean que es true si todos los campos de texto están vacios.
     */
    private boolean comprobarCamposTextoVacios() {
        return (this.tfLinea1.getText().isEmpty()
                && this.tfLinea2.getText().isEmpty()
                && this.tfLinea3.getText().isEmpty()
                && this.tfLinea4.getText().isEmpty()
                && this.tfLinea5.getText().isEmpty());
    }

    /**
     * Este método imprime la etiqueta. Obtiene los PrintService (impresoras) de
     * los que dispone el equipo, crea un documento imprimible con los datos que
     * le pasamos (los creados en el método 'hacerEtiqueta') y lo imprime en la
     * impresora Zebra LP2844.
     *
     * @param datos una matriz de bytes con los datos de la etiqueta a imprimir.
     */
    private void imprimirEtiqueta(byte[] datos) {
        if (this.comprobarCamposTextoVacios()) {
            JOptionPane.showMessageDialog(this, "No hay datos para imprimir",
                    "Mensaje", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        PrintService[] services
                = PrintServiceLookup.lookupPrintServices(null, null);
        Doc doc;
        doc = new SimpleDoc(datos, DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
        for (PrintService impresora : services) {
            if (impresora.getName().equalsIgnoreCase(this.NOMBRE_IMPRESORA)) {
                DocPrintJob pj = impresora.createPrintJob();
                try {
                    pj.print(doc, null);
                } catch (PrintException ex) {
                    JOptionPane.showMessageDialog(null, ex.toString(),
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                }
                return;
            }
            
        }
        String mensaje = "No se ha encontrado la impresora "
                        + "Zebra LP2844. ¿Desea salir del programa?";
        if(JOptionPane.showConfirmDialog(this, mensaje,
               "Impresora no encontrada",
               JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
               System.exit(0);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        tfLinea1 = new javax.swing.JTextField();
        tfLinea2 = new javax.swing.JTextField();
        tfLinea3 = new javax.swing.JTextField();
        tfLinea4 = new javax.swing.JTextField();
        tfLinea5 = new javax.swing.JTextField();
        botonCancelar = new javax.swing.JButton();
        botonImprimir = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        comboFuente = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        comboRotacion = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        comboHorizontal = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        comboVertical = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        comboImagen = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        tfCopias = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Imprimir etiquetas Zebra");
        setIconImage(icono.getImage());
        setResizable(false);

        jLabel1.setText("Linea 1");

        jLabel2.setText("Linea 2");

        jLabel3.setText("Linea 3");

        jLabel4.setText("Linea 4");

        jLabel5.setText("Linea 5");

        tfLinea1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        tfLinea2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        tfLinea3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        tfLinea4.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        tfLinea5.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        botonCancelar.setText("Cancelar");
        botonCancelar.setToolTipText("Sale del programa");
        botonCancelar.setFocusable(false);
        botonCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonCancelarActionPerformed(evt);
            }
        });

        botonImprimir.setText("Imprimir");
        botonImprimir.setToolTipText("Imprime la etiqueta");
        botonImprimir.setFocusable(false);
        botonImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonImprimirActionPerformed(evt);
            }
        });

        jLabel6.setText("Tamaño");

        comboFuente.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4" }));
        comboFuente.setSelectedIndex(2);
        comboFuente.setToolTipText("Tamaño de la letra");
        comboFuente.setFocusable(false);

        jLabel7.setText("Rotación");

        comboRotacion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0-No gira", "1-90º" }));
        comboRotacion.setToolTipText("Rotación del texto");
        comboRotacion.setFocusable(false);

        jLabel8.setText("Xhorizontal");

        comboHorizontal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "8" }));
        comboHorizontal.setSelectedIndex(1);
        comboHorizontal.setToolTipText("Expansión horizontal del texto");
        comboHorizontal.setFocusable(false);

        jLabel9.setText("Xvertical");

        comboVertical.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9" }));
        comboVertical.setSelectedIndex(1);
        comboVertical.setToolTipText("Expansión vertical del texto");
        comboVertical.setFocusable(false);

        jLabel10.setText("Imagen");

        comboImagen.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Normal", "Reverse" }));
        comboImagen.setToolTipText("Color frente y fondo");
        comboImagen.setFocusable(false);

        jLabel11.setText("Copias");

        tfCopias.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfCopias.setText("1");
        tfCopias.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfLinea1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(comboFuente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfLinea2, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                        .addComponent(comboRotacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfLinea3, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(comboHorizontal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(botonCancelar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(botonImprimir))
                            .addComponent(tfLinea5, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(comboImagen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(tfCopias, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfLinea4, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(comboVertical, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tfLinea1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(comboFuente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tfLinea2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(comboRotacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tfLinea3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(comboHorizontal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(tfLinea4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(comboVertical, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(tfLinea5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(comboImagen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botonCancelar)
                    .addComponent(botonImprimir)
                    .addComponent(jLabel11)
                    .addComponent(tfCopias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botonImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonImprimirActionPerformed
        hacerEtiqueta();
    }//GEN-LAST:event_botonImprimirActionPerformed

    private void textFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFieldFocusGained
        JTextField tf = (JTextField) evt.getSource();
        tf.selectAll();
    }//GEN-LAST:event_textFieldFocusGained

    private void botonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonCancelarActionPerformed
        System.exit(0);
    }//GEN-LAST:event_botonCancelarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Windows look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException
                | javax.swing.UnsupportedLookAndFeelException ex) {
            JOptionPane.showMessageDialog(null, ex.toString(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        EtiquetasGUI eg = new EtiquetasGUI();
        
        
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int anchoPantalla = (int)d.getWidth();
        int altoPantalla = (int)d.getHeight();
        int anchoAplicacion = eg.getWidth();
        int altoAplicacion = eg.getHeight();
        // centramos la ventana el la pantalla
        eg.setLocation(((anchoPantalla/2)-(anchoAplicacion/2)),
                ((altoPantalla/2)-(altoAplicacion/2)));
        eg.setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonCancelar;
    private javax.swing.JButton botonImprimir;
    private javax.swing.JComboBox<String> comboFuente;
    private javax.swing.JComboBox<String> comboHorizontal;
    private javax.swing.JComboBox<String> comboImagen;
    private javax.swing.JComboBox<String> comboRotacion;
    private javax.swing.JComboBox<String> comboVertical;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField tfCopias;
    private javax.swing.JTextField tfLinea1;
    private javax.swing.JTextField tfLinea2;
    private javax.swing.JTextField tfLinea3;
    private javax.swing.JTextField tfLinea4;
    private javax.swing.JTextField tfLinea5;
    // End of variables declaration//GEN-END:variables
}
