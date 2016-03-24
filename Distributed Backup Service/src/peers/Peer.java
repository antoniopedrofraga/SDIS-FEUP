package peers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

import channels.McChannel;
import channels.MdbChannel;
import channels.MdrChannel;
import database.Storage;
import exceptions.ArgsException;
import messages.Header;
import messages.Message;
import subprotocols.Backup;
import subprotocols.Restore;

public class Peer {
	private static String serverId;
	
	private static McChannel mcChannel;
	private static MdbChannel mdbChannel;
	private static MdrChannel mdrChannel;
	
	private static Storage storage;
	
	public Peer(String serverId, String mcAddress, String mcPort,
			String mdbAddress, String mdbPort, String mdrAddress,
			String mdrPort) throws IOException {
		Peer.serverId = serverId;
		
		mcChannel = new McChannel(mcAddress, mcPort);
		mdbChannel = new MdbChannel(mdbAddress, mdbPort);
		mdrChannel = new MdrChannel(mdrAddress, mdrPort);
		
		storage = new Storage();
	}

	private void listenChannels() {
		mcChannel.listen();
		mdbChannel.listen();
		mdrChannel.listen();
	}
	
	private void listenActions() throws ArgsException {
		String read = "";
		while(read != "quit") {
			System.out.println("Insert command: ");
			@SuppressWarnings("resource")
			Scanner in = new Scanner(System.in);
			read = in.nextLine();
			String[] command = Message.splitArgs(read);
			switch (command[0]) {
			case "backup":
				Backup backup = new Backup(command[1], command[2]);
				backup.start();
				break;
			case "restore":
				Restore restore = new Restore(command[1]);
				restore.start();
				break;
			default:
				System.out.println("Unknown command: " + read);
				break;
			}
		}
	}

	public static String rcvMultiCastData(MulticastSocket socket, InetAddress address) throws IOException {
        byte[] rbuf = new byte[utilities.Constants.CHUNK_SIZE];
		DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);
		socket.receive(packet);
		String received = new String(packet.getData(), 0, packet.getLength());
        return received;
	}
	
	public static void main(String[] args) throws ArgsException, IOException {
		if (args.length != 7)
			throw new ArgsException("peer <Server ID> <MC> <MC port> <MDB> <MDB port> <MDR> <MDR port>");
		Peer peer = new Peer(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
		peer.listenChannels();
		peer.listenActions();
	}
	
	/* Getters */
	public static McChannel getMcChannel() {
		return mcChannel;
	}


	public static MdbChannel getMdbChannel() {
		return mdbChannel;
	}


	public static MdrChannel getMdrChannel() {
		return mdrChannel;
	}

	
	public static String getServerId() {
		return serverId;
	}

	public static Storage getStorage() {
		return storage;
	}
}
