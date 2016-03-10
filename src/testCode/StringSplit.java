/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testCode;

import java.util.Arrays;

/**
 *
 * @author jl948836
 */
public class StringSplit {
    
    
    public StringSplit(){
        String text = "##########\n# ######\n# ##### #\ndfsdf #\ndsfsdf#sfsdf#";
        String[] lines = text.split("\n");
    }
    
    public static void main(String[] args){
        StringSplit stringSplit = new StringSplit();
        
        String text = "##########\n# ######\n# ##### #\ndfsdf #\ndsfsdf#sfsdf#";
        String[] lines = text.split("\n");
        System.out.println(Arrays.toString(lines));
        for (String line : lines) {
            String[] token = line.split("#");
            System.out.print(token.length + " ");
            System.out.println(Arrays.toString(token));
        }
    }
}
