package peers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import channels.McChannel;
import channels.MdbChannel;
import channels.MdrChannel;
import data.Data;
import exceptions.ArgsException;
import messages.Message;
import subprotocols.Backup;
import subprotocols.Delete;
import subprotocols.Restore;
import subprotocols.SpaceReclaim;

public class Peer {
	private static String serverId;
	
	private static McChannel mcChannel;
	private static MdbChannel mdbChannel;
	private static MdrChannel mdrChannel;
	
	private static Data storage;
	private DatagramSocket socket;
	
	public Peer(String serverId, String mcAddress, String mcPort,
			String mdbAddress, String mdbPort, String mdrAddress,
			String mdrPort) throws IOException {
		Peer.serverId = serverId;
		
		mcChannel = new McChannel(mcAddress, mcPort);
		mdbChannel = new MdbChannel(mdbAddress, mdbPort);
		mdrChannel = new MdrChannel(mdrAddress, mdrPort);
		
		storage = new Data();
		this.socket = new DatagramSocket(Integer.parseInt(serverId));
	}

	private void listenChannels() {
		mcChannel.listen();
		mdbChannel.listen();
		mdrChannel.listen();
	}
	
	private void listenActions() throws ArgsException {
		System.out.println("Listening TestApp...");
		String received = "";
		while(received != "quit") {
			byte[] rbuf = new byte[utilities.Constants.MAX_MSG_SIZE];
			DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);
			try {
				this.socket.receive(packet);
			} catch (IOException e) {
				System.out.println("This peer could not receive a packet from TestApp...");
			}
			received = new String(packet.getData(), 0, packet.getLength());
			String[] command = Message.splitArgs(received);
			switch (command[0]) {
			case "BACKUP":
				Backup backup = new Backup(command[1], command[2]);
				backup.start();
				break;
			case "RESTORE":
				Restore restore = new Restore(command[1]);
				restore.start();
				break;
			case "DELETE":
				Delete delete = new Delete(command[1], false);
				delete.start();
				break;
			case "ENHANCEDDELETE":
				Delete enhancedDelete = new Delete(command[1], true);
				enhancedDelete.start();
				break;
			case "RECLAIM":
				SpaceReclaim spaceReclaim = new SpaceReclaim(Integer.parseInt(command[1]));
				spaceReclaim.start();
				break;
			default:
				System.out.println("Unknown command: " + received);
				break;
			}
			String teste;
		}
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

	public static Data getStorage() {
		return storage;
	}
}
