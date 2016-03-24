package channels;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import messages.Header;
import messages.Message;
import peers.Peer;
import utilities.Constants;

public class MdbChannel extends Channel{
	
	public MdbChannel(String mdbAddress, String mdbPort) throws IOException {
		super(mdbAddress, mdbPort);
		this.thread = new MdbThread();
	}
	
	public void listen() {
		this.thread.start();
	}
	
	public void handlePutChunk(Header header, byte[] body) throws InterruptedException {
		
		//save chunk
		System.out.println("That PUTCHUNK had " + body.length + " bytes of data.");
		Long chunkNo = Long.parseLong(header.getChunkNo());
		try {
			Peer.getStorage().saveChunk(header.getFileId(), chunkNo, body);
		} catch (IOException e) {
			System.out.println("Could not save the chunk number " + header.getChunkNo() + "from file " + header.getFileId());
			return;
		}
		System.out.println("Chunk number " + header.getChunkNo() + " from file " + header.getFileId() + " was saved! Replying...");
		
		//reply
		Header replyHeader = new Header(Message.STORED, header.getVersion(),
				Peer.getServerId(), header.getFileId(), header.getChunkNo(), null);
		Message reply = new Message(Peer.getMcChannel().getSocket(), Peer.getMcChannel().getAddress(), replyHeader, null);
		int timeout = ThreadLocalRandom.current().nextInt(0, 400);
		Thread.sleep(timeout);
		new Thread(reply).start();
	}
	
	public class MdbThread extends Thread {
		public void run() {
			while(true) {
				System.out.println("Listening the MDB channel...");
				try {
					socket.joinGroup(address);
					// separate data
					String data = Peer.rcvMultiCastData(socket, address);
					Message message = Message.getMessageFromData(data);
					Header header = message.getHeader();
					byte[] body = message.getBody();
					
					//analising data
					if(!Peer.getServerId().equals(header.getSenderId())) {
						switch (header.getMsgType()) {
						case Message.PUTCHUNK:
							System.out.println("Received a PUTCHUNK message, will handle it...");
							handlePutChunk(header, body);
							break;
						default:
							System.out.println("Ignoring message from type " + header.getMsgType());
							break;
						}
					}
					socket.leaveGroup(address);
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}