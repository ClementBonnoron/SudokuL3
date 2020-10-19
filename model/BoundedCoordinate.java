package model;

/**
 * D�finit une coordonn�e contenant une valeur en x, et une valeur en y
 * @author cleme
 * @inv
 * 		getX() >= 0
 * 		getY() >= 0
 * 		toString().compareTo("(" + getX() + ";" + ")") == 0
 * @constructor
 * 		$DESC$ Renvoie une coordonn�e.
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
	 * Renvoie la valeur en x de cette cordonn�e.
	 */
	int getX();

	/**
	 * Renvoie la valeur en y de cette cordonn�e.
	 */
	int getY();
	
	/**
	 * Renvoie une cha�ne d�crivant cette coordonn�e.
	 * @return "(" + getX() + ";" + getY() + ")"
	 */
	String toString();
}
