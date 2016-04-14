/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package machine.view;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import machine.presenter.MachineController;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 *
 * @author jl948836
 */
public class MachineView extends javax.swing.JFrame {

    private final MachineController controller;
    Integer[] SIZES = { 8, 9, 10, 11, 12, 14, 16, 18, 20,
        22, 24, 26, 28, 36, 48, 72 }; //the sizes for the font size combo box. MB
    
    String[] THEMES = { "Black on White", "Green on Black", "Blue on Black", "Red on Black", 
        "White on Black", "Black on Light-Yellow"};
    
    
        /**
     * Creates new form MachineView
     */
    public MachineView() {
        controller = null;
        initComponents();
        
        
        super.setSize(null);
        //super.pack();
        super.setLocationRelativeTo(null);
        super.setVisible(true); 
    }
    
    /**
     * Creates new form MachView
     * @param controller
     */
    public MachineView(final MachineController controller) {
        this.controller = controller;
        initComponents();
        customInitComponents();
        
        super.setTitle("WALL - Machine Simulator");
        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                controller.disposeMachineView();
            }
        };
        super.addWindowListener(exitListener);
       
        //super.pack();
        super.setLocationRelativeTo(null);
        super.setVisible(true); 
    }

/*
 * This section is code to help control the MachinePanel tab   
 */
    
    /**
     * 
     * @param address
     * @return 
     */
    public String getRAMBytes(int address) {
        return machine1.getRAMBytes(address);
    }
    
    /**
     * 
     * @param value
     * @param address 
     */
    public void setRAMBytes(String value, int address) {
        machine1.setRAMBytes(value, address);
    }
    
    /**
     * 
     * @return 
     */
    public String[] getAllRAMBytes() {
        return machine1.getAllRAMBytes();
    }

    /**
     * 
     * @param ramBytes 
     */
    public void setAllRAMBytes(byte[] ramBytes) {
        machine1.setAllRAMBytes(ramBytes);
    }
    
    /**
     * 
     * @return 
     */
    public String getInstructionPointer() {
        return machine1.getInstructionPointer();
    }

    /**
     * 
     * @param value 
     */
    public void setInstructionPointer(String value) {
        machine1.setInstructionPointer(value);
    }
    
    /**
     * 
     * @return 
     */
    public String getInstructionRegister() {
        return machine1.getInstructionRegister();
    }

    /**
     * 
     * @param value 
     */
    public void setInstructionRegister(String value) {
        machine1.setInstructionRegister(value);
    }
    
    /**
     * 
     * @param register
     * @return 
     */
    public String getRegisterBytes(int register) {
        return machine1.getRegisterBytes(register);
    }

    /**
     * 
     * @param value
     * @param register 
     */
    public void setRegisterBytes(String value, int register) {
        machine1.setRegisterBytes(value, register);
    }
    
    /**
     * 
     * @return 
     */
    public String[] getAllRegisterBytes() {
        return machine1.getAllRegisterBytes();
    }
    
    /**
    * Used to create a new activation record and add it to the stack panel.
    * @param returnAddress
    * @param dynamicLink
    */
    public void createActivationRecord(int returnAddress, int dynamicLink) {
        machine1.createActivationRecord(returnAddress, dynamicLink);
    }
    
    /**
     * Used to delete an existing activation record and remove it from the stack panel.
     */
    public void deleteActivationRecord() {
        machine1.deleteActivationRecord();
    }

    /**
     * Used to delete all activation records and clear the stack panel.
     */
    public void resetActivationRecords() {
        machine1.resetActivationRecords();
    }
    
    /**
     * 
     * @return 
     */
    public JTextArea getConsoleTextArea() {
        return machine1.getConsoleTextArea();
    }
    
    /**
     * 
     * @return 
     */
    public JTextArea getDisassTextArea() {
        return machine1.getDisassTextArea();
    }
    
