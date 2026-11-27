package car;

public class Car {
	private Make make;
	private Type type;
	private int year;
	private double price;
	private int mileage;
	private VehicleStatus status;
	
	public Car(Make aMake, Type aType, int aYear, double aPrice, int aMileage, VehicleStatus aStatus) {
		this.make = aMake;
		this.type = aType;
		this.year = aYear;
		this.price = aPrice;
		this.mileage = aMileage;
		this.status = aStatus;
	}
	
	public Make getMake() {
		return make;
	}
	
	public Type getType() {
		return type;
	}
	
	public int getYear() {
		return year;
	}
	
	public double getPrice() {
		return price;
	}
	
	public int getMileage() {
		return mileage;
	}
	
	public VehicleStatus getStatus() {
		return status;
	}
	
	public void setMake(Make aMake) {
		this.make = aMake;
	}
	
	public void setType(Type aType) {
		this.type = aType;
	}
	
	public void setYear(int aYear) {
		this.year = aYear;
	}
	
	public void setPrice(double aPrice) {
		this.price = aPrice;
	}
	
	public void setMileage(int aMileage) {
		this.mileage = aMileage;
	}
	
	public void setStatus(VehicleStatus aStatus) {
		this.status = aStatus;
	} 
	
	public String toString() {
		return year + " " + make + " " + type + " | $" + price
		         + " | " + mileage + " mi | " + status;
	}

}
