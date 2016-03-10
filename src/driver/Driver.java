package driver;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import machine.presenter.MachineController;
//edited by Mariela Barrera
//This version of the Driver class works without the dashbaord that was included in the orginal one
public class Driver {

	public static void main(String[] args) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                Logger.getLogger(Driver.class.getName()).log(Level.SEVERE, null, ex);
            }
           MachineController MachineController = new MachineController();
            }
        }


     


