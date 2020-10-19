package graphic;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.awt.Font;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.Grid;
import model.StdCell.PropertyChangeCell;
import model.StdGrid;
import model.BoundedCoordinate;
import model.Cell;
import misc.GridException;
import misc.GridFileSystem;
import misc.GridGenerator;
import misc.GridZoneThread;

/**
 * Cette classe est un bean affichant une une grille de sudoku
 * @author fantovic
 */
public class GridViewer extends JPanel {
	private static final long serialVersionUID = 5915122487831473789L;
	
	// CONSTANTES
	private static final String TITLE = "Grid Viewer :)";
	private static final int SIMPLE_SIZE = 3;
	private static final int SIZE = SIMPLE_SIZE * SIMPLE_SIZE;
	private static final int CANDIDATE_SIZE_MOD = 1;
	private static final int VALUE_SIZE_MOD = 2;
	private static final int CELL_SIZE_MOD = 3 * 2;
	
	private static final Color LINE_COLOR = new Color(.9f, .9f, .9f);
	private static final Color CELL_COLOR = new Color(.95f, .95f, .95f);
	private static final Color BLOCKED_CELL_COLOR = new Color(.85f, .8f, .8f);
	private static final Color HOVERING_LINE_COLOR = new Color(.6f, .6f, .6f);
	private static final Color SELECTED_CELL_COLOR = Color.red;
	
	/**
	 * Nom de la propri�t� SelectedCell.
	 * Elle permet d'�tre notifi� quand une cellule est selectionn�e ou d�selectionn�e.
	 */
	public static final String PROP_SELECTED_CELL = "SELECTIONADO";
	/**
	 * Nom de la propriété Editor
	 * Permet d'être notifié lorsque le mode éditeur est activé ou désactivé.
	 */
	public static final String PROP_EDITOR = "STANLEE";
	
	// ATTRIBUTS
	private JPanel mainFrame;
    private JPanel[][] cells;
    private JPanel[][] cellgroups;
    
    private GridViewerModel model;
    private PropertyChangeSupport support;
    
    private GridMouseAdapter[][] mouseAdapters;
    private CellColorer[][] cellColorers;
    
    private boolean editor;
    
    private int zoom = 10;
    private boolean allowRefresh;
    private PropertyChangeListener refresher; 
    // CONSTRUCTEURS
    
	/**
	 * Initialise un GridViewer affichant la grille g.
	 * @param g
	 */
    public GridViewer(Grid g) {
        createModel(g);
        createView();
        placeComponents();
        createController();
        allowRefresh = true;
        refresh();
    	this.add(mainFrame);
        this.setGrid(g);
    }
    
    /**
     * Initialise un GridViewer affichant une grille al�atoire
     * @see GridGenerator
     */
    public GridViewer() {
    	this(new GridGenerator(StdGrid.defaultValueSet()).getRandomGrid());
    }
    
    /**
     * Retourne un GridViewer affichant la grille par d�faut
     * @return Un GridViewer
     */
    public static GridViewer defaultGridViewer() {
		try {
			return new GridViewer(GridFileSystem.loadGrid(new File("./vide.grid")));
		} catch (IOException | GridException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }

    /**
     * Retourne un GridViewer affichant une grille vide
     * @return Un GridViewer
     */
    public static GridViewer EmptyGridViewer() {
		return new GridViewer(new StdGrid(StdGrid.defaultValueSet()));
    }
    
    // REQUETES
    
    /**
     * Retourne la cellule selectionn�e si elle existe et null sinon. 
     * @return la cellule selectionn�e
     */
    public Cell getSelectedCell() {
    	return GridMouseAdapter.getSelectedCell();
    }
    
    /**
     * Renvoie le mod�le du GridViewer.
     * @return Le mod�le du GridViwer.
     */
    public GridViewerModel getModel() {
    	return model;
    }
    
    /**
     * Retourne la couleur actuelle des bordures de la cellule en x:y
     * @param x
     * @param y
     * @return la couleur de la bordure
     */
    public Color getCellBorderColor(int x, int y) {
    	return cellColorers[x][y].getCurrentBorderColor();
    }
    
    /**
     * Retourne vrai si le gridviewer est actuellement en mode �diteur de grille.
     * @return
     */
    public boolean getEditor() {
    	return editor;
    }
    
    /**
     * @return un screenshot de la grille en format BufferedImage
     * @throws AWTException
     */
    public BufferedImage getScreenshot() throws AWTException {
    	Rectangle screenRect = new Rectangle(mainFrame.getLocationOnScreen().x, mainFrame.getLocationOnScreen().y, mainFrame.getWidth(), mainFrame.getHeight());
    	BufferedImage capture = new Robot().createScreenCapture(screenRect);
    	return capture;
    }

    public int getCandidateFontSize() {
    	return zoom * CANDIDATE_SIZE_MOD;
    }

    public int getValueFontSize() {
    	return zoom * VALUE_SIZE_MOD;
    }

    public int getCellSize() {
    	return zoom * CELL_SIZE_MOD;
    }
    
    // COMMANDES
    
    /**
     * Active ou désactive le mode éditeur
     */
    public void toggleEditor() {
    	if (editor) {
    		disableEditor();
    	} else {
    		enableEditor();
    	}
    	refresh();
    	support.firePropertyChange(PROP_EDITOR, !editor, editor);
    }

    /**
     * Ouvre un menu de sauvegarde de capture d'écran
     */
    public void saveScreenshot() {
    	Rectangle screenRect = new Rectangle(mainFrame.getLocationOnScreen().x, mainFrame.getLocationOnScreen().y, mainFrame.getWidth(), mainFrame.getHeight());
    	try {
        	BufferedImage capture = new Robot().createScreenCapture(screenRect);
        	JFileChooser fc = new JFileChooser();
        	fc.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "gif", "bmp"));
        	int returnVal = fc.showSaveDialog(null);
        	String extention = "png";
        	File f = null;
            if (returnVal == JFileChooser.APPROVE_OPTION) {
            	if (fc.getSelectedFile().getName().contains(".")) {
            		extention = fc.getSelectedFile().getName().substring(fc.getSelectedFile().getName().lastIndexOf(".") + 1);;
            		f = fc.getSelectedFile();
            	} else {
            		f = new File(fc.getSelectedFile().getAbsolutePath() + "." + extention);
            	}
            } else {
            	return;
            }
			ImageIO.write(capture, extention, f);
		} catch (IOException e) {
			GridException.ShowErrorMessage(e.getLocalizedMessage());
		} catch (AWTException e) {
			GridException.ShowErrorMessage(e.getLocalizedMessage());
		}
    }
    
    /**
     * Rend l'application visible au centre de l'�cran.
     */
    public void display() {
    	JFrame f = new JFrame(TITLE);
    	JScrollPane g = new JScrollPane(mainFrame);
    	f.add(g);
        refresh();
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    public void setEnablePropertyChangeListeners(boolean value) {
    	allowRefresh = value;
    	if (value) {
    		this.refresh();
    	}
    }
    
    /**
     * Change la grille courante en g
     * @param g
     */
    public void setGrid(Grid g) {
    	if (g != null) {
    		model.setGrid(g);
    		model.getGrid().addPropertyChangeListenerGrid(Cell.PROP_VALUE,
    				refresher);
    		model.getGrid().addPropertyChangeListenerGrid(Cell.PROP_CANDIDATES, 
    				refresher);
        	for (int x = 0; x < SIZE; x++) {
        		for (int y = 0; y < SIZE; y++) {
        			mouseAdapters[x][y].UpdateCell(model.getCell(x, y));
        		}
        	}
        	refresh();
    	}
    }
    
    /**
     * Ajoute un PropertyChangeListener au GridViewer.
     * Les propri�t�s sont : {@link #PROP_SELECTED_CELL}
     */
    public void addPropertyChangeListener(String PropertyName, PropertyChangeListener listener) {
    	support.addPropertyChangeListener(PropertyName, listener);
    }

    /**
     * Retire un PropertyChangeListener au GridViewer.
     * Les propri�t�s sont : {@link #PROP_SELECTED_CELL}
     */
    public void removePropertyChangeListener(String PropertyName, PropertyChangeListener listener) {
    	support.removePropertyChangeListener(PropertyName, listener);
    }
    
    /**
     * Assigne la couleur c comme bordure de la cellule en x:y.
     * @param x
     * @param y
     * @param c
     */
    public void setCellBorderColor(int x, int y, Color c) {
    	cellColorers[x][y].colorBorder(c);
    }

    /**
     * Remet la couleur de bordure de la cellule x:y � la valeur par d�faut
     * @param x
     * @param y
     */
    public void removeCellBorderColor(int x, int y) {
    	cellColorers[x][y].removeBorderColor();
    }
    
    public void zoom() {
    	zoom++;
    	refresh();
    }
    
    public void unzoom() {
    	if (zoom > 1) {
    		zoom--;
    	}
    	refresh();
    }
    
    public void addKeybinding(KeyStroke key, AbstractAction action, String actionName) {
    	mainFrame.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(key, actionName);
    	mainFrame.getActionMap().put(actionName, action);
    }

    public void addKeybinding(KeyStroke[] keys, AbstractAction action, String actionName) {
    	for (KeyStroke ks : keys) {
    		mainFrame.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(ks, actionName);
    	}
    	mainFrame.getActionMap().put(actionName, action);
    }
    
    // OUTILS

    private void disableEditor() {
    	blockGrid();
    	fillCandidates();
    	editor = false;
    }
    
    private void enableEditor() {
    	allowRefresh = false;
    	model.editorGrid();
    	editor = true;
    	allowRefresh = true;
    	refresh();
    }
    
    private void createModel(Grid g) {
    	model = new GridViewerModel(g);
    	support = new PropertyChangeSupport(this);
    }
    
    private void createView() {
        mainFrame = new JPanel();
    	cells = new JPanel[SIZE][SIZE];
    	cellgroups = new JPanel[SIMPLE_SIZE][SIMPLE_SIZE];
    	cellColorers = new CellColorer[SIZE][SIZE];
    	mouseAdapters = new GridMouseAdapter[SIZE][SIZE]; 
    	for (int i = 0; i < SIZE; i++) {
    		for (int j = 0; j < SIZE; j++) {
    			cells[i][j] = new JPanel(new GridLayout(1, 1));
    			cells[i][j].setPreferredSize(new Dimension(getCellSize(), getCellSize()));
    			//cells[i][j].setBorder(BorderFactory.createLineBorder(cells[i][j].getBackground()));
    			cells[i][j].setBorder(BorderFactory.createLineBorder(LINE_COLOR));
    		}
    	}
    }
    
    private void placeComponents() {
    	// On place d'abord la grille de groupes de cases !
    	mainFrame = new JPanel(new GridLayout(SIMPLE_SIZE, SIMPLE_SIZE)); {
    		for (int y = 0; y < SIMPLE_SIZE; y++) {
    			for (int x = 0; x < SIMPLE_SIZE; x++) {
    				// On ajoute les groupes...
    				cellgroups[x][y] = new JPanel(new GridLayout(SIMPLE_SIZE, SIMPLE_SIZE)); {
						for (int j = 0; j < SIMPLE_SIZE; j++) {
			    			for (int i = 0; i < SIMPLE_SIZE; i++) {
			    				// Et dans les groupes on ajoute les cellules :)
			    				cellgroups[x][y].add(cells[x * SIMPLE_SIZE + i][y * SIMPLE_SIZE + j]);
			    			}
		    			}
    				}
    				cellgroups[x][y].setBorder(BorderFactory.createLineBorder(Color.darkGray));
    				mainFrame.add(cellgroups[x][y]);
    			}
    		}
    	}
    	mainFrame.setBorder(BorderFactory.createLineBorder(Color.darkGray));
    	// Et on fini par coller la grande grille dans le mainframe !
    	//mainFrame.add(gameboard);
    }
    
    private void createController() {
    	refresher = new PropertyChangeListener() {
    		@Override
    		public void propertyChange(PropertyChangeEvent evt) {
    			if (!allowRefresh) {
    				return;
    			}
    			PropertyChangeCell pcc = (PropertyChangeCell) evt.getNewValue();
    			int x = pcc.getCell().getCoordinate().getX();
    			int y = pcc.getCell().getCoordinate().getY();
    			refreshCell(x, y);
    		}
    	};
    	for (int x = 0; x < SIZE; x++) {
    		for (int y = 0; y < SIZE; y++) {
    			cellColorers[x][y] = new CellColorer(cells[x][y]);
    			mouseAdapters[x][y] = new GridMouseAdapter(model.getCell(x, y), support, cellColorers[x][y]);
    			cells[x][y].addMouseListener(mouseAdapters[x][y]);
    		}
    	}
		model.getGrid().addPropertyChangeListenerGrid(Cell.PROP_VALUE,
				refresher);
		model.getGrid().addPropertyChangeListenerGrid(Cell.PROP_CANDIDATES, 
				refresher);
    	setUpKeyBindings();
    }
    
    private void setUpKeyBindings() {    	
    	int[] inputs = new int[] {
    			KeyEvent.VK_NUMPAD0,
    			KeyEvent.VK_NUMPAD1,
    			KeyEvent.VK_NUMPAD2,
    			KeyEvent.VK_NUMPAD3,
    			KeyEvent.VK_NUMPAD4,
    			KeyEvent.VK_NUMPAD5,
    			KeyEvent.VK_NUMPAD6,
    			KeyEvent.VK_NUMPAD7,
    			KeyEvent.VK_NUMPAD8,
    			KeyEvent.VK_NUMPAD9
    	};
    	int[] inputs2 = new int[] {
    			KeyEvent.VK_0,
    			KeyEvent.VK_1,
    			KeyEvent.VK_2,
    			KeyEvent.VK_3,
    			KeyEvent.VK_4,
    			KeyEvent.VK_5,
    			KeyEvent.VK_6,
    			KeyEvent.VK_7,
    			KeyEvent.VK_8,
    			KeyEvent.VK_9
    	};
    	for (int i = 0; i < inputs.length; i++) {
    		Action valueAction = new SwitchValueAction(model, i);
    		String valueActionName = Integer.toString(i);
    		
    		mainFrame.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(inputs[i], 0), valueActionName);
    		mainFrame.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(inputs2[i], 0), valueActionName);
    		mainFrame.getActionMap().put(valueActionName, valueAction);
    		
    		if (i != 0) {
        		Action candidateAction = new ToggleCandidateAction(i);
        		String candidateActionName = "_" + Integer.toString(i);
        		
        		mainFrame.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(inputs[i], InputEvent.CTRL_DOWN_MASK), candidateActionName);
        		mainFrame.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(inputs2[i], InputEvent.CTRL_DOWN_MASK), candidateActionName);
        		mainFrame.getActionMap().put(candidateActionName, candidateAction);
    		}
    	}
    	
    	mainFrame.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "save");
		mainFrame.getActionMap().put("save", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					GridFileSystem.setNextGridBlocked(editor);
					GridFileSystem.openSaveWindow(model.getGrid(), !editor);
				} catch (IOException e1) {
					GridException.ShowErrorMessage(e1.getMessage());
				}
			}
		});
		
		mainFrame.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK), "load");
		mainFrame.getActionMap().put("load", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Grid g = GridFileSystem.openLoadWindow();
					setGrid(g);
					if (editor) {
						disableEditor();
					}
				} catch (IOException e1) {
					GridException.ShowErrorMessage(e1.getMessage());
				}
			}
		});
		
		mainFrame.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");
		mainFrame.getActionMap().put("right", new AbstractAction() {
			private static final long serialVersionUID = -4632419787558585749L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (GridMouseAdapter.selectedCell == null) {
					return;
				}
				BoundedCoordinate c = GridMouseAdapter.selectedCell.cell.getCoordinate();
				int x = c.getX() + 1;
				if (x >= 9)
					x = 0;
				int y = c.getY();
				if (verifyCoordinate(x, y)) {
					mouseAdapters[x][y].select();
				}
			}
		});

		mainFrame.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
		mainFrame.getActionMap().put("up", new AbstractAction() {
			private static final long serialVersionUID = 3217195254410782447L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (GridMouseAdapter.selectedCell == null) {
					return;
				}
				BoundedCoordinate c = GridMouseAdapter.selectedCell.cell.getCoordinate();
				int x = c.getX();
				int y = c.getY() - 1;
				if (y < 0)
					y = 8;
				if (verifyCoordinate(x, y)) {
					mouseAdapters[x][y].select();
				}
			}
		});

		mainFrame.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
		mainFrame.getActionMap().put("left", new AbstractAction() {
			private static final long serialVersionUID = 5970100545475033925L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (GridMouseAdapter.selectedCell == null) {
					return;
				}
				BoundedCoordinate c = GridMouseAdapter.selectedCell.cell.getCoordinate();
				int x = c.getX() - 1;
				if (x < 0)
					x = 8;
				int y = c.getY();
				if (verifyCoordinate(x, y)) {
					mouseAdapters[x][y].select();
				}
			}
		});

		mainFrame.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
		mainFrame.getActionMap().put("down", new AbstractAction() {
			private static final long serialVersionUID = -2475917792174824018L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (GridMouseAdapter.selectedCell == null) {
					return;
				}
				BoundedCoordinate c = GridMouseAdapter.selectedCell.cell.getCoordinate();
				int x = c.getX();
				int y = c.getY() + 1;
				if (y >= 9)
					y = 0;
				if (verifyCoordinate(x, y)) {
					mouseAdapters[x][y].select();
				}
			}
		});
		
		mainFrame.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0), "screenshot");
		mainFrame.getActionMap().put("screenshot", new AbstractAction() {
			private static final long serialVersionUID = -546812912684414583L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveScreenshot();
			}
		});
		
		mainFrame.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "editor");
		mainFrame.getActionMap().put("editor", new AbstractAction() {
			private static final long serialVersionUID = -546812912684414583L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				toggleEditor();
			}
		});

		mainFrame.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0), "zoom");
		mainFrame.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 0), "zoom");
		mainFrame.getActionMap().put("zoom", new AbstractAction() {
			private static final long serialVersionUID = -5578123104367131864L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				zoom();
			}
		});

		mainFrame.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0), "unzoom");
		mainFrame.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT_PARENTHESIS, 0), "unzoom");
		mainFrame.getActionMap().put("unzoom", new AbstractAction() {
			private static final long serialVersionUID = -3918572585892729920L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				unzoom();
			}
		});
    }
    
    private boolean verifyCoordinate(int x, int y) {
    	return x >= 0 && x < SIZE && y >= 0 && y < SIZE;
    }
    
    private void refresh() {
    	for (int i = 0; i < SIZE; i++) {
    		for (int j = 0; j < SIZE; j++) {
    			refreshCell(i, j);
    		}
    	}
    }
    
    private void refreshCell(int i, int j) {
		Cell c = model.getGrid().getCellAt(i, j);
		cells[i][j].setPreferredSize(new Dimension(getCellSize(), getCellSize()));
		cells[i][j].removeAll();
		if (c.getValue() == null) {
			cells[i][j].add(getCandidatePanel(c));
		} else {
			cells[i][j].add(getValuePanel(c));
		}
		cells[i][j].revalidate();
		cells[i][j].repaint();
    }
    
    private JPanel getCandidatePanel(Cell c) {
		Set<String> can = c.getCandidates();
    	JPanel p = new JPanel(new GridLayout(SIMPLE_SIZE, SIMPLE_SIZE)); {
    		for (String s : model.getGrid().getValues()) {
    			JPanel q = new JPanel(new FlowLayout(FlowLayout.CENTER)); {
    				JLabel l = new JLabel();
        			if (can.contains(s)) {
        				l.setText(s);
        			} else {
        				l.setText(" ");
        			}
        			l.setFont(new Font("Arial", Font.PLAIN, getCandidateFontSize()));
        			l.setBackground(CELL_COLOR);
        			q.add(l);
        			q.setBackground(CELL_COLOR);
    			}
    			p.add(q);
    			p.setBackground(CELL_COLOR);
    		}
    	}
    	return p;
    }
    
    private JPanel getValuePanel(Cell c) {
    	JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER)); {
    		JLabel l = new JLabel(c.getValue());
			l.setFont(new Font("Arial", Font.BOLD, getValueFontSize()));
			p.add(l);
			if (c.isBlocked()) {
				p.setBackground(BLOCKED_CELL_COLOR);
				l.setBackground(BLOCKED_CELL_COLOR);
			} else {
				p.setBackground(CELL_COLOR);
				l.setBackground(CELL_COLOR);
			}
    	}
    	return p;
    }
    
    private void blockGrid() {
    	for (int x = 0; x < model.getGrid().getSize(); x++)
		{
			for (int y = 0; y < model.getGrid().getSize(); y++) {
				if (model.getGrid().getValueFrom(x, y) != null) {
					model.getCell(x, y).block();
				}
			}
		}
    }
    
    private void fillCandidates() {
    	for (int i = 0; i < 9; i++) {
    		GridZoneThread gzt = new GridZoneThread(model.getGrid(), i) {
				@Override
				public void action(Cell c) {
					if (c.getValue() == null) {
						int x = c.getCoordinate().getX();
						int y = c.getCoordinate().getY();
						Set<String> p = model.getGrid().getPossibleCandidatesFrom(x, y);
						for (String v : model.getGrid().getValues()) {
							if (p.contains(v)) {
								c.addCandidate(v);
							} else {
								c.eliminateCandidate(v);
							}
						}
					}
				}
			};
			gzt.run();
    	}
    }

    // POINT D'ENTREE

    /**
     * Lance un programme contenant un simple GridViewer sans autre interface.
     * @param args
     */
	public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //new GridViewer().display();
            	GridViewer gv = GridViewer.EmptyGridViewer();
            	//GridViewer gv = GridViewer.EmptyGridViewer();
            	gv.display();
            }
        });
    }
	
	// CLASSES INTERNES
	
	private static class GridMouseAdapter extends MouseAdapter {

		// STATICS
		private static GridMouseAdapter selectedCell;
		
		// ATTRIBUT
		private Cell cell;
		private boolean hovering;
		private boolean selected;
		private PropertyChangeSupport support;
		private CellColorer colorer;
		
		// CONSTRUCTEUR
		GridMouseAdapter(Cell c, PropertyChangeSupport s, CellColorer cc) {
			cell = c;
			support = s;
			colorer = cc;
		}
		
		// REQUETES
		
		/**
		 * Renvoie la case selectionn�e si il y en a une et null sinon.
		 * @return
		 */
		public static Cell getSelectedCell() {
			if (selectedCell == null) {
				return null;
			}
			return selectedCell.cell;
		}
		
		// COMMANDES
		
		private void UpdateCell(Cell _cell) {
			this.cell = _cell;
		}
		
		private void hover() {
			hovering = true;
			if (!selected) {
				colorer.hover();
			}
		}
		
		private void unhover() {
			if (hovering) {
				hovering = false;
				colorer.unhover();
			}
		}
				
		private void select() {
			if (selectedCell != null) {
				selectedCell.unselect();
			}
			selectedCell = this;
			selected = true;
			colorer.select();
			support.firePropertyChange(PROP_SELECTED_CELL, null, this.cell);
		}
		
		private void unselect() {
			selectedCell = null;
			selected = false;
			colorer.unselect();
			if (hovering) {
				hover();
			} else {
				colorer.unselect();
			}
			support.firePropertyChange(PROP_SELECTED_CELL, null, this.cell);
		}
        
        @Override
        public void mouseClicked(MouseEvent e) {
        	if (selected) {
				unselect();
			} else {
				select();
			}
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
        	unhover();
        }
        
        @Override
        public void mouseEntered(MouseEvent e) {
        	hover();
        }
	}

	private static class CellColorer {	
		
		private Color currentBorderColor;
		
		private JPanel panel;
		
		private CellColorer(JPanel p) {
			panel = p;
			currentBorderColor = LINE_COLOR;
		}
		
		void select() {
			panel.setBorder(BorderFactory.createLineBorder(SELECTED_CELL_COLOR));
			currentBorderColor = SELECTED_CELL_COLOR;
			panel.repaint();
		}
		
		void hover() {
			if (currentBorderColor.equals(LINE_COLOR)) {
				panel.setBorder(BorderFactory.createLineBorder((HOVERING_LINE_COLOR)));
				currentBorderColor = HOVERING_LINE_COLOR;
				panel.repaint();
			}
		}
		
		void unselect() {
			if (currentBorderColor.equals(SELECTED_CELL_COLOR)) {
				panel.setBorder(BorderFactory.createLineBorder(LINE_COLOR));
				currentBorderColor = LINE_COLOR;
				panel.repaint();
			}
		}
		
		void unhover() {
			if (currentBorderColor.equals(HOVERING_LINE_COLOR)) {
				panel.setBorder(BorderFactory.createLineBorder(LINE_COLOR));
				currentBorderColor = LINE_COLOR;
				panel.repaint();
			}
		}
		
		void colorBorder(Color c) {
			currentBorderColor = c;
			panel.setBorder(BorderFactory.createLineBorder(c));
			panel.repaint();
		}
		
		void removeBorderColor() {
			panel.setBorder(BorderFactory.createLineBorder(LINE_COLOR));
			currentBorderColor = LINE_COLOR;
			panel.repaint();
		}
		
		Color getCurrentBorderColor() {
			return currentBorderColor;
		}
	}
	
	private class SwitchValueAction extends AbstractAction {
		private static final long serialVersionUID = 1969987163301418624L;
		
		private GridViewerModel model;
		private int value;
		
		public SwitchValueAction(GridViewerModel model, int value) {
			this.model = model;
			this.value = value;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (GridMouseAdapter.getSelectedCell() == null) {
				return;
			}
			if (value == 0) {
				model.removeValue(GridMouseAdapter.getSelectedCell(), !getEditor());
				
			} else {
				model.removeValue(GridMouseAdapter.getSelectedCell(), !getEditor());
				model.setValue(Integer.toString(value), GridMouseAdapter.getSelectedCell(), !getEditor());
			}
		}
	}
	
	private class ToggleCandidateAction extends AbstractAction {
		private static final long serialVersionUID = 3800409483281814110L;
		
		private int value;
		
		public ToggleCandidateAction(int value) {
			this.value = value;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (editor || GridMouseAdapter.getSelectedCell() == null || GridMouseAdapter.getSelectedCell().getCandidates() == null) {
				return;
			}
			if (GridMouseAdapter.getSelectedCell().getCandidates().contains(Integer.toString(value))) {
				GridMouseAdapter.getSelectedCell().eliminateCandidate(Integer.toString(value));
			} else {
				GridMouseAdapter.getSelectedCell().addCandidate(Integer.toString(value));
			}
		}
	}
}
