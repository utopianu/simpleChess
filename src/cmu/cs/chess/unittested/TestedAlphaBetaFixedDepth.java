package cmu.cs.chess.unittested;

import cmu.cs.chess.board.ArrayBoard;
import cmu.cs.chess.board.ArrayMove;
import cmu.cs.chess.board.Board;
import cmu.cs.chess.board.Move;
import cmu.cs.chess.evaluation.Evaluator;
import cmu.cs.chess.search.AbstractSearcher;
import cmu.cs.chess.tests.TestUtil;

import java.util.List;

/**
 * An implementation of Alpha Beta search.
 * 
 * This is the class that will be unit tested by FrontDesk.
 */
public class TestedAlphaBetaFixedDepth<M extends Move<M>, B extends Board<M, B>> extends AbstractSearcher<M, B> {

	private static final int BONUS = 10;

	public M getBestMove(B board, int myTime, int opTime) {
		List<M> moves = board.generateMoves();

		if (moves.isEmpty()) {
			return null;
		}

		int alpha = -evaluator.infty();
		int beta = evaluator.infty();

		M maxM = null;
		int depth = maxDepth;

		for (M move : moves) {

			board.applyMove(move);
			int oldAlpha = alpha;
			int newAlpha = -1 * negamaxAB(board, -1 * beta, -1 * alpha, depth - 1);
			if (newAlpha > oldAlpha) {
				maxM = move;
				alpha = newAlpha;
			} else {
				alpha = oldAlpha;
			}
			board.undoMove();
		}

		return maxM;

	}

	private int negamaxAB(B board, int alpha, int beta, int depth) {

		// when reach the depth
		if (depth == 0) {
			return evaluator.eval(board);
		}

		List<M> moves = board.generateMoves();

		// mate and stalemate cases
		if (moves.isEmpty()) {
			if (board.inCheck()) {
				return -evaluator.mate() - depth * BONUS;
			} else {
				return -evaluator.stalemate();
			}
		}

		for (M move : moves) {
			board.applyMove(move);
			alpha = Math.max(alpha, -1 * negamaxAB(board, -1 * beta, -1 * alpha, depth - 1));
			board.undoMove();

			if (alpha >= beta) {
				return alpha;
			}
		}

		return alpha;
	}

	public static void main(String[] args) {

		String fen = "rnbqkbr1/pp1p1ppp/2p1p3/1N1n4/P3P3/5N1P/1PPPQPP1/R1B1KBR1 b KQ -";

		int depth = 2;

		ArrayBoard student = ArrayBoard.FACTORY.create().init(fen);

		TestedAlphaBetaFixedDepth<ArrayMove, ArrayBoard> ab = new TestedAlphaBetaFixedDepth<ArrayMove, ArrayBoard>();

		TestedEvaluator stu_evaluator = new TestedEvaluator();

		ab.setEvaluator(stu_evaluator);
		ab.setFixedDepth(depth);

		String studMove = ab.getBestMove(student, 10000, 10000).serverString().substring(0, 4);

		System.out.println(studMove);
	}
}
