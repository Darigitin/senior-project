/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package system.view;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author mb936840
 edited by Mariela Barrera
 This is a stripped version of the SystemView of the karel project.
 It basically exists to make sure the machine will run without an error.
 */
public class SystemView {
    public static final int CODE_TAB_INDEX = 0;
    public static final Font DEFAULT_FONT = new Font("Tahoma", Font.PLAIN, 14);
    private javax.swing.JEditorPane languageReferencePane;
    private javax.swing.JScrollPane languageReferenceScrollPane;

public SystemView() {
    addLanguageReference();
}

private void addLanguageReference() {
        languageReferencePane.setContentType("text/html");
        InputStream inputStream =  SystemView.class.getResourceAsStream("/html/languageReference.html");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            languageReferencePane.setText(sb.toString());
        } catch (IOException ex) {
            languageReferencePane.setText("Could not load language reference.");
        }
        try {
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(SystemView.class.getName()).log(Level.SEVERE, null, ex);
        }
        languageReferencePane.setEditable(false);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                languageReferenceScrollPane.getVerticalScrollBar().setValue(0);
            }
        });
    }


private void initComponents() {
languageReferenceScrollPane = new javax.swing.JScrollPane();
languageReferenceScrollPane.setViewportView(languageReferencePane);
    }
}