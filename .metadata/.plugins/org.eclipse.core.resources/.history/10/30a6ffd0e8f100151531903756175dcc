package messages;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utilities.Constants;

public class Message implements Runnable {
	
	public static final String TEST = "TEST";
	public static final String PUTCHUNK = "PUTCHUNK";
	public static final String STORED = "STORED";
	public static final String RESTORE = "GETCHUNK";
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
		String[] headerAndBody = data.split(Constants.CR + Constants.LF + Constants.CR + Constants.LF + "");
		
		String headerStr = headerAndBody[0];
		byte[] body = headerAndBody[1].getBytes();
		System.out.println("header & body groups -> " + headerAndBody.length + " with data: " + headerStr);
		String[] splittedHeader = Message.splitArgs(headerStr);
		Header header = new Header(splittedHeader[Constants.MESSAGE_TYPE], splittedHeader[Constants.VERSION], splittedHeader[Constants.SENDER_ID],
				splittedHeader[Constants.FILE_ID], splittedHeader[Constants.CHUNK_NO], splittedHeader[Constants.REPLICATION_DEG]);
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
