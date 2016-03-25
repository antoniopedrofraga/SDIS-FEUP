package database;

import java.util.ArrayList;

public class ChunksList extends ArrayList<Long> {

	private static final long serialVersionUID = 1L;

	public void addChunk(Long number) {
		for (int i = 0; i < this.size(); i++) {
			if (number == this.get(i))
				return;
		}
		this.add(number);
	}
}