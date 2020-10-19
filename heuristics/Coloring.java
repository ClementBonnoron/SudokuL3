package heuristics;

import java.util.HashMap;
import java.util.Map;

import cmd.EliminateCandidate;

import java.awt.Color;

import model.Cell;
import model.Grid;

public class Coloring extends AHeuristic implements IHeuristic {

	// CONSTANTES
	
	public static final int IDENTICALS_CELLS = 2;
	public static final int MINIMUM_CELLS = 4;
	
	public static final Color ELIMINATE_COLOR = Color.ORANGE;
	public static final Color CAUSE_COLOR = Color.BLUE;
	// ATTRIBUTS
	
	/**
	 * Contient les cellules qui sont seules � avoir le m�me candidat sur une 
	 * unit�.
	 */
	private Cell[] start;
	
	/**
	 * Contient les cellules r�sultats
	 */
	private Map<Cell, Colour> result;
	
	// CONSTRUCTEURS
	
	public Coloring(Grid grid) {
		super(IHeuristic.LevelHeuristics.Coloring, grid);
		start = new Cell[IDENTICALS_CELLS];
		result = new HashMap<Cell, Colour>();
	}
	
	// REQUETES
	
	@Override
	public Solution getSolution() {
		for (Unity u : Unity.values()) {
			for (String cand : getGrid().getValues()) {
				for (int i = 0; i < getGrid().getSize(); ++i) {
					onlyTwoCandidates(cand, u, i);
					if (testStart()) {
						fillResult(cand, u, i);	
						// Cr�ation du tableau de cellules contenues dans result.
						Cell[] cells = new Cell[result.size()];
						int cpt = 0;
						for (Cell ci : result.keySet()) {
							cells[cpt] = ci;
							++cpt;
						}
						Colour col = testResult(cells, cand);
						if (col != null) {
							return setSolution(col, cand);
						} else {
							Cell cell = testResult2(cells, cand);
							if (cell != null) {
								return setSolution(cell, cand);
							} else {
								Cell[] cls = testResult3(cells, cand);
								if (cls != null) {
									return setSolution(cls, cand);
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
	
	/**
	 * Cr�er la solution une fois que l'heuristique est trouv�e.
	 * Coloriage est une heuristique qui poss�de plusieurs cas repr�sent�s ici
	 * par les variables donn�es en param�tre : 
	 * 		- Colour + cand :
	 * 				 2 cases de la m�me couleurs dans result ont la m�me unit�
	 * 		- Cell + cand : 
	 * 				1 cellule avec le canndidat cand poss�de est commune � 
	 * 				deux cases dans result avec une couleur diff�rente (test� 
	 * 				seulement dans le cas ou nb == 0 est faux donc que l'on a 
	 * 				une boucle)
	 * 		- Cell[] + cand : 
	 * 				Une r�gion contient 2 cases de couleurs diff�rentes et 
	 * 				d'autres cases poss�dant le candidat cand, ces derniers
	 * 				peuvent donc �tre supprim�s.
	 */
	private Solution setSolution(Colour col, String cand) {
		StdSolution res = new StdSolution();
		
		for (Cell c : result.keySet()) {
			if (result.get(c).equals(col)) {
				try {
					res.addAction(new EliminateCandidate(c, cand));
				} catch (SolutionInitializeException e) {
					e.printStackTrace();
					return null;
				}
				res.addReason(ELIMINATE_COLOR, c);
			} else {
				res.addReason(CAUSE_COLOR, c);
			}
		}
		res.setDescription("\"" + this.getLevelHeuristics().getName() + "\":\n"
			  + "Il est possible de supprimer les candidats " 
				+ cand.toString() + " des cases en " + col.getName() 
				+ ", car il y en a deux dans la m�me unit�.");
		return res;
	}
	
	private Solution setSolution(Cell cell, String cand) {
		StdSolution res = new StdSolution();
		try {
			res.addAction(new EliminateCandidate(cell, cand));
		} catch (SolutionInitializeException e1) {
			e1.printStackTrace();
		}
		res.addReason(ELIMINATE_COLOR, cell);
		
		for (Cell c : result.keySet()) {
			res.addReason(result.get(c).getColor(), c);	
		}
		
		res.setDescription("\"" + this.getLevelHeuristics().getName() + "\":\n"
		+ "Il est possible de supprimer les candidats " + cand
				+ " à l'intersection des deux couleurs");
		return res;
	}
	
	private Solution setSolution(Cell[] cells, String cand) {
		StdSolution res = new StdSolution();
		
		for (Cell ci : cells) {
			try {
				res.addAction(new EliminateCandidate(ci, cand));
			} catch (SolutionInitializeException e1) {
				e1.printStackTrace();
			}
			res.addReason(ELIMINATE_COLOR, ci);
		}
		
		for (Cell c : result.keySet()) {
			res.addReason(result.get(c).getColor(), c);	
		}
		
		res.setDescription("\"" + this.getLevelHeuristics().getName() + "\":\n"
		+ "Les candidats " + cand + " des cases en rouge "
				+ "peuvent etre supprimé car deux couleurs sont dans la meme"
				+ " unite.");
		return res;
	}
	
	// --- CASE 3 -------------------------------------------------------------
	
	/**
	 * Test le cas o� une r�gion possede 2 cases de couleurs diff�rentes et 
	 * 	d'autres cases avec le candidat cand.
	 */
	private Cell[] testResult3(Cell[] cells, String cand) {
		for (int i = 0; i < cells.length; ++i) {
			for (int j = i + 1; j < cells.length; ++j) {
				if (!result.get(cells[i]).equals(result.get(cells[j]))) {
					if (getNumberOfUnity(cells[i], Unity.REGION) 
							== getNumberOfUnity(cells[j], Unity.REGION)) {
						Cell[] res = getNewCellsOfRegion(getNumberOfUnity(cells[i], 
								Unity.REGION), cand);
						if (res != null) {
							return res;
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Retourne toutes les cellules de la region numero nb qui n'appartiennent
	 *  pas � result et qui poss�dent le candidat cand
	 */
	private Cell[] getNewCellsOfRegion(int nb, String cand) {
		Cell[] res = new Cell[getGrid().getSize()];
		int cpt = 0;
		for (int i = 0; i < getGrid().getSize(); ++i) {
			int x = getX(Unity.REGION, nb, i);
			int y = getY(Unity.REGION, nb, i);
			Cell ci = getGrid().getCellAt(x, y);
			if (!result.containsKey(ci) && ci.getCandidates() != null 
					&& ci.getCandidates().contains(cand)) {
				res[cpt] = ci;
				++cpt;
			}
		}
		
		if (cpt == 0) {
			return null;
		}
		Cell[] resCell = new Cell[cpt];
		for (int i = 0; i < res.length; ++i) {
			if (res[i] != null) {
				resCell[i] = res[i];
			}
		}
		
		return resCell;
	}
	
	// --- CAS 1 --------------------------------------------------------------
	
	/**
	 * Test si le contenu de cells permet de supprimer des candidats 
	 */
	private Colour testResult(Cell[] cells, String c) {
		// S'il n'y a pas au moins 5 cases, aucun croisement n'est possible
		if (result.keySet().size() <= MINIMUM_CELLS) {
			return null;
		}
		
		for (int i = 0; i < cells.length; ++i) {
			for (int j = i + 1; j < cells.length; ++j) {
				for (Unity unit : Unity.values()) {
					if (testBadCells(cells[i], cells[j], c, unit)) {
						return result.get(cells[i]);
					}	
				}
			}
		}
		return null;
	}
	
	/**
	 * Test si des cellules dans result avec la m�me couleur ont une unit� 
	 * commune et si elles sont les seules � poss�der ce candidat.
	 * Renvoie vrai si les cellules permettre d'avoir une heuristique vraie
	 */
	private boolean testBadCells(Cell c1, Cell c2, String cand, Unity u) {
		return result.get(c1).equals(result.get(c2)) 
				&& getNumberOfUnity(c1, u) == getNumberOfUnity(c2, u);
	}

	/**
	 * Commence par supprimer tous les �lements de la variable de classe
	 * result pour ensuite la remplir avec toutes les cases qui sont les seules
	 * de leur unit� � posseder le candidat c � partir des cellules stock�es 
	 * dans la variable de classe start.
	 */
	private void fillResult(String c, Unity unit, int n) {
		result.clear();
		for (int i = 0; i < start.length; ++i) {
			Colour coul = Colour.values()[i];
			if (!result.containsKey(start[i])) {
				result.put(start[i], coul);
				fillResultWithCell(c, start[i], unit, coul.getNextColour());		
			}
		}
	}
	
	/**
	 * Parcours les cellules pour les ajouter dans r�sultats lorsqu'elles 
	 * correspondent aux cellules voulut sur toutes les unit�s en alternant
	 * la couleur � enregistrer.
	 */
	private void fillResultWithCell(String cand, Cell c, Unity unit, Colour coul) {
		for (Unity u : Unity.values()) {
			if (!u.equals(unit)) {
				Cell res = testOnlyTwoCandidates(c, cand, u, getNumberOfUnity(c, u));
				if (res != null) {
					if (result.containsKey(res)) {
						return;
					}
					result.put(res, coul);
					fillResultWithCell(cand, res, u, coul.getNextColour());
				}
			}
		}
	}
	
	
	/**
	 * Permet d'obtenir le num�ro de l'unit� � partir d'une cellule et du 
	 * type de l'unit�.
	 */
	private int getNumberOfUnity(Cell c, Unity unit) {
		int size = getGrid().getSizeSquare();
		switch (unit) {
		case LINE:
			return c.getCoordinate().getX();
		case COL:
			return c.getCoordinate().getY();
		case REGION:
			return (c.getCoordinate().getX() / size) * size 
					+ c.getCoordinate().getY() / size;
		}
		return -1;
	}
	
	
	/**
	 * Test si l'unit� u num�ro n poss�de seulement 2 candidats et si c'est le
	 * cas, renvoie la cellule diff�rente de c
	 */
	private Cell testOnlyTwoCandidates(Cell cell, String c, Unity u, int n) {
		Cell res = null;
		int count = 0;
		for (int i = 0; i < getGrid().getSize(); ++i) {
			int x = getX(u, n, i);
			int y = getY(u, n, i);
			Cell ci = getGrid().getCellAt(x, y);
			if (ci.getCandidates() != null) {
				if (ci.getCandidates().contains(c)) {
					if (count == IDENTICALS_CELLS) {
						return null;
					}
					if (ci != cell) {
						res = ci;
					}
					++count;
				}
			}
		}
		return count == IDENTICALS_CELLS ? res : null;
	}
	
	
	/**
	 * Cette m�thode test si une unit� ne poss�de que 2 cases avec le m�me 
	 * candidat. Si c'est le cas, ces cases sont stock�es dans la variable de
	 * classe start, sinon, start poss�dera des pointeurs nuls.
	 */
	private void onlyTwoCandidates(String c, Unity u, int n) {
		clearStart();
		int count = 0;
		for (int i = 0; i < getGrid().getSize(); ++i) {
			int x = getX(u, n, i);
			int y = getY(u, n, i);
			Cell ci = getGrid().getCellAt(x, y);
			if (ci.getCandidates() != null) {
				if (ci.getCandidates().contains(c)) {
					if (count == IDENTICALS_CELLS) {
						clearStart();
						return;
					}
					start[count] = ci;
					++count;
				}
			}
		}
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
	 * Test s'il est necessaire de remplir result ou non � partir du contenu
	 * de start
	 */
	private boolean testStart() {
		for (Cell c : start) {
			if (c == null) {
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * Supprime les pointeurs des variables dans start.
	 */
	private void clearStart() {
		for (int i = 0; i < start.length; ++i) {
			start[i] = null;
		}
	}
	

	
	// --- CAS 2 --------------------------------------------------------------
	
	/**
	 * Methode pour le cas 2 : 
	 * Permet de savoir si une cellule est sur la m�me unit� (COL/LINE)
	 *  que 2 cases de couleurs diff�rentes dans result
	 */
	private Cell testResult2(Cell[] cells, String c) {
		if (result.keySet().size() < MINIMUM_CELLS) {
			return null;
		}
	
		for (int i = 0; i < cells.length; ++i) {
			for (int j = i + 1; j < cells.length; ++j) {
				if (!result.get(cells[i]).equals(result.get(cells[j]))) {
					if (hasNotSameUnity(cells[i], cells[j])) {
						Cell ci = getInCommonCell(cells[i], cells[j], c);
						if (ci != null) {
							return ci;
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Renvoie la cellule en commun de c1 et c2 possedant le candidat cand.
	 * Si cette cellule n'existe pas, renvoie null.
	 */
	private Cell getInCommonCell(Cell c1, Cell c2, String cand) {
		Cell ci = getGrid().getCellAt(c1.getCoordinate().getX(), 
				c2.getCoordinate().getY());
		if (!result.containsKey(ci) && ci.getCandidates() != null
				&& ci.getCandidates().contains(cand)) {
			return ci;
		}
		ci = getGrid().getCellAt(c2.getCoordinate().getX(), 
				c1.getCoordinate().getY());
		if (!result.containsKey(ci) && ci.getCandidates() != null
				&& ci.getCandidates().contains(cand)) {
			return ci;
		}
		return null;
	}
	
	/**
	 * Test si les cellules c1 et c2 ont une unit� en commun.
	 */
	private boolean hasNotSameUnity(Cell c1, Cell c2) {
		for (Unity u : Unity.values()) {
			if (getNumberOfUnity(c1, u) == getNumberOfUnity(c2, u)) {
				return false;
			}
		}
		return true;
}
		
	// CLASSES
	
	/**
	 * Repr�sente les diff�rentes couleurs que le coloriage va utiliser.
	 * Seulement 2 sont utiles pour cette heuristique.
	 */
	private enum Colour {
		GREEN("vert", Color.GREEN),
		BLUE("bleu", Color.BLUE);
		
		// ATTRIBUTS
		
		private String name;
		private Color color;
		
		// CONSTRUCTEURS
		
		Colour(String s, Color c) {
			name = s;
			color = c;
		}
		
		// REQUETES
		
		public Color getColor() {
			return color;
		}
		
		public String getName() {
			return name;
		}
		
		// COMMANDES
		
		public Colour getNextColour() {
			return Colour.values()[(this.ordinal() + 1) % Colour.values().length];
		}
	}
}
