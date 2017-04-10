package cmu.cs.chess.search;

import java.util.List;

import cmu.cs.chess.board.Board;
import cmu.cs.chess.board.Move;

/**
 * 
 * @author jzhou & cliu
 *
 */
public class QSearch<M extends Move<M>, B extends Board<M, B>> extends AbstractSearcher<M, B> {

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

		if (depth == 0) {

			for (M move : moves) {
				board.applyMove(move);
				alpha = Math.max(alpha, -1 * quiescentNegamaxAB(board, -1 * beta, -1 * alpha, 5));
				board.undoMove();

				if (alpha >= beta) {
					return alpha;
				}

				if (timer.timeup()) {
					return alpha;
				}
			}

		} else {
			for (M move : moves) {
				board.applyMove(move);
				alpha = Math.max(alpha, -1 * negamaxAB(board, -1 * beta, -1 * alpha, depth - 1));
				board.undoMove();

				if (alpha >= beta) {
					return alpha;
				}
			}
		}

		return alpha;
	}

	private int quiescentNegamaxAB(B board, int alpha, int beta, int newDepth) {

		List<M> moves = board.generateMoves();

		if (moves.isEmpty()) {
			if (board.inCheck()) {
				return -evaluator.mate() - newDepth * BONUS;
			} else {
				return -evaluator.stalemate();
			}
		}

		// boolean isInCheck = board.inCheck();
		boolean hasCaptureMove = false;

		int v = evaluator.eval(board);

		if (v > alpha) {
			alpha = v;
		}

		if (alpha >= beta) {
			return alpha;
		}

		for (M move : moves) {

			if (move.isCapture() || move.isPromotion()) {
				hasCaptureMove = true;
				board.applyMove(move);
				alpha = Math.max(alpha, -1 * quiescentNegamaxAB(board, -1 * beta, -1 * alpha, newDepth - 1));
				board.undoMove();

				if (alpha >= beta) {
					return alpha;
				}
			}
		}

		if (!hasCaptureMove) {
			return v;
		}

		return alpha;
	}
}
