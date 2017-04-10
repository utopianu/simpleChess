package cmu.cs.chess.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cmu.cs.chess.board.ArrayBoard;
import cmu.cs.chess.board.ArrayMove;
import cmu.cs.chess.board.Board;
import cmu.cs.chess.board.Move;

/**
 * @author cliu
 *
 */
public class MySearch<M extends Move<M>, B extends Board<M, B>> extends AbstractSearcher<M, B> {

	private static final int BONUS = 10;

	private Map<B, Integer> repetitionMap;
	private Map<B, Integer> evalMap;

	public MySearch() {
		repetitionMap = new HashMap<B, Integer>();
		evalMap = new HashMap<B, Integer>();
	}

	public int lookup(B b) {
		Integer result = repetitionMap.get(b);
		if (result == null) {
			B newB = b.copy();
			repetitionMap.put(newB, 1);
			return 0;
		}
		return result;
	}

	public void delta(B b, int d) {
		Integer result = repetitionMap.get(b);
		if (result == null) {
			B newB = b.copy();
			repetitionMap.put(newB, 1);
		} else {
			repetitionMap.remove(b);
			B newBoard = b.copy();
			result += d;
			repetitionMap.put(newBoard, result);
		}
	}

	class MoveWithEval {
		M move;
		int alpha;

		MoveWithEval(M m, int a) {
			move = m;
			alpha = a;
		}

		public M getMove() {
			return move;
		}

		public int getAlpha() {
			return alpha;
		}
	}

	public M getBestMove(B board, int myTime, int opTime) {
		timer.start(myTime, opTime);

		List<M> moves = board.generateMoves();

		if (moves.isEmpty()) {
			return null;
		}

		int alpha = -evaluator.infty();
		int beta = evaluator.infty();

		M maxM = null;
		// int depth = maxDepth;

		// List<M> orderedMoves = getMovingOrder(board, moves);

		int movesSize = moves.size();
		ArrayList<MoveWithEval> oldMoveEvalList = null;

		outer: for (int searchDepth = 0; searchDepth < maxDepth; searchDepth++) {

			// timer.okToTimeup();

			if (searchDepth > minDepth && timer.timeup()) {
				break outer;
			}

			// timer.notOkToTimeup();

			ArrayList<MoveWithEval> moveEvalList = new ArrayList<MoveWithEval>();

			for (int i = 0; i < movesSize; i++) {

				M move = null;
				if (searchDepth != 0) {
					move = oldMoveEvalList.get(i).getMove();
				} else {
					move = moves.get(i);
				}

				board.applyMove(move);

				int newAlpha = -1 * negamaxAB(board, -1 * beta, -1 * alpha, searchDepth);

				MoveWithEval moveEval = new MoveWithEval(move, newAlpha);
				moveEvalList.add(moveEval);

				if (newAlpha > alpha) {
					maxM = move;
					alpha = newAlpha;
				}

				board.undoMove();

				if (searchDepth > minDepth && timer.timeup()) {
					break outer;
				}
			}

			Collections.sort(moveEvalList, new Comparator<MoveWithEval>() {
				@Override
				public int compare(MoveWithEval o1, MoveWithEval o2) {
					int compareResult = o1.getAlpha() - o2.getAlpha();
					if (compareResult > 0) {
						return 1;
					} else if (compareResult < 0) {
						return -1;
					} else {
						return 0;
					}
				}
			});

			oldMoveEvalList = new ArrayList<MoveWithEval>(movesSize);

			for (int j = 0; j < moveEvalList.size(); j++) {
				oldMoveEvalList.add(moveEvalList.get(j));
			}

		}

		return maxM;

	}

	private int negamaxAB(B board, int alpha, int beta, int newDepth) {

		// reach depth 0
		if (newDepth == 0) {
			int evalResult;

			if (evalMap.size() > 2000) {
				evalMap = new HashMap<B, Integer>();
			}

			if (evalMap.containsKey(board)) {
				evalResult = evalMap.get(board);
			} else {
				evalResult = evaluator.eval(board);
				B newBoard = board.copy();
				evalMap.put(newBoard, evalResult);
			}

			return evalResult;
		}

		List<M> moves = board.generateMoves();

		// mate and stalemate
		if (moves.isEmpty()) {
			if (board.inCheck()) {
				return -evaluator.mate() - newDepth * BONUS;
			} else {
				return -evaluator.stalemate();
			}
		}

		int quiescentDepth = 5;

		for (M move : moves) {
			board.applyMove(move);
			alpha = Math.max(alpha, -1 * negamaxAB(board, -1 * beta, -1 * alpha, newDepth - 1));
			board.undoMove();

			if (alpha >= beta) {
				return alpha;
			}

			if (timer.timeup()) {
				return alpha;
			}
		}
		// }

		// delta(board, -1);

		return alpha;
	}

	private int quiescentNegamaxAB(B board, int alpha, int beta, int newDepth) {
		// reach depth 0
		if (newDepth == 0) {
			int evalResult;

			if (evalMap.size() > 2000) {
				evalMap = new HashMap<B, Integer>();
			}

			if (evalMap.containsKey(board)) {
				evalResult = evalMap.get(board);
			} else {
				evalResult = evaluator.eval(board);
				B newBoard = board.copy();
				evalMap.put(newBoard, evalResult);
			}

			return evalResult;
		}

		List<M> moves = board.generateMoves();

		if (moves.isEmpty()) {
			if (board.inCheck()) {
				return -evaluator.mate() - newDepth * BONUS;
			} else {
				return -evaluator.stalemate();
			}
		}

		// int repeatTimes = lookup(board);
		// if (repeatTimes == 2) {
		// return 0;
		// }

		// delta(board, 1);

		boolean isInCheck = board.inCheck();
		boolean hasCaptureMove = false;

		int v = evaluator.eval(board);

		if (v > alpha) {
			alpha = v;
		}

		if (alpha >= beta) {
			return alpha;
		}

		for (M move : moves) {

			if (isInCheck || move.isCapture() || move.isPromotion()) {
				hasCaptureMove = true;
				board.applyMove(move);
				alpha = Math.max(alpha, -1 * quiescentNegamaxAB(board, -1 * beta, -1 * alpha, newDepth - 1));
				board.undoMove();

				if (alpha >= beta) {
					return alpha;
				}

				if (timer.timeup()) {
					return alpha;
				}
			}
		}

		// delta(board, -1);

		return alpha;
	}

	public static void main(String[] args) {

		// TestUtil.alphaBetaTest(
		// "2b2k2/1rqpn2B/n3p1r1/P1p1P1p1/PN6/3PP2P/1B2K3/3Rb1NR b - -",
		// 3, new String[] { "e1b4, b7b4, a6b4" });

		String fen = "rnbqkbr1/pp1p1ppp/2p1p3/1N1n4/P3P3/5N1P/1PPPQPP1/R1B1KBR1 b KQ -";

		int depth = 2;

		ArrayBoard student = ArrayBoard.FACTORY.create().init(fen);

		MySearch<ArrayMove, ArrayBoard> ab = new MySearch<ArrayMove, ArrayBoard>();

		int r1 = ab.lookup(student);

		ab.delta(student, 1);

		int r2 = ab.lookup(student);

		int r3 = ab.lookup(student.copy());

		//
		// TestedEvaluator stu_evaluator = new TestedEvaluator();
		//
		// ab.setEvaluator(stu_evaluator);
		// ab.setFixedDepth(depth);
		//
		// String studMove = ab.getBestMove(student, 10000,
		// 10000).serverString()
		// .substring(0, 4);
		//
		// System.out.println(studMove);

	}
}
