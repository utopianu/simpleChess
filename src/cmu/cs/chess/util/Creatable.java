package cmu.cs.chess.util;

/**
 * A class which implements Creatable should define a public static member
 * variable called FACTORY and should initialize it as an object of said class.
 * 
 */
public interface Creatable<R extends Creatable<R>> {
	/**
	 * Returns a new object of the same type as this.
	 * 
	 * State of the returned object may not be valid.
	 * 
	 * @return a new object of the same type as this.
	 */
	public R create();

	/**
	 * Returns a deep copy of this.
	 * 
	 * @return the copy.
	 */
	public R copy();
}
