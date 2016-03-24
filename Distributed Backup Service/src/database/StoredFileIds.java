package database;

import java.util.HashMap;

public class StoredFileIds extends HashMap<String, String> {
	public void markAsStored(String fileName, String fileId) {
		this.put(fileName, fileId);
	}
}
