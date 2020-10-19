package graphic;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import misc.GridZoneThread;
import model.Cell;
import model.Grid;

/**
 * Le GridViewerModel fonctionne avec le GridViewer.
 * Elle repr�sente le fonctionnement interne de la Grille graphique.
 * @author fantovic
 */
class GridViewerModel {
	// CONSTANTES
	/**
	 * Nom de la propri�t� de grille
	 */
	public static final String PROP_GRID = "grid";
	
	// ATTRIBUTS
	private Grid grid;
	
	private PropertyChangeSupport propSup;
	
	// CONSTRUCTEUR 
	/**
	 * Cr�e un nouveau GridViewerModel avec comme grille g
	 * @param g une grille nulle ou non
	 */
	public GridViewerModel(Grid g) {
		grid = g;
		propSup = new PropertyChangeSupport(this);
	}
	
	// REQUETES
	/**
	 * Renvoie la grille actuelle du mod�le
	 * @return la grille du mod�le
	 */
	public Grid getGrid() {
		return grid;
	}
	
	/**
	 * Renvoie la cellule en (x, y) de la grille actuelle
	 * @param x position horizontale de la cellule
	 * @param y position verticale de la cellule
	 * @return la cellule recherch�e
	 */
	public Cell getCell(int x, int y) {
		return grid.getCellAt(x, y);
	}
	
	// COMMANDES
	/**
	 * Remplace la grille courante par grid
	 * @param grid
	 */
	public void setGrid(Grid grid) {
		Grid old = this.grid;
		this.grid = grid;
		old.copySharedPCL(grid);
		propSup.firePropertyChange(PROP_GRID, old, grid);
	}
	
	/**
	 * Prépare la grille courante pour le mode éditeur
	 */
	public void editorGrid() {
		GridZoneThread[] gzts = new GridZoneThread[9];
    	for (int i = 0; i < 9; i++) {
    		gzts[i] = new GridZoneThread(getGrid(), i) {
				@Override
				public void action(Cell c) {
					if (c.getValue() != null) {
						if (c.isBlocked()) {
							c.unblock();
						} else {
							c.removeValue();
						}
					}
					for (String v : getGrid().getValues()) {
						c.eliminateCandidate(v);
					}
				}
			};
			gzts[i].run();
    	}
    	for (int i = 0; i < 9; i++) {
    		try {
				gzts[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
	}
	
	/**
	 * Assigne (si possible) la valeur value � la case de position (x, y) si celle-ci existe.
	 * @param value Valeur � mettre dans la case
	 * @param x Coordonn�e horizontale de la case
	 * @param y Coordonn�e verticale de la case
	 */
	public void setValue(String value, int x, int y, boolean advanced) {
		grid.setValue(x,  y, value, advanced);
	}

	/**
	 * Assigne (si possible) la valeur value � la case selectedCell si elle n'est pas nulle.
	 * @param value
	 * @param selectedCell
	 */
	public void setValue(String value, Cell selectedCell, boolean advanced) {
		if (selectedCell != null) {
			setValue(value, selectedCell.getCoordinate().getX(), selectedCell.getCoordinate().getY(), advanced);
		}
	}
	
	/**
	 * Retire la valeur (si elle existe) de coordon�e x:y
	 * @param x
	 * @param y
	 */
	public void removeValue(int x, int y, boolean advanced) {
		grid.removeValue(x, y, advanced);
	}
	
	/**
	 * Retire la valeur (si elle existe) de la cellule selectedCell
	 * @param x
	 * @param y
	 */
	public void removeValue(Cell selectedCell, boolean advanced) {
		if (selectedCell != null) {
			removeValue(selectedCell.getCoordinate().getX(), selectedCell.getCoordinate().getY(), advanced);
		}
	}
	
	/**
	 * Bloque les valeurs de toutes les cases ayant une valeur
	 */
	public void blockValues() {
		for (int x = 0; x < grid.getSize(); x++) {
			for (int y = 0; y < grid.getSize(); y++) {
				if (grid.getValueFrom(x, y) != null)
					grid.getCellAt(x, y).block();
			}
		}
	}
	
	/**
	 * Ajoute un PropertyChangeListener list � la propri�t� pName
	 * @param pName
	 * @param list
	 */
	public void addPropertyChangeListener(String pName, PropertyChangeListener list) {
		if (list != null) {
			propSup.addPropertyChangeListener(pName, list);
		}
	}

	/**
	 * Retourne le PropertyChangeListener list � la propri�t� pName
	 * @param pName
	 * @param list
	 */
	public void removePropertyChangeListener(String pName, PropertyChangeListener list) {
		if (list != null) {
			propSup.removePropertyChangeListener(pName, list);
		}
	}
}
