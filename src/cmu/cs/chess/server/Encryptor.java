package cmu.cs.chess.server;

public final class Encryptor {

	public void encrypt_string(byte[] s, int len) {
		if (!Hub.boolTimestampPossible)
			return; // do nothing
	}

	@SuppressWarnings("deprecation")
	public byte[] applyTimestamp(String acmd) {
	    //		if (!Hub.boolTimestampPossible) {
			byte[] ts = new byte[acmd.length()];
			acmd.getBytes(0, ts.length, ts, 0); // this only takes lower order bits
			return ts;
            //		}
	}
}
