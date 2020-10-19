package heuristics;

import java.util.LinkedList;

import cmd.SetValue;
import model.Cell;
import model.Grid;

public class Backtracking extends AHeuristic implements IHeuristic {

	// ATTRIBUTS
	
	private String[][] currentGrid;
	private String[][] currentGrid2;
	private boolean[][] lockerGrid;
	private LinkedList<String> values;
	int nb;
	private boolean unique;
	
	// CONSTRUCTEURS
	
	/**
	 * Heuristique de BackTracking. Si le parametre unique == true alors 
	 * l'heuristique cherche si la solution est unique.
	 */
	public Backtracking(Grid grid, boolean unique) {
		super(IHeuristic.LevelHeuristics.Backtracking, grid);
		initializeCurrentGrid();
		values = new LinkedList<String>();
		for (String s : grid.getValues()) {
			values.add(s);
		}
		this.unique = unique;
	}
	
	// REQUETES
	
	@Override
	public Solution getSolution() {
		boolean res = resolver(0, getGrid(), currentGrid, lockerGrid);
		if (!unique) {
			return setSolution(res, true);
		}
		boolean res2 = resolverReverse(getGrid().getSize() * getGrid().getSize() - 1,
				getGrid(), currentGrid2, lockerGrid, values);
		boolean fres = isSameGrid();
		return setSolution(res && res2, fres);
	}

	// OUTILS

	private Solution setSolution(boolean res, boolean fres) {
		StdSolution solv = new StdSolution();
		if (!fres) {
			if (!res) {
				solv.setDescription("Backtracking :\nPas de solution");
			} else {
				solv.setDescription("Backtracking :\nPlusieurs solutions");	
			}
		} else {
			if (!res) {
				solv.setDescription("Backtracking :\nPas de solution");
			} else {
				for (int i = 0; i < getGrid().getSize(); ++i) {
					for (int j = 0; j < getGrid().getSize(); ++j) {
						Cell c = getGrid().getCellAt(i, j);
						if (currentGrid[i][j] != c.getValue()) {
							try {
								solv.addAction(new SetValue(c, getGrid(), currentGrid[i][j], true));
							} catch (SolutionInitializeException e) {
								e.printStackTrace();
							}
						}
					}
				}
				solv.setDescription("Backtracking :\nL'algorithme a ete effectué avec succès.");
			}
		}
		return solv;
	}
	
	/**
	 * Initialise les attributs qui repr�sentent les grilles trouv�s.
	 */
	private void initializeCurrentGrid() {
		int size = getGrid().getValues().size();
		currentGrid = new String[size][size];
		currentGrid2 = new String[size][size];
		lockerGrid = new boolean[size][size];
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				String value = getGrid().getStringGrid()[i][j];
				currentGrid[i][j] = value;
				currentGrid2[i][j] = value;
				lockerGrid[i][j] = value != null;
			}
		}
	}
	
	/**
	 * Alogorithme de backtracking classique.
	 */
	private static boolean resolver(int position, Grid g, 
			String[][]currentGrid, boolean[][]lockerGrid) {
		if (position == currentGrid.length * currentGrid.length) {
			return true;
		}
		int l = position / currentGrid.length;
		int c = position % currentGrid.length;
		if (currentGrid[l][c] != null) {
			return resolver(position + 1, g, currentGrid, lockerGrid);
		}
		for (String s : g.getValues()) {
			if (!lockerGrid[l][c] && condAdd(s, l, c, currentGrid)) {
				currentGrid[l][c] = s;
				if (resolver(position + 1, g, currentGrid, lockerGrid)) {
					return true;
				}
			}
      }
	  currentGrid[l][c] = null;
      return false;
	}
	
	/**
	 * Permet de faire l'algorithme de backtracking � l'envers.
	 */
	private static boolean resolverReverse(int position, Grid g, 
			String[][]currentGrid, boolean[][]lockerGrid, LinkedList<String> values) {
		if (position < 0) {
			return true;
		}
		int l = position / currentGrid.length;
		int c = position % currentGrid.length;
		if (currentGrid[l][c] != null) {
			return resolverReverse(position - 1, g, currentGrid, lockerGrid, values);
		}
		for (int i = g.getSize() - 1; i >= 0; --i) {
			String s = values.get(i);
			if (!lockerGrid[l][c] && condAdd(s, l, c, currentGrid)) {
				currentGrid[l][c] = s;
				if (resolverReverse(position - 1, g, currentGrid, lockerGrid, values)) {
					return true;
				}
			}
      }
	  currentGrid[l][c] = null;
      return false;
	}
	
	/**
	 * Compare currentGrid et currentGrid2 pour savoir si ce sont les m�mes
	 * grilles.
	 */
	private boolean isSameGrid() {
		int size = currentGrid.length;
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				String s = currentGrid[i][j];
				String s2 = currentGrid2[i][j];
				if (s == null || s2 == null) {
					return false;
				}
				if (!s.equals(s2)) {
				    return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Indique si le candidat v peut �tre plac� dans la grid currentGrid aux 
	 * coordonn�es  (l, c)
	 */
	private static boolean condAdd(String v, int l, int c, String[][]currentGrid) {
	    if (v == null || v.equals(currentGrid[l][c])) {
	        return true;
	    }
	    for (int i = 0; i < currentGrid.length; i++) {
	        if (v.equals(currentGrid[i][c])) {
	            return false;
	        }
	        if (v.equals(currentGrid[l][i])) {
	            return false;
	        }
	    }
	    int sizeSquare = (int) Math.sqrt(currentGrid.length);
	    int x = l / sizeSquare;
	    int y = c / sizeSquare;
	    for (int i = 0; i < sizeSquare; ++i) {
	        for (int j = 0; j < sizeSquare; ++j) {
	            int xx = x * sizeSquare + i;
	            int yy = y * sizeSquare + j;
	            if (v.equals(currentGrid[xx][yy])) {
	                return false;
	            }
	        }
	    }
	    return true;
	}
}
