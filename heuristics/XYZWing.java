package heuristics;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cmd.EliminateCandidate;
import model.BoundedCoordinate;
import model.Cell;
import model.Grid;

public class XYZWing extends AHeuristic implements IHeuristic {

	private Map<String, Integer> mapStrings;
	private Map<Integer, String> mapValues;
	
	public XYZWing(Grid grid) {
		super(IHeuristic.LevelHeuristics.XYZWing, grid);
		mapStrings = new HashMap<String, Integer>();
		mapValues = new HashMap<Integer, String>();
		setMapStrings(grid.getValues());
		setMapValues(grid.getValues());
	}

	@Override
	public Solution getSolution() {
		
		Grid grid = getGrid();
		int size = getGrid().getSize();
		int sizeSquare = getGrid().getSizeSquare();
		
		for (Cell cell : grid.getCells()) {

			
			// Pour toutes les cellules contenant uniquement 3 candidats
			if (cell.getCandidates() != null && cell.getCandidates().size() == 3) {
				BoundedCoordinate bc = cell.getCoordinate();
				Map<Cell, String> mapFirst = new HashMap<Cell, String>();
				Map<Cell, String> mapSecond = new HashMap<Cell, String>();
				
				
				boolean contain = false;
				
				// On vérifie sur la ligne si elle contient des cases à 2 candidats
				for (int k = 0; k < size; ++k) {
					if (k != bc.getX()) {
						Cell firstCell = grid.getCellAt(bc.getX(), k);
						
						// Pour chaque cellule sur la même ligne contenant uniquement
						//	2 candidats
						if (firstCell.getCandidates() != null &&
								firstCell.getCandidates().size() == 2) {
							
							// On stocke la cellule avec la valeur différente
							String value = getDifferenceOnlyOne(firstCell.getCandidates(), cell.getCandidates());
							if (value != null) {
								contain = true;
								mapFirst.put(firstCell, value);
							}
						}
					}
				}
				
				// Si la ligne contient au moins une case à 2 candidats
				if (contain) {
					int startX = bc.getX() - bc.getX() % sizeSquare;
					int startY = bc.getY() - bc.getY() % sizeSquare;
					for (int i = 0; i < sizeSquare ; ++i) {
						for (int j = 0; j < sizeSquare; ++j) {
							
							
							if ((i + startX) != bc.getX() && (j + startY) != bc.getY()) {
								Cell secondCell = grid.getCellAt(i + startX, j + startY);
								
								// Pour chaque cellule dans la même région que cell ayant 
								//	uniquement 2 candidats
								if (secondCell.getCandidates() != null &&
										secondCell.getCandidates().size() == 2) {
									
									// On stocke la cellule avec la valeur différente
									String value = getDifferenceOnlyOne(secondCell.getCandidates(), cell.getCandidates());
									if (value != null) {
										contain = true;
										mapSecond.put(secondCell, value);
									}
								}
							}
						}
					}
				}
				
				// Si la ligne ET la région contiennent au moins une case à 2 candidats
				if (contain) {
					for (Cell cellFirst : mapFirst.keySet()) {
						String valueFirst = mapFirst.get(cellFirst);
						
						for (Cell cellSecond : mapSecond.keySet()) {
							String valueSecond = mapSecond.get(cellSecond);
							
							// Si la valeur différentes sont différentes
							if (!valueFirst.equals(valueSecond)) {
								String valueToRemove = getFirstNotContain(cell.getCandidates(), valueFirst, valueSecond);
								List<Cell> removes = new ArrayList<Cell>();
								boolean fromLine = false;
								
								// Récupère les infos sur si on doit éliminer sur la ligne ou la colonne
								if (fromSameRegion(cell, cellFirst)) {
									if (fromSameLine(cell, cellSecond)) {
										fromLine = true;
									}
								} else {
									if (fromSameLine(cell, cellFirst)) {
										fromLine = true;
									}
								}
								
								// Récupère les cellules à retirer le candidat
								if (fromLine) {
									for (int k = 0; k < sizeSquare; ++k) {
										if ((bc.getY() - bc.getY() % sizeSquare) + k != bc.getY()) {
											Cell cellRemove = grid.getCellAt(
													bc.getX(),
													(bc.getY() - bc.getY() % sizeSquare) + k);
											if (cellRemove.getCandidates() != null &&
													cellRemove.getCandidates().contains(valueToRemove)) {
												removes.add(cellRemove);
											}
										}
									}
								} else {
									for (int k = 0; k < sizeSquare; ++k) {
										if ((bc.getX() - bc.getX() % sizeSquare) + k != bc.getX()) {
											Cell cellRemove = grid.getCellAt(
													(bc.getX() - bc.getX() % sizeSquare) + k,
													bc.getY());
											if (cellRemove.getCandidates() != null &&
													cellRemove.getCandidates().contains(valueToRemove)) {
												removes.add(cellRemove);
											}
										}
									}
								}
								
								// Si il y a des cellules avec le candidat à retirer
								if (removes.size() > 0) {
									Solution sol = new StdSolution();
									setSolution((StdSolution) sol, cell, cellFirst, cellSecond, removes, valueToRemove, fromLine);
									return sol;
								}
								
							}
						}
					}
				}
				
				mapFirst.clear();
				mapSecond.clear();
				contain = false;
				
				// On vérifie sur la ligne si elle contient des cases à 2 candidats
				for (int k = 0; k < size; ++k) {
					if (k != bc.getX()) {
						Cell firstCell = grid.getCellAt(k, bc.getY());
						
						// Pour chaque cellule sur la même ligne contenant uniquement
						//	2 candidats
						if (firstCell.getCandidates() != null &&
								firstCell.getCandidates().size() == 2) {
							
							// On stocke la cellule avec la valeur différente
							String value = getDifferenceOnlyOne(firstCell.getCandidates(), cell.getCandidates());
							if (value != null) {
								contain = true;
								mapFirst.put(firstCell, value);
							}
						}
					}
				}
				
				// Si la ligne contient au moins une case à 2 candidats
				if (contain) {
					int startX = bc.getX() - bc.getX() % sizeSquare;
					int startY = bc.getY() - bc.getY() % sizeSquare;
					for (int i = 0; i < sizeSquare ; ++i) {
						for (int j = 0; j < sizeSquare; ++j) {
							
							
							if ((i + startX) != bc.getX() && (j + startY) != bc.getY()) {
								Cell secondCell = grid.getCellAt(i + startX, j + startY);
								
								// Pour chaque cellule dans la même région que cell ayant 
								//	uniquement 2 candidats
								if (secondCell.getCandidates() != null &&
										secondCell.getCandidates().size() == 2) {
									
									// On stocke la cellule avec la valeur différente
									String value = getDifferenceOnlyOne(secondCell.getCandidates(), cell.getCandidates());
									if (value != null) {
										contain = true;
										mapSecond.put(secondCell, value);
									}
								}
							}
						}
					}
				}
				
				// Si la ligne ET la région contiennent au moins une case à 2 candidats
				if (contain) {
					for (Cell cellFirst : mapFirst.keySet()) {
						String valueFirst = mapFirst.get(cellFirst);
						
						for (Cell cellSecond : mapSecond.keySet()) {
							String valueSecond = mapSecond.get(cellSecond);
							
							// Si la valeur différentes sont différentes
							if (!valueFirst.equals(valueSecond)) {
								String valueToRemove = getFirstNotContain(cell.getCandidates(), valueFirst, valueSecond);
								List<Cell> removes = new ArrayList<Cell>();
								boolean fromLine = false;
								
								// Récupère les infos sur si on doit éliminer sur la ligne ou la colonne
								if (fromSameRegion(cell, cellFirst)) {
									if (fromSameLine(cell, cellSecond)) {
										fromLine = true;
									}
								} else {
									if (fromSameLine(cell, cellFirst)) {
										fromLine = true;
									}
								}
								
								// Récupère les cellules à retirer le candidat
								if (fromLine) {
									for (int k = 0; k < sizeSquare; ++k) {
										if ((bc.getY() - bc.getY() % sizeSquare) + k != bc.getY()) {
											Cell cellRemove = grid.getCellAt(
													bc.getX(),
													(bc.getY() - bc.getY() % sizeSquare) + k);
											if (cellRemove.getCandidates() != null &&
													cellRemove.getCandidates().contains(valueToRemove)) {
												removes.add(cellRemove);
											}
										}
									}
								} else {
									for (int k = 0; k < sizeSquare; ++k) {
										if ((bc.getX() - bc.getX() % sizeSquare) + k != bc.getX()) {
											Cell cellRemove = grid.getCellAt(
													(bc.getX() - bc.getX() % sizeSquare) + k,
													bc.getY());
											if (cellRemove.getCandidates() != null &&
													cellRemove.getCandidates().contains(valueToRemove)) {
												removes.add(cellRemove);
											}
										}
									}
								}
								
								// Si il y a des cellules avec le candidat à retirer
								if (removes.size() > 0) {
									Solution sol = new StdSolution();
									setSolution((StdSolution) sol, cell, cellFirst, cellSecond, removes, valueToRemove, fromLine);
									return sol;
								}
								
							}
						}
					}
				}
				
				
			}
			
			
		}
		
		
		
		/*
		Grid grid = getGrid();
		int size = getGrid().getSize();
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				Cell cell = grid.getCellAt(i, j);
				if (cell.getValue() == null && cell.getCandidates().size() == 3) {
					List<Cell> listCellSquare = squareFromContaining(cell, 2);
					Cell cellColumn = null;
					Cell cellLine = null;
					for (int k = 0; k < size; ++k) {
						cellColumn = grid.getCellAt(k, j);
						cellLine = grid.getCellAt(i, k);
						if (k >= (i + grid.getSizeSquare() - i % 3) && cellColumn.getValue() == null && cellColumn.getCandidates().size() == 2) {
							for (Cell cellSquare : listCellSquare) {
								String value = candidatesCorresponding(cell, cellColumn, cellSquare, false);
								if (value != null) {
									StdSolution solution = new StdSolution();
									setSolution(solution, cell, cellColumn, cellSquare, false, value);
									if (solution.getActions().size() > 0) {
										return solution;
									}
								}
							}
						}
						if (k >= (j + grid.getSizeSquare() - j % 3) && cellLine.getValue() == null && cellLine.getCandidates().size() == 2) {
							for (Cell cellSquare : listCellSquare) {
								String value = candidatesCorresponding(cell, cellLine, cellSquare, true);
								if (value != null) {
									StdSolution solution = new StdSolution();
									setSolution(solution, cell, cellLine, cellSquare, true, value);
									if (solution.getActions().size() > 0) {
										return solution;
									}
								}
							}
						}
					}
				}
			}
		}*/
		return null;
	}
	
	
	
