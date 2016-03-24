package subprotocols;

import peers.Peer;

public class Restore extends Thread {
	String fileName;
	public Restore(String fileName) {
		this.fileName = fileName;
	}
	
	public void restore() {
		String fileId = Peer.getStorage().getStoredFileIds().get(fileName);
		if (fileId == null) {
			System.out.println("This file '" + fileName + "' was not backed up yet");
			return;
		}
		
	}
}
