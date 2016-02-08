/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package machine.model;

import java.awt.Component;
import java.awt.Font;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author Ryan Ball
 */
public class CellEditor extends DefaultCellEditor {

    public CellEditor() {
        super(new JTextField());
    }
    
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
      int row, int column) {
    
        JTextField editor = (JTextField) super.getTableCellEditorComponent(table, value, isSelected,
                                row, column);
        
        if (value != null) {
            editor.setText(value.toString());
            editor.setHorizontalAlignment(SwingConstants.CENTER);
            editor.setFont(new Font("SansSerif", Font.PLAIN, 14));
        }
        return editor;
    }
}
