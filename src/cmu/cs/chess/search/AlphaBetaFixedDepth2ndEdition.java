package cmu.cs.chess.search;

import cmu.cs.chess.board.ArrayBoard;
import cmu.cs.chess.board.ArrayMove;
import cmu.cs.chess.board.Board;
import cmu.cs.chess.board.Move;
import cmu.cs.chess.search.AbstractSearcher;

import java.util.HashMap;
import java.util.List;

/**
 * @author ciu
 *
 */
public class AlphaBetaFixedDepth2ndEdition<M extends Move<M>, B extends Board<M, B>> extends AbstractSearcher<M, B> {

	private static final int BONUS = 10;
	private B myBoard;
	private static int QDepth;
	private RepeatitionTable repetition = new RepeatitionTable();

	private HashMap<Long, Object[]> moveOrderMap = new HashMap<Long, Object[]>();
	private OrderMovesComparator comparator = new OrderMovesComparator();
	private HashMap<Long, ResultWrapper> bestMoveMap = new HashMap<Long, ResultWrapper>();

	private class ResultWrapper {

		private int alpha;
		private int beta;
		private M bestMove;
		private int depth;

		ResultWrapper(int aAlpha, int aBeta, M aBestMove, int aDepth) {
			alpha = aAlpha;
			beta = aBeta;
			bestMove = aBestMove;
			depth = aDepth;
		}

		public int getDepth() {
			return depth;
		}

		public void setDepth(int aDepth) {
			this.depth = aDepth;
		}

		public int getAlpha() {
			return alpha;
		}

		public void setAlpha(int alpha) {
			this.alpha = alpha;
		}

		public int getBeta() {
			return beta;
		}

		public void setBeta(int beta) {
			this.beta = beta;
		}

		public M getBestMove() {
			return bestMove;
		}

		public void setBestMove(M bestMove) {
			this.bestMove = bestMove;
		}

	}

	public M getBestMove(B board, int myTime, int opTime) {
		List<M> moves = board.generateMoves();
		myBoard = board;
		if (moves.isEmpty()) {
			return null;
		}

		int alpha = -evaluator.infty();
		int beta = evaluator.infty();

		M maxM = null;
		int depth;

		int plyCount = board.plyCount();
		int pieceCount = ((ArrayBoard) board).countOfAllPieces();

		if (plyCount < 20) {
			depth = minDepth + 1;
			QDepth = 8;
		} else {
			if (myTime < 30000) {
				depth = minDepth;
				if (pieceCount > 32) {
					QDepth = 2;
				} else if (pieceCount > 27) {
					QDepth = 3;
				} else if (pieceCount > 21) {
					QDepth = 4;
				} else if (pieceCount > 16) {
					QDepth = 5;
				} else if (pieceCount > 10) {
					QDepth = 6;
				} else {
					QDepth = 7;
				}
			} else if (myTime < 60000) {
				depth = minDepth + 1;
				if (pieceCount > 32) {
					QDepth = 3;
				} else if (pieceCount > 27) {
					QDepth = 4;
				} else if (pieceCount > 21) {
					QDepth = 5;
				} else if (pieceCount > 16) {
					QDepth = 6;
				} else if (pieceCount > 10) {
					QDepth = 7;
				} else {
					QDepth = 8;
				}
			} else if (myTime < 120000) {
				depth = (maxDepth + minDepth) / 2;
				if (pieceCount > 32) {
					QDepth = 4;
				} else if (pieceCount > 27) {
					QDepth = 5;
				} else if (pieceCount > 21) {
					QDepth = 6;
				} else if (pieceCount > 16) {
					QDepth = 7;
				} else if (pieceCount > 10) {
					QDepth = 8;
				} else {
					QDepth = 9;
				}
			} else if (myTime < 180000) {
				depth = maxDepth;
				if (pieceCount > 32) {
					QDepth = 4;
				} else if (pieceCount > 27) {
					QDepth = 5;
				} else if (pieceCount > 21) {
					QDepth = 6;
				} else if (pieceCount > 16) {
					QDepth = 7;
				} else if (pieceCount > 10) {
					QDepth = 8;
				} else {
					QDepth = 9;
				}
			} else {
				depth = maxDepth;
				if (pieceCount > 32) {
					QDepth = 4;
				} else if (pieceCount > 27) {
					QDepth = 5;
				} else if (pieceCount > 21) {
					QDepth = 6;
				} else if (pieceCount > 16) {
					QDepth = 7;
				} else if (pieceCount > 10) {
					QDepth = 8;
				} else {
					QDepth = 9;
				}
			}
		}

		// long sign = myBoard.signature();
		//
		// repetition.delta(sign, 1);

		for (int i = 0; i < moves.size(); i++) {

			M move = moves.get(i);

			myBoard.applyMove(move);
			int oldAlpha = alpha;

			int newAlpha;
			if (i == 0) {
				newAlpha = -1 * negamaxAB(board, -1 * beta, -1 * alpha, depth - 1);
			} else {
				newAlpha = -1 * negamaxAB(board, -1 * (alpha + 1), -1 * alpha, depth - 1);
				if (alpha < newAlpha && newAlpha < beta) {
					newAlpha = -1 * negamaxAB(board, -1 * beta, -1 * newAlpha, depth - 1);
				}
			}
			if (newAlpha > oldAlpha) {
				maxM = move;
				alpha = newAlpha;
			} else {
				alpha = oldAlpha;
			}
			board.undoMove();
		}

		// repetition.delta(sign, -1);

		return maxM;

	}

