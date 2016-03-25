package database;

import java.util.HashMap;

public class BackedUpFiles extends HashMap<String, FileInfo> {

	private static final long serialVersionUID = -7257861820917361024L;

	public void markAsBackedUp(String fileName, FileInfo fileInfo) {
		this.put(fileName, fileInfo);
	}
}
