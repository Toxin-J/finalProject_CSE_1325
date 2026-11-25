package car;

import java.util.ArrayList;

import customer.Customer;

public class CarInfoManager {
	private ArrayList<Car> cars;
	private ArrayList<Customer> customers;
	
	public void addCar(Car aCar) {
		if (aCar != null) {
			cars.add(aCar);
		}
	}
	
	public void removeCar(Car aCar) {
		cars.remove(aCar);
	}
	
	public boolean updateCar(Car oldCar, Car newCar) {
		int index = cars.indexOf(oldCar);
		if (index == -1) {
			return false;
		}
		cars.set(index, newCar);
		return true;
	}
	
	public void viewCarInfo(Car aCar) {
		System.out.println(aCar);
	}

	// When working on UI: Consider removing Double from method parameters if the UI filter doesn't actually include it
	public ArrayList<Car> filterCar(Make aMake, Type aType, VehicleStatus aStatus, double aPrice, int aYear) {
		ArrayList<Car> results = new ArrayList<>();
		for (Car c: cars) {
			if (aMake != null && c.getMake() != aMake) {
				continue;
			}
			if (aType != null && c.getType() != aType) {
				continue;
			}
			if (aStatus != null && c.getStatus() != aStatus) {
				continue;
			}
			if (c.getPrice() != aPrice) {
				continue;
			}
			if (c.getYear() != aYear) {
				continue;
			}
		}
		return results;
	}
	
	public void addCustomer(Customer aCustomer) {
		if (aCustomer != null) {
			customers.add(aCustomer);
		}
	}
	
}
