package heuristics;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cmd.EliminateCandidate;
import model.Cell;
import model.Grid;

/**
 * Impl�mentation de l'heuristique Burma. Comme les heuristiques 
 * X-Wing (= Barma(2, 2)), Swordfish (= Barma(3, 3)), Jellyfish (= Barma(4, 4))
 *  et Squirmbag (= Barma(5, 5)) reprennent le m�me principe, seul le cas
 * Barma(6, 6) est impl�menter pour des questions de performance.
 * @author Thomas
 *
 */
public class Burma extends AHeuristic implements IHeuristic {

	// CONSTANTES
	public static final int MIN_NB_CAND = 6;
	public static final int MAX_NB_CAND = 6;
	
	public static final int MIN_NB_CELLS = 6;
	public static final int MAX_NB_CELLS = 6;
	
	public static final Color REASON_COLOR = Color.BLUE;
	public static final Color DELETE_COLOR = Color.ORANGE;
	
	
	// ATTRIBUTS
	
	private Map<Unity, Map<Integer, Cell[]>> data;
	
	// CONSTRUCTEURS
	
	public Burma(Grid grid) {
		super(IHeuristic.LevelHeuristics.Burma, grid);
		data = new HashMap<Unity, Map<Integer, Cell[]>>();
	}
	
	// REQUETES

	@Override
	public Solution getSolution() {

		for (String cand : getGrid().getValues()) {
			fillData(cand);
			if (!data.isEmpty()) {
				for (Unity u : Unity.values()) {
					if (!u.equals(Unity.REGION)) {
						int[] res = tryGetSolution(u, cand);
						if (res != null) {
							boolean correct = true;
							Set<Integer> op = getOppositeUnities(u, res);
							for (Integer i : res) {
								for (int j = 0; j < getGrid().getSize(); ++j) {
									if (op.contains(j)) {
										Cell ci = u.equals(Unity.LINE) ? getGrid().getCellAt(i, j) : 
											getGrid().getCellAt(j, i);
										if (ci.getValue() == null && !ci.getCandidates().contains(cand)) {
											correct = false;
										}
									}
								}
							}
							if (correct) {
								return setSolution(u, cand, res);
							}
							correct = false;
						}
					}
				}
				data.clear();	
			}
		}
		return null;
	}
	
	// OUTILS

	/**
	 * Remplit la variable data pour le candidat cand
	 */
	private void fillData(String cand) {
		for (Unity u : Unity.values()) {
			for (int i = 0; i < getGrid().getSize(); ++i) {
				Cell[] cells = getValidCells(u, i, cand);
				if (cells != null) {
					fillData(u, i, cells);
				}
			}
		}
	}

