package cmu.cs.chess.tests;

import org.junit.Test;

/**
 * This class tests the version of alpha beta 
 * 
 */
public class AlphaBetaTest {

	@Test (timeout = 1000)
	public void alphaBetaDepth2Test () {
		TestUtil.alphaBetaTest ("r1bq1b1r/pppkpppp/3p4/8/8/P2PP2P/1PP2PP1/RNB1KBNR b KQ -",2,
			new String[] {"e7e5"});
	}
	
	@Test (timeout = 1000)
	public void alphaBetaCheckmateAndStalemateTest () {
		TestUtil.alphaBetaTest ("K7/7r/8/2k5/8/8/1p1p4/8 b - -",3,
			new String[] {"d2d1"});
	}
	
	@Test (timeout = 1000)
	public void alphaBetaDepth2AnotherTest () {
		TestUtil.alphaBetaTest ("rnbqkbr1/pp1p1ppp/2p1p3/1N1n4/P3P3/5N1P/1PPPQPP1/R1B1KBR1 b KQ -",2,
			new String[] {"c6b5"});
	}
	
	@Test (timeout = 1000)
	public void alphaBetaDepth3Test () {
		TestUtil.alphaBetaTest ("2b2k2/1rqpn2B/n3p1r1/P1p1P1p1/PN6/3PP2P/1B2K3/3Rb1NR b - -",3,
			new String[] {"e1b4", "b7b4", "a6b4"});
	}
	
	@Test (timeout = 1000)
	public void alphaBetaDepth4Test () {
		TestUtil.alphaBetaTest ("rN3Bn1/2p2k2/pp2p2r/2P2bpp/P3Pp1P/1P1P4/N4P2/RQ1K1B1R w - -",4,
			new String[] {"f8h6"});
	}
}
