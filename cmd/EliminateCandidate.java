package cmd;

import model.Cell;

public class EliminateCandidate extends AbstractAction implements Action {
	
	// ATTRIBUTS
	
	// CONSTRUCTEUR
	
	public EliminateCandidate(Cell cell, String value) {
		super(cell, value);
	}
	
	// COMMANDES
	
    @Override
    protected void doIt() {
    	getCell().eliminateCandidate(getValue());
    }
    
    @Override
    protected void undoIt() {
    	getCell().addCandidate(getValue());
    }
}
