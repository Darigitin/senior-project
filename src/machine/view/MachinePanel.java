/**
 * Program: Machine Panel
 * 
 * Purpose: Creates a custom panel that is essentially the Machine of the WALL
 *          Assembler. Contains the RAM Table, PSW Table, Register Table, Disassemble
 *          Console, Display Console, and the Location Counter.
 *          Uses a GridBag Layout.
 * 
 * @author: jl94836, Jordan Lescallette
 * 
 * date/ver: 03/19/16 1.0.5
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
import machine.controller.RAMTableCellRenderer;
import machine.controller.ColumnHeaderRenderer;
import machine.controller.PSWTableCellRenderer;
import machine.model.RAMTableModel;
import machine.controller.RegisterTableCellRenderer;
import machine.model.RegisterTableModel;
import machine.controller.RowHeaderRenderer;
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
     * Get the bytes located at a specific memory address in the RAM table.
     * 
     * @param address - Between 0-255
     * @return - Value at the memory address 
     */
    public String getRAMBytes(int address) {
        int row = address / 16;
        int col = address % 16;
        return (String)ramTable.getValueAt(row, col + 1);
    }
    
    /**
     * Set the bytes located at a specific memory address into the RAM table.
     * 
     * @param value - between 0-255
     * @param address - (hex) between 00-FF
     */
    public void setRAMBytes(String value, int address) {
        int row = address / 16;
        int col = address % 16;
        ramTable.setValueAt(value, row, col + 1);
    }
    
    /**
     * Get the value of every memory address in the RAM Table (from 0-255)
     * 
     * @return - String Array of all values in the RAM Table
     */
    public String[] getAllRAMBytes() {
        String[] ramBytes= new String[256];

        for (int address = 0; address < 256; address++) {
                ramBytes[address] = getRAMBytes(address);
        }

        return ramBytes;
    }

    /**
     * Set the value of every memory address in the RAM Table (from 0-255)
     * 
     * @param ramBytes - byte Array containing values for every address (from 0-255)
     */
    public void setAllRAMBytes(byte[] ramBytes) {
        for (int i = 0; i < 256; i++) {

        }
    }
    
    /**
     * Get value from the Instruction Pointer (IP) in the PSW Table
     * 
     * @return
     */
    public String getInstructionPointer() {
        return (String) pswTable.getValueAt(0, 1);
    }

    /**
     * Set the value of the Instruction Pointer (IP) in the PSW Table
     * 
     * @param value - String hex number 00-FF
     */
    public void setInstructionPointer(String value) {
        pswTable.setValueAt(value, 0, 1);
    }
    
    /**
     * Get the value of the Instruction Register (IR) in the PSW Table
     * 
     * @return 
     */
    public String getInstructionRegister() {
        return (String) pswTable.getValueAt(1, 1);
    }

    /**
     * Set the value in the Instruction Register (IR) in the PSW Table
     * 
     * @param value - String hex number 00-FF
     */
    public void setInstructionRegister(String value) {
        pswTable.setValueAt(value, 1, 1);
    }

    /**
     * Get the value from a Register in the Register Table
     * 
     * @param register - (dec) from 0-15
     * @return 
     */
    public String getRegisterBytes(int register) {
        return (String) registerTable.getValueAt(register, 1);
    }

    /**
     * Set the value in a Register in the Register Table
     * 
     * @param value - (hex) from 00-FF
     * @param register - (dec) from 0-15
     */
    public void setRegisterBytes(String value, int register) {
        registerTable.setValueAt(value, register, 1);
    }
    
    /**
     * Get the values from every Register in the Register Table
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
    * 
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
     * Get reference to the RAM Table
     * 
     * @return 
     */
    public JTable getRamTable() {
        return ramTable;
    }
    
    /**
     * Get reference to the PSW Table
     * 
     * @return 
     */
    public JTable getPswTable() {
        return pswTable;
    }
    
    /**
     * Get reference to the Register Table
     * 
     * @return 
     */
    public JTable getRegisterTable() {
        return registerTable;
    }
    
    /**
     * Get reference to the Display Console
     * 
     * @return 
     */
    public JTextArea getConsoleTextArea() {
        return displayConsole;
    }
    
    /**
     * Get reference to the Disassemble Console
     * 
     * @return 
     */
    public JTextArea getDisassTextArea() {
        return disassembledConsole;
    }
    
    /**
     * Get reference to Instruction Counter Console
     * 
     * @return 
     */
    public JTextArea getInstructCounterTextArea() {
        return instructionCounterConsole;
    }
    
    /**
     * Get a reference to the Memory Error Display
     * 
     * @return 
     */
    public JTextArea getRunTimeErrorTextArea() {
        return runTimeErrorDisplay;
    }
    
    /**
     * Set up, initialize, format the RAM Table, Register Table and PSW Table.
     * Initialize the Cell Renderer.
     */
    private void customInitComponents(){
        ColumnHeaderRenderer colRenderer = new ColumnHeaderRenderer();
        RowHeaderRenderer rowRenderer = new RowHeaderRenderer();
        RAMTableCellRenderer ramCellRenderer = new RAMTableCellRenderer(this);
        RegisterTableCellRenderer registerCellRenderer = new RegisterTableCellRenderer();
        PSWTableCellRenderer pswCellRenderer = new PSWTableCellRenderer();
        
        CellEditor cellEditor = new CellEditor();
        cellEditor.setClickCountToStart(1);
        TableColumn column;
        
        ramTable.getTableHeader().setDefaultRenderer(colRenderer);
        ramTable.getColumnModel().getColumn(0).setCellRenderer(rowRenderer);
        
        for (int col = 1; col < 17; col++) {
            column = ramTable.getColumnModel().getColumn(col);
            column.setCellRenderer(ramCellRenderer);
            column.setCellEditor(cellEditor);
        }
        ramTable.getTableHeader().setReorderingAllowed(false);
        ramTable.getTableHeader().setResizingAllowed(false);
        ramTable.setCellSelectionEnabled(true);
        
        pswTable.setCellSelectionEnabled(true);
        pswTable.getTableHeader().setDefaultRenderer(colRenderer);
        pswTable.getColumnModel().getColumn(0).setCellRenderer(
                new RowHeaderRenderer(new Color(128, 0, 0), Color.white,
                new Font("SansSerif", Font.BOLD, 16)));
        column = pswTable.getColumnModel().getColumn(1);
        column.setCellRenderer(pswCellRenderer);
        column.setCellEditor(cellEditor);
        
        registerTable.getTableHeader().setDefaultRenderer(colRenderer);
        registerTable.getColumnModel().getColumn(0).setCellRenderer(
                new RowHeaderRenderer(new Color(128, 0, 0), Color.white,
                new Font("SansSerif", Font.BOLD, 16)));
        column = registerTable.getColumnModel().getColumn(1);
        column.setCellRenderer(registerCellRenderer);
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
        jPanel1 = new javax.swing.JPanel();
        instructionCounterScrollPane = new javax.swing.JScrollPane();
        instructionCounterConsole = new javax.swing.JTextArea();
        runTimeErrorDisplayScrollPane = new javax.swing.JScrollPane();
        runTimeErrorDisplay = new javax.swing.JTextArea();

        setMinimumSize(new java.awt.Dimension(600, 500));
        setLayout(new java.awt.GridBagLayout());

        ramTable.setModel(new RAMTableModel(this));
        ramTable.setFillsViewportHeight(true);
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
        pswTable.setFillsViewportHeight(true);
        pswTable.setRowHeight(19);
        pswTable.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                pswTableComponentResized(evt);
            }
        });
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
        registerTable.setFillsViewportHeight(true);
        registerTable.setRowHeight(20);
        registerTable.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                registerTableComponentResized(evt);
            }
        });
        registerScrollPane.setViewportView(registerTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 0.9;
        add(registerScrollPane, gridBagConstraints);

        disassembleScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Disassembled Console"));

        disassembledConsole.setEditable(false);
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

        displayConsole.setEditable(false);
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

        jPanel1.setLayout(new java.awt.GridBagLayout());

        instructionCounterScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Instruction Counter"));

        instructionCounterConsole.setEditable(false);
        instructionCounterConsole.setBackground(new java.awt.Color(204, 204, 204));
        instructionCounterConsole.setColumns(20);
        instructionCounterConsole.setRows(5);
        instructionCounterConsole.setText("Count of the number of Instructions executed.");
        instructionCounterScrollPane.setViewportView(instructionCounterConsole);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.025;
        jPanel1.add(instructionCounterScrollPane, gridBagConstraints);

        runTimeErrorDisplayScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Run-Time Errors"));

        runTimeErrorDisplay.setEditable(false);
        runTimeErrorDisplay.setBackground(new java.awt.Color(204, 204, 204));
        runTimeErrorDisplay.setColumns(20);
        runTimeErrorDisplay.setLineWrap(true);
        runTimeErrorDisplay.setRows(5);
        runTimeErrorDisplayScrollPane.setViewportView(runTimeErrorDisplay);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.975;
        jPanel1.add(runTimeErrorDisplayScrollPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void ramTableComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_ramTableComponentResized
        //System.out.println(evt.getComponent().getClass().getName() + " --- Resized ");
        int ramSPHeight = ramScrollPane.getViewport().getSize().height;
        ramTable.setRowHeight(ramSPHeight/16);
    }//GEN-LAST:event_ramTableComponentResized

    private void pswTableComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_pswTableComponentResized
        int ramSPHeight = pswScrollPane.getViewport().getSize().height;
        pswTable.setRowHeight(ramSPHeight/2);
    }//GEN-LAST:event_pswTableComponentResized

    private void registerTableComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_registerTableComponentResized
        int ramSPHeight = registerScrollPane.getViewport().getSize().height;
        registerTable.setRowHeight(ramSPHeight/16);
    }//GEN-LAST:event_registerTableComponentResized


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane disassembleScrollPane;
    private javax.swing.JTextArea disassembledConsole;
    private javax.swing.JTextArea displayConsole;
    private javax.swing.JScrollPane displayScrollPane;
    private javax.swing.JTextArea instructionCounterConsole;
    private javax.swing.JScrollPane instructionCounterScrollPane;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane pswScrollPane;
    private javax.swing.JTable pswTable;
    private javax.swing.JScrollPane ramScrollPane;
    private javax.swing.JTable ramTable;
    private javax.swing.JScrollPane registerScrollPane;
    private javax.swing.JTable registerTable;
    private javax.swing.JTextArea runTimeErrorDisplay;
    private javax.swing.JScrollPane runTimeErrorDisplayScrollPane;
    private javax.swing.JScrollPane stackPanelScrollPane;
    private machine.view.StackPanel stackRecordPanel;
    // End of variables declaration//GEN-END:variables
}
