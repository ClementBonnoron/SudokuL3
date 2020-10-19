package graphic;

import java.util.EnumMap;
import java.util.Map;

enum Menu {
    FILE("Fichier"),
    EDIT("Édition"),
	RESOLUTION("Résolution"),
	HELP("Aide");
    
    private String label;

    Menu(String lb) {
        label = lb;
    }

    public String getLabel() {
        return label;
    }
    
    static final Map<Menu, Item[]> MENU_STRUCT;

    static {
        MENU_STRUCT = new EnumMap<Menu, Item[]>(Menu.class);
        MENU_STRUCT.put(Menu.FILE, new Item[] {
                Item.SAVE, Item.OPEN, Item.NEW,
                Item.SCREENSHOT, 
                });
        MENU_STRUCT.put(Menu.EDIT, new Item[] {
        		Item.EDITOR, Item.UNDO, Item.REDO});
        MENU_STRUCT.put(Menu.RESOLUTION, new Item[] { 
        		Item.INDICE,Item.RESOLVEINDICE,  Item.RESOLVE, Item.LEVEL});
        MENU_STRUCT.put(Menu.HELP, new Item[] { 
        		Item.RULES, Item.SHORTCUT});
    }

}
