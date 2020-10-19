package model;

import java.beans.PropertyChangeListener;
import java.util.Set;

/**
 * D�finit une cellule, associ� � une grille.
 * Cette cellule � une coordonn�e qui lui es donn� lors
 * 	de sa cr�ation.
 * Cette cellule contient une valeur, ainsi qu'une liste de candidats.
 * 	La valeur n'est pas forc�ment comprises parmis les candidats
 * Des PropertyChangeListeners peuvent �tre ajouter � une cellule
 * 	� l'aide des m�thodes 
 * 	{@link #addPropertyChangeListener(String, PropertyChangeListener)}
 * 	et {@link #removePropertyChangeListener(String, PropertyChangeListener)}.
 * Ces PCL peuvent d�tecter ma modification des beans 
 * {@code currentValue} et {@code candidates} d�finis par les macro
 * {@link #PROP_VALUE} et {@link #PROP_CANDIDATES}.
 * @author cleme
 * @constructor
 * 		$DESC$ Renvoie une cellule associ� � une grille.
 * 		$ARGS$ Grid grid, BoundedCoordinate coord
 * 		$pre$
 * 			grid != null
 * 		$post$
 * 			getGrid() == grid
 * 			getCoordinate() coord
 * 	
 */
public interface Cell {

	public static final String PROP_VALUE = "currentValue";
	public static final String PROP_CANDIDATES = "candidates";
	public static final String PROP_BLOCKED = "blocked";
	
	// REQUETES
	
	/**
	 * Renvoie vrai ssi value peut �tre d�fini comme valeur pour la cellule.
	 * @param value
	 * @return boolean
	 */
	boolean isPossible(String value);
	
	/**
	 * Renvoie vrai ssi la cellule ne peut plus �tre modifi�.
	 * @return boolean
	 */
	boolean isBlocked();
	
	/**
	 * Renvoie la valeur courante.
	 * Correspond au bean {@link #PROP_VALUE}.
	 * @return valeur de {@link #PROP_VALUE}.
	 */
	String getValue();
	
	/**
	 * Renvoie une cha�ne de caract�res d�finissant la cellule.
	 * @return "(" + getValue() + ";" + {@code forall s:getValue() ",s"} +
	 * 		 ";" + getCoordinate() + ")"
	 */
	String toString();
	
	/**
	 * Renvoie les coordonn�es de cette cellule.
	 * @return coord
	 */
	BoundedCoordinate getCoordinate();
	
	/**
	 * Renvoie la grille correspondante � cette cellule.
	 * @return
	 */
	Grid getGrid();
	
	/**
	 * Renvoie la liste des candidats de cette cellules si getValue() != null,
	 * 	sinon renvoie null
	 * Correspond au bean {@link #PROP_CANDIDATES}.
	 * @return valeur de {@link #PROP_CANDIDATES}.
	 */
	Set<String> getCandidates();
	
	// COMMANDES
	
	/**
	 * D�finie la cellule comme bloqu�
	 * @pre
	 * 		!isBlocked()
	 */
	void block();
	
	/**
	 * D�finie la cellule comme d�bloqu�
	 * @pre
	 * 		!isBlocked()
	 */
	void unblock();
	
	/**
	 * Modifie la valeur de la cellule.
	 * Notifie les PCL pos�s sur le bean {@link #PROP_VALUE}.
	 * Si la cellule est bloqu�, alors ne fais rien.
	 * @param value
	 * @post
	 * 		getValue() == value
	 */
	void setValue(String value);
	
	/**
	 * Retire la valeur de la cellule.
	 * Notifie les PCL pos�s sur le bean {@link #PROP_VALUE}.
	 * Si la cellule est bloqu�, alors ne fais rien.
	 * @post
	 * 		getValue() == null
	 */
	void removeValue();
	
	/**
	 * Ajout value aux candidats.
	 * Notifie les PCL pos�s sur le bean {@link #PROP_CANDIDATES}.
	 * Si la cellule est bloqu�, alors ne fais rien.
	 * @param value
	 * @post
	 * 		{@code getCandidates().contains(value)}
	 */
	void addCandidate(String value);

	/**
	 * Retire value des candidats possible.
	 * Notifie les PCL pos�s sur le bean {@link #PROP_CANDIDATES}.
	 * Si la cellule est bloqu�, alors ne fais rien.
	 * @param value
	 * @post
	 * 		{@code !getCandidates().contains(value)}
	 */
	void eliminateCandidate(String value);
	
	// LISTENERS
	
	/**
	 * Ajout listener comme PCL sur le bean de nom {@code pname}.
	 * Si {@code listener == null}, alors ne fais rien.
	 * @param pName
	 * @param listener
	 * @post
	 * 		listener != null => Lorsque le bean de nom {@code pName} sera modifi�,
	 * 			alors listener sera notifi�.
	 */
	void addPropertyChangeListener(String pName, PropertyChangeListener listener);

	/**
	 * Retire la PCL listener du bean de nom {@code pname}.
	 * Si {@code listener == null}, alors ne fais rien.
	 * @param pName
	 * @param listener
	 * @post
	 * 		listener != null => Lorsque le bean de nom {@code pName} sera modifi�,
	 * 			alors listener ne sera plus notifi�.
	 */
	void removePropertyChangeListener(String pName, PropertyChangeListener listener);

	/**
	 * Renvoie la liste des listeners de la cellule.
	 * @post
	 *		Contient la liste des PCL
	 */
	PropertyChangeListener[] getPropertyChangeListeners();
	
	/**
	 * Renvoie la liste des listeners de nom {@code name} de la cellule.
	 * @post
	 *		Contient la liste des PCL pos� sur {@code name}
	 */
	PropertyChangeListener[] getPropertyChangeListeners(String name);
	 
}