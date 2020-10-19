package heuristics;

import java.util.ArrayList;
import java.util.List;

import cmd.Action;
import graphic.GridViewer;
import heuristics.IHeuristic.LevelHeuristics;
import heuristics.IHeuristic.Solution;
import model.Grid;
import model.StdGrid;

public class StdRegroupHeuristics implements RegroupHeuristics {
	
	// ATTRIBUTS
	
	private Grid grid;
	private String[][] solution;
	private List<LevelHeuristics> sortedLH;
	private int difficulty;
	
	public static int DEFAULT_DIFFICULTY = -1;
	
	// CONSTRCTEURS
	
	public StdRegroupHeuristics(Grid grid) {
		if (grid == null) {
			throw new AssertionError("Constructor : StdRegroupementHeuristique");
		}
		this.grid = grid;
		//this.solution = grid.getSolutionString();
		this.solution = null;
		sortedLH = new ArrayList<LevelHeuristics>();
		for (LevelHeuristics lh : LevelHeuristics.values()) {
			int x = 0;
			while (x < sortedLH.size()) {
				if (sortedLH.get(x).getLevel() >= lh.getLevel()) {
					sortedLH.add(x, lh);
				}
				++x;
			}
			sortedLH.add(lh);
		}
		difficulty = DEFAULT_DIFFICULTY;
	}

	@Override
	public Grid getGrid() {
		return grid;
	}

	@Override
	public String[][] getSolution() {
		return solution;
	}

	@Override
	public Solution UniqueCandidate() {
		return new UniqueCandidate(grid).getSolution();
	}

	@Override
	public Solution oneCandidate() {
		return new OneCandidate(grid).getSolution();
	}

	@Override
	public Solution twinsAndTriplet() {
		return new OneCandidate(grid).getSolution();
	}

	@Override
	public Solution interactionsBetweenRegion() {
		return new OneCandidate(grid).getSolution();
	}

	@Override
	public Solution identicalCandidates() {
		return new IdenticalCandidates(grid).getSolution();
	}

	@Override
	public Solution isolatedGroups() {
		return new IsolatedGroups(grid).getSolution();
	}

	@Override
	public Solution mixedGroups() {
		return new MixedGroups(grid).getSolution();
	}

	@Override
	public Solution XWing() {
		return new XWing(grid).getSolution();
	}

	@Override
	public Solution XYWing() {
		return new XYWing(grid).getSolution();
	}

	@Override
	public Solution XYZWing() {
		return new XYZWing(grid).getSolution();
	}

	@Override
	public Solution getEasiestHeuristic() {
		IHeuristic heur = this.getEasiestLevel();
		if (heur == null) {
			return null;
		}
		return heur.getSolution();
	}
	
	public IHeuristic getEasiestLevel() {
		IHeuristic heur;
		for (int x = 0; x < sortedLH.size(); ++x) {
			switch(sortedLH.get(x)) {
				case OneCandidate:
					heur = new OneCandidate(grid);
					if (heur.getSolution() != null) {return heur;}
				case UniqueCandidate:
					heur = new UniqueCandidate(grid);
					if (heur.getSolution() != null) {return heur;}
					case TwinsAndTriplet:
						heur = new TwinsAndTriplet(grid);
						if (heur.getSolution() != null) {return heur;}
				case IdenticalCandidates:
					heur = new IdenticalCandidates(grid);
					if (heur.getSolution() != null) {return heur;}
				case InteractionsBetweenRegion:
					heur = new InteractionsBetweenRegion(grid);
					if (heur.getSolution() != null) {return heur;}
				case IsolatedGroups:
					heur = new IsolatedGroups(grid);
					if (heur.getSolution() != null) {return heur;}
				case MixedGroups:
					heur = new MixedGroups(grid);
					if (heur.getSolution() != null) {return heur;}
				case XWing:
					heur = new XWing(grid);
					if (heur.getSolution() != null) {return heur;}
				case XYWing:
					heur = new XYWing(grid);
					if (heur.getSolution() != null) {return heur;}
				case XYZWing:
					heur = new XYZWing(grid);
					if (heur.getSolution() != null) {return heur;}
				case Swordfish:
					heur = new Swordfish(grid);
					if (heur.getSolution() != null) {return heur;}
				case Jellyfish:
					heur = new Jellyfish(grid);
					if (heur.getSolution() != null) {return heur;}
				case Squirmbag:
					heur = new Squirmbag(grid);
					if (heur.getSolution() != null) {return heur;}
				case Burma:
					heur = new Burma(grid);
					if (heur.getSolution() != null) {return heur;}
				case Coloring:
					heur = new Coloring(grid);
					if (heur.getSolution() != null) {return heur;}
				case Backtracking:
					heur = new Backtracking(grid, true);
					if (heur.getSolution() != null) {return heur;}
				default:
					break;
			}
		}
		return null;
	}
	
