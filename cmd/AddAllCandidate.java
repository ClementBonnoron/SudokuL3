package cmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import model.BoundedCoordinate;
import model.Cell;
import model.Grid;

public class AddAllCandidate extends AbstractAction implements Action {

	private Grid grid;
	private Collection<String> candidates;

	public AddAllCandidate(Cell cell, Grid grid) {
		super(cell, null);
		if (grid == null) {
			throw new AssertionError("constructor grid null : AddAllCandidate");
		}
		candidates = new ArrayList<String>();
		this.grid = grid;
	}

	@Override
	protected void doIt() {
		candidates.clear();
		candidates.addAll(grid.getValues());
		BoundedCoordinate coord = getCell().getCoordinate();
    	for (int k = 0; k < grid.getSize(); ++k) {
    		if (k != coord.getX()) {
    			if (grid.getCellAt(k, coord.getX()).getValue() != null) {
    				candidates.remove(grid.getCellAt(k, coord.getX()).getValue());
    			}
    		}
    		if (k != coord.getY()) {
    			if (grid.getCellAt(coord.getX(), k).getValue() != null) {
    				candidates.remove(grid.getCellAt(coord.getX(), k).getValue());
    			}
    		}
    	}
    	int sizeSquare = grid.getSizeSquare();
    	for (int i = 0; i < sizeSquare; ++i) {
        	for (int j = 0; j < sizeSquare; ++j) {
        		Cell cell = grid.getCellAt(
        				i + (coord.getX() / sizeSquare) * sizeSquare,
        				j + (coord.getX() / sizeSquare) * sizeSquare);
        		if (cell.getValue() != null) {
        			candidates.remove(cell.getValue());
        		}
        	}
    	}
    	for (String s : candidates) {
    		getCell().addCandidate(s);
    	}
	}

	@Override
	protected void undoIt() {
    	for (String s : candidates) {
    		getCell().addCandidate(s);
    	}
	}

}
