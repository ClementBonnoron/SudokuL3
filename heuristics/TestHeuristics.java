package heuristics;

import java.beans.PropertyVetoException;

import model.*;

public class TestHeuristics {
	
	private final static Grid grid = new StdGrid(StdGrid.defaultValueSet());	
	
	private final static AHeuristic[] HEURISTICS = {/*
			new UniqueCandidate(grid),
			new OneCandidate(grid),
			new TwinsAndTriplet(grid),*/
			new XWing(grid),/*
			new XYWing(grid),
			new XYZWing(grid)*/
	};
	
	private static final int NUMCA_UNIQUECANDIDATE = 5;
	private static final int NUMCO_UNIQUECANDIDATE = 3;
	private static final int NUMLI_UNIQUECANDIDATE = 3;
	private static final int NUMCA_ONECANDIDATE = 3;
	private static final int NUMLI_ONECANDIDATE = 1;
	private static final int NUMCO_ONECANDIDATE = 2;
	private static final int STARTX_TAT = 3;
	private static final int STARTY_TAT = 6;
	private static final int ENDX_TAT = 6;
	private static final int ENDY_TAT = 9;
	private static final int COORD1_X_TAT = 4;
	private static final int COORD2_X_TAT = 5;
	private static final int COORD1_Y_TAT = 8;
	private static final int COORD2_Y_TAT = 8;
	private static final int NUMCA_TAT = 4;
	private static final int NUMCA_XW = 2;
	// PARTIE 1 XWING
	
	private static final int COORD1_X_XW = 2;
	private static final int COORD2_X_XW = 4;
	private static final int COORD1_Y_XW = 0;
	private static final int COORD2_Y_XW = 4;
	// PARTIE 2 XWING
	/*
	private static final int COORD1_X_XW = 3;
	private static final int COORD2_X_XW = 5;
	private static final int COORD1_Y_XW = 0;
	private static final int COORD2_Y_XW = 1;
	private static final int COORD3_Y_XW = 6;
	private static final int COORD4_Y_XW = 8;
	*/

	private static final int COORD_X_XY = 3;
	private static final int COORD_Y_XY = 4;
	private static final int COORDL_Y_XY = 8;
	private static final int COORDC_X_XY = 7;
	private static final int VALUE1_XY = 1;
	private static final int VALUE2_XY = 3;
	private static final int VALUER_XY = 7;

	private static final int COORD_X_XYZ = 3;
	private static final int COORD_Y_XYZ = 4;
	private static final int COORDL_Y_XYZ = 7;
	private static final int COORDS_X_XYZ = 5;
	private static final int COORDS_Y_XYZ = 3;
	private static final int VALUE1_XYZ = 1;
	private static final int VALUE2_XYZ = 3;
	private static final int VALUER_XYZ = 7;
	
	public static void main(String[] args) throws PropertyVetoException {
		for (AHeuristic h : HEURISTICS) {
			actFromHeuristic(h);
			h.testTimeSolution(1, true);
			System.out.println("\n");
			for (String s : grid.getValues()) {
				displayCandidateIsPresent(s);
			}
			actReverseFromHeuristic(h);
		}
	}
	
