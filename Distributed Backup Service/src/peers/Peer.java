package peers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import exceptions.ArgsException;
import messages.Header;
import messages.Message;
import subprotocols.Backup;
import subprotocols.ChunkBackup;
import utilities.Constants;

public class Peer {
	private String serverId;

	private MulticastThread mcThread;
	private MulticastSocket mcSocket;
	private InetAddress mcAddress;
	private int mcPort;
	
	private MdbThread mdbThread;
	private MulticastSocket mdbSocket;
	private InetAddress mdbAddress;
	private int mdbPort;
	
	private MulticastSocket mdrSocket;
	private InetAddress mdrAddress;
	private int mdrPort;
	
	
	
	public Peer(String serverId, String mcAddress, String mcPort,
			String mdbAddress, String mdbPort, String mdrAddress,
			String mdrPort) throws IOException {
		this.serverId = serverId;
		
		this.mcAddress = InetAddress.getByName(mcAddress);
		this.mcPort = Integer.parseInt(mcPort);
		this.mcSocket = new MulticastSocket(this.mcPort);
		this.mcThread = new MulticastThread();
		this.mcThread.start();
		
		this.mdbAddress = InetAddress.getByName(mdbAddress);
		this.mdbPort = Integer.parseInt(mdbPort);
		this.mdbSocket = new MulticastSocket(this.mdbPort);
		this.mdbThread = new MdbThread();
		this.mdbThread.start();
		
		this.mdrAddress = InetAddress.getByName(mdrAddress);
		this.mdrPort = Integer.parseInt(mdrPort);
		this.mdrSocket = new MulticastSocket(this.mdrPort);
		
	}


	private void listenActions() throws ArgsException {
		Scanner in = new Scanner(System.in);
		String read = "";
		while(read != "quit") {
			System.out.println("Insert command: ");
			read = in.nextLine();
			Header header = new Header(Constants.TEST, "version",
					serverId, "fileId", "chunkNo", "replicationDeg");
			String[] command = Message.splitArgs(read);
			switch (command[0]) {
			case "backup":
				Backup backup = new Backup(this, command[1], command[2]);
				backup.start();
				break;
			case "mdr":
				Message reply1 = new Message(mdrSocket, mdrAddress, header, null);
				new Thread(reply1).start();
				break;
			case "mdb":
				Message reply2 = new Message(mdbSocket, mdbAddress, header, null);
				new Thread(reply2).start();
				break;
			default:
				System.out.println("Unknown command: " + read);
				break;
			}
		}
	}

	public static String rcvMultiCastData(MulticastSocket socket, InetAddress address) throws IOException {
        socket.joinGroup(address);
        byte[] rbuf = new byte[utilities.Constants.CHUNK_SIZE];
		DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);
		socket.receive(packet);
		String received = new String(packet.getData(), 0, packet.getLength());
        socket.leaveGroup(address);
        return received;
	}
	
	public static void main(String[] args) throws ArgsException, IOException {
		if (args.length != 7)
			throw new ArgsException("peer <Server ID> <MC> <MC port> <MDB> <MDB port> <MDR> <MDR port>");
		Peer peer = new Peer(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
		peer.listenActions();
	}
	
	/* Getters */
	public MulticastSocket getMcSocket() {
		return mcSocket;
	}
	public InetAddress getMcAddress() {
		return mcAddress;
	}
	public MulticastSocket getMdbSocket() {
		return mdbSocket;
	}
	public InetAddress getMdbAddress() {
		return mdbAddress;
	}
	public String getServerId() {
		return serverId;
	}
	/*---------*/
	
	/* Listeners */
	private class MulticastThread extends Thread {
		public void run() {
			while(true) {
				System.out.println("Listening the MC channel...");
				try {
					String data = rcvMultiCastData(mcSocket, mcAddress);
					String[] splittedMsg = Message.splitArgs(data);
					if(!serverId.equals(splittedMsg[Constants.SENDER_ID])) {
						Header header = new Header(Constants.STORED, splittedMsg[Constants.VERSION],
								serverId, splittedMsg[Constants.FILE_ID], splittedMsg[Constants.CHUNK_NO], null);
						Message reply = new Message(mdrSocket, mdrAddress, header, null);
						int timeout = ThreadLocalRandom.current().nextInt(0, 400);
						System.out.println("Waiting time: " + timeout);
						Thread.sleep(timeout);
						new Thread(reply).start();
					}
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class MdbThread extends Thread {
		public void run() {
			while(true) {
				System.out.println("Listening the MDB channel...");
				try {
					String data = rcvMultiCastData(mdbSocket, mdbAddress);
					String[] splittedMsg = Message.splitArgs(data);
					if(!serverId.equals(splittedMsg[Constants.SENDER_ID])) {
						Header header = new Header(Constants.STORED, splittedMsg[Constants.VERSION],
								serverId, splittedMsg[Constants.FILE_ID], splittedMsg[Constants.CHUNK_NO], null);
						Message reply = new Message(mcSocket, mcAddress, header, null);
						int timeout = ThreadLocalRandom.current().nextInt(0, 400);
						System.out.println("Waiting time: " + timeout);
						Thread.sleep(timeout);
						new Thread(reply).start();
					}
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class MdrThread extends Thread {
		public void run() {
			while(true) {
				System.out.println("Listening the MDR channel...");
				try {
					String data = rcvMultiCastData(mdbSocket, mdbAddress);
					String[] splittedMsg = Message.splitArgs(data);
					if(!serverId.equals(splittedMsg[Constants.SENDER_ID])) {
						Header header = new Header(Constants.STORED, splittedMsg[Constants.VERSION],
								serverId, splittedMsg[Constants.FILE_ID], splittedMsg[Constants.CHUNK_NO], null);
						Message reply = new Message(mcSocket, mcAddress, header, null);
						int timeout = ThreadLocalRandom.current().nextInt(0, 400);
						this.wait(timeout);
						new Thread(reply).start();
					}
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/*--------------*/
}
