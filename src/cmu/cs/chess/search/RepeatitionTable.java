package cmu.cs.chess.search;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jzhou & cliu
 *
 */
public class RepeatitionTable {

	private Map<Long, Integer> repTable;

	public RepeatitionTable() {
		repTable = new HashMap<Long, Integer>();
	}

	public int lookup(Long sign) {
		if (!repTable.containsKey(sign)) {
			return 0;
		} else {
			return repTable.get(sign);
		}
	}

	public void delta(Long sign, int delta) {
		if (!repTable.containsKey(sign)) {
			repTable.put(sign, 1);
		} else {
			int prev = repTable.get(sign);
			repTable.put(sign, prev + delta);
		}
	}
}
