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

}
