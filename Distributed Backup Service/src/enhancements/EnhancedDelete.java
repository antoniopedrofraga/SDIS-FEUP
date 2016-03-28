package enhancements;

import java.util.ArrayList;

import data.Data;
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
		while (Data.getChunksBackedUp().get(fileId).size() > 0) {
			Header deleteHeader = new Header(Message.DELETE, Constants.ENHANCED_DELETE_VERSION, Peer.getServerId(), fileId, null, null);
			Message deleteMsg = new Message(Peer.getMcChannel().getSocket(), Peer.getMcChannel().getAddress(), deleteHeader, null);
			new Thread(deleteMsg).start();
			try {
				Thread.sleep(waitingTime);
			} catch (InterruptedException e) {
				System.out.println("Could not sleep while listening CHUNKDELETED messages");
			}
			ArrayList<Header> headers = Peer.getMcChannel().getDeleteConfirms();
			for (Header header : headers) {
				header.setMsgType(Message.STORED); //to match headers from storeReplies
				Data.getReceivedStoreMessages().get(fileId).remove(header);
			}
			if (waitingTime < Constants.MAX_WAITING_TIME)
				waitingTime *= 2;
		}
	}
}