/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package machine.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;
import machine.model.CellEditor;
import machine.model.CellRenderer;
import machine.model.ColumnHeaderRenderer;
import machine.model.RowHeaderRenderer;
import machine.presenter.MachineController;
import machine.view.Documentation;
import machine.view.Controls;
import static machine.view.Machine.disassembledConsole;
import static machine.view.Machine.displayConsole;
import static machine.view.Machine.pswTable;
import static machine.view.Machine.ramTable;
import static machine.view.Machine.registerTable;
import static machine.view.Machine.stackRecordPanel;


/**
 *
 * @author jl948836
 */
public class MachView extends javax.swing.JFrame {

    private final MachineController controller;
    
        /**
     * Creates new form MachineView
     */
    public MachView() {
        controller = null;
        initComponents();
        
        super.pack();
        super.setLocationRelativeTo(null);
        super.setVisible(true); 
    }
    
    /**
     * Creates new form MachView
     * @param controller
     */
    public MachView(final MachineController controller) {
        this.controller = controller;
        initComponents();
        
        super.setTitle("WAL - Machine Simulator");
        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                controller.disposeMachineView();
            }
        };
        super.addWindowListener(exitListener);
        
        ColumnHeaderRenderer colRenderer = new ColumnHeaderRenderer();
        RowHeaderRenderer rowRenderer = new RowHeaderRenderer();
        CellRenderer cellRenderer = new CellRenderer();
        CellEditor cellEditor = new CellEditor();
        cellEditor.setClickCountToStart(1);
        TableColumn column;
        
        Machine.ramTable.getTableHeader().setDefaultRenderer(colRenderer);
        Machine.ramTable.getColumnModel().getColumn(0).setCellRenderer(rowRenderer);
        for (int col = 1; col < 17; col++) {
            column = Machine.ramTable.getColumnModel().getColumn(col);
            column.setCellRenderer(cellRenderer);
            column.setCellEditor(cellEditor);
        }
        Machine.ramTable.getTableHeader().setReorderingAllowed(false);
        Machine.ramTable.getTableHeader().setResizingAllowed(false);
        Machine.ramTable.setCellSelectionEnabled(true);
        
        Machine.pswTable.getTableHeader().setDefaultRenderer(colRenderer);
        Machine.pswTable.getColumnModel().getColumn(0).setCellRenderer(
                new RowHeaderRenderer(new Color(128, 0, 0), Color.white,
                new Font("SansSerif", Font.BOLD, 16)));
        column = Machine.pswTable.getColumnModel().getColumn(1);
        column.setCellRenderer(cellRenderer);
        
        Machine.registerTable.getTableHeader().setDefaultRenderer(colRenderer);
        Machine.registerTable.getColumnModel().getColumn(0).setCellRenderer(
                new RowHeaderRenderer(new Color(128, 0, 0), Color.white,
                new Font("SansSerif", Font.BOLD, 16)));
        column = Machine.registerTable.getColumnModel().getColumn(1);
        column.setCellRenderer(cellRenderer);
        column.setCellEditor(cellEditor);
        
        errorDisplay.setVisible(false);
        
        speedComboBox.setSelectedIndex(4);
        
        Documentation.addLanguageReference();
        Documentation.addSyntaxReference();
        
        super.pack();
        super.setLocationRelativeTo(null);
        super.setVisible(true); 
    }

