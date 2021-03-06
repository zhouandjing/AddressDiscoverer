/**
 * Part of the AddressDiscoverer project.
 */
package org.norvelle.addressdiscoverer.gui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.norvelle.addressdiscoverer.AddressDiscoverer;
import org.norvelle.addressdiscoverer.gui.action.AllIndividualExportAction;
import org.norvelle.addressdiscoverer.gui.action.NewIndividualExportAction;
import org.norvelle.utils.Utils;

/**
 * Creates and manages the main MDI window of the GUI, with its menu and managed
 * windows.
 * 
 * @author Erik Norvelle <erik.norvelle@cyberlogos.co>
 */


public class MainWindow extends javax.swing.JFrame {

    private final AddressDiscoverer application;
    private final GUIManagementPane threePane;
    
    /**
     * Creates new form MainWindow
     * @param application
     */
    public MainWindow(AddressDiscoverer application) {
        this.application = application;
        initComponents();
        threePane = new GUIManagementPane(this, application);
        this.jMainPanel.setLayout(new BorderLayout());
        Toolkit tk = Toolkit.getDefaultToolkit(); 
        this.jMainPanel.add(threePane, BorderLayout.CENTER);
        int xSize = (int) (tk.getScreenSize().getWidth() * 0.9);  
        int ySize = (int) (tk.getScreenSize().getHeight() * 0.9);  
        this.setSize(xSize,ySize);

        // Set the program icon
        List<Image> imagesList = new ArrayList<>();
        Image smallImage = Toolkit.getDefaultToolkit().getImage(
            getClass().getResource("/org/norvelle/addressdiscoverer/resources/icon_small.png"));
        Image medImage = Toolkit.getDefaultToolkit().getImage(
            getClass().getResource("/org/norvelle/addressdiscoverer/resources/icon_med.png"));
        Image largeImage = Toolkit.getDefaultToolkit().getImage(
            getClass().getResource("/org/norvelle/addressdiscoverer/resources/icon_large.png"));
        imagesList.add(smallImage);
        imagesList.add(medImage);
        imagesList.add(largeImage);
        this.setIconImages(imagesList);
        AddressDiscoverer.application.statusChanged();
    }
    
    /**
     * A single-access point for reporting exceptions to the user.
     * 
     * @param message
     */
    public void reportException(String message) {
        JOptionPane.showMessageDialog(this,
            Utils.wordWrapString(message, 50),
            "Program error", JOptionPane.ERROR_MESSAGE);
    }
    
    public void updateStatus(String message) {
        this.jStatusLabel.setText(message);
    }
    
