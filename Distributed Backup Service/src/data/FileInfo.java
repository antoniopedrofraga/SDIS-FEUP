package data;

import java.util.ArrayList;
import java.util.HashMap;

import messages.Header;

public class FileInfo {
	private String fileName;
	private String fileId;
	private int numberOfChunks;
	private int fileSize;
	
	public FileInfo(String fileName, String fileId, int numberOfChunks, long size) {
		this.fileName = fileName;
		this.fileId = fileId;
		this.numberOfChunks = numberOfChunks;
		this.fileSize = (int) size;
	}
	
	public String getFileId() {
		return fileId;
	}
	public int getNumberOfChunks() {
		return numberOfChunks;
	}
	public String getFileName() {
		return fileName;
	}
	public int getFileSize() {
		return fileSize;
	}
	
}
