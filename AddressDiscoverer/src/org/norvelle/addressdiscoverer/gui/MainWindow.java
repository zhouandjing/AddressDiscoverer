/**
 * Part of the AddressDiscoverer project.
 */
package org.norvelle.addressdiscoverer.gui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.norvelle.addressdiscoverer.AddressDiscoverer;
import org.norvelle.addressdiscoverer.Utils;
import org.norvelle.addressdiscoverer.exceptions.OrmObjectNotConfiguredException;

/**
 * Creates and manages the main MDI window of the GUI, with its menu and managed
 * windows.
 * 
 * @author Erik Norvelle <erik.norvelle@cyberlogos.co>
 */


public class MainWindow extends javax.swing.JFrame {

    private final AddressDiscoverer application;
    
    /**
     * Creates new form MainWindow
     * @param application
     * @throws org.norvelle.addressdiscoverer.exceptions.OrmObjectNotConfiguredException
     */
    public MainWindow(AddressDiscoverer application) throws OrmObjectNotConfiguredException {
        this.application = application;
        initComponents();
        ThreeWaySplitPane threePane = new ThreeWaySplitPane(this, application);
        this.setLayout(new BorderLayout());
        Toolkit tk = Toolkit.getDefaultToolkit(); 
        this.getContentPane().add(threePane, BorderLayout.CENTER);
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMainMenuBar = new javax.swing.JMenuBar();
        jFileMenu = new javax.swing.JMenu();
        jExitMenuItem = new javax.swing.JMenuItem();
        jHelpMenu = new javax.swing.JMenu();
        jAboutMenu = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jFileMenu.setText("File");

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
            .addGap(0, 979, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 589, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jExitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jExitMenuItemActionPerformed
        this.application.shutdown();
    }//GEN-LAST:event_jExitMenuItemActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        this.application.shutdown();
    }//GEN-LAST:event_formWindowClosing


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem jAboutMenu;
    private javax.swing.JMenuItem jExitMenuItem;
    private javax.swing.JMenu jFileMenu;
    private javax.swing.JMenu jHelpMenu;
    private javax.swing.JMenuBar jMainMenuBar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
