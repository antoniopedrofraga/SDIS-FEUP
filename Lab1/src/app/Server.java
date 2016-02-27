package app;
import db.Owner;
import utilities.Constants;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.Vector;
import java.io.IOException;

public class Server {
	private int portNumber;
	private Vector<Owner> owners;
	private DatagramSocket socket;


	public Server(int portNumber) throws SocketException {
		this.portNumber = portNumber;
		this.owners =  new Vector<Owner>();
		this.socket = new DatagramSocket(portNumber);
	}

	private int listenRequests() throws IOException {
		boolean listening = true;
		while(listening) {
			byte[] rbuf = new byte[utilities.Constants.MAX_MSG_SIZE];
			DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);
			socket.receive(packet);
			String received = new String(packet.getData(), 0, packet.getLength());
			System.out.println(received);
		}

		return Constants.OK;
	}


	public static void main(String[] args) throws IOException {
		if (args.length != 1) return;
		Server sv = new Server(Integer.parseInt(args[0]));
		sv.listenRequests();
	}
}
