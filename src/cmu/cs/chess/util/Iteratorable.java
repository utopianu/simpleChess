package cmu.cs.chess.util;

import java.util.Iterator;

/**
 * An Iterator that is Iterable over itself
 */
public interface Iteratorable<E> extends Iterator<E>, Iterable<E> {
	// nothing
}
