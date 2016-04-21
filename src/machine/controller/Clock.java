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
 * 1 jl948836 - 02/20/16: Added a third parameter to immediateLoad; Feeds in
 *                        third Nibble of instruction; If it is hex F; then
 *                        the immediateLoad is part of the rload instruction,
 *                        and registerF shouldn't print a value
 * 
 * 3 jl948836 - 03/24/16: The additional 1 is now generated at byte code, instead
 *                        of by the clock
 * 
 * 4 jl948836 - 04/01/16: Fixed the SCALL and SRET functions.
 * 
 * 5 jl948836 - 04/01/16: Corrected format of ByteCode for iload and move
 * 
 * 6 jl948836 - 04/01/16:
 * 4 jl948836 - 04/01/16: Fixed the SCALL and SRET functions
 * 
 * 5 mv935583 - 04/11/16: Implemented changed to add shift instructions.
 * 
 * 6 jl948836 - 04/19/16: flipped RSTORE, register operands were backward.
 */

/* Change Log
    #Guojun Liu  03/08/16 
    1. Modified the fetch phase
*/

package machine.controller;

import java.util.Timer;
import java.util.TimerTask;


public class Clock {

    private final MachineController controller;
    Timer timer = new Timer();
    private TimerTask task;
    private int instructionPointer;
    private final Disassembler disassembler = new Disassembler();
    private int speed;
	
    /**
    * Creates a Clock object every time an instruction is executed
    * @param controller
    */
    public Clock(MachineController controller) {
        this.controller = controller;
    }

