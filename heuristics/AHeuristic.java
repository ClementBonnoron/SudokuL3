package heuristics;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cmd.Action;
import model.BoundedCoordinate;
import model.Cell;
import model.Grid;

public abstract class AHeuristic implements IHeuristic {
	
	// ATTRIBUTS
	
	private LevelHeuristics lh;
	private Grid grid;
	
	private static final int NB_TEST_TIME = 81;
	
	// CONSTRUCTORS
	
	public AHeuristic(LevelHeuristics levelH, Grid grid) {
		if (levelH == null) {
			throw new AssertionError("Constructor levelH null : AHeuristic");
		}
		if (grid == null) {
			throw new AssertionError("Constructor grid null : AHeuristic");
		}
		this.lh = levelH;
		this.grid = grid;
	}
	
	// REQUESTS
	
	public int getLevel() {
		return lh.getLevel();
	}
	
	public String getName() {
		return lh.getName();
	}
	
	// PROTECTED METHODS
	
	protected Grid getGrid() {
		return this.grid;
	}
	
	protected LevelHeuristics getLevelHeuristics() {
		return this.lh;
	}
	
	/**
	 * M�thode permettant de faire nb test sur l'heuristique courante pour v�rifier sa stabilit�e.
	 * 	Affichage :
	 * 		--- TEST <<nom heuristique>> (LevelHeuristique.<<nom LevelHeuristique>>) ---
	 * 		Temps moyen				tm		ms
	 * 		Temps total				tt		ms
	 * 		Nombre test r�ussi		nr		/nb		(<<pourcentage>>%)
	 * 				(affichage selon le taux de r�ussite (x) ->
	 * 					x = 100% 		: Cette heuristique � l'air de fonctionner
	 * 					x = 0%			: Cette heuristique fonctionne peut-�tre, mais aucune solution trouv�e
	 * 					0% < x < 100%	: Cette heuristique n'as pas l'air de fonctionner, trop de variations)
	 * 
	 * Si displayTest == true, alors affiche des informations suppl�mentaires � chaque test.
	 * 	Affichage :
	 * 		Test <<num test>> : (affichage selon solution trouv�e ou non ->
	 * 				solution == null 	:	any solution
	 * 				solution != null	:	
	 * 					solution.description()
	 * 						forall a:solution.getActions()
	 * 							action : a.getName() ; cell : a.getCell().getCoordonnate().toString() ; value : a.getValue()
	 *						forall c:solution.getReason().keySet()
	 *							color : c.toString() ; cell : (forall ce:solution.getReason().get(c) -> ce.getCoordonnate().toString()))
	 * 
 	 * @param nb
	 * @param displayTests
	 * @author cleme
	 
	 */
	protected void testTimeSolution(int nb, boolean displayTests) {
		System.out.println(" --- TEST " + toUpperCase(this.getClass().getSimpleName()) +
				" (LevelHeuristic." + this.getLevelHeuristics().getName() + ") ---");
		long allTime = 0;
		long firstTime = System.currentTimeMillis();
		int nbSuccess = 0;
		for (int i = 0; i < nb; ++i) {
			long d = System.currentTimeMillis();
			Solution sol = this.getSolution();
			if (displayTests) {
				System.out.println("Test " + (i + 1) +
						(sol == null ? " : any solution" : " :\n\t" + sol.description()));
				if (sol != null) {
					for (Action a : sol.getActions()) {
						System.out.println("\t\taction : " + a.getClass().getSimpleName() +
								" ; cell : " + a.getCell().getCoordinate().toString() +
								" ; value : " + a.getValue());
					}
					Map<Color, Collection<Cell>> map = sol.getReasons();
					for (Color c : map.keySet()) {
						StringBuilder listCellCoord = new StringBuilder();
						for (Cell cell : map.get(c)) {
							listCellCoord.append(cell.getCoordinate().toString() + ",");
						}
						System.out.println("\t\tcolor : " + c.toString() +
								" ; cell : " + listCellCoord.toString());
					}
					System.out.println("\n");
				}
			}
			allTime += (System.currentTimeMillis() - d);
			if (sol != null) {
				nbSuccess += 1;
			}
		}
		System.out.println("Temps moyen\t\t" + (allTime / nb) + "\tms");
		System.out.println("Temps total\t\t" + 
				(System.currentTimeMillis() - firstTime) + "\tms");
		System.out.println("Nombre test r�ussi\t" + nbSuccess + "\t/" + nb +
				"\t(" + (100 * nbSuccess / nb) + "%)");
		if (nbSuccess == nb) {
			System.out.println("\tCette heuristique � l'air de fonctionner");
		} else {
			if (nbSuccess == 0) {
				System.out.println("\tCette heuristique fonctionne peut-�tre, mais aucune solution trouv�e");
			} else {
				System.out.println("\tCette heuristique n'as pas l'air de fonctionner, trop de variations");
			}
		}
	}
	
	protected void testTimeSolution(boolean displayTests) {
		testTimeSolution(NB_TEST_TIME, displayTests);
	}
	
	protected void testTimeSolution(int nb) {
		testTimeSolution(nb, false);
	}
	
	protected void testTimeSolution() {
		testTimeSolution(NB_TEST_TIME, false);
	}
	
	/**
	 * Renvoie l'ensemble des cellules contenu dans la ligne num�ro nb.
	 * @param nb
	 * @author cleme
	 * @throws RecuperationException
	 */
	protected List<Cell> getLineNb(int nb) throws RecuperationException {
		if (nb < 0 || nb >= grid.getSize()) {
			throw new RecuperationException("getLineNb value not match : AHeuristic");
		}
		List<Cell> list = new ArrayList<Cell>();
		for (int i = 0; i < grid.getSize(); ++i) {
			list.add(grid.getCellAt(nb, i));
		}
		return list;
	}
	
