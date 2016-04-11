package machine.view;

import system.view.CompoundUndoManager;
import system.view.TextLineNumber;
import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;
import java.util.Arrays;
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

/**
 * Change Log
 *
 * mv935583 -> Matthew Vertefeuille
 * # author   - date:     description
 * 1 mv935583 - 03/18/16: Implemented necessary function calls in order to
 *                         invoke syntax highlighting
 * 2 mv935583 - 04/11/16: Added functions and code necessary to uncouple 
 *                        font name and size changing and to implement color theme
 *                        changing.
 */
public class TextEditor extends JScrollPane implements Serializable {

    private final Color textColor = Color.BLACK;    //BEGIN CHANGE LOG: 2
    private final Color reservedTextColor = Color.BLUE;
    private final Color primitiveTextColor = Color.RED;
    private final Color numericTextColor;
    private final Color[] colorArray;
    private static final int FONTSTYLE = Font.PLAIN;
    private static String fontName = "Tahoma";
    private static int fontSize = 14;   //END CHANGE LOG: 2
    
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
    public static final int CODE_TAB_INDEX = 0;
    public final Font DEFAULT_FONT = new Font(fontName, FONTSTYLE, fontSize); //CHANGE LOG: 2
    
    public TextEditor() {
        this.numericTextColor = Color.getHSBColor((float) 0.11,(float) .80,(float) 0.90);   //CHANGE LOG: 2
        this.colorArray = new Color[]{textColor, reservedTextColor, primitiveTextColor, numericTextColor};  //CHANGE LOG: 2
        
        verticalBar = super.getVerticalScrollBar();
        
        textPane = new JTextPane();
        doc = new KarelDocument();
        textPane.setDocument(doc);
        
        super.setViewportView(textPane);
        tln = new TextLineNumber(textPane);
        super.setRowHeaderView( tln );
        
        tln.setUpdateFont(true);
        textPane.setFont(DEFAULT_FONT);
        
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
    
    public void setBackGround(String backGroundColor){      //BEGIN CHANGE LOG: 2
        
        if (backGroundColor.equals("BLACK")){
            textPane.setBackground(Color.BLACK);
        }
        else if (backGroundColor.equals("WHITE")){
            textPane.setBackground(Color.WHITE);
        }
        else if (backGroundColor.equals("LIGHT-YELLOW")){
            float[] hsbvals = new float[3];
            hsbvals = Color.RGBtoHSB(246, 242, 124, hsbvals);
            Color backGround = Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]);
            textPane.setBackground(backGround);
            hsbvals = Color.RGBtoHSB(71, 225, 12, hsbvals);
            colorArray[3] = Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]);
                
        }
        setTextPaneFont(textPane.getFont());
        
    }
    
    public void setTextColor(String textColor){
        float[] hsbvals = new float[3];
        switch (textColor){
            case "GREEN":
                hsbvals = Color.RGBtoHSB(71, 225, 12, hsbvals);
                colorArray[0] = Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]);
                colorArray[1] = Color.BLUE;
                colorArray[2] = Color.RED;
                colorArray[3] = Color.getHSBColor((float) 0.11,(float) .80,(float) 0.90);
                break;
            case "BLUE":
                colorArray[0] = Color.BLUE;
                hsbvals = Color.RGBtoHSB(87, 54, 131, hsbvals);
                colorArray[1] = Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]);
                colorArray[2] = Color.RED;
                colorArray[3] = Color.getHSBColor((float) 0.11,(float) .80,(float) 0.90);
                break;
            case "WHITE":
                colorArray[0] = Color.WHITE;
                colorArray[1] = Color.BLUE;
                colorArray[2] = Color.RED;
                colorArray[3] = Color.getHSBColor((float) 0.11,(float) .80,(float) 0.90);
                break;
            case "RED":
                colorArray[0] = Color.RED;
                colorArray[1] = Color.BLUE;
                hsbvals = Color.RGBtoHSB(87, 54, 131, hsbvals);
                colorArray[2] = Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]);
                colorArray[3] = Color.getHSBColor((float) 0.11,(float) .80,(float) 0.90);
                break;
            default: //BLACK
                colorArray[0] = Color.BLACK;
                colorArray[1] = Color.BLUE;
                colorArray[2] = Color.RED;
                colorArray[3] = Color.getHSBColor((float) 0.11,(float) .80,(float) 0.90);
                break;
        }
    }   //END CHANGE LOG: 2
    
    public void setFontName(String fontName){
        TextEditor.fontName = fontName;
        updateFont();
    }
    
    public void setFontSize(int fontSize){
        TextEditor.fontSize = fontSize;
        updateFont();
    }
    
    public int getFontSize(){
        return TextEditor.fontSize;
    }
    
    private void updateFont(){
        
        Font newFont = new Font(fontName, FONTSTYLE, fontSize);
        textPane.setFont(newFont);
        
    }   //END CHANGE LOG: 2
    
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
        
        if (tabIndex == CODE_TAB_INDEX) {
                new SyntaxHighlighter(textPane, false, 0, doc.getLength(), colorArray).execute();   //CHANGE LOG: 2
        }
        else {
                new SyntaxHighlighter(textPane, true, 0, doc.getLength(), colorArray).execute();    //CHANGE LOG: 2
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
            if (tabIndex == CODE_TAB_INDEX) {    //Changelog Begin: 1
                new SyntaxHighlighter(textPane, false, offs, str.length(), colorArray).execute();   //CHANGE LOG: 2
            }
            else {
                new SyntaxHighlighter(textPane, true, offs, str.length(), colorArray).execute();    //CHANGE LOG: 2
            }   //Changelog End: 1
            
                        
            resetHighlighter();
        }
        
        @Override
        public void remove(int offset, int length) throws BadLocationException {
            
            super.remove(offset, length);
            if (tabIndex == CODE_TAB_INDEX) {    //Changelog Begin: 1
                new SyntaxHighlighter(textPane, false, offset, 0, colorArray).execute();    //CHANGE LOG: 2
            }
            else {
                new SyntaxHighlighter(textPane, true, offset, 0, colorArray).execute(); //CHANGE LOG: 2
            }   //Changelog End: 1
            

            resetHighlighter();
        }
    }
}

