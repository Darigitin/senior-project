/**
 * Program: Assembler.java
 *
 * Purpose: This is our Assembler class. It takes the source from the editor 
 *          view and sends it through a two pass parser. Pass one parses the 
 *          text, splitting everything into labels, operations, and comments. 
 *          Pass two takes this information.
 *
 * @author:
 *
 * date/ver: 
 */

/**
 * Change Log
 *
 * mv935583 -> Matthew Vertefeuille
 * jl948836 -> Jordan Lescallatte
 * # author   - date:     description
 * 1 jl948836 - 02/11/16: Comment character changes from ";" to "#"
 * 2 jl948836 - 02/18/16: Add Spaces back into String, that regEx parses out
 *                        Adjust String size for quotation (") characters
 * 3 jl948836 - 02/18/16: Added -1 to .length(), to parse off "]" at the end of
 *                        RBP, RSP register aliases
 * 
 * 8 jl948836 - 03/05/16: Comment Character overflow fixed; Error occurred when
 *                        comment character was the only character on a line
 *                        (excluding \n); check for array length to fix
 * 9 jl948836 - 03/05/16: Overload on return now does allows user full range of
 *                        0-Max for SP increment (got rid of "1" + ...)
 * 4 mv935583 - 02/23/2016: Fixed db so it does not append a null 
 *                                   character at the end
 * 5 mv935583 - 02/23/2016: rload now requires hex offset to be 0x0N 
 *                                   instead of 0xN
 * 6 mv935583 - 02/23/2016: jmpeq now works with R0 instead of just r0
 */

