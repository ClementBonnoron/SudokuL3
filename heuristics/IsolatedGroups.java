package heuristics;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cmd.EliminateCandidate;
import model.Cell;
import model.Grid;

public class IsolatedGroups extends AHeuristic implements IHeuristic {
	
	// CONSTANTES
	
	public static final Color REASON_COLOR = Color.BLUE;
	public static final Color ACTION_COLOR = Color.ORANGE;
	private static final int MIN_SIZE = 2;
	private static final int MAX_SIZE = 4;
	
	// ATTRIBUTS
	
	private Map<Cell, List<String>> map;
	
	// CONSTRUCTEURS
		
	public IsolatedGroups(Grid grid) {
		super(IHeuristic.LevelHeuristics.IsolatedGroups, grid);
		map = new LinkedHashMap<Cell, List<String>>();

	}

	// REQUETES
	
	@Override
	public Solution getSolution() {
		int size = getGrid().getSize();
		for (Unity u : Unity.values()) {
			for (int i = 0; i < size; ++i) {
				for (int j = 0; j < size; ++j) {
					map.clear();
					if (getGrid().getCellAt(i, j).getValue() == null) {
						fillMap(i, j, u);
					}
					if (!map.isEmpty()) {
						for (int n = 2; n <= MAX_SIZE; ++n) {
							Cell[] res = tryUnion(n);
							if (res != null) {
								if (testSolution(res)) {
									return setSolution(res, u, getNb(i, j, u) + 1);
								}	
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	// OUTILS
	
	private boolean isCellSolution(Cell c, Cell[] solution) {
		if (solution == null || c == null) {
			return false;
		}
		for (Cell ci : solution) {
			if (c == ci) {
				return true;
			}
		}
		return false;
	}
	
	private boolean testSolution(Cell[] val) {
		String[] union = calcUnion(val);
		for (Cell c : map.keySet()) {
			if (c.getCandidates() != null && !isCellSolution(c, val)) {
				for (String cand : c.getCandidates()) {
					if (tabContains(union, cand)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private Solution setSolution(Cell[] val, Unity unit, int nb) {
		StdSolution res = new StdSolution();
		String[] union = calcUnion(val);
		for (Cell c : map.keySet()) {
			if (c.getCandidates() != null) {
				res.addReason(REASON_COLOR, c);
			}
			if (c.getCandidates() != null && !isCellSolution(c, val)) {
				for (String cand : c.getCandidates()) {
					if (tabContains(union, cand)) {
						res.removeReason(c);
						res.addReason(ACTION_COLOR, c);
						try {
							res.addAction(new EliminateCandidate(c, cand));
						} catch (SolutionInitializeException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		res.setDescription(setDescription(unit, union, nb));
		return res;
	}
	
	private String setDescription(Unity unit, String[] val, int nb) {
		StringBuilder res = new StringBuilder(); 
		res.append("\"" + this.getLevelHeuristics().getName() + "\":\n");
		res.append("Les " + val.length + " candidats : ");
		for (String s : val) {
			res.append(s + " ");
		}
		res.append("sont pr�sents seuls dans " + val.length 
				+ " cases sur la " + unit.getName() + " numero " + nb
				+ ", il est donc possible de les supprimer"
				+ " dans ces cases.");
		return res.toString();
	}
	
	private boolean tabContains(String[] tab, String val) {
		for (String si : tab) {
			if (si.equals(val)) {
				return true;
			}
		}
		return false;
	}

	private Cell[] tryUnion(int n) {
		Cell[] tab = new Cell[getGrid().getSize()];
		int count = 0;
		for (Cell c : map.keySet()) {
			if (map.get(c) != null) {
				if (map.get(c).size() <= n && map.get(c).size() >= MIN_SIZE) {
					tab[count] = c;
					++count;
				}
			}
		}
		if (count == 0) {
			return null;
		}
		Cell[] res = new Cell[count];
		count = 0;
		while (count < tab.length && tab[count] != null) {
			res[count] = tab[count];
			++count;
		}
		if (res.length < n) {
			return null;
		}
		return union(res, n);
	}
	
	private Cell[] union(Cell[] tab, int n) {
		Cell[][] ens = new Cell[(int) Math.pow(tab.length, n)][n];
		int count = 0;
		switch (n) {
		case 2:
			for (int i = 0; i < tab.length; ++i) {
				for (int j = i + 1; j < tab.length; ++j) {
					ens[count][0] = tab[i];
					ens[count][1] = tab[j];
					count++;
				}
			}
			break;
		case 3:
			for (int i = 0; i < tab.length; ++i) {
				for (int j = i + 1; j < tab.length; ++j) {
					for (int k = j + 1; k < tab.length; ++k) {
						ens[count][0] = tab[i];
						ens[count][1] = tab[j];
						ens[count][2] = tab[k];
						count++;
					}
				}
			}
			break;
		case 4:
			for (int i = 0; i < tab.length; ++i) {
				for (int j = i + 1; j < tab.length; ++j) {
					for (int k = j + 1; k < tab.length; ++k) {
						for (int m = k + 1; m < tab.length; ++m) {
							ens[count][0] = tab[i];
							ens[count][1] = tab[j];
							ens[count][2] = tab[k];
							ens[count][3] = tab[m];
							count++;
						}
					}
				}
			}
			break;
		case 5:
			for (int i = 0; i < tab.length; ++i) {
				for (int j = i + 1; j < tab.length; ++j) {
					for (int k = j + 1; k < tab.length; ++k) {
						for (int m = k + 1; m < tab.length; ++m) {
							for (int nn = m + 1; nn < tab.length; ++nn) {
								ens[count][0] = tab[i];
								ens[count][1] = tab[j];
								ens[count][2] = tab[k];
								ens[count][3] = tab[m];
								ens[count][4] = tab[nn];
								count++;
							}
						}
					}
				}
			}
			break;
		case 6:
			for (int i = 0; i < tab.length; ++i) {
				for (int j = i + 1; j < tab.length; ++j) {
					for (int k = j + 1; k < tab.length; ++k) {
						for (int m = k + 1; m < tab.length; ++m) {
							for (int nn = m + 1; nn < tab.length; ++nn) {
								for (int o = nn + 1; o < tab.length; ++o) {
									ens[count][0] = tab[i];
									ens[count][1] = tab[j];
									ens[count][2] = tab[k];
									ens[count][3] = tab[m];
									ens[count][4] = tab[nn];
									ens[count][5] = tab[o];
									count++;	
								}
							}
						}
					}
				}
			}
			break;
		default:
			return null;
		}
		for (Cell[] st : ens) {
			if (calcUnion(st).length == n) {
				return st;
			}
		}
		return null;
	}
	
	private String[] calcUnion(Cell[] tab) {
		Set<String> res = new HashSet<String>();
		for (Cell c : tab) {
			if (c != null) {
				for (String s : map.get(c)) {
					if (!res.contains(s)) {
						res.add(s);
					}
				}
			}
		}
		Object[] ol = res.toArray();
		String[] s = new String[res.size()];
		for (int i = 0; i < s.length; ++i) {
			s[i] = (String) ol[i];
		}
		return s;
	}
	
	private void fillMap(int line, int col, Unity unity) {
		int x;
		int y;
		for (int i = 0; i < getGrid().getSize(); ++i) {
			switch (unity) {
				case LINE:
					x = line;
					y = i;
					break;
				case COL:
					x = i;
					y = col;
					break;
				case REGION:
					x = getBase(line) + (i / getGrid().getSizeSquare());
					y = getBase(col) + (i % getGrid().getSizeSquare());
					break;
				default:
					return;
			}
			Cell c = getGrid().getCellAt(x, y);
			map.put(c, new ArrayList<String>());
			if (c.getCandidates() != null) {
				for (String s : c.getCandidates()) {
					map.get(c).add(s);
				}
			}
		}
	}
	
	private int getBase(int i) {
		return i / getGrid().getSizeSquare() * getGrid().getSizeSquare();
	}
	
	private int getNb(int i, int j, Unity u) {
		switch(u) {
		case LINE:
			return i;
		case COL:
			return j;
		case REGION:
			return getBase(i) + j / getGrid().getSizeSquare();
		}
		return -1;
	}
}