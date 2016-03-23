package subprotocols;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import exceptions.ArgsException;
import messages.Header;
import peers.Peer;
import utilities.Constants;

public class Backup extends Thread{
	private String fileName;
	private Peer originalPeer;
	private int replicationDeg;
	
	public Backup(Peer peer, String fileName, String replicationDeg) throws ArgsException {
		this.originalPeer = peer;
		this.fileName = fileName;
		this.replicationDeg = Integer.parseInt(replicationDeg);
		if (this.replicationDeg > 9 && this.replicationDeg < 1)
			throw new ArgsException("ReplicationDeg must be a number between 1 and 9...");
	}
	
	public void run() {
		Path path = Paths.get(Constants.FILES_ROOT + fileName);
		try {
			byte[] data = Files.readAllBytes(path);
			sendChunks(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendChunks(byte[] data) throws IOException {
		int chunksNum = data.length / Constants.CHUNK_SIZE + 1;
		Header header = new Header(Constants.BACKUP, "1.0", originalPeer.getServerId(), fileName, "0", replicationDeg + "");
		for (int i = 0; i < chunksNum; i++) {
			byte[] chunk = getChunkData(i, header, data);
			ChunkBackup chunkBackup = new ChunkBackup(originalPeer, header, chunk);
			chunkBackup.listenReplications();
		}
	}

	private byte[] getChunkData(int i, Header header, byte[] data) {
		header.setChunkNo(i + "");
		int dataSize = Constants.CHUNK_SIZE - header.toString().getBytes().length;
		int lastIndex = (i + 1) * dataSize < data.length ? (i + 1) * dataSize : data.length;
		return Arrays.copyOfRange(data, i * Constants.CHUNK_SIZE, lastIndex);
	}
}
