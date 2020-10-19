package model;

/**
 * Définit une coordonnée contenant une valeur en x, et une valeur en y
 * @author cleme
 * @inv
 * 		getX() >= 0
 * 		getY() >= 0
 * 		toString().compareTo("(" + getX() + ";" + ")") == 0
 * @constructor
 * 		$DESC$ Renvoie une coordonnée.
 *    	$ARGS$ int x, int y
 *     	$PRE$
 *      	x >= 0
 *      	y >= 0
 *     	$POST$
 *     		getX() == x
 *     		getY() == y
 */
public interface BoundedCoordinate {
	
	/**
	 * Renvoie la valeur en x de cette cordonnée.
	 */
	int getX();

	/**
	 * Renvoie la valeur en y de cette cordonnée.
	 */
	int getY();
	
	/**
	 * Renvoie une chaîne décrivant cette coordonnée.
	 * @return "(" + getX() + ";" + getY() + ")"
	 */
	String toString();
}
