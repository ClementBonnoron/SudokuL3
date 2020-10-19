package heuristics;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import cmd.EliminateCandidate;
import model.Cell;
import model.Grid;

public class IdenticalCandidates extends AHeuristic implements IHeuristic {
	
	// CONSTANTES
	
	public static final Color REASON_COLOR = Color.BLUE;
	public static final Color ACTION_COLOR = Color.ORANGE;
	
	public IdenticalCandidates(Grid grid) {
		super(IHeuristic.LevelHeuristics.IdenticalCandidates, grid);
	}

	@Override
	public Solution getSolution() {
		int size = getGrid().getSize();
		// Case (i, j) et regarder les cases (i, k), (k, j) et 
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				Cell c = getGrid().getCellAt(i, j);
				Set<String> cands = c.getCandidates();
				if (cands != null) {
					Set<Cell> reasons = new HashSet<Cell>();
					// Sur un ligne 
					for (int k = j + 1; k < size; ++k) {
						Cell cc = getGrid().getCellAt(i, k);
						if (identicalsCandidates(c, cc)) {
							reasons.add(cc);
						}
					}
					if (reasons.size() + 1 == c.getCandidates().size()) {
						if (isUseful(c, reasons, 0)) {
							return setSolution(c, reasons, 0);
						}
					}
					reasons.clear();
					
					// Sur une colonne
					for (int k = i + 1; k < size; ++k) {
						Cell cc = getGrid().getCellAt(k, j);
						if (identicalsCandidates(c, cc)) {
							reasons.add(cc);
						}
					}
					if (reasons.size() + 1 == c.getCandidates().size()) {
						if (isUseful(c, reasons, 1)) {
							return setSolution(c, reasons, 1);
						}
					}
					reasons.clear();
					
					// Sur une r�gion
					int ss = getGrid().getSizeSquare();
					for (int m = i / ss * ss; m < i / ss * ss + ss; ++m) {
						for (int n = j / ss * ss; n < j / ss * ss + ss; ++n) {
							if (m != i || n != j) {
								Cell cc = getGrid().getCellAt(m, n);
								if (identicalsCandidates(c, cc)) {
									reasons.add(cc);
								}
							}
						}
					}
					if (reasons.size() + 1 == c.getCandidates().size()) {
						if (isUseful(c, reasons, 2)) {
							return setSolution(c, reasons, 2);
						}
					}
					reasons.clear();
				}
			}
		}
		return null;
	}
	
	// OUTILS
	
	/* Le param�tre type correspond au type d'unit� dans laquelle l'heuristique
	 * a trouv�e un r�sultat : 
	 * 		- 0 : Ligne
	 * 		- 1 : Colonne
	 *   	- 2 : R�gion
	 */
	
	private Solution setSolution(Cell c, Set<Cell> reasons, int type) {
		StdSolution res = new StdSolution();
		for (Cell cl : reasons) {
			res.addReason(REASON_COLOR, cl);
		}
		res.addReason(REASON_COLOR, c);

		int line = c.getCoordinate().getX();
		int col = c.getCoordinate().getY();
		for (String v : c.getCandidates()) {
			try {
				switch (type) {
				case 0:
					for (int i = 0; i < getGrid().getSize(); ++i) {
						Cell cc = getGrid().getCellAt(line, i);
						if (cc != c && !reasons.contains(cc) && containsCandidate(cc, v)) {
							res.addAction(new EliminateCandidate(cc, v));
							res.addReason(ACTION_COLOR, cc);
						}
					}
					break;
				case 1:
					for (int i = 0; i < getGrid().getSize(); ++i) {
						Cell cc = getGrid().getCellAt(i, col);
						if (cc != c && !reasons.contains(cc) && containsCandidate(cc, v)) {
							res.addAction(new EliminateCandidate(cc, v));
							res.addReason(ACTION_COLOR, cc);
						}
					}
					break;
				case 2:
					int ss = getGrid().getSizeSquare();
					int baseL = line / ss * ss;
					int baseC = col / ss * ss;
					for (int i = baseL; i < baseL + ss; ++i) {
						for (int j = baseC; j < baseC + ss; ++j) {
							Cell cc = getGrid().getCellAt(i, j);
							if (cc != c && !reasons.contains(cc) && containsCandidate(cc, v)) {
								res.addAction(new EliminateCandidate(cc, v));
								res.addReason(ACTION_COLOR, cc);
							}
						}
					}
					break;
				default:
					return null;
				}
			} catch (SolutionInitializeException e) {
				return null;
			}
				
		}
		res.setDescription(setDescription(c, type));
		return res;
	}
	
	private String setDescription(Cell c, int type) {
		StringBuilder res = new StringBuilder();
		res.append("\"" + this.getLevelHeuristics().getName() + "\":\n");
		String unity = null;
		int coord = -1;
		switch (type) {
			case 0:
				unity = "ligne";
				coord = c.getCoordinate().getX() + 1;
				break;
			case 1:
				unity = "colonne";
				coord = c.getCoordinate().getY() + 1;
				break;
			case 2:
				int ss =  getGrid().getSizeSquare();
				unity = "r�gion";
				coord = (c.getCoordinate().getX() / ss) * ss
						+(c.getCoordinate().getY() / ss) * ss + 1;
				break;
			default:
				return "Pas de solution";
		}
		int size = c.getCandidates().size();
		res.append("Les " + size + " candidats ");
		for (String v : c.getCandidates()) {
			res.append(v + " ");
		}
		res.append("sont pr�sents tous les " + size + " dans " + size 
				+ " cases de la m�me " + unity + " : n�" + coord + ", il est donc possible de "
				+ "les supprimer dans les autres cases de cette unit�.");
		return res.toString();
	}
	
	private boolean identicalsCandidates(Cell c1, Cell c2) {
		if (c1.getCandidates() == null || c2.getCandidates() == null 
				|| c1.getCandidates().size() != c2.getCandidates().size()) {
			return false;
		}
		
		int count = 0;
		for (String v : c2.getCandidates()) {
			if (!c1.getCandidates().contains(v)) {
				return false;
			}
			count++;
		}
		return count == c1.getCandidates().size();
	}
	
	private boolean containsCandidate(Cell c1, String v) {
		if (c1.getCandidates() == null) {
			return false;
		}
		return c1.getCandidates().contains(v);
	}
	
	/*
	 * Indique si la cellule c2 contient au moins un candidat
	 * 	de la cellule c1.
	 */
	private boolean containsCandidates(Cell c1, Cell c2) {
		if (c1.getCandidates() == null || c2.getCandidates() == null) {
			return false;
		}
		
		for (String v : c1.getCandidates()) {
			if (c2.getCandidates().contains(v)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isUseful(Cell c, Set<Cell> cells, int type) {
		int line = c.getCoordinate().getX();
		int col = c.getCoordinate().getY();
		switch (type) {
			case 0:
				for (int i = 0; i < getGrid().getSize(); ++i) {
					Cell cc = getGrid().getCellAt(line, i);
					if (cc != c && !cells.contains(cc) 
							&& containsCandidates(c, cc)) {
						return true;
					}
				}
				break;
				
			case 1:
				for (int i = 0; i < getGrid().getSize(); ++i) {
					Cell cc = getGrid().getCellAt(i, col);
					if (cc != c && !cells.contains(cc) 
							&& containsCandidates(c, cc)) {
						return true;
					}
				}
				break;
				
			case 2:
				int ss = getGrid().getSizeSquare();
				int baseL = line / ss * ss;
				int baseC = col / ss * ss;
				for (int i = baseL; i < baseL + ss; ++i) {
					for (int j = baseC; j < baseC + ss; ++j) {
						Cell cc = getGrid().getCellAt(i, j);
						if (cc != c && !cells.contains(cc) 
								&& containsCandidates(c, cc)) {
							return true;
						}
					}
				}
				break;
				
			default:
				break;
		}
		
		return false;
	}
}