	@Override
	public Solution getSolution(LevelHeuristics heuristic) {
		switch(heuristic) {
			case UniqueCandidate:
				return new UniqueCandidate(grid).getSolution();
			case IdenticalCandidates:
				return new IdenticalCandidates(grid).getSolution();
			case InteractionsBetweenRegion:
				return new InteractionsBetweenRegion(grid).getSolution();
			case IsolatedGroups:
				return new IsolatedGroups(grid).getSolution();
			case MixedGroups:
				return new MixedGroups(grid).getSolution();
			case OneCandidate:
				return new OneCandidate(grid).getSolution();
			case TwinsAndTriplet:
				return new TwinsAndTriplet(grid).getSolution();
			case XWing:
				return new XWing(grid).getSolution();
			case XYWing:
				return new XYWing(grid).getSolution();
			case XYZWing:
				return new XYZWing(grid).getSolution();
			case Swordfish:
				return new Swordfish(grid).getSolution();
			case Jellyfish:
				return new Jellyfish(grid).getSolution();
			case Squirmbag:
				return new Squirmbag(grid).getSolution();
			case Burma:
				return new Burma(grid).getSolution();
			case Coloring:
				return new Coloring(grid).getSolution();
			case Backtracking:
				return new Backtracking(grid, true).getSolution();
			default:
				break;
		}
		return null;
	}
	
	@Override
	public int getDifficulty() {
		if (difficulty != DEFAULT_DIFFICULTY) {
			return difficulty;
		}
		int higherDifficulty = DEFAULT_DIFFICULTY;
		int counter = 0;
		Grid tmp = copyCurrentGrid();
		RegroupHeuristics helps = tmp.getRegroupHeuristics();
		while (!tmp.isFull()) {
			IHeuristic sol = helps.getEasiestLevel();
			if (sol == null) {
				return DEFAULT_DIFFICULTY;
			}
			higherDifficulty = Math.max(higherDifficulty, sol.getLevel());
			for (Action action : sol.getSolution().getActions()) {
				action.act();
			}
			++counter;
			if (counter >= 729) {
				return DEFAULT_DIFFICULTY;
			}
		}
		if (higherDifficulty == LevelHeuristics.Backtracking.getLevel()) {
			difficulty = 0;
			return 0;
		}
		if (difficulty == DEFAULT_DIFFICULTY) {
			difficulty = (higherDifficulty / 2) + 1;
		}
		return (higherDifficulty == DEFAULT_DIFFICULTY ? DEFAULT_DIFFICULTY : (higherDifficulty / 2) + 1);
	}
	
	private Grid copyCurrentGrid() {
		Grid tmp = new StdGrid(StdGrid.defaultValueSet());
		for (int i = 0; i < grid.getSize(); ++i) {
			for (int j = 0; j < grid.getSize(); ++j) {
				if (grid.getCellAt(i, j).getValue() != null) {
					tmp.setValue(i, j, grid.getCellAt(i, j).getValue(), true);
				} else {
					for (String candidate : grid.getCellAt(i, j).getCandidates()) {
						tmp.addCandidate(i, j, candidate);
					}
				}
			}
		}
		return tmp;
	}
	
	@Override
	public int getMinDifficulty() {
		LevelHeuristics lh = sortedLH.get(0);
		if (lh == LevelHeuristics.Backtracking) {
			return 0;
		} else {
			return (lh.getLevel() == DEFAULT_DIFFICULTY ?
					DEFAULT_DIFFICULTY :
					(lh.getLevel() / 2) + 1);
		}
	}
	
	@Override
	public int getMaxDifficulty() {
		LevelHeuristics lh = sortedLH.get(sortedLH.size() - 2);
		if (lh == LevelHeuristics.Backtracking) {
			return 0;
		} else {
			return (lh.getLevel() == DEFAULT_DIFFICULTY ?
					DEFAULT_DIFFICULTY :
					(lh.getLevel() / 2) + 1);
		}
	}
	
	@Override
	public void resetDifficultyMemory() {
		difficulty = DEFAULT_DIFFICULTY;
	}
}
