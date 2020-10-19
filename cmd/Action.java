package cmd;

import model.Cell;

/**
 * D�finis une action.
 * Une action est ex�cut� sur une cellule, et contient une
 * 	valeur.
 * La s�mantique de l'action ne pourra �tre compl�te que dans les classes
 *  qui impl�menteront cette interface.
 * @inv <pre>
 *     getState() != null
 *     getCell() != null
 *     canDo() ==> getState() == State.DO
 *     canUndo() ==> getState() == State.UNDO </pre>
 * @author cleme
 */
public interface Action {
	
	/**
	 * Renvoie la cellule correspondante � l'action
	 * @return cell
	 * @post
	 * 		getCell() != null
	 */
	Cell getCell();
	
	/**
	 * Renvoie la valeur correspondante � l'action.
	 * @return
	 */
	String getValue();
	
	/**
	 * Renvoie l'�tat actuel de l'action
	 * @return State
	 * @post
	 * 		getState() == State.DO || 
	 * 			getState() == State.UNDO
	 */
	State getState();
	
	/**
	 * Renvoie vrai ssi l'action peut �tre faite.
	 * @return boolean
	 * @post
	 * 		canDo() <=> getState() == State.DO
	 */
	boolean canDo();

	/**
	 * Renvoie vrai ssi l'action peut �tre d�faite.
	 * @return boolean
	 * @post
	 * 		canDo() <=> getState() == State.UNDO
	 */
	boolean canUndo();
	
	/**
	 * Fais l'action.
	 * La m�thode sera mieux d�fini dans les classes l'impl�mentant.
	 */
	void act();
}
