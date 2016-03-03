package machine.view;

import system.view.SystemView;
import system.view.CompoundUndoManager;
import system.view.SyntaxHighlighter;
import system.view.TextLineNumber;
import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.Action;
import javax.swing.text.Document;

/**
 *
 * @author Ryan Ball
 */
public class TextEditor extends JScrollPane implements Serializable {
    
    private final JTextPane textPane;
    private final KarelDocument doc;
    private final TextLineNumber tln;
    
    public static final Color ERROR_COLOR = new Color(255,50,50);
    public static final Color EXECUTION_COLOR = new Color(0,220,0);
    
    private final JScrollBar verticalBar;
    private final CompoundUndoManager undo;
    
    private int tabIndex;
    
    private int highlightStart = 0;
    private int highlightLength = 0;
    
    public TextEditor() {
        
        verticalBar = super.getVerticalScrollBar();
        
        textPane = new JTextPane();
        doc = new KarelDocument();
        textPane.setDocument(doc);
        
        super.setViewportView(textPane);
        tln = new TextLineNumber(textPane);
        super.setRowHeaderView( tln );
        
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
    
    public Document getDocument() {
        return doc;
    }
    
    /** Sets the tab index that this object belongs to.
     * @param tabIndex */
    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
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
        
        undo.addListeners();
    }
    
    public void resetHighlighter() {
        undo.removeListeners();
        
        SimpleAttributeSet original = new SimpleAttributeSet();
        StyleConstants.setBackground(original, Color.white);
        doc.setCharacterAttributes(highlightStart, highlightLength, original, false);
        highlightStart = -1;
        highlightLength = -1;
        
        undo.addListeners();
    }
    
    /** Custom Document that changes all tabs to four spaces. */
    private class KarelDocument extends DefaultStyledDocument {
        
        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            str = str.replaceAll("\t", "    ");
            super.insertString(offs, str, a);
                        
            resetHighlighter();
        }
        
        @Override
        public void remove(int offset, int length) throws BadLocationException {
            
            super.remove(offset, length);

            resetHighlighter();
        }
    }
}

