package heuristics;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cmd.EliminateCandidate;
import model.BoundedCoordinate;
import model.Cell;
import model.Grid;

public class TwinsAndTriplet extends AHeuristic implements IHeuristic {

	
	public TwinsAndTriplet(Grid grid) {
		super(IHeuristic.LevelHeuristics.TwinsAndTriplet, grid);
	}

	@Override
	public Solution getSolution() {
		
		Grid grid = getGrid();
		int size = getGrid().getSizeSquare();
		
		// Pour tout les candidats
		for (String c : grid.getValues()) {
			
			// Pour tout les triplet(al, be, ga) de région alignées en ligne
			for (int k = 0; k < size; ++k) {
				int[] regions = new int[size];
				for (int j = 0; j < size; ++j) {
					regions[j] = k * size + j;
				}
				
				// Pour chaque région de la ligne
				for (int numRegion : regions) {
					
					// Décalage lié à la région
					int shiftX = (numRegion % size) * size;
					int shiftY = (numRegion / size) * size;
					List<Cell> cellFromRegion = new ArrayList<Cell>();
					
					// Stocke si ligne de la région contiens la valeur
					for (int i = 0; i < size; ++i) {
						for (int j = 0; j < size; ++j) {
							Cell cell = grid.getCellAt(i + shiftX, j + shiftY);
							if (cell.getCandidates() != null &&
									cell.getCandidates().contains(c)) {
								cellFromRegion.add(cell);
							}
						}
					}

					// Vérification si la région est correct (renvoie -1 si faux);
					int numLine = regionLineIsCorrect(cellFromRegion, size);
					// Si la région est correct (le candidat sur une seule ligne de la région
					if (numLine != -1) {
						List<Cell> cellCorrectY = new ArrayList<Cell>();
						
						// On regarde sur les autres régions sur la même ligne si des
						//	des cellules contiennent la valeur en candidat également.
						//	Si c'est le cas, on les met dans cellCorrectY
						for (int j = 0; j < size * size; ++j) {
							Cell cell = grid.getCellAt(j, numLine);
							if (!cellFromRegion.contains(cell) && cell.getCandidates() != null && cell.getCandidates().contains(c)) {
								cellCorrectY.add(cell);
							}
						}
						
						if (cellCorrectY.size() > 0) {
							Solution sol = new StdSolution();
							SetSolution((StdSolution) sol, cellFromRegion, cellCorrectY, c, size, true);
							return sol;
						}
					}
				}
			}
			
			// Pour tout les triplet(al, be, ga) de région alignées en colonne
			for (int k = 0; k < size; ++k) {
				int[] regions = new int[size];
				for (int j = 0; j < size; ++j) {
					regions[j] = j * size + k;
				}
				
				// Pour chaque région de la ligne
				for (int numRegion : regions) {
					
					// Décalage lié à la région
					int shiftX = (numRegion % size) * size;
					int shiftY = (numRegion / size) * size;
					List<Cell> cellFromRegion = new ArrayList<Cell>();
					
					// Stocke si ligne de la région contiens la valeur
					for (int i = 0; i < size; ++i) {
						for (int j = 0; j < size; ++j) {
							Cell cell = grid.getCellAt(j + shiftX, i + shiftY);
							if (cell.getCandidates() != null &&
									cell.getCandidates().contains(c)) {
								cellFromRegion.add(cell);
							}
						}
					}

					// Vérification si la région est correct (renvoie -1 si faux);
					int numColumn = regionColumnIsCorrect(cellFromRegion, size);
					// Si la région est correct (le candidat sur une seule ligne de la région
					if (numColumn != -1) {
						List<Cell> cellCorrectX = new ArrayList<Cell>();
						
						// On regarde sur les autres régions sur la même ligne si des
						//	des cellules contiennent la valeur en candidat également.
						//	Si c'est le cas, on les met dans cellCorrectY
						for (int j = 0; j < size * size; ++j) {
							Cell cell = grid.getCellAt(numColumn, j);
							if (!cellFromRegion.contains(cell) && cell.getCandidates() != null && cell.getCandidates().contains(c)) {
								cellCorrectX.add(cell);
							}
						}
						
						if (cellCorrectX.size() > 0) {
							Solution sol = new StdSolution();
							SetSolution((StdSolution) sol, cellFromRegion, cellCorrectX, c, size, false);
							return sol;
						}
					}
				}
			}
		}
		return null;
	}
	
	
	private void SetSolution(StdSolution solution, List<Cell> cellsReason, List<Cell> cellsToRemove, String value, int size, boolean fromLine) {
		for (Cell cell : cellsReason) {
			solution.addReason(Color.BLUE, cell);
		}
		for (Cell cell : cellsToRemove) {
			solution.addReason(Color.ORANGE, cell);
			try {
				solution.addAction(new EliminateCandidate(cell, value));
			} catch (SolutionInitializeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        StringBuilder description = getDescription(cellsReason, value, size, fromLine);
        solution.setDescription(description.toString());
	}
	
	
	private StringBuilder getDescription(List<Cell> cells, String value, int size, boolean fromLine) {
		BoundedCoordinate tmp = cells.get(0).getCoordinate();
		int num = (fromLine ?
				tmp.getY() :
				tmp.getX());
		int numRegion = (tmp.getX() / size) + (tmp.getY() / size) + 1;
		StringBuilder sb = new StringBuilder();
		sb.append("\"" + this.getLevelHeuristics().getName() + "\":\n");
		sb.append("Le candidat '" + value + "'");
		sb.append(" n'étant présent uniquement sur la ");
		sb.append((fromLine ? "ligne " : "colonne"));
		sb.append(" numéro " + (num + 1));
		sb.append(" dans la région numéro " + numRegion);
		sb.append(", alors on peut retirer tout autres mêmes candidats sur la m½me ");
		sb.append((fromLine ? "ligne" : "colonne"));
		return sb;
	}
	
	
	private int regionColumnIsCorrect(List<Cell> cells, int size) {
		int numLine = -1;
		for (Cell cell : cells) {
			if (numLine == -1) {
				numLine = cell.getCoordinate().getX();
			} else {
				if (cell.getCoordinate().getX() != numLine) {
					return -1;
				}
			}
		}
		return (cells != null && cells.size() > 1 ? numLine : -1);
	}
	
	
	private int regionLineIsCorrect(List<Cell> cells, int size) {
		int numLine = -1;
		for (Cell cell : cells) {
			if (numLine == -1) {
				numLine = cell.getCoordinate().getY();
			} else {
				if (cell.getCoordinate().getY() != numLine) {
					return -1;
				}
			}
		}
		return numLine;
	}
}
