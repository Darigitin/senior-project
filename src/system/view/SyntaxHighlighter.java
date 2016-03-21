/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package system.view;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.SwingWorker;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.JTextPane;

/**
 *
 * @author Ryan Ball
 */

/**
 * Change Log
 *
 * mv935583 -> Matthew Vertefeuille
 * # author   - date:     description
 * 01 mv935583 - 03/18/16: Converted karel syntax highlighting in order to 
 *                         properly catch all needed highlighting in the WAL
 *                         instead.
 */
public class SyntaxHighlighter extends SwingWorker<Void,Object> {
    
    private final StyledDocument doc;
    private final int fontSize;
    private final boolean commentsOnly;
    private final int docOffset;
    private final int length;
    
    public SyntaxHighlighter(JTextPane textPane, boolean commentsOnly, int offset, int length) {
        this.doc = textPane.getStyledDocument();
        this.fontSize = textPane.getFont().getSize();
        this.commentsOnly = commentsOnly;
        this.docOffset = offset;
        this.length = length;
    }

    @Override
    protected Void doInBackground() throws Exception {
        if (commentsOnly) {
            highlightCommentsOnly();
        }
        else {
            highlightAll();  
        }
        
        return null;
    }
    
    private void highlightCommentsOnly() {
        
        StyleContext style = StyleContext.getDefaultStyleContext();
        AttributeSet textStyle = style.addAttribute(style.getEmptySet() ,StyleConstants.FontSize, fontSize);

        try {
            String text = doc.getText(0, doc.getLength());
            int lineStart = findLineStart(text, docOffset);
            int lineEnd = findLineEnd(text, docOffset);
            Matcher matcher = Pattern.compile("[ \t]*#[^\\n]*|.*").matcher(text.substring(lineStart, lineEnd));

            while (matcher.find()) {
                
                int start = matcher.start() + lineStart;
                int end = matcher.end() + lineStart;
                    
                String word = text.substring(start,end);
                
                // highlight comment
                if (word.matches("[ \t]*#[^\\n]*")) {
                    textStyle = style.addAttribute(textStyle,StyleConstants.Foreground, Color.lightGray);
                    doc.setCharacterAttributes(start, end - start, textStyle, false);
                }
                else {
                    textStyle = style.addAttribute(textStyle,StyleConstants.Foreground, Color.black);
                    doc.setCharacterAttributes(start, end - start, textStyle, false);
                }
            }
        } catch (BadLocationException ex) {
            Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void highlightAll() {
            StyleContext style = StyleContext.getDefaultStyleContext();
            
            try {
                String text = doc.getText(0, doc.getLength());
                int lineStart = findLineStart(text, docOffset);
                int lineEnd = findLineEnd(text, docOffset);
                
                Matcher matcher = Pattern.compile("([ \t]*#[^\\n]*)|\\w+|[\\W&&\\S]+").matcher(text.substring(lineStart, lineEnd));
                
                // highlight instructions
                while (matcher.find()) {
                    AttributeSet textStyle = style.addAttribute(style.getEmptySet() ,StyleConstants.FontSize, fontSize);
                    
                    int start = matcher.start() + lineStart;
                    int end = matcher.end() + lineStart;
                    
                    String word = text.substring(start,end);
                    String toUpperCase = word.toUpperCase();    //Changelog Begin: 1

                    if(isReservedWord(toUpperCase)){
                            textStyle = style.addAttribute(textStyle,StyleConstants.Foreground, Color.blue);
                            doc.setCharacterAttributes(start, end - start, textStyle, false);
                    }
                    else if (isPrimitive(toUpperCase)) {
                                textStyle = style.addAttribute(textStyle,StyleConstants.Foreground, Color.red);
                                doc.setCharacterAttributes(start, end - start, textStyle, false);
                    }
                    // match integer literals
                    else if (isInt(toUpperCase) || isHex(toUpperCase)) {
                        textStyle = style.addAttribute(textStyle,StyleConstants.Foreground, Color.cyan);
                        doc.setCharacterAttributes(start, end - start, textStyle, false);
                    }   //Changelog End: 1
                    // match comment lines
                    else if (word.matches("[ \t]*#[^\\n]*")) {
                        textStyle = style.addAttribute(textStyle,StyleConstants.Foreground, Color.lightGray);
                        doc.setCharacterAttributes(start, end - start, textStyle, false);
                    }
                    else {
                        textStyle = style.addAttribute(textStyle,StyleConstants.Foreground, Color.black);
                        doc.setCharacterAttributes(start, end - start, textStyle, false);
                    }  
                    }
                
            } catch (BadLocationException ex) {
                Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    private int findLineStart(String text, int offset) { 
        
        --offset;
        
        while (offset >= 0 && text.charAt(offset) != '\n') {
            --offset;
        }
        return ++offset;
    }
    
    private int findLineEnd(String text, int offset) {
        
        offset += length;
        
        while (offset < doc.getLength() && text.charAt(offset) != '\n') {
            ++offset;
        }
        return offset;
    }
    
    private boolean isReservedWord(String str){ //Changelog Begin: 1
        final String[] reservedWords = {"SIP", "ORG", "BSS", 
            "DB", "EQU", "LOAD", "STORE", "MOVE", "ADD", "CALL", "RET", 
            "SCALL", "SRET", "PUSH", "POP", "OR", "AND", "XOR", 
            "ROR", "JMPEQ", "JMP", "HALT", "ILOAD", "ISTORE",
            "RLOAD", "RSTORE", "JMPLT"};
        for (String reservedWord : reservedWords) {
            if (reservedWord.equals(str)) {
                return true;
            }
        }
        return false;
        
    }   //Changelog End: 1
    
    private boolean isPrimitive(String str) {   //Changelog Begin: 1
        String[] primitives = {"R0","R1","R2","R3","R4","R5","R6","R7","R8",
            "R9","RA","RB","RC","RD","RE","RF", "RSP", "RBP"};
        for (String primitive : primitives) {
            if (str.equals(primitive)) {
                return true;
            }
        }
        return false;   //Changelog End: 1
    }
    
    private boolean isHex(String number) {  //Changelog Begin: 1
        int len = number.length();
        if (len > 4 || len < 3) {
            return false;
        } else {
            if (len == 4) {
                if (number.substring(0, 2).equalsIgnoreCase("0x")
                    && number.substring(2, 4).toUpperCase().matches("[0-9A-F]{2}")) {
                    return true;
                }
            } else {
                if (number.substring(0, 2).equalsIgnoreCase("0x")
                    && number.substring(2, 3).toUpperCase().matches("[0-9A-F]")) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean isInt(String number) {
        if (number.length() == 0 || number.length() > 4) {
            return false;
        } 
        else if (number.matches("[0-9][0-9]{0,2}") || number.matches("-[0-9][0-9]{0,2}")) {
            return true;
        }
        return false;
    }   //Changelog End: 1
}
