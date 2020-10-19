package heuristics;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import cmd.SetValue;
import model.BoundedCoordinate;
import model.Cell;
import model.Grid;

public class UniqueCandidate extends AHeuristic implements IHeuristic {

	// ATTRIBUTS
	
	private Map<String, Integer> mapStrings;
	private Map<Integer, String> mapValues;
	
	// CONSTRUCTEUR
	
	public UniqueCandidate(Grid grid) {
		super(IHeuristic.LevelHeuristics.UniqueCandidate, grid);
		mapStrings = new HashMap<String, Integer>();
		mapValues = new HashMap<Integer, String>();
		setMapStrings(grid.getValues());
		setMapValues(grid.getValues());
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Solution getSolution() {
		int size = mapStrings.size();
		Integer[] nbPresentLine = new Integer[size];
		Integer[] nbPresentColumn = new Integer[size];
		Integer[] nbPresentRegion = new Integer[size];
		Object[][] candidates = new Object[size][size];
		Cell[] lastLine = new Cell[size];
		Cell[] lastColumn = new Cell[size];
		for (int i = 0; i < size; ++i) {
			nbPresentLine[i] = 0;
			nbPresentColumn[i] = 0;
			nbPresentRegion[i] = 0;
		}
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				if (candidates[i][j] == null) {
					if (getGrid().getCellAt(i, j).getCandidates() == null) {
						candidates[i][j] = new TreeSet<String>();
					} else {
						candidates[i][j] = getGrid().getCellAt(i, j).getCandidates();
					}
				}
				if (candidates[j][i] == null) {
					if (getGrid().getCellAt(j, i).getCandidates() == null) {
						candidates[j][i] = new TreeSet<String>();
					} else {
						candidates[j][i] = getGrid().getCellAt(j, i).getCandidates();
					}
				}
				if (i != 0 && i < j) {
					candidates[i - 1][j] = null;
					candidates[j][i - 1] = null;
				}
				for (String s : (Set<String>) candidates[i][j]) {
					nbPresentLine[mapStrings.get(s)] += 1;
					if (nbPresentLine[mapStrings.get(s)] < 2) {
						lastLine[mapStrings.get(s)] = getGrid().getCellAt(i, j);
					}
				}
				for (String s : (Set<String>) candidates[j][i]) {
					nbPresentColumn[mapStrings.get(s)] += 1;
					if (nbPresentColumn[mapStrings.get(s)] < 2) {
						lastColumn[mapStrings.get(s)] = getGrid().getCellAt(j, i);
					}
				}
			}
			for (int j = 0; j < size; ++j) {
				if (nbPresentLine[j] == 1) {
					if(!zoneContainValue(lastLine[j], mapValues.get(j))) {
						Solution solution = new StdSolution();
						setSolution((StdSolution) solution, lastLine[j], mapValues.get(j), false);
						return solution;
					}
				} else {
					nbPresentLine[j] = 0;
					if (nbPresentColumn[j] == 1) {
						if(!zoneContainValue(lastColumn[j], mapValues.get(j))) {
							Solution solution = new StdSolution();
							setSolution((StdSolution) solution, lastColumn[j], mapValues.get(j), true);
							return solution;
						}
					} else {
						nbPresentColumn[j] = 0;
					}
				}
			}
		}
		return null;
	}
	
	private boolean zoneContainValue(Cell cell, String value) {
		Grid grid = getGrid();
		BoundedCoordinate bc = cell.getCoordinate();
		for (int i = 0; i < grid.getSize(); ++i) {
			if (i != bc.getX()) {
				if (value.equals(grid.getCellAt(i, bc.getY()).getValue())) {
					return true;
				}
			}
			if (i != bc.getY()) {
				if (value.equals(grid.getCellAt(bc.getX(), i).getValue())) {
					return true;
				}
			}
		}
		int sizeS = grid.getSizeSquare();
		for (int i = 0; i < sizeS; ++i) {
			for (int j = 0; j < sizeS; ++j) {
				if (i + (bc.getX() - bc.getX() % sizeS) != bc.getX() || j + (bc.getY() - bc.getY() % sizeS) != bc.getY()) {
					if (value.equals(grid.getCellAt(i + (bc.getX() - bc.getX() % sizeS), j + (bc.getY() - bc.getY() % sizeS)).getValue())) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private void setSolution(StdSolution solution, Cell cell, String value, boolean fromLine) {
		assert cell != null;
		try {
			solution.addAction(new SetValue(cell, this.getGrid(), value, true));
			solution.addReason(Color.BLUE, cell);
			StringBuilder description = getDescription(cell, value, fromLine);
			solution.setDescription(description.toString());
		} catch (SolutionInitializeException e) {
			// SHOULD NEVER HAPPEND
			e.printStackTrace();
		}
	}
	
	private StringBuilder getDescription(Cell cell, String value, boolean fromLine) {
		StringBuilder sb = new StringBuilder();
		sb.append("\"" + this.getLevelHeuristics().getName() + "\":\n");
		sb.append("Le candidat '" + value + "' sur la ");
		sb.append((fromLine ? "ligne" : "colonne"));
		sb.append(" est disponible uniquement sur la cellule de coordonn�e ");
		sb.append("(" + (cell.getCoordinate().getY() + 1) + ";" + (cell.getCoordinate().getX() + 1) + ")");
		return sb;
	}
	
	private void setMapStrings(Collection<String> collection) {
		int i = 0;
		for (String s : collection) {
			mapStrings.put(s, i);
			++i;
		}
	}
	
	private void setMapValues(Collection<String> collection) {
		int i = 0;
		for (String s : collection) {
			mapValues.put(i, s);
			++i;
		}
	}
}
