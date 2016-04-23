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

/* Change Log
    #Guojun Liu  03/08/16 
    1. Create a new string array to hold the instruction in IP
    2. Create three new methods new CPU cycle
*/

package machine.controller;

import machine.model.Assembler;
import java.util.ArrayList;
//import machine.view.MachineView;
//import testCode.MachineView;
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
     * 
     */
    public MachineController() {
        machineView = new MachineView(this);

        for (int i = 0; i < 257; i++) {
            lastAssembledProg.add("00");
        }
    }

    /**
     * 
     * @return 
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Called by the Run button handler
     */
    public void runClock() {
        if (!isRunning) {
            isRunning = true;
            clock.run();
        }
    }

    /**
     * Called by Reset button handler
     */
    public void resetMachine() {
        isRunning = false;
        clock.timer.cancel();
        machineView.reset();
        fatalMemErrorList.removeAll(fatalMemErrorList);
        loadMachine(lastAssembledProg);
        refreshMachineView();
        machineView.getConsoleTextArea().setText("");
        machineView.getDisassTextArea().setText("No disassembler text yet");
        machineView.getMemoryErrorTextArea().setText("No Errors");
        machineView.resetActivationRecords();
    }

    /**
     * Called by Stop button handler
     */
    public void stopClock() {
        isRunning = false;
        clock.timer.cancel();
    }

    /**
     * Called by Step button handler
     */
    public void stepClock() {
        isRunning = false;
        clock.timer.cancel();
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
     * 
     */
    public void performAssemble() {
        resetMachine();
        Assembler assembler = new Assembler(this);
        String text = machineView.getEditorText();
        ArrayList<String> codes = assembler.parse(text);
        lastAssembledProg.clear();
        lastAssembledProg = codes;
        if (!machineView.getErrorTextArea().isVisible()) {
            loadMachine(codes);
            machineView.resetActivationRecords();
        }
    }
    
    /**
     * 
     * @param consoleText 
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
     * When code is assembled loadMachine
     * populates all registers and memory table
     * with appropriate values
     * @param Array of bitCode
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
     * setClockSpeed is used to change the clock speed.
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
     * 
     * @return The contents in the instruction register
     * parsed as hexadecimal integer
     */
    public int[] getInstructionRegister() {
        String[] ir = machineView.getInstructionRegister().split(" ");
        int[] instructions = {Integer.parseInt(ir[0], 16), Integer.parseInt(ir[1], 16)};
        return instructions;
    }
    
    /**
     * 
     */
    public void setInstructionRegister() {
        int ip = Integer.parseInt(machineView.getInstructionPointer(), 16);
        //original code
            /*String[] newir = {machineView.getRAMBytes(ip).toUpperCase(), 
                            machineView.getRAMBytes(ip+1).toUpperCase()};*/
            String[] newir = {machineView.getRAMBytes(ip-2).toUpperCase(), 
                            machineView.getRAMBytes(ip-1).toUpperCase()};
        machineView.setInstructionRegister(newir[0] + " " + newir[1]);
    }
    
    //update the current conent to IF before jump
    public void setInstructionRegisterForJump() {
        machineView.setInstructionRegister(MemoryAddressRegister[0] + " " 
                + MemoryAddressRegister[1]);
    }
        
    //set the current instruction register after the fetch phase
    public void setIPinstruction() {           
        int ip = Integer.parseInt(machineView.getInstructionPointer(), 16);
        String[] newir = {machineView.getRAMBytes(ip).toUpperCase(), 
                        machineView.getRAMBytes(ip+1).toUpperCase()};
        MemoryAddressRegister[0] = newir[0];
        MemoryAddressRegister[1] = newir[1];
    }
        
    public int[] getInstructionFromIP() {
        String[] ir = MemoryAddressRegister;
        int[] instructions = {Integer.parseInt(ir[0], 16),
                            Integer.parseInt(ir[1], 16)};
        return instructions;
    }
        
    public void updateIPwhenHalt(){
        machineView.setInstructionRegister("C0 00");
    }

    /**
     * Called by Clock to get Instruction Pointer
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
     * Called by Clock to set Instruction Pointer
     * with new value
     * @param value -
     */
    public void setInstructionPointer(int value) {
        String newValue = Integer.toHexString(value).toUpperCase();
        if(newValue.length() == 1)
            newValue = "0" + newValue;
        machineView.setInstructionPointer(newValue);
        //update machine view (highlight cell that IP is pointing to
        //This is the highlight cell function call - it is broken
//      try {
//          machineView.setHighlightedCell(value);
////        refreshMachineView(); // this doesn't help the issue
//	} catch (Exception e) {
//          Log.error("Caught an exception, not doing anything about it: " + e.getMessage());
//	}
    }

    /**
     * 
     * @param instructionPointer
     * @param ramBytes
     * @return 
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
    * Displays the errorList in the Editor depending on the boolean
    * value specified
    * @param errorList
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
