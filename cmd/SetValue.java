package cmd;

import java.util.ArrayList;
import java.util.List;

import heuristics.Unity;
import model.Cell;
import model.Grid;

public class SetValue extends AbstractAction implements Action {
	
	// ATTRIBUTS
	
	private String backup;
	private Grid grid;
	private List<Cell> removeCandidateCells;
	/**
	 * Si true, permet de rajouter tous les candidats possibles des cases 
	 * communes, sinon ajoute/retire juste la valeur de la case.
	 */
	private boolean advanced;
	
	// CONSTRUCTEUR
	
	private SetValue(Cell cell, String value) {
		super(cell, value);
		backup = cell.getValue();
		removeCandidateCells = new ArrayList<Cell>();
	}
	
	public SetValue(Cell cell, Grid grid, String value, boolean advanced) {
		super(cell, value);
		if (grid == null) {
			throw new AssertionError("constructor grid null : SetValue");
		}
		backup = cell.getValue();
		this.grid = grid;
		this.advanced = advanced;
		removeCandidateCells = new ArrayList<Cell>();
	}
	
	// COMMANDES
	
    @Override
    protected void doIt() {
			getCell().setValue(getValue());
			if (advanced) {
				if (getValue() != null) {
					if (grid != null) {
						for (Unity u : Unity.values()) {
							int n = getNumberOfUnity(this.getCell(), u);
							for (int i = 0; i < grid.getSize(); ++i) {
								Cell ci = grid.getCellAt(getX(u, n, i),
										getY(u, n, i));
								if (!ci.isBlocked() && ci.getCandidates() != null 
										&& ci.getCandidates().contains(getValue())) {
									removeCandidateCells.add(ci);
								}
							}
						}
					}
					for (Cell c : removeCandidateCells) {
						c.eliminateCandidate(getValue());
					}
					if (backup != null) {
						for (Unity u : Unity.values()) {
							int n = getNumberOfUnity(this.getCell(), u);
							for (int i = 0; i < grid.getSize(); ++i) {
								Cell ci = grid.getCellAt(getX(u, n, i),
										getY(u, n, i));
								if (!ci.isBlocked() && ci.getCandidates() != null
										&& grid.getPossibleCandidatesFrom(ci.getCoordinate().getX(), ci.getCoordinate().getY()).contains(getValue())) {
									ci.addCandidate(getValue());
								}
							}
						}
					}
				} else {
					if (grid != null) {
						for (Unity u : Unity.values()) {
							int n = getNumberOfUnity(this.getCell(), u);
							for (int i = 0; i < grid.getSize(); ++i) {
								Cell ci = grid.getCellAt(getX(u, n, i),
										getY(u, n, i));
								if (!ci.isBlocked() && ci.getCandidates() != null 
										&& backup != null && !ci.getCandidates().contains(backup)) {
									removeCandidateCells.add(ci);
								}
							}
						}
					}
					
					for (Cell c : removeCandidateCells) {
						if (grid.getPossibleCandidatesFrom(c.getCoordinate().getX(), c.getCoordinate().getY()).contains(backup)) {
							c.addCandidate(backup);
						}
					}
					int x = getCell().getCoordinate().getX();
					int y = getCell().getCoordinate().getY();
					grid.removeAllCandidate(x, y);
					for( String cand : grid.getPossibleCandidatesFrom(x, y)) {
						getCell().addCandidate(cand);
					}
				}
			}
    }
    
    @Override
    protected void undoIt() {
			getCell().setValue(backup);
			if (advanced) {
				if (getValue() != null) {
					for (Unity u : Unity.values()) {
						int n = getNumberOfUnity(this.getCell(), u);
						for (int i = 0; i < grid.getSize(); ++i) {
							Cell ci = grid.getCellAt(getX(u, n, i),
									getY(u, n, i));
							if (!ci.isBlocked() && ci.getCandidates() != null
									&& grid.getPossibleCandidatesFrom(ci.getCoordinate().getX(), ci.getCoordinate().getY()).contains(getValue())) {
								ci.addCandidate(getValue());
							}
						}
					}
					int x = getCell().getCoordinate().getX();
					int y = getCell().getCoordinate().getY();
					if (grid.getCellAt(x, y) != null) {
						grid.removeAllCandidate(x, y);
						for( String cand : grid.getPossibleCandidatesFrom(x, y)) {
							getCell().addCandidate(cand);
						}
					}
				} else {
					for (Cell c : removeCandidateCells) {
						c.eliminateCandidate(backup);
					}
				}
			}
    }
    
    // OUTILS
    
	/**
	 * Permet de r�cup�rer la coordonn�e x d'une case en fonction de l'unit� u,
	 *  le num�ro de l'unit� n et le num�ro de la case dans l'unit�.
	 */
	private int getX(Unity u, int                                                                                                         n, int nb) {
		int size = grid.getSizeSquare();
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
		int size = grid.getSizeSquare();
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
	 * Permet de connaitre le num�ro de l'unit� en fonction de son instance
	 * et d'une cellule de celle-ci
	 */
	private int getNumberOfUnity(Cell c, Unity unit) {
		int size = grid.getSizeSquare();
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

}