	public static void actFromHeuristic(AHeuristic a) throws PropertyVetoException {
		switch (a.getLevelHeuristics()) {
			case OneCandidate:
				Cell cell = grid.getCellAt(NUMLI_ONECANDIDATE, NUMCO_ONECANDIDATE);
				for (Object s : cell.getCandidates().toArray()) {
					if (s.toString().compareTo(Integer.toString(NUMCA_ONECANDIDATE)) != 0) {
						if (cell.getCandidates().contains(s.toString())) {
							cell.eliminateCandidate(s.toString());;
						}
					}
				}
				break;
			case UniqueCandidate:
				for (int i = 0; i < grid.getSize(); ++i) {
					if (i != NUMLI_UNIQUECANDIDATE) {
						if (grid.getCellAt(i, NUMCO_UNIQUECANDIDATE).getCandidates().contains(Integer.toString(NUMCA_UNIQUECANDIDATE))) {
							grid.getCellAt(i, NUMCO_UNIQUECANDIDATE).eliminateCandidate(
									Integer.toString(NUMCA_UNIQUECANDIDATE));
						}
					}
				}
				break;
			case TwinsAndTriplet:
				for (int i = STARTX_TAT; i < ENDX_TAT; ++i) {
					for (int j = STARTY_TAT; j < ENDY_TAT; ++j) {
						if (!((i == COORD1_X_TAT && j == COORD1_Y_TAT) || (i == COORD2_X_TAT && j == COORD2_Y_TAT))) {
							if (grid.getCellAt(i, j).getCandidates().contains(Integer.toString(NUMCA_TAT))) {
								grid.getCellAt(i, j).eliminateCandidate(Integer.toString(NUMCA_TAT));
							}
						}
					}
				}
				break;
			case XWing:
				// PARTIE 1 XWING
				
				for (int i = 0; i < grid.getSize(); ++i) {
					if (i != COORD1_X_XW && i != COORD2_X_XW) {
						if (grid.getCellAt(i, COORD1_Y_XW).getCandidates().contains(Integer.toString(NUMCA_XW))) {
							grid.getCellAt(i, COORD1_Y_XW).eliminateCandidate(Integer.toString(NUMCA_XW));
						}
						if (grid.getCellAt(i, COORD2_Y_XW).getCandidates().contains(Integer.toString(NUMCA_XW))) {
							grid.getCellAt(i, COORD2_Y_XW).eliminateCandidate(Integer.toString(NUMCA_XW));
						}
					}
				}
				// PARTIE 2 XWING
				/*
				for (int i = 0; i < grid.getSizeSquare(); ++i) {
					for (int j = 0; j < grid.getSizeSquare(); ++j) {
						if (!(((i + 3) == COORD1_X_XW && j == COORD2_Y_XW) ||
								((i + 3) == COORD2_X_XW && j == COORD1_Y_XW))) {
							if (grid.getCellAt(i + 3, j).getCandidates().contains(Integer.toString(NUMCA_XW))) {
								grid.getCellAt(i + 3, j).eliminateCandidate(Integer.toString(NUMCA_XW));
							}
						}
						if (!(((i + 3) == COORD1_X_XW && (j + 6) == COORD3_Y_XW) ||
								((i + 3) == COORD2_X_XW && (j + 6) == COORD4_Y_XW))) {
							if (grid.getCellAt(i + 3, j + 6).getCandidates().contains(Integer.toString(NUMCA_XW))) {
								grid.getCellAt(i + 3, j + 6).eliminateCandidate(Integer.toString(NUMCA_XW));
							}
						}
					}
				}*/
				break;
			case XYWing:
				for (String value : StdGrid.defaultValueSet()) {
					if (!(value.equals(Integer.toString(VALUE1_XY)) || value.equals(Integer.toString(VALUE2_XY))) &&
							grid.getCellAt(COORD_X_XY, COORD_Y_XY).getCandidates().contains(value)) {
						grid.getCellAt(COORD_X_XY, COORD_Y_XY).eliminateCandidate(value);
					}
					if (!(value.equals(Integer.toString(VALUE1_XY)) || value.equals(Integer.toString(VALUER_XY))) &&
							grid.getCellAt(COORDC_X_XY, COORD_Y_XY).getCandidates().contains(value)) {
						grid.getCellAt(COORDC_X_XY, COORD_Y_XY).eliminateCandidate(value);
					}
					if (!(value.equals(Integer.toString(VALUER_XY)) || value.equals(Integer.toString(VALUE2_XY))) &&
							grid.getCellAt(COORD_X_XY, COORDL_Y_XY).getCandidates().contains(value)) {
						grid.getCellAt(COORD_X_XY, COORDL_Y_XY).eliminateCandidate(value);
					}
				}
				break;
			case XYZWing:
				for (String value : StdGrid.defaultValueSet()) {
					if (!(value.equals(Integer.toString(VALUE1_XYZ)) || value.equals(Integer.toString(VALUE2_XYZ)) || value.equals(Integer.toString(VALUER_XYZ))) &&
							grid.getCellAt(COORD_X_XYZ, COORD_Y_XYZ).getCandidates().contains(value)) {
						grid.getCellAt(COORD_X_XYZ, COORD_Y_XYZ).eliminateCandidate(value);
					}
					if (!(value.equals(Integer.toString(VALUE2_XYZ)) || value.equals(Integer.toString(VALUER_XYZ))) &&
							grid.getCellAt(COORDS_Y_XYZ, COORDS_X_XYZ).getCandidates().contains(value)) {
						grid.getCellAt(COORDS_Y_XYZ, COORDS_X_XYZ).eliminateCandidate(value);
					}
					if (!(value.equals(Integer.toString(VALUE1_XYZ)) || value.equals(Integer.toString(VALUER_XYZ))) &&
							grid.getCellAt(COORD_X_XYZ, COORDL_Y_XYZ).getCandidates().contains(value)) {
						grid.getCellAt(COORD_X_XYZ, COORDL_Y_XYZ).eliminateCandidate(value);
					}
				}
				System.out.println("1 \t" + grid.getCellAt(COORD_X_XYZ, COORD_Y_XYZ));
				System.out.println("1 \t" + grid.getCellAt(COORDS_X_XYZ, COORDS_Y_XYZ));
				System.out.println("1 \t" + grid.getCellAt(COORD_X_XYZ, COORDL_Y_XYZ));
				break;
			default:
				throw new AssertionError("Create something for " + a.getLevelHeuristics().getName());
		}
	}
	
