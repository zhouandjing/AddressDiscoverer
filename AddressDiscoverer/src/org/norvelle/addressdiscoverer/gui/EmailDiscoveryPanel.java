/**
 * Part of the AddressDiscoverer project, licensed under the GPL v.3 license.
 * This project provides intelligence for discovering email addresses in
 * specified web pages, associating them with a given institution and department
 * and address type.
 *
 * This project is licensed under the GPL v.3. Your rights to copy and modify
 * are regulated by the conditions specified in that license, available at
 * http://www.gnu.org/licenses/gpl-3.0.html
 */
package org.norvelle.addressdiscoverer.gui;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.apache.commons.io.IOUtils;
import org.norvelle.addressdiscoverer.AddressDiscoverer;
import org.norvelle.addressdiscoverer.model.Department;
import org.xml.sax.SAXException;

/**
 *
 * @author Erik Norvelle <erik.norvelle@cyberlogos.co>
 */
public class EmailDiscoveryPanel extends javax.swing.JPanel {

    private final GUIManagementPane parent;
    private Department currentDepartment;
    
    /**
     * Creates new form EmailDiscoveryPanel
     * 
     * @param parent The GUIManagementPane that acts as our controller
     */
    public EmailDiscoveryPanel(GUIManagementPane parent) {
        this.parent = parent;
        initComponents();
    }
    
    public void setDepartment(Department department) throws IOException, SAXException {
        this.currentDepartment = department;
        if (department == null) {
            this.jWebAddressField.setText("");
            this.jWebAddressField.setEnabled(false);
            this.jBytesReceivedLabel.setEnabled(false);
            this.jRetrieveHTMLButton.setEnabled(false);
            this.jHTMLPanel.setEnabled(false);
        }
        else {
            this.setEnabled(true);
            String url = department.getWebAddress();
            this.jWebAddressField.setText(url);
            this.jWebAddressField.setEnabled(true);
            this.jBytesReceivedLabel.setEnabled(true);
            this.jRetrieveHTMLButton.setEnabled(true);
            this.jHTMLPanel.setEnabled(true);
            this.updateWebPageContents();
        }
    }
    
    public void updateWebPageContents() {
        this.jRetrieveHTMLButton.setEnabled(false);
        final String myURI = this.jWebAddressField.getText();
        if (!myURI.isEmpty())
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(myURI);
                        URLConnection connection = url.openConnection();
                        InputStream in = connection.getInputStream();
                        StringWriter writer = new StringWriter();
                        IOUtils.copy(in, writer, Charset.forName("UTF-8"));
                        String html = writer.toString();
                        jBytesReceivedLabel.setText(Integer.toString(html.length()));
                        jRetrieveHTMLButton.setEnabled(true);
                        setHTMLPanelContents(html);
                    } catch (IOException ex) {
                        AddressDiscoverer.reportException(ex);
                    }
                }
            });
    }
    
    private void setHTMLPanelContents(String html) {
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jHTMLRenderPanel = new javax.swing.JTabbedPane();
        jEmailSourcePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jWebAddressField = new javax.swing.JTextField();
        jRetrieveHTMLButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jBytesReceivedLabel = new javax.swing.JLabel();
        jEmailDisplayPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPageContentPanel = new javax.swing.JPanel();
        jHTMLPanel = new org.lobobrowser.html.gui.HtmlPanel();

        jLabel1.setText("Web address:");

        jWebAddressField.setEnabled(false);
        jWebAddressField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jWebAddressFieldActionPerformed(evt);
            }
        });

        jRetrieveHTMLButton.setText("Retrieve HTML");
        jRetrieveHTMLButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRetrieveHTMLButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Bytes received:");

        jBytesReceivedLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jBytesReceivedLabel.setText("0");
        jBytesReceivedLabel.setEnabled(false);

        javax.swing.GroupLayout jEmailSourcePanelLayout = new javax.swing.GroupLayout(jEmailSourcePanel);
        jEmailSourcePanel.setLayout(jEmailSourcePanelLayout);
        jEmailSourcePanelLayout.setHorizontalGroup(
            jEmailSourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jEmailSourcePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jEmailSourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jEmailSourcePanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jWebAddressField))
                    .addGroup(jEmailSourcePanelLayout.createSequentialGroup()
                        .addComponent(jRetrieveHTMLButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBytesReceivedLabel)
                        .addGap(0, 308, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jEmailSourcePanelLayout.setVerticalGroup(
            jEmailSourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jEmailSourcePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jEmailSourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jWebAddressField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jEmailSourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRetrieveHTMLButton)
                    .addComponent(jLabel2)
                    .addComponent(jBytesReceivedLabel))
                .addContainerGap(439, Short.MAX_VALUE))
        );

        jHTMLRenderPanel.addTab("Source Page", jEmailSourcePanel);

        javax.swing.GroupLayout jEmailDisplayPanelLayout = new javax.swing.GroupLayout(jEmailDisplayPanel);
        jEmailDisplayPanel.setLayout(jEmailDisplayPanelLayout);
        jEmailDisplayPanelLayout.setHorizontalGroup(
            jEmailDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 561, Short.MAX_VALUE)
        );
        jEmailDisplayPanelLayout.setVerticalGroup(
            jEmailDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 506, Short.MAX_VALUE)
        );

        jHTMLRenderPanel.addTab("Discovered Emails", jEmailDisplayPanel);

        jPageContentPanel.setLayout(new java.awt.BorderLayout());
        jPageContentPanel.add(jHTMLPanel, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPageContentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPageContentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jHTMLRenderPanel.addTab("Page Content", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jHTMLRenderPanel)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jHTMLRenderPanel)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jRetrieveHTMLButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRetrieveHTMLButtonActionPerformed
        if (this.jWebAddressField.getText().isEmpty())
            return;
        this.updateWebPageContents();
    }//GEN-LAST:event_jRetrieveHTMLButtonActionPerformed

    private void jWebAddressFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jWebAddressFieldActionPerformed
        this.currentDepartment.setWebAddress(this.jWebAddressField.getText());
        try {
            Department.update(this.currentDepartment);
        } catch (SQLException ex) {
            AddressDiscoverer.reportException(ex);
        }
    }//GEN-LAST:event_jWebAddressFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jBytesReceivedLabel;
    private javax.swing.JPanel jEmailDisplayPanel;
    private javax.swing.JPanel jEmailSourcePanel;
    private org.lobobrowser.html.gui.HtmlPanel jHTMLPanel;
    private javax.swing.JTabbedPane jHTMLRenderPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPageContentPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jRetrieveHTMLButton;
    private javax.swing.JTextField jWebAddressField;
    // End of variables declaration//GEN-END:variables
}