    public void refreshIndividualList() throws SQLException {
        this.threePane.refreshIndividualList();
        AddressDiscoverer.application.statusChanged();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSaveFileChooser = new javax.swing.JFileChooser();
        jMainPanel = new javax.swing.JPanel();
        jStatusPanel = new javax.swing.JPanel();
        jStatusLabel = new javax.swing.JLabel();
        jMainMenuBar = new javax.swing.JMenuBar();
        jFileMenu = new javax.swing.JMenu();
        jExportNewMenuItem = new javax.swing.JMenuItem();
        jExportAllMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jDatabaseToolsMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jExitMenuItem = new javax.swing.JMenuItem();
        jHelpMenu = new javax.swing.JMenu();
        jAboutMenu = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        javax.swing.GroupLayout jMainPanelLayout = new javax.swing.GroupLayout(jMainPanel);
        jMainPanel.setLayout(jMainPanelLayout);
        jMainPanelLayout.setHorizontalGroup(
            jMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jMainPanelLayout.setVerticalGroup(
            jMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 562, Short.MAX_VALUE)
        );

        jStatusPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jStatusLabel.setText("Status:");

        javax.swing.GroupLayout jStatusPanelLayout = new javax.swing.GroupLayout(jStatusPanel);
        jStatusPanel.setLayout(jStatusPanelLayout);
        jStatusPanelLayout.setHorizontalGroup(
            jStatusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jStatusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 975, Short.MAX_VALUE)
        );
        jStatusPanelLayout.setVerticalGroup(
            jStatusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jStatusLabel)
        );

        jFileMenu.setText("File");

        jExportNewMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jExportNewMenuItem.setText("Export New Individuals");
        jExportNewMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jExportNewMenuItemActionPerformed(evt);
            }
        });
        jFileMenu.add(jExportNewMenuItem);

        jExportAllMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        jExportAllMenuItem.setText("Export All Individuals");
        jExportAllMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jExportAllMenuItemActionPerformed(evt);
            }
        });
        jFileMenu.add(jExportAllMenuItem);
        jFileMenu.add(jSeparator2);

        jDatabaseToolsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        jDatabaseToolsMenuItem.setText("Database Tools");
        jDatabaseToolsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDatabaseToolsMenuItemActionPerformed(evt);
            }
        });
        jFileMenu.add(jDatabaseToolsMenuItem);
        jFileMenu.add(jSeparator1);

        jExitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        jExitMenuItem.setText("Exit");
        jExitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jExitMenuItemActionPerformed(evt);
            }
        });
        jFileMenu.add(jExitMenuItem);

        jMainMenuBar.add(jFileMenu);

        jHelpMenu.setText("Help");

        jAboutMenu.setText("About AddressDiscoverer");
        jHelpMenu.add(jAboutMenu);

        jMainMenuBar.add(jHelpMenu);

        setJMenuBar(jMainMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jStatusPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jMainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jMainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jStatusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jExitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jExitMenuItemActionPerformed
        this.application.shutdown();
    }//GEN-LAST:event_jExitMenuItemActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        this.application.shutdown();
    }//GEN-LAST:event_formWindowClosing

    private void jExportAllMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jExportAllMenuItemActionPerformed
        int returnVal = this.jSaveFileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = jSaveFileChooser.getSelectedFile();
            if (file.exists()) {
                int reply = JOptionPane.showConfirmDialog(null, "File exists. Overwrite?", "Confirm overwrite", 
                        JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.NO_OPTION) 
                    return;
            }
            ExportProgressDialog form = new ExportProgressDialog(null, file);
            form.setLocationRelativeTo(null);
            form.setVisible(true);
            form.doExport();
        }
        
    }//GEN-LAST:event_jExportAllMenuItemActionPerformed

    private void jExportNewMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jExportNewMenuItemActionPerformed
        int returnVal = this.jSaveFileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = jSaveFileChooser.getSelectedFile();
            if (file.exists()) {
                int reply = JOptionPane.showConfirmDialog(null, "File exists. Overwrite?", "Confirm overwrite", 
                        JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.NO_OPTION) 
                    return;
            }
            try {
                NewIndividualExportAction exporter = 
                    new NewIndividualExportAction(file);
                exporter.export();
            } catch (IOException | SQLException ex) {
                AddressDiscoverer.reportException(ex);
            } 
        }
        
    }//GEN-LAST:event_jExportNewMenuItemActionPerformed

    private void jDatabaseToolsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDatabaseToolsMenuItemActionPerformed
        DatabaseToolsForm form = new DatabaseToolsForm(this);
        form.setLocationRelativeTo(null);
        form.setVisible(true);
    }//GEN-LAST:event_jDatabaseToolsMenuItemActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem jAboutMenu;
    private javax.swing.JMenuItem jDatabaseToolsMenuItem;
    private javax.swing.JMenuItem jExitMenuItem;
    private javax.swing.JMenuItem jExportAllMenuItem;
    private javax.swing.JMenuItem jExportNewMenuItem;
    private javax.swing.JMenu jFileMenu;
    private javax.swing.JMenu jHelpMenu;
    private javax.swing.JMenuBar jMainMenuBar;
    private javax.swing.JPanel jMainPanel;
    private javax.swing.JFileChooser jSaveFileChooser;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JLabel jStatusLabel;
    private javax.swing.JPanel jStatusPanel;
    // End of variables declaration//GEN-END:variables
}
