/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package machine.model;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import machine.view.MachinePanel;

/**
 *
 * @author Ryan Ball
 */
public class SpecialTableModel extends AbstractTableModel{
    
    private final String[] columnNames = {"Special", "Contents"};
    private final String[][] data = { {"IP", "00"}, { "IR", "00 00"}};
    
    private final MachinePanel machineView;
    
    public SpecialTableModel(MachinePanel machineView) {
        this.machineView = machineView;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    @Override
    public Class getColumnClass(int c) {
        return columnNames.getClass();
    }
    
    @Override
    public boolean isCellEditable(int row, int col) {
        return !(col == 0 || (row == 1 && col == 1));
    }
 
    @Override
    public void setValueAt(Object value, int row, int col) {
        
        String val = ((String) value).toUpperCase();
        int len = val.length();
        
        if (row == 1) {
            data[row][col] = val;
        }
        else {
            if (len > 2) {
                //machineView.showInstructionPointerError();
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
            @Override
            public void run() {
                String message = "Invalid bytes entered in IP.";
                JOptionPane.showMessageDialog(machineView, message, null, JOptionPane.ERROR_MESSAGE);
            }
        });
    }  
}
