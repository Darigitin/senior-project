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
public class SyntaxHighlighter extends SwingWorker<Void,Object> {
    
    private StyledDocument doc;
    private int fontSize;
    private boolean commentsOnly;
    private int docOffset;
    private int length;
    
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

                    switch(word) {
                        case "if": case "else": case "elif":
                        case "while":
                        case "do":
                        case "def":
                            textStyle = style.addAttribute(textStyle,StyleConstants.Foreground, Color.blue);
                            doc.setCharacterAttributes(start, end - start, textStyle, false);
                            break;
                        default:
                            if (isPrimitive(word)) {
                                textStyle = style.addAttribute(textStyle,StyleConstants.Foreground, Color.red);
                                doc.setCharacterAttributes(start, end - start, textStyle, false);
                            }
                            else if (isTest(word)) {
                                textStyle = style.addAttribute(textStyle,StyleConstants.Foreground, Color.CYAN);
                                doc.setCharacterAttributes(start, end - start, textStyle, false);
                            }
                            // match integer literals
                            else if (word.matches("[0-9]+")) {
                                textStyle = style.addAttribute(textStyle,StyleConstants.Foreground, Color.ORANGE);
                                doc.setCharacterAttributes(start, end - start, textStyle, false);
                            }
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
    
    private boolean isPrimitive(String str) {
        String[] primitives = {"move", "turnleft", "pickbeeper", "putbeeper", "turnoff"};
        for (int i = 0; i < primitives.length; i++) {
            if (str.equals(primitives[i])) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isTest(String str) {
        try {
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
