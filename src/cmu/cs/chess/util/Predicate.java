package cmu.cs.chess.util;

/**
 * A functor that serves as a predicate for some type.
 */
public interface Predicate<T> {
	public boolean check(T t);
}
