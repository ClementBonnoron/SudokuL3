package heuristics;

import java.awt.Color;

import cmd.EliminateCandidate;
import model.Cell;
import model.Grid;

public class InteractionsBetweenRegion extends AHeuristic implements IHeuristic {
	
	// CONSTANTES
	
	public final Color REMOVED = Color.ORANGE;
	public final Color HELP = Color.GREEN;
	public final Color REASON = Color.BLUE;
	
	// CONSTRUCTEURS
	
	public InteractionsBetweenRegion(Grid grid) {
		super(IHeuristic.LevelHeuristics.InteractionsBetweenRegion, grid);
	}
	
	// REQUETES
	
	@Override
	public Solution getSolution() {
		int size = getGrid().getSize();
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
			}
		}
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				if (getGrid().getCellAt(i, j).getCandidates() != null) {
					for (String v : getGrid().getCellAt(i, j).getCandidates()) {
						// Parcours la ligne 
						int findedL = -1;
						for (int k = 0; k < size; ++k) {
							if (!isSameBlock(j, k)) {
								Cell cc = getGrid().getCellAt(i, k);
								if (cc.getCandidates() != null 
										&& cc.getCandidates().contains(v)) {
									findedL = k;;
									break;
								}
							}
						}
						if (findedL == -1 
								&& isInteraction(i, j, v, true)
								&& isUseful(i, j, v, true)
								&& test(i, j, v, true)) {
							return setSolution(i, j, v, true);
						}
					
						// Parcours la colonne
						int findedC = -1;
						for (int k = 0; k < size; ++k) {
							if (!isSameBlock(i, k)) {
								Cell cc = getGrid().getCellAt(k, j);
								if (cc.getCandidates() != null 
										&& cc.getCandidates().contains(v)) {
									findedC = k;
									break;
								}
							}
						}
						if (findedC == -1 
								&& isInteraction(i, j, v, false)
								&& isUseful(i, j, v, false)
								&& test(i, j, v, false)) {
							return setSolution(i, j, v, false);
						}
					}
					
				}
			}
		}
		return null;
	}
	
	// OUTILS
	
	private Solution setSolution(int line, int col, String value, boolean isLine) {
		StdSolution res = new StdSolution();
		int size  = getGrid().getSize();
		int ss = getGrid().getSizeSquare();
		
		int base = (isLine ? line / ss * ss : col / ss * ss);
		int t = isLine ? col : line;
		int tbase = t / ss * ss;
		for (int i = base; i < base + getGrid().getSizeSquare(); ++i) {
			for (int j = 0; j < size; ++j) {
				if (i != (isLine ? line : col)) {
					Cell c = isLine ? getGrid().getCellAt(i, j) 
							: getGrid().getCellAt(j, i);
					if (c.getCandidates() != null) {
						if (j != tbase + j % ss) {
							if (c.getCandidates().contains(value)) {
								res.addReason(REASON, c);
							}
						} else {
							if (c.getCandidates().contains(value)) {
								try {
									res.addAction(new EliminateCandidate(c, value));
									res.addReason(REMOVED, c);
								} catch (SolutionInitializeException e) {
									return null;
								}
							}
						}
					}
				}
			}
		}
		res.setDescription(setDescriptionSolution(value, isLine));
		return res;
	}
	
	private String setDescriptionSolution(String value, boolean isLine) {
		StringBuilder res = new StringBuilder();
		res.append("\"" + this.getLevelHeuristics().getName() + "\":\n");
		String s = isLine ? "lignes" : "colonnes";
		res.append("Le candidat " + value + " n'est pr�sent que sur 2 " + s + " de 2 r�gions align�es.\n"
				+ "On peut donc le supprimer dans ces " + s + " de la 3eme r�gion.");
		return res.toString();
	}
	
	private boolean isSameBlock(int x1, int x2) {
		int blocksNb =getGrid().getSizeSquare();
		return x1 / blocksNb == x2 / blocksNb;
	}
	
	private boolean isInteraction(int numL, int numC, String v, boolean isLine) {
		int ss = getGrid().getSizeSquare();
		int t = (isLine ? numL : numC);
		int base = t / ss * ss;
		for (int i = base; i < base + ss; ++i) {
			int inv = (!isLine ? numL : numC) / ss * ss; 
			if (i != t) {
				int cpt = 0;
				boolean finded = false;
				for (int j = 0; j < getGrid().getSize(); ++j) {
					if (j != inv + j % ss) {
						if (j % ss == 0) {
							finded = false;
						}
						Cell c = isLine ? getGrid().getCellAt(i, j) : getGrid().getCellAt(j, i);
						if (v.equals(c.getValue())) {
							return false;
						}
						if (!finded && c.getCandidates() != null && c.getCandidates().contains(v)) {
							finded = true;
							cpt += 1;
						}
					}
				}
				if (cpt != getGrid().getSizeSquare() - 1) {
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean isUseful (int numL, int numC, String v, boolean isLine) {
		int ss = getGrid().getSizeSquare();
		int t = (isLine ? numL : numC);
		int base = t / ss * ss;
		for (int i = base; i < base + ss; ++i) {
			if (i != t) {
				int inv = (!isLine ? numL : numC) / ss * ss; 
				for (int j = inv; j < ss + inv; ++j) {
					Cell c = isLine ? getGrid().getCellAt(i, j) : getGrid().getCellAt(j, i);
					if (c != getGrid().getCellAt(numL, numC) 
							&& c.getCandidates() != null 
							&& c.getCandidates().contains(v)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Test si les 2 autres r�gions poss�dent bien la valeur value dans une de 
	 * leur case
	 */
	private boolean test(int line, int col, String value, boolean isLine) {
		int size  = getGrid().getSize();
		int ss = getGrid().getSizeSquare();
		
		int base = (isLine ? line / ss * ss : col / ss * ss);
		int count = 0;
		int t = isLine ? col : line;
		int tbase = t / ss * ss;
		for (int i = base; i < base + getGrid().getSizeSquare(); ++i) {
			for (int j = 0; j < size; ++j) {
				if (i != (isLine ? line : col)) {
					Cell c = isLine ? getGrid().getCellAt(i, j) 
							: getGrid().getCellAt(j, i);
					if (c.getCandidates() != null) {
						if (j != tbase + j % ss) {
							continue;
						} else {
							if (c.getCandidates().contains(value)) {
								++count;
							}
						}
					}
				}
			}
		}
		return count != 0;

	}
}