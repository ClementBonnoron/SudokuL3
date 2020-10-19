package heuristics;


import java.awt.Color;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import cmd.Action;
import heuristics.IHeuristic.Solution;
import model.Cell;
import model.Grid;
import model.StdGrid;

public class Test {
	
	// ATTRIBUTS
	
	private static Grid grid;
	private static Set<String> CANDIDATES;
	private static AHeuristic[] HEURISTICS;
	
	private static boolean UNIQUE_BT = false;
	
	
	// OUTILS
	
	public static void printGrid(Grid g) {
		for (String[] s : g.getStringGrid()) {
			for (int i = 0; i < s.length; ++i) {
				if (s[i] == null) {
					System.out.print("X ");
				} else {
					System.out.print(s[i] + " ");
				}
			}
			System.out.println();
		}
	}
	
	public static void printCandidates(Grid g) {
		for (int i = 0; i < grid.getSize(); ++i) {
			for (int j = 0; j < grid.getSize(); ++j) {
				System.out.print(g.getCellAt(i, j).toString() + "\t");
			}
			System.out.println();
		}
	}
	public static void printReason(Solution s) {
		assert (s != null);
		
		Map<Color, Collection<Cell>> map = s.getReasons();
		for (Color c : s.getReasons().keySet()) {
			System.out.println(c.toString() + " :");
			for (Cell cell : map.get(c)) {
				System.out.println("\t" + cell.getCoordinate().toString() + "\t" + cell.toString());
			}
			System.out.println();
		}
	}
	
	// METHODES STATIQUES
	
	// POINT D'ENTREE
	
