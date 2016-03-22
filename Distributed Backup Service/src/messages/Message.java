package messages;

import java.nio.charset.StandardCharsets;

import utilities.Constants;

public class Message {
	private Header header;
	private String body;
	
	public Message(Header header) {
		this.header = header;
		this.body = "";
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	public String toString() {
		return header.getMessageType() + " " + header.getVersion() + " "
				+ header.getSenderId() + " " + Constants.sha256(header.getFileId()) + " " + 
				header.getChunkNo() + " " + header.getReplicationDeg() + " " + Constants.CR + Constants.LF +
				Constants.CR + Constants.LF + body;
	}
}