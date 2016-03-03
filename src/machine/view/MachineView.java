/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/* Change Log
    #1 Matt Vertefeuille 02/22/16 Reset button now unfocuses all cell editoring on press
*/
package machine.view;

import machine.model.ColumnHeaderRenderer;
import machine.model.CellRenderer;
import machine.model.CellEditor;
import machine.model.RAMTableModel;
import machine.model.SpecialTableModel;
import machine.model.CustomTable;
import machine.model.RowHeaderRenderer;
import machine.model.RegisterTableModel;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import system.view.SystemView;
import machine.presenter.MachineController;

/**
 *
 * @author Ryan Ball
 */
public class MachineView extends javax.swing.JFrame {

    private String[] fontNames = null;
    Integer[] sizes = { 8, 9, 10, 11, 12, 14, 16, 18, 20,
		22, 24, 26, 28, 36, 48, 72 }; //the sizes for the font size combo box. MB
    private final MachineController controller;
    protected String[] getFontNames()
    // Will get the all the avalilable texts from the system.
    // Programmer: Mariela Barrera
    {
		if (fontNames == null)
		{
			GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
                        GraphicsConfiguration.
			fontNames = env.getAvailableFontFamilyNames();
                        System.out.println(Arrays.toString(fontNames));
		}
		return fontNames;
	}
    public void updateText() {
        //creates and sets the font according to the options selected from the font size and name combo boxs.
        //Programmer: Mariela Barrera
      String name = (String) fontNamejComboBox.getSelectedItem();

      Integer size = (Integer) fontSizejComboBox.getSelectedItem();

      int style = Font.PLAIN;

      Font newfont = new Font(name, style, size);
      textEditor.getTextPane().setFont(newfont);
    }
    public MachineView(final MachineController controller) {
        this.controller = controller;
        
        initComponents();
        
        super.setTitle("Machine Simulator");
        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                controller.disposeMachineView();
            }
        };
        super.addWindowListener(exitListener);
        
        ColumnHeaderRenderer colRenderer = new ColumnHeaderRenderer();
        RowHeaderRenderer rowRenderer = new RowHeaderRenderer();
        CellRenderer cellRenderer = new CellRenderer();
        CellEditor cellEditor = new CellEditor();
        cellEditor.setClickCountToStart(1);
        TableColumn column;
        
        ramTable.getTableHeader().setDefaultRenderer(colRenderer);
        ramTable.getColumnModel().getColumn(0).setCellRenderer(rowRenderer);
        for (int col = 1; col < 17; col++) {
            column = ramTable.getColumnModel().getColumn(col);
            column.setCellRenderer(cellRenderer);
            column.setCellEditor(cellEditor);
        }
        ramTable.getTableHeader().setReorderingAllowed(false);
        ramTable.getTableHeader().setResizingAllowed(false);
        ramTable.setCellSelectionEnabled(true);
        
        specialTable.getTableHeader().setDefaultRenderer(colRenderer);
        specialTable.getColumnModel().getColumn(0).setCellRenderer(
                new RowHeaderRenderer(new Color(128, 0, 0), Color.white,
                new Font("SansSerif", Font.BOLD, 16)));
        column = specialTable.getColumnModel().getColumn(1);
        column.setCellRenderer(cellRenderer);
        
        registerTable.getTableHeader().setDefaultRenderer(colRenderer);
        registerTable.getColumnModel().getColumn(0).setCellRenderer(
                new RowHeaderRenderer(new Color(128, 0, 0), Color.white,
                new Font("SansSerif", Font.BOLD, 16)));
        column = registerTable.getColumnModel().getColumn(1);
        column.setCellRenderer(cellRenderer);
        column.setCellEditor(cellEditor);
        
        errorPane.setVisible(false);
        
        speedComboBox.setSelectedIndex(4);
        
        addLanguageReference();
        addSyntaxReference();
        
        super.pack();
        super.setLocationRelativeTo(null);
        super.setVisible(true); 
    }
    
    private void addLanguageReference() {
        languageReferenceEditorPane.setContentType("text/html");
        InputStream inputStream =  SystemView.class.getResourceAsStream("/html/machineLanguageReference.html");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            languageReferenceEditorPane.setText(sb.toString());
        } catch (IOException ex) {
            languageReferenceEditorPane.setText("Could not load language reference.");
        }
        try {
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(SystemView.class.getName()).log(Level.SEVERE, null, ex);
        }
        languageReferenceEditorPane.setEditable(false);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                languageReferenceScrollPane.getVerticalScrollBar().setValue(0);
            }
        });
    }
    
    private void addSyntaxReference() {
        syntaxEditorPane.setContentType("text/html");
        InputStream inputStream =  SystemView.class.getResourceAsStream("/html/machineSyntax.html");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            syntaxEditorPane.setText(sb.toString());
        } catch (IOException ex) {
            syntaxEditorPane.setText("Could not load language reference.");
        }
        try {
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(SystemView.class.getName()).log(Level.SEVERE, null, ex);
        }
        syntaxEditorPane.setEditable(false);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                syntaxScrollPane.getVerticalScrollBar().setValue(0);
            }
        });
    }
    
    public String getRAMBytes(int address) {
        int row = address / 16;
        int col = address % 16;
        return (String)ramTable.getValueAt(row, col + 1);
    }
    
    public String[] getAllRAMBytes() {
        String[] ramBytes= new String[256];
        
        int i = 0;
        for (int address = 0; address < 256; address++) {
                ramBytes[address] = getRAMBytes(address);
        }
        
        return ramBytes;
    }
    
    public void setRAMBytes(String value, int address) {
        int row = address / 16;
        int col = address % 16;
        ramTable.setValueAt(value, row, col + 1);
    }
    
    public void setAllRAMBytes(byte[] ramBytes) {
        for (int i = 0; i < 256; i++) {
  
        }
    }
    
    public String getInstructionPointer() {
        return (String) specialTable.getValueAt(0, 1);
    }
    
    public void setInstructionPointer(String value) {
        specialTable.setValueAt(value, 0, 1);
    }
    
    public String getInstructionRegister() {
        return (String) specialTable.getValueAt(1, 1);
    }
    
    public void setInstructionRegister(String value) {
        specialTable.setValueAt(value, 1, 1);
    }
    
    public String getRegisterBytes(int register) {
        return (String) registerTable.getValueAt(register, 1);
    }
    
    public String[] getAllRegisterBytes() {
        String[] registerBytes = new String[16];
        for (int i = 0; i < 16; i++) {
            registerBytes[i] = (String) registerTable.getValueAt(i, 1);
        }
        
        return registerBytes;
    }
    
    public void setRegisterBytes(String value, int register) {
        registerTable.setValueAt(value, register, 1);
    }
    
    public void highlightCells(byte[] row, byte[] col) 
            throws IllegalArgumentException {
        
        ramTable.clearSelection();
       
        if (row.length != col.length) {
            throw new IllegalArgumentException("row and col must be equal length");
        }
        
        for (int i = 0; i < row.length; i++) {
            ramTable.changeSelection(row[i], col[i], false, false);
        } 
    }
    
    public JTextComponent getEditorPane() {
        return textEditor.getTextPane();
    }
    
    public JTextArea getConsoleTextArea() {
        return consoleTextArea;
    }
    
    public JTextArea getDisassTextArea() {
        return disassembledTextArea;
    }
    
    /**
    * Used to create a new activation record and add it to the stack panel.
    * @param returnAddress
    * @param dynamicLink
    */
    public void createActivationRecord(int returnAddress, int dynamicLink) {
            String call = "CallAt 0x" + 
                             Integer.toHexString(returnAddress).toUpperCase();
            ActivationRecord ar = new ActivationRecord(call, 
                    returnAddress + 2,dynamicLink);
            stackPanel.addRecord(ar);
    }
    
    /**
     * Used to delete an existing activation record and remove it from the stack panel.
     */
    public void deleteActivationRecord() {
            stackPanel.removeRecord();
    }

    /**
     * Used to delete all activation records and clear the stack panel.
     */
    public void resetActivationRecords() {
            stackPanel.resetRecords();
    }
    
    public void showInstructionPointerError() {
        final String message;
        if (controller.isRunning()) {
            controller.stopClock();
            message = "Instruction pointer out of range. Simulation has stopped.";
        }
        else {
            message = "Instruction pointer out of range.";
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(MachineView.this, message, null, JOptionPane.ERROR_MESSAGE);
            }
        });
  
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        callStackPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        mainPanel = new javax.swing.JPanel();
        tabbedPane = new javax.swing.JTabbedPane();
        machinePanel = new javax.swing.JPanel();
        ramScrollPane = new javax.swing.JScrollPane();
        ramTable = new CustomTable()
        ;
        registerScrollPane = new javax.swing.JScrollPane();
        registerTable = new CustomTable();
        specialScrollPane = new javax.swing.JScrollPane();
        specialTable = new CustomTable();
        consoleScrollPane = new javax.swing.JScrollPane();
        consoleTextArea = new javax.swing.JTextArea();
        disassScrollPane = new javax.swing.JScrollPane();
        disassembledTextArea = new javax.swing.JTextArea();
        stackScrollPane = new javax.swing.JScrollPane();
        stackPanel = new machine.view.StackPanel();
        helpPanel = new javax.swing.JPanel();
        helpTabbedPane = new javax.swing.JTabbedPane();
        languageReferenceScrollPane = new javax.swing.JScrollPane();
        languageReferenceEditorPane = new javax.swing.JEditorPane();
        syntaxScrollPane = new javax.swing.JScrollPane();
        syntaxEditorPane = new javax.swing.JEditorPane();
        textEditor = new machine.view.TextEditor();
        errorPane = new javax.swing.JScrollPane();
        errors = new javax.swing.JTextArea();
        controlPanel = new javax.swing.JPanel();
        AssembleButton = new javax.swing.JButton();
        runButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        stepButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        disassembleButton = new javax.swing.JButton();
        speedLabel = new javax.swing.JLabel();
        speedComboBox = new javax.swing.JComboBox();
        fontNamejLabel = new javax.swing.JLabel();
        fontNamejComboBox = new javax.swing.JComboBox(getFontNames());
        fontSizejComboBox = new javax.swing.JComboBox(sizes);
        fontSize = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMachineMenuItem = new javax.swing.JMenuItem();
        openSourceMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        saveMachineMenuItem = new javax.swing.JMenuItem();
        saveSourceMenuItem = new javax.swing.JMenuItem();

        callStackPanel.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout callStackPanelLayout = new javax.swing.GroupLayout(callStackPanel);
        callStackPanel.setLayout(callStackPanelLayout);
        callStackPanelLayout.setHorizontalGroup(
            callStackPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        callStackPanelLayout.setVerticalGroup(
            callStackPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tabbedPane.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        tabbedPane.setPreferredSize(new java.awt.Dimension(1100, 800));

        machinePanel.setBackground(new java.awt.Color(255, 255, 255));

        ramTable.setModel(new RAMTableModel(this));
        ramTable.setRowHeight(30);
        ramScrollPane.setViewportView(ramTable);

        registerTable.setModel(new RegisterTableModel(this));
        registerTable.setRowHeight(30);
        registerScrollPane.setViewportView(registerTable);

        specialTable.setModel(new SpecialTableModel(this));
        specialTable.setRowHeight(30);
        specialScrollPane.setViewportView(specialTable);

        consoleScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Console", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        consoleTextArea.setEditable(false);
        consoleTextArea.setBackground(new java.awt.Color(0, 0, 0));
        consoleTextArea.setColumns(20);
        consoleTextArea.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        consoleTextArea.setForeground(new java.awt.Color(0, 255, 0));
        consoleTextArea.setRows(5);
        consoleScrollPane.setViewportView(consoleTextArea);

        disassScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Disassembled Text", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        disassembledTextArea.setEditable(false);
        disassembledTextArea.setBackground(new java.awt.Color(204, 204, 204));
        disassembledTextArea.setColumns(20);
        disassembledTextArea.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        disassembledTextArea.setRows(5);
        disassembledTextArea.setText("No disassembler text yet");
        disassScrollPane.setViewportView(disassembledTextArea);

        stackScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Stack Records", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        stackScrollPane.setPreferredSize(new java.awt.Dimension(100, 110));
        stackScrollPane.setViewportView(stackPanel);

        javax.swing.GroupLayout machinePanelLayout = new javax.swing.GroupLayout(machinePanel);
        machinePanel.setLayout(machinePanelLayout);
        machinePanelLayout.setHorizontalGroup(
            machinePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(machinePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(machinePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(machinePanelLayout.createSequentialGroup()
                        .addComponent(disassScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(consoleScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(ramScrollPane, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(12, 12, 12)
                .addGroup(machinePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(specialScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addComponent(registerScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(stackScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 35, Short.MAX_VALUE))
        );
        machinePanelLayout.setVerticalGroup(
            machinePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(machinePanelLayout.createSequentialGroup()
                .addGroup(machinePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(machinePanelLayout.createSequentialGroup()
                        .addComponent(specialScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(registerScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 508, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(machinePanelLayout.createSequentialGroup()
                        .addComponent(ramScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 507, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(machinePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(disassScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(consoleScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(stackScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tabbedPane.addTab("Machine", machinePanel);

        helpTabbedPane.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        languageReferenceScrollPane.setViewportView(languageReferenceEditorPane);

        helpTabbedPane.addTab("Language Reference", languageReferenceScrollPane);

        syntaxScrollPane.setViewportView(syntaxEditorPane);

        helpTabbedPane.addTab("Syntax", syntaxScrollPane);

        javax.swing.GroupLayout helpPanelLayout = new javax.swing.GroupLayout(helpPanel);
        helpPanel.setLayout(helpPanelLayout);
        helpPanelLayout.setHorizontalGroup(
            helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(helpTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1095, Short.MAX_VALUE)
        );
        helpPanelLayout.setVerticalGroup(
            helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(helpTabbedPane)
        );

        tabbedPane.addTab("Help", helpPanel);

        textEditor.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Editor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        textEditor.setPreferredSize(new java.awt.Dimension(300, 500));

        errorPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Errors", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        errors.setColumns(20);
        errors.setForeground(new java.awt.Color(255, 0, 0));
        errors.setRows(5);
        errorPane.setViewportView(errors);

        controlPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Control Panel", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        controlPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 5));

        AssembleButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AssembleButton.setText("Assemble");
        AssembleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AssembleButtonActionPerformed(evt);
            }
        });
        controlPanel.add(AssembleButton);

        runButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        runButton.setText("Run");
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        controlPanel.add(runButton);

        stopButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        stopButton.setText("Stop");
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });
        controlPanel.add(stopButton);

        stepButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        stepButton.setText("Step");
        stepButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stepButtonActionPerformed(evt);
            }
        });
        controlPanel.add(stepButton);

        resetButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });
        controlPanel.add(resetButton);

        disassembleButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        disassembleButton.setText("Disassemble");
        disassembleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disassembleButtonActionPerformed(evt);
            }
        });
        controlPanel.add(disassembleButton);

        speedLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        speedLabel.setText("Speed:");
        controlPanel.add(speedLabel);

        speedComboBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        speedComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10%", "20%",
            "30%", "40%", "50%", "60%", "70%", "80%", "90%", "100%"}));
speedComboBox.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        speedComboBoxActionPerformed(evt);
    }
    });
    controlPanel.add(speedComboBox);

    fontNamejLabel.setText("Font:");

    fontNamejComboBox.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            fontNamejComboBoxActionPerformed(evt);
        }
    });

    fontSizejComboBox.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            fontSizejComboBoxActionPerformed(evt);
        }
    });

    fontSize.setText("Font Size:");

    javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
    mainPanel.setLayout(mainPanelLayout);
    mainPanelLayout.setHorizontalGroup(
        mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(mainPanelLayout.createSequentialGroup()
            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(controlPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainPanelLayout.createSequentialGroup()
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(textEditor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(errorPane, javax.swing.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)))
                .addGroup(mainPanelLayout.createSequentialGroup()
                    .addGap(100, 100, 100)
                    .addComponent(fontNamejLabel)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(fontNamejComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fontSize)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(fontSizejComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(98, 98, 98))))
    );
    mainPanelLayout.setVerticalGroup(
        mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainPanelLayout.createSequentialGroup()
                    .addComponent(controlPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(0, 6, Short.MAX_VALUE))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(fontNamejLabel)
                        .addComponent(fontNamejComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(fontSizejComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(fontSize))))
            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addGroup(mainPanelLayout.createSequentialGroup()
                    .addComponent(textEditor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(errorPane, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(45, 45, 45))
                .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 755, javax.swing.GroupLayout.PREFERRED_SIZE)))
    );

    jScrollPane1.setViewportView(mainPanel);

    fileMenu.setText("File");

    openMachineMenuItem.setText("Open Machine State...");
    openMachineMenuItem.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            openMachineMenuItemActionPerformed(evt);
        }
    });
    fileMenu.add(openMachineMenuItem);

    openSourceMenuItem.setText("Open Source File...");
    openSourceMenuItem.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            openSourceMenuItemActionPerformed(evt);
        }
    });
    fileMenu.add(openSourceMenuItem);
    fileMenu.add(jSeparator1);

    saveMachineMenuItem.setText("Save Machine State...");
    saveMachineMenuItem.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            saveMachineMenuItemActionPerformed(evt);
        }
    });
    fileMenu.add(saveMachineMenuItem);

    saveSourceMenuItem.setText("Save Source File...");
    saveSourceMenuItem.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            saveSourceMenuItemActionPerformed(evt);
        }
    });
    fileMenu.add(saveSourceMenuItem);

    menuBar.add(fileMenu);

    setJMenuBar(menuBar);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1634, Short.MAX_VALUE)
            .addContainerGap())
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 837, Short.MAX_VALUE)
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void disassembleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disassembleButtonActionPerformed
        String[] ramBytes= getAllRAMBytes();
        
        textEditor.getTextPane().setText(
            controller.performDisassemble(getInstructionPointer(), ramBytes));
    }//GEN-LAST:event_disassembleButtonActionPerformed

    private void openMachineMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMachineMenuItemActionPerformed
        JFileChooser fc = new JFileChooser();
        
        FileFilter machineFilter = new FileNameExtensionFilter("Machine state (.machine)","machine");
        fc.addChoosableFileFilter(machineFilter);
        fc.setFileFilter(machineFilter);
        fc.setSelectedFile(new File("untitled.machine"));
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File machineFile = fc.getSelectedFile();
            try (FileReader reader = new FileReader(machineFile)){
                String bytes;
                
                for (int i = 0; i < 16; i++) {
                    for (int j = 1; j < 17; j++) {
                        bytes = String.valueOf((char)reader.read()) +
                                String.valueOf((char)reader.read());
                        ramTable.setValueAt(bytes, i, j);
                    }
                }
                
                for (int i = 0; i < 16; i++) {
                    bytes = String.valueOf((char)reader.read()) +
                            String.valueOf((char)reader.read());
                    registerTable.setValueAt(bytes, i, 1);
                }
                
                // set IP
                bytes = String.valueOf((char)reader.read()) +
                        String.valueOf((char)reader.read());
                specialTable.setValueAt(bytes, 0, 1);
                
                // set IR
                bytes = String.valueOf((char)reader.read()) +
                        String.valueOf((char)reader.read()) +
                        String.valueOf((char)reader.read()) +
                        String.valueOf((char)reader.read()) +
                        String.valueOf((char)reader.read());
                
                specialTable.setValueAt(bytes, 1, 1);
 
                reader.close();
            }
            catch (IOException ex) {
                    ex.printStackTrace(System.out);
            }
        }
    }//GEN-LAST:event_openMachineMenuItemActionPerformed

    private void saveMachineMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMachineMenuItemActionPerformed
        JFileChooser fc = new JFileChooser();
        FileFilter machineFilter = new FileNameExtensionFilter("Machine state (.machine)","machine");
        fc.addChoosableFileFilter(machineFilter);
        fc.setFileFilter(machineFilter);
        fc.setSelectedFile(new File("untitled.machine"));
        
        int returnVal = fc.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File machineFile = fc.getSelectedFile();
            
            try (FileWriter writer = new FileWriter(machineFile)){
                String[] strRAMBytes = getAllRAMBytes();

                for (int i = 0; i < 256; i++) {
                    writer.write(strRAMBytes[i]);
                }

                String[] strRegisterBytes = getAllRegisterBytes();
                for (int i = 0; i < 16; i++) {
                    writer.write(strRegisterBytes[i]);
                }

                String ip = (String) specialTable.getValueAt(0, 1);
                writer.write(ip);

                String ir = (String) specialTable.getValueAt(1, 1);
                writer.write(ir);
                
                writer.close();
            }
            catch (IOException ex) {
                ex.printStackTrace(System.out);
            }
        }
    }//GEN-LAST:event_saveMachineMenuItemActionPerformed

    private void openSourceMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openSourceMenuItemActionPerformed
        JFileChooser fc = new JFileChooser();
        
        FileFilter sourceFilter;
        sourceFilter = new FileNameExtensionFilter("Source file (.txt)","txt");
        fc.addChoosableFileFilter(sourceFilter);
        fc.setFileFilter(sourceFilter);
        fc.setSelectedFile(new File("untitled.txt"));
        int returnVal = fc.showOpenDialog(this);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File sourceFile = fc.getSelectedFile();
                try(BufferedReader reader = new BufferedReader(new FileReader(sourceFile))){
                    Document doc = textEditor.getDocument();
                    
                    String line;
                    while ((line = reader.readLine()) != null) {
                        doc.insertString(doc.getLength(), line + '\n', null);
                    }
                    reader.close();
                } catch (IOException | BadLocationException ex) {
                    Logger.getLogger(MachineView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
    }//GEN-LAST:event_openSourceMenuItemActionPerformed

    private void saveSourceMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSourceMenuItemActionPerformed
        JFileChooser fc = new JFileChooser();
        FileFilter sourceFilter = new FileNameExtensionFilter("Source file (.txt)","txt");
        fc.addChoosableFileFilter(sourceFilter);
        fc.setFileFilter(sourceFilter);
        fc.setSelectedFile(new File("untitled.txt"));
        
        int returnVal = fc.showSaveDialog(this);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File sourceFile = fc.getSelectedFile();
            
            try(PrintWriter writer = new PrintWriter(sourceFile)) {
                Document doc = textEditor.getDocument();
                writer.print(doc.getText(0, doc.getLength()));
                
                writer.close();
                
            } catch (FileNotFoundException | BadLocationException ex) {
                Logger.getLogger(MachineView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_saveSourceMenuItemActionPerformed

    private void AssembleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AssembleButtonActionPerformed
        tabbedPane.setSelectedIndex(0);
        controller.performAssemble();
    }//GEN-LAST:event_AssembleButtonActionPerformed

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

    private void speedComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speedComboBoxActionPerformed
        controller.setClockSpeed(getSpeed());
    }//GEN-LAST:event_speedComboBoxActionPerformed

    private void fontNamejComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontNamejComboBoxActionPerformed
      updateText();
    }//GEN-LAST:event_fontNamejComboBoxActionPerformed

    private void fontSizejComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontSizejComboBoxActionPerformed
       updateText();
    }//GEN-LAST:event_fontSizejComboBoxActionPerformed
    
    
    private int getSpeed() {
        int speed;
        switch((String)speedComboBox.getSelectedItem()) {
            case "10%": speed = 1;
                            break;
            case "20%": speed = 2;
                            break;
            case "30%": speed = 3;
                            break;
            case "40%": speed = 4;
                            break;
            case "50%": speed = 5;
                            break;
            case "60%": speed = 6;
                            break;
            case "70%": speed = 7;
                            break;
            case "80%": speed = 8;
                            break;
            case "90%": speed = 9;
                            break;
            case "100%": speed = 10;
                            break;
            default: speed = 500;
                             break;
        }
        
        return speed;
    }
    /**
     * Displays the error List after the user presses the Assemble button
     * @param errorList
     */
    public void setErrorText(ArrayList<String> errorList) {
            String errorText = "";
            for (String error : errorList){
                    errorText += error + "\n";
            }
            errors.setText(errorText);
    }
    
    public JScrollPane getErrorPane() {
        return errorPane;
    }
    
    public JTextArea getErrorTextArea() {
        return errors;
    }
    
    public String getEditorText() {
        return textEditor.getText();
    }
    
    public void reset() {
        if (registerTable.isEditing()){                         //Change Log Begin #1
            registerTable.getCellEditor().cancelCellEditing();
        }
        if (ramTable.isEditing()){
            ramTable.getCellEditor().cancelCellEditing();
        }                                                       //Change Log End #1
        for (int i = 0; i < 16; i++) {
            if (i == 13 || i == 14) {
                registerTable.setValueAt("FF", i, 1);
            }
            else {
                registerTable.setValueAt("00", i, 1);
            }
 
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AssembleButton;
    private javax.swing.JPanel callStackPanel;
    private javax.swing.JScrollPane consoleScrollPane;
    private javax.swing.JTextArea consoleTextArea;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JScrollPane disassScrollPane;
    private javax.swing.JButton disassembleButton;
    private javax.swing.JTextArea disassembledTextArea;
    private javax.swing.JScrollPane errorPane;
    private javax.swing.JTextArea errors;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JComboBox fontNamejComboBox;
    private javax.swing.JLabel fontNamejLabel;
    private javax.swing.JLabel fontSize;
    private javax.swing.JComboBox fontSizejComboBox;
    private javax.swing.JPanel helpPanel;
    private javax.swing.JTabbedPane helpTabbedPane;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JEditorPane languageReferenceEditorPane;
    private javax.swing.JScrollPane languageReferenceScrollPane;
    private javax.swing.JPanel machinePanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openMachineMenuItem;
    private javax.swing.JMenuItem openSourceMenuItem;
    private javax.swing.JScrollPane ramScrollPane;
    private javax.swing.JTable ramTable;
    private javax.swing.JScrollPane registerScrollPane;
    private javax.swing.JTable registerTable;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton runButton;
    private javax.swing.JMenuItem saveMachineMenuItem;
    private javax.swing.JMenuItem saveSourceMenuItem;
    private javax.swing.JScrollPane specialScrollPane;
    private javax.swing.JTable specialTable;
    private javax.swing.JComboBox speedComboBox;
    private javax.swing.JLabel speedLabel;
    private machine.view.StackPanel stackPanel;
    private javax.swing.JScrollPane stackScrollPane;
    private javax.swing.JButton stepButton;
    private javax.swing.JButton stopButton;
    private javax.swing.JEditorPane syntaxEditorPane;
    private javax.swing.JScrollPane syntaxScrollPane;
    private javax.swing.JTabbedPane tabbedPane;
    private machine.view.TextEditor textEditor;
    // End of variables declaration//GEN-END:variables
}
