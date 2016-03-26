package subprotocols;

import messages.Header;
import messages.Message;

public class ChunkRestore {
	Message message;
	public ChunkRestore(Header header) {
		this.message = new Message(peers.Peer.getMcChannel().getSocket(), peers.Peer.getMcChannel().getAddress(), header, null);
	}
	public void sendMessage() {
		new Thread(this.message).start();
	}

}
