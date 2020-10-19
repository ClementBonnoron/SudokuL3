package model;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.TreeSet;

import cmd.*;
import heuristics.*;
import heuristics.IHeuristic.Solution;
import heuristics.IHeuristic.LevelHeuristics;
import misc.GridException;

/*
 * 
 * Ceci est un test git
 */
public class StdGrid extends Observable implements Grid {
	
	// ATTRIBUTS

	private int size;
	private Set<String> values;
	private Set<PropertyChangeListener> sharedPCL;
	private Map<PropertyChangeListener, String> sharedPCLName;
	
	/**
	 * Grille contenant la valeur et les candidats possible sélectionné par l'utilisateur.
	 * Utilisation de cette grille par les heuristiques.
	 */
	private Cell[][] cells;
	/**
	 * Grille contenant la solution attendu.
	 */
	@SuppressWarnings("unused")
	private Cell[][] solution;
	private History<Action> history;
	@SuppressWarnings("unused")
	private RegroupHeuristics helps;
	private static final int SIZE_HISTORY_MAX = 1024;
	
	// CONSTRUCTEURS
	
	private StdGrid(Set<String> set, int sizeArea, boolean generateGrid) {
		if (set == null) {
			throw new AssertionError("constructor set null : StdGrid");
		}
		if (!valideSize(sizeArea)) {
			throw new AssertionError("constructor sizeArea not corresponding: StdGrid");
		}
		if (set.size() != (sizeArea * sizeArea)) {
			throw new AssertionError("constructor set doesn't contains enough element: StdGrid");
		}
		this.size = sizeArea * sizeArea;
		this.values = new TreeSet<String>();
		this.solution = null;
		for (String e : set) {
			values.add(e);
		}
		this.cells = new Cell[size][size];
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				this.cells[i][j] = new StdCell(this, new StdBoundedCoordinate(i, j));
			}
		}
		sharedPCL = new HashSet<PropertyChangeListener>();
		sharedPCLName = new HashMap<PropertyChangeListener, String>();
		this.history = new StdHistory<Action>(SIZE_HISTORY_MAX);
		this.helps = new StdRegroupHeuristics(this);
	}
	
	public StdGrid(Set<String> set) {
		this(set, DEFAULT_SIZE, true);
	}
	
	/*
	 * Constructeur pour l'éditeur de sudoku.
	 * 	Solution est la grille à moitié remplie par l'utilisateur.
	 *	Calcule de la solution
	 */
	public StdGrid(Set<String> set, String[][] halfDone) {
		this(set, DEFAULT_SIZE, false);
		/*Cell[][] sol = (Cell[][]) new Object[size][size];
		if (checkGridSolution(sol)) {
			throw new AssertionError("Constructor : StdRegroupementHeuristique");
		}*/
		this.helps = new StdRegroupHeuristics(this);
	}
	
	// REQUETES
	
	@Override
	public boolean canBeResolved() {
		if (helps.getMaxDifficulty() == StdRegroupHeuristics.DEFAULT_DIFFICULTY) {
			return false;
		}
		return true;
	}

	@Override
	public int getSize() {
		return size;
	}
	
	@Override
	public int getSizeSquare() {
		return (int) Math.sqrt(size);
	}
	
	@Override
	public boolean isFull() {
		for (int i = 0; i < this.size; ++i) {
			for (int j = 0; j < this.size; ++j) {
				if (cells[i][j].getValue() == null) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public Cell getCellAt(int x, int y) {
		if (!checkCoordinate(x, y)) {
			throw new AssertionError("getCellAt coord not valid : StdGrid");
		}
		return cells[x][y];
	}

	@Override
	public BoundedCoordinate getCoordinate(Cell cell) {
		return cell.getCoordinate();
	}
	
	@Override
	public Collection<String> getValues() {
		return Collections.unmodifiableCollection(values);
	}

	@Override
	public Collection<Cell> getCells() {
		Collection<Cell> c = new ArrayList<Cell>();
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				c.add(cells[i][j]);
			}
		}
		return c;
	}
	
	@Override
	public String getValueFrom(int x, int y) {
		if (!checkCoordinate(x, y)) {
			throw new AssertionError("getValueFrom coord not valid : StdGrid");
		}
		return cells[x][y].getValue();
	}
	
	@Override
	public Set<String> getCandidatesFrom(int x, int y) {
		if (!checkCoordinate(x, y)) {
			throw new AssertionError("getCandidatesFrom coord not valid : StdGrid");
		}
		return cells[x][y].getCandidates();
	}
	
	@Override
	public Set<String> getPossibleCandidatesFrom(int x, int y) {
		if (!checkCoordinate(x, y)) {
			throw new AssertionError("getCandidatesFrom coord not valid : StdGrid");
		}
		Set<String> candidates = StdGrid.defaultValueSet();
		for (int i = 0; i < size; ++i) {
			Cell line = this.cells[i][y];
			Cell column = this.cells[x][i];
			if (i != x && line.getValue() != null) {
				candidates.remove(line.getValue());
			}
			if (i != y && column.getValue() != null) {
				candidates.remove(column.getValue());
			}
		}
		for (int i = (x - (x % getSizeSquare())); i <= (x - (x % getSizeSquare()) + getSizeSquare() - 1); ++i) {
			for (int j = (y - (y % getSizeSquare())); j <= (y - (y % getSizeSquare()) + getSizeSquare() - 1); ++j) {
				if (i >= 0 && i < size && j >= 0 && j < size && !(i == x && j == y)) {
					if (cells[i][j].getValue() != null) {
						candidates.remove(cells[i][j].getValue());
					}
				}
			}
		}
		return candidates;
	}
	
	@Override
	public Cell[][] getSolution() {
		// return this.getSolution();
		return null; // J'ai temporairement changé pour ne pas avoir de StackOverflow, merci. @fantovic
	}

	@Override
	public String[][] getSolutionString() {
		String[][] sol = new String[getSize()][getSize()];
		for (int i = 0; i < getSize() ; ++i) {
			for (int j = 0; j < getSize() ; ++j) {
				sol[i][j] = solution[i][j].getValue();
			}
		}
		// return this.getSolution();
		return sol; // J'ai temporairement changé pour ne pas avoir de StackOverflow, merci. @fantovic
	}
	
	@Override
	public String[][] getStringGrid() {
		String [][] array = new String[size][size];
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				array[i][j] = cells[i][j].getValue();
			}
		}
		return array;
	}

	@Override
	public History<Action> getHistory() {
		return this.history;
	}
	
	@Override
	public RegroupHeuristics getRegroupHeuristics() {
		return helps;
	}
	
	@Override
	public int getDifficulty() {
		return helps.getDifficulty();
	}
	
	@Override
	public int getMinDifficulty() {
		return helps.getMinDifficulty();
	}
	
	@Override
	public int getMaxDifficulty() {
		return helps.getMaxDifficulty();
	}
	
	// PRIVATE REQUEST

	private boolean valideSize(int size) {
		return (size == 3);
	}
	
	@SuppressWarnings("unused")
	private boolean checkGridSolution(String[][] solution) {
		if (solution.length != size || solution[0].length != size) {
			return false;
		}
		Set<String> elementsLine = new TreeSet<String>();
		Set<String> elementsCol = new TreeSet<String>();
		Collection<String> values = getValues();
		for (int i = 0; i < solution.length; ++i) {
			elementsLine.addAll(values);
			elementsCol.addAll(values);
			for (int j = 0; j < solution[i].length; ++j) {
				if (elementsLine.remove(solution[i][j]) == false
						|| elementsCol.remove(solution[j][i]) == false) {
					return false;
				}
			}
			if (elementsLine.size() != 0 || elementsCol.size() != 0) {
				return false;
			}
			// Tester si la valeurs de la case 
		}
		return true;
	}

	private boolean checkCoordinate(int x, int y) {
		return !((x < 0 || x >= size) &&
				(y < 0 || y >= size));
	}
	
	// COMMANDS
	
	@Override
	public void setSolution(String[][] sol) throws GridException {
		if (!checkGridSolution(sol)) {
			throw new GridException("Solution non valable");
		}
		this.solution = new Cell[size][size];
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				this.solution[i][j] = new StdCell(this, new StdBoundedCoordinate(i, j));
				this.solution[i][j].setValue(sol[i][j]);
			}
		}
	}
	
	@Override
	public boolean resolve() {
		// if (solution == null) {
			Solution sol = new Backtracking(this, true).getSolution();
			if (sol == null) {
				return false;
			}
			// for (Action a : sol.getActions()) {
			// 	a.act();
			// }
			Action action = new ResolveIndice(sol, this);
			action.act();
			history.add(action);
			return true;
		// } else {
		// 	for (int i = 0; i < size; ++i) {
		// 		for (int j = 0; j < size; ++j) {
		// 			if (cells[i][j].getValue() != solution[i][j].getValue()) {
		// 				this.setValue(i, j, solution[i][j].getValue());
		// 			}
		// 		}
		// 	}
		// 	return true;
		// }
	}
	
	@Override
	public void redo() {
		if (history.getEndPosition() - history.getCurrentPosition() <= 0) {
			throw new AssertionError("redo not action available :  StdGrid");
		}
		this.history.goForward();
		Action c = this.history.getCurrentElement();
		c.act();
	}
	
	@Override
	public void clearHistory() {
		history.clearAll();
	}
	
	@Override
	public void undo() {
		if (history.getCurrentPosition() <= 0) {
			throw new AssertionError("undo not action available :  StdGrid");
		}
		Action c = this.history.getCurrentElement();
		c.act();
		this.history.goBackward();
	}
	
	@Override
	public Solution getHelp() {
		return helps.getEasiestHeuristic();
	}

	@Override
	public Solution resolveHelp() {
		Solution res = helps.getEasiestHeuristic();
		if (res != null) {
			Action act = new ResolveIndice(res, this);
			act.act();
			history.add(act);
		} 
		return res;
	}
	
	@Override
	public Solution getHelp(LevelHeuristics heuristic) {
		return helps.getEasiestHeuristic();
	}

	@Override
	public void addAllCandidate(int x, int y) {
		if (!checkCoordinate(x, y)) {
			throw new AssertionError("setValue coord not valid : StdGrid");
		}
		Action action = new AddAllCandidate(cells[x][y], this);
		action.act();
		history.add(action);
	}

	@Override
	public void removeAllCandidate(int x, int y) {
		if (!checkCoordinate(x, y)) {
			throw new AssertionError("setValue coord not valid : StdGrid");
		}
		Action action = new RemoveAllCandidate(cells[x][y]);
		action.act();
		// history.add(action);
	}

	@Override
	public void setValue(int x, int y, String value, boolean advanced) {
		if (!checkCoordinate(x, y)) {
			throw new AssertionError("setValue coord not valid : StdGrid");
		}
		if (value != null && !values.contains(value)) {
			throw new AssertionError("setValue value doesn't contain on set : StdGrid");
		}
		Action action = new SetValue(cells[x][y], this, value, advanced);
		action.act();
		history.add(action);
	}

	@Override
	public void removeValue(int x, int y, boolean advanced) {
		if (!checkCoordinate(x, y)) {
			throw new AssertionError("removeValue coord not valid : StdGrid");
		}
		Action action = new SetValue(cells[x][y], this, null, advanced);
		action.act();
		history.add(action);
	}

	@Override
	public void addCandidate(int x, int y, String value) {
		if (!checkCoordinate(x, y)) {
			throw new AssertionError("addCandidate coord not valid : StdGrid");
		}
		if (!values.contains(value)) {
			throw new AssertionError("addCandidate value doesn't contain on set : StdGrid");
		}
		Action action = new AddCandidate(cells[x][y], value);
		action.act();
		history.add(action);
	}

	@Override
	public void removeCandidate(int x, int y, String value) {
		if (!checkCoordinate(x, y)) {
			throw new AssertionError("removeCandidate coord not valid : StdGrid");
		}
		if (!values.contains(value)) {
			throw new AssertionError("removeCandidate value doesn't contain on set : StdGrid");
		}
		Action action = new EliminateCandidate(cells[x][y], value);
		action.act();
		history.add(action);
	}
	
	// STATIC METHODS
	
	/**
	 * Retourne le set de valeurs par défaut d'un sudoku :)
	 * @author fantovic
	 * @return un set contenant 1 2 3 4 5 6 7 8 et 9.
	 */
	public static Set<String> defaultValueSet() {
		Set<String> values = new HashSet<String>();
		for (int i = 1; i < 10; i++) {
			values.add(Integer.toString(i));
		}
		return values;
	}
	
	// LISTENERS
	
	public void addPropertyChangeListenerGrid(String pName, PropertyChangeListener listener) {
		for (int i = 0; i < getSize(); ++i) {
			for (int j = 0; j < getSize(); ++j) {
				cells[i][j].addPropertyChangeListener(pName, listener);
			}
		}
		sharedPCL.add(listener);
		sharedPCLName.put(listener, pName);
	}
	
	public void removePropertyChangeListenerGrid(String pName, PropertyChangeListener listener) {
		for (int i = 0; i < getSize(); ++i) {
			for (int j = 0; j < getSize(); ++j) {
				cells[i][j].removePropertyChangeListener(pName, listener);
			}
		}
		sharedPCL.remove(listener);
		sharedPCLName.remove(listener);
	}
	
	public void addPropertyChangeListenerCell(String pName, PropertyChangeListener listener,
			int x, int y) {
		if (!checkCoordinate(x, y)) {
			throw new AssertionError("addPCLCell error coord : StdGrid");
		}
		cells[x][y].addPropertyChangeListener(pName, listener);
	}
	
	public void removePropertyChangeListenerCell(String pName, PropertyChangeListener listener,
			int x, int y) {
		if (!checkCoordinate(x, y)) {
			throw new AssertionError("removePCLCell error coord : StdGrid");
		}
		cells[x][y].removePropertyChangeListener(pName, listener);
	}
	
	public PropertyChangeListener[] getPropertyChangeListenerCell(int x, int y) {
		if (!checkCoordinate(x, y)) {
			throw new AssertionError("removePCLCell error coord : StdGrid");
		}
		return cells[x][y].getPropertyChangeListeners();
	}
	
	public PropertyChangeListener[] getPropertyChangeListenerShared() {
		return (PropertyChangeListener[]) sharedPCL.toArray();
	}
	
	@Override
	public void copySharedPCL(Grid newGrid) {
		if (newGrid == null) {
			throw new AssertionError();
		}
		for (PropertyChangeListener pcl : sharedPCL) {
			newGrid.addPropertyChangeListenerGrid(sharedPCLName.get(pcl), pcl);
		}
	}

	// DEPRECATED METHODS
	
	@Override
	public Cell getCellAt(BoundedCoordinate coord) {
		if (!checkBoundedCoordinate(coord)) {
			throw new AssertionError("getCellAt coord not valid : StdGrid");
		}
		return cells[coord.getX()][coord.getY()];
	}

	private boolean checkBoundedCoordinate(BoundedCoordinate coord) {
		return !((coord.getX() < 0 || coord.getX() >= size) &&
				(coord.getY() < 0 || coord.getY() >= size));
	}
	
	@Override
	public void addAllCandidate(BoundedCoordinate coord) {
		if (!checkBoundedCoordinate(coord)) {
			throw new AssertionError("setValue coord not valid : StdGrid");
		}
		Set<String> candidates = Grid.getAllCandidatesFrom(this, coord.getX(), coord.getY());
		for (String value : values) {
			if (!candidates.contains(value)) {
				addCandidate(coord, value);
			}
		}
	}
	
	@Override
	public void setValue(BoundedCoordinate coord, String value, boolean advanced) {
		if (!checkBoundedCoordinate(coord)) {
			throw new AssertionError("setValue coord not valid : StdGrid");
		}
		if (value != null && !values.contains(value)) {
			throw new AssertionError("setValue value doesn't contain on set : StdGrid");
		}
		Action action = new SetValue(cells[coord.getX()][coord.getY()], this, value, advanced);
		action.act();
		history.add(action);
	}
	
	@Override
	public void removeValue(BoundedCoordinate coord, boolean advanced) {
		if (!checkBoundedCoordinate(coord)) {
			throw new AssertionError("removeValue coord not valid : StdGrid");
		}
		Action action = new SetValue(cells[coord.getX()][coord.getY()],this,  null, advanced);
		action.act();
		history.add(action);
	}
	
	@Override
	public void addCandidate(BoundedCoordinate coord, String value) {
		if (!checkBoundedCoordinate(coord)) {
			throw new AssertionError("addCandidate coord not valid : StdGrid");
		}
		if (!values.contains(value)) {
			throw new AssertionError("addCandidate value doesn't contain on set : StdGrid");
		}
		Action action = new AddCandidate(cells[coord.getX()][coord.getY()], value);
		action.act();
		history.add(action);
	}
	
	@Override
	public void removeCandidate(BoundedCoordinate coord, String value) {
		if (!checkBoundedCoordinate(coord)) {
			throw new AssertionError("removeCandidate coord not valid : StdGrid");
		}
		if (!values.contains(value)) {
			throw new AssertionError("removeCandidate value doesn't contain on set : StdGrid");
		}
		Action action = new EliminateCandidate(cells[coord.getX()][coord.getY()], value);
		action.act();
		history.add(action);
	}
	
	
}
