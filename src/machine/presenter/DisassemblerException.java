/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package machine.presenter;

@SuppressWarnings("serial")
public class DisassemblerException extends Exception {
        
    String message = "Invalid byte entered at RAM address 0x";
    /**
     * 
     * @param address 
     */
    public DisassemblerException(int address) {
        String strAddress= Integer.toHexString(address).toUpperCase();
        if (strAddress.length() == 1) {
            strAddress = "0" + strAddress;
        }
        message += strAddress + ".";
    }

    /**
     * 
     * @return 
     */
    @Override
    public String getMessage() {
        return message;
    }
} 
