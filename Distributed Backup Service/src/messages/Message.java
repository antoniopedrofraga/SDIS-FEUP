package messages;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import utilities.Constants;

public class Message implements Runnable {
	
	public static final String TEST = "TEST";
	public static final String PUTCHUNK = "PUTCHUNK";
	public static final String STORED = "STORED";
	public static final String GETCHUNK = "GETCHUNK";
	public static final String CHUNK = "CHUNK";
	public static final String DELETE = "DELETE";
	
	
	private MulticastSocket socket;
	private InetAddress address;
	private Header header;
	private byte[] body;

	
	public Message(MulticastSocket socket, InetAddress address, Header header, byte[] body) {
		this.socket = socket;
		this.address = address;
		this.header = header;
		this.body = body;
	}
	

	
	public Message(Header header, byte[] body) {
		this.header = header;
		this.body = body;
		this.socket = null;
		this.address = null;
	}



	public static String[] splitArgs(String message) {
		return message.split(" ");
	}

	@Override
	public void run() {
		byte[] headerBytes = header.toString().getBytes();
		byte[] message = {};
		
		if (body != null) {
			message = new byte[headerBytes.length + body.length];
			System.arraycopy(headerBytes, 0, message, 0, headerBytes.length);
			System.arraycopy(body, 0, message, headerBytes.length, body.length);
		} else {
			message = headerBytes;
		}
		
		DatagramPacket packet = new DatagramPacket(message,
				message.length, address,
				socket.getLocalPort());
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static Message getMessageFromData(String data) {
		String[] headerAndBody = data.split(Constants.CRLF + Constants.CRLF);
		String headerStr = headerAndBody[0];
		byte[] body = headerAndBody.length == 2 ? headerAndBody[1].getBytes() : null;
		String[] splittedHeader = Message.splitArgs(headerStr);
		String replicationDeg = splittedHeader.length > Constants.REPLICATION_DEG ? splittedHeader[Constants.REPLICATION_DEG] : null;
		Header header = new Header(splittedHeader[Constants.MESSAGE_TYPE], splittedHeader[Constants.VERSION], splittedHeader[Constants.SENDER_ID],
				splittedHeader[Constants.FILE_ID], splittedHeader[Constants.CHUNK_NO], replicationDeg);
		Message msg = new Message(header, body);
		return msg;
	}


	public Header getHeader() {
		return header;
	}
	public byte[] getBody() {
		return body;
	}

}
