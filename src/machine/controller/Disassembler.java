/**
 * Program: Disassembler.java
 * 
 * Purpose: used to generate high-level code from the machine representation for 
 *          displaying in both the editor view (Disassemble Button) and in the 
 *          Disassemble console at runtime.
 * 
 * @author:
 * 
 * date/ver:
 */

/**
 * Change Log
 * 
 * Matt Vertefeuille -> mv935583
 * # author - date: description
 * 1 mv935583 - 02/22/16: Bug Fix - rload now displays the 0x0 to 
 *                                 reflect Change #2 in Assembler.java
 * 
 * 2 mv935583 - 04/11/16: Implemented changed to add shift instructions.
 * 
 * 3 jl948836 - 04/19/16: Swapped how RSTORE is displayed
*/

/*
* Change log
*
* # author - Guojun Liu - 
* # modfied istore and restore
*/

package machine.controller;

import java.util.ArrayList;
public class Disassembler {

    String[] input;
    MachineController controller;

    /**
     * Constructor used to create disassembler console output
     */
    public Disassembler() {
    }

    /**
     * Sets which MachineController that this Disassembler is tied to.
     * @param controller - sets the global controller to the invoking controller.
     */
    public Disassembler(MachineController controller) {
        this.controller = controller;
    }

    /**
     * Takes the provided instructionPointer and byteCode and generates a high level
     * interpretation of the byteCode. 
     * @param instructionPointer - location of the currently executing instruction.
     * @param byteCode - Array of strings representing current memory and IP state.
     * @return high level code for displaying in editor view.
     */
    public String getDisassemble(String instructionPointer, String[] byteCode) {
        input = byteCode;
        ArrayList<String> output = new ArrayList<>();
        String code = "sip 0x" + instructionPointer + "\n";
        for (int i = 0; i < byteCode.length; i += 2) {
            output.add(disassemble(byteCode[i], byteCode[i + 1]));
        }
        for(String line : output){
            code = code.concat(line + "\n");
        }
        while (code.endsWith("noop\n")) {
            code = code.substring(0, code.length()-5);
        }
        return code;
    }

    /**
     * Executed by the clock - used to display 3 instructions before IP,
     * IP, and 3 instructions after IP in the disassemble console
     * @param bytecode - must be only 7 elements
     * @param IP - indicates which of the 7 lines of bytecode is the IP
     * @return 7 lines of disassembled code
     */
    public String getConsoleDisassemble(String[] bytecode, int IP) {
        input = bytecode;
        ArrayList<String> output = new ArrayList<>();
        String code = "";
        for (int i = 0; i < bytecode.length; i += 2) {
            if ( (IP * 2) == i) {
                String outText;
                outText = disassemble(bytecode[i], bytecode[i + 1]);
                if (outText.length() > 11) {
                    output.add(outText + " <<");
                } else if (outText.length() < 8){
                    output.add(outText + "\t\t<<");
                } else {
                    output.add(outText + "\t<<");
                }
            } else {
                output.add(disassemble(bytecode[i], bytecode[i + 1]));
            }
        }
        for(String line : output){
            code = code.concat(line + "\n");
        }
        return code;		
    }

    /**
     * Takes the firstByte and secondByte of byteCode passed in and generates a 
     * high level code string of the byteCode and returns it.
     * @param firstByte - first byte of the byte code instruction
     * @param secondByte - second byte of the byte code instruction
     * @return string containing the high level code of the passed in byte code.
     */
    private String disassemble(String firstByte, String secondByte) {
        String firstNibble = firstByte.substring(0, 1);
        String secondNibble = firstByte.substring(1, 2);
        String thirdNibble = secondByte.substring(0, 1);
        String fourthNibble = secondByte.substring(1, 2);

        switch (firstNibble) {
            case "0":
                return "noop";
            case "1":
                return "load " + "R" + secondNibble + ",[0x" + secondByte + "]";
            case "2":
                return "load " + "R" + secondNibble + ",0x" + secondByte;
            case "3":
                //return "store " + "R" + secondNibble + ",[0x" + secondByte + "]";
                return "store " + "[0x" + secondByte + "]" + ",R"+ secondNibble ;
            case "4":
                //CHANGE LOG BEGIN: 3
                return "rload " + "R" + thirdNibble + ",0x" + secondNibble + "[R" + fourthNibble + "]";
            case "5":
                return "add " + "R" + secondNibble + ",R" + thirdNibble + ",R" + fourthNibble;
            case "6":
            switch (secondNibble) {
                case "0":
                    return "call " + "0x" + secondByte;
                case "1":
                    return "ret";
                case "2":
                    return "scall " + "0x" + secondByte;
                case "3":
                    return "sret";
                case "4":
                    return "push " + "R" + thirdNibble;
                case "5":
                    return "pop " + "R" + thirdNibble;
                default:
                    return "invalid";
            }
            case "7":
                return "or " + "R" + secondNibble + ",R" + thirdNibble + ",R" + fourthNibble;
            case "8":
                return "and " + "R" + secondNibble + ",R" + thirdNibble + ",R" + fourthNibble;
            case "9":
                return "xor " + "R" + secondNibble + ",R" + thirdNibble + ",R" + fourthNibble;
            case "A":
                switch(secondNibble){   //BEGIN CHANGE LOG: 2
                    case "0":
                        return "ror " + "R" + thirdNibble + "," + fourthNibble;
                    case "1":
                        return "rol " + "R" + thirdNibble + "," + fourthNibble;
                    case "2":
                        return "sra " + "R" + thirdNibble + "," + fourthNibble;
                    case "3":
                        return "srl " + "R" + thirdNibble + "," + fourthNibble;
                    case "4":
                        return "sl " + "R" + thirdNibble + "," + fourthNibble;
                    default:
                        return "invalid";
                }   //END CHANGE LOG: 2
            case "B":
                if (secondNibble.equals("0")) {
                    return "jmp 0x" + secondByte;
                } else {
                    return "jmpEQ " + "R" + secondNibble + "=R0,0x" + secondByte;
                }
            case "C":
                return "halt";
            case "D":
            switch (secondNibble) {
                case "0":
                    return "iload " + "R" + thirdNibble + ",[R" + fourthNibble + "]";
                case "1":
                    //modified code
                    return "istore " + "[R" + fourthNibble	+ "]" + " ,R" + thirdNibble;
                    //original code
                    //return "istore " + "R" + thirdNibble + ",[R" + fourthNibble	+ "]";
                case "2": //CHANGE LOG: 3
                    return "move " + "R" + thirdNibble + ",R" + fourthNibble;
                default:
                    return "invalid";
            }
            case "E":
                //modified code
                return "rstore " + "0x" + secondNibble + "[R"
			+ thirdNibble + "]"+ " ,R" + fourthNibble; //CHANGE LOG: 3
                //original code
		/*return "rstore " + "R" + thirdNibble + ",0x" + secondNibble + "[R"
                    + fourthNibble + "]";*/
            case "F":
                return "jmpLT " + "R" + secondNibble + "<R0,0x" + secondByte;  // change "jmpLE" to "jmpLT" and "<=" to "<"
        }
            return "invalid";
    }
}