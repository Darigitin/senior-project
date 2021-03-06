/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Program: Documentation Panel
 * 
 * Purpose: Creates a custom panel with tabbed panels inside it to display the
 *          Language Reference and Machine Syntax Documentation.
 *          Uses a GridBag Layout.
 * 
 * @author: Jordan Lescallette
 * 
 * date/ver: 03/18/16 1.0.0
 */
package machine.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author jl948836
 */
public final class DocumentationPanel extends javax.swing.JPanel {
    
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.add(new DocumentationPanel());
        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     * Constructs a DocumentationPanel()
     */
    public DocumentationPanel() {
        initComponents();
        
        addLanguageReference();
        addSyntaxReference();
        addExamples();
        addAboutWALL();
    }

    /**
     * Adds the Language Reference html to the Language Reference tab
     */
    public void addLanguageReference() {
        languageRefEditorPane.setContentType("text/html");
        InputStream inputStream =  DocumentationPanel.class.getResourceAsStream("/html/languageReference(1.5).html");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            languageRefEditorPane.setText(sb.toString());
        } catch (IOException ex) {
            languageRefEditorPane.setText("Could not load language reference.");
        }
        try {
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(DocumentationPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        languageRefEditorPane.setEditable(false);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                languageRefScrollPane.getVerticalScrollBar().setValue(0);
            }
        });
    }
    
    /**
     * Adds the Machine Syntax html to the Syntax tab.
     */
    public void addSyntaxReference() {
        syntaxEditorPane.setContentType("text/html");
        InputStream inputStream =  DocumentationPanel.class.getResourceAsStream("/html/machineSyntax(1.5).html");
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
            Logger.getLogger(DocumentationPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        syntaxEditorPane.setEditable(false);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                syntaxScrollPane.getVerticalScrollBar().setValue(0);
            }
        });
    }
    
    
    public void addExamples() {
        examplesEditorPane.setContentType("text/html");
        InputStream inputStream =  DocumentationPanel.class.getResourceAsStream("/html/examples.html");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            examplesEditorPane.setText(sb.toString());
        } catch (IOException ex) {
            examplesEditorPane.setText("Could not load language reference.");
        }
        try {
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(DocumentationPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        examplesEditorPane.setEditable(false);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                examplesScrollPane.getVerticalScrollBar().setValue(0);
            }
        });
    }
    
    
    public void addAboutWALL() {
        aboutWallEditorPane.setContentType("text/html");
        InputStream inputStream =  DocumentationPanel.class.getResourceAsStream("/html/aboutWALL.html");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            aboutWallEditorPane.setText(sb.toString());
        } catch (IOException ex) {
            aboutWallEditorPane.setText("Could not load language reference.");
        }
        try {
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(DocumentationPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        aboutWallEditorPane.setEditable(false);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                aboutWallScrollPane.getVerticalScrollBar().setValue(0);
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
        java.awt.GridBagConstraints gridBagConstraints;

        documentationTabbedPane = new javax.swing.JTabbedPane();
        syntaxPanel = new javax.swing.JPanel();
        syntaxScrollPane = new javax.swing.JScrollPane();
        syntaxEditorPane = new javax.swing.JEditorPane();
        languageRefPanel = new javax.swing.JPanel();
        languageRefScrollPane = new javax.swing.JScrollPane();
        languageRefEditorPane = new javax.swing.JEditorPane();
        examplesPanel = new javax.swing.JPanel();
        examplesScrollPane = new javax.swing.JScrollPane();
        examplesEditorPane = new javax.swing.JEditorPane();
        aboutWallPanel = new javax.swing.JPanel();
        aboutWallScrollPane = new javax.swing.JScrollPane();
        aboutWallEditorPane = new javax.swing.JEditorPane();

        setLayout(new java.awt.GridBagLayout());

        syntaxPanel.setLayout(new java.awt.GridBagLayout());

        syntaxEditorPane.setContentType("text/html"); // NOI18N
        syntaxScrollPane.setViewportView(syntaxEditorPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        syntaxPanel.add(syntaxScrollPane, gridBagConstraints);

        documentationTabbedPane.addTab("Syntax", syntaxPanel);

        languageRefPanel.setLayout(new java.awt.GridBagLayout());

        languageRefEditorPane.setContentType("text/html"); // NOI18N
        languageRefScrollPane.setViewportView(languageRefEditorPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        languageRefPanel.add(languageRefScrollPane, gridBagConstraints);

        documentationTabbedPane.addTab("Language Reference", languageRefPanel);

        examplesPanel.setLayout(new java.awt.GridBagLayout());

        examplesScrollPane.setViewportView(examplesEditorPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        examplesPanel.add(examplesScrollPane, gridBagConstraints);

        documentationTabbedPane.addTab("Examples", examplesPanel);

        aboutWallPanel.setLayout(new java.awt.GridBagLayout());

        aboutWallScrollPane.setViewportView(aboutWallEditorPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        aboutWallPanel.add(aboutWallScrollPane, gridBagConstraints);

        documentationTabbedPane.addTab("About WALL", aboutWallPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(documentationTabbedPane, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane aboutWallEditorPane;
    private javax.swing.JPanel aboutWallPanel;
    private javax.swing.JScrollPane aboutWallScrollPane;
    private javax.swing.JTabbedPane documentationTabbedPane;
    private javax.swing.JEditorPane examplesEditorPane;
    private javax.swing.JPanel examplesPanel;
    private javax.swing.JScrollPane examplesScrollPane;
    private static javax.swing.JEditorPane languageRefEditorPane;
    private javax.swing.JPanel languageRefPanel;
    private static javax.swing.JScrollPane languageRefScrollPane;
    private static javax.swing.JEditorPane syntaxEditorPane;
    private javax.swing.JPanel syntaxPanel;
    private static javax.swing.JScrollPane syntaxScrollPane;
    // End of variables declaration//GEN-END:variables
}
