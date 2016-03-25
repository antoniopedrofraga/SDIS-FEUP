package channels;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import messages.Header;
import messages.Message;
import peers.Peer;
import utilities.Constants;


public class MdrChannel extends Channel{

	public MdrChannel(String mdrAddress, String mdrPort) throws IOException {
		super(mdrAddress, mdrPort);
		this.thread = new MdrThread();
	}
	
	public void listen() {
		this.thread.start();
	}
	
	public class MdrThread extends Thread {
		public void run() {
			System.out.println("Listening the MDR channel...");
			while(true) {
				try {
					socket.joinGroup(address);
					String data = rcvMultiCastData();
					String[] splittedMsg = Message.splitArgs(data);
					if(!Peer.getServerId().equals(splittedMsg[Constants.SENDER_ID])) {
						Header header = new Header(Message.STORED, splittedMsg[Constants.VERSION],
								Peer.getServerId(), splittedMsg[Constants.FILE_ID], splittedMsg[Constants.CHUNK_NO], null);
						Message reply = new Message(Peer.getMcChannel().getSocket(), Peer.getMcChannel().getAddress(), header, null);
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
