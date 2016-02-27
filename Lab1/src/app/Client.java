package app;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import utilities.Constants;

public class Client {
	private InetAddress adress;
	private int port_number;
	private String oper;
	private ArrayList<String> args;
	private DatagramSocket socket;
	
	Client(String [] args) throws UnknownHostException, SocketException {
		
		this.adress = InetAddress.getByName(args[0]);
		
		this.port_number = Integer.parseInt(args[1]);
		this.oper = args[2];
		this.args = new ArrayList<>();
		for (int i = 3; i < args.length; i++)
			this.args.add(args[i]);
		
		this.socket = new DatagramSocket();	
		
	}
	
	private void sendRequest() throws IOException {
		
		String request = "Request sent!";
		byte[] buf = request.getBytes();
		DatagramPacket packet = new DatagramPacket(buf, buf.length, this.adress, port_number);
		this.socket.send(packet);
		
	}
	
	public static void main(String [] args) throws IOException {
		
		if (args.length != 4 && args.length != 5) return;
		Client cl = new Client(args);
		cl.sendRequest();
		
	}

}
