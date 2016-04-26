/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package machine.view;

import java.util.ArrayList;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

/**
 * Program: Text Editor Frame
 * 
 * Purpose: A standalone frame for a Text Editor.
 * 
 * @author: jl948836
 * 
 * date/ver: 04/07/16 1.0.0
 */
public class TextEditorFrame extends javax.swing.JFrame {

    MachineView machineView;
    Integer[] SIZES = { 8, 9, 10, 11, 12, 14, 16, 18, 20,
        22, 24, 26, 28, 36, 48, 72 }; //the sizes for the font size combo box. MB
    
    public TextEditorFrame() {
        initComponents();
        textEditorPanel.getSplitJoinButton().setText("Join Editor");
    }
    
    /**
     * Creates new form machineFrame
     * @param machineView
     */
    public TextEditorFrame(MachineView machineView) {
        this.machineView = machineView;
        initComponents();
        textEditorPanel.getSplitJoinButton().setText("Join Editor");
    }
    
    /**
     * 
     * @return 
     */
    public JTextArea getErrorPane() {
        return textEditorPanel.getErrorPane();
    }
    
    /**
     * 
     * @param errorList 
     */
    public void setErrorText(ArrayList<String> errorList) {
        textEditorPanel.setErrorText(errorList);
    }
    
    /**
     * 
     * @return 
     */
    public JTextComponent getEditorPane() {
        return textEditorPanel.getEditorPane();
    }
    
    /**
     * 
     * @return 
     */
    public String getEditorText() {
        return textEditorPanel.getEditorText();
    }
    
    /**
     * 
     * @return 
     */
    protected String[] getFontNames() {
        return textEditorPanel.getFontNames();
    }
    
    /**
     * 
     * @return 
     */
    public TextEditorPanel getTextEditorPanel() {
        return textEditorPanel;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        textEditorPanel = new machine.view.TextEditorPanel(machineView, this);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(500, 800));
        getContentPane().setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(textEditorPanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TextEditorFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TextEditorFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private machine.view.TextEditorPanel textEditorPanel;
    // End of variables declaration//GEN-END:variables
}
