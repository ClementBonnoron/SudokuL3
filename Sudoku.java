import javax.swing.SwingUtilities;

import graphic.Window;

public class Sudoku {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	new Window().display();
            }
		});
	}
}
