package cmu.cs.chess.engine;

import java.util.Observer;

import cmu.cs.chess.board.ArrayBoard;
import cmu.cs.chess.board.ArrayMove;
import cmu.cs.chess.board.Board;
import cmu.cs.chess.search.AlphaBetaFixedDepth2ndEdition;
import cmu.cs.chess.search.ModifiedAlphaBetaFixedDepth;
import cmu.cs.chess.search.MyTimer;
import cmu.cs.chess.search.NoTimer;
import cmu.cs.chess.search.Searcher;
import cmu.cs.chess.search.SimpleTimer;
import cmu.cs.chess.search.Timer;
import cmu.cs.chess.server.Hub;
import cmu.cs.chess.unittested.TestedAlphaBetaFixedDepth;
import cmu.cs.chess.unittested.TestedEvaluator;


/**
 * @author cliu
 *
 */
public class Engine
{

	private ArrayBoard
		board    = ArrayBoard.FACTORY.create().init(Board.STARTING_POSITION);

	
	private Searcher <ArrayMove,ArrayBoard> 
	searcher = new AlphaBetaFixedDepth2ndEdition<ArrayMove,ArrayBoard>();
	
	private TestedEvaluator	  eval = new TestedEvaluator();
	  
	
	private int
	  plyCount = 0;
	
	public String getName()
	{
		return "Lion Heart";
	}

	public Engine(int time, int inc)
	{
//		searcher.setFixedDepth(3);
		searcher.setMinDepth(2);
		searcher.setMaxDepth(4);
		searcher.setEvaluator(eval);
//		Timer timer = new NoTimer();
		Timer timer = new SimpleTimer(time, inc);
		searcher.setTimer(timer);
	}

	/**
	 * Converts the string representation of a move into a move
	 * and then applies it to the current board.
	 * 
	 * @param m the move string.
	 */
	public void applyMove(String m)
	{
	  if( board.plyCount() != plyCount++ )
	  {
	    throw new IllegalStateException(
	      "Did you forget to call undoMove() somewhere?"
	    );
	  }
//	  	((MySearch<ArrayMove,ArrayBoard>)searcher).delta(board, 1);
		board.applyMove(board.createMoveFromString(m));
	}

	/**
	 * Return the player's board state
	 */
	public ArrayBoard getBoard()
	{
		return board;
	}

	/**
	 * Compute and return a move in the current position.
	 * 
	 * The returned move must be in the String format accepted
	 * by the server.
	 * 
	 * @param myTime number of seconds left on the player's clock
	 * @param opTime number of seconds left on the opponent's clock
	 */
	public ArrayMove computeMove(int myTime, int opTime)
	{
	  assert(false) : "Assertions should be disabled when playing competitively.";
	  

    System.out.println(eval.eval(board));
	  
		ArrayMove move = searcher.getBestMove(getBoard(), myTime, opTime);
		
		
		return move;
	}

	/* These are for operating with the EasyChess GUI. */
		
	public Hub theHub;

	public Engine(Hub h, int time, int inc)
	{
		this(time, inc);
		theHub = h;
	}
	
	// This can be expanded so that the Observer is notified of other
	// events as well.
	/**
	 * Adds an Observer to the Searcher so that when a new best move
	 * is found, the Observer will be notified. 
	 * @param o the new Observer
	 */
	public void addBestMoveObserver(Observer o)
	{
		searcher.addBestMoveObserver(o);
	}
}
