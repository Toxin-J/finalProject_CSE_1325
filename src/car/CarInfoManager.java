package car;

import java.util.ArrayList;

import customer.Customer;

public class CarInfoManager {
	// Use an ArrayList for this, it's way faster LOL
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

	/*
	 public ArrayList<Car> filterCar() {
		
	}
	
	*/
	
	public void addCustomer(Customer aCustomer) {
		if (aCustomer != null) {
			customers.add(aCustomer);
		}
	}
	
	public boolean assignCarToCustomer(Car car, Customer customer) {
		if (!cars.contains(car) || !customers.contains(customer) ) {
			return false;
		}
		
		if (car.getStatus() != VehicleStatus.AVAILABLE) {
			return false;
		}
		
		/*
		 Missing operation here; car is not being assigned to any customer yet
		 Worth considering adding an ArrayList<Cars> to the customer class to track cars belonging to customers?
		*/
		
		car.setStatus(VehicleStatus.SOLD);
		return true;
	}
	
}
