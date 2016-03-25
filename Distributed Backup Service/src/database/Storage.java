package database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import utilities.Constants;


public class Storage {
	HashMap<String, ChunksList> storedChunks; //FileId as key, Array of ChunkNo as value
	BackedUpFiles backedUpFiles; //HashMap containing which files are backed up, fileId as Keys
	

	File chunks;
	public Storage() {
		storedChunks = new HashMap<String, ChunksList>();
		backedUpFiles = new BackedUpFiles();
		chunks = new File(Constants.FILES_ROOT + Constants.CHUNKS_ROOT);
		createFolders();
	}

	private void createFolders() {
		if (!chunks.exists())
			chunks.mkdirs();
	}


	public void saveChunk(String fileId, Long chunkNo, byte[] data) throws IOException {
		File chunkFolder = new File(chunks.getPath() + "/" + fileId + "/");
		if (!chunkFolder.exists())
			chunkFolder.mkdirs();
		FileOutputStream stream = new FileOutputStream(chunkFolder.getPath() + "/" + chunkNo + ".data");
		try {
		    stream.write(data);
		} finally {
		    stream.close();
		    ChunksList chunks = storedChunks.get(fileId) != null ? storedChunks.get(fileId) : new ChunksList(); 
		    chunks.addChunk(chunkNo);
		}
		
	};
	
	public BackedUpFiles getBackedUpFiles() {
		return backedUpFiles;
	}
	public HashMap<String, ChunksList> getStoredChunks() {
		return storedChunks;
	}

	public static byte[] getChunkBody() {
		return null;
	}

	public static boolean chunkIsStored(String fileId, int chunkNo) {
		// TODO Auto-generated method stub
		return false;
	}
}
