package misc;

import javax.swing.JOptionPane;

/**
 * Type d'exception juste pour les grilles de sudoku
 * @author fantovic
 */
public class GridException extends Exception  {
	private static final long serialVersionUID = 2415568828773892519L;

	public GridException() { 
		super(); 
	}
	
	public GridException(String message) { 
		super(message); 
	}
	
  	public GridException(String message, Throwable cause) { 
  		super(message, cause); 
	}

  	public GridException(Throwable cause) { 
  		super(cause); 
	}
  	
  	public static void ShowErrorMessage(String message, String title) {
  		JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
  	}

  	public static void ShowErrorMessage(String message) {
  		ShowErrorMessage(message, "Erreur !");
  	}
}
