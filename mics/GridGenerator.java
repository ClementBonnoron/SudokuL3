package misc;

import java.util.Random;
import java.util.Set;

import model.Cell;
import model.Grid;
import model.StdGrid;

/**
 * GridGenerator est une classe permettant de créer une grille aléatoire.
 * @author fantovic
 * @see Grid
 * @version 0.1
 * @implNote Cette classe n'est pas thread safe ! Chaque thread doit avoir son propre générateur.
 */
public class GridGenerator {
	// ATTRIBUTS
	private Grid lastGrid;
	private Random rng;
	private Set<String> values;

	// CONSTRUCTEUR
	
	public GridGenerator(Set<String> valueSet) {
		this(valueSet, 0);
	}
	
	public GridGenerator(Set<String> valueSet, long seed) {
		lastGrid = null;
		values = valueSet;
		if (seed == 0) {
			rng = new Random();
		} else {
			rng = new Random(seed);
		}
	}
	
	// REQUETES
	
	/**
	 * generateGrid crée une grille aléatoire et la renvoie.
	 */
	public Grid getRandomGrid() {
		lastGrid = generateFullGrid();
		return lastGrid;
	}
	
	/**
	 * Renvoie la dernière grille générée, et null si aucune grille n'a été générée.
	 * @return la dernière grille générée par le générateur.
	 */
	public Grid getLastGrid() {
		return lastGrid;
	}
	
	// COMMANDES
	
	public static void calculateObviousCandidates(Grid g) {
		for (int y = 0; y < g.getSize(); y++) {
			for (int x = 0; x < g.getSize(); x++) {
				calculateCellObviousCandiates(g, x, y);
			}
		}
	}
	
	public static void calculateCellObviousCandiates(Grid g, int x, int y) {
		Cell c = g.getCellAt(x, y);
		if (c.getValue() != null) {
			return;
		}
		for (String v : g.getValues()) {
//			System.out.print("> " + x + ":" + y + " [" + v + "]");
			c.eliminateCandidate(v);
			if (!isCandidateInLine(g, v, y)
					&& !isCandidateInColumn(g, v, x)
					&& !isCandidateInSquare(g, v, x, y)) {
//				System.out.print("\tyup");
				c.addCandidate(v);
			}
			System.out.println();
		}
	}
	
	// OUTILS
	
	private static boolean isCandidateInLine(Grid g, String candidate, int y) {
//		System.out.print(".");
		for (int x = 0; x < g.getSize(); x++) {
			if (g.getCellAt(x, y).getValue() != null 
					&& g.getCellAt(x, y).getValue().equals(candidate)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isCandidateInColumn(Grid g, String candidate, int x) {
//		System.out.print(".");
		for (int y = 0; y < g.getSize(); y++) {
			if (g.getCellAt(x, y).getValue() != null 
					&& g.getCellAt(x, y).getValue().equals(candidate)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isCandidateInSquare(Grid g, String candidate, int x, int y) {
//		System.out.print(".");
		int _x = (x / g.getSizeSquare()) * g.getSizeSquare();
		int _y = (y / g.getSizeSquare()) * g.getSizeSquare();
//		System.out.println("Entering " + x + ":" + y + "(" + candidate + ")");
		for (int i = _x; i < g.getSizeSquare() + _x; ++i) {
			for (int j = _y; j < g.getSizeSquare() + _y; ++j) {
				if (g.getCellAt(i, j).getValue() != null 
						&& g.getCellAt(i, j).getValue().equals(candidate)) {
					return true;
				}
//				System.out.println(" > Checking " + i + ":" + j);
			}
		}
		
		return false;
	}
	
	private String randomValue() {
		return (String) values.toArray()[rng.nextInt(values.size())];
	}
	
	private Grid generateFullGrid() {
		Grid g = new StdGrid(values);
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				g.getCellAt(i, j).setValue(randomValue());
			}
		}
		return g;
	}
	
}