/*
 * This section is code to help control the Machine tab   
 */
    /**
     * 
     * @param address
     * @return 
     */
    public String getRAMBytes(int address) {
        int row = address / 16;
        int col = address % 16;
        return (String)ramTable.getValueAt(row, col + 1);
    }
    
    /**
     * 
     * @param value
     * @param address 
     */
    public void setRAMBytes(String value, int address) {
        int row = address / 16;
        int col = address % 16;
        ramTable.setValueAt(value, row, col + 1);
    }
    
    /**
     * 
     * @return 
     */
    public String[] getAllRAMBytes() {
        String[] ramBytes= new String[256];

        int i = 0;
        for (int address = 0; address < 256; address++) {
                ramBytes[address] = getRAMBytes(address);
        }

        return ramBytes;
    }

    /**
     * 
     * @param ramBytes 
     */
    public void setAllRAMBytes(byte[] ramBytes) {
        for (int i = 0; i < 256; i++) {

        }
    }
    
    /**
     * 
     * @return 
     */
    public String getInstructionPointer() {
        return (String) pswTable.getValueAt(0, 1);
    }

    /**
     * 
     * @param value 
     */
    public void setInstructionPointer(String value) {
        pswTable.setValueAt(value, 0, 1);
    }
    
    /**
     * 
     * @return 
     */
    public String getInstructionRegister() {
        return (String) pswTable.getValueAt(1, 1);
    }

    /**
     * 
     * @param value 
     */
    public void setInstructionRegister(String value) {
        pswTable.setValueAt(value, 1, 1);
    }
    
    /**
     * 
     * @param register
     * @return 
     */
    public String getRegisterBytes(int register) {
        return (String) registerTable.getValueAt(register, 1);
    }

    /**
     * 
     * @param value
     * @param register 
     */
    public void setRegisterBytes(String value, int register) {
        registerTable.setValueAt(value, register, 1);
    }
    
    /**
     * 
     * @return 
     */
    public String[] getAllRegisterBytes() {
        String[] registerBytes = new String[16];
        for (int i = 0; i < 16; i++) {
            registerBytes[i] = (String) registerTable.getValueAt(i, 1);
        }

        return registerBytes;
    }
    
    /**
    * Used to create a new activation record and add it to the stack panel.
    * @param returnAddress
    * @param dynamicLink
    */
    public void createActivationRecord(int returnAddress, int dynamicLink) {
            String call = "CallAt 0x" + 
                             Integer.toHexString(returnAddress).toUpperCase();
            ActivationRecord ar = new ActivationRecord(call, 
                    returnAddress + 2,dynamicLink);
            stackRecordPanel.addRecord(ar);
    }
    
    /**
     * Used to delete an existing activation record and remove it from the stack panel.
     */
    public void deleteActivationRecord() {
            stackRecordPanel.removeRecord();
    }

    /**
     * Used to delete all activation records and clear the stack panel.
     */
    public void resetActivationRecords() {
            stackRecordPanel.resetRecords();
    }
    
    /**
     * 
     * @return 
     */
    public JTextArea getConsoleTextArea() {
        return displayConsole;
    }
    
    /**
     * 
     * @return 
     */
    public JTextArea getDisassTextArea() {
        return disassembledConsole;
    }
    
    public JTextArea getErrorTextArea() {
        return errorDisplay;
    }
    
/*
 * This section is to help control the Documentation Tab    
 */
    
    
    
/*
 * This section is to help control the Control Panel   
 */
    
    public void reset() {
        if (registerTable.isEditing()){                         //Change Log Begin #1
            registerTable.getCellEditor().cancelCellEditing();
        }
        if (ramTable.isEditing()){
            ramTable.getCellEditor().cancelCellEditing();
        }                                                       //Change Log End #1
        for (int i = 0; i < 16; i++) {
            if (i == 13 || i == 14) {
                registerTable.setValueAt("FF", i, 1);
            }
            else {
                registerTable.setValueAt("00", i, 1);
            }

        }
    }
    
/*
 * This section is to help control textEditor and ErrorPanel portions   
 */    
    public JTextArea getErrorPane(){
        return errorDisplay;
    }
    
    /**
     * Displays the error List after the user presses the Assemble button
     * @param errorList
     */
    public void setErrorText(ArrayList<String> errorList) {
            String errorText = "";
            for (String error : errorList){
                    errorText += error + "\n";
            }
            errorDisplay.setText(errorText);
    }
    
    public JTextComponent getEditorPane() {
        return textEditor.getTextPane();
    }
    
    public String getEditorText() {
        return textEditor.getText();
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        controlPanel = new javax.swing.JPanel();
        assembleButton = new javax.swing.JButton();
        runButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        stepButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        disassembleButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        speedComboBox = new javax.swing.JComboBox();
        mainPanel = new javax.swing.JTabbedPane();
        machine1 = new machine.view.Machine();
        documentation1 = new machine.view.Documentation();
        textEditorPanel = new javax.swing.JPanel();
        textEditor = new machine.view.TextEditor();
        errorDisplayScrollPane = new javax.swing.JScrollPane();
        errorDisplay = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(3840, 2160));
        setMinimumSize(new java.awt.Dimension(800, 600));
        setPreferredSize(new java.awt.Dimension(1100, 800));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        controlPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Control Panel"));
        controlPanel.setLayout(new java.awt.GridBagLayout());

        assembleButton.setText("Assemble");
        assembleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                assembleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        controlPanel.add(assembleButton, gridBagConstraints);

        runButton.setText("Run");
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        controlPanel.add(runButton, gridBagConstraints);

        stopButton.setText("Stop");
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        controlPanel.add(stopButton, gridBagConstraints);

        stepButton.setText("Step");
        stepButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stepButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        controlPanel.add(stepButton, gridBagConstraints);

        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        controlPanel.add(resetButton, gridBagConstraints);

        disassembleButton.setText("Disassemble");
        disassembleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disassembleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        controlPanel.add(disassembleButton, gridBagConstraints);

        jLabel1.setText("Speed:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        controlPanel.add(jLabel1, gridBagConstraints);

        speedComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10%", "20%",
            "30%", "40%", "50%", "60%", "70%", "80%", "90%", "100%"}));
