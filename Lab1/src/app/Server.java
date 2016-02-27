package app;
import db.Owner;
import utilities.Constants;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.ArrayList;
import java.io.IOException;

public class Server {
	private int portNumber;
	private ArrayList<Owner> owners;
	private DatagramSocket socket;


	public Server(int portNumber) throws SocketException {
		this.portNumber = portNumber;
		this.owners =  new ArrayList<Owner>();
		this.socket = new DatagramSocket(portNumber);
	}

	private int listenRequests() throws IOException {
		boolean listening = true;
		while(listening) {
			byte[] rbuf = new byte[utilities.Constants.MAX_MSG_SIZE];
			DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);
			this.socket.receive(packet);
			String received = new String(packet.getData(), 0, packet.getLength());
			System.out.println("'" + received + "' was received.");
			
			String[] tokens = received.split(" ");
			
			String response = null;
			if(tokens[0].equals("lookup")) {
				response = lookUpFor(tokens[1]);
			} else if(tokens[0].equals("register")) {
				response = register(tokens[1], tokens[2]);
			}
			
			rbuf = response.getBytes();
			InetAddress address = packet.getAddress();
			int port = packet.getPort();
			packet = new DatagramPacket(rbuf, rbuf.length, address, port);
			socket.send(packet);
			
			for (int i = 0; i < this.owners.size(); i++)
				System.out.println(this.owners.get(i).getPlateNumber() + " - " + this.owners.get(i).getName());
		}

		return Constants.OK;
	}


	private String register(String plateNumber, String ownerName) {
		for(int i = 0; i < this.owners.size(); i++)
			if(this.owners.get(i).getPlateNumber().equals(plateNumber) && this.owners.get(i).getName().equals(ownerName)) {
				System.out.println("Error! There is an user with that name and plate number.");
				return "" + Constants.ERROR;
			}
		this.owners.add(new Owner(plateNumber, ownerName));
		System.out.println("User was created!");
		return ""  + this.owners.size();
	}

	private String lookUpFor(String plateNumber) {
		
		for(int i = 0; i < this.owners.size(); i++)
			if(this.owners.get(i).getPlateNumber().equals(plateNumber)) return this.owners.get(i).getPlateNumber() + " " + this.owners.get(i).getName();
		
		System.out.println("Could not find an user with this plate number -> " + plateNumber + ".");
		return "NOT_FOUND";
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 1) return;
		Server sv = new Server(Integer.parseInt(args[0]));
		sv.listenRequests();
	}
}
