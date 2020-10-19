package heuristics;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import cmd.EliminateCandidate;
import model.Cell;
import model.Grid;

@SuppressWarnings("unused")
public class XWing extends AHeuristic implements IHeuristic {

	private Map<String, Integer> mapStrings;
	private Map<Integer, String> mapValues;
	
	private static final int NB_ELEMENT = 2;
	
	public XWing(Grid grid) {
		super(IHeuristic.LevelHeuristics.XWing, grid);
		mapStrings = new HashMap<String, Integer>();
		mapValues = new HashMap<Integer, String>();
		setMapStrings(grid.getValues());
		setMapValues(grid.getValues());
	}

	@Override
	public Solution getSolution() {
		Solution sol = null;
		if ((sol = checkFirstSolution()) != null && sol.getActions().size() > 0) {
			return sol;
		}/*
		if ((sol = checkSecondSolution()) != null && sol.getActions().size() > 0) {
			return sol;
		}*/
		return null;
	}
	
	private Solution checkSecondSolution() {
		Grid grid = getGrid();
		int nbSquare = grid.getSizeSquare();
		/*
		 * Pour chaque valeur
		 */
		for (String value : mapValues.values()) {
			/*
			 * Pour chaque ligne/colonne de région de la grille.
			 */
			for (int k = 0; k < nbSquare; ++k) {
				Map<Integer, List<Integer>> mapLine = 
						new HashMap<Integer, List<Integer>>();
				Map<Integer, List<Integer>> mapColumn = 
						new HashMap<Integer, List<Integer>>();
				/*
				 * Pour chaque carré dans la ligne/colonne de région
				 */
				for (int i = 0; i < nbSquare; ++i) {
					List<Integer> listLine = new ArrayList<Integer>();
					List<Integer> listColumn = new ArrayList<Integer>();
					/*
					 * Pour chaque ligne dans les carrés
					 */
					for (int n = 0; n < nbSquare; ++n) {
						/*
						 * Pour chaque case de la ligne
						 */
						int nbValueLine = 0;
						int nbValueColumn = 0;
						for (int j = 0; j < nbSquare; ++j) {
							Cell cellLine = grid.getCellAt((n + k * nbSquare), (j + i * nbSquare));
							if (cellLine.getCandidates() != null && cellLine.getCandidates().contains(value)) {
								++nbValueLine;
							}
							Cell cellColumn = grid.getCellAt((j + i * nbSquare), (n + k * nbSquare));
							if (cellColumn.getCandidates() != null && cellColumn.getCandidates().contains(value)) {
								++nbValueColumn;
							}
						}
						if (nbValueLine == 1) {
							listLine.add(n);
						}
						if (nbValueColumn == 1) {
							listColumn.add(n);
						}
					}
					if (listLine.size() == 2) {
						mapLine.put(i, listLine);
					}
					if (listColumn.size() == 2) {
						mapColumn.put(i, listColumn);
					}
				}
				if (mapLine.size() == 2) {
					StdSolution solution = new StdSolution();
					setSecondSolution((StdSolution) solution, mapLine, k, value, true);
					if (solution.getActions().size() > 0) {
						return solution;
					}
				}
				if (mapColumn.size() == 2) {
					StdSolution solution = new StdSolution();
					setSecondSolution((StdSolution) solution, mapColumn, k, value, false);
					if (solution.getActions().size() > 0) {
						return solution;
					}
				}
			}
		}
		return null;
	}
	
