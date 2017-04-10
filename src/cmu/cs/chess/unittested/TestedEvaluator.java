package cmu.cs.chess.unittested;

import java.util.Iterator;

import cmu.cs.chess.board.ArrayBoard;
import cmu.cs.chess.board.ArrayPiece;
import cmu.cs.chess.evaluation.Evaluator;

public class TestedEvaluator implements Evaluator<ArrayBoard> {

	private static final int INFINITY = 1000000;
	private static final int MATE = 300000;
	private static final int STALEMATE = 0;

	public int infty() {
		return INFINITY;
	}

	public int mate() {
		return MATE;
	}

	public int stalemate() {
		return STALEMATE;
	}

	public int eval(ArrayBoard board) {
		int currentColor = board.toPlay();

		int nextColor;

		if (currentColor == ArrayBoard.WHITE) {
			nextColor = ArrayBoard.BLACK;
		} else {
			nextColor = ArrayBoard.WHITE;
		}

		int currentPlayerSum = getOnePlayerEval(board, currentColor);
		int nextPlayerSum = getOnePlayerEval(board, nextColor);
		
		return currentPlayerSum - nextPlayerSum;
	}

	private int getOnePlayerEval(ArrayBoard board, int currentColor) {
		Iterator<ArrayPiece> currentColorIter = board
				.allPiecesOfColor(currentColor);

		int total = 0;
		
		while (currentColorIter.hasNext()) {
			int valueOfArrayPiece = 0;
			ArrayPiece currentPiece = currentColorIter.next();
			int type = currentPiece.type();
			int col = currentPiece.col();
			
			int row;
			
			if (currentColor == ArrayBoard.WHITE)
			{
				row = currentPiece.row();
			}
			else
			{
				row = 7 - currentPiece.row();
			}
			
			int typeBase = 0;

			// Knight
			if (type == ArrayPiece.KNIGHT) {
				typeBase = knightval;

				int positionValue = knightpos[row][col];

				valueOfArrayPiece = typeBase + positionValue;
			} else if (type == ArrayPiece.BISHOP) // Bishop
			{
				typeBase = bishopval;

				int positionValue = bishoppos[row][col];

				valueOfArrayPiece = typeBase + positionValue;
			} else if (type == ArrayPiece.ROOK) // Rook
			{
				typeBase = rookval;
				valueOfArrayPiece = typeBase;

			} else if (type == ArrayPiece.QUEEN) // Queen
			{
				typeBase = queenval;
				valueOfArrayPiece = typeBase;
			} else if (type == ArrayPiece.KING) // King
			{
				typeBase = kingval;
				valueOfArrayPiece = typeBase;
			} else if (type == ArrayPiece.PAWN) // Pawn
			{
				typeBase = pawnval;
				int positionValue = pawnpos[row][col];
				valueOfArrayPiece = typeBase + positionValue;
			}
			else {
				throw new IllegalArgumentException();
			}

			total += valueOfArrayPiece;
		}
		
		if (board.hasCastled[currentColor]) {
			total += CASTLE_BONUS;
		}
		
		return total;
	}

	/*
	 * Piece value tables modify the value of each piece according to where it
	 * is on the board.
	 * 
	 * To orient these tables, each row of 8 represents one row (rank) of the
	 * chessboard.
	 * 
	 * !!! The first row is where white's pieces start !!!
	 * 
	 * So, for example having a pawn at d2 is worth -5 for white. Having it at
	 * d7 is worth 20. Note that these have to be flipped over to evaluate
	 * black's pawns since pawn values are not symmetric.
	 */
	private static int bishoppos[][] = { { -5, -5, -5, -5, -5, -5, -5, -5 },
			{ -5, 10, 5, 8, 8, 5, 10, -5 }, { -5, 5, 3, 8, 8, 3, 5, -5 },
			{ -5, 3, 10, 3, 3, 10, 3, -5 }, { -5, 3, 10, 3, 3, 10, 3, -5 },
			{ -5, 5, 3, 8, 8, 3, 5, -5 }, { -5, 10, 5, 8, 8, 5, 10, -5 },
			{ -5, -5, -5, -5, -5, -5, -5, -5 } };
	private static int knightpos[][] = { { -10, -5, -5, -5, -5, -5, -5, -10 },
			{ -8, 0, 0, 3, 3, 0, 0, -8 }, { -8, 0, 10, 8, 8, 10, 0, -8 },
			{ -8, 0, 8, 10, 10, 8, 0, -8 }, { -8, 0, 8, 10, 10, 8, 0, -8 },
			{ -8, 0, 10, 8, 8, 10, 0, -8 }, { -8, 0, 0, 3, 3, 0, 0, -8 },
			{ -10, -5, -5, -5, -5, -5, -5, -10 } };
	private static int pawnpos[][] = { { 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, -5, -5, 0, 0, 0 }, { 0, 2, 3, 4, 4, 3, 2, 0 },
			{ 0, 4, 6, 10, 10, 6, 4, 0 }, { 0, 6, 9, 10, 10, 9, 6, 0 },
			{ 4, 8, 12, 16, 16, 12, 8, 4 }, { 5, 10, 15, 20, 20, 15, 10, 5 },
			{ 0, 0, 0, 0, 0, 0, 0, 0 } };

	/* Material value of a piece */
	private static final int kingval = 350;
	private static final int queenval = 900;
	private static final int rookval = 500;
	private static final int bishopval = 300;
	private static final int knightval = 300;
	private static final int pawnval = 100;
	// private static final int emptyval = 0;

	/* The bonus for castling */
	private static final int CASTLE_BONUS = 10;
	
	/*
	 * Test purpose only.
	 */
	public static void main(String[] args)
	{
		TestedEvaluator stu_evaluator = new TestedEvaluator();
		String fen = "rn1k1bnr/p1q1p1p1/1pp2p2/P2p3p/2PP2b1/5PQ1/1P2P1PP/RNB1KBNR w KQ -";
		ArrayBoard student = ArrayBoard.FACTORY.create().init(fen);
		
		int value = stu_evaluator.eval(student);
	}
}
