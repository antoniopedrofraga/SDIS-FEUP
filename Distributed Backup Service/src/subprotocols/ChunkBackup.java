package subprotocols;

import java.io.IOException;

import messages.Header;
import messages.Message;
import peers.Peer;

public class ChunkBackup {
	private Peer originalPeer;
	private Message message;
	public ChunkBackup(Peer originalPeer, Header header, byte[] body) {
		this.originalPeer = originalPeer;
		this.message = new Message(originalPeer.getMdbSocket(), originalPeer.getMdbAddress(), header, body);
	}
	
	public void sendChunk() {
		new Thread(this.message).start();
	}

	public void listenReplications() throws IOException {
		String reply = originalPeer.rcvMultiCastData(originalPeer.getMcSocket(), originalPeer.getMcAddress());
	}
}
