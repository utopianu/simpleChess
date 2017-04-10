package cmu.cs.chess.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import cmu.cs.chess.board.ArrayBoard;
import cmu.cs.chess.board.ArrayMove;
import cmu.cs.chess.unittested.TestedAlphaBetaFixedDepth;
import cmu.cs.chess.unittested.TestedEvaluator;

public class TestUtil {

	private static final TestedEvaluator stu_evaluator = new TestedEvaluator();
	
	public static void evaluatorTest(String fen, int value) {
		ArrayBoard student = ArrayBoard.FACTORY.create().init(fen);
		assertEquals(
			"Evaluation of boards not equal (" + fen + ")",
			value,
			stu_evaluator.eval(student)
		);
	}

	public static void alphaBetaTest(String fen, int depth, String[] validMoves) {
		ArrayBoard
			student = ArrayBoard.FACTORY.create().init(fen);
		TestedAlphaBetaFixedDepth<ArrayMove, ArrayBoard>
			ab = new TestedAlphaBetaFixedDepth<ArrayMove, ArrayBoard>();
		
		ab.setEvaluator(stu_evaluator);
		ab.setFixedDepth(depth);
		
		String studMove = ab.getBestMove(student, 10000, 10000).serverString().substring(0,4);

		List<String> validMoveList = Arrays.asList(validMoves);

		assertTrue(student.toString() + "\n\nMove returned by depth " + depth + " search on [" + fen + "] was " + studMove
				+ " but we expected something from " + validMoveList, validMoveList.contains(studMove));
	}
}
