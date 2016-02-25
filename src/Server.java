import db.Owner;
import utilities.Constants;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.Vector;
import java.io.IOException;

public class Server {
	private int port_number;
	private Vector<Owner> owners;
	private DatagramSocket socket;


	public Server(int port_number) throws SocketException {
		this.port_number = port_number;
		this.owners =  new Vector<Owner>();
		this.socket = new DatagramSocket();
	}

	private int listenRequests() throws IOException {
		while (socket.isConnected()) {
			byte[] rbuf = new byte[utilities.Constants.MAX_MSG_SIZE];
			DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);
			socket.receive(packet);
			String received = new String(packet.getData());
			System.out.println(received);
		}

		return Constants.OK;
	}


	public static int main(String[] args) throws IOException {
		if (args.length != 1) return Constants.ERROR;
		Server sv;
		try {
			sv = new Server(Integer.parseInt(args[1]));
			sv.listenRequests();
		} catch (NumberFormatException | SocketException e) {
			e.printStackTrace();
			return Constants.ERROR;
		}
		return Constants.OK;
	}

}