package machine.presenter;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Assembler {

    private final MachineController controller;
    private final ArrayList<String> byteCode = new ArrayList<>();
    private final ArrayList<String> errorList = new ArrayList<>();
    private final ArrayList<String> logList = new ArrayList<>();
    private final ArrayList<String> codeList = new ArrayList<>();
    private final ArrayList<String> defList = new ArrayList<>();
    private final ArrayList<String> refList = new ArrayList<>();
    private final ArrayList<String> memMatList = new ArrayList<>();
    private final ArrayList<String> labelList = new ArrayList<>();
    private final static String[] pseudoOps = {"SIP", "ORG", "BSS", "DB"};
    private final static String[] operations = 
        {"LOAD", "STORE", "MOVE", "ADD", "CALL", "RET",
         "SCALL", "SRET", "PUSH", "POP", "OR", "AND", "XOR",
         "ROR", "JMPEQ", "JMP", "HALT", "ILOAD", "ISTORE",
         "RLOAD", "RSTORE", "JMPLT"}; 
    BufferedWriter logfile;
    String[] codes;
    String[] labels;
    String[] tempMem;
    HashMap<String, Integer> labelMap = new HashMap<>(256);
    String labelAppears;
    String SIP = "00";
    int errorCount = 0;
    int codeLines = 1;
    int labelAppearsLine;
    

    /**
     * Creates a new controller object and sets the memory table all to zeroes
     *
     * @param controller
     */
    public Assembler(MachineController controller) {
        this.controller = controller;
	//TODO: add trim() to Labels and Codes

        // initialize tempMem to hold our passTwo artifacts
        tempMem = new String[256];
        for (int i = 0; i < 256; i++) {
            tempMem[i] = "00";
        }
    }

    /**
     * Entry point into this class - called by controller
     *
     * @param text  - source code from editor view
     * @return assembled byte code
     */
    public ArrayList<String> parse(String text) {
        passOne(text);
        //createLogfile();
       
        if (errorList.isEmpty()) {
            controller.setEditorErrorVisible(false);
        } else {
            displayErrors();
        }
        return byteCode;
    }

    /**
     * Displays the errors in the Editor whenever the user wants to compile
     * assembly code
     */
    private void displayErrors() {
        controller.setEditorErrors(errorList);
        controller.setEditorErrorVisible(true);
    }

    /**
     * Pass One - parses text and splits everything into labels, codes, and
     * comments, then calls Pass Two
     *
     * @param text
     */
    private void createLogfile() {
        //Creates a logfile and files it with assembler information
        //Programmer: Mariela Barrera
        if (new File ("logfile.txt").exists()){
            System.out.print("It exsists");
            new File ("logfile.txt").setWritable(true);
        }
        String errorCount = "0";
        Date date = new Date();
        SimpleDateFormat simpDate = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        int refLineCount;
        int DefLineCount;
        int labelLineNum = 1;
        
       
        try 
        {
            logfile = new BufferedWriter(new FileWriter("logfile.txt"));
        
         
            logfile.append("************Logfile************");
            logfile.newLine();
            logfile.newLine();
            logfile.append(simpDate.format(date));
            logfile.newLine();
            logHeaders("PROGRAM");
            for (String codeList1 : codeList) {
                logfile.append(codeLines +" " + codeList1);
                logfile.newLine(); 
                codeLines++;
            }  
            //This loop prints information about special cases
            for (String logList1 : logList) {
                logfile.newLine();
                logfile.append(logList1);
                logfile.newLine();
            }
            logHeaders("Printing through Label Map");
            logfile.append("*************LABELS*************");
            logfile.newLine();
            logfile.newLine();
       
            for (String label : labels) {
                if (label != null) {
                    logfile.append(label);
                    logfile.newLine();
                    labelMatching(label);
                    labelLineNum++;
                } else {
                    labelLineNum++; 
                }
            }
         

            logfile.newLine();      
            //This loop prints the defined information
            for (String defList1 : defList) {
                logfile.append(defList1);
                logfile.newLine();
            }
            logfile.newLine();
            //This loop prints the reference information
            for (String refList1 : refList) {
                logfile.append(refList1);
                logfile.newLine();
            }
                
            logHeaders("MEMORY");
            //This loop prints the memory matrix    
            for (int i = 0; i < tempMem.length; i++) {
                if ((i+1) % 16 == 0) {
                    logfile.append(memMatList.get(i));
                    logfile.newLine();
                } else {
                     logfile.append(memMatList.get(i) + ", ");
                }
            }
               
            logHeaders("ERROR REPORT");
            
            //This loop prints the error report
            if (!errorList.isEmpty()){
                for (String errorList1 : errorList) {
                    logfile.append(errorList1);
                    logfile.newLine();
                    logfile.newLine();
                    errorCount = Integer.toString(errorList.size());
                    logfile.append("Error Count: "+errorCount);
               }
            } else{
                    logfile.append("Error Count: "+errorCount);  
            }
            logfile.close();
        }
       
        catch (Exception e) {}
    }
    
    /**
     * 
     * @param labelAppears 
     */
    private void labelMatching(String labelAppears) {
   //Programmer: Mariela Barrera
   //Finds where all the labels are referenced
        for (int i =0; i<codeList.size(); i++){ 
            if (codeList.get(i).contains(labelAppears)){
                labelAppearsLine = i+1;
                try{
                    logfile.append("appears in line " +labelAppearsLine);
                    logfile.newLine();
                }
                catch (Exception e){}
            }
        }
    }
    
    /**
     * 
     * @param header 
     */
    private void logHeaders(String header) {
    //Creates the logfile headers.    
    //Programmer: Mariela Barrera
        try{ 
            logfile.newLine();
            logfile.append( "*******************************");
            logfile.newLine();
            logfile.append(header);
            logfile.newLine();
            logfile.append( "*******************************");
            logfile.newLine();         
            logfile.newLine();
        }
        catch (Exception e){}
    }
    
    /**
     * 
     */
    public void displayLog() {
        //Displays the logfile as an automated pop up.
        //Programmer: Mariela Barrera
        new File ("logfile.txt").setReadOnly();
        try{
            Desktop.getDesktop().open(new File ("logfile.txt"));
        }
        catch (Exception e){}
    }
    
    /**
     * 
     * @param text 
     */
    private void passOne(String text) {
        String[] lines = text.split("\n");
        int lineCount = lines.length;
        codes = new String[lineCount];
        labels = new String[lineCount];
        String[] tokens;
        int i;
        // tokens[0] has code
        // tokens[1] has comments
        for (i = 0; i < lineCount; i++) { // Removes comments
            codeList.add(lines[i]);
            // CHANGE LOG BEGIN - 1
            tokens = lines[i].split("#"); //tokens[0] = code, tokens[1] = comments
            //if entire line is NOT comment && line has content (comment or code)
            if (!lines[i].trim().equals("#") && tokens.length != 0){ //CHANGE LOG - 8
                codes[i] = tokens[0].trim(); //put code in codes
            }//end if
            //CHANGE LOG END - 1
            else{ //no code
                codes[i] = "";
            }//end else
        }
        
	// codes[] now has all the code and labels
        // pull out any labels and store them
        for (i = 0; i < lineCount; i++) {
            tokens = codes[i].split(":");
            if (codes[i].contains(":") && isValidLabel(tokens[0]) // check to see if label has been used
                && tokens.length > 1) {
                codes[i] = tokens[1].trim(); // can put code on same line as label
                labels[i] = tokens[0].trim();
            } else if (codes[i].contains(":") && isValidLabel(tokens[0]) // check to see if label without
                && tokens.length == 1) {                        // code has been used
                labels[i] = tokens[0].trim();
                codes[i] = "";
            } else if (codes[i].contains(":")) {
                errorList.add("Error: Invalid label found on line " + (i + 1) + ": " + tokens[0]);
            }
        }
        
	// codes[] now has all the code without labels or comments
        // labels[] now has all the labels
        // parse pseudo-ops
        int currentLocation = 0;
        for (i = 0; i < codes.length; i++) {
            tokens = codes[i].split("\\s+"); //(?=([^\"]*\"[^\"]*\")*[^\"]*$)
            //System.out.println("After \\s+ ------->" + Arrays.toString(tokens));
            if (tokens.length > 0) { // A line with pseudo-op or operation
                if (tokens[0].toUpperCase().equals("ORG")) { // handle ORG pseudo-op
                    currentLocation = orgLocation(tokens, i);
                    mapLabel(i, currentLocation); // map label to org
                }   
                else if ((tokens[0].toUpperCase().equals("RLOAD")) && (labels[i] == null)){ //Rload without a label
                    currentLocation += 4; 
                }   
                else if (tokens[0].toUpperCase().equals("DB")) { 	// handle DB pseudo op
                    mapLabel(i, currentLocation); // map label before updating current location...
                    System.out.println("In passOne, before dbOneLocation, currentLocation = " + currentLocation);
                    logList.add("In passOne, before dbOneLocation, currentLocation = " + currentLocation);
                    
                    currentLocation += dbOneLocation(tokens, i);
                    System.out.println("In passOne, after dbOneLocation, currentLocation = " + currentLocation);
                    logList.add("In passOne, after dbOneLocation, currentLocation = " + currentLocation);
                }   
                else if ((labels[i] != null) && (tokens[0].toUpperCase().equals("RLOAD"))){ //RLOAD after a label
                    mapLabel(i, currentLocation);
                    currentLocation +=4;
                }   
                else if (labels[i] != null) { 	// we have a label on this line with operation following
                    mapLabel(i, currentLocation);	// map currentLocation to the label
                    if (tokens[0].toUpperCase().equals("BSS")) { 	// handle BSS pseudo op
                        currentLocation += bssLocation(tokens, i);
                    } else if (tokens[0] != null && isOperation(tokens[0])) { //label found, operation following
                        currentLocation += operationLocation(tokens);
                    }
                } 
                else if (tokens[0].toUpperCase().equals("BSS")) { // error, bss and no label
                    errorList.add("Error: BSS pseudo op on line " + (i + 1) + " is missing a label.");    
                } 
                else if (isOperation(tokens[0])) { 	// operation without label
                    currentLocation += operationLocation(tokens);
                }
            } 
            else if (labels[i] != null) { // we have a label with nothing following 
                mapLabel(i, currentLocation);	// map currentLocation to the label
            }
        }

        for (String key : labelMap.keySet()) {
            int value = labelMap.get(key);
        }

        printContent(labels, codes);
        passTwo();
    }

    /**
     * Pass Two - Handles RLOAD and handles the SIP pseudo op as well
     */
    private void passTwo() {

        int currentLocation = 0;
        String[] tokens;
        String bytes;
        for (int i = 0; i < codes.length; i++) {
            /*if (i==39){
                //This is a debug method, ignore this.
               
            }*/
               
            tokens = codes[i].split("\\s+");
            if (tokens.length > 0) { // A line with pseudo-op or operation
                switch (tokens[0].toUpperCase()) {
                    case "ORG":
                        // handle ORG pseudo-op
                        currentLocation = orgLocation(tokens, i);
                        break;
                    case "DB":
                        // handle DB pseudo-op
                        System.out.println("Inside passTwo, before dbTwoLocation, currentLocation = " + currentLocation);
                        logList.add("Inside passTwo, before dbTwoLocation, currentLocation = " + currentLocation);
                        currentLocation += dbTwoLocation(codes[i], i, currentLocation, i + 1);
                        System.out.println("Inside passTwo, after dbTwoLocation, currentLocation = " + currentLocation);
                        logList.add("Inside passTwo, after dbTwoLocation, currentLocation = " + currentLocation);
                        break;
                    case "BSS":
                        // handle BSS pseudo-op
                        System.out.println("In passTwo(), before bssLocation, currentLocation = " + currentLocation);
                        logList.add("In passTwo(), before bssLocation, currentLocation = " + currentLocation);
                        currentLocation += bssLocation(tokens, i);
                        System.out.println("In passTwo(), after bssLocation, currentLocation = " + currentLocation);
                        logList.add("In passTwo(), after bssLocation, currentLocation = " + currentLocation);
                        break;
                    case "SIP":
                        storeSIP(tokens, i);
                        break;
                }
            }
            if (labels[i] != null && !codes[i].trim().isEmpty() && !isPseudoOp(tokens[0])) { //has a label
                //also has a code and is not a pseudoOp
                currentLocation = labelMap.get(labels[i]);
                bytes = getByteCode(codes[i].trim(), i + 1);
                currentLocation += byteCodeInTemp(bytes, currentLocation, i);
            } 
            else if (labels[i] != null && (tokens[0].toUpperCase().equals("DB") || tokens[0].toUpperCase().equals("BSS"))) { // Special case for DB and BSS
                // do nothing.
            } 
            else if (labels[i] != null) { //There is a label with no code.
                currentLocation = labelMap.get(labels[i]);
            } 
            else if (!codes[i].trim().isEmpty() && !isPseudoOp(tokens[0])) {
                bytes = getByteCode(codes[i].trim(), i + 1);
                currentLocation += byteCodeInTemp(bytes, currentLocation, i);
            }
        }

        for (int i = 0; i < tempMem.length; i++) {
            if ((i+1) % 16 == 0) {
                System.out.print(tempMem[i]);
                memMatList.add(tempMem[i]);
                System.out.println();              
            } else {
                System.out.print(tempMem[i] + ", ");
                memMatList.add(tempMem[i]);
            
            }
        }

        byteCode.add(SIP);

        // build up bytecode for return
        byteCode.addAll(Arrays.asList(tempMem));
    }

    /**
     * handle instances of found DB pseudo op in passone.
     *
     * @param tokens
     * @return number of bytes to skip
     */
    private int passOneDB(String[] tokens) {
        int result;
        String temp = "";
        // concatenate the parameter to db
        for (int i = 1; i < tokens.length; i++) {
            temp += tokens[i];
            //CHANGE LOG BEGIN - 2
            if (i != tokens.length-1) { //re-add spaces that regex kills
                temp += " ";
            }
            //CHANGE LOG END - 2
        }
        // is the argument a string?
        if (temp.matches("[\"]{1}.*[\"]{1}") || temp.matches("[\']{1}.*[\']{1}")) {
            System.out.println("**********************************************************");
            System.out.println("dbString is: " + temp + " The size is: " + temp.length());
            //-2 for both the beginning and ending " char. See passTwo
            result = temp.length() - 2; //CHANGE LOG - 2
            System.out.println("In passOneDB, length of string is: " + result);
            logList.add("In passOneDB, length of string is: " + result);
        } else { // not a string, split on ,
            String[] args = temp.split(",");
            result = args.length;
        }
        return result;
    }

    /**
     * handle instances of BSS in pass one
     *
     * @param string
     * @return number of bytes to skip
     */
    private int passOneBSS(String value) {
        // we know string is valid hex or int
        if (isHex(value)) {
            return hexToInt(value);
        } else {
            return Integer.parseInt(value);
        }
    }

    /**
     *
     * @param tokens
     * @param i
     * @return
     */
    private int orgLocation(String[] tokens, int i) {
        int location = 0;
        if (tokens.length == 2 && isHex(tokens[1])) {
            location = hexToInt(tokens[1]);
        } else { // Invalid number of arguments or argument not hex
            errorList.add("Error: invalid argument to ORG found on line " + (i + 1));
        }
        return location;
    }

    /**
     *
     * @param tokens
     * @param i
     * @return
     */
    private int dbOneLocation(String[] tokens, int i) {
        int location = 0;
        if (tokens.length == 1) { // there must be an argument
            errorList.add("Error: Argument list for DB op is invalid on line " + (i + 1));
        } else {
            location = passOneDB(tokens);
        }
        return location;
    }

    /**
     *
     * @param tokens
     * @param i
     * @return
     */
    private int bssLocation(String[] tokens, int i) {
        int location = 0;
        if (tokens.length == 2 && (isHex(tokens[1]) || isInt(tokens[1]))) { // tokens[1] is hex or int
            location = passOneBSS(tokens[1]);
        } else {
            errorList.add("Error: BSS pseudo-op on line " + (i + 1) + " invalid argument(s)");
        }
        return location;
    }

    /**
     *
     * @param tokens
     * @return
     */
    private int operationLocation(String[] tokens) {
        int location;
        if (tokens[0].equals("RLOAD")) {
            location = 4;
        } else {
            location = 2;
        }
        return location;
    }

    /**
     *
     * @param i
     * @param currentLocation
     */
    private void mapLabel(int i, int currentLocation) {
        labelMap.put(labels[i], currentLocation);
    }

    /**
     *
     * @param tokens
     */
    private void storeSIP(String[] tokens, int i) {
        if (tokens.length == 2) {
            if (isHex(tokens[1])) {
                SIP = tokens[1].substring(2, 4);
            } 
            else if (isInt(tokens[1])) {
                SIP = intToHex((tokens[1]));
            } 
            else if (labelMap.containsKey(tokens[1])) {
                SIP = intToHex(labelMap.get(tokens[1]).toString());
            }
        } else {
            errorList.add("Error: SIP psuedo-op on line " + (i + 1) + "invalid argument");
        }
    }

    /**
     *
     * @param tokens
     * @param i
     * @return
     */
    private int dbTwoLocation(String dbString, int i, int currentLocation, int lineNum) {
        int location = 0;
        if (dbString.split("\\s+").length == 1) { // there must be an argument
            errorList.add("Error: DB pseudo op argument list is invalid on line " + (i + 1));
        } else {
            location = passTwoDB(dbString, currentLocation, lineNum);
        }
        return location;
    }

    /**
     * handle instances of found DB pseudo op in passtwo.
     *
     * @param tokens
     * @return number of bytes to skip
     */
    private int passTwoDB(String dbString, int currentLocation, int lineNum) {
        System.out.println(dbString);
        logList.add(dbString);
        int result = 0;
        String temp = "";
        dbString = dbString.substring(3, dbString.length());

        // find parameters while ignoring whitespace inbetween
        String regExPattern = "([\"]{1}[^\"]*[\"]{1})|([\\']{1}[^\\']*[\\']{1})|([\\S]+)";
        Matcher matcher = Pattern.compile(regExPattern).matcher(dbString);
        while (matcher.find()) {
            temp += dbString.substring(matcher.start(), matcher.end());
        }

        if (temp.matches("[\"]{1}[^\"]*[\"]{1}") || temp.matches("[\']{1}[^\']*[\']{1}")) {
            result = temp.length() - 1;
            for (int i = 0; i < result - 1; i++) {
                tempMem[currentLocation + i] = intToHex(Integer.toString((int) temp.charAt(i + 1)));
            }
            result -= 1; //CHANGE LOG - 4
        } else {
            String[] args = temp.split(",");
            for (String arg : args) {
                if (isHex(arg)) {
                    tempMem[currentLocation + result++] = Integer.toHexString(hexToInt(arg));
                } 
                else if (isInt(arg)) {
                    tempMem[currentLocation + result++] = intToHex(arg);
                } 
                else if (arg.matches("[\"]{1}[^\"]*[\"]{1}") || arg.matches("[\']{1}[^\']*[\']{1}")) {
                    int argLen = arg.length() - 1;
                    for (int j = 0; j < argLen - 1; j++) {
                        tempMem[currentLocation + result++] = intToHex(Integer.toString((int) arg.charAt(j + 1)));
                    }
                } 
                else {
                    errorList.add("Invalid db parameter \"" + arg + "\" found on line " + lineNum);
                }
            }
        }
        return result;
    }

    /**
     * @param operation
     * @param line
     * @return A String value containing the specified operation
     */
    private String getByteCode(String operation, int line) {
        // \\s+ means any amount of whitespace
        String[] tokens = operation.split("\\s+", 2); //split opcode from args
        String op = tokens[0];
        String opcode; //CHANGE LOG Begin 7 jl948836
        if (tokens.length == 2) { //if op-code has args
            String[] args = tokens[1].split("\\s*,\\s*");
            if (args.length == 1) { // 1 argument found
                opcode = "Operation: " + op + " args: " + args[0];
                System.out.println(opcode);
                defList.add(opcode);
                switch (op.toUpperCase()) {
                    case "CALL":
                        return "6" + call(args[0], line);
                    case "SCALL":
                        return "6" + scall(args[0], line);
                    case "PUSH":
                        return "6" + push(args[0], line);
                    case "POP":
                        return "6" + pop(args[0], line);
                    case "JMP":
                        return "B" + jump(args[0], line);
                    case "RET":
                        return "6" + ret(args[0], line);
                }
            } 
            else if (args.length == 2) { // 2 arguments found
                opcode = "Operation: " + op + " args: " + args[0] + " " + args[1];
                System.out.println(opcode);
                defList.add(opcode);
                
                if (op.toUpperCase().equals("LOAD") && args[1].startsWith("[")
                    && args[1].endsWith("]")) { // Direct Load
                    return "1" + load1(args[0], args[1], line);
                } 
                //Broke if else statment at load, so that the rest could
                //be made into a switch statment.
                switch (op.toUpperCase()) {
                    case "LOAD":
                        // Immediate Load
                        return "2" + load2(args[0], args[1], line);
                    case "STORE":
                        return "3" + store(args[0], args[1], line);
                    case "MOVE":
                        return "4" + move(args[0], args[1], line);
                    case "ROR":
                        return "A" + ror(args[0], args[1], line);
                    case "JMPEQ":
                        return "B" + jmpeq(args[0], args[1], line);
                    case "ILOAD":
                        return "D" + iload(args[0], args[1], line);
                    case "ISTORE":
                        return "D" + istore(args[0], args[1], line);
                    case "RLOAD":
                        return rload(args[0], args[1], line); //args[1] #[RN]
                    case "RSTORE":
                        return "D" + rstore(args[0], args[1], line);
                    case "JMPLT":
                        //change "JMPLE" to "JMPLT"
                        return "F" + jmplt(args[0], args[1], line); //change "JMPLE" to "JMPLT"
                }
            } 
            else if (args.length == 3) { // 3 arguments found
                opcode = "Operation: " + op + " args: " + args[0] + " " + args[1] + " " + args[2];
                System.out.println(opcode);
                defList.add(opcode);
              
                switch (op.toUpperCase()) {
                    case "ADD":
                        return "5" + add(args[0], args[1], args[2], line);
                    case "OR":
                        return "7" + or(args[0], args[1], args[2], line);
                    case "AND":
                        return "8" + and(args[0], args[1], args[2], line);
                    case "XOR":
                        return "9" + xor(args[0], args[1], args[2], line);
                }
            }
        } else if (tokens.length == 1) { 	// No arguments
            opcode = "Operation: " + op + "args: none";
            System.out.println(opcode);
            defList.add(opcode);
            switch (op.toUpperCase()) {
                case "RET":
                    return "6100";
                case "SRET":
                    return "6300";
                case "HALT":
                    return "C000";
                case "NOOP":
                    return "0000";
            }

        }
        errorList.add("Invalid operation found on line " + line);
        return "0000"; // Should never get here
    }

    /**
     *
     * @param bytes
     * @param currentLocation
     * @param i
     * @return
     */
    private int byteCodeInTemp(String bytes, int currentLocation, int i) {
        int location;
        
        if (codes[i].trim().toUpperCase().contains("RLOAD")) { // RLOAD special case
            tempMem[currentLocation] = bytes.substring(0, 2);  // Placing Bytecode in memory
            tempMem[currentLocation + 1] = bytes.substring(2, 4);
            tempMem[currentLocation + 2] = bytes.substring(4, 6);
            tempMem[currentLocation + 3] = bytes.substring(6, 8);
            System.out.println("Code: " + codes[i] + "Currentlocation: " + intToHex(Integer.toString(currentLocation)));
            refList.add("Code: " + codes[i] + "Currentlocation: " + intToHex(Integer.toString(currentLocation)));
 
            location = 4;
        } else {
            tempMem[currentLocation] = bytes.substring(0, 2);// Placing Bytecode in memory
            tempMem[currentLocation + 1] = bytes.substring(2, 4);
            System.out.println("Code: " + codes[i] + "Currentlocation: " + intToHex(Integer.toString(currentLocation)));
            refList.add("Code: " + codes[i] + "Currentlocation: " + intToHex(Integer.toString(currentLocation)));
        
            location = 2;
        }
        return location;
    }

    /**
     *
     * @param firstArg
     * @param secondArg
     * @param line
     * @return Last three byte values that are a part of the LOAD instruction
     */
    private String load1(String firstArg, String secondArg, int line) {
        String result = "000";
        String firstRegister = getRegister(firstArg, line);
        String address = secondArg.substring(1, secondArg.length() - 1);
        if (labelMap.containsKey(address)) {
            result = firstRegister + intToHex(Integer.toString(labelMap.get(address)));
        }
        else if (isHex(address)) {
            result = firstRegister + address.substring(2, 4);
        } 
        else if (isInt(address)) {
            result = firstRegister + intToHex(address);
        } 
        else {
            errorList.add("Error: LOAD operations on line " + line
                + " has invalid arguments.");
        }
        return result;
    }

    /**
     *
     * @param firstArg
     * @param secondArg
     * @param line
     * @return Last three bytes for assembly of the LOAD instruction
     */
    private String load2(String firstArg, String secondArg, int line) {
        String result = "000";
        String firstRegister = getRegister(firstArg, line);
        String address = secondArg;
        if (labelMap.containsKey(address)) {
            result = firstRegister + intToHex(Integer.toString(labelMap.get(address)));
        } 
        else if (isHex(address)) {
            result = firstRegister + address.substring(2, 4);
        } 
        else if (isInt(address)) {
            result = firstRegister + intToHex(address);
        } 
        else {
            errorList.add("Error: LOAD operations on line " + line
                + " has invalid arguments.");
        }
        return result;
    }

    /**
     *
     * @param firstArg
     * @param secondArg
     * @param line
     * @return Last three bytes for assembly of the JMPLT instruction
     */
    private String jmplt(String firstArg, String secondArg, int line) { //change "JMPLE" to "JMPLT"
        String result = "000";
        if (firstArg.toUpperCase().contains("<R0")) { //change "<=R0" to "<R0"
            String first[] = firstArg.split("<"); //change "<=" to "<"
            firstArg = getRegister(first[0], line);
            if (labelMap.containsKey(secondArg)) { // arg is a label
                result = firstArg + intToHex(Integer.toString(labelMap.get(secondArg)));
            } 
            else if (isInt(secondArg)) { // arg is decimal
                result = firstArg + intToHex(secondArg);
            } 
            else if (isHex(secondArg)) { // arg is hex
                result = firstArg + secondArg.substring(2, 4);
            } 
            else {
                //change "JMPLE" to "JMPLT"
                errorList.add("Error: Invalid destination for JMPLT on line " + line);
            }
        } else {
            //change "JMPLE" to "JMPLT"
            errorList.add("Error: Missing equal sign for JMPLT on line " + line);
        }
        return result;
    }

    /**
     *
     * @param firstArg
     * @param secondArg
     * @param line
     * @return Last three bytes for the assembly of the RSTORE instruction.
     */
    private String rstore(String firstArg, String secondArg, int line) {
        String result = "000";
        String firstRegister = getRegister(firstArg, line);
        String secondRegister;
        String offset;
        String tokens[] = secondArg.split("\\[");
        if (tokens.length == 2 && tokens[1].endsWith("]")) {
            if (isSingleHex(tokens[0])) {
                offset = tokens[0].toUpperCase().substring(2, 3);
            } 
            else if (tokens[0].startsWith("-") && tokens[0].length() == 2) {
                int number = Integer.parseInt(tokens[0].toUpperCase().substring(1, 2));
                switch (number) {
                    case 1:
                        offset = "F";
                        break;
                    case 2:
                        offset = "E";
                        break;
                    case 3:
                        offset = "D";
                        break;
                    case 4:
                        offset = "C";
                        break;
                    case 5:
                        offset = "B";
                        break;
                    case 6:
                        offset = "A";
                        break;
                    case 7:
                        offset = "9";
                        break;
                    case 8:
                        offset = "8";
                        break;
                    default:
                        errorList.add("Error: Invalid offset found on line " + line);
                       
                        return result;
                }
            } 
            else if (tokens[0].length() == 1 && Integer.parseInt(tokens[0]) < 8) {
                offset = tokens[0];
            } 
            else {
                errorList.add("Error: Invalid argument on line " + line);
            
                return result;
            }
        } else {
            errorList.add("Error: Invalid argument on line " + line);
            
            return result;
        }
        //TODO: remove 1 from legnth of tokens. Why?
        secondRegister = getRegister(tokens[1].substring(0, tokens[1].length()), line);
        return offset + firstRegister + secondRegister;
    }

    /**
     *
     * @param firstArg
     * @param secondArg
     * @param line
     * @return Assembled byte values for the RLOAD instruction.
     */
    private String rload(String firstArg, String secondArg, int line) {
        // rload is special, it returns 8 hex digits
        String result = "00000000";
        String firstRegister = getRegister(firstArg, line);
        String secondRegister;
        String offset;
        String tokens[] = secondArg.split("\\["); //tokens[0]=offset, tokens[1]= reg]
        System.out.println("Break Down of RLOAD: ");
        System.out.println("firstReg: " + firstRegister);
        System.out.println("tokens: " + tokens[0] + " " + tokens[1]);
        if (tokens.length == 2 && tokens[1].endsWith("]")) {
            if (isSingleHex(tokens[0])) {
                offset = tokens[0].toUpperCase().substring(2, 3);
            } 
            else if (tokens[0].startsWith("-") && tokens[0].length() == 2) {
                int number = Integer.parseInt(tokens[0].toUpperCase().substring(1, 2));
                switch (number) {
                    case 1:
                        offset = "F";
                        break;
                    case 2:
                        offset = "E";
                        break;
                    case 3:
                        offset = "D";
                        break;
                    case 4:
                        offset = "C";
                        break;
                    case 5:
                        offset = "B";
                        break;
                    case 6:
                        offset = "A";
                        break;
                    case 7:
                        offset = "9";
                        break;
                    case 8:
                        offset = "8";
                        break;
                    default:
                        errorList.add("Error: Invalid offset found on line " + line);
                        
                        return result;
                }
            } 
            else if (tokens[0].length() == 1 && Integer.parseInt(tokens[0]) < 8) {
                offset = tokens[0];
            } 
            else {
                errorList.add("Error: Invalid argument on line " + line);
                return result;
            }
        } else {
            errorList.add("Error: Invalid argument on line " + line);
            return result;
        }
        //CHANGE LOG BEGIN - 3
        String regName = tokens[1].substring(0, tokens[1].length()-1); //truncate "]"
        secondRegister = getRegister(regName, line);
        //construct op-code/machine code. "F" is a flag.
        return "2" + firstRegister + "F" + offset + "D2" + firstRegister + secondRegister;
        //CHANGE LOG END - 3
    }

    /**
     *
     * @param firstArg
     * @param secondArg
     * @param line
     * @return Last three bytes for assembly of the ISTORE instruction
     */
    private String istore(String firstArg, String secondArg, int line) {
        String result = "000";
        firstArg = getRegister(firstArg, line);
        if (secondArg.startsWith("[") && secondArg.endsWith("]")) {
            secondArg = secondArg.substring(1, secondArg.length() - 1);
            secondArg = getRegister(secondArg, line);
            result = "1" + firstArg + secondArg;
        } else {
            errorList.add("Error: ISTORE operation on line " + line
                + " has invalid arguments.");
        }
        return result;
    }

    /**
     *
     * @param firstArg
     * @param secondArg
     * @param line
     * @return Last three bytes for assembly of the ILOAD instruction
     */
    private String iload(String firstArg, String secondArg, int line) {
        String result = "000";
        firstArg = getRegister(firstArg, line);
        if (secondArg.startsWith("[") && secondArg.endsWith("]")) {
            secondArg = secondArg.substring(1, secondArg.length() - 1);
            secondArg = getRegister(secondArg, line);
            result = "0" + firstArg + secondArg;
        } else {
            errorList.add("Error: ILOAD operation on line " + line
                + " has invalid arguments.");
        }
        return result;
    }

    /**
     *
     * @param firstArg
     * @param line
     * @return Last three bytes for assembly of the JMP instruction
     */
    private String jump(String firstArg, int line) {
        String result = "000";
        if (labelMap.containsKey(firstArg)) { // arg is a label
            result = "0" + intToHex(Integer.toString(labelMap.get(firstArg)));
        } 
        else if (isInt(firstArg)) { // arg is decimal
            result = "0" + intToHex(firstArg);
        } 
        else if (isHex(firstArg)) { // arg is hex
            result = "0" + firstArg.substring(2, 4);
        } 
        else {
            errorList.add("Error: Invalid destination for JUMP on line " + line);
        }
        return result;
    }

    /**
     *
     * @param firstArg
     * @param secondArg
     * @param line
     * @return Last three bytes for assembly of the JMPEQ instruction
     */
    private String jmpeq(String firstArg, String secondArg, int line) {
        String result = "000";
        if (firstArg.toUpperCase().contains("=R0")) {   //CHANGE LOG - 6
            String first[] = firstArg.split("=");
            firstArg = getRegister(first[0], line);
            if (labelMap.containsKey(secondArg)) { // arg is a label
                result = firstArg + intToHex(Integer.toString(labelMap.get(secondArg)));
            } 
            else if (isInt(secondArg)) { // arg is decimal
                result = firstArg + intToHex(secondArg);
            } 
            else if (isHex(secondArg)) { // arg is hex
                result = firstArg + secondArg.substring(2, 4);
            } 
            else {
                errorList.add("Error: Invalid destination for JMPEQ on line " + line);
            }
        } else {
            errorList.add("Error: Missing equal sign for JMPEQ on line " + line);
        }
        return result;
    }

    /**
     *
     * @param firstArg
     * @param secondArg
     * @param line
     * @return Last three bytes for assembly of the ROR instruction
     */
    private String ror(String firstArg, String secondArg, int line) {
        String result = "000";
        firstArg = getRegister(firstArg, line);
        if (isHex(secondArg)) {
            result = firstArg + secondArg.substring(2, 4);
        } 
        else if (isInt(secondArg)) {
            result = firstArg + intToHex(secondArg);
        } 
        else {
            errorList.add("Error: ROR operations on line " + line
                + " has invalid arguments.");
        }
        return result;
    }

    /**
     *
     * @param firstArg
     * @param secondArg
     * @param thirdArg
     * @param line
     * @return Last three bytes for assembly of the XOR instruction
     */
    private String xor(String firstArg, String secondArg, String thirdArg, int line) {
        return getRegister(firstArg, line) + getRegister(secondArg, line) + getRegister(thirdArg, line);
    }

    /**
     *
     * @param firstArg
     * @param secondArg
     * @param thirdArg
     * @param line
     * @return Last three bytes for assembly of the AND instruction
     */
    private String and(String firstArg, String secondArg, String thirdArg, int line) {
        return getRegister(firstArg, line) + getRegister(secondArg, line) + getRegister(thirdArg, line);
    }

    /**
     *
     * @param firstArg
     * @param secondArg
     * @param thirdArg
     * @param line
     * @return Last three bytes for assembly of the OR instruction
     */
    private String or(String firstArg, String secondArg, String thirdArg, int line) {
        return getRegister(firstArg, line) + getRegister(secondArg, line) + getRegister(thirdArg, line);
    }

    /**
     *
     * @param firstArg
     * @param line
     * @return Last three bytes for assembly of the POP instruction
     */
    private String pop(String firstArg, int line) {
        String result = "5" + getRegister(firstArg, line) + "0";
        return result;
    }

    /**
     *
     * @param firstArg
     * @param line
     * @return Last three bytes for assembly of the PUSH instruction
     */
    private String push(String firstArg, int line) {
        String result = "4" + getRegister(firstArg, line) + "0";
        return result;
    }

    /**
     *
     * @param firstArg
     * @param line
     * @return Last three bytes for assembly of the SCALL instruction
     */
    private String scall(String firstArg, int line) {
        String result = "000";
        if (labelMap.containsKey(firstArg)) { // arg is a label
            result = "2" + intToHex(Integer.toString(labelMap.get(firstArg)));
        } 
        else if (isInt(firstArg)) { // arg is decimal
            result = "2" + intToHex(firstArg);
        } 
        else if (isHex(firstArg)) { // arg is hex
            result = "2" + firstArg.substring(2, 4);
        } 
        else {
            errorList.add("Error: Invalid destination for SCALL on line " + line);    
        }
        return result;
    }

    /**
     * @param firstArg
     * @param line
     * @return Last three bytes for assembly of the CALL instruction
     */
    private String call(String firstArg, int line) {
        String result = "000";
        if (labelMap.containsKey(firstArg)) { // arg is a label
            result = "0" + intToHex(Integer.toString(labelMap.get(firstArg)));
        } 
        else if (isInt(firstArg)) { // arg is decimal
            result = "0" + intToHex(firstArg);
        } 
        else if (isHex(firstArg)) { // arg is hex
            result = "0" + firstArg.substring(2, 4);
        } 
        else {
            errorList.add("Error: Invalid destination for CALL on line " + line); 
        }
        return result;
    }

    /**
     * @param firstArg
     * @param line
     * @return Last three bytes for assembly of the RET instruction
     */
    private String ret(String firstArg, int line) {
        String result = "100";
        if (isInt(firstArg)) {
            result = intToHex(firstArg); //CHANGE LOG BEGIN - 9
        } 
        else if (isHex(firstArg)) {
            result = firstArg.substring(2, 4); //CHANGE LOG END - 9
        }
        else {
            errorList.add("Error: Invalid number for RET on line " + line);
        }
        return result;
    }

    /**
     *
     * @param firstArg
     * @param secondArg
     * @param thirdArg
     * @param line
     * @return Last three bytes for assembly of the ADD instruction
     */
    private String add(String firstArg, String secondArg, String thirdArg, int line) {
        //TODO: add error reporting inside getRegister
        return getRegister(firstArg, line) + getRegister(secondArg, line) + getRegister(thirdArg, line);
    }

    /**
     *
     * @param firstArg
     * @param secondArg
     * @param line
     * @return Last three bytes for assembly of the MOVE instruction
     */
    private String move(String firstArg, String secondArg, int line) {
        return "0" + getRegister(secondArg, line) + getRegister(firstArg, line);
    }

    /**
     *
     * @param firstArg
     * @param secondArg
     * @return Last three bytes for assembly of the STORE instruction
     */
    //modified store  --- the store command is: STORE [XY], RN
    //store the value in register N in the memory cell at address XY
    private String store(String firstArg, String secondArg, int line) {
        String result = "000";
        secondArg = getRegister(secondArg, line);
        if (firstArg.startsWith("[") == false || firstArg.endsWith("]") == false){
            errorList.add("Error: STORE operations on line " + line
                    + " has invalid arguments.");
        }
        else{
            if (firstArg.startsWith("[") && firstArg.endsWith("]")) {
                firstArg = firstArg.substring(1, firstArg.length() - 1);
                if (labelMap.containsKey(firstArg)) {
                    result = secondArg + intToHex(Integer.toString(labelMap.get(firstArg)));
                } else if (isHex(firstArg)) {
                    result = secondArg + firstArg.substring(2, 4);
                } else if (isInt(firstArg)) {
                    result = secondArg + intToHex(firstArg);
                } else {
                    errorList.add("Error: STORE operations on line " + line
                        + " has invalid arguments.");               
                }
            }
        } 
        return result;
    }
    
    /*
    private String store(String firstArg, String secondArg, int line) {
        String result = "000";
        firstArg = getRegister(firstArg, line);
        if (secondArg.startsWith("[") && secondArg.endsWith("]")) {
            secondArg = secondArg.substring(1, secondArg.length() - 1);
            if (labelMap.containsKey(secondArg)) {
                result = firstArg + intToHex(Integer.toString(labelMap.get(secondArg)));
            } else if (isHex(secondArg)) {
                result = firstArg + secondArg.substring(2, 4);
            } else if (isInt(secondArg)) {
                result = firstArg + intToHex(secondArg);
            } else {
                errorList.add("Error: STORE operations on line " + line
                    + " has invalid arguments.");
                
            }
        }
        return result;
    }*/

    /**
     * Helper method Returns a string with the corresponding register
     *
     * @param register
     * @return The index of the specified register
     */
    private String getRegister(String register, int line) {
        if (register.toUpperCase().startsWith("R")) {
            if (register.length() == 3) {
                /*
                 * Added the or statements to allow for users to call
                 * either the literal name of the bp or just the 
                 * register name
                 * Cody Galbreath - 03/23/2014
                 */
                if (register.toUpperCase().equals("RBP") || register.toUpperCase().equals("RD")) {
                    return "D";
                } else if (register.toUpperCase().equals("RSP") || register.toUpperCase().equals("RE")) {
                    return "E";
                }
            }
            return register.substring(1);
        }
        errorList.add("Error: Invalid register on line " + line);
        return "0";
    }

    /**
     * Helper method Ensure label has not been used and follows the regex
     * pattern ^[a-zA-Z]{1}[a-zA-Z0-9]*$
     *
     * @param labels
     * @param token
     * @return boolean
     */
    private boolean isValidLabel(String token) {
        //make sure label is unique
        for (String label : labels) {
            if (label != null && label.equals(token)) {
                return false;
            }
        }
        // TODO: make sure we match the regex
        // label okay
        return true;
    }

    /**
     * Helper method to check for valid 2 digit int string
     *
     * @param number
     * @return boolean
     */
    private boolean isInt(String number) {
        if (number.length() == 0 || number.length() > 4) {
            return false;
        } 
        else if (number.matches("[0-9][0-9]{0,2}") || number.matches("-[0-9][0-9]{0,2}")) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param number
     * @return True if it is Hex, false if not
     */
    private boolean isSingleHex(String number) {
        //System.out.println(number.length());
        if (number.length() != 4) { //CHANGE LOG Begin - 5
            return false;
        } else {
            if (number.substring(0, 3).equalsIgnoreCase("0x0")
                && number.substring(3, 4).toUpperCase().matches("[0-9A-F]")) {  //CHANGE LOG End - 5
                return true;
            }
        }
        return false;
    }
 
    /**
     * Helper method to check for valid 2 digit hex string must have '0x' at the
     * start to be valid
     *
     * @param number
     * @return boolean
     */
    private boolean isHex(String number) {
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

    /**
     * Helper method, hex string to integer, chops 0x at start of string
     *
     * @param hex
     * @return integer
     */
    private int hexToInt(String hex) {
        return Integer.parseInt(hex.toUpperCase().replaceFirst("0X", ""), 16);
    }

    /**
     * Helper method, decimal string to hex string. 0x is left off.
     *
     * @param decimal
     * @return hexadecimal
     */
    private String intToHex(String decimal) {
        String result = Integer.toHexString(Integer.parseInt(decimal)).toUpperCase();
        if (result.length() == 1) {
            result = "0" + result;
        } 
        else if (result.length() > 2) {
            result = result.substring(result.length() - 2);
        }
        return result;
    }

    /**
     * loop through pseudoOps and see if token is valid
     *
     * @param token
     * @return boolean
     */
    private boolean isPseudoOp(String token) {
        for (String pseudoOp : pseudoOps) {
            if (pseudoOp.equals(token.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * loop through operations and see if token is valid
     *
     * @param token
     * @return boolean
     */
    private boolean isOperation(String token) {
        for (String operation : operations) {
            if (operation.equals(token.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Prints the contents of the lables array and the codes array for the
     * virtual machine.
     *
     * @param labels
     * @param codes
     */
    public void printContent(String[] labels, String[] codes) {
        System.out.println("\nLABELS:");
        for (String label : labels) {
            System.out.println(label);
            labelList.add(label);
        }
        
        System.out.println("\nPrinting through Label map");
        for (String key: labelMap.keySet()){
            System.out.println(key + " : " + intToHex(Integer.toString(labelMap.get(key))));
            logList.add(key + " : " + intToHex(Integer.toString(labelMap.get(key))));
        }
        
        System.out.println("\nCODES:");
        for (String code : codes) {
            System.out.println(code);
            //codeList.add(codes[i]);
        }
    }
}
