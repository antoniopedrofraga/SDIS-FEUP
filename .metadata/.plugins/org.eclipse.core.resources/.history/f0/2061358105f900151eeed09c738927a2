package enhancements;

import java.util.ArrayList;


import messages.Header;
import messages.Message;
import peers.Peer;
import utilities.Constants;

public class EnhancedDelete extends Thread {
	private String fileId;
	public EnhancedDelete (String fileId) {
		this.fileId = fileId;
	}
	public void run () {
		int waitingTime = Constants.DEFAULT_WAITING_TIME;
		while (Peer.getInstance().getStorage().getChunksBackedUp().get(fileId).size() > 0) {
			System.out.println("Sending Enhanced Delete (Chunks backed up size -> " + Peer.getInstance().getStorage().getChunksBackedUp().get(fileId).size() + ")");
			Peer.getInstance();
			Header deleteHeader = new Header(Message.DELETE, Constants.ENHANCED_DELETE_VERSION, Peer.getServerId(), fileId, null, null);
			Message deleteMsg = new Message(Peer.getInstance().getMcChannel().getSocket(), Peer.getInstance().getMcChannel().getAddress(), deleteHeader, null);
			new Thread(deleteMsg).start();
			try {
				Thread.sleep(waitingTime);
			} catch (InterruptedException e) {
				System.out.println("Could not sleep while listening CHUNKDELETED messages");
			}
			ArrayList<Header> headers = Peer.getInstance().getMcChannel().getDeleteConfirms();
			for (Header header : headers) {
				header.setMsgType(Message.STORED); //to match headers from storeReplies
				Peer.getInstance().getStorage().getReceivedStoreMessages().get(fileId).remove(header);
			}
			if (waitingTime < Constants.MAX_WAITING_TIME)
				waitingTime *= 2;
		}
	}
}