	@SuppressWarnings("unused")
	private static void displayCandidateIsPresent(String value) {
		StringBuilder sb = new StringBuilder("    --- " + value + " ---\n");
		for (int i = 0; i < grid.getSize(); ++i) {
			for (int j = 0; j < grid.getSize(); ++j) {
				sb.append((grid.getCellAt(i, j).getCandidates().contains(value) ? "O " : ". "));
			}
			sb.append("\n");
		}
		System.out.println(sb.toString());
	}
	
	public static void actReverseFromHeuristic(AHeuristic a) throws PropertyVetoException {
		switch (a.getLevelHeuristics()) {
			case OneCandidate:
				for (String value : StdGrid.defaultValueSet()) {
					for (int i = 0; i < grid.getSize(); ++i) {
						for (int j = 0; j < grid.getSize(); ++j) {
							grid.addCandidate(i, j, value);
						}
					}
				}
				break;
			case UniqueCandidate:
				for (String value : StdGrid.defaultValueSet()) {
					for (int i = 0; i < grid.getSize(); ++i) {
						for (int j = 0; j < grid.getSize(); ++j) {
							grid.addCandidate(i, j, value);
						}
					}
				}
			case TwinsAndTriplet:
				for (String value : StdGrid.defaultValueSet()) {
					for (int i = 0; i < grid.getSize(); ++i) {
						for (int j = 0; j < grid.getSize(); ++j) {
							grid.addCandidate(i, j, value);
						}
					}
				}
				break;
			case XWing:
				for (String value : StdGrid.defaultValueSet()) {
					for (int i = 0; i < grid.getSize(); ++i) {
						for (int j = 0; j < grid.getSize(); ++j) {
							grid.addCandidate(i, j, value);
						}
					}
				}
				break;
			case XYWing:
				for (String value : StdGrid.defaultValueSet()) {
					for (int i = 0; i < grid.getSize(); ++i) {
						for (int j = 0; j < grid.getSize(); ++j) {
							grid.addCandidate(i, j, value);
						}
					}
				}
				break;
			case XYZWing:
				for (String value : StdGrid.defaultValueSet()) {
					for (int i = 0; i < grid.getSize(); ++i) {
						for (int j = 0; j < grid.getSize(); ++j) {
							grid.addCandidate(i, j, value);
						}
					}
				}
				break;
			default:
				throw new AssertionError("Create something for " + a.getLevelHeuristics().getName());
		}
	}
}
