/**
 * Program: Clock.java
 * 
 * Purpose:
 * 
 * @author:
 * 
 * date/ver:
 */

/**
 * Change Log
 * 
 * # author - date: description
 * 
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/** Change Log
*    #Guojun Liu  03/08/16 
*    1. Create a new string array as memory address register (MAR) to 
*       hold the instruction in IP
*    2. Create three new methods for CPU cycle
*    3. Modified the loadMachine so that IR could display the correct values
*    4. Create setInstructionRegister method for IP
*    5. Create setInstructionRegisterForJump method to hold the condition when
*       IP jump to a branch call.
*    6. Modified the setIPinstruction method.
*    7. Modified the getInstructionFromIP method.
*    8. Create updateIPwhenHalt method to update IR when the program stopped.s
*   
**/

package machine.controller;

import machine.model.Assembler;
import java.util.ArrayList;
import machine.view.MachineView;
/**
 *
 * @author Ryan Ball
 */
public class MachineController {
        
       
    private final MachineView machineView;
    private final Clock clock = new Clock(this);
    private ArrayList<String> lastAssembledProg = new ArrayList<>();
    private boolean isRunning = false;
    private static final String[] MemoryAddressRegister = new String[2];     //declear MAR
    Assembler assembler = new Assembler(this);
    private final ArrayList<String> fatalMemErrorList = new ArrayList<>();
    private final ArrayList<String> nonFatalMemErrorList = new ArrayList<>();

    /**
     * Constructor method, initializes the machineView, and sets the initial state
     * of the lastAssembleProg array list to a null state.
     */
    public MachineController() {
        machineView = new MachineView(this);

        for (int i = 0; i < 257; i++) {
            lastAssembledProg.add("00");
        }
    }

    /**
     * Informs the caller if the machine is currently running or not.
     * 
     * @return true if the isRunning is true, false otherwise.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * If the machine is not running, starts the clock and sets isRunning to true.
     */
    public void runClock() {
        if (!isRunning) {
            isRunning = true;
            clock.run();
        }
    }

    /**
     * Resets the machine back to its default state, setting isRunning to false,
     * cancels the clock timer, resets the clock instruction count, reseting the machineView,
     * removes all entries in the fatalMemErrorList, loads lastAssembledProg, refreshes the machinView,
     * sets all textAreas back to their default state, and finally resets the ActivationRecords.
     */
    public void resetMachine() {
        isRunning = false;
        clock.timer.cancel();
        clock.resetInstructionCount();
        machineView.reset();
        fatalMemErrorList.removeAll(fatalMemErrorList);
        loadMachine(lastAssembledProg);
        refreshMachineView();
        machineView.getConsoleTextArea().setText("");
        machineView.getDisassTextArea().setText("No disassembler text yet");
        machineView.getInstructionCountTextArea().setText("Count of the number of Instructions executed.");
        machineView.getErrorTextArea().setText("");
        machineView.getMemoryErrorTextArea().setText("No Errors");
        machineView.resetActivationRecords();
    }

    /**
     * Stops the clock by setting isRunning to false and canceling the clock timer.
     */
    public void stopClock() {
        isRunning = false;
        clock.timer.cancel();
    }

    /**
     * Stops the clock and then steps the clock forward one step.
     */
    public void stepClock() {
        stopClock();
        if (fatalMemErrorList.isEmpty() && nonFatalMemErrorList.isEmpty()) {
            clock.step();
        }
        else if (!nonFatalMemErrorList.isEmpty()) {
            setMemoryErrors();
            clock.step();
        }
        else {
            setMemoryErrors();
        }
    }

    /**
     * Resets the machine and assembles the high level code currently located in
     * the text editor.
     */
    public void performAssemble() {
        resetMachine();
        Assembler assembler1 = new Assembler(this);
        String text = machineView.getEditorText();
        ArrayList<String> codes = assembler1.parse(text);
        lastAssembledProg.clear();
        lastAssembledProg = codes;
        if (!machineView.getErrorTextArea().isVisible()) {
            loadMachine(codes);
            machineView.resetActivationRecords();
        }
    }
    
    /**
     * Sets the machineView Disassemble text area to the provided string consoleText.
     * @param consoleText - String of disassembled byteCode to be loaded into the Disassembler Text Area.
     */
    public void setDisassText(String consoleText) {
        machineView.getDisassTextArea().setText(consoleText);
    }
    
    /**
     * 
     * @param c 
     */
    public void setConsoleText(char c) {
        machineView.getConsoleTextArea().setText(
        machineView.getConsoleTextArea().getText() + c);
    }
    
