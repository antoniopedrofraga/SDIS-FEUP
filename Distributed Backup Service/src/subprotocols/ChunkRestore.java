package subprotocols;

import messages.Header;
import messages.Message;

public class ChunkRestore extends Thread {
	Message message;
	public ChunkRestore(Header header) {
		this.message = new Message(peers.Peer.getMdbChannel().getSocket(), peers.Peer.getMdbChannel().getAddress(), header, null);
	}

	@Override
	public void run() {
		
	}

}
