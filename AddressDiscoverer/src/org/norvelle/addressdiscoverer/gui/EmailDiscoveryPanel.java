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

import org.norvelle.addressdiscoverer.gui.threading.StatusReporter;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import org.xml.sax.SAXException;
import org.norvelle.addressdiscoverer.AddressDiscoverer;
import org.norvelle.addressdiscoverer.gui.action.IndividualForDepartmentExportAction;
import org.norvelle.addressdiscoverer.gui.threading.ExtractIndividualsFromFileWorker;
import org.norvelle.addressdiscoverer.gui.threading.ExtractIndividualsFromUrlWorker;
import org.norvelle.addressdiscoverer.model.Department;
import org.norvelle.addressdiscoverer.model.Individual;
import org.norvelle.addressdiscoverer.model.UnparsableIndividual;
import org.norvelle.utils.Utils;

/**
 *
 * @author Erik Norvelle <erik.norvelle@cyberlogos.co>
 */
public class EmailDiscoveryPanel extends javax.swing.JPanel {

    // A logger instance
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME); 

    private final GUIManagementPane parent;
    private Department currentDepartment;
    private List<Individual> individuals;
    
    /**
     * Creates new form EmailDiscoveryPanel
     * 
     * @param parent The GUIManagementPane that acts as our controller
     */
    public EmailDiscoveryPanel(GUIManagementPane parent) {
        this.parent = parent;
        initComponents();
        this.jWebAddressField.getDocument().addDocumentListener(
                new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent de) {
                updateDepartmentWebAddress();
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                updateDepartmentWebAddress();
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
                updateDepartmentWebAddress();
            }
        });
        
        // Add a mouse listener for double clicks on the table
        final EmailDiscoveryPanel myThis = this;
        this.jAddressesFoundTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                JTable table =(JTable) me.getSource();
                Point p = me.getPoint();
                int row = table.rowAtPoint(p);
                if (me.getClickCount() == 2) {
                    Individual individual = individuals.get(row);
                    if (individual.getClass().equals(UnparsableIndividual.class)) {
                        CreateIndividualFromUnparsedTextDialog dialog = 
                                new CreateIndividualFromUnparsedTextDialog(myThis, 
                                        (UnparsableIndividual) individual, currentDepartment, true);
                        dialog.setVisible(true);
                    }
                    else {
                        EditIndividualDialog dialog = new EditIndividualDialog(myThis, individual, true);
                        dialog.setLocationRelativeTo(null);
                        dialog.setVisible(true);
                    }
                }
            }
        });

    }
    
    /**
     * If the user selects a department in the GUI, this pane should be activated
     * so that he user can adjust its properties and retrieve HTML for its personnel.
     * 
     * @param department The Department object selected by the user.
     * @throws IOException
     * @throws SAXException 
     */
    public void setDepartment(Department department) throws IOException, SAXException {
        this.currentDepartment = department;
        this.jStageNameLabel.setText("Idle");
        if (department == null) {
            this.jWebAddressField.setText("");
            this.jWebAddressField.setEnabled(false);
            this.jRetrieveHTMLButton.setEnabled(false);
            this.jBytesReceivedLabel.setEnabled(false);
            this.jBytesReceivedLabel.setText("0");
            this.individuals = new ArrayList<>();
            this.populateResultsTable(this.individuals);
            this.jSaveResultsButton.setEnabled(false);
        }
        else {
            String url = department.getWebAddress();
            this.jWebAddressField.setText(url);
            this.jWebAddressField.setCaretPosition(0);
            this.jWebAddressField.setEnabled(true);
            this.jBytesReceivedLabel.setEnabled(true);
            if (this.jWebAddressField.getText().isEmpty()) 
                this.jRetrieveHTMLButton.setEnabled(false);
            else
                this.jRetrieveHTMLButton.setEnabled(true);
            String html = department.getHtml();
            try {
                this.individuals = Individual.getIndividualsForDepartment(department);
                this.populateResultsTable(this.individuals);
                this.jSaveResultsButton.setEnabled(true);
            } catch (SQLException ex) {
                AddressDiscoverer.reportException(ex);
            }
            
        }
    }
    
    /**
     * Asynchronously fetch the HTML for the specified webpage and update our
     * HTML rendering pane with that content.
     */
    private void fetchAndParseHtml() {
        this.jRetrieveHTMLButton.setEnabled(false);
        this.jSaveResultsButton.setEnabled(false);
        this.jStageNameLabel.setEnabled(true);
        final String myURI = this.jWebAddressField.getText();
        this.populateResultsTable(null);
        if (!myURI.isEmpty()) {
            File file = new File(myURI);
            ExtractIndividualsFromFileWorker worker;
            this.jDebugOutputTextArea.setText("");
            boolean useUnstructuredPageParser = this.jUseUnstructuredParserCheckbox.isSelected();
            
            // If the user has specified a local file, we use that to fetch HTML
            if (file.exists()) {
                worker = new ExtractIndividualsFromFileWorker(this, file, 
                        this.currentDepartment, useUnstructuredPageParser);
            } // if file.exists()
            
            // Otherwise we fetch the HTML from the website via HTTP
            else {
                try {
                    worker = new ExtractIndividualsFromUrlWorker(this, myURI, 
                            currentDepartment, useUnstructuredPageParser);
                } // else
                catch (URISyntaxException | IOException ex) {
                    AddressDiscoverer.reportException(ex);
                    return;
                }                
            }

            worker.execute();
        } // if (!myURI
    }
    
    public void addNewIndividual(Individual individual, UnparsableIndividual old) {
        this.individuals.remove(old);
        this.individuals.add(individual);
        this.populateResultsTable(this.individuals);
    }
    
    public void refreshResultsTable() throws SQLException {
        this.individuals = Individual.getIndividualsForDepartment(this.currentDepartment);
        this.populateResultsTable(this.individuals);
    }
    
    public void populateResultsTable(List<Individual> individuals) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Title");
        model.addColumn("First");
        model.addColumn("Last");
        model.addColumn("Email");
        model.addColumn("Role");
        model.addColumn("Other");
        if (individuals == null) {
            model.setRowCount(0);
        }
        else {
            model.setRowCount(individuals.size());
            int rowCount = 0;
            for (Individual i : individuals) {
                if (!i.getClass().equals(UnparsableIndividual.class)) {
                    model.setValueAt(i.getTitle(), rowCount, 0);
                    model.setValueAt(i.getFirstName(), rowCount, 1);
                    model.setValueAt(Utils.chop(i.getLastName(), 20), rowCount, 2);
                    model.setValueAt(i.getEmail(), rowCount, 3);
                    model.setValueAt(Utils.chop(i.getRole(), 20), rowCount, 4);
                    model.setValueAt(Utils.chop(i.getUnprocessed(), 20), rowCount, 5);
                }
                else {
                    model.setValueAt("Edit to fix", rowCount, 0);
                    model.setValueAt("NULL", rowCount, 1);
                    model.setValueAt("NULL", rowCount, 2);
                    model.setValueAt("NULL", rowCount, 3);
                    model.setValueAt("NULL", rowCount, 4);
                    model.setValueAt("NULL", rowCount, 5);
                }
                rowCount ++;
            }
        }
        this.jAddressesFoundTable.setModel(model);
        this.individuals = individuals;
        AddressDiscoverer.application.statusChanged();
    }
    
    /**
     * Gets called when the user changes the value for the web address field.
     */
    private void updateDepartmentWebAddress() {
        String newAddress = this.jWebAddressField.getText();
        if (newAddress == null || newAddress.isEmpty()) {
            this.jRetrieveHTMLButton.setEnabled(false);
            return;
        }
        this.jRetrieveHTMLButton.setEnabled(true);
        if (this.currentDepartment != null && 
                !newAddress.equals(this.currentDepartment.getWebAddress())) 
        {
            this.currentDepartment.setWebAddress(newAddress);
            try {
                Department.update(this.currentDepartment);
            } catch (SQLException ex) {
                AddressDiscoverer.reportException(ex);
            }        
        }
    }

    public void notifyParsingFinished() {
        jRetrieveHTMLButton.setEnabled(true);
        this.jSaveResultsButton.setEnabled(true);
        this.jStageNameLabel.setEnabled(false);
        this.jStageNameLabel.setText("Idle");
    }
    
    public void notifyIndividualDeleted(Individual i) {
        this.individuals.remove(i);
    }
    
    public void notifyParsingStage(StatusReporter status) {
        this.jStageNameLabel.setText(status.getLabel());
    }
    
    public void addToOutput(String line) {
        this.jDebugOutputTextArea.append(line + "\n");
    }

    public JLabel getjStageNameLabel() {
        return jStageNameLabel;
    }

    public JLabel getjBytesReceivedLabel() {
        return jBytesReceivedLabel;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jOpenFileChooser = new javax.swing.JFileChooser();
        jSaveFileChooser = new javax.swing.JFileChooser();
        jHTMLRenderPanel = new javax.swing.JTabbedPane();
        jEmailSourceTab = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jWebAddressField = new javax.swing.JTextField();
        jRetrieveHTMLButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jBytesReceivedLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jSaveResultsButton = new javax.swing.JButton();
        jStageNameLabel = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jAddressesFoundTable = new javax.swing.JTable();
        jSelectFileButton = new javax.swing.JButton();
        jUseUnstructuredParserCheckbox = new javax.swing.JCheckBox();
        jPageContentTab = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jDebugOutputTextArea = new javax.swing.JTextArea();

        jLabel1.setText("Web address:");

        jWebAddressField.setEnabled(false);
        jWebAddressField.setMaximumSize(new java.awt.Dimension(6, 22));

        jRetrieveHTMLButton.setText("Retrieve HTML");
        jRetrieveHTMLButton.setEnabled(false);
        jRetrieveHTMLButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRetrieveHTMLButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Bytes received:");

        jBytesReceivedLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jBytesReceivedLabel.setText("0");
        jBytesReceivedLabel.setEnabled(false);

        jLabel3.setText("Addresses found:");

        jLabel4.setText("Parsing progress:");

        jSaveResultsButton.setText("Save Results");
        jSaveResultsButton.setEnabled(false);
        jSaveResultsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSaveResultsButtonActionPerformed(evt);
            }
        });

        jStageNameLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jStageNameLabel.setText("Finding Emails in Links");
        jStageNameLabel.setEnabled(false);

        jAddressesFoundTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "First Name", "Last Name", "Email", "Title"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jAddressesFoundTable);

        jSelectFileButton.setText("Select File");
        jSelectFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSelectFileButtonActionPerformed(evt);
            }
        });

        jUseUnstructuredParserCheckbox.setText("Use unstructured page parser");

        javax.swing.GroupLayout jEmailSourceTabLayout = new javax.swing.GroupLayout(jEmailSourceTab);
        jEmailSourceTab.setLayout(jEmailSourceTabLayout);
        jEmailSourceTabLayout.setHorizontalGroup(
            jEmailSourceTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jEmailSourceTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jEmailSourceTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jEmailSourceTabLayout.createSequentialGroup()
                        .addGroup(jEmailSourceTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jEmailSourceTabLayout.createSequentialGroup()
                                .addComponent(jRetrieveHTMLButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jBytesReceivedLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 147, Short.MAX_VALUE)
                                .addComponent(jUseUnstructuredParserCheckbox))
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addComponent(jSaveResultsButton))
                    .addGroup(jEmailSourceTabLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jStageNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jEmailSourceTabLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jWebAddressField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSelectFileButton)))
                .addContainerGap())
        );
        jEmailSourceTabLayout.setVerticalGroup(
            jEmailSourceTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jEmailSourceTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jEmailSourceTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jWebAddressField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSelectFileButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jEmailSourceTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRetrieveHTMLButton)
                    .addComponent(jLabel2)
                    .addComponent(jBytesReceivedLabel)
                    .addComponent(jSaveResultsButton)
                    .addComponent(jUseUnstructuredParserCheckbox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jEmailSourceTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jStageNameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                .addContainerGap())
        );

        jHTMLRenderPanel.addTab("Source Page", jEmailSourceTab);

        jDebugOutputTextArea.setColumns(20);
        jDebugOutputTextArea.setRows(5);
        jScrollPane4.setViewportView(jDebugOutputTextArea);

        javax.swing.GroupLayout jPageContentTabLayout = new javax.swing.GroupLayout(jPageContentTab);
        jPageContentTab.setLayout(jPageContentTabLayout);
        jPageContentTabLayout.setHorizontalGroup(
            jPageContentTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 769, Short.MAX_VALUE)
        );
        jPageContentTabLayout.setVerticalGroup(
            jPageContentTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)
        );

        jHTMLRenderPanel.addTab("Page Content", jPageContentTab);

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
        if (!this.jWebAddressField.getText().isEmpty()) {
            this.jHTMLRenderPanel.setEnabledAt(1, true);
            this.fetchAndParseHtml();
        }
    }//GEN-LAST:event_jRetrieveHTMLButtonActionPerformed

    private void jSaveResultsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSaveResultsButtonActionPerformed
        int returnVal = this.jSaveFileChooser.showOpenDialog(this.parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = jSaveFileChooser.getSelectedFile();
            try {
                IndividualForDepartmentExportAction exporter = 
                        new IndividualForDepartmentExportAction(file, this.currentDepartment);
                exporter.export();
            } catch (IOException | SQLException ex) {
                AddressDiscoverer.reportException(ex);
            } 
        }
        
    }//GEN-LAST:event_jSaveResultsButtonActionPerformed

    private void jSelectFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSelectFileButtonActionPerformed
        int returnVal = this.jOpenFileChooser.showOpenDialog(this.parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = jOpenFileChooser.getSelectedFile();
            this.jWebAddressField.setText(file.getAbsolutePath());
            this.jWebAddressField.setCaretPosition(0);
            this.updateDepartmentWebAddress();
        }
    }//GEN-LAST:event_jSelectFileButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable jAddressesFoundTable;
    private javax.swing.JLabel jBytesReceivedLabel;
    private javax.swing.JTextArea jDebugOutputTextArea;
    private javax.swing.JPanel jEmailSourceTab;
    private javax.swing.JTabbedPane jHTMLRenderPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JFileChooser jOpenFileChooser;
    private javax.swing.JPanel jPageContentTab;
    private javax.swing.JButton jRetrieveHTMLButton;
    private javax.swing.JFileChooser jSaveFileChooser;
    private javax.swing.JButton jSaveResultsButton;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JButton jSelectFileButton;
    private javax.swing.JLabel jStageNameLabel;
    private javax.swing.JCheckBox jUseUnstructuredParserCheckbox;
    private javax.swing.JTextField jWebAddressField;
    // End of variables declaration//GEN-END:variables

}