/*
 * This section is to help control the Control Panel   
 */
    
    /**
     * 
     */
    public void reset() {
        if (machine1.getRegisterTable().isEditing()){                         //Change Log Begin #1
            machine1.getRegisterTable().getCellEditor().cancelCellEditing();
        }
        if (machine1.getRamTable().isEditing()){
            machine1.getRamTable().getCellEditor().cancelCellEditing();
        }                                                       //Change Log End #1
        for (int i = 0; i < 16; i++) {
            if (i == 13 || i == 14) {
                machine1.getRegisterTable().setValueAt("FF", i, 1);
            }
            else {
                machine1.getRegisterTable().setValueAt("00", i, 1);
            }

        }
    }
    
    /**
     * 
     * @return speed
     */
    private int getSpeed() {
        int speed;
        switch((String)speedComboBox.getSelectedItem()) {
            case "10%": 
                speed = 1;
                break;
            case "20%": 
                speed = 2;
                break;
            case "30%": 
                speed = 3;
                break;
            case "40%": 
                speed = 4;
                break;
            case "50%": 
                speed = 5;
                break;
            case "60%": 
                speed = 6;
                break;
            case "70%": 
                speed = 7;
                break;
            case "80%": 
                speed = 8;
                break;
            case "90%": 
                speed = 9;
                break;
            case "100%": 
                speed = 10;
                break;
            default: 
                speed = 500;
                break;
            }
        
        return speed;
    }
    
/*
 * This section is to help control textEditor and ErrorPanel portions   
 */    
    
    public TextEditorPanel getTextEditorPanel() {
        return textEditorPanel;

    }
    public JTextArea getErrorPane() {
        if (textEditorPanel.isVisible()) {
            return textEditorPanel.getErrorPane();
        }
        else {
            return textEditorPanel.getTextEditorFrame().getErrorPane();
        }
    }
    
    public JTextArea getErrorTextArea() {
        if (textEditorPanel.isVisible()) {
            return textEditorPanel.getErrorPane();
        }
        else {
            return textEditorPanel.getTextEditorFrame().getErrorPane();
        }
    }
    
    public void setErrorText(ArrayList<String> errorList) {
        if (textEditorPanel.isVisible()) {
            textEditorPanel.setErrorText(errorList);
        }
        else {
            textEditorPanel.getTextEditorFrame().setErrorText(errorList);
        }
    }
    
    public JTextComponent getEditorPane() {
        if (textEditorPanel.isVisible()) {
            return textEditorPanel.getEditorPane();
        }
        else {
            return textEditorPanel.getTextEditorFrame().getEditorPane();
        }
    }
    
    public String getEditorText() {
        if (textEditorPanel.isVisible()) {
            return textEditorPanel.getEditorText();
        }
        else {
            return textEditorPanel.getTextEditorFrame().getEditorText();
        }
    }
    
    protected String[] getFontNames() {
        if (textEditorPanel.isVisible()) {
            return textEditorPanel.getFontNames();
        }
        else {
            return textEditorPanel.getTextEditorFrame().getFontNames();
        }
    }
    
    private void customInitComponents(){
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        super.setBounds(0,0,(int) ((int) screenSize.width * .85), (int) ((int) screenSize.height * .85));
        //System.out.println((int) ((int) screenSize.width * .85) + " " + (int) ((int) screenSize.height * .85));
        
        
        //errorDisplayScrollPane.setVisible(false);
        getErrorPane().setVisible(false);
        
        speedComboBox.setSelectedIndex(4);
        
        documentation1.addLanguageReference();
        documentation1.addSyntaxReference();
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

        controlPanel = new javax.swing.JPanel();
        assembleButton = new javax.swing.JButton();
        runButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        stepButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        disassembleButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        speedComboBox = new javax.swing.JComboBox();
        mainPanel = new javax.swing.JTabbedPane();
        machine1 = new machine.view.MachinePanel();
        documentation1 = new machine.view.DocumentationPanel();
        textEditorPanel = new machine.view.TextEditorPanel(this);
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        openMachineMenuItem = new javax.swing.JMenuItem();
        openSourceMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        saveMachineMenuItem = new javax.swing.JMenuItem();
        saveSourceMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(3840, 2160));
        setMinimumSize(new java.awt.Dimension(800, 600));
        setPreferredSize(new java.awt.Dimension(1100, 900));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        controlPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Control Panel"));
        controlPanel.setLayout(new java.awt.GridBagLayout());

        assembleButton.setText("Assemble");
        assembleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                assembleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        controlPanel.add(assembleButton, gridBagConstraints);

        runButton.setText("Run");
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        controlPanel.add(runButton, gridBagConstraints);

        stopButton.setText("Stop");
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        controlPanel.add(stopButton, gridBagConstraints);

        stepButton.setText("Step");
        stepButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stepButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        controlPanel.add(stepButton, gridBagConstraints);

        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        controlPanel.add(resetButton, gridBagConstraints);

        disassembleButton.setText("Disassemble");
        disassembleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disassembleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        controlPanel.add(disassembleButton, gridBagConstraints);

        jLabel1.setText("Speed:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        controlPanel.add(jLabel1, gridBagConstraints);

        speedComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10%", "20%",
            "30%", "40%", "50%", "60%", "70%", "80%", "90%", "100%"}));
