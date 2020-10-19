package misc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import model.Cell;
import model.Grid;
import model.StdGrid;

/**
 * Cette classe (qui n'a que des fonctions statiques, pas besoin de l'instancer) permet de sauvegarder et de charger des grilles.
 * Le format des grilles est sp�cifi� dans "mod�le.grid". TODO: faire une sp� propre ;)
 * @author fantovic
 */
public class GridFileSystem {
	
	private static boolean lockNext;
	
	/**
	 * Sauvegarde la grille g dans un fichier f. Si f n'existe pas sur la machine, le fichier est cr��.
	 * @param g grille de sudoku
	 * @param f fichier
	 * @param saveCandidates indique s'il faut ou non sauvegarder les candidats. Sinon ils ne seront pas modifi�s
	 * @throws IOException
	 */
	public static void saveGrid(Grid g, File f, boolean saveCandidates) throws IOException {
		if (lockNext) {
			lockGrid(g);
		}
		if (!f.exists()) {
			f.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(f);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeInt(g.getSize());
        dos.writeBoolean(saveCandidates);
        
        for (String s : g.getValues()) {
        	writeString(dos, s);
        }
        
        for (int y = 0; y < g.getSize(); y++) {
        	for (int x = 0; x < g.getSize(); x++) {
        		saveCell(dos, g.getCellAt(x, y), saveCandidates);
        	}
        }
        
        dos.close();
        bos.close();
        fos.close();
	}
	
	/**
	 * Sauvegarde la grille g dans un fichier f. Si f n'existe pas sur la machine, le fichier est cr��.
	 * Les candidats ne sont pas sauvegard�s.
	 * @param g grille de sudoku
	 * @param f fichier
	 * @throws IOException
	 */
	public static void saveGrid(Grid g, File f) throws IOException {
		saveGrid(g, f, false);
	}
	
	/**
	 * Ouvre une fen�tre de sauvegarde de fichier et sauvegarde la grille � l'emplacement sp�cifi� par l'utilisateur
	 * @param g
	 * @throws IOException
	 */
	public static void openSaveWindow(Grid g) throws IOException {
		openSaveWindow(g, true);
	}
	
	public static void openSaveWindow(Grid g, boolean saveCandidates) throws IOException {
		JFileChooser fc = new JFileChooser();
		FileFilter ff = new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith(".skg");
			}

			@Override
			public String getDescription() {
				return "Fichier de grille \".skg";
			}
		};
		fc.setFileFilter(ff);
		
