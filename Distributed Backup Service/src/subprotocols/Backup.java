package subprotocols;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import data.ChunkInfo;
import data.ChunksList;
import data.Data;
import data.FileInfo;
import messages.Header;
import messages.Message;
import peers.Peer;
import utilities.Constants;
import utilities.Utilities;

public class Backup extends Thread {
	static File file;
	private int replicationDeg;
	
	public Backup(String fileName, String replicationDeg) {
		file = new File(Constants.FILES_ROOT + fileName);
		this.replicationDeg = Integer.parseInt(replicationDeg);
		if (this.replicationDeg > 9 && this.replicationDeg < 1) {
			System.out.println("ReplicationDeg must be a number between 1 and 9...");
			return;
		}
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
		
		int numberOfChunks = data.length / Constants.CHUNK_SIZE + 1;
		String fileId = Utilities.getFileId(file);
		Header header = new Header(Message.PUTCHUNK, Constants.PROTOCOL_VERSION, Peer.getServerId(), fileId, "0", replicationDeg + "");
		
		for (int i = 0; i < numberOfChunks; i++) {
			header.setChunkNo(i + "");
			byte[] chunk = getChunkData(i, data);
			sendChunk(header, chunk);
		}
		Peer.getStorage();
		if (Data.getBackedUpFiles().get(file.getName()) == null) {
			Peer.getStorage();
			Data.getBackedUpFiles().markAsBackedUp(file.getName(), new FileInfo(file.getName(), fileId, numberOfChunks, file.length()));
		}
		System.out.println("File was backed up succesfully!");
	}

	public static void sendChunk(Header header, byte[] chunk) {
		int waitingTime = Constants.DEFAULT_WAITING_TIME;
		int chunksSent = 0;
		while (chunksSent < Constants.MAX_CHUNK_RETRY) {
			System.out.println("Sending chunk.");
			ChunkBackup backupChunk = new ChunkBackup(header, chunk);
			backupChunk.sendChunk();
			try {
				Thread.sleep(waitingTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			backupChunk.checkReplies();
			Peer.getStorage();
			Peer.getStorage();
			ChunksList chunksList = Data.getChunksBackedUp().get(header.getFileId()) != null ? Data.getChunksBackedUp().get(header.getFileId()) : null;
			int confirmedBackUps = 0;
			ChunkInfo thisChunkInfo = new ChunkInfo(header, chunk.length);
			//Getting confirmedBackUps
			if (chunksList != null)
				for (ChunkInfo chunkInfo : chunksList)
					if (chunkInfo.equals(thisChunkInfo))
						confirmedBackUps = chunkInfo.getStoredHeaders().size();
			//Checking if this Peer has the chunk stored
			ChunkInfo chunkInfo = new ChunkInfo(header, chunk.length);
			if (Data.getChunksSaved().get(header.getFileId()) != null) 
				if (Data.getChunksSaved().get(header.getFileId()).contains(chunkInfo))
					confirmedBackUps++;
			
			int repDeg = Integer.parseInt(header.getReplicationDeg());
			if (confirmedBackUps < repDeg) {
				chunksSent++;
				waitingTime *= 2;
				System.out.println("ReplicationDeg was not achieved... Waiting more " + waitingTime + "ms.");
			} else {
				break;
			}
		}
		waitingTime = Constants.DEFAULT_WAITING_TIME;
	}

	private byte[] getChunkData(int i, byte[] data) {
		int lastIndex = (i + 1) * Constants.CHUNK_SIZE < data.length + 1 ? (i + 1) * Constants.CHUNK_SIZE : data.length;
		return Arrays.copyOfRange(data, i * Constants.CHUNK_SIZE, lastIndex);
	}

	public static File getFile() {
		return file;
	}

}
