package graphic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import cmd.Action;
import heuristics.IHeuristic.Solution;
import misc.GridFileSystem;
import model.BoundedCoordinate;
import model.Cell;
import model.History;

public class Window {
	;
	private JFrame frame;
	private Map<String, JButton> buttonValue;
	private Map<String,JToggleButton > buttonCandidate;
	private JTextArea heuristique;
	private ButtonGroup group1;
	private JButton undo;
	private JButton redo;
	private JButton exit;
	private JButton indice;
	private JButton zoom;
	private JButton unzoom;
	private JButton resolve;
	private GridViewer viewer;
	private MenuListenerBar menu;
	private Collection<Cell> backupColor;
	private JScrollPane scrollPane;
	
	private PropertyChangeListener listenerButtons;
	// CONSTRUCTEUR
	
    public Window() {
    	createModel();
        createView();
        placeComponents();
        createController();
    }
    
    // COMMANDES
    
    public void createModel() {
        viewer = GridViewer.EmptyGridViewer();
    }
    
    public void display() {
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    public void createView() {
    	final int frameWidth = 820;
    	final int frameHeight = 820;
    	
        frame = new JFrame("Sudoku");
        frame.setPreferredSize(new Dimension(frameWidth, frameHeight));

        scrollPane = new JScrollPane(viewer);
        scrollPane.setBorder(null);
        JPanel panneau = new JPanel();
        panneau.setBackground(Color.white);
        frame.setContentPane(panneau);
        buttonValue = new HashMap<String,  JButton>();
        group1 = new ButtonGroup();
        for (int n = 1; n <= 9; n++) {
        	buttonValue.put(Integer.toString(n),  new JButton(String.valueOf(n)));
        	group1.add(buttonValue.get(Integer.toString(n)));
        }
        buttonCandidate = new HashMap<String, JToggleButton >();
        for (int n = 1; n <= 9; n++) {
        	buttonCandidate.put(Integer.toString(n),  new JToggleButton(String.valueOf(n)));
        }
        backupColor = new ArrayList<Cell>();
        undo = new JButtonRond("Undo");
        exit = new JButtonRond("Quitter");
        redo = new JButtonRond("Redo");
        zoom = new JButtonRond("zoom +");
        resolve = new JButtonRond("Résoudre");
        unzoom = new JButtonRond("zoom -");
        undo.setEnabled(false);
        redo.setEnabled(false);
        indice = new JButtonRond("Indice");
        heuristique = new JTextArea(8, 50);
        heuristique.setLineWrap(true);
        heuristique.setWrapStyleWord(true);
        heuristique.setEditable(false);
        menu = new MenuListenerBar(frame, viewer.getModel().getGrid(), heuristique, viewer, undo, redo, indice, exit, resolve);

    }
    
    public void placeComponents() {
		frame.setLayout(new BorderLayout());
    	JPanel p = new JPanel(); {
    		p.add(new JLabel(" "));
    	}
    	frame.add(p, BorderLayout.NORTH);
        //--------- COMPOSANT GRAPHIQUE DE LA GRILLE ---------\\
        
    	frame.add(scrollPane, BorderLayout.CENTER);
        //-----------------------------------------------------\\
        
        p = new JPanel(new GridLayout(3, 0)); {
        	JPanel sp = new JPanel(new GridLayout(2, 0)); {
        		JLabel label = new JLabel(); {
            		label.setHorizontalAlignment(JLabel.CENTER);
            		label.setText("Placer une valeur : ");
        		}
        		sp.add(label);
        		JPanel r = new JPanel(new FlowLayout(FlowLayout.CENTER)); {
            		JPanel numPad = new JPanel(new GridLayout(3, 3, 3, 1)); {
                		for (int n = 1; n <= 9; n++) {
                			numPad.add(buttonValue.get(Integer.toString(n)));
                		}
                	}
            		r.add(numPad);
        		}
        		JScrollPane q = new JScrollPane(r);
        		q.setEnabled(false);
        		q.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));
        		sp.add(q);
        	}
        	p.add(sp, BorderLayout.NORTH);
        	sp = new JPanel(new GridLayout(2, 0)); {
        		JLabel label = new JLabel(); {
	        		label.setHorizontalAlignment(JLabel.CENTER);
	        		label.setText("Afficher/Masquer un candidat : ");
        		}
        		sp.add(label);
        		JPanel r = new JPanel(new FlowLayout(FlowLayout.CENTER)); {
            		JPanel candidatePad = new JPanel(new GridLayout(3, 3, 3, 1)); {
                		for (int n = 1; n <= 9; n++) {
                			candidatePad.add(buttonCandidate.get(Integer.toString(n)));
                		}
                	}
            		r.add(candidatePad);
        		}
        		JScrollPane q = new JScrollPane(r);
        		q.setEnabled(false);
        		q.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));
        		sp.add(q);
        	}
            p.add(sp, BorderLayout.SOUTH);
            sp = new JPanel(new GridLayout(3, 0)); {
        		JPanel q = new JPanel(); {
        			q.setLayout(new BoxLayout(q, BoxLayout.LINE_AXIS));
        	        q.add(undo);
        	        q.add(redo);
        		}
        		p.add(q);
        		q = new JPanel(); {
        			q.setLayout(new BoxLayout(q, BoxLayout.LINE_AXIS));
        	        q.add(indice);
        	        q.add(resolve);
        	        q.add(exit);
        		}
        		p.add(q);
        		q = new JPanel(); {
        			q.setLayout(new BoxLayout(q, BoxLayout.LINE_AXIS));
        	        q.add(zoom);
        	        q.add(unzoom);
        		}
        		p.add(q);
        	}
            p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
            p.add(sp, BorderLayout.EAST);
        }
        frame.add(p, BorderLayout.EAST);
	    p = new JPanel(new BorderLayout()); {
	    	JPanel q = new JPanel(); {
	    		q.add(new JScrollPane(heuristique));
	    	}
	    	p.add(q, BorderLayout.CENTER);
	    }
	    frame.add(p, BorderLayout.SOUTH);
    }
    
    public void createController() {
		listenerButtons = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// TODO Auto-generated method stub
				Cell selectedCell = (Cell) evt.getNewValue();
				if (selectedCell != null) {
					for(int i = 0; i < viewer.getModel().getGrid().getValues().size(); ++i) {
						if (selectedCell.getValue() == null) {							
							setButtonValue(false);
						} else {
							setButtonValue(false);
							buttonValue.get(selectedCell.getValue()).setSelected(true);
							buttonValue.get(selectedCell.getValue()).setBackground(Color.GREEN);
						}
						if (selectedCell.getCandidates() == null) {
							setButtonCandidates(false);
						} else {
							setButtonCandidates(false);
							for (String s : selectedCell.getCandidates()) {
								buttonCandidate.get(s).setSelected(true);
							}
						}
					}
				}
			}
		};
    	
    	viewer.addPropertyChangeListener(GridViewer.PROP_SELECTED_CELL, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// TODO Auto-generated method stub
				Cell selectedCell = (Cell) evt.getNewValue();
				if (selectedCell != null && !selectedCell.isBlocked()) {
					for(int i = 1; i <= buttonValue.size(); ++i ) {
						buttonValue.get(Integer.toString(i)).setEnabled(true);
						buttonCandidate.get(Integer.toString(i)).setEnabled(!viewer.getEditor());
					}
				}else {
					for(int i = 1; i <= buttonValue.size(); ++i ) {
						buttonValue.get(Integer.toString(i)).setEnabled(false);
						buttonCandidate.get(Integer.toString(i)).setEnabled(false);
					}
				}
				
			}
		});
    	for(int i = 1; i <= 9; ++i) {
    		final int n = i;
    		buttonCandidate.get(Integer.toString(i)).addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					if(!viewer.getEditor()) {
						Cell selectedCell = viewer.getSelectedCell();
						if(selectedCell != null) {
							if(buttonCandidate.get(Integer.toString(n)).isSelected()) {
								viewer.getModel().getGrid().addCandidate(selectedCell.getCoordinate().getX(), selectedCell.getCoordinate().getY(),buttonCandidate.get(Integer.toString(n)).getText());
							}else {
								viewer.getModel().getGrid().removeCandidate(selectedCell.getCoordinate().getX(), selectedCell.getCoordinate().getY(),buttonCandidate.get(Integer.toString(n)).getText());
							}
						}
					}else {
						for(int i = 1; i <= buttonCandidate.size(); ++i ) {
							buttonCandidate.get(Integer.toString(i)).setEnabled(false);
						}
					}
				}
    		});
    		buttonValue.get(Integer.toString(i)).addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					Cell selectedCell = viewer.getSelectedCell();
					if(selectedCell != null) {
						if(selectedCell.getValue() != null) {
							int x = selectedCell.getCoordinate().getX();
							int y = selectedCell.getCoordinate().getY();
							String s = selectedCell.getValue();
							viewer.getModel().getGrid().removeValue(x, y, true);
							setButtonValue(false);
							if(!s.equals(buttonValue.get(Integer.toString(n)).getText())) {
								viewer.getModel().getGrid().setValue(selectedCell.getCoordinate().getX(), selectedCell.getCoordinate().getY(), Integer.toString(n), true);
								buttonValue.get(Integer.toString(n)).setBackground(Color.green);
							}
							
						}else {
							viewer.getModel().getGrid().setValue(selectedCell.getCoordinate().getX(), selectedCell.getCoordinate().getY(), Integer.toString(n), true);
							buttonValue.get(Integer.toString(n)).setBackground(Color.green);
						}
					}
				}
    		});
    		
    	}
    	undo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					viewer.getModel().getGrid().undo();
				}
			});
		redo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				viewer.getModel().getGrid().redo();
			}
		});
		indice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				indice(false);
			}
		
		});
		zoom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				viewer.zoom();
			}
		});
		unzoom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				viewer.unzoom();
			}
		});
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				toggleEditor();
			}
		});
		resolve.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				indice(true);
			}
		});
		viewer.addPropertyChangeListener(GridViewer.PROP_SELECTED_CELL, listenerButtons);
    	viewer.getModel().getGrid().getHistory().addPropertyChangeListener(History.PROP_UNDO, new PropertyChangeListener() {
    		public void propertyChange(PropertyChangeEvent evt) {
    			undo.setEnabled((boolean) evt.getNewValue());
    		}
    	});
    	viewer.getModel().getGrid().getHistory().addPropertyChangeListener(History.PROP_REDO, new PropertyChangeListener() {
    		public void propertyChange(PropertyChangeEvent evt) {
    			redo.setEnabled((boolean) evt.getNewValue());
    		}
    	});
    	viewer.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "undo");
		viewer.getActionMap().put("undo", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				History<Action> hist = viewer.getModel().getGrid().getHistory();
				if (hist.getCurrentPosition() > 0) {
					viewer.getModel().getGrid().undo();
				}
			}
		});
		viewer.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK), "redo");
		viewer.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "redo");
		viewer.getActionMap().put("redo", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				History<Action> hist = viewer.getModel().getGrid().getHistory();
				if (hist.getCurrentPosition() < hist.getEndPosition()) {
					viewer.getModel().getGrid().redo();
				}
			}
		});
		viewer.getModel().addPropertyChangeListener(GridViewerModel.PROP_GRID, new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				viewer.removePropertyChangeListener(listenerButtons);
		    	viewer.getModel().getGrid().getHistory().addPropertyChangeListener(History.PROP_UNDO, new PropertyChangeListener() {
		    		public void propertyChange(PropertyChangeEvent evt) {
		    			undo.setEnabled((boolean) evt.getNewValue());
		    		}
		    	});
		    	viewer.getModel().getGrid().getHistory().addPropertyChangeListener(History.PROP_REDO, new PropertyChangeListener() {
		    		public void propertyChange(PropertyChangeEvent evt) {
		    			redo.setEnabled((boolean) evt.getNewValue());
		    		}
		    	});
			}
			
		});
    }
    private void setButtonValue(boolean selected) {
    	for (String s : buttonValue.keySet()) {
    		buttonValue.get(s).setSelected(selected);
    		buttonValue.get(s).setBackground(null);
    	}
    }
    
    private void setButtonCandidates(boolean selected) {
    	for (String s : buttonCandidate.keySet()) {
    		buttonCandidate.get(s).setSelected(selected);
    	}
    }
    
    private void indice(boolean res) {
    	heuristique.setText("");
  		for(int i = 0; i <viewer.getModel().getGrid().getSize(); ++i) {
    		for(int j = 0; j <viewer.getModel().getGrid().getSize(); ++j) {
    			if(viewer.getCellBorderColor(i, j) != Color.red) {
    				viewer.removeCellBorderColor(i, j);
    			}
    		}
    	}
    	Solution sol = viewer.getModel().getGrid().getHelp();
 		if (sol != null) {
 			backupColor.clear();
 			heuristique.setText(sol.description());
 			Map<Color, Collection<Cell>> map = sol.getReasons();
 			for (Color c : map.keySet()) {
 				for (Cell cell : map.get(c)) {
 					backupColor.add(cell);
 					BoundedCoordinate bc = cell.getCoordinate();
		 			viewer.setCellBorderColor(bc.getX(), bc.getY(), c);
 				}
			 }
			 if (res) {
				 viewer.getModel().getGrid().resolveHelp();
			 }
 			// if(res) {
 			// 	for (Action a : sol.getActions()) {
 			// 		a.act();
 			// 	}
 			// }
 		} else {
 			heuristique.setText("Aucun indice ne peut �tre donn�\n");

 		}
    }
    private void toggleEditor() {
    	for(int i = 0; i <viewer.getModel().getGrid().getSize(); ++i) {
    		for(int j = 0; j <viewer.getModel().getGrid().getSize(); ++j) {
    			if(viewer.getCellBorderColor(i, j) != Color.red) {
    				viewer.removeCellBorderColor(i, j);
    			}
    		}
    	}
    	viewer.toggleEditor();
		exit.setVisible(viewer.getEditor());
		resolve.setVisible(!viewer.getEditor());
		undo.setEnabled(!viewer.getEditor());
		redo.setEnabled(!viewer.getEditor());
		indice.setEnabled(!viewer.getEditor());
		menu.MAP_JITEM.get(Item.UNDO).setEnabled(!viewer.getEditor());
		menu.MAP_JITEM.get(Item.NEW).setEnabled(!viewer.getEditor());
		menu.MAP_JITEM.get(Item.REDO).setEnabled(!viewer.getEditor());
		menu.MAP_JITEM.get(Item.INDICE).setEnabled(!viewer.getEditor());
		menu.MAP_JITEM.get(Item.RESOLVE).setEnabled(!viewer.getEditor());
		menu.MAP_JITEM.get(Item.RESOLVEINDICE).setEnabled(!viewer.getEditor());
		menu.MAP_JITEM.get(Item.LEVEL).setEnabled(!viewer.getEditor());
		menu.MAP_JITEM.get(Item.EDITOR).setText(viewer.getEditor() ? "Quitter l'edition" : "Editer la grille"); 
		viewer.getModel().getGrid().clearHistory();
    }
// POINT D'ENTREE
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Window().display();
            }
        });
    }
}
