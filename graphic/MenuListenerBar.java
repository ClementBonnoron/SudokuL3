package graphic;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

import cmd.Action;
import heuristics.IHeuristic.Solution;
import misc.GridFileSystem;
import model.BoundedCoordinate;
import model.Cell;
import model.Grid;
import model.History;
import model.StdGrid;

      public class MenuListenerBar implements ActionListener {
      	//ATTRIBUTS
      	private JFrame myFrame;
      	private Grid grid;
      	private JMenuBar menuBar;
      	protected final Map<JMenuItem, Item> MAP_ITEM =
      			new HashMap<JMenuItem, Item>();
      	protected final Map<Item, JMenuItem> MAP_JITEM =
      			new HashMap<Item, JMenuItem>();
      	private JTextArea heuristic;
      	private GridViewer viewer;
      	private GridFileSystem gfs;
      	private Collection<Cell> backupColor;
      	private JButton undo;
      	private JButton redo;
      	private JButton indice;
      	private JButton exit;
      	private JButton resolve;
      	
      	//CONSTRUCTEUR
      	public MenuListenerBar(JFrame frame, Grid g, JTextArea h, GridViewer v, JButton undo2, JButton redo2, JButton indice2, JButton exit2, JButton resolve2) {
      		myFrame = frame;
      		grid = g;
      		heuristic = h;
      		viewer = v;      		
      		backupColor = new ArrayList<Cell>();
      		this.exit = exit2;
      		this.resolve = resolve2;
      		this.undo = undo2;
      		this.redo = redo2;
      		this.indice = indice2;
      		createView();
      		placeComponents();
      		createController();
      	}
      	
      	public void createView() {
      		menuBar = new JMenuBar();	
      		for (Menu m : Menu.values()) {
      			for (Item i : Menu.MENU_STRUCT.get(m)) {
      				if (i != null) {
      					JMenuItem j = new JMenuItem(i.getLabel());
      					MAP_ITEM.put(j, i);
      					MAP_JITEM.put(i, j);
      				}
      			}
      		}
      		exit.setVisible(false);

      	}
      	public void placeComponents() {
      			for (Menu m : Menu.values()) {
      				JMenu menu = new JMenu(m.getLabel());
      				for (Item i : Menu.MENU_STRUCT.get(m)) {
      					if (i == null) {
      						menu.addSeparator();
      					} else {
      						menu.add(MAP_JITEM.get(i));
      						if((MAP_JITEM.get(i) == MAP_JITEM.get(Item.UNDO)) || (MAP_JITEM.get(i) == MAP_JITEM.get(Item.REDO)) ) {
      							MAP_JITEM.get(i).setEnabled(false);
      						}
      					}
      				}
      				menuBar.add(menu);
      			}
      			myFrame.setJMenuBar(menuBar);
      			myFrame.setVisible(true);
      	}
      	public void createController() {
      		for (JMenuItem i : MAP_ITEM.keySet()) {
      			final Item item = MAP_ITEM.get(i);
    			i.addActionListener(new ActionListener() {
    				@SuppressWarnings("static-access")
					public void actionPerformed(ActionEvent e) {
    					switch (item) {
    					case OPEN:
    						removeCellColor();
    						try {
    							if(viewer.getEditor()) {
    								 int rep = JOptionPane.showConfirmDialog(null,"Voulez-vous sauvegarder votre partie?", "Information", JOptionPane.YES_NO_CANCEL_OPTION);
    						 		    if (rep == JOptionPane.CANCEL_OPTION) {
    						 		      return;
    						 		    }
    						 		    if(rep == JOptionPane.YES_OPTION) {
    						 		      try {
    						 		    	GridFileSystem.setNextGridBlocked(true);
    						 		        gfs.openSaveWindow(grid, !viewer.getEditor());
    						 		      } catch (IOException e2) {                                      
    						 		    	  e2.printStackTrace();
    						 		      }
    						 		    }
    								toggleEditor();
    							}
									Grid Gload = gfs.openLoadWindow();
									if (Gload != null) {
										viewer.setGrid(Gload);
										grid = Gload;
										addHistoryListener();
										grid.clearHistory();
									}
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
					 		break;
					 	case SAVE:
					 		try {
					 			if(viewer.getEditor()) {
					 				GridFileSystem.setNextGridBlocked(true);
					 				gfs.openSaveWindow(grid, !viewer.getEditor());
					 				toggleEditor();
					 			}else {
					 				gfs.openSaveWindow(grid, !viewer.getEditor());
					 			}
							} catch (IOException e2) {
								// TODO Auto-generated catch block
								e2.printStackTrace();
							}
					 		break;
					 	case NEW:
				 		  boolean useful = false;
				 		 removeCellColor();
				 		  for(int i = 0; i < grid.getSize() && !useful; ++i) {
				 		    for(int j = 0; j < grid.getSize() && !useful; ++j) {                                 
				 		    	if(grid.getCellAt(i, j).getValue() != null) {
				 		    		useful = true;
				 		      }
				 		    }
				 		  }
				 		  if(useful) {  
				 		    int rep = JOptionPane.showConfirmDialog(null,"Voulez-vous sauvegarder votre partie?", "Information", JOptionPane.YES_NO_CANCEL_OPTION);
				 		    if (rep == JOptionPane.CANCEL_OPTION) {
				 		      return;
				 		    }
				 		    if(rep == JOptionPane.YES_OPTION) {
				 		      try {                                        
				 		        gfs.openSaveWindow(grid);
				 		      } catch (IOException e2) {                                      
				 		    	  e2.printStackTrace();
				 		      }
				 		    }
				 		  }
				 		  viewer.setEnablePropertyChangeListeners(false);
				 		  for(int i = 0; i < grid.getSize(); ++i) {
				 		    for(int j = 0; j < grid.getSize(); ++j) { 
				 		    	if(grid.getCellAt(i, j).isBlocked()) {
				 		    		grid.getCellAt(i, j).unblock();
				 		    	}
				 		    	if (grid.getValueFrom(i, j) != null) {
					 		    	grid.removeValue(i, j, true);
				 		    	}
				 		    }
				 		  }
				 		  for(int i = 0; i < grid.getSize(); ++i) {
				 		    for(int j = 0; j < grid.getSize(); ++j) {
				 		    	if (grid.getCandidatesFrom(i, j) != null &&
				 		    			grid.getCandidatesFrom(i, j).size() != StdGrid.defaultValueSet().size()) {
					 		    	grid.addAllCandidate(i, j);
				 		    	}
				 		    }
				 		  }
				 		  grid.clearHistory();
				 		  viewer.setEnablePropertyChangeListeners(true);
				 		  break;
					 	case SCREENSHOT:
					 		removeCellColor();
					 		menuBar.setVisible(false);
					 		Timer timer = new Timer(10, new ActionListener() {
					 		    @Override
					 		    public void actionPerformed(ActionEvent arg0) {            
					 		    	viewer.saveScreenshot();
					 		    }
					 		});
					 		timer.setRepeats(false);
					 		timer.start();
					 		menuBar.setVisible(true);
					 		break;
					 	case UNDO:
					 		grid.undo();
					 		break;
					 	case REDO:
					 		grid.redo();
					 		break;
					 	case EDITOR:
					 		toggleEditor();
					 		break;
					 	case INDICE:
					 		viewer.setEnablePropertyChangeListeners(false);
					 		indice(false);
					 		viewer.setEnablePropertyChangeListeners(true);
					 		break;
					 	case RESOLVEINDICE:
					 		viewer.setEnablePropertyChangeListeners(false);
					 		indice(true);
					 		viewer.setEnablePropertyChangeListeners(true);
					 		break;
					 	case RESOLVE:
					 		removeCellColor();
					 		viewer.setEnablePropertyChangeListeners(false);
					 		grid.resolve();
					 		viewer.setEnablePropertyChangeListeners(true);
					 		// grid.clearHistory();
					 		break;
					 	case RULES:
					 		JOptionPane.showMessageDialog(myFrame, "-Pour résumer la règle\n" + 
					 				"Il faut remplir la grille en utilisant les chiffres de 1 à 9. Obligatoirement une seule fois dans chaque ligne, colonne et carré de 3 x 3.\n" + 
					 				"\n" + 
					 				"-Pour reprendre le vocabulaire du guide\n" + 
					 				"Il faut remplir la grille en utilisant les 9 symboles donnés (généralement les chiffres de 1 à 9, ou des lettres indiquées au bas de la grille).\n" +
					 				"Obligatoirement une seule fois (mais pas plus) sur chaque ligne, colonne et région (ou carré de 3 par 3).\n"  
					 				,"Rules",  JOptionPane.INFORMATION_MESSAGE);
					 		break;
					 	case SHORTCUT:
					 		JOptionPane.showMessageDialog(myFrame,
					 				"=====    Par défaut sur le GridViewer    =====\n"+
					 				"X <- 0, 1, 2, 3, 4, 5, 6, 7, 8 ou 9\n"+

					 				"X        	 	met la valeur X dans la case\n"+
					 				"Ctrl + X    	met ou enlève X comme candidat\n"+
					 				"Ctrl + S    	Sauvegarde la grille\n"+
					 				"Ctrl + L    	Charge une grille\n"+
					 				"Flêche droite  déplace le curseur vers la droite\n"+
					 				"Flêche haut    déplace le curseur vers le haut\n"+
					 				"Flêche gauche  déplace le curseur vers le gauche\n"+
					 				"Flêche bas     déplace le curseur vers le bas\n"+
					 				"F12         	Screenshot\n"+
					 				"F1         	Mode éditeur\n"+
					 				"+         		Zoom\n"+
					 				"-         		Dézoom\n"+
					 				"=====    À ajouter en externe (Window)    =====\n"+
					 				"Ctrl + Z    	  Retour arrière\n"+
					 				"Ctrl + Shift + Z Retour avant\n"+
					 				"Ctrl + Y    	  Retour avant\n"
					 				,"SHORTCUT",  JOptionPane.INFORMATION_MESSAGE);
					 		break;
					 	case LEVEL:
					 		int level = viewer.getModel().getGrid().getDifficulty();
					 		String message = "";
					 		if (viewer.getModel().getGrid().isFull()) {
					 			message = "Cette grille est déjà résolu !\n";
					 		} else if (level == -1) {
					 			message = "Cette grille à plusieurs solutions !\n";
					 		} else if (level == 0) {
					 			message = "Nous ne pouvons pas donner de difficulté pour cette grille avec" + 
					 					" les heuristicques actuellement implémentées!\n";
					 		} else {
					 			System.out.println((level / 2));
					 			message = "La difficulté de la grille est :\n"  +
								 		level + " / " + viewer.getModel().getGrid().getMaxDifficulty();
					 			message += " " + getDifficultyMessage((level + 1) / 2) + "\n";
					 		}
					 		JOptionPane.showMessageDialog(myFrame, message
					 				,"Difficulté",  JOptionPane.INFORMATION_MESSAGE);
					 		viewer.getModel().getGrid().getRegroupHeuristics().resetDifficultyMemory();
					 		//viewer.getModel().getGrid().
					 		break;
					 	default:
					 		break;
    					}
    				}
    			});
    			addHistoryListener();
      		}
      		
      		
      	}
      	
      	private String getDifficultyMessage(int i) {
      		switch (i) {
      		case 1:
      			return "(Facile)";
      		case 2:
      			return "(Moyen)";
      		case 3:
      			return "(Difficile)";
      		case 4:
      			return "(Impossible)";
	      		default:
	      			return null;
      		}
      	}
      	
      	private void toggleEditor() {
      		removeCellColor();
      		viewer.toggleEditor();
 			exit.setVisible(viewer.getEditor());
 			resolve.setVisible(!viewer.getEditor());
 			undo.setEnabled(!viewer.getEditor());
 			redo.setEnabled(!viewer.getEditor());
 			indice.setEnabled(!viewer.getEditor());
 			MAP_JITEM.get(Item.UNDO).setEnabled(!viewer.getEditor());
 			MAP_JITEM.get(Item.NEW).setEnabled(!viewer.getEditor());
 			MAP_JITEM.get(Item.REDO).setEnabled(!viewer.getEditor());
 			MAP_JITEM.get(Item.INDICE).setEnabled(!viewer.getEditor());
 			MAP_JITEM.get(Item.RESOLVE).setEnabled(!viewer.getEditor());
 			MAP_JITEM.get(Item.RESOLVEINDICE).setEnabled(!viewer.getEditor());
 			MAP_JITEM.get(Item.LEVEL).setEnabled(!viewer.getEditor());
 			MAP_JITEM.get(Item.EDITOR).setText(viewer.getEditor() ? "Quitter l'edition" : "Editer la grille");
 			if(grid != null) {
 				grid.clearHistory();
 			}
 			
 			
      	}
      	private void indice(boolean res) {
        	heuristic.setText("");
        	removeCellColor();
        	Solution sol = viewer.getModel().getGrid().getHelp();
     		if (sol != null) {
     			backupColor.clear();
     			heuristic.setText(sol.description());
     			Map<Color, Collection<Cell>> map = sol.getReasons();
     			for (Color c : map.keySet()) {
     				for (Cell cell : map.get(c)) {
     					backupColor.add(cell);
     					BoundedCoordinate bc = cell.getCoordinate();
    		 			viewer.setCellBorderColor(bc.getX(), bc.getY(), c);
     				}
     			}
     			if(res) {
     				for (Action a : sol.getActions()) {
     					a.act();
     				}
     			}
     		} else {
     			heuristic.setText("Aucun indice ne peut �tre donn�\n");

     		}
        }
      	private void removeCellColor() {
      		for(int i = 0; i <viewer.getModel().getGrid().getSize(); ++i) {
        		for(int j = 0; j <viewer.getModel().getGrid().getSize(); ++j) {
        			if(viewer.getCellBorderColor(i, j) != Color.red) {
        				viewer.removeCellBorderColor(i, j);
        			}
        		}
        	}
      	}
      	private void addHistoryListener() {
      		grid.getHistory().addPropertyChangeListener(History.PROP_UNDO, new PropertyChangeListener() {
        		public void propertyChange(PropertyChangeEvent evt) {
        			MAP_JITEM.get(Item.UNDO).setEnabled((boolean) evt.getNewValue());
        		}
        	});
      		grid.getHistory().addPropertyChangeListener(History.PROP_REDO, new PropertyChangeListener() {
        		public void propertyChange(PropertyChangeEvent evt) {
        			MAP_JITEM.get(Item.REDO).setEnabled((boolean) evt.getNewValue());
        		}
        	});
      	}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
}