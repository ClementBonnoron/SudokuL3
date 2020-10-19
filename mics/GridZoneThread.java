package misc;

import model.Cell;
import model.Grid;

public abstract class GridZoneThread extends Thread {
	private Grid grid;
	private int xm;
	private int xM;
	private int ym;
	private int yM;
	
	public GridZoneThread(Grid g, int xmin, int xmax, int ymin, int ymax) {
		grid = g;
		xm = xmin;
		xM = xmax;
		ym = ymin;
		yM = ymax;
	}
	
	public GridZoneThread(Grid g, int zone) {
		this(g, zones[zone][0], zones[zone][1], zones[zone][2], zones[zone][3]);
	}
	
	@Override
	public void run() {
		for (int x = xm; x < xM; x++) {
			for (int y = ym; y < yM; y++) {
				action(grid.getCellAt(x, y));
			}
		}
	}
	
	public abstract void action(Cell c);
	
	
	/// STATIC
	
	public static int[][] zones = {
			{ 0, 3, 0, 3 },
			{ 3, 6, 0, 3 },
			{ 6, 9, 0, 3 },
			{ 0, 3, 3, 6 },
			{ 3, 6, 3, 6 },
			{ 6, 9, 3, 6 },
			{ 0, 3, 6, 9 },
			{ 3, 6, 6, 9 },
			{ 6, 9, 6, 9 },
	};
}
