package cmd;

import model.Cell;

public class AddCandidate extends AbstractAction implements Action {
	
	// ATTRIBUTS
	
	// CONSTRUCTEUR
	
	public AddCandidate(Cell cell, String value) {
		super(cell, value);
		if (value == null) {
			throw new AssertionError("Constructor value null : AddCandidate");
		}
	}
	
	// COMMANDES
	
    @Override
    protected void doIt() {
    	getCell().addCandidate(getValue());
    }
    
    @Override
    protected void undoIt() {
    	getCell().eliminateCandidate(getValue());
    }

}