		int res = fc.showSaveDialog(null);
		if (res == JFileChooser.APPROVE_OPTION) {
			File f = fc.getSelectedFile();
			if (!f.getName().endsWith(".skg")) {
				f = new File(f.getAbsolutePath() + ".skg");
			}
		    saveGrid(g, f, saveCandidates);
		}
	}
	
	/**
	 * Charge une grille depuis un fichier et la renvoie.
	 * @param f le fichier
	 * @return la grille
	 * @throws FileNotFoundException si le fichier n'existe pas.
	 */
	public static Grid loadGrid(File f) throws FileNotFoundException, GridException {
		if (!f.exists()) {
			throw new FileNotFoundException();
		}
		try {
			FileInputStream fis = new FileInputStream(f);
	        BufferedInputStream bis = new BufferedInputStream(fis);
	        DataInputStream dis = new DataInputStream(bis);
	        int size = dis.readInt();
	        boolean candidates = dis.readBoolean();
	        
	        Set<String> values = new HashSet<String>();
	        
	        for (int i = 0; i < size; i++) {
	        	values.add(loadString(dis));
	        }
	        
	        Grid g = new StdGrid(values);
	        
	        for (int y = 0; y < size; y++) {
	        	for (int x = 0; x < size; x++) {
	        		loadCell(dis, g.getCellAt(x, y), candidates);
	        	}
	        }
	        if (!candidates) {
	        	for (int y = 0; y < size; y++) {
		        	for (int x = 0; x < size; x++) {
		        		Cell c = g.getCellAt(x, y);
		        		if (c.getValue() == null) {
							Set<String> p = g.getPossibleCandidatesFrom(x, y);
							for (String v : g.getValues()) {
								if (p.contains(v)) {
									c.addCandidate(v);
								} else {
									c.eliminateCandidate(v);
								}
							}
						}
		        	}
		        }
	        }
	        
	        dis.close();
	        bis.close();
	        fis.close();
	        return g;
		} catch (IOException e) {
			throw new GridException("La grille n'a pas �t� correctement sauvegard�e...");
		}
	}
	
	/**
	 * Ouvre une fen�tre de chargement de fichier et charge la grille � l'emplacement sp�cifi� par l'utilisateur. Renvoie la grille lue.
	 * @throws IOException
	 */
	public static Grid openLoadWindow() throws IOException {
		JFileChooser fc = new JFileChooser();
		FileFilter ff = new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith(".skg");
			}

			@Override
			public String getDescription() {
				return "Fichier de grille \".skg";
			}
		};
		fc.setFileFilter(ff);
		int res = fc.showOpenDialog(null);
		if (res == JFileChooser.APPROVE_OPTION) {
		    File f = fc.getSelectedFile();
		    try {
				return loadGrid(f);
			} catch (GridException e) {
				GridException.ShowErrorMessage(e.getMessage(), "Erreur");
			}
		}
		return null;
	}
	
	/**
	 * D�finie si la prochaine grille sauvegard�e est une grille normale (false), ou un niveau (true)
	 * @param value
	 */
	public static void setNextGridBlocked(boolean value) {
		lockNext = value;
	}
	
	// OUTILS
	
	private static void lockGrid(Grid g) {
		for (int x = 0; x < g.getSize(); x++)
		{
			for (int y = 0; y < g.getSize(); y++) {
				if (g.getValueFrom(x, y) != null) {
					g.getCellAt(x, y).block();
				}
			}
		}
		
		lockNext = false;
	}
	
	private static void saveCell(DataOutputStream dos, Cell c, boolean candidates) throws IOException {
		writeString(dos, c.getValue());
		dos.writeBoolean(c.isBlocked());
		if (candidates) {
			saveCellCandidates(dos, c);
		}
	}
	
	private static void saveCellCandidates(DataOutputStream dos, Cell c) throws IOException {
		Collection<String> candidates = c.getCandidates();
		for (String s : c.getGrid().getValues()) {
			if (candidates != null && candidates.contains(s)) {
				dos.writeBoolean(true);
			} else {
				dos.writeBoolean(false);
			}
		}
	}
	
	private static void writeString(DataOutputStream dos, String value) throws IOException {
		if (value == null) {
			dos.writeInt(0);
		} else {
			dos.writeInt(value.length());
			dos.writeChars(value);
		}
	}
	
	private static String loadString(DataInputStream dis) throws IOException {
		int size = dis.readInt();
		if (size == 0) {
			return null;
		}
		StringBuilder sb = new StringBuilder(size);
		for (int i = 0; i < size; i++) {
			sb.append(dis.readChar());
		}
		return sb.toString();
	}
	
	private static void loadCell(DataInputStream dis, Cell c, boolean candidates) throws IOException {
		c.setValue(loadString(dis));
		boolean blocked = dis.readBoolean();
		if (blocked) {
			c.block();
		}
		if (candidates) {
			loadCellCandidates(dis, c);
		}
	}
	
	private static void loadCellCandidates(DataInputStream dis, Cell c) throws IOException {
		for (String s : c.getGrid().getValues()) {
			if (dis.readBoolean()) {
				c.addCandidate(s);
			} else {
				c.eliminateCandidate(s);
			}
		}
	}
	
	@SuppressWarnings("unused")
	private static <E> Set<E> arrayToSet(E[] values) {
		Set<E> s = new TreeSet<E>();
		for (E e : values) {
			s.add(e);
		}
		return s;
	}
}