	/**
	 * Renvoie l'ensemble des cellules contenu dans la colonne num�ro nb.
	 * @param nb
	 * @author cleme
	 * @throws RecuperationException
	 */
	protected List<Cell> getColumnNb(int nb) throws RecuperationException {
		if (nb < 0 || nb >= grid.getSize()) {
			throw new RecuperationException("getLineNb value not match : AHeuristic");
		}
		List<Cell> list = new ArrayList<Cell>();
		for (int i = 0; i < grid.getSize(); ++i) {
			list.add(grid.getCellAt(i, nb));
		}
		return list;
	}
	
	/**
	 * Renvoie l'ensemble des cellules contenu dans le carr� contenant la cellule cell.
	 * @param cell
	 * @author cleme
	 * @throws RecuperationException
	 */
	protected List<Cell> getSquareFrom(Cell cell) {
		BoundedCoordinate coord = cell.getCoordinate();
		int sizeSquare = (int) Math.sqrt((double) grid.getSize());
		int xStart = coord.getX() - (coord.getX() % sizeSquare);
		int yStart = coord.getY() - (coord.getY() % sizeSquare);
		List<Cell> list = new ArrayList<Cell>();
		for (int i = xStart; i < xStart + sizeSquare; ++i) {
			for (int j = yStart; j < yStart + sizeSquare; ++j) {
				list.add(grid.getCellAt(i, j));
			}
		}
		return list;
	}
	
	/**
	 * Renvoie vrai ssi la cellule cell est contenu dans la ligne num�ro nb.
	 * @param cell
	 * @param nb
	 * @author cleme
	 */
	protected boolean isInLine(Cell cell, int nb) {
		return (cell.getCoordinate().getX() == nb);
	}

	/**
	 * Renvoie vrai ssi la cellule cell est contenu dans la colonne num�ro nb.
	 * @param cell
	 * @param nb
	 * @author cleme
	 */
	protected boolean isInColumn(Cell cell, int nb) {
		return (cell.getCoordinate().getY() == nb);
	}
	
	/**
	 * Renvoie vrai ssi la cellule cell est contenu dans le carr� de coordonn�e squareNb.
	 * Les coordonn�es des carr�s sont :
	 * 		(0,0) (0,1) (0,2)
	 * 		(1,0) (1,1) (1,2)
	 * 		(2,0) (2,1) (2,2).
	 * @param cell
	 * @deprecated do not use this request !!! doesn't work at all.
	 * @param nb
	 * @author cleme
	 */
	protected boolean isInSquare(Cell cell, BoundedCoordinate squareNb) {
		BoundedCoordinate coord = cell.getCoordinate();
		int sizeSquare = (int) Math.sqrt((double) grid.getSize());
		int xCell = coord.getX() - (coord.getX() % sizeSquare);
		int yCell = coord.getY() - (coord.getY() % sizeSquare);
		return (xCell == squareNb.getX() && yCell == squareNb.getY());
	}
    
	// PRIVATE REQUESTS
	
	private String toUpperCase(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); ++i) {
			sb.append(Character.toUpperCase(s.charAt(i)));
		}
		return sb.toString();
	}
	
	// IMPLEMENTED INTERN CLASS
	
    public class StdSolution implements Solution {
    	
    	// ATTRIBUTS
    	
    	private List<Action> actions;
    	private Map<Color, Collection<Cell>> reasons;
    	private String description;
    	
    	private static final String DEFAULT_DESCRIPTION = "No description";
    	
    	// CONSTRUCTEUR
    	
    	public StdSolution() {
    		actions = new ArrayList<Action>();
    		reasons = new HashMap<Color, Collection<Cell>>();
    		description = DEFAULT_DESCRIPTION;
    	}
    	
    	@Override
    	public List<Action> getActions() {
    		return Collections.unmodifiableList(this.actions);
    	}

    	@Override
    	public Map<Color, Collection<Cell>> getReasons() {
    		return Collections.unmodifiableMap(this.reasons);
    	}

    	@Override
    	public String description() {
    		return this.description;
    	}
    	
		protected void addAction(Action action) throws SolutionInitializeException {
    		if (action == null) {
    			throw new SolutionInitializeException("adding null action");
    		}
    		actions.add(action);
    	}

		protected void removeLastAction() throws SolutionInitializeException {
    		if (actions.size() == 0) {
    			throw new SolutionInitializeException("No action to remove");
    		}
    		actions.remove(actions.size() - 1);
    	}

		protected void addReason(Color color, Cell cell) {
    		Collection<Cell> list = reasons.get(color);
    		if (list == null) {
    			list = new ArrayList<Cell>();
    		}
    		if (!list.contains(cell)) {
        		list.add(cell);
    		}
    		reasons.put(color, list);
    	}

		protected void removeReason(Color color, Cell cell) {
    		Collection<Cell> list = reasons.get(color);
    		if (list != null && list.contains(cell)) {
    			list.remove(cell);
    			reasons.put(color, list);
    		}
    	}

		protected void removeReason(Color color) {
    		reasons.remove(color);
    	}

		protected void removeReason(Cell cell) {
    		for (Color color : reasons.keySet()) {
        		Collection<Cell> list = reasons.get(color);
        		if (list != null && list.contains(cell)) {
        			list.remove(cell);
        			reasons.put(color, list);
        		}
    		}
    	}

		protected void setDescription(String s) {
    		if (s == null) {
    			this.description = DEFAULT_DESCRIPTION;
    		}
    		this.description = s;
    	}
    }
	
    // ABSTRACT METHODS
    
	public abstract Solution getSolution();
}
