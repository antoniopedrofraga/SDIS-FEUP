package subprotocols;

import messages.Header;
import messages.Message;

public class ChunkBackup extends SubProtocol{
	private Message message;
	public ChunkBackup(String action, String version, String senderId, 
			String fileId, String chunkNo, String replicationDeg) {
		super(action);
		Header header = new Header(action, version, senderId, fileId, chunkNo, replicationDeg);
		this.message = new Message(header);
		this.sendMessage();
	}


	public void sendMessage() {
		
	}
}
