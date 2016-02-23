package system.view;

import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;
import javax.swing.JMenuItem;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.Action;

/**
 *
 * @author Ryan Ball
 */
public class TextEditor extends JScrollPane implements Serializable {
    
    private JTextPane textPane;
    private KarelDocument doc;
    private TextLineNumber tln;
    
    public static final Color ERROR_COLOR = new Color(255,50,50);
    public static final Color EXECUTION_COLOR = new Color(0,220,0);
    
    private final JScrollBar verticalBar;
    private JMenuItem saveMenuItem;
    private CompoundUndoManager undo;
    
    private JTabbedPane tabbedPane;
    private int tabIndex;
    
    private int highlightStart = 0;
    private int highlightLength = 0;

    private boolean showChange = true;
    
    public TextEditor() {
        
        verticalBar = this.getVerticalScrollBar();
        
        textPane = new JTextPane();
        doc = new KarelDocument();
        textPane.setDocument(doc);
        
        this.setViewportView(textPane);
        tln = new TextLineNumber(textPane);
        this.setRowHeaderView( tln );
        
        tln.setUpdateFont(true);
        textPane.setFont(SystemView.DEFAULT_FONT);
        
        undo = new CompoundUndoManager(textPane);
    }
    
    public Action getUndoAction() {
        return undo.getUndoAction();
    }
    
    public Action getRedoAction() {
        return undo.getRedoAction();
    }
    
    /** Gets the text in the text pane.
     * @return  */
    public String getText() {
        return textPane.getText();
    }
    
    /** Sets the text in the text pane.
     * @param content */
    public void setText(String content) {
        textPane.setText(content);
    }
    
    public JTextPane getTextPane() {
        return textPane;
    }
    
    /** Sets the tab that this object belongs to.
     * @param tabbedPane */
    public void setTabbedPane(JTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane;
    }
    
    /** Sets the tab index that this object belongs to.
     * @param tabIndex */
    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }
    
    /** Sets the save menu item that will be updated by this editor.
     * @param saveMenuItem*/
    public void setSaveMenuItem(JMenuItem saveMenuItem) {
        this.saveMenuItem = saveMenuItem;
    }
    
    /** Sets whether or not this component is editable.
     * @param editable */
    public void setEditable(boolean editable) {
        textPane.setEditable(editable);
    }
    
    public void setTextPaneFont(Font font) {
        textPane.setFont(font);
        
        if (tabIndex == SystemView.CODE_TAB_INDEX) {
                new SyntaxHighlighter(textPane, false, 0, doc.getLength()).execute();
        }
        else {
            new SyntaxHighlighter(textPane, true, 0, doc.getLength()).execute();
        }
    }
    
    
    /** Highlights a given line.
     * 
     * @param lineNum   line to highlight
     * @param color     highlight color
     */
    public void highlightLine(int lineNum, Color color) {
        
        undo.removeListeners();
        showChange = false;
        lineNum--;
        
        if (highlightStart != -1 && highlightLength != -1) {
            SimpleAttributeSet original = new SimpleAttributeSet();
            StyleConstants.setBackground(original, Color.white);
            doc.setCharacterAttributes(highlightStart, highlightLength, original, false);
        }
        this.repaint();
        
        // scroll to line if outside viewport
        int pHeight = this.getHeight();
        int fontHeight = textPane.getGraphics().getFontMetrics().getHeight();
        int lineOffset = fontHeight * (lineNum);
        if ((lineOffset - fontHeight) < verticalBar.getValue()
                || (lineOffset + fontHeight) > verticalBar.getValue() + pHeight) {
            verticalBar.setValue(lineOffset);
        }

        Element element = doc.getDefaultRootElement().getElement(lineNum);
        highlightStart = element.getStartOffset();
        highlightLength = element.getEndOffset() - highlightStart;
        SimpleAttributeSet sas = new SimpleAttributeSet();
        StyleConstants.setBackground(sas, color);
        doc.setCharacterAttributes(highlightStart, highlightLength, sas, false);
        
        showChange = true;
        undo.addListeners();
    }
    
    public void resetHighlighter() {
        undo.removeListeners();
        showChange = false;
        
        SimpleAttributeSet original = new SimpleAttributeSet();
        StyleConstants.setBackground(original, Color.white);
        doc.setCharacterAttributes(highlightStart, highlightLength, original, false);
        highlightStart = -1;
        highlightLength = -1;
        
        showChange = true;
        undo.addListeners();
    }
    
    /** Shows that the editor has been changed by placing an asterisk next to the
    *  tab title. Also enables the save menu item.
    */
    private void showChangeIndicator() {
        
        String tabTitle = tabbedPane.getTitleAt(tabIndex);
        if (!tabTitle.startsWith("*")) {
            tabbedPane.setTitleAt(tabIndex, "*" + tabTitle.replaceFirst("\\*", ""));
            saveMenuItem.setEnabled(true);
        }
    }
    
    /** Custom Document that changes all tabs to four spaces. */
    private class KarelDocument extends DefaultStyledDocument {
           
        public KarelDocument() {
            addDocumentListener(new TextEditorListener());
        } 
        
        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            str = str.replaceAll("\t", "    ");
            super.insertString(offs, str, a);
            
            if (tabIndex == SystemView.CODE_TAB_INDEX) {
                new SyntaxHighlighter(textPane, false, offs, str.length()).execute();
            }
            else {
                new SyntaxHighlighter(textPane, true, offs, str.length()).execute();
            }
            
            resetHighlighter();
        }
        
        @Override
        public void remove(int offset, int length) throws BadLocationException {
            
            super.remove(offset, length);
            
            if (tabIndex == SystemView.CODE_TAB_INDEX) {
                new SyntaxHighlighter(textPane, false, offset, 0).execute();
            }
            else {
                new SyntaxHighlighter(textPane, true, offset, 0).execute();
            }
            
            resetHighlighter();
        }
        
        /** A class that listens for changes in a document and notifies a TextEditor
         *  object when a change has been made.
         */
        private class TextEditorListener implements DocumentListener {
        
            @Override
            public void insertUpdate(DocumentEvent e) {
                showChangeIndicator();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                showChangeIndicator();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (showChange) {
                    showChangeIndicator();
                }
            }
        }
    }
}

