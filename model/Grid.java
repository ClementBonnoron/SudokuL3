package model;

import java.beans.PropertyChangeListener;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import cmd.Action;
import heuristics.IHeuristic.Solution;
import heuristics.RegroupHeuristics;
import heuristics.IHeuristic.LevelHeuristics;
import misc.GridException;

/**
 * D�finit une grille de sudoku contenant un ensemble de 
 * 	{@code (DEFAULT_SIZE * DEFAULT_SIZE)} par 
 * 	{@code (DEFAULT_SIZE * DEFAULT_SIZE)} cellules.
 * Chaque cellule � une coordonn�es unique, correspondant �
 * 	son emplacement dans cette grille.
 * Cette grille contient un {@link History} et permet l'utilisation
 * 	d'un historique.
 * @author cleme
 * @constructor
 * 		$DESC$ Renvoie une grille contenant un tableau de cellules.
 * 			Les cellules de la grille ne pourront que contenir des 
 * 			valeurs/candidats uniquement si ils appartiennent �
 * 			l'ensemble pass� au constructeur. La grille est g�n�r�
 * 			al�atoirement par Victor.
 * 		$ARGS$ Set<String> set
 * 		$PRE$
 * 			set != null
 * 		$POST$
 * 			getValues() == set
 * 			forall cell:GetCells()
 * 				cell != null
 * 				getCoordinate(cell) est unique
 * @constructor
 * 		$DESC$ Renvoie une grille contenant un tableau de cellules.
 * 			Les cellules de la grille ne pourront que contenir des 
 * 			valeurs/candidats uniquement si ils appartiennent �
 * 			l'ensemble pass� au constructeur. La grille pass� en
 * 			param�tre est � moiti� rempli, et une solution est
 * 			calcul� en interne en conservant les valeurs pass�
 * 			par halfDone.
 * 		$ARGS$ Set<String> set, String[][] halfDone
 * 		$PRE$ 
 * 			set != null
 * 		$POST$
 * 			getValues() == set
 * 			getCellAt(i, j).getValue() == halfDone[i][j]
 * 			getSize() == {@link #DEFAULT_SIZE} * {@link #DEFAULT_SIZE}
 * 			getStringGrid() == halfDone
 * 			getHistory().getCurrentPosition() == 0
 */
public interface Grid extends ObservableModel {
	
	static final int DEFAULT_SIZE = 3;
	
	// TODO: commentaires svp ?
	// TODO: peut-on rajouter les fonctions suivantes ?
	// Cell getCellAt(int x, int y);
	// void setValueAt(int x, int y, String value);
	// C'est pour le générateur, ce serait plus pratique :)
	
	// REQUESTS
	
	/**
	 * Renvoie vrai ssi la grille peut �tre r�solu
	 * @return boolean
	 */
	boolean canBeResolved();
	
	/**
	 * Renvoie la taille de la grille.
	 * @return
	 */
	int getSize();
	
	/**
	 * Renvoie la taille d'une zone de la grille
	 */
	int getSizeSquare();
	
	
	/**
	 * Renvoie vrai ssi la grille est pleine. (chaque case
	 * 	a une valeur.
	 */
	boolean isFull();
	
	/**
	 * Renvoie la cellule de coordonn�e x,y.
	 * @param x
	 * @param y
	 * @pre
	 * 		getSize() > x >= 0
	 * 		getSize() > y >= 0
	 * @post
	 * 		getCellAt(x, y) != null
	 */
	Cell getCellAt(int x, int y);

	/**
	 * Renvoie les coordonn�es de la cellule.
	 * @param cell
	 * @return
	 * @pre
	 * 		cell != null
	 * @post
	 * 		coord:getCoordinate(cell)
	 * 			coord != null
	 * 			coord == cell.getCoord()
	 */
	BoundedCoordinate getCoordinate(Cell cell);
	
	/**
	 * Renvoie la liste des valeurs qui peuvent �tre ajout�s aux
	 * 	candidats et � la valeur.
	 * @return
	 * @post
	 * 		getValues() != null
	 */
	Collection<String> getValues();
	
	/**
	 * Renvoie la liste des cellules contenus dans la grille.
	 * @return
	 * @post 
	 * 		getCells() != null
	 */
	Collection<Cell> getCells();
	
