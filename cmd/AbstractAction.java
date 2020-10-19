package cmd;

import model.Cell;

/**
 * Définis le code principal d'une action.
 * Chaque action a un état et l'action effectué dépend
 * 	de l'état de l'action.
 * Si getState() == State.DO alors, exécutera {@link #doIt()}
 * Si getState()} == State.UNDO alors, exécutera {@link #undoIt()}
 * Les méthodes doivent être redéfinis dans les classes extendant
 * 	cette classe abstraite.
 * @author cleme
 * @construtor
 * 		$DESC$ Créer une action sur une cellule et une valeur.
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
	 * 			{@link #doIt()} est appelé}
	 * 		(old getState()) == State.UNDO =>
	 * 			getState() == State.DO
	 * 			{@link #undoIt()} est appelé}
	 */
	@Override
	public final void act() {
	    if (!canDo() && !canUndo()) {
	    	throw new AssertionError("act no state : AbstractAction");
	    }
	
	    if (canDo()) {
	        doIt();
	        state = State.UNDO;
	    } else { // nÃ©cessairement canUndo() == true
	        undoIt();
	        state = State.DO;
	    }
	}
    
    protected abstract void doIt();
    protected abstract void undoIt();

}
