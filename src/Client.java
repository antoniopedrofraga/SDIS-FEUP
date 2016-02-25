import java.io.IOException;
import java.net.DatagramSocket;

import utilities.Constants;

public class Client {
	private String host_name;
	private int port_number;
	
	public static int main(String[] args) {
		if (args.length != 2) return Constants.ERROR;
		return Constants.OK;
	}
}