	/**
	 * Renvoie la liste des candidats de la cellule de coordonn�e x et y.
	 * @param x
	 * @param y
	 * @pre
	 * 		0 <= x && x <= getSize()
	 * 		0 <= y && y <= getSize()
	 * @post
	 * 		getCandidatesFrom(x, y) == getCellAt(x, y).getCandidates();
	 */
	String getValueFrom(int x, int y);
	
	/**
	 * Renvoie la liste des candidats de la cellule de coordonn�e x et y.
	 * @param x
	 * @param y
	 * @pre
	 * 		0 <= x && x <= getSize()
	 * 		0 <= y && y <= getSize()
	 * @post
	 * 		getCandidatesFrom(x, y) == getCellAt(x, y).getCandidates();
	 */
	Set<String> getCandidatesFrom(int x, int y);
	
	/**
	 * Renvoie la liste des candidats possible de la cellule de coordonn�e
	 * 	x et y.
	 * @param x
	 * @param y
	 * @pre
	 * 		0 <= x && x <= getSize()
	 * 		0 <= y && y <= getSize()
	 * @post
	 * 		getCandidatesFrom(x, y) renvoie tout les candidats possibles de la
	 * 			cellule.
	 */
	Set<String> getPossibleCandidatesFrom(int x, int y);
	
	/**
	 * Renvoie la solution de la grille.
	 * @deprecated use {@link #getSolutionString()}} instead.
	 * @return
	 */
	Cell[][] getSolution();
	
	/**
	 * Renvoie la solution de la grille � l'aide
	 * 	d'un tableau de cha�ne de caract�re.
	 * @post
	 * 		value:getSolution()
	 * 		forall i,j;:0...getSize()
	 * 			getCellAt(i, j).getValue() == value[i][j]
	 */
	String[][] getSolutionString();
	
	/**
	 * Renvoie un tableau de cha�ne de caract�res d�crivant l'�tat
	 * 	interne de la grille
	 * @return
	 */
	String[][] getStringGrid();
	
	/**
	 * Renvoie l'historique d'action courant.
	 * @return {@link History}
	 * @post
	 * 		getHistory() != null
	 * 		getHistory().getCurrentPosition() == le nombre d'actions
	 * 			effectu�es depuis la cr�ation de la grille.
	 */
	History<Action> getHistory();
	
	/**
	 * Renvoie le regroupement d'heuristiques.
	 */
	RegroupHeuristics getRegroupHeuristics();
	
	/**
	 * Renvoie la difficult� de la grille.
	 * @return {@link RegroupHeuristics.getDifficulty()}
	 * @post
	 * 		getDifficulty() est �gal � la diffilcult� de la grille
	 * 			courante.
	 */
	int getDifficulty();
	
	/**
	 * Renvoie la difficult� minimum d'une grille.
	 */
	int getMinDifficulty();
	
	/**
	 * Renvoie la difficult� maximum d'une grille.
	 */
	int getMaxDifficulty();
	
	/**
	 * Renvoie le premier indice disponible
	 * @return {@link Solution}
	 * @post
	 * 		getHelp() renvoie la premier solution de l'heuristique
	 * 			la plus simple fonctionnant.
	 * 		Renvoie null si aucune heuristique fonctionne, signifiant
	 * 			que l'utilisateur s'est trompé quelque part.
	 */
	Solution getHelp();
	
	/**
	 * Renvoie la solution la solution de l'heuristique pass�
	 * 		en param�tre
	 * @post
	 * 		getHelp() renvoie la premier solution de l'heuristique
	 * 			la plus simple fonctionnant.
	 * 		Renvoie null si aucune heuristique fonctionne, signifiant
	 * 			que l'utilisateur s'est trompé quelque part.
	 */
	Solution getHelp(LevelHeuristics heuristic);

	/**
	 * Renvoie la solution la solution de l'heuristique pass�
	 * 		en param�tre et la résout
	 * @post
	 * 		resolveHelp() renvoie la premier solution de l'heuristique
	 * 			la plus simple fonctionnant.
	 * 		Renvoie null si aucune heuristique fonctionne, signifiant
	 * 			que l'utilisateur s'est trompé quelque part ou que la grille ne
	 * 			peut pas être résolue.
	 */
	Solution resolveHelp();
	
	// COMMANDES
	
