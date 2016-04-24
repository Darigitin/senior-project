/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package machine.model;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.table.DefaultTableCellRenderer;
import machine.view.MachinePanel;

/**
 *
 * @author jl948836
 */
public class RAMTableCellRenderer extends DefaultTableCellRenderer {
    
    private final Font font = new Font("SansSerif", Font.PLAIN, 14);
    private final MachinePanel machine;
    
//    public RAMTableCellRenderer() {
//        
//        super.setHorizontalAlignment(CENTER);
//        super.setHorizontalTextPosition(CENTER);
//        super.setVerticalAlignment(CENTER);
//        super.setOpaque(true);
//    }
    
    public RAMTableCellRenderer(MachinePanel machine) {
        this.machine = machine;
        super.setHorizontalAlignment(CENTER);
        super.setHorizontalTextPosition(CENTER);
        super.setVerticalAlignment(CENTER);
        super.setOpaque(true);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (hasFocus && isSelected) {
            setBackground(new Color(255, 160, 160));
        }
        else {
            setBackground(Color.white);
        }
        setForeground(Color.black);
        
        //Visual Stack - Stack Pointer
        String sp = machine.getRegisterBytes(14);
        int spRow = Integer.parseInt(sp.substring(0, 1), 16);
        int spColumn = Integer.parseInt(sp.substring(1, 2), 16) + 1;
        if (spRow == row && spColumn == column) {
            //setBackground(Color.red);
            setForeground(Color.red);
        }
        
        //Visual Stack - Base Pointer
        String bp = machine.getRegisterBytes(13);
        int bpRow = Integer.parseInt(bp.substring(0, 1), 16);
        int bpColumn = Integer.parseInt(bp.substring(1, 2), 16) + 1;
        if (bpRow == row && bpColumn == column) {
            //setBackground(Color.blue);
            setForeground(Color.blue);
        }
        
        //Visual - Instruction Pointer
        String ip = machine.getInstructionPointer();
        int ipRow = Integer.parseInt(ip.substring(0, 1), 16);
        int ipColumn = Integer.parseInt(ip.substring(1, 2), 16) + 1;
        if (ipRow == row && ipColumn == column) {
            //setBackground(Color.GREEN);
            setForeground(Color.green);
        }
        
        
        setFont(font);
        setText((String) value);
        setBorder(BorderFactory.createLineBorder(Color.black));
        return this;
    }
    
}
