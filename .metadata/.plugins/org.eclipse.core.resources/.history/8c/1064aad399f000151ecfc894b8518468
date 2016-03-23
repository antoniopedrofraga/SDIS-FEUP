package peers;

import exceptions.ArgsException;
import subprotocols.ChunkBackup;
import subprotocols.SubProtocol;
import utilities.Constants;

public class InitiatorPeer {
	private SubProtocol protocol;
	InitiatorPeer(SubProtocol protocol) {
		this.protocol = protocol;
	}
	
	public static void main(String[] args) throws ArgsException {
		InitiatorPeer initiatorPeer = getPeerFromArgs(args);
	}

	public static InitiatorPeer getPeerFromArgs(String[] args) throws ArgsException {
		
		String action = args[0];
		SubProtocol protocol;
		
		switch(action) {
		case Constants.BACKUP:
			if (args.length != 6)
				throw new ArgsException("PUTCHUNK subprotocol was called with " + args.length + " and it's suposed to call it with 6 arguments.");
			
			protocol = new ChunkBackup(action, args[1], args[2], args[3], args[4], args[5]);
			break;
		default:
			throw new ArgsException("Action: " + args[0] + " is unknown.");
		}
		
		return new InitiatorPeer(protocol);
	}
}