	/**
	 * Défini la solution de la grille.
	 * @pre
	 * 		sol est valable.
	 * @post
	 * 		getSolutionString() == sol
	 * @throws GridException() ssi la solution n'est pas valable.
	 */
	void setSolution(String[][] sol) throws GridException;
	
	/**
	 * R�sout la grille et modifie l'�tat des cellules.
	 * @post
	 * 		solution:getSolutionString()
	 * 		forall i,j:0...getSize(�
	 * 			getCellAt(i, j).getValue() == solution[i][j]
	 */
	boolean resolve();
	
	/**
	 * Vide l'historique courrant.
	 */
	void clearHistory();
	
	/**
	 * Annule le dernier coups enregistr� dans l'historique interne.
	 * V�rifie la documentation de {@link History.goForward()}
	 */
	void undo();

	/**
	 * Annule le dernier coups enregistr� dans l'historique interne.
	 * V�rifie la documentation de {@link History.goBackward()}
	 */
	void redo();
	
	/**
	 * Ajoute tout les candidats � la cellule de coordonn�e x,y
	 * 	selon l'�tat des cellules de la grille.
	 * @param x
	 * @param y
	 * @post
	 * 		for s:getValues()
	 * 			getCellAt(x, y).getCandidates().contains(s) <=>
	 * 				s est un candidat possible � la cellule
	 */
	void addAllCandidate(int x, int y);
	
	/**
	 * Retire tout les candidats � la cellule de coordonn�e x,y
	 * 	selon l'�tat des cellules de la grille.
	 * @param x
	 * @param y
	 * @post
	 * 		for s:getValues()
	 * 			getCellAt(x, y).getCandidates().contains(s) <=>
	 * 				s n'est plus un candidat possible � la cellule
	 */
	void removeAllCandidate(int x, int y);
	
	/**
	 * D�finis la valeur de la cellule de coordonn�e x,y �
	 * 	value.
	 * @param x
	 * @param y
	 * @param value
	 * @param advanced
	 * @post
	 * 		getCellAt(x, y).getValue() == value
     *     	getHistory().getCurrentPosition() == old getCurrentPosition() + 1
     *     	getHistory().getEndPosition() == 0
	 */
	void setValue(int x, int y, String value, boolean advanced);

	/**
	 * Retire la valeur de la cellule de coordonn�e x,y.
	 * @param x
	 * @param y
	 * @param value
	 * @param advanced
	 * @post
	 * 		getCellAt(x, y).getValue() == null
     *     	getHistory().getCurrentPosition() == old getCurrentPosition() + 1
     *     	getHistory().getEndPosition() == 0
	 */
	void removeValue(int x, int y, boolean advanced);

	/**
	 * Ajoute � la cellule de coordonn�e x,y le
	 * 	candidats value
	 * 	value.
	 * @param x
	 * @param y
	 * @param value
	 * @pre
	 * 		!getCellAt(x, y).getCandidates().contains(value)
	 * @post
	 * 		getCellAt(x, y).getCandidates().contains(value)
     *     	getHistory().getCurrentPosition() == old getCurrentPosition() + 1
     *     	getHistory().getEndPosition() == 0
	 */
	void addCandidate(int x, int y, String value);

	/**
	 * Retire le candidat value de la cellule de coordonn�e x,y.
	 * 	value.
	 * @param x
	 * @param y
	 * @param value
	 * @pre
	 * 		getCellAt(x, y).getCandidates().contains(value)
	 * @post
	 * 		!getCellAt(x, y).getCandidates().contains(value)
     *     	getHistory().getCurrentPosition() == old getCurrentPosition() + 1
     *     	getHistory().getEndPosition() == 0
	 */
	void removeCandidate(int x, int y, String value);
	
	// LISTENERS
	
	/**
	 * Rajoute listener comme PCL au bean de nom {@code pName} � toutes
	 * 	les cellules de la grille.
	 * @param pName
	 * @param listener
	 * @post
	 * 		Les bean de nom {@code pName} de toutes les cellules de la grille
	 * 			notifiront listener lorsqu'ils changeront d'�tat
	 */
	void addPropertyChangeListenerGrid(String pName, PropertyChangeListener listener);

