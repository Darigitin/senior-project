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
    
   /**
    * Returns the custom table cell renderer with overridden foreground background font and border.
    * During a printing operation, this method will be called with isSelected and hasFocus values of false to prevent selection and focus from appearing in the printed output. To do other customization based on whether or not the table is being printed, check the return value from JComponent.isPaintingForPrint().
    *
    * @param table - the JTable \
    * @param value - the value to assign to the cell at [row, column] 
    * @param isSelected - true if cell is selected 
    * @param hasFocus - true if cell has focus 
    * @param row - the row of the cell to render 
    * @param column - the column of the cell to render 
    * @return the default table cell renderer 
    * @see JComponent.isPaintingForPrint()
    **/
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
