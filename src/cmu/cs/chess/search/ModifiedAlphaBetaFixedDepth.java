package cmu.cs.chess.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cmu.cs.chess.board.ArrayBoard;
import cmu.cs.chess.board.ArrayMove;
import cmu.cs.chess.board.Board;
import cmu.cs.chess.board.Move;

/**
 * @author jzhou & cliu
 *
 */
public class ModifiedAlphaBetaFixedDepth<M extends Move<M>, B extends Board<M, B>> extends AbstractSearcher<M, B> {

	private static final int BONUS = 10;
	private B myBoard;
	private Set<Long> repetitionSet = new HashSet<Long>();
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
		timer.start(myTime, opTime);

		myBoard = board;

		List<M> moves = myBoard.generateMoves();

		if (moves.isEmpty()) {
			return null;
		} else if (moves.size() == 1) {
			return moves.get(0);
		}

		int alpha = -evaluator.infty();
		int beta = evaluator.infty();

		M maxM = null;

		// long sign = myBoard.signature();
		//
		// Object[] orderMoves;
		//
		// if (moveOrderMap.containsKey(sign)) {
		// orderMoves = moveOrderMap.get(sign);
		// } else {
		// orderMoves = moves.toArray();
		// Arrays.sort(orderMoves, comparator);
		// moveOrderMap.put(sign, orderMoves);
		// }

		long sign = myBoard.signature();

		for (int j = minDepth; j <= maxDepth; j++) {

			if (timer.timeup()) {
				break;
			}

			if (bestMoveMap.containsKey(sign)) {
				ResultWrapper rw = bestMoveMap.get(sign);
				M bestMove = rw.getBestMove();
				M exchangeMove = moves.get(0);
				M temp = exchangeMove;
				exchangeMove = bestMove;
				bestMove = temp;
			}

			for (int i = 0; i < moves.size(); i++) {

				M move = moves.get(i);

				myBoard.applyMove(move);
				int oldAlpha = alpha;

				int newAlpha;
				if (i == 0) {
					newAlpha = -1 * negamaxAB(-1 * beta, -1 * alpha, j - 1, j);
				} else {
					newAlpha = -1 * negamaxAB(-1 * (alpha + 1), -1 * alpha, j - 1, j);
					if (alpha < newAlpha && newAlpha < beta) {
						newAlpha = -1 * negamaxAB(-1 * beta, -1 * newAlpha, j - 1, j);
					}
				}

				if (newAlpha > oldAlpha) {
					maxM = move;
					alpha = newAlpha;
					reportNewBestMove(move);

					ResultWrapper result = new ResultWrapper(newAlpha, beta, move, 0);
					long sign2 = myBoard.signature();

					bestMoveMap.put(sign2, result);
				} else {
					alpha = oldAlpha;
				}

				myBoard.undoMove();

				if (alpha >= beta) {
					break;
				}
			}
		}
		//
		// bestMoveMap.clear();