	private void setSolution(StdSolution solution, Cell beginning, Cell first, Cell second, List<Cell> removes,
		String valueToRemove, boolean fromLine) {
		solution.addReason(Color.BLUE, beginning);
		solution.addReason(Color.BLUE, first);
		solution.addReason(Color.BLUE, second);
		try {
			for (Cell cell : removes) {
				solution.addReason(Color.ORANGE, cell);
				solution.addAction(new EliminateCandidate(cell, valueToRemove));
			}
		} catch (SolutionInitializeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuilder sb = getDescription(beginning, first, second, removes, valueToRemove, fromLine);
		solution.setDescription(sb.toString());
		
	}

	private StringBuilder getDescription(Cell beginning, Cell first, Cell second, List<Cell> removes, String valueToRemove, boolean fromLine) {
		String v1 = null;
		String v2 = null;
		String v3 = null;
		for (String v : beginning.getCandidates()) {
			if (v1 == null) {
				v1 = v;
			} else if (v2 == null) {
				v2 = v;
			} else {
				v3 = v;
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append("\"" + this.getLevelHeuristics().getName() + "\":\n");
		sb.append("Quel que puisse être le choix dans la case de départ de coordonnée " + beginning.getCoordinate());
		sb.append(", on est certain de ne pas retrouver le candidat ");
		sb.append(valueToRemove + " dans les case finale (en Orange).\n");
		sb.append("Si on choisit " + v1 + ", c'est " + valueToRemove + " qui va prendre place ");
		if (getCommunValue(beginning, first, valueToRemove) == v1) {
			if (fromLine) {
				sb.append("sur la colonne.");
			} else {
				sb.append("sur la ligne.");
			}
		} else {
			sb.append("dans celui dans le même carré.");
		}
		sb.append(" Et si on choisit le " + v2 + ", le " + valueToRemove + " prendra la place ");
		if (getCommunValue(beginning, second, valueToRemove) == v2) {
			sb.append("dans celui dans le même carré.");
		} else {
			if (fromLine) {
				sb.append("sur la colonne.");
			} else {
				sb.append("sur la ligne.");
			}
		}
		sb.append(" Dans les deux cas, les cases en magenta ne peuvent pas être " + valueToRemove + ".");
		sb.append("On peut donc supprimer " + valueToRemove + " des candidats pour ces cases.");
		return sb;
	}

	private String getFirstNotContain(Set<String> list, String a, String b) {
		for (String s : list) {
			if (s != a && s != b) {
				return s;
			}
		}
		return null;
	}
	
	private boolean fromSameRegion(Cell first, Cell second) {
		BoundedCoordinate bc1 = first.getCoordinate();
		BoundedCoordinate bc2 = second.getCoordinate();
		int sizeSquare = getGrid().getSizeSquare();
		int startX1 = bc1.getX() - bc1.getX() % sizeSquare;
		int startY1 = bc1.getY() - bc1.getY() % sizeSquare;
		int startX2 = bc2.getX() - bc2.getX() % sizeSquare;
		int startY2 = bc2.getY() - bc2.getY() % sizeSquare;
		return (startX1 == startX2 && startY1 == startY2);
	}
	
	private boolean fromSameLine(Cell first, Cell second) {
		BoundedCoordinate bc1 = first.getCoordinate();
		BoundedCoordinate bc2 = second.getCoordinate();
		return (bc1.getX() == bc2.getX());
	}
	
	private boolean fromColumnLine(Cell first, Cell second) {
		BoundedCoordinate bc1 = first.getCoordinate();
		BoundedCoordinate bc2 = second.getCoordinate();
		return (bc1.getY() == bc2.getY());
	}
	
	private String getDifferenceOnlyOne(Set<String> a, Set<String> b) {
		String value = null;
		for (String currentValue : b) {
			if (value == null) {
				value = (!a.contains(currentValue) ? currentValue : null);
			} else {
				if (!a.contains(currentValue)) {
					return null;
				}
			}
		}
		return value;
	}
	
	private List<Cell> squareFromContaining(Cell c, int numberElement) {
		List<Cell> list = new ArrayList<Cell>();
		int xCell = c.getCoordinate().getX();
		int yCell = c.getCoordinate().getY();
		for (int i = xCell - xCell % 3; i < xCell - xCell % getGrid().getSizeSquare() + getGrid().getSizeSquare(); ++i) {
			for (int j = yCell - yCell % 3; j < yCell - yCell % getGrid().getSizeSquare() + getGrid().getSizeSquare(); ++j) {
				if (i != xCell || j != yCell) {
					if (getGrid().getCellAt(i, j).getCandidates().size() == numberElement) {
						list.add(getGrid().getCellAt(i, j));
					}
				}
			}
		}
		return list;
	}
	
	private String candidatesCorresponding(Cell cell, Cell cellCL, Cell cellSquare, boolean fromLine) {
		if (cell.getCandidates().size() != 3 || cellCL.getCandidates().size() != 2 || 
				cellSquare.getCandidates().size() != 2) {
			return null;
		}
		String valueCL = null;
		boolean isGood = false;
		for (String candidate : cell.getCandidates()) {
			if (!cellCL.getCandidates().contains(candidate)) {
				if (isGood) {
					return null;
				}
				isGood = true;
				valueCL = candidate;
			}
		}
		String valueSquare = null;
		isGood = false;
		for (String candidate : cell.getCandidates()) {
			if (!cellSquare.getCandidates().contains(candidate)) {
				if (isGood) {
					return null;
				}
				isGood = true;
				valueSquare = candidate;
			}
		}
		System.out.println("values : " + valueCL + ";" + valueSquare);
		if (valueCL == valueSquare) {
			return null;
		}
		String correctValue = null;
		for (String value : cell.getCandidates()) {
			if (value != valueSquare && value != valueCL) {
				correctValue = value;
			}
		}
		if (correctValue == null) {
			return null;
		}
		BoundedCoordinate bc = cell.getCoordinate();
		int x = bc.getX() - (bc.getX() % 3);
		int y = bc.getY() - (bc.getY() % 3);
		for (int i = 0; i < getGrid().getSize(); ++i) {
			if (fromLine) {
				if ((x + i) != bc.getX()) {
					if (getGrid().getCellAt(x, bc.getY()).getCandidates().contains(correctValue)) {
						return correctValue;
					}
				}
			} else {
				if ((y + i) != bc.getY()) {
					if (getGrid().getCellAt(bc.getX(), y).getCandidates().contains(correctValue)) {
						return correctValue;
					}
				}
			}
		}
		return null;
	}
	
	private void setSecondSolution(StdSolution solution, Cell cell, Cell cellCL, Cell cellSquare, boolean fromLine, String value) {
		try {
			solution.addReason(Color.BLUE, cell);
			solution.addReason(Color.BLUE, cellCL);
			solution.addReason(Color.BLUE, cellSquare);
			BoundedCoordinate bc = cell.getCoordinate();
			int x = bc.getX() - (bc.getX() % 3);
			int y = bc.getY() - (bc.getY() % 3);
			for (int i = 0; i < getGrid().getSizeSquare(); ++i) {
				if (fromLine) {
					if ((x + i) != bc.getX()) {
						if (getGrid().getCellAt((x + i), bc.getY()).getCandidates().contains(value)) {
							solution.addReason(Color.ORANGE, getGrid().getCellAt((x + i), bc.getY()));
							solution.addAction(new EliminateCandidate(getGrid().getCellAt((x + i), bc.getY()), value));
						}
					}
				} else {
					if ((y + i) != bc.getY()) {
						if (getGrid().getCellAt(bc.getX(), (y + i)).getCandidates().contains(value)) {
							solution.addReason(Color.ORANGE, getGrid().getCellAt(bc.getX(), (y + i)));
							solution.addAction(new EliminateCandidate(getGrid().getCellAt(bc.getX(), (y + i)), value));
						}
					}
				}
			}
		} catch (SolutionInitializeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuilder description = getSecondDescription(cell, cellCL, cellSquare, value, fromLine);
		solution.setDescription(description.toString());
	}
	
	private StringBuilder getSecondDescription(Cell cell, Cell cellCL, Cell cellSquare, String value, boolean fromLine) {
		StringBuilder sb = new StringBuilder();
		sb.append("\"" + this.getLevelHeuristics().getName() + "\":\n");
		sb.append("Quel que puisse être le choix dans la case de départ de coordonnée " + cell.getCoordinate());
		String v1 = null;
		String v2 = null;
		String v3 = null;
		for (String v : cell.getCandidates()) {
			if (v1 == null) {
				v1 = v;
			} else if (v2 == null) {
				v2 = v;
			} else {
				v3 = v;
			}
		}
		sb.append(" (" + v1 + ", " + v2 + ", ou " + v3 + ")");
		sb.append(", on est certain de ne pas retrouver le candidat ");
		sb.append(value + " dans les case finale (en Magenta).\n");
		sb.append("Si on choisit " + v1 + ", c'est " + value + " qui va prendre place ");
		if (getCommunValue(cell, cellCL, value) == v1) {
			if (fromLine) {
				sb.append(cell.getCoordinate().getX() > cellCL.getCoordinate().getX() ? "en haut." : "en bas.");
			} else {
				sb.append(cell.getCoordinate().getY() > cellCL.getCoordinate().getY() ? "à gauche." : "à droite.");
			}
		} else {
			sb.append("dans celui dans le même carré.");
		}
		sb.append(" Et si on choisit le " + v2 + ", le " + value + " prendra la place ");
		if (getCommunValue(cell, cellSquare, value) == v2) {
			sb.append("dans celui dans le même carré.");
		} else {
			if (fromLine) {
				sb.append(cell.getCoordinate().getX() > cellCL.getCoordinate().getX() ? "en haut." : "en bas.");
			} else {
				sb.append(cell.getCoordinate().getY() > cellCL.getCoordinate().getY() ? "à gauche." : "à droite.");
			}
		}
		sb.append("Dans les deux cas, les cases en magenta ne peuvent pas être " + value + ".");
		sb.append("On peut donc supprimer " + value + " des candidats pour ces cases.");
		return sb;
	}
	
	private String getCommunValue(Cell cell1, Cell cell2, String notThisValue) {
		String value = null;
		boolean isGood = false;
		for (String candidate : cell1.getCandidates()) {
			if (candidate != notThisValue && cell2.getCandidates().contains(candidate)) {
				if (isGood) {
					return null;
				}
				isGood = true;
				value = candidate;
			}
		}
		return value;
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