speedComboBox.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        speedComboBoxActionPerformed(evt);
    }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
    controlPanel.add(speedComboBox, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.7;
    gridBagConstraints.weighty = 0.05;
    getContentPane().add(controlPanel, gridBagConstraints);

    mainPanel.addTab("Machine", machine1);
    mainPanel.addTab("Help", documentation1);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.7;
    gridBagConstraints.weighty = 0.9;
    getContentPane().add(mainPanel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridheight = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.3;
    gridBagConstraints.weighty = 1.0;
    getContentPane().add(textEditorPanel, gridBagConstraints);

    jMenu1.setText("File");

    openMachineMenuItem.setText("Open Machine State");
    openMachineMenuItem.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            openMachineMenuItemActionPerformed(evt);
        }
    });
    jMenu1.add(openMachineMenuItem);

    openSourceMenuItem.setText("Open Source File");
    openSourceMenuItem.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            openSourceMenuItemActionPerformed(evt);
        }
    });
    jMenu1.add(openSourceMenuItem);
    jMenu1.add(jSeparator1);

    saveMachineMenuItem.setText("Save Machine State");
    saveMachineMenuItem.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            saveMachineMenuItemActionPerformed(evt);
        }
    });
    jMenu1.add(saveMachineMenuItem);

    saveSourceMenuItem.setText("Save Source File");
    saveSourceMenuItem.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            saveSourceMenuItemActionPerformed(evt);
        }
    });
    jMenu1.add(saveSourceMenuItem);

    jMenuBar1.add(jMenu1);

    setJMenuBar(jMenuBar1);

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void assembleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_assembleButtonActionPerformed
        mainPanel.setSelectedIndex(0);
        controller.performAssemble();
    }//GEN-LAST:event_assembleButtonActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        controller.setClockSpeed(getSpeed());
        controller.runClock();
    }//GEN-LAST:event_runButtonActionPerformed

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        controller.stopClock();
    }//GEN-LAST:event_stopButtonActionPerformed

    private void stepButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stepButtonActionPerformed
        controller.stepClock();
    }//GEN-LAST:event_stepButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        controller.resetMachine();
    }//GEN-LAST:event_resetButtonActionPerformed

    private void disassembleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disassembleButtonActionPerformed
        String[] ramBytes= getAllRAMBytes();
        
        textEditorPanel.textEditor.getTextPane().setText(
            controller.performDisassemble(getInstructionPointer(), ramBytes));
    }//GEN-LAST:event_disassembleButtonActionPerformed

    private void speedComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speedComboBoxActionPerformed
        controller.setClockSpeed(getSpeed());
    }//GEN-LAST:event_speedComboBoxActionPerformed

    private void openMachineMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMachineMenuItemActionPerformed
        JFileChooser fc = new JFileChooser();
        
        FileFilter machineFilter = new FileNameExtensionFilter("Machine state (.machine)","machine");
        fc.addChoosableFileFilter(machineFilter);
        fc.setFileFilter(machineFilter);
        fc.setSelectedFile(new File("untitled.machine"));
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File machineFile = fc.getSelectedFile();
            try {
                try (FileReader reader = new FileReader(machineFile)) {
                    String bytes;
                    
                    for (int i = 0; i < 16; i++) {
                        for (int j = 1; j < 17; j++) {
                            bytes = String.valueOf((char)reader.read()) +
                                    String.valueOf((char)reader.read());
                            machine1.getRamTable().setValueAt(bytes, i, j);
                        }
                    }
                    
                    for (int i = 0; i < 16; i++) {
                        bytes = String.valueOf((char)reader.read()) +
                                String.valueOf((char)reader.read());
                        machine1.getRegisterTable().setValueAt(bytes, i, 1);
                    }
                    
                    // set IP
                    bytes = String.valueOf((char)reader.read()) +
                            String.valueOf((char)reader.read());
                    machine1.getPswTable().setValueAt(bytes, 0, 1);
                    
                    // set IR
                    bytes = String.valueOf((char)reader.read()) +
                            String.valueOf((char)reader.read()) +
                            String.valueOf((char)reader.read()) +
                            String.valueOf((char)reader.read()) +
                            String.valueOf((char)reader.read());
                    
                    machine1.getPswTable().setValueAt(bytes, 1, 1);
                }
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            catch (IOException ex) {
                    ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_openMachineMenuItemActionPerformed

    private void openSourceMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openSourceMenuItemActionPerformed
        JFileChooser fc = new JFileChooser();
        
        FileFilter sourceFilter = new FileNameExtensionFilter("Source file (.txt)","txt");
        fc.addChoosableFileFilter(sourceFilter);
        fc.setFileFilter(sourceFilter);
        fc.setSelectedFile(new File("untitled.txt"));
        int returnVal = fc.showOpenDialog(this);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File sourceFile = fc.getSelectedFile();
                try {
                try (BufferedReader reader = new BufferedReader(new FileReader(sourceFile))) {
                    Document doc = textEditorPanel.textEditor.getDocument();
                    
                    String line;
                    while ((line = reader.readLine()) != null) {
                        doc.insertString(doc.getLength(), line + '\n', null);
                    }
                }
                } catch (IOException | BadLocationException ex) {
                    Logger.getLogger(MachineView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
    }//GEN-LAST:event_openSourceMenuItemActionPerformed

    private void saveMachineMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMachineMenuItemActionPerformed
        JFileChooser fc = new JFileChooser();
        FileFilter machineFilter = new FileNameExtensionFilter("Machine state (.machine)","machine");
        fc.addChoosableFileFilter(machineFilter);
        fc.setFileFilter(machineFilter);
        fc.setSelectedFile(new File("untitled.machine"));
        
        int returnVal = fc.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File machineFile = fc.getSelectedFile();
            
            try {
                try (FileWriter writer = new FileWriter(machineFile)) {
                    String[] strRAMBytes = getAllRAMBytes();
                    
                    for (int i = 0; i < 256; i++) {
                        writer.write(strRAMBytes[i]);
                    }
                    
                    String[] strRegisterBytes = getAllRegisterBytes();
                    for (int i = 0; i < 16; i++) {
                        writer.write(strRegisterBytes[i]);
                    }
                    
                    String ip = (String) machine1.getPswTable().getValueAt(0, 1);
                    writer.write(ip);
                    
                    String ir = (String) machine1.getPswTable().getValueAt(1, 1);
                    writer.write(ir);
                }
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_saveMachineMenuItemActionPerformed

    private void saveSourceMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSourceMenuItemActionPerformed
        JFileChooser fc = new JFileChooser();
        FileFilter sourceFilter = new FileNameExtensionFilter("Source file (.txt)","txt");
        fc.addChoosableFileFilter(sourceFilter);
        fc.setFileFilter(sourceFilter);
        fc.setSelectedFile(new File("untitled.txt"));
        
        int returnVal = fc.showSaveDialog(this);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File sourceFile = fc.getSelectedFile();
            
            try {
                try (PrintWriter writer = new PrintWriter(sourceFile)) {
                    Document doc = textEditorPanel.textEditor.getDocument();
                    writer.print(doc.getText(0, doc.getLength()));
                }
                
            } catch (FileNotFoundException | BadLocationException ex) {
                Logger.getLogger(MachineView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_saveSourceMenuItemActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton assembleButton;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JButton disassembleButton;
    private machine.view.DocumentationPanel documentation1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private machine.view.MachinePanel machine1;
    private javax.swing.JTabbedPane mainPanel;
    private javax.swing.JMenuItem openMachineMenuItem;
    private javax.swing.JMenuItem openSourceMenuItem;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton runButton;
    private javax.swing.JMenuItem saveMachineMenuItem;
    private javax.swing.JMenuItem saveSourceMenuItem;
    private javax.swing.JComboBox speedComboBox;
    private javax.swing.JButton stepButton;
    private javax.swing.JButton stopButton;
    private machine.view.TextEditorPanel textEditorPanel;
    // End of variables declaration//GEN-END:variables
}
