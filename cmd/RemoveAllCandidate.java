package cmd;

import java.util.ArrayList;
import java.util.List;

import model.Cell;

public class RemoveAllCandidate extends AbstractAction implements Action {
	
	private List<String> candidates;
	
	public RemoveAllCandidate(Cell cell) {
		super(cell, null);
		candidates = new ArrayList<String>();
	}

	@Override
	protected void doIt() {
		if (getCell().getCandidates() != null) {
			for (String s : getCell().getCandidates()) {
				candidates.add(s);
			}
			for (String s : candidates) {
				getCell().eliminateCandidate(s);
			}
		}
	}

	@Override
	protected void undoIt() {
		for (String s : candidates) {
			getCell().addCandidate(s);
		}
	}

}
