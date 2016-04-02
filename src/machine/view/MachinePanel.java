/**
 * Program:
 * 
 * Purpose:
 * 
 * @author:
 * 
 * date/ver:
 */

/**
 * Change Log
 * 
 * # author   - date:     description
 * 
 */
package machine.view;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableColumn;
import machine.model.CellEditor;
import machine.model.CellRenderer;
import machine.model.ColumnHeaderRenderer;
import machine.model.RAMTableModel;
import machine.model.RegisterTableModel;
import machine.model.RowHeaderRenderer;
import machine.model.SpecialTableModel;

/**
 *
 * @author jl948836
 */
public class MachinePanel extends javax.swing.JPanel {
    
    public MachinePanel(){
        initComponents();
        customInitComponents();
    }

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
    
//    public void showInstructionPointerError() {
//        final String message;
//        if (controller.isRunning()) {
//            controller.stopClock();
//            message = "Instruction pointer out of range. Simulation has stopped.";
//        }
//        else {
//            message = "Instruction pointer out of range.";
//        }
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                JOptionPane.showMessageDialog(machine.view.MachView.this, message, null, JOptionPane.ERROR_MESSAGE);
//            }
//        });
//  
//    }

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
    public JTable getRamTable() {
        return ramTable;
    }
    
    /**
     * 
     * @return 
     */
    public JTable getPswTable() {
        return pswTable;
    }
    
    /**
     * 
     * @return 
     */
    public JTable getRegisterTable() {
        return registerTable;
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
    
    private void customInitComponents(){
        ColumnHeaderRenderer colRenderer = new ColumnHeaderRenderer();
        RowHeaderRenderer rowRenderer = new RowHeaderRenderer();
        CellRenderer cellRenderer = new CellRenderer();
        CellEditor cellEditor = new CellEditor();
        cellEditor.setClickCountToStart(1);
        TableColumn column;
        
        ramTable.getTableHeader().setDefaultRenderer(colRenderer);
        ramTable.getColumnModel().getColumn(0).setCellRenderer(rowRenderer);
        for (int col = 1; col < 17; col++) {
            column = ramTable.getColumnModel().getColumn(col);
            column.setCellRenderer(cellRenderer);
            column.setCellEditor(cellEditor);
        }
        ramTable.getTableHeader().setReorderingAllowed(false);
        ramTable.getTableHeader().setResizingAllowed(false);
        ramTable.setCellSelectionEnabled(true);
        
        pswTable.getTableHeader().setDefaultRenderer(colRenderer);
        pswTable.getColumnModel().getColumn(0).setCellRenderer(
                new RowHeaderRenderer(new Color(128, 0, 0), Color.white,
                new Font("SansSerif", Font.BOLD, 16)));
        column = pswTable.getColumnModel().getColumn(1);
        column.setCellRenderer(cellRenderer);
        
        registerTable.getTableHeader().setDefaultRenderer(colRenderer);
        registerTable.getColumnModel().getColumn(0).setCellRenderer(
                new RowHeaderRenderer(new Color(128, 0, 0), Color.white,
                new Font("SansSerif", Font.BOLD, 16)));
        column = registerTable.getColumnModel().getColumn(1);
        column.setCellRenderer(cellRenderer);
        column.setCellEditor(cellEditor);
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

        ramScrollPane = new javax.swing.JScrollPane();
        ramTable = new javax.swing.JTable();
        pswScrollPane = new javax.swing.JScrollPane();
        pswTable = new javax.swing.JTable();
        registerScrollPane = new javax.swing.JScrollPane();
        registerTable = new javax.swing.JTable();
        disassembleScrollPane = new javax.swing.JScrollPane();
        disassembledConsole = new javax.swing.JTextArea();
        displayScrollPane = new javax.swing.JScrollPane();
        displayConsole = new javax.swing.JTextArea();
        stackPanelScrollPane = new javax.swing.JScrollPane();
        stackRecordPanel = new machine.view.StackPanel();

        setLayout(new java.awt.GridBagLayout());

        ramTable.setModel(new RAMTableModel(this));
        ramTable.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        ramTable.setRowHeight(24);
        ramTable.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                ramTableComponentResized(evt);
            }
        });
        ramScrollPane.setViewportView(ramTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.53;
        add(ramScrollPane, gridBagConstraints);

        pswTable.setModel(new SpecialTableModel(this));
        pswTable.setRowHeight(19);
        pswScrollPane.setViewportView(pswTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 0.1;
        add(pswScrollPane, gridBagConstraints);

        registerTable.setModel(new RegisterTableModel(this));
        registerTable.setRowHeight(20);
        registerScrollPane.setViewportView(registerTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 0.9;
        add(registerScrollPane, gridBagConstraints);

        disassembleScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Disassembled Console"));

        disassembledConsole.setBackground(new java.awt.Color(204, 204, 204));
        disassembledConsole.setColumns(20);
        disassembledConsole.setRows(5);
        disassembledConsole.setText("No disassembled code yet...");
        disassembleScrollPane.setViewportView(disassembledConsole);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.47;
        add(disassembleScrollPane, gridBagConstraints);

        displayScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Display Console"));

        displayConsole.setBackground(new java.awt.Color(0, 0, 0));
        displayConsole.setColumns(20);
        displayConsole.setForeground(new java.awt.Color(0, 250, 0));
        displayConsole.setRows(5);
        displayScrollPane.setViewportView(displayConsole);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.47;
        add(displayScrollPane, gridBagConstraints);

        stackPanelScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Stack Panel"));
        stackPanelScrollPane.setViewportView(stackRecordPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 1.0;
        add(stackPanelScrollPane, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void ramTableComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_ramTableComponentResized
        int ramSPHeight = ramScrollPane.getViewport().getSize().height;
        ramTable.setRowHeight(ramSPHeight/16);
    }//GEN-LAST:event_ramTableComponentResized


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane disassembleScrollPane;
    private javax.swing.JTextArea disassembledConsole;
    private javax.swing.JTextArea displayConsole;
    private javax.swing.JScrollPane displayScrollPane;
    private javax.swing.JScrollPane pswScrollPane;
    private javax.swing.JTable pswTable;
    private javax.swing.JScrollPane ramScrollPane;
    private javax.swing.JTable ramTable;
    private javax.swing.JScrollPane registerScrollPane;
    private javax.swing.JTable registerTable;
    private javax.swing.JScrollPane stackPanelScrollPane;
    private machine.view.StackPanel stackRecordPanel;
    // End of variables declaration//GEN-END:variables
}
