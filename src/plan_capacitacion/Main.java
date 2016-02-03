/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package plan_capacitacion;

import com.gruposcit.plancapacitacion.gui.Principal;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author leofavio_ar
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    new Principal();
                } catch (UnsupportedLookAndFeelException e) {
                    System.err.println(e.getMessage());
                    new Principal();
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                    new Principal();
                }
            }
        });
    }

}
