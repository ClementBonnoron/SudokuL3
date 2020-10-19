package heuristics;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import cmd.SetValue;
import model.*;

public class OneCandidate extends AHeuristic implements IHeuristic {

	private Map<String, Integer> mapStrings;
	
	public OneCandidate(Grid grid) {
		super(IHeuristic.LevelHeuristics.OneCandidate, grid);
		mapStrings = new HashMap<String, Integer>();
		setMapStrings(grid.getValues());
	}
	
	@Override
	public Solution getSolution() {
		int size = mapStrings.size();
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				Cell cell = getGrid().getCellAt(i, j);
				if (cell.getValue() == null && cell.getCandidates().size() == 1) {
					String value = getGrid().getCellAt(i, j).getCandidates().iterator().next();
					Solution solution = new StdSolution();
					setSolution((StdSolution) solution, getGrid().getCellAt(i, j), value);
					return solution;
				}
			}
		}
		return null;
	}
	
	private void setSolution(StdSolution solution, Cell cell, String value) {
		assert cell != null;
		try {
			solution.addAction(new SetValue(cell, this.getGrid(), value, true));
			solution.addReason(Color.BLUE, cell);
			StringBuilder description = getDescription(cell, value);
			solution.setDescription(description.toString());
		} catch (SolutionInitializeException e) {
			// SHOULD NEVER HAPPEND
			e.printStackTrace();
		}
	}
	
	private StringBuilder getDescription(Cell cell, String value) {
		StringBuilder sb = new StringBuilder();
		sb.append("\"" + this.getLevelHeuristics().getName() + "\":\n");
		sb.append("Le candidat '" + value + "'");
		sb.append(" est l'unique candidat possible pour la cellule de coordonn�e ");
		sb.append("(" + (cell.getCoordinate().getX() + 1) + ";" + (cell.getCoordinate().getY() + 1) + ")");
		return sb;
	}
	
	private void setMapStrings(Collection<String> collection) {
		int i = 0;
		for (String s : collection) {
			mapStrings.put(s, i);
			++i;
		}
	}
}
