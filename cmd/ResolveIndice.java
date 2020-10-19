package cmd;

import heuristics.IHeuristic.Solution;
import model.Cell;
import model.Grid;

public class ResolveIndice extends AbstractAction implements Action {

  // ATTRIBUTS

  private Solution solution;

  // CONSTRUCTEURS

  private ResolveIndice(Cell cell, String value) {
    super(cell, value);
  }

  public ResolveIndice(Solution sol, Grid grid) {
    this(grid.getCellAt(0, 0), null);
    if (sol == null) {
      throw new AssertionError("Invalid Solution : ResolveIndice");
    }
    solution = sol;
  }

  @Override
  protected void doIt() {
    if (solution.getActions() != null) {
      for (Action a : solution.getActions()) {
        a.act();
      }
    }
  }

  @Override
  protected void undoIt() {
    if (solution.getActions() != null) {
      for (Action a : solution.getActions()) {
        a.act();
      }
    }
  }
  
}