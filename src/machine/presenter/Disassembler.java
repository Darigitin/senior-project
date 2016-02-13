package machine.presenter;

import java.util.ArrayList;


/**
 * Disassembler class 
 * - used to generate high-level code from the machine representation for displaying
 * in both the editor view (Disassemble Button) and in the Disassemble console at 
 * runtime.
 */
public class Disassembler {

	String[] input;
        MachineController controller;
        
        	/**
	 * Constructor used to create disassembler console output
	 */
	public Disassembler() {
	}
	
	/**
	 * Constructor used to create disassembler output for editor
	 * @param controller is needed to get SIP from the model
	 */
	public Disassembler(MachineController controller) {
		this.controller = controller;
	}

	/**
	 * Executed when Disassemble button is clicked
	 * @param byte code - Array of strings representing current memory and IP state
	 * @return high level code for displaying in editor view
	 */
	public String getDisassemble(String instructionPointer, String[] bytecode) {
		input = bytecode;
		ArrayList<String> output = new ArrayList<String>();
		String code = "sip 0x" + instructionPointer + "\n";
		for (int i = 0; i < bytecode.length; i += 2) {
			if (bytecode[i].equals("D2")) {
				output.remove(output.size()-1);
			}
			output.add(disassemble(bytecode[i], bytecode[i + 1], i));
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
		ArrayList<String> output = new ArrayList<String>();
		String code = "";
		boolean foundRload = false;
		for (int i = 0; i < bytecode.length; i += 2) {
			if (bytecode[i].equals("D2")) {
				output.remove(output.size()-1);
				foundRload = true;
			}
			if ( (IP * 2) == i || (foundRload && (IP == 2)) ) {
				String outText = "";
				outText = disassemble(bytecode[i], bytecode[i + 1], i);
				if (outText.length() > 11) {
					output.add(outText + " <<");
				} else if (outText.length() < 8){
					output.add(outText + "\t\t<<");
				} else {
					output.add(outText + "\t<<");
				}
				foundRload = false;
			} else {
				output.add(disassemble(bytecode[i], bytecode[i + 1], i));
			}
		}
		for(String line : output){
			code = code.concat(line + "\n");
		}
		return code;		
	}
	
	private String disassemble(String firstByte, String secondByte, int location) {
		String firstNibble = firstByte.substring(0, 1);
		String secondNibble = firstByte.substring(1, 2);
		String thirdNibble = secondByte.substring(0, 1);
		String fourthNibble = secondByte.substring(1, 2);

		if (firstNibble.equals("0")) {
			return "noop";
		} else if (firstNibble.equals("1")) {
			return "load " + "R" + secondNibble + ",[0x" + secondByte + "]";
		} else if (firstNibble.equals("2")) {
			return "load " + "R" + secondNibble + ",0x" + secondByte;
		} else if (firstNibble.equals("3")) {
			return "store " + "R" + secondNibble + ",[0x" + secondByte + "]";
		} else if (firstNibble.equals("4")) {
			return "move " + "R" + fourthNibble + ",R" + thirdNibble;
		} else if (firstNibble.equals("5")) {
			return "add " + "R" + secondNibble + ",R" + thirdNibble + ",R" + fourthNibble;
		} else if (firstNibble.equals("6")) {
			if (secondNibble.equals("0")) {
				return "call " + "0x" + secondByte;
			} else if (secondNibble.equals("1")) {
				return "ret";
			} else if (secondNibble.equals("2")) {
				return "scall " + "0x" + secondByte;
			} else if (secondNibble.equals("3")) {
				return "sret";
			} else if (secondNibble.equals("4")) {
				return "push " + "R" + thirdNibble;
			} else if (secondNibble.equals("5")) {
				return "pop " + "R" + thirdNibble;
			} else {
				return "invalid";
			}
		} else if (firstNibble.equals("7")) {
			return "or " + "R" + secondNibble + ",R" + thirdNibble + ",R" + fourthNibble;
		} else if (firstNibble.equals("8")) {
			return "and " + "R" + secondNibble + ",R" + thirdNibble + ",R" + fourthNibble;
		} else if (firstNibble.equals("9")) {
			return "xor " + "R" + secondNibble + ",R" + thirdNibble + ",R" + fourthNibble;
		} else if (firstNibble.equals("A")) {
			return "ror " + "R" + secondNibble + ",0x" + secondByte;
		} else if (firstNibble.equals("B")) {
			if (secondNibble.equals("0")) {
				return "jmp 0x" + secondByte;
			} else {
				return "jmpEQ " + "R" + secondNibble + "=R0,0x" + secondByte;
			}
		} else if (firstNibble.equals("C")) {
			return "halt";
		} else if (firstNibble.equals("D")) {
			if (secondNibble.equals("0")) {
				return "iload " + "R" + thirdNibble + ",[R" + fourthNibble + "]";
			} else if (secondNibble.equals("1")) {
				return "istore " + "R" + thirdNibble + ",[R" + fourthNibble	+ "]";
			} else if (secondNibble.equals("2")) {
				String fb = input[location - 2];
				String sb = input[location - 1];
				if (fb.substring(0, 1).equals("2") && fb.substring(1, 2).equals(thirdNibble)) {
					// delete last operation then do this
					return "rload " + "R" + thirdNibble + ",0x" + sb.substring(1, 2) + "[R" + fourthNibble + "]";
				} else {
					return "ERROR";
				}
			} else {
				return "invalid";
			}
		} else if (firstNibble.equals("E")) {
			return "rstore " + "R" + thirdNibble + ",0x" + secondNibble + "[R"
					+ fourthNibble + "]";
		} else if (firstNibble.equals("F")) {
			return "jmpLT " + "R" + secondNibble + "<R0,0x" + secondByte;  // change "jmpLE" to "jmpLT" and "<=" to "<"
		}
		return "invalid";
	}
}