package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class StdCell implements Cell {
	
	// ATTRIBUTS
	
	private Grid currentGrid;
	private BoundedCoordinate coord;
	private boolean isBlocked;
	
	private String currentValue;
	private Set<String> candidates;
	private PropertyChangeSupport pcs;
	
	// CONSTRUCTEURS
	
	public StdCell(Grid grid, BoundedCoordinate coord) {
		if (grid == null) {
			throw new AssertionError("Constructor StdCell");
		}
		this.currentGrid = grid;
		this.currentValue = null;
		this.coord = coord;
		this.candidates = new TreeSet<String>(grid.getValues());
		pcs = new PropertyChangeSupport(this);
		isBlocked = false;
	}
	
	// REQUETES

	@Override
	public boolean isPossible(String value) {
		return !isBlocked;
	}
	
	@Override
	public boolean isBlocked() {
		return isBlocked;
	}

	@Override
	public String getValue() {
		return this.currentValue;
	}

	@Override
	// (currentValue;value1,value2,...,valuen)
	public String toString() {
		StringBuffer value = new StringBuffer();
		value.append((currentValue == null ?
			"(X;" :
			"(" + getValue().toString() + ";"));
		if (this.getCandidates() != null && this.getCandidates().size() > 0) {
			Object[] tab = getCandidates().toArray();
			value.append(tab[0].toString());
			for (int i = 1; i < tab.length; ++i) {
				value.append("," + tab[i].toString());
			}
		} else {
			value.append("X");
		}
		value.append(";" + this.coord.toString());
		value.append(")");
		return value.toString();
	}

	@Override
	public BoundedCoordinate getCoordinate() {
		return this.coord;
	}

	@Override
	public Grid getGrid() {
		return this.currentGrid;
	}

	@Override
	public Set<String> getCandidates() {
		return (getValue() != null ? null : Collections.unmodifiableSet(candidates));
	}

	// COMMANDES
	
	@Override
	public void block() {
		if (!isBlocked()) {
			isBlocked = true;
			pcs.firePropertyChange(PROP_BLOCKED, false, true);
		}
	}
	
	@Override
	public void unblock() {
		if (isBlocked()) {
			isBlocked = false;
			pcs.firePropertyChange(PROP_BLOCKED, true, false);
		}
	}

	@Override
	public void setValue(String value) {
		if (!isBlocked) {
			String oldValue = this.currentValue;
			this.currentValue = value;
			pcs.firePropertyChange(PROP_VALUE, 
					new PropertyChangeCell(this, oldValue, null), 
					new PropertyChangeCell(this, value, null));
		}
	}

	@Override
	public void removeValue() {
		if (!isBlocked) {
			String oldValue = this.currentValue;
			this.currentValue = null;
			pcs.firePropertyChange(PROP_VALUE, 
					new PropertyChangeCell(this, oldValue, null), 
					new PropertyChangeCell(this, null, null));
		}
	}

	@Override
	public void addCandidate(String value) {
		if (!isBlocked) {
			if (value == null) {
				throw new AssertionError("addCandidate null: StdCell");
			}
			boolean contains = candidates.contains(value);
			this.candidates.add(value);
			pcs.firePropertyChange(PROP_CANDIDATES, 
					new PropertyChangeCell(this, null, (contains ? value : null)), 
					new PropertyChangeCell(this, null, value));
		}
	}

	@Override
	public void eliminateCandidate(String value) {
		if (!isBlocked) {
			if (value == null) {
				throw new AssertionError("eliminateCandidate null: StdCell");
			}
			boolean contains = candidates.contains(value);
			this.candidates.remove(value);
			pcs.firePropertyChange(PROP_CANDIDATES, 
					new PropertyChangeCell(this, null, (contains ? value : null)), 
					new PropertyChangeCell(this, null, null));
		}
	}

	// LISTENERS
	
	@Override
	public void addPropertyChangeListener(String pName, PropertyChangeListener listener) {
		if (listener != null) {
			pcs.addPropertyChangeListener(pName, listener);
		}
	}

	@Override
	public void removePropertyChangeListener(String pName, PropertyChangeListener listener) {
		if (listener != null) {
			pcs.removePropertyChangeListener(pName, listener);
		}
	}
	
	@Override
	public PropertyChangeListener[] getPropertyChangeListeners() {
		return pcs.getPropertyChangeListeners();
	}
	
	@Override
	public PropertyChangeListener[] getPropertyChangeListeners(String name) {
		return pcs.getPropertyChangeListeners(name);
	}
	
	public class PropertyChangeCell {
		private Cell cell;
		private String value;
		private String candidate;
		
		private PropertyChangeCell(Cell _cell, String _value, String _candidate) {
			cell = _cell;
			value = _value;
			candidate = _candidate;
		}
		
		public Cell getCell() {
			return cell;
		}
		
		public String getValue() {
			return value;
		}
		
		public String getCandidate() {
			return candidate;
		}
	}
}
