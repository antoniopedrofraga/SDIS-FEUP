package db;

public class Owner {
	
	private String plateNumber;
	private String name;
	
	
	public Owner(String plateNumber, String ownerName) {
		this.plateNumber = plateNumber;
		this.name = ownerName;
	}

	public String getPlateNumber() {
		return plateNumber;
	}
	
	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