    /**
     * Sets the InstructionCounterTextArea with the current instructionCount.
     * @param instructionCount - current number of instructions executed by the clock.
     */
    public void setInstructionCounterText(int instructionCount){
        machineView.getInstructionCountTextArea().setText(
        "Instruction count: " + clock.getInstructionCount());
    }
 
    /**
     * When code is assembled loadMachine
     * populates all registers and memory table
     * with appropriate values.
     * @param Array of byteCode.
     */
    private void loadMachine(ArrayList<String> codes) {
	int memoryIndex = 0;
        for (String code : codes) {
            if (memoryIndex == 0) {
		machineView.setInstructionPointer(code); 
                machineView.setInstructionRegister("00 00");
            } else {
		machineView.setRAMBytes(code, memoryIndex - 1);		
            }
            memoryIndex++;            
	}
	// grab the instruction pointer from the codes array
	int IP = Integer.parseInt(codes.get(0),16);               //Get instruction pointer from the first place
	if (IP < 255) { // only make sense that IP is < 255...            
            String firstOp = codes.get(IP+1);
            String secondOp = codes.get(IP+2);                        
            MemoryAddressRegister[0] = firstOp;
            MemoryAddressRegister[1] = secondOp;
	} else {
            machineView.setInstructionRegister("XX XX");
	}
    }
    
    /**
     * Sets the clock clockSpeed to the passed in integer speed or to 500 if speed is 
     * not a valid speed. Also if the the clock is running it cancels the current clock
     * and reruns it based off the new speed.
     * 
     * @param speed - integer value between 1 and 10 - 1 is slowest, 10 is fastest
     */
    public void setClockSpeed(int speed) {
        int time;
        switch (speed) {
            case 1: 
                time = 5000;
                break;
            case 2: 
                time = 3500;
                break;
            case 3: 
                time = 2000;
                break;
            case 4: 
                time = 1000;
                break;
            case 5: 
                time = 500;
                break;
            case 6: 
                time = 300;
                break;
            case 7: 
                time = 100;
                break;
            case 8: 
                time = 50;
                break;
            case 9: 
                time = 10;
                break;
            case 10: 
                time = 1;
                break;
            default: 
                time = 500;
                break;
        }
        
        clock.setSpeed(time);

        if (isRunning) {
            clock.timer.cancel();
            clock.run();
        }
    }

    /**
     * Returns the contents of the Instruction Register as an integer array.
     * 
     * @return the contents in the instruction register parsed as hexadecimal integer
     */
    public int[] getInstructionRegister() {
        String[] ir = machineView.getInstructionRegister().split(" ");
        int[] instructions = {Integer.parseInt(ir[0], 16), Integer.parseInt(ir[1], 16)};
        return instructions;
    }
    
    /**
     * Sets the Instruction Register to the byte code located by the machineViews 
     * instruction pointer value.
     */
    public void setInstructionRegister() {
        int ip = Integer.parseInt(machineView.getInstructionPointer(), 16);
            String[] newir = {machineView.getRAMBytes(ip-2).toUpperCase(), 
                            machineView.getRAMBytes(ip-1).toUpperCase()};
        machineView.setInstructionRegister(newir[0] + " " + newir[1]);
    }
    
    /**
     * Special setInstructionRegister for the jump instructions.
     */
    public void setInstructionRegisterForJump() {
        machineView.setInstructionRegister(MemoryAddressRegister[0] + " " 
                + MemoryAddressRegister[1]);
    }
        
    /**
     * Sets the instruction pointer to what the machineView instruction pointer.
     */
    public void setIPinstruction() {           
        int ip = Integer.parseInt(machineView.getInstructionPointer(), 16);
        String[] newir = {machineView.getRAMBytes(ip).toUpperCase(), 
                        machineView.getRAMBytes(ip+1).toUpperCase()};
        MemoryAddressRegister[0] = newir[0];
        MemoryAddressRegister[1] = newir[1];
    }
    
    /**
     * Returns an integer array that holds the instruction that the instruction pointer 
     * is pointing to.
     * 
     * @return integer array containing the instruction pointed to by the instruction
     * pointer.
     */
    public String[] getInstructionFromIP() {
        String[] ir = MemoryAddressRegister;
        if (ir[0] == null || ir[1] == null) {
            fatalMemErrorList.add("Error: Nothing Assembled into memory.");
            String[] invalid = {"00", "00"};
            return invalid;
        }
        else {
            String[] instructions = {ir[0], ir[1]};
            return instructions;
        }
    }
    
    /**
     * Updates the Instruction Point when the halt instruction has been reached.
     */
    public void updateIPwhenHalt(){
        machineView.setInstructionRegister("C0 00");
    }