	public static void main(String[] args) {
		CANDIDATES = new LinkedHashSet<String>();
		CANDIDATES.add("1");
		CANDIDATES.add("2");
		CANDIDATES.add("3");
		CANDIDATES.add("4");
		CANDIDATES.add("5");
		CANDIDATES.add("6");
		CANDIDATES.add("7");
		CANDIDATES.add("8");
		CANDIDATES.add("9");
		Object[] cc = CANDIDATES.toArray();
		String[] c = new String[cc.length];
		for (int i = 0; i < cc.length; ++i) {
			c[i] = (String) cc[i];
		}
		grid = new StdGrid(CANDIDATES);
		
		// Giga grille (y a de tout)
		// https://www.mots-croises.ch/Sudoku/grille.htm?g=G629657124545-f5aba&t=N8
		/*
		 * Note : Les 3 derniers coloring se font en 1 
		 */
//		setValue(1, 2, 1);
//		setValue(1, 4, 9);
//		setValue(1, 7, 7);
//		setValue(1, 9, 6);
//		
//		setValue(2, 8, 4);
//		
//		setValue(3, 1, 2);
//		setValue(3, 4, 4);
//		setValue(3, 5, 7);
//		setValue(3, 8, 8);
//		
//		setValue(4, 1, 6);
//		setValue(4, 4, 1);
//		setValue(4, 9, 8);
//		
//		setValue(5, 2, 4);
//		setValue(5, 4, 2);
//		setValue(5, 6, 7);
//		setValue(5, 8, 6);
//		
//		setValue(6, 1, 7);
//		setValue(6, 6, 9);
//		setValue(6, 9, 1);
//		
//		setValue(7, 2, 8);
//		setValue(7, 5, 2);
//		setValue(7, 6, 5);
//		setValue(7, 9, 3);
//		
//		setValue(8, 2, 6);
//		
//		setValue(9, 1, 1);
//		setValue(9, 3, 2);
//		setValue(9, 6, 4);
//		setValue(9, 8, 9);
		
		// Test X-Wing
		// https://www.mots-croises.ch/Sudoku/grille.htm?g=G140172074230-af432&t=N6
		/*
		 * Problemes : 
		 *   - Page 1 : trouve un jumeau au lieu du premier groupe isolé 
		 *   - Page 3 : le X-Wing trouvé n'est pas correct (candidat 1 qui apparait 0 fois dans des régions)
		 *   ==> suite intestable
		 */
		
//		setValue(1, 2, 9);
//		setValue(1, 4, 1);
//		setValue(1, 6, 3);
//		setValue(1, 8, 8);
//		
//		setValue(2, 1, 1);
//		setValue(2, 9, 3);
//		
//		setValue(3, 2, 3);
//		setValue(3, 4, 7);
//		setValue(3, 5, 2);
//		setValue(3, 8, 5);
//		
//		setValue(4, 2, 4);
//		setValue(4, 4, 5);
//		setValue(4, 6, 7);
//		setValue(4, 7, 3);
//		
//		setValue(5, 2, 7);
//		setValue(5, 5, 8);
//		setValue(5, 8, 1);
//		
//		setValue(6, 3, 5);
//		setValue(6, 4, 2);
//		setValue(6, 6, 6);
//		setValue(6, 8, 7);
//		
//		setValue(7, 2, 1);
//		setValue(7, 4, 6);
//		setValue(7, 5, 7);
//		setValue(7, 8, 9);
//		
//		setValue(8, 1, 7);
//		setValue(8, 9, 6);
//		
//		setValue(9, 2, 6);
//		setValue(9, 4, 9);
//		setValue(9, 6, 2);
//		setValue(9, 8, 3);
		
		// Test XY-Wing avec jumeaux
		// https://www.mots-croises.ch/Sudoku/grille.htm?g=G640007350145-09654&t=N7
		/*
		 * Problemes : 
		 *  - Trouve un candidat 5 qui ne doit pas être supprimé
		 *  - suite intestable : Une fois le probleme résolu, tester XY-Wing
		 */
		setValue(1, 1, 1);
		setValue(1, 9, 9);
		
		setValue(2, 3, 8);
		setValue(2, 4, 6);
		setValue(2, 6, 2);
		setValue(2, 7, 4);

		setValue(3, 2, 9);
		setValue(3, 4, 5);
		setValue(3, 8, 3);

		setValue(4, 2, 7);
		setValue(4, 5, 4);
		setValue(4, 7, 3);
		setValue(4, 8, 8);

		setValue(6, 2, 4);
		setValue(6, 3, 1);
		setValue(6, 5, 5);
		setValue(6, 8, 7);

		setValue(7, 2, 1);
		setValue(7, 6, 9);
		setValue(7, 8, 6);

		setValue(8, 3, 6);
		setValue(8, 4, 3);
		setValue(8, 6, 7);
		setValue(8, 7, 5);

		setValue(9, 1, 3);
		setValue(9, 9, 4);

		//////////////////////////////////////////////////////////////////////
		printGrid(grid);
		printCandidates(grid);
		
		boolean stop = false;
		HEURISTICS = new AHeuristic[] {
			new OneCandidate(grid),
			new UniqueCandidate(grid),
			new TwinsAndTriplet(grid),
			new InteractionsBetweenRegion(grid),
			new IdenticalCandidates(grid),
			new IsolatedGroups(grid),
			new MixedGroups(grid),
			new XWing(grid),
			new XYWing(grid),
			new Swordfish(grid),
			new Jellyfish(grid),
			new Squirmbag(grid),
			new Burma(grid),
			new Coloring(grid),
//			new Backtracking(grid, UNIQUE_BT),
		};
		
		for (int i = 0; i < HEURISTICS.length;) {
			switch(HEURISTICS[i].getLevelHeuristics()) {
			
			case OneCandidate:
				HEURISTICS[i] = new OneCandidate(grid);
				break;
			case UniqueCandidate:
				HEURISTICS[i] = new UniqueCandidate(grid);
				break;
			case TwinsAndTriplet:
				HEURISTICS[i] = new TwinsAndTriplet(grid);
				break;
			case InteractionsBetweenRegion:
				HEURISTICS[i] = new InteractionsBetweenRegion(grid);
				break;
			case IdenticalCandidates:
				HEURISTICS[i] = new IdenticalCandidates(grid);
				break;
			case IsolatedGroups:
				HEURISTICS[i] = new IsolatedGroups(grid);
				break;
			case MixedGroups:
				HEURISTICS[i] = new MixedGroups(grid);
				break;
			case XWing:
				HEURISTICS[i] = new XWing(grid);
				break;
			case Swordfish:
				HEURISTICS[i] = new Swordfish(grid);
				break;
			case Jellyfish:
				HEURISTICS[i] = new Jellyfish(grid);
				break;
			case Coloring:
				HEURISTICS[i] = new Coloring(grid);
				break;
			case Backtracking:
				HEURISTICS[i] = new Backtracking(grid, UNIQUE_BT);
				break;
			default:
				break;
		}

			int cpt = 0;
			Solution solution = HEURISTICS[i].getSolution();
			while (solution != null && !stop) {
				System.out.println(HEURISTICS[i].getName());
				System.out.println(solution.description() + "\n");
				
//				if (HEURISTICS[i].getLevelHeuristics() == IHeuristique.LevelHeuristics.TwinsAndTriplet) {
//					printCandidates(grid);
//				}
				
				System.out.println("\tRaisons : ");
				for (Color ci : solution.getReasons().keySet()) {
					System.out.println("\t\tCouleur " + ci.toString() + " : ");
					for (Cell celli : solution.getReasons().get(ci)) {
						System.out.println("\t\t\t-" + celli);
					}
				}
				
				for (Action a : solution.getActions()) {
					System.out.println("\t-" + a.getCell() + " : " + a.getValue().toString());
					a.act();
				}
				
				System.out.println("--------------------------------------------------------------");
				
				if (HEURISTICS[i].getLevelHeuristics() 
						== IHeuristic.LevelHeuristics.Backtracking) {
					stop = true;
				}
				i = 0;
				++cpt;
				solution = HEURISTICS[i].getSolution();
			}
			if (cpt == 0) {
				++i;
			}
		}
		printGrid(grid);
		printCandidates(grid);
	}
	
	private static void setValue(int l, int col, int cand) {
		Object[] cc = CANDIDATES.toArray();
		String[] c = new String[cc.length];
		for (int i = 0; i < cc.length; ++i) {
			c[i] = (String) cc[i];
		}
		grid.setValue(l - 1,  col - 1, c[cand - 1], true);
	}
	
	private static void removeCand(int l, int col, int cand) {
		Object[] cc = CANDIDATES.toArray();
		String[] c = new String[cc.length];
		for (int i = 0; i < cc.length; ++i) {
			c[i] = (String) cc[i];
		}
		grid.removeCandidate(l - 1,  col - 1, c[cand - 1]);
	}
}
