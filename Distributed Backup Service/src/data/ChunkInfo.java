package data;

public class ChunkInfo {
	String fileId;
	int chunkNo;
	int chunkSize;
	public ChunkInfo(String fileId, int chunkNo, int chunkSize) {
		this.fileId = fileId;
		this.chunkNo = chunkNo;
		this.chunkSize = chunkSize;
	}
	public String getFileId() {
		return fileId;
	}
	public int getChunkNo() {
		return chunkNo;
	}
	public int getChunkSize() {
		return chunkSize;
	}
	@Override
	public String toString() {
		return "ChunkInfo [fileId=" + fileId + ", chunkNo=" + chunkNo + ", chunkSize=" + chunkSize + "]";
	}
	
}
