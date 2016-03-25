package subprotocols;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import database.FileInfo;
import exceptions.ArgsException;
import messages.Header;
import messages.Message;
import peers.Peer;
import utilities.Constants;
import utilities.Hash;

public class Backup extends Thread {
	static File file;
	private int replicationDeg;
	
	public Backup(String fileName, String replicationDeg) throws ArgsException {
		file = new File(Constants.FILES_ROOT + fileName);
		this.replicationDeg = Integer.parseInt(replicationDeg);
		if (this.replicationDeg > 9 && this.replicationDeg < 1)
			throw new ArgsException("ReplicationDeg must be a number between 1 and 9...");
	}
	
	public void run() {
		try {
			byte[] data = Files.readAllBytes(file.toPath());
			sendChunks(data);
		} catch (IOException e) {
			System.out.println("The file '" + file.getName() + "' does not exist.");
		} catch (InterruptedException e) {
			System.out.println("Could not sleep while sending chunks, aborting...");
		}
	}

	private void sendChunks(byte[] data) throws InterruptedException {
		
		int waitingTime = Constants.DEFAULT_WAITING_TIME;
		int numberOfChunks = data.length / Constants.CHUNK_SIZE + 1;
		String fileId = Hash.sha256(file.getName() + file.lastModified() + Peer.getServerId());
		Header header = new Header(Message.PUTCHUNK, Constants.PROTOCOL_VERSION, Peer.getServerId(), fileId, "0", replicationDeg + "");
		
		for (int i = 0; i < numberOfChunks; i++) {
			header.setChunkNo(i + "");
			byte[] chunk = getChunkData(i, data);
			int chunksSent = 0;
			while (chunksSent < Constants.MAX_CHUNK_RETRY) {
				System.out.println("Sending chunk number " + i + " with " + chunk.length + " bytes, waiting " + waitingTime + "ms after that.");
				ChunkBackup backupChunk = new ChunkBackup(header, chunk);
				backupChunk.sendChunk();
				Thread.sleep(waitingTime);
				backupChunk.checkReplies();
				FileInfo fileInfo = Peer.getStorage().getBackedUpFiles().get(file.getName()) == null ? 
										null : Peer.getStorage().getBackedUpFiles().get(file.getName());
				int confirmedBackUps = 
						fileInfo == null || fileInfo.getBackedUpChunks().get(i) == null ?
										0 : fileInfo.getBackedUpChunks().get(i).size();
				if (confirmedBackUps < replicationDeg) {
					chunksSent++;
					waitingTime *= 2;
					System.out.println("ReplicationDeg was not achieved...");
				} else {
					break;
				}
			}
			waitingTime = Constants.DEFAULT_WAITING_TIME;
		}
		System.out.println("All chunks were sent");
	}

	private byte[] getChunkData(int i, byte[] data) {
		int lastIndex = (i + 1) * Constants.CHUNK_SIZE < data.length ? (i + 1) * Constants.CHUNK_SIZE : data.length;
		return Arrays.copyOfRange(data, i * Constants.CHUNK_SIZE, lastIndex);
	}

	public static File getFile() {
		return file;
	}

}