	private int negamaxAB(B board, int alpha, int beta, int depth) {

		// when reach the depth
		if (depth == 0) {
			// return evaluator.eval(board);
			return quiescentSearch(alpha, beta, QDepth);
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

		for (int i = 0; i < moves.size(); i++) {

			M move = moves.get(i);
			board.applyMove(move);

			int newAlpha;
			if (i == 0) {
				newAlpha = -1 * negamaxAB(board, -1 * beta, -1 * alpha, depth - 1);
			} else {
				newAlpha = -1 * negamaxAB(board, -1 * (alpha + 1), -1 * alpha, depth - 1);
				if (alpha < newAlpha && newAlpha < beta) {
					newAlpha = -1 * negamaxAB(board, -1 * beta, -1 * newAlpha, depth - 1);
				}
			}
			if (newAlpha > alpha) {
				alpha = newAlpha;
			}

			board.undoMove();

			if (alpha >= beta) {
				return alpha;
			}
		}

		// repetition.delta(sign, -1);

		return alpha;
	}

	public int quiescentSearch(int alpha, int beta, int depth) {

		int currentValue = evaluator.eval(myBoard);

		if (depth == 0) {
			return currentValue;
		}

		if (currentValue > alpha) {
			alpha = currentValue;
		}

		if (alpha >= beta) {
			return alpha;
		}

		List<M> moves = myBoard.generateMoves();
		;

		if (timer.timeup()) {
			return currentValue;
		}

		if (moves.isEmpty()) {
			return currentValue;
		}

		boolean findCaptureMove = false;
		for (int i = 0; i < moves.size(); i++) {

			M move = (M) moves.get(i);

			ArrayMove moveArray = (ArrayMove) move;
			if (myBoard.inCheck() || moveArray.isCapture() || moveArray.isEnpassant() || moveArray.isPromotion()) {
				findCaptureMove = true;

				myBoard.applyMove(move);
				alpha = Math.max(alpha, -1 * quiescentSearch(-1 * beta, -1 * alpha, depth - 1));
				myBoard.undoMove();

				if (alpha >= beta) {
					return alpha;
				}
			}
		}

		// repetition.delta(sign, -1);

		if (findCaptureMove) {
			return alpha;
		} else {
			return currentValue;
		}
	}

	public static void main(String[] args) {

	}

}
