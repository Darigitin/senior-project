/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package machine.controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author jl948836
 */
public class PSWTableCellRenderer extends DefaultTableCellRenderer {
    private final Font font = new Font("SansSerif", Font.PLAIN, 14);
    
    public PSWTableCellRenderer() {
        super.setHorizontalAlignment(CENTER);
        super.setHorizontalTextPosition(CENTER);
        super.setVerticalAlignment(CENTER);
        super.setOpaque(true);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        Component l = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (hasFocus && isSelected) {
            setBackground(new Color(255, 160, 160));
        }
        else {
            setBackground(Color.white);
        }
        
        setForeground(Color.black);
        
        //Visual Register - Instruction Pointer
        if (row == 0 && column == 1) {
            setBackground(getBackground().darker());
            setForeground(Color.magenta.darker());
        }
        
        
        setFont(font);
        setText((String) value);
        setBorder(BorderFactory.createLineBorder(Color.black));
        return this;
    }
}
