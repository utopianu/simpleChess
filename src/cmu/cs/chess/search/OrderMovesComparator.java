package cmu.cs.chess.search;

import java.util.Comparator;

import cmu.cs.chess.board.ArrayMove;
import cmu.cs.chess.board.ArrayPiece;

/**
 * 
 * @author cliu
 *
 */
public class OrderMovesComparator implements Comparator {

	private int typeToValue[];

	public OrderMovesComparator() {
		typeToValue = new int[8];
		typeToValue[ArrayPiece.EMPTY] = 0;
		typeToValue[ArrayPiece.PAWN] = 100;
		typeToValue[ArrayPiece.KNIGHT] = 100;
		typeToValue[ArrayPiece.KING] = 350;
		typeToValue[ArrayPiece.BISHOP] = 300;
		typeToValue[ArrayPiece.ROOK] = 500;
		typeToValue[ArrayPiece.QUEEN] = 900;
	}

	@Override
	public int compare(Object o1, Object o2) {
		ArrayMove m1 = (ArrayMove) o1;
		ArrayMove m2 = (ArrayMove) o2;

		boolean m1Capture = m1.isCapture();
		boolean m2Capture = m2.isCapture();

		if (m1Capture && m2Capture) {
			int comp = typeToValue[m1.dest.type()] - typeToValue[m2.dest.type()];
			if (comp > 0) {
				return 1;
			} else if (comp < 0) {
				return -1;
			} else {
				return typeToValue[m1.source.type()] - typeToValue[m2.source.type()];
			}
		} else if (m1Capture) {
			return 1;
		} else if (m2Capture) {
			return -1;
		} else {
			return 0;
		}
	}

}
