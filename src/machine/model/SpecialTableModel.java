/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package machine.model;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import machine.view.MachineView;

/**
 *
 * @author Ryan Ball
 */
public class SpecialTableModel extends AbstractTableModel{
    
    private final String[] columnNames = {"Special", "Contents"};
    private String[][] data = { {"IP", "00"}, { "IR", "00 00"}};
    
    private MachineView machineView;
    
    public SpecialTableModel(MachineView machineView) {
        this.machineView = machineView;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    public Class getColumnClass(int c) {
        return String.class;
    }
    
    public boolean isCellEditable(int row, int col) {

        if (col == 0 || (row == 1 && col == 1)) {
            return false;
        } else {
            return true;
        }
    }
 
    public void setValueAt(Object value, int row, int col) {
        
        String val = ((String) value).toUpperCase();
        int len = val.length();
        
        if (row == 1) {
            data[row][col] = val;
        }
        else {
            if (len > 2) {
                machineView.showInstructionPointerError();
            }
            else if (len > 0) {
                try {
                    Integer.parseInt(val, 16);
                    if (len == 2) {
                        data[row][col] = val;
                    }
                    if (len == 1) {
                        data[row][col] = "0" + val;
                    }
                }
                catch (NumberFormatException ex) {
                    showError();
                }
            }
            else {
                data[row][col] = "00";
            }  
        }
        this.fireTableCellUpdated(row, col);
    }

    private void showError() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String message = "Invalid bytes entered in IP.";
                JOptionPane.showMessageDialog(machineView, message, null, JOptionPane.ERROR_MESSAGE);
            }
        });
    }  
}
