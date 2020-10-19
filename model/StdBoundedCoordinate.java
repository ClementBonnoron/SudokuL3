package model;

public class StdBoundedCoordinate implements BoundedCoordinate {
	
	private int x;
	private int y;
	
	public StdBoundedCoordinate(int x, int y) {
		if (x < 0 || y < 0) {
			throw new AssertionError("Constructor StdBoundedCoordinate");
		}
		this.x = x;
		this.y = y;
	}

	@Override
	public int getX() {
		return this.x;
	}

	@Override
	public int getY() {
		return this.y;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(getX());
		sb.append(",");
		sb.append(getY());
		sb.append(")");
		return sb.toString();
	}
}
