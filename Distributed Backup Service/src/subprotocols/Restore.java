package subprotocols;


import database.FileInfo;
import messages.Header;
import messages.Message;
import peers.Peer;
import utilities.Constants;

public class Restore extends Thread {
	String fileName;
	public Restore(String fileName) {
		this.fileName = fileName;
	}
	
	public void restore() {
		if (!Peer.getStorage().getBackedUpFiles().containsKey(fileName)) {
			System.out.println("This file '" + fileName + "' was not backed up yet");
			return;
		}
		
		FileInfo fileInfo = Peer.getStorage().getBackedUpFiles().get(fileName);
		int numberOfChunks = fileInfo.getNumberOfChunks();
		Header header = new Header(Message.GETCHUNK, Constants.PROTOCOL_VERSION, Peer.getServerId(), fileInfo.getFileId(), "0", null);
		
		for (int i = 0; i < numberOfChunks; i++) {
			header.setChunkNo("" + i);
			new ChunkRestore(header).start();
		}
	}
	
	public void run() {
		restore();
	}
}