speedComboBox.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        speedComboBoxActionPerformed(evt);
    }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
    controlPanel.add(speedComboBox, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.2;
    gridBagConstraints.weighty = 0.05;
    getContentPane().add(controlPanel, gridBagConstraints);

    mainPanel.addTab("Machine", machine1);
    mainPanel.addTab("Help", documentation1);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.1;
    gridBagConstraints.weighty = 0.9;
    getContentPane().add(mainPanel, gridBagConstraints);

    textEditorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Text Editor"));

    javax.swing.GroupLayout textEditorPanelLayout = new javax.swing.GroupLayout(textEditorPanel);
    textEditorPanel.setLayout(textEditorPanelLayout);
    textEditorPanelLayout.setHorizontalGroup(
        textEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(textEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
    );
    textEditorPanelLayout.setVerticalGroup(
        textEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(textEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE)
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.weighty = 0.75;
    getContentPane().add(textEditorPanel, gridBagConstraints);

    errorDisplayScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Errors"));

    errorDisplay.setColumns(20);
    errorDisplay.setRows(5);
    errorDisplayScrollPane.setViewportView(errorDisplay);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.weighty = 0.1;
    getContentPane().add(errorDisplayScrollPane, gridBagConstraints);

    jMenu1.setText("File");
    jMenuBar1.add(jMenu1);

    jMenu2.setText("Edit");
    jMenuBar1.add(jMenu2);

    setJMenuBar(jMenuBar1);

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void assembleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_assembleButtonActionPerformed
        mainPanel.setSelectedIndex(0);
        controller.performAssemble();
    }//GEN-LAST:event_assembleButtonActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        controller.setClockSpeed(getSpeed());
        controller.runClock();
    }//GEN-LAST:event_runButtonActionPerformed

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        controller.stopClock();
    }//GEN-LAST:event_stopButtonActionPerformed

    private void stepButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stepButtonActionPerformed
        controller.stepClock();
    }//GEN-LAST:event_stepButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        controller.resetMachine();
    }//GEN-LAST:event_resetButtonActionPerformed

    private void disassembleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disassembleButtonActionPerformed
        String[] ramBytes= getAllRAMBytes();
        
        textEditor.getTextPane().setText(
            controller.performDisassemble(getInstructionPointer(), ramBytes));
    }//GEN-LAST:event_disassembleButtonActionPerformed

    private void speedComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speedComboBoxActionPerformed
        controller.setClockSpeed(getSpeed());
    }//GEN-LAST:event_speedComboBoxActionPerformed
    
    private int getSpeed() {
        int speed;
        switch((String)speedComboBox.getSelectedItem()) {
            case "10%": speed = 1;
                            break;
            case "20%": speed = 2;
                            break;
            case "30%": speed = 3;
                            break;
            case "40%": speed = 4;
                            break;
            case "50%": speed = 5;
                            break;
            case "60%": speed = 6;
                            break;
            case "70%": speed = 7;
                            break;
            case "80%": speed = 8;
                            break;
            case "90%": speed = 9;
                            break;
            case "100%": speed = 10;
                            break;
            default: speed = 500;
                             break;
        }
        
        return speed;
    }
    
//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(MachView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(MachView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(MachView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(MachView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new MachView().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton assembleButton;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JButton disassembleButton;
    private machine.view.Documentation documentation1;
    private javax.swing.JTextArea errorDisplay;
    private javax.swing.JScrollPane errorDisplayScrollPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private machine.view.Machine machine1;
    private javax.swing.JTabbedPane mainPanel;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton runButton;
    private javax.swing.JComboBox speedComboBox;
    private javax.swing.JButton stepButton;
    private javax.swing.JButton stopButton;
    private machine.view.TextEditor textEditor;
    private javax.swing.JPanel textEditorPanel;
    // End of variables declaration//GEN-END:variables
}
