/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package machine.model;


import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Ryan Ball
 */
public class RAMTableModel extends AbstractTableModel {
    
    private JFrame frame;
    
    public RAMTableModel(JFrame frame) {
        this.frame = frame;
    }
    
     private final String[] columnNames = {"RAM", "0", "1", "2", "3", "4", "5", "6",
                                     "7", "8", "9", "A", "B", "C", "D", "E" , "F"};
     
        private String[][] data = { {"0" , "00", "00", "00", "00", "00", "00",
                                    "00", "00", "00", "00", "00", "00", 
                                    "00", "00", "00", "00"},
            
                                    {"1" , "00", "00", "00", "00", "00", "00",
                                    "00", "00", "00", "00", "00", "00", 
                                    "00", "00", "00", "00"},
                                    
                                    {"2" , "00", "00", "00", "00", "00", "00",
                                    "00", "00", "00", "00", "00", "00", 
                                    "00", "00", "00", "00"},
                                    
                                    {"3" , "00", "00", "00", "00", "00", "00",
                                    "00", "00", "00", "00", "00", "00", 
                                    "00", "00", "00", "00"},
                                    
                                    {"4" , "00", "00", "00", "00", "00", "00",
                                    "00", "00", "00", "00", "00", "00", 
                                    "00", "00", "00", "00"},
                                    
                                    {"5" , "00", "00", "00", "00", "00", "00",
                                    "00", "00", "00", "00", "00", "00", 
                                    "00", "00", "00", "00"},
                                    
                                    {"6" , "00", "00", "00", "00", "00", "00",
                                    "00", "00", "00", "00", "00", "00", 
                                    "00", "00", "00", "00"},
                                    
                                    {"7" , "00", "00", "00", "00", "00", "00",
                                    "00", "00", "00", "00", "00", "00", 
                                    "00", "00", "00", "00"},
                                    
                                    {"8" , "00", "00", "00", "00", "00", "00",
                                    "00", "00", "00", "00", "00", "00", 
                                    "00", "00", "00", "00"},
                                    
                                    {"9" , "00", "00", "00", "00", "00", "00",
                                    "00", "00", "00", "00", "00", "00", 
                                    "00", "00", "00", "00"},
                                    
                                    {"A" , "00", "00", "00", "00", "00", "00",
                                    "00", "00", "00", "00", "00", "00", 
                                    "00", "00", "00", "00"},
                                    
                                    {"B" , "00", "00", "00", "00", "00", "00",
                                    "00", "00", "00", "00", "00", "00", 
                                    "00", "00", "00", "00"},
                                    
                                    {"C" , "00", "00", "00", "00", "00", "00",
                                    "00", "00", "00", "00", "00", "00", 
                                    "00", "00", "00", "00"},
                                    
                                    {"D" , "00", "00", "00", "00", "00", "00",
                                    "00", "00", "00", "00", "00", "00", 
                                    "00", "00", "00", "00"},
                                    
                                    {"E" , "00", "00", "00", "00", "00", "00",
                                    "00", "00", "00", "00", "00", "00", 
                                    "00", "00", "00", "00"},
                                    
                                    {"F" , "00", "00", "00", "00", "00", "00",
                                    "00", "00", "00", "00", "00", "00", 
                                    "00", "00", "00", "00"}};
 
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

            if (col == 0) {
                return false;
            } else {
                return true;
            }
        }
 
        public void setValueAt(Object value, int row, int col) {
            super.setValueAt(value, row, col);
            
            String val = ((String) value).toUpperCase();
            int len = val.length();
            
            if (len > 2) {
                showError(row, col - 1);
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
                    showError(row, col - 1);
                }
            }
            else {
                data[row][col] = "00";
            }
            this.fireTableCellUpdated(row, col);
        }
        
        private void showError(final int row, final int col) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    String message = "Invalid bytes entered at RAM address 0x" +
                            Integer.toHexString(row).toUpperCase() +
                            Integer.toHexString(col).toUpperCase() + ".";
                    JOptionPane.showMessageDialog(frame, message, null, JOptionPane.ERROR_MESSAGE);
                }
            });
        }
}