    /**
     * 
     * @param speed 
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * 
     */
    public void run() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
               step();
            }
        };
        timer.scheduleAtFixedRate(task, 0, speed);
    }

    /**
    * Calls the fetch method and cancels the timer
    * once the Instruction Register is greater than 0xFF
    */
    public synchronized void step() {
        if (controller.getInstructionPointer() > 0xFF) {
            timer.cancel();
        }
        fetch();
    }

    /**
    * Gets the instruction to be executed and calls the decode method
    */
    private void fetch() {
        //System.out.println("Fetch Me");
        //original fetch phase
	//int[] instructions = controller.getInstructionRegister();
        int[] instructions = controller.getInstructionFromIP();
        decode(instructions);
    }

    /**
    * Performs the instructions that were fetched.
    * @param instructions
    */
    private void decode(int[] instructions) {
        //System.out.println("Decode Me");
        // get the opcode
        String operation = Integer.toHexString(instructions[0]);
        String operand = Integer.toHexString(instructions[1]);
        // if operation a single digit concatenate 0 to beginning 
        if(operation.length() == 1)
            operation = "0" + operation;
        if(operand.length() == 1)
            operand = "0" + operand;
        // first nibble of opcode
        char firstOpcode = Character.toUpperCase(operation.charAt(0));
        // second nibble of opcode
        char secondOpcode = Character.toUpperCase(operation.charAt(1));
        // second Byte of opcode
        char firstOperand = Character.toUpperCase(operand.charAt(0));
        char secondOperand = Character.toUpperCase(operand.charAt(1));
        int secondNibble = Character.digit(secondOpcode,16);
        int thirdNibble = Character.digit(firstOperand,16);
        int fourthNibble = Character.digit(secondOperand,16);
        int secondByte = Integer.parseInt(operand, 16);
        //System.out.println("Opcode Decoded: " + firstOpcode + secondOpcode);
        switch(firstOpcode){
            case '1':
                directLoad(secondNibble,secondByte);
                execute();
                break;
            case '2':
                //CHANGE LOG BEGIN - 1
                //immediateLoad(secondNibble,secondByte);
                immediateLoad(secondNibble, secondByte, thirdNibble);
                //CHANGE LOG END - 1
                execute();
                break;
            case '3': 
                directStore(secondNibble, secondByte);
                execute();
                break;
            case '4': //CHANGE LOG: 6
                //move(thirdNibble,fourthNibble);
                rload(secondNibble, thirdNibble, fourthNibble);
                execute();
                break;
            case '5': 
                add(secondNibble,thirdNibble,fourthNibble);
                execute();
                break;
            case '6': 
                switch (secondOpcode) {
                case '0':
                    // regular call
                    call();
                    execute(secondByte);
                    break;
                case '1':
                    // regular return
                    ret(secondByte);
                    execute(controller.getInstructionPointer());
                    break;
                case '2':
                    scall();
                    execute(secondByte);
                    break;
                case '3':
                    sret(secondByte);
                    execute(controller.getInstructionPointer());
                    break;
                case '4':
                    // push
                    push(thirdNibble);
                    execute();
                    break;
                case '5':
                    // pop
                    pop(thirdNibble);
                    execute();
                    break;
                default:
                    break;
                }
                break;
            case '7': 
                or(secondNibble,thirdNibble,fourthNibble);
                execute();
                break;
            case '8': 
                and(secondNibble,thirdNibble,fourthNibble);
                execute();
                break;
            case '9': 
                xor(secondNibble,thirdNibble,fourthNibble);
                execute();
                break;
            case 'A':
                switch (secondOpcode) { //BEGIN CHANGE LOG: 5
                    case '0':
                        ror(thirdNibble, fourthNibble);
                        execute();
                        break;
                    case '1':
                        rol(thirdNibble, fourthNibble);
                        execute();
                        break;
                    case '2':
                        sra(thirdNibble, fourthNibble);
                        execute();
                        break;
                    case '3':
                        srl(thirdNibble, fourthNibble);
                        execute();
                        break;
                    case '4':
                        sl(thirdNibble, fourthNibble);
                        execute();
                        break;
                    default:
                        break;
                }
                break;  //END CHANGE LOG: 5
            case 'B':
                if (jump(secondNibble)) {
                        execute(secondByte); //jmp
                } else {
                        execute(); //jmpeq
                }
                break;
            // halt operation
            case 'C': halt();
                controller.updateIPwhenHalt();
                break;
            case 'D': 
                if (secondOpcode == '0') { //ILOAD
                    iload(thirdNibble, fourthNibble);
                } 
                else if (secondOpcode == '1') { //ISTORE
                    istore(thirdNibble, fourthNibble);
                } 
                else if (secondOpcode == '2') { //MOVE
                    move(thirdNibble,fourthNibble); //CHANGE LOG: 5
                    //rload(thirdNibble, fourthNibble);
                }
                execute();
                break;
            case 'E':
                rstore(secondNibble, thirdNibble, fourthNibble);
                execute();
                break;
            case 'F':
                if(jmplt(secondNibble)){ // change "jmple" to "jmplt"
                    execute(secondByte);
                } else {
                    execute();
                }
                break;
            default: 
                execute();
                break;
        }
        updateDisassembleDisplay();
    }

    /**
    * Regular execute
    */
    private void execute() {
        //System.out.println("Execute Me: Method 1");
        //System.out.println("********************* END TEST **********************");
        instructionPointer = controller.getInstructionPointer();
        instructionPointer += 2;
        controller.setInstructionPointer(instructionPointer);
        controller.setIPinstruction();    //update MAR
        controller.setInstructionRegister();
        controller.refreshMachineView();
    }

    /**
    * Overloaded execute for jump instructions
    * @param location
    */
    private void execute(int location) {
        //System.out.println("Execute Me: Method 2");
        //System.out.println("********************* END TEST **********************");
        instructionPointer = controller.getInstructionPointer();
        instructionPointer = location;
        controller.setInstructionPointer(instructionPointer);
        controller.setInstructionRegisterForJump();
        controller.setIPinstruction();    //update MAR
        //controller.setInstructionRegister();
        controller.refreshMachineView();
    }

    /**
    * Opcode 1 - LOAD
    * @param register
    * @param memIndex
    */
    private void directLoad(int register,int memIndex) {
        String data = controller.getMemoryValue(memIndex);
        controller.setRegisterValue(register, data);
        if (register == 0x0F){
            printRegisterF();
        }
    }

    /**
    * Opcode 2 - LOAD
    * @param register
    * @param memIndex
    */
    //CHANGE LOG BEGIN - 1
    private void immediateLoad(int register, int memIndex, int rloadFlag) {
        String memory = Integer.toHexString(memIndex);
        controller.setRegisterValue(register, memory);
        if (register == 0x0F){ // && rloadFlag != 15){
            printRegisterF();
        }
    }
    //CHANGE LOG END - 1

    /**
    * Opcode 3 - STORE
    * @param register
    * @param memIndex
    */
    private void directStore(int register, int memIndex) {
        String value = controller.getRegisterValue(register);
        controller.setMemoryValue(memIndex, value);
    }

    /**
    * Opcode 4 - RLOAD
    * @param register
    * @param pointer
    */
    private void rload(int offset, int register, int pointer) {
        //String offset = controller.getRegisterValue(register);
        //int realOffset = Character.digit(offset.charAt(1), 16);
        if (offset > 7) {
            offset -= 16;
        }
        int address = Integer.parseInt(controller.getRegisterValue(pointer), 16);
        int offsetAddress = address + offset;
        String data = controller.getMemoryValue(offsetAddress);
        controller.setRegisterValue(register, data);
        if (register == 0x0F){
            printRegisterF();
        }
    }

    /**
    * Opcode 5 - ADD
    * @param resultRegister
    * @param oneRegister
    * @param twoRegister
    */
    private void add(int resultRegister, int oneRegister, int twoRegister) {
        int x = Integer.parseInt(controller.getRegisterValue(oneRegister),16);
        int y = Integer.parseInt(controller.getRegisterValue(twoRegister),16);
        int result = (x + y) & 0xFF;
        String value = Integer.toHexString(result);
        controller.setRegisterValue(resultRegister, value);
        if (resultRegister == 0x0F){
            printRegisterF();
        }
    }

    /**
    * Opcode 60 - CALL
    */
    private void call() {
        int ip = controller.getInstructionPointer();
        int sp = Integer.parseInt(controller.getRegisterValue(0xE),16)-1;
        int bp = Integer.parseInt(controller.getRegisterValue(0xD), 16);
        String stackPointer = Integer.toHexString(sp);
        controller.setRegisterValue(0xE, stackPointer);
        controller.setMemoryValue(sp, Integer.toHexString(ip+2));
        controller.createActivationRecord(ip,bp);
    }

    /**
    * Opcode 61 - RETURN
    */
    private void ret(int spAdd) {
        int sp = Integer.parseInt(controller.getRegisterValue(0xE),16);
        int value = Integer.parseInt(controller.getMemoryValue(sp),16);
        controller.setInstructionPointer(value);
        sp += spAdd; //CHANGE LOG: 3
        String stackPointer = Integer.toHexString(sp);
        controller.setRegisterValue(0xE, stackPointer);
        controller.deleteActivationRecord();
    }
    /**
    * Opcode 62 - SCALL
    */
    //CHANGE LOG BEGIN: 4
    private void scall() {
        call();
        push(0x0D);
        move(0x0D,0x0E);
    }
    //CHANGE LOG END: 4

    /**
    * Opcode 63 - SRETURN
    */
    //CHANGE LOG BEGIN: 4
    private void sret(int spAdd) {
        pop(0x0D); //Reset Frame of Reference
        ret(spAdd); //Return
        move(0x0E,0x0D); //Clean arguments off the Stack
    }
    //CHANGE LOG END: 4

    /**
    * Opcode 64 - PUSH
    * @param register
    */
    private void push(int register) {
        String value = controller.getRegisterValue(register);
        int sp = Integer.parseInt(controller.getRegisterValue(0xE),16) - 1;
        String stackPointer = Integer.toHexString(sp);
        controller.setRegisterValue(0xE, stackPointer);
        controller.setMemoryValue(sp, value);
    }

    /**
    * Opcode 65 - POP
    * @param register
    */
    private void pop(int register) {
        int sp = Integer.parseInt(controller.getRegisterValue(0xE),16);
        String value = controller.getMemoryValue(sp);
        controller.setRegisterValue(register, value);
        sp += 1;
        String stackPointer = Integer.toHexString(sp);
        controller.setRegisterValue(0xE, stackPointer);
        if (register == 0x0F){
            printRegisterF();
        }
    }
    /**
    * Opcode 7 - OR
    * @param resultRegister
    * @param oneRegister
    * @param twoRegister
    */
    private void or(int resultRegister, int oneRegister, int twoRegister) {
        int x = Integer.parseInt(controller.getRegisterValue(oneRegister),16);
        int y = Integer.parseInt(controller.getRegisterValue(twoRegister),16);
        int result = x | y;
        String value = Integer.toHexString(result);
        controller.setRegisterValue(resultRegister, value);
        if (resultRegister == 0x0F){
            printRegisterF();
        }
    }

    /**
    * Opcode 8 - AND
    * @param resultRegister
    * @param oneRegister
    * @param twoRegister
    */
    private void and(int resultRegister, int oneRegister, int twoRegister) {
        int x = Integer.parseInt(controller.getRegisterValue(oneRegister),16);
        int y = Integer.parseInt(controller.getRegisterValue(twoRegister),16);
        int result = x & y;
        String value = Integer.toHexString(result);
        controller.setRegisterValue(resultRegister, value);	
        if (resultRegister == 0x0F){
            printRegisterF();
        }
    }

    /**
    * Opcode 9 - XOR
    * @param resultRegister
    * @param oneRegister
    * @param twoRegister
    */
    private void xor(int resultRegister, int oneRegister, int twoRegister) {
        int x = Integer.parseInt(controller.getRegisterValue(oneRegister),16);
        int y = Integer.parseInt(controller.getRegisterValue(twoRegister),16);
        int result = x ^ y;
        String value = Integer.toHexString(result);
        controller.setRegisterValue(resultRegister, value);	
        if (resultRegister == 0x0F){
            printRegisterF();
        }
    }

    /**
    * Opcode A - ROR
    * @param register
    * @param times
    */
    private void ror(int register, int times) {
        int bitPattern = Integer.parseInt(controller.getRegisterValue(register), 16);
        int carry;
        int shifted;
        for (int i = 0; i < times; i++){
            carry = bitPattern << 7;
            shifted = bitPattern >> 1;
            bitPattern = (carry | shifted) & 0xFF;
        }
        controller.setRegisterValue(register, Integer.toHexString(bitPattern));
        if (register == 0x0F){
            printRegisterF();
        }
    }
    
    private void rol(int register, int times){  //BEGIN CHANGE LOG: 5
        int bitPattern = Integer.parseInt(controller.getRegisterValue(register), 16);
        int carry;
        int shifted;
        for (int i = 0; i < times; i++){
            carry = bitPattern >> 7;
            shifted = bitPattern << 1;
            bitPattern = (carry | shifted) & 0xFF;
        }
        controller.setRegisterValue(register, Integer.toHexString(bitPattern));
        if (register == 0x0F){
            printRegisterF();
        }
    }
    
    private void sra(int register, int times){
        int bitPattern = Integer.parseInt(controller.getRegisterValue(register), 16);
        for (int i = 0; i < times; i++){
            int leadingBit = bitPattern & 0x80;
            bitPattern = bitPattern >> 1;
            bitPattern = (leadingBit | bitPattern) & 0xFF;
        }
        controller.setRegisterValue(register, Integer.toHexString(bitPattern));
        if (register == 0x0F){
            printRegisterF();
        }
    }
    
    private void srl(int register, int times){
        int bitPattern = Integer.parseInt(controller.getRegisterValue(register), 16);
        for (int i = 0; i < times; i++){
            bitPattern = bitPattern >>> 1;
            bitPattern = bitPattern & 0xFF;
        }
        controller.setRegisterValue(register, Integer.toHexString(bitPattern));
        if (register == 0x0F){
            printRegisterF();
        }
    }
    
    private void sl(int register, int times){
        int bitPattern = Integer.parseInt(controller.getRegisterValue(register), 16);
        for (int i = 0; i < times; i++){
            bitPattern = bitPattern << 1;
            bitPattern = bitPattern & 0xFF;
        }
        controller.setRegisterValue(register, Integer.toHexString(bitPattern));
        if (register == 0x0F){
            printRegisterF();
        }
    }   //END CHANGE LOG: 5

    /**
    * Opcode B - JMPEQ and JMP
    * @param register
    * @return
    */
    private boolean jump(int register) {
        return (controller.getRegisterValue(register).equals(controller.getRegisterValue(0)));
    }

    /**
    * Opcode C - HALT
    */
    private void halt() {
        timer.cancel();
    }

    /**
    * Opcode D0 - ILOAD
    * @param register
    * @param pointer
    */
    private void iload(int register, int pointer) {
        String address = controller.getRegisterValue(pointer);
        String value = controller.getMemoryValue(Integer.parseInt(address,16));
        controller.setRegisterValue(register, value);
        if (register == 0x0F){
            printRegisterF();
        }
    }
    
    /**
    * Opcode D1 - ISTORE
    * @param register
    * @param pointer
    */
    //CHANGE LOG: 5
    private void istore(int pointer, int register) {
           String value = controller.getRegisterValue(register);
           int address = Integer.parseInt(controller.getRegisterValue(pointer),16);
           controller.setMemoryValue(address, value);
    }

    /**
    * Opcode D2 - MOVE
    * @param fromRegister
    * @param toRegister
    */
    //CHANGE LOG: 5
    private void move(int toRegister, int fromRegister) {
        String data = controller.getRegisterValue(fromRegister);
        controller.setRegisterValue(toRegister, data);
        if (toRegister == 0x0F){
            printRegisterF();
        }
    }
    
    /**
    * Opcode E - RSTORE
    * @param offset
    * @param register
    * @param pointer
    */
    private void rstore(int offset, int pointer, int register) { //CHANGE LOG: 6
        if(offset > 7) {
            offset -= 16;
        }
        String value = controller.getRegisterValue(register);
        int address = Integer.parseInt(controller.getRegisterValue(pointer),16);
        int offsetAddress = address + offset;
        controller.setMemoryValue(offsetAddress, value);
    }
    
    /**
    * Opcode F - JMPLT
    * @param register
    * @return
    */
    private boolean jmplt(int register) { // change "jmple" to "jmplt"
        int value = Integer.parseInt(controller.getRegisterValue(register),16);
        int registerZero = Integer.parseInt(controller.getRegisterValue(0),16);
        //System.out.println("Comparison Value: " + value + "Register 0: " + registerZero);
        //System.out.println("Result: " + (value < registerZero));
//      if (registerZero > 127) {
//            registerZero -= 256;
//        }
//        if (value > 127) {
//            value -= 256;
//        }
        return (value < registerZero); //change "<=" to "<"
    }

    /**
    * Prints out register F.  
    */
    private void printRegisterF() {
        char c = ' ';
        int temp = Integer.parseInt(controller.getRegisterValue(15), 16);
        if ((temp >= 32 && temp < 127) || temp == '\n') {
            c = (char)temp;
        }
        controller.setConsoleText(c);
    }

    /**
    * Updates the disassemble console text after each execute
    */
    private void updateDisassembleDisplay() {
        boolean update = true;
        // set disassembler console text
        // if IP is odd for whatever reason, we aren't updating the
        // disassembler console
        int IP = controller.getInstructionPointer();
        int relativeIP = 6;
        String[] codes = {"","","","","","","","","","","","","",""};
        if ( (IP > 5 && IP < 249) && ( (IP % 2) == 0)) { // this should test first
            codes[0] = controller.getMemoryValue(IP - 6);
            codes[1] = controller.getMemoryValue(IP - 5);
            codes[2] = controller.getMemoryValue(IP - 4);
            codes[3] = controller.getMemoryValue(IP - 3);
            codes[4] = controller.getMemoryValue(IP - 2);
            codes[5] = controller.getMemoryValue(IP - 1);
            codes[6] = controller.getMemoryValue(IP); // << IP here
            codes[7] = controller.getMemoryValue(IP + 1);
            codes[8] = controller.getMemoryValue(IP + 2);
            codes[9] = controller.getMemoryValue(IP + 3);
            codes[10] = controller.getMemoryValue(IP + 4);
            codes[11] = controller.getMemoryValue(IP + 5);
            codes[12] = controller.getMemoryValue(IP + 6);
            codes[13] = controller.getMemoryValue(IP + 7);
            relativeIP = 3;
        } 
        else if (IP < 5) { // got to handle the edge cases...
            codes[0] = controller.getMemoryValue(0);
            codes[1] = controller.getMemoryValue(1);
            codes[2] = controller.getMemoryValue(2);
            codes[3] = controller.getMemoryValue(3);
            codes[4] = controller.getMemoryValue(4);
            codes[5] = controller.getMemoryValue(5);
            codes[6] = controller.getMemoryValue(6);
            codes[7] = controller.getMemoryValue(7);
            codes[8] = controller.getMemoryValue(8);
            codes[9] = controller.getMemoryValue(9);
            codes[10] = controller.getMemoryValue(10);
            codes[11] = controller.getMemoryValue(11);
            codes[12] = controller.getMemoryValue(12);
            codes[13] = controller.getMemoryValue(13);
            relativeIP = IP / 2;
        } 
        else if (IP > 249) {
            codes[0] = controller.getMemoryValue(242);
            codes[1] = controller.getMemoryValue(243);
            codes[2] = controller.getMemoryValue(244);
            codes[3] = controller.getMemoryValue(245);
            codes[4] = controller.getMemoryValue(246);
            codes[5] = controller.getMemoryValue(247);
            codes[6] = controller.getMemoryValue(248);
            codes[7] = controller.getMemoryValue(249);
            codes[8] = controller.getMemoryValue(250);
            codes[9] = controller.getMemoryValue(251);
            codes[10] = controller.getMemoryValue(252);
            codes[11] = controller.getMemoryValue(253);
            codes[12] = controller.getMemoryValue(254);
            codes[13] = controller.getMemoryValue(255);
            if (IP == 250) { 
                relativeIP = 4;
            } else if (IP == 252) {
                relativeIP = 5;
            } else {
                relativeIP = 6;
            }
        } 
        else { // IP is odd... 
            update = false;
        }

        if (update) {
            // send codes off to disassembler
            String consoleText;
//            if (codes[0].equals("D2")) { // handle rload specially
//                String loadByte1 = "";
//                String loadByte2 = "";
//                if ( (IP > 5) && (IP < 249) ) { 
//                    loadByte1 = controller.getMemoryValue(IP - 8);
//                    loadByte2 = controller.getMemoryValue(IP - 7);
//                    relativeIP++;
//                } 
//                else if (IP > 249) {
//                    loadByte1 = controller.getMemoryValue(240);
//                    loadByte2 = controller.getMemoryValue(241);
//                    relativeIP++;
//                }
//                
//                String[] fixedCodes = new String[16];
//                System.arraycopy(codes, 0, fixedCodes, 2, 14);
//                /*for (int i = 0; i < 14; i++) {
//                        fixedCodes[i+2] = codes[i];
//                }*/
//                fixedCodes[0] = loadByte1;
//                fixedCodes[1] = loadByte2;
//                consoleText = disassembler.getConsoleDisassemble(fixedCodes, relativeIP);
//            } else {
                consoleText = disassembler.getConsoleDisassemble(codes, relativeIP);
            //}
            controller.setDisassText(consoleText);
        }	
    }
}