	private Solution setSolution(Unity unit, String cand, int[] unities) {
		StdSolution res = new StdSolution();
		Set<Integer> op = getOppositeUnities(unit, unities);
		for (Integer i : unities) {
			for (int j = 0; j < getGrid().getSize(); ++j) {
				if (op.contains(j)) {
					Cell ci = unit.equals(Unity.LINE) ? getGrid().getCellAt(i, j) : 
						getGrid().getCellAt(j, i);
					res.addReason(REASON_COLOR, ci);
				}
			}
		}
		for (int i : op) {
			for (int j = 0; j < getGrid().getSize(); ++j) {
				if (!isInTab(unities, j)) {
					Cell ci = !unit.equals(Unity.LINE) 
							? getGrid().getCellAt(i, j) 
							: getGrid().getCellAt(j, i);
					if (ci.getCandidates() != null 
							&& ci.getCandidates().contains(cand)) {
						try {
							res.addAction(new EliminateCandidate(ci, cand));
							res.addReason(DELETE_COLOR,  ci);
						} catch (SolutionInitializeException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		res.setDescription(setDescription(unit, cand));
		return res;
	}
	
	private String setDescription(Unity unit, String cand) {
		StringBuilder res = new StringBuilder();
		res.append("\"" + this.getLevelHeuristics().getName() + "\":\n");
		res.append("Burma avec les " + cand + ". Il est possible de "
				+ "supprimer les candidats " + cand + " des cases en rouge");
		return res.toString();
	}
	

	/**
	 * Renvoie l'unit� oppos�e (ligne -> colonne et colonne -> ligne)
	 */
	private Unity getOppositeUnity(Unity unit) {
		switch (unit) {
		case LINE:
			return Unity.COL;
		case COL: 
			return Unity.LINE;
		case REGION:
			return null;
		default:
			return null;
		}
	}
	
	/**
	 * Test si les l'unit� oppos� � unit � des candidats � supprimer
	 * 
	 */
	private boolean isUseful1(Unity unit, String cand, int[] unities, 
			Set<Integer> opposite) {
		Unity op = getOppositeUnity(unit);
		if (op == null) {
			return false;
		}
		
		for (Integer i : opposite) {
			for (int j = 0; j < getGrid().getSize(); ++j) {
				int x = unit.equals(Unity.LINE) ? j : i;
				int y = unit.equals(Unity.LINE) ? i : j;
				Cell ci = getGrid().getCellAt(x, y);
				int coord = unit.equals(Unity.LINE) ? ci.getCoordinate().getX() 
						: ci.getCoordinate().getY();
				if (!isInTab(unities, coord) 
						&& ci.getCandidates() != null
						&& ci.getCandidates().contains(cand)) {
					return true;
				}
			}
		}
		return false;
	}
		

	/**
	 * Test sur les cases des unit�s unit de num�ro unities ne poss�de que
	 * MAX_NB_CELLS colonnes en communs et si il y a des candidats � supprimer
	 * dans ces colonnes.
	 */
	private boolean testUnities(Unity unit, String cand, int[] unities) {
		for (int unity : unities) {
			if (data.get(unit).get(unity) == null) {
				return false;
			}
		}
		Set<Integer> res = getOppositeUnities(unit, unities);
		if (res.size() == MAX_NB_CELLS) {
			if (isUseful1(unit, cand, unities, res)) {
				return true;
			}
		}
		return false;
	}
	
	private Set<Integer> getOppositeUnities(Unity unit, int[] unities) {
		Set<Integer> res = new HashSet<>();
		for (int i : unities) {
			if (data.get(unit).get(i) != null) {
				for (Cell ci : data.get(unit).get(i)) {
					res.add((unit.equals(Unity.LINE) ? ci.getCoordinate().getY() 
							: ci.getCoordinate().getX()));
				}	
			}
		}
		return res;
	}
	
	/*
	 	private int[] tryGetSolution(Unity unit, String cand) {
		int size = data.get(unit).size();
		for (int i = 0; i < size; ++i) {
			for (int j = i + 1; j < size; ++j) {
				for (int k = j + 1; k < size; ++k) {
					for (int m = k + 1; m < size; ++m) {
						for (int n = m + 1; n < size; ++n) {
							for (int p = n + 1; p < size; ++p) {
								int[] tab = {(int) data.get(unit).keySet().toArray()[i],
										(int) data.get(unit).keySet().toArray()[j],
										(int) data.get(unit).keySet().toArray()[k],
										(int) data.get(unit).keySet().toArray()[m],
										(int) data.get(unit).keySet().toArray()[n],
										(int) data.get(unit).keySet().toArray()[p]};
								if (testUnities1(unit, cand, tab)) {
									return tab;
								}
							}
						}
					}
				}
			}
		}
		return null;
	}
	 */

	
	private int[] tryGetSolution(Unity unit, String cand) {
		if (unit.equals(Unity.REGION) || data.get(unit) == null) {
			return null;
		}
		final int UNITY_NB = MAX_NB_CELLS;
		int[] res = new int[UNITY_NB];
		return tryGetSolution(unit, cand, res, 0, 0, UNITY_NB);
	}
	
	private int[] tryGetSolution(Unity unit, String cand, int[] tab, int nb, int index, int max) {
		if (nb >= max) {
			if (testUnities(unit, cand, tab)) {
				return tab;
			}
			return null;
		} else {
			for (int i = index; i < data.get(unit).size(); ++i) {
				tab[nb] = (int) data.get(unit).keySet().toArray()[i];
				int[] res = tryGetSolution(unit, cand, tab, nb + 1, i + 1, max);
				if (res != null) {
					return res;
				}
			}
		}
		return null;
	}
	
	/**
	 * Retourne le tableau des cellules  de l'unit� u avec le candidat cand 
	 * si leur nombres est >= MIN_NB_CAND et <= MAX_NB_CAND
	 */
	private Cell[] getValidCells(Unity unit, int nb, String cand) {
		int cpt = 0;
		for (int i = 0; i < getGrid().getSize(); ++i) {
			int x = getX(unit, nb, i);
			int y = getY(unit, nb, i);
			if (getGrid().getCellAt(x, y).getCandidates() != null) {
				if (getGrid().getCellAt(x, y).getCandidates().contains(cand)) {
					cpt++;
				}
			}
		}
		if (cpt < MIN_NB_CAND || cpt > MAX_NB_CAND) {
			return null;
		}
		Cell[] res = new Cell[cpt];
		cpt = 0;
		for (int i = 0; i < getGrid().getSize(); ++i) {
			int x = getX(unit, nb, i);
			int y = getY(unit, nb, i);
			Cell ci = getGrid().getCellAt(x, y);
			if (ci.getCandidates() != null) {
				if (ci.getCandidates().contains(cand)) {
					res[cpt] = ci;
					cpt++;
				}
			}
		}
		return res;
	}

	/**
	 * Remplie la variable data avec l'unit� unit, le num�ro de l'unit� nb
	 * et les cellules cells.
	 */
	private void fillData(Unity unit, int nb, Cell[] cells) {
		if (unit == null || nb < 0 || nb >= getGrid().getSize() 
				|| cells == null) {
			return;
		}

		if (!data.containsKey(unit)) {
			data.put(unit, new HashMap<Integer, Cell[]>());
		}
		data.get(unit).put(nb, cells);
	}
	
	/**
	 * Permet de r�cup�rer la coordonn�e x d'une case en fonction de l'unit� u,
	 *  le num�ro de l'unit� n et le num�ro de la case dans l'unit�.
	 */
	private int getX(Unity u, int n, int nb) {
		int size = getGrid().getSizeSquare();
		int x  = -1;
		switch (u) {
			case LINE:
				x = n;
				break;
			case COL:
				x = nb;
				break;
			case REGION:
				x = (n / size) * size + nb / size;
				break;
			default:
				break;
		}
		return x;
	}
	
	/**
	 * Permet de r�cup�rer la coordonn�e y d'une case en fonction de l'unit� u,
	 *  le num�ro de l'unit� n et le num�ro de la case dans l'unit�.
	 */
	private int getY(Unity u, int n, int nb) {
		int size = getGrid().getSizeSquare();
		int y  = -1;
		switch (u) {
			case LINE:
				y = nb;
				break;
			case COL:
				y = n;
				break;
			case REGION:
				y = (n % size) * size + nb % size;
				break;
			default:
				break;
		}
		return y;
	}
	
	/**
	 * Test si n est dans l tableau tab
	 */
	private boolean isInTab(int[] tab, int n) {
		for (int i : tab) {
			if (n == i) {
				return true;
			}
		}
		return false;
	}
}
