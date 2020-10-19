package heuristics;

import java.awt.Color;
import java.util.*;

import cmd.*;
import model.Cell;

/**
 * 
 * @author cleme
 * @author thoma
 * 
 */
public interface IHeuristic {
	
	// REQUETES
	
	/**
	 * Indique le niveau de difficult� de l'heuristique
	 * @return
	 */
    int getLevel();

	// COMMANDES

	/**
	 * Retourne la solution trouv�e par l'heuristique sur la grille grid
	 */
	Solution getSolution();
	
	/**
	 * Renvoie le nom de l'Heuristique
	 */
	String getName();
	
    // CLASSES
	
    public interface Solution {
    	/*
    	 * Actions à effectuer pour remplir le contract de l'heuristique
    	 */
    	List<Action> getActions();
    	
    	/*
    	 * Listes de cellules à colorier, en adéquation avec la raison de
    	 * 		l'heuristique
    	 */
    	Map<Color, Collection<Cell>> getReasons();
    	
    	/*
    	 * Description de l'heuristique, avec son contract
    	 */
        String description();
    }
    
    enum LevelHeuristics {
    	OneCandidate("OneCandidate", 0),
    	UniqueCandidate("UniqueCandidate", 1),
    	TwinsAndTriplet("TwinsAndTriplet", 2),
    	InteractionsBetweenRegion("InteractionsBetweenRegion", 3),
    	IdenticalCandidates("IdenticalCandidates", 4),
    	IsolatedGroups("IsolatedGroups", 5),
    	MixedGroups("MixedGroups", 6),
    	XWing("XWing", 7),
    	XYWing("XYWing", 8),
    	XYZWing("XYZWing", 9),
    	Swordfish("Swordfish", 11),
    	Jellyfish("Jellyfish", 12),
    	Squirmbag("Squirmbag", 13),
    	Burma("Burma", 14),
    	Coloring("Coloring", 15),
    	Backtracking("Backtracking", 100);
    	
    	private String name;
    	private int level;
    	
    	private LevelHeuristics(String name, int level) {
    		this.name = name;
    		this.level = level;
    	}
    	
    	public int getLevel() {
    		return this.level;
    	}
    	
    	public String getName() {
    		return this.name;
    	}
    	
    	static public LevelHeuristics getHeuristique(IHeuristic c) {
    		String nameC = c.getName();
    		for (LevelHeuristics h : LevelHeuristics.values()) {
    			if (nameC.compareTo(h.getName()) == 0) {
    				return h;
    			}
    		}
    		return null;
    	}
    	
    	static public LevelHeuristics getHeuristique(String name) {
    		for (LevelHeuristics h : LevelHeuristics.values()) {
    			if (name.compareTo(h.getName()) == 0) {
    				return h;
    			}
    		}
    		return null;
    	}
    	
    	
    }
    
}