	private void setSecondSolution(StdSolution solution, Map<Integer, List<Integer>> map,
			int k, String value, boolean fromLine) {
		Grid grid = getGrid();
		int sizeSquare = grid.getSizeSquare();
		List<Integer> listNotInMap = new ArrayList<Integer>();
		List<Integer> listInMap = new ArrayList<Integer>();
		for (int i = 0; i < sizeSquare; ++i) {
			listNotInMap.add(i);
		}
		for (List<Integer> list : map.values()) {
			for (Integer i : list) {
				listNotInMap.remove(i);
				if (!listInMap.contains(i)) {
					listInMap.add(i);
				}
			}
		}
		for (int num = 0; num < sizeSquare; ++num) {
			for (int i = 0; i < sizeSquare; ++i) {
				for (int j = 0; j < sizeSquare; ++j) {
					int x = (fromLine ? i + k * sizeSquare : i + num * sizeSquare);
					int y = (fromLine ? j + num * sizeSquare : j + k * sizeSquare);
					if (grid.getCellAt(x, y).getCandidates() != null &&
							grid.getCellAt(x, y).getCandidates().contains(value)) {
						try {
							if (map.containsKey(num) && listNotInMap.contains(i)) {
								solution.addAction(new EliminateCandidate(grid.getCellAt(x, y), value));
								solution.addReason(Color.BLUE, grid.getCellAt(x, y));
							} else if (!map.containsKey(num) && listInMap.contains(i)){
								solution.addAction(new EliminateCandidate(grid.getCellAt(x, y), value));
								solution.addReason(Color.BLUE, grid.getCellAt(x, y));
							} else if (map.containsKey(num) && listInMap.contains(i)) {
								solution.addReason(Color.MAGENTA, grid.getCellAt(x, y));
							}
						} catch (SolutionInitializeException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		StringBuilder description = getSecondDescription(map.keySet(), listInMap, k, value, fromLine);
		solution.setDescription(description.toString());
	}
	
	private StringBuilder getSecondDescription(Set<Integer> regions, List<Integer> nums, int k,
			String value, boolean fromLine) {
		StringBuilder sb = new StringBuilder();
		List<Integer> listNotRegion = new ArrayList<Integer>();
		List<Integer> listNotLine = new ArrayList<Integer>();
		for (int i = 0; i < getGrid().getSizeSquare(); ++i) {
			if (!regions.contains(i) && !listNotRegion.contains(i)) {
				listNotRegion.add(i);
			}
			if (!nums.contains(i) && !listNotLine.contains(i)) {
				listNotLine.add(i);
			}
		}
		sb.append("\"" + this.getLevelHeuristics().getName() + "\":\n");
		sb.append("On ne trouve le candidat '" + value + "' ");
		sb.append("que " + nums.size() + " fois dans les régions (");
		Object[] array = regions.toArray();
		sb.append((fromLine ? (Integer) array[0] + k * getGrid().getSizeSquare() + 1 :
			(Integer) array[0] * getGrid().getSizeSquare() + k + 1));
		for (int i = 1; i < array.length; ++i) {
			sb.append("," + (fromLine ? (Integer) array[i] + k * getGrid().getSizeSquare() :
				(Integer) array[i] * getGrid().getSizeSquare() + k));
		}
		sb.append("), de plus dans les mêmes ");
		sb.append(fromLine ? "lignes (" : "colonnes (");
		if (nums.size() > 0) {
			sb.append((nums.get(0) + 1));
			for (int i = 1; i < nums.size(); ++i) {
				sb.append("," + (nums.get(i) + 1));
			}
		}
		sb.append(").\nIl est donc possible de supprimer les autres '");
		sb.append(value + "' des " + nums.size());
		sb.append(fromLine ? " lignes " : " colonnes ");
		sb.append("correspondantes de la ");
		sb.append((listNotRegion.get(0) + 1) + suffixe(listNotRegion.get(0) + 1));
		for (int i = 1; i < listNotRegion.size(); ++i) {
			sb.append(", " + (listNotRegion.get(i) + 1) + suffixe(listNotRegion.get(i) + 1));
		}
		sb.append(" région.\nOn peut également retirer '");
		sb.append(value + "' des 2 régions correspondantes ");
		sb.append(listNotLine.size() > 1 ? "des " : " de la ");
		sb.append((listNotLine.get(0) + 1) + suffixe(listNotLine.get(0) + 1));
		for (int i = 1; i < listNotLine.size(); ++i) {
			sb.append(", " + (listNotLine.get(i) + 1) + suffixe(listNotLine.get(i) + 1));
		}
		sb.append(" " + (fromLine ? "ligne." : "colonne."));
		
		return sb;
	}
	
	private String suffixe(int i) {
		if (i == 1) {
			return "ère";
		} else {
			return "ème";
		}
	}
	
	private Solution checkFirstSolution() {
		Grid grid = getGrid();
		int size = getGrid().getSize();
		Set<Integer> setFirstLine = new TreeSet<Integer>();
		Set<Integer> setFirstColumn = new TreeSet<Integer>();
		Set<Integer> setSecondLine = new TreeSet<Integer>();
		Set<Integer> setSecondColumn = new TreeSet<Integer>();
		for (String value : mapValues.values()) {
			int indexFirst = 0;
			int indexSecond = 0;
			boolean notValide = false;
			while (indexFirst < size - 1) {
				int k = 0;
				while (k < size && 
						(setFirstLine.size() <= NB_ELEMENT || setFirstColumn.size() <= NB_ELEMENT)) {
					if (grid.getCellAt(indexFirst, k).getCandidates() != null &&
							grid.getCellAt(indexFirst, k).getCandidates().contains(value)) {
						setFirstLine.add(k);
					}
					if (grid.getCellAt(k, indexFirst).getCandidates() != null &&
							grid.getCellAt(k, indexFirst).getCandidates().contains(value)) {
						setFirstColumn.add(k);
					}
					++k;
				}
				indexSecond = indexFirst + 1;
				notValide = false;
				if (setFirstLine.size() == NB_ELEMENT) {
					while (indexSecond < size) {
						k = 0;
						while (k < size && setSecondLine.size() <= NB_ELEMENT) {
							if (!notValide && 
									grid.getCellAt(indexSecond, k).getCandidates() != null &&
									grid.getCellAt(indexSecond, k).getCandidates().contains(value)) {
								notValide = !setFirstLine.contains(k);
								setSecondLine.add(k);
							}
							++k;
						}
						if (!notValide && setFirstLine.equals(setSecondLine)) {
							StdSolution solution = new StdSolution();
							setFirstSolution((StdSolution) solution,  value, indexFirst, indexSecond,
									setSecondLine, true);
							if (solution.getActions().size() > 0) {
								return solution;
							}
						}
						setSecondLine.clear();
						notValide = false;
						++indexSecond;
					}
				}
				indexSecond = indexFirst + 1;
				notValide = false;
				if (setFirstColumn.size() == NB_ELEMENT) {
					while (indexSecond < size) {
						k = 0;
						while (k < size && setSecondColumn.size() <= NB_ELEMENT) {
							if (!notValide && 
									grid.getCellAt(k, indexSecond).getCandidates() != null &&
									grid.getCellAt(k, indexSecond).getCandidates().contains(value)) {
								notValide = !setFirstColumn.contains(k);
								setSecondColumn.add(k);
							}
							++k;
						}
						if (!notValide && setFirstColumn.equals(setSecondColumn)) {
							StdSolution solution = new StdSolution();
							setFirstSolution((StdSolution) solution,  value, indexFirst, indexSecond,
									setSecondColumn, false);
							if (solution.getActions().size() > 0) {
								return solution;
							}
						}
						setSecondColumn.clear();
						notValide = false;
						++indexSecond;
					}
				}
				setFirstLine.clear();
				setFirstColumn.clear();
				++indexFirst;
			}
		}
		return null;
	}
	
	private void setFirstSolution(StdSolution solution, String value, int num1, int num2,
			Set<Integer> list, boolean fromLine) {
		Grid grid = getGrid();
		for (int i = 0; i < getGrid().getSize(); ++i) {
			if (i != num1 && i != num2) {
				for (Integer j : list) {
					if (fromLine && grid.getCellAt(i, j).getCandidates() != null &&
							grid.getCellAt(i, j).getCandidates().contains(value)) {
						try {
							solution.addAction(new EliminateCandidate(grid.getCellAt(i, j), value));
							solution.addReason(Color.ORANGE, grid.getCellAt(i, j));
						} catch (SolutionInitializeException e) {
							e.printStackTrace();
						}
					} else if (!fromLine && getGrid().getCellAt(j, i).getCandidates() != null &&
							getGrid().getCellAt(j, i).getCandidates().contains(value)){
						try {
							solution.addAction(new EliminateCandidate(grid.getCellAt(j, i), value));
							solution.addReason(Color.ORANGE, grid.getCellAt(j, i));
						} catch (SolutionInitializeException e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				for (Integer j : list) {
					solution.addReason(Color.MAGENTA, grid.getCellAt(i, j));
				}
			}
		}
		StringBuilder description = getFirstDescription(value, num1, num2, list, fromLine);
		solution.setDescription(description.toString());
	}
	
	private StringBuilder getFirstDescription(String value, int num1, int num2, Set<Integer> list,
			boolean fromLine) {
		StringBuilder sb = new StringBuilder();
		sb.append("\"" + this.getLevelHeuristics().getName() + "\":\n");
		sb.append("Le candidat '" + value + "'");
		sb.append(" n'est disponible qu'à " + list.size() + " emplacement sur les ");
		sb.append(fromLine ? "lignes" : "colonnes");
		sb.append(" numéro " + num1 + " et " + num2);
		sb.append(".\nDe plus, ces candidats font partie des ");
		sb.append(fromLine ? "colonnes" : "lignes");
		Object[] values = list.toArray();
		sb.append(" " + values[0]);
		for (int i = 1; i < list.size(); ++i) {
			sb.append(" et " + values[i]);
		}
		sb.append(".\nIl est donc possible de supprimer tous les autres" +
				" candidats '" + value + "' de ces " + list.size() + " ");
		sb.append(fromLine ? "colonnes." : "lignes.");
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
