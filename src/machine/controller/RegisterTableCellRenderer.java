package machine.controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.table.DefaultTableCellRenderer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jl948836
 */
public class RegisterTableCellRenderer extends DefaultTableCellRenderer {
 
    private final Font font = new Font("SansSerif", Font.PLAIN, 14);
    
    public RegisterTableCellRenderer() {
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
        
        
        //Visual Register - Stack Pointer
        if (row == 14 && column == 1) {
            setBackground(getBackground().darker());
            setForeground(Color.red);
        }
        
        //Visual Register - Base Pointer
        if (row == 13 && column == 1) {
            setBackground(getBackground().darker());
            setForeground(Color.blue);
        }
        
        
        setFont(font);
        setText((String) value);
        setBorder(BorderFactory.createLineBorder(Color.black));
        return this;
    }
}