	/**
	 * Retire listener des PCL du bean de nom {@code pName} � toutes
	 * 	les cellules de la grille.
	 * @param pName
	 * @param listener
	 * @post
	 * 		Les bean de nom {@code pName} de toutes les cellules de la grille
	 * 			ne notifiront plus listener lorsqu'ils changeront d'�tat
	 */
	void removePropertyChangeListenerGrid(String pName, PropertyChangeListener listener);
	
	/**
	 * Rajoute listener comme PCL au bean de nom {@code pName} � la cellule
	 * 	de coordonn�e x,y
	 * @param pName
	 * @param listener
	 * @post
	 * 		Le bean de nom {@code pName} de la cellule de coordonn�e x,y
	 * 			notifira listener lorsqu'elle changera d'�tat
	 */
	void addPropertyChangeListenerCell(String pName, PropertyChangeListener listener,
			int x, int y);
	
	/**
	 * Retire listener des PCL du bean de nom {@code pName} de la cellule
	 * 	de coordonn�e x,y
	 * @param pName
	 * @param listener
	 * @post
	 * 		Le bean de nom {@code pName} de la cellule de coordonn�e x,y
	 * 			ne notifira plus listener lorsqu'elle changera d'�tat
	 */
	void removePropertyChangeListenerCell(String pName, PropertyChangeListener listener,
			int x, int y);
	
	/**
	 * Renvoie la liste des PCL mises sur la cellule de coordonn�e x, y.
	 * @return
	 * @pre
	 * 		0 <= x && x <= getSize()
	 * 		0 <= y && y <= getSize()
	 */
	PropertyChangeListener[] getPropertyChangeListenerCell(int x, int y);
	
	/**
	 // TODO
	 * @param grid
	 */
	void copySharedPCL(Grid newGrid);
	
	// DEPRECATED METHODS

	/**
	 * @deprecated use {@link #getCellAt(int, int)} instead.
	 * @param coord
	 * @author cleme
	 * @return
	 */
	Cell getCellAt(BoundedCoordinate coord);
	
	/**
	 * @deprecated use {@link #addAllCandidate(int, int)} instead.
	 * @author cleme
	 * @param coord
	 */
	void addAllCandidate(BoundedCoordinate coord);

	/**
	 * @deprecated use {@link #setValue(int, int, String)} instead.
	 * @author cleme
	 * @param coord
	 * @param advanced
	 */
	void setValue(BoundedCoordinate coord, String value, boolean advanced);

	/**
	 * @deprecated use {@link #removeValue(int, int)} instead.
	 * @author cleme
	 * @param coord
	 * @param advanced
	 */
	void removeValue(BoundedCoordinate coord, boolean advanced);

	/**
	 * @deprecated use {@link #addCandidate(int, int, String)} instead.
	 * @author cleme
	 * @param coord
	 */
	void addCandidate(BoundedCoordinate coord, String value);

	/**
	 * @deprecated use {@link #removeCandidate(int, int, String)} instead.
	 * @author cleme
	 * @param coord
	 */
	void removeCandidate(BoundedCoordinate coord, String value);

	/**
	 * Renvoie la liste des PCL mises en commun sur toutes les cellules de la grille.
	 * @return
	 * @deprecated use {@link #copySharedPCL(Grid)} instead.
	 */
	PropertyChangeListener[] getPropertyChangeListenerShared();
	
	// STATIC METHODS
	
	static Set<String> getAllCandidatesFrom(Grid grid, int x, int y) {
		Collection<String> values = grid.getValues();
		Set<String> set = new TreeSet<String>(values);
		int sizeSquare = (int) Math.sqrt(grid.getSize());
		for (int i = 0; i < grid.getSize(); ++i) {
			if (i != y && grid.getCellAt(x, i).getValue() != null) {
				set.remove(grid.getCellAt(x, i).getValue());
			}
			if (i != x && grid.getCellAt(i, y).getValue() != null) {
				set.remove(grid.getCellAt(i, y).getValue());
			}
			if ((x - (x % sizeSquare)) + x / sizeSquare != x &&
					(y - (y % sizeSquare)) + x % sizeSquare != y &&
					grid.getCellAt(
							(x - (x % sizeSquare)) + x / sizeSquare,
							(y - (y % sizeSquare)) + x % sizeSquare).getValue() != null) {
				set.remove(grid.getCellAt(
						(x - (x % sizeSquare)) + x / sizeSquare,
						(y - (y % sizeSquare)) + x % sizeSquare).getValue());
			}
		}
		return set;
	}
}
