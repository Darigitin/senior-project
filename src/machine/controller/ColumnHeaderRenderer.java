/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package machine.controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 *
 * @author Ryan and Nicole
 */
public class ColumnHeaderRenderer extends DefaultTableCellRenderer {
    
    private final Color foregroundColor = Color.WHITE;
    private final Color backgroundColor = new Color(128, 0, 0); //Maroon
    private final Font font = new Font("SansSerif", Font.BOLD, 14);
    
    public ColumnHeaderRenderer() {
        
        super.setHorizontalAlignment(CENTER);
        super.setHorizontalTextPosition(CENTER);
        super.setVerticalAlignment(BOTTOM);
        super.setOpaque(true);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
      
        super.getTableCellRendererComponent(table, value,
              isSelected, hasFocus, row, column);
        
        JTableHeader tableHeader = table.getTableHeader();
      
        if (tableHeader != null) {
          setForeground(foregroundColor);
          setBackground(backgroundColor);
          setFont(font);
          setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        }
        
        return this;
    }
}