    /**
     * Returns the Instruction Pointer that the machineView currently has.
     * 
     * @return Instruction Pointer
     */
    public int getInstructionPointer() {
        int ip = Integer.parseInt(machineView.getInstructionPointer(),16);
        if (ip % 2 != 0 && !nonFatalMemErrorList.contains("Potential Error: Instruction Pointer Misaligned")) {
            nonFatalMemErrorList.add("Potential Error: Instruction Pointer Misaligned");
            return ip;
        }
        else {
            //nonFatalMemErrorList.remove("Potential Error: Instruction Pointer Misaligned");
            //nonFatalMemErrorList.clear();
            //setMemoryErrors();
            return ip;
        }
    }

    /**
     * Set Instruction Pointer to the integer value passed after converting it to
     * a hex value.
     * @param ip - value to set the instruction pointer to.
     * 
     */
    public void setInstructionPointer(int ip) {
        String newIp = Integer.toHexString(ip).toUpperCase();
        if(newIp.length() == 1)
            newIp = "0" + newIp;
        machineView.setInstructionPointer(newIp);
    }

    /**
     * First resets the machine state. Then taking the provided instructionPointer String
     * and String array ramBytes to invoke the Disassembler for generating the text for the
     * Disassemble button action.
     * 
     * @param instructionPointer - the location of the currently executing instruction.
     * @param ramBytes - contents of memory starting at the instructionPointer.
     * @return text containing the current sip and all contents of the ram starting at the sip.
     */
    public String performDisassemble(String instructionPointer, String[] ramBytes) {
        resetMachine();

        Disassembler disassembler = new Disassembler(this);
        String text = disassembler.getDisassemble(instructionPointer, ramBytes);

        machineView.resetActivationRecords();
        machineView.getErrorTextArea().setVisible(false);
        machineView.revalidate();
        machineView.repaint();

        return text;
    }

    /**
    * Sets the machineView error text area to the passed in errorList.
    * 
    * @param errorList - list containing currently encountered errors.
    */
    public void setEditorErrors(ArrayList<String> errorList) {
        StringBuilder sb = new StringBuilder();

        for (String error : errorList) {
            sb.append(error).append('\n');
        }

        machineView.getErrorTextArea().setText(sb.toString());
    }

    /**
     * Displays the error that is wrong with the value
     * @param value
     */
    public void setEditorErrorVisible(boolean value) {
        machineView.getErrorTextArea().setVisible(value);
        machineView.revalidate();
        machineView.repaint();
    }
    
    /**
     * Displays the memory errors in the Memory Error Display
     */
    public void setMemoryErrors() {
        StringBuilder sb = new StringBuilder();
        
        for (String error : fatalMemErrorList) {
            sb.append(error).append("\n");
        }
        for (String error : nonFatalMemErrorList) {
            sb.append(error).append("\n");
        }
        machineView.getMemoryErrorTextArea().setText(sb.toString());
    }

    /**
     * 
     */
    public void refreshMachineView() {
        machineView.revalidate();
        machineView.repaint();
    }

    /**
     * 
     */
    public void disposeMachineView() {
        clock.timer.cancel();
        machineView.dispose();
    }

    /**
     * Calls setter method in model to update register value
     * @param index
     * @param value
     */
    public void setRegisterValue(int index, String value) {
        machineView.setRegisterBytes(value, index);
        refreshMachineView();
    }

    /**
     * Return register value as Hex string at index
     * @param index
     * @return
     */
    public String getRegisterValue(int index){
        return machineView.getRegisterBytes(index);
    }

    /**
     * Calls setter method in model to update a memory value
     * @param index
     * @param value
     */
    public void setMemoryValue(int index, String value) {
        if (index >= 255) {
            fatalMemErrorList.add("Error: Segmentation Fault - Invalid Memory Address");
        }
        else {
            machineView.setRAMBytes(value, index);
            refreshMachineView();
        }
    }

    /**
     * 
     * @param index
     * @return 
     */
    public String getMemoryValue(int index){
        if (index >= 255) {
            fatalMemErrorList.add("Error: Segmentation Fault - Invalid Memory Address");
            return "00";
        }
        else {
            return machineView.getRAMBytes(index);
        }
    }
    
    /**
     * Returns the fatalMemErrorList to the call.
     * 
     * @return String ArrayList containing all fatal errors encountered.
     */
    public ArrayList<String> getFatalRunTimeErrorList(){
        return fatalMemErrorList;
    }

     /**
     * Creates an activation record with the Instruction pointer
     * and base pointer specified.
     * @param ip
     * @param bp
     */
    public void createActivationRecord(int ip, int bp) {
        machineView.createActivationRecord(ip, bp);
    }

    /**
     * Deletes an activation record if needed.
     */
    public void deleteActivationRecord() {
        machineView.deleteActivationRecord();
    }
}