		// myBoard.applyMove(maxM);
		// sign = myBoard.signature();
		// repetitionSet.add(sign);
		// myBoard.undoMove();
		return maxM;
	}

	private int negamaxAB(int alpha, int beta, int depth, int fullDepth) {

		if (timer.timeup()) {
			return evaluator.eval(myBoard);
		}

		long sign = myBoard.signature();
		// if (repetitionSet.contains(sign)) {
		// return 0;
		// }

		// when reach the depth
		if (depth == 0) {
			// return quiescentSearch(alpha, beta, 3);
			return evaluator.eval(myBoard);
		}

		List<M> moves = myBoard.generateMoves();

		// mate and stalemate cases
		if (moves.isEmpty()) {
			if (myBoard.inCheck()) {
				return -evaluator.mate() - depth * BONUS;
			} else {
				return -evaluator.stalemate();
			}
		}

		// Object[] orderMoves;
		//
		// if (moveOrderMap.containsKey(sign)) {
		// orderMoves = moveOrderMap.get(sign);
		// } else {
		// orderMoves = moves.toArray();
		// Arrays.sort(orderMoves, comparator);
		// moveOrderMap.put(sign, orderMoves);
		// }
		//
		// M move = null;
		//
		// for (int i = 0; i < orderMoves.length; i++) {

		if (bestMoveMap.containsKey(sign)) {
			ResultWrapper rw = bestMoveMap.get(sign);
			M bestMove = rw.getBestMove();
			int dest = moves.indexOf(bestMove);
			M exchangeMove = moves.get(0);
			M temp = exchangeMove;
			exchangeMove = bestMove;
			bestMove = temp;
		}

		for (int i = 0; i < moves.size(); i++) {

			M move = moves.get(i);

			myBoard.applyMove(move);
			int oldAlpha = alpha;

			int newAlpha;
			if (i == 0) {
				newAlpha = -1 * negamaxAB(-1 * beta, -1 * alpha, depth - 1, fullDepth);
			} else {
				newAlpha = -1 * negamaxAB(-1 * (alpha + 1), -1 * alpha, depth - 1, fullDepth);

				if (alpha < newAlpha && newAlpha < beta) {
					newAlpha = -1 * negamaxAB(-1 * beta, -1 * newAlpha, depth - 1, fullDepth);
				}
			}

			if (newAlpha > oldAlpha) {
				alpha = newAlpha;

				long sign2 = myBoard.signature();

				if (bestMoveMap.containsKey(sign2)) {
					ResultWrapper prev = bestMoveMap.get(sign2);
					if (prev.getDepth() > (fullDepth - depth)) {
						ResultWrapper result = new ResultWrapper(newAlpha, beta, move, fullDepth - depth);
						bestMoveMap.put(sign2, result);
					}
				} else {
					ResultWrapper result = new ResultWrapper(newAlpha, beta, move, fullDepth - depth);
					bestMoveMap.put(sign2, result);
				}
			} else {
				alpha = oldAlpha;
			}
			myBoard.undoMove();
			if (alpha >= beta) {
				return alpha;
			}
		}

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

		// long sign = myBoard.signature();
		//
		// if (bestMoveMap.containsKey(sign)) {
		// ResultWrapper rw = bestMoveMap.get(sign);
		// M bestMove = rw.getBestMove();
		// int dest = moves.indexOf(bestMove);
		// M exchangeMove = (M) moves.get(0);
		// M temp = exchangeMove;
		// exchangeMove = bestMove;
		// bestMove = temp;
		// }

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

		if (findCaptureMove) {
			return alpha;
		} else {
			return currentValue;
		}
	}

	private List<ArrayMove> generateOnlyCaptureMoves(B board) {
		ArrayBoard aBoard = (ArrayBoard) board;
		List<ArrayMove> pseudoMoves = aBoard.generatePseudoMoves();
		List<ArrayMove> captureMoves = new ArrayList<ArrayMove>(pseudoMoves.size());

		for (ArrayMove m : pseudoMoves) {

			if (aBoard.isLegalPseudoMove(m)) {
				if (m.isCapture() || m.isPromotion() || m.isEnpassant()) {
					captureMoves.add(m);
				}
			}
		}

		return captureMoves;
	}

	public static void main(String[] args) {

		// TestUtil.alphaBetaTest
		// ("2b2k2/1rqpn2B/n3p1r1/P1p1P1p1/PN6/3PP2P/1B2K3/3Rb1NR b - -",3,
		// new String[] {"e1b4, b7b4, a6b4"});

		String fen = "rnbqkbr1/pp1p1ppp/2p1p3/1N1n4/P3P3/5N1P/1PPPQPP1/R1B1KBR1 b KQ -";

		int depth = 2;

		ArrayBoard student = ArrayBoard.FACTORY.create().init(fen);

		ModifiedAlphaBetaFixedDepth<ArrayMove, ArrayBoard> ab = new ModifiedAlphaBetaFixedDepth<ArrayMove, ArrayBoard>();

		ModifiedEvaluator stu_evaluator = new ModifiedEvaluator();

		ab.setEvaluator(stu_evaluator);
		ab.setFixedDepth(depth);

		String studMove = ab.getBestMove(student, 10000, 10000).serverString().substring(0, 4);

		System.out.println(studMove);
	}
}
