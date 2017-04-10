package cmu.cs.chess.search;

import java.util.List;

import cmu.cs.chess.board.Board;
import cmu.cs.chess.board.Move;

/**
 * @author jzhou & cliu
 *
 */
public class IterDeepenSearch<M extends Move<M>, B extends Board<M, B>> extends AbstractSearcher<M, B> {

	private static final int BONUS = 10;

	public M getBestMove(B board, int myTime, int opTime) {

		timer.start(myTime, opTime);

		List<M> moves = board.generateMoves();

		if (moves.isEmpty()) {
			return null;
		}

		int alpha = -evaluator.infty();
		int beta = evaluator.infty();

		M maxM = moves.get(0);
		M lastBestMove = moves.get(0);

		int depth = maxDepth;

		outer: for (int aDepth = 1; aDepth <= depth; aDepth++) {

			if (timer.timeup()) {
				break outer;
			}

			board.applyMove(lastBestMove);
			int oldAlpha = alpha;
			int newAlpha = -1 * negamaxAB(board, -1 * beta, -1 * alpha, aDepth - 1);
			if (newAlpha > oldAlpha) {
				alpha = newAlpha;
				maxM = lastBestMove;
			} else {
				alpha = oldAlpha;
			}
			board.undoMove();

			inner: for (M move : moves) {

				if (move.equals(lastBestMove)) {
					continue inner;
				}

				board.applyMove(move);
				oldAlpha = alpha;
				newAlpha = -1 * negamaxAB(board, -1 * beta, -1 * alpha, aDepth - 1);
				if (newAlpha > oldAlpha) {
					maxM = move;
					alpha = newAlpha;
				} else {
					alpha = oldAlpha;
				}
				board.undoMove();
			}

			lastBestMove = maxM;

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
}
