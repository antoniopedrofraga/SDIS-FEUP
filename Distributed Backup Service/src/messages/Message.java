package messages;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utilities.Constants;

public class Message implements Runnable {
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
	

	
	public static String[] splitArgs(String message) {
		return message.split("\\s+");
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
}
