package graphic;

enum Item {
    SAVE("Sauvegarder"),
    OPEN("Ouvrir"),
    NEW("Nouvelle Partie"),
    SCREENSHOT("Capture d'écran"),
    UNDO("Undo"),
    REDO("Redo"),
    INDICE("Indice"),
    RESOLVEINDICE("Résoudre l'indice"),
	RESOLVE("Résoudre la grille"),
	RULES("Règles Sudoku"),
	EDITOR("Editer grille"),
	SHORTCUT("Raccourcis"),
	LEVEL("Difficulté de la grille");
	
    private String label;

    Item(String lb) {
        label = lb;
    }
    
    public String getLabel() {
        return label;
    }
}
