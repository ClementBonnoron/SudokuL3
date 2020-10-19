package cmd;

import model.Cell;

/**
 * D�finis le code principal d'une action.
 * Chaque action a un �tat et l'action effectu� d�pend
 * 	de l'�tat de l'action.
 * Si getState() == State.DO alors, ex�cutera {@link #doIt()}
 * Si getState()} == State.UNDO alors, ex�cutera {@link #undoIt()}
 * Les m�thodes doivent �tre red�finis dans les classes extendant
 * 	cette classe abstraite.
 * @author cleme
 * @construtor
 * 		$DESC$ Cr�er une action sur une cellule et une valeur.
 * 		$ARGS$ Cell cell, String value
 * 		$PRE$
 * 			cell != null
 * 		$POST$
 * 			getCell() == cell;
 * 			getValue() == value
 * 			getState() == State.DO
 */
public abstract class AbstractAction implements Action {
	
	// ATTRIBUTS
	
	private final Cell cell;
	private String value;
	private State state;
	
	// CONSTRUCTEUR
	
	public AbstractAction(Cell cell, String value) {
		if (cell == null) {
			throw new AssertionError("Constructor null : AbstractAction");
		}
		this.cell = cell;
		this.value = value;
		this.state = State.DO;
	}
	
	@Override
	public Cell getCell() {
		return this.cell;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public State getState() {
		return this.state;
	}

	@Override
	public boolean canDo() {
		return state == State.DO;
	}

	@Override
	public boolean canUndo() {
		return state == State.UNDO;
	}
	
	/**
	 * Acte l'action.
	 * @post
	 * 		(old getState()) == State.DO =>
	 * 			getState() == State.UNDO
	 * 			{@link #doIt()} est appel�}
	 * 		(old getState()) == State.UNDO =>
	 * 			getState() == State.DO
	 * 			{@link #undoIt()} est appel�}
	 */
	@Override
	public final void act() {
	    if (!canDo() && !canUndo()) {
	    	throw new AssertionError("act no state : AbstractAction");
	    }
	
	    if (canDo()) {
	        doIt();
	        state = State.UNDO;
	    } else { // nécessairement canUndo() == true
	        undoIt();
	        state = State.DO;
	    }
	}
    
    protected abstract void doIt();
    protected abstract void undoIt();

}
