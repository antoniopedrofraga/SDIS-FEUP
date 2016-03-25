package subprotocols;


import database.FileInfo;
import peers.Peer;

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
		for (int i = 0; i < numberOfChunks; i++) {
			
		}
	}
	
	public void run() {
		restore();
	}
}