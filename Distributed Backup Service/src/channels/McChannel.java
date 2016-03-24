package channels;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import messages.Header;
import messages.Message;
import peers.Peer;
import utilities.Constants;

public class McChannel extends Channel{
	
	public McChannel(String mcAddress, String mcPort) throws IOException {
		super(mcAddress, mcPort);
		this.thread = new MulticastThread();
	}
	
	public class MulticastThread extends Thread {
		public void run() {
			while(true) {
				System.out.println("Listening the MC channel...");
				try {
					socket.joinGroup(address);
					String data = Peer.rcvMultiCastData(socket, address);
					String[] splittedMsg = Message.splitArgs(data);
					if(!Peer.getServerId().equals(splittedMsg[Constants.SENDER_ID])) {
						Header header = new Header(Message.STORED, splittedMsg[Constants.VERSION],
								Peer.getServerId(), splittedMsg[Constants.FILE_ID], splittedMsg[Constants.CHUNK_NO], null);
						Message reply = new Message(Peer.getMdrChannel().getSocket(), Peer.getMdrChannel().getAddress(), header, null);
						int timeout = ThreadLocalRandom.current().nextInt(0, 400);
						Thread.sleep(timeout);
						new Thread(reply).start();
					}
					socket.leaveGroup(address);
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}