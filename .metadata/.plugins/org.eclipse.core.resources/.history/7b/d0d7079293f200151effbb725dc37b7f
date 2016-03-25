package database;

import java.util.ArrayList;

public class ChunksList extends ArrayList<Long> {
	public void addChunk(Long number) {
		for (int i = 0; i < this.size(); i++) {
			if (number == this.get(i))
				return;
		}
		this.add(number);
	}
}
