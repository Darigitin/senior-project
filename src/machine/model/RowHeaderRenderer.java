/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package machine.model;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import static javax.swing.SwingConstants.BOTTOM;
import static javax.swing.SwingConstants.CENTER;
import static javax.swing.SwingConstants.LEFT;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 *
 * @author Ryan Ball
 */
public class RowHeaderRenderer extends DefaultTableCellRenderer {
    
    private Color foregroundColor;
    private Color backgroundColor; 
    private final Font font;
    
    public RowHeaderRenderer() {
        this(Color.WHITE, new Color(128, 0, 0), new Font("SansSerif", Font.BOLD, 14));
    }
    
    public RowHeaderRenderer(Color foregroundColor, Color backgroundColor, Font font) {
        setHorizontalAlignment(CENTER);
        setHorizontalTextPosition(CENTER);
        setVerticalAlignment(CENTER);
        setOpaque(true);
        
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.font = font;
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
      
        super.getTableCellRendererComponent(table, value,
              isSelected, hasFocus, row, column);
      
        setForeground(foregroundColor);
        setBackground(backgroundColor);
        setFont(font);
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        return this;
    }
}
