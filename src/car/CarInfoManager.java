package car;

import java.util.ArrayList;

import customer.Customer;
import customer.PurchaseType;

public class CarInfoManager {
	// GUI fix: the lists were never created, so addCar/addCustomer threw a NullPointerException
	private ArrayList<Car> cars = new ArrayList<>();
	private ArrayList<Customer> customers = new ArrayList<>();
	
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

	// GUI fix: matching cars are now added to results, and a price or year of -1 means "any"
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
			if (aPrice != -1 && c.getPrice() != aPrice) {
				continue;
			}
			if (aYear != -1 && c.getYear() != aYear) {
				continue;
			}
			results.add(c);
		}
		return results;
	}

	// GUI addition: same filter plus a maximum mileage (-1 means any mileage), for the GUI's mileage slider
	public ArrayList<Car> filterCar(Make aMake, Type aType, VehicleStatus aStatus, double aPrice, int aYear, int aMaxMileage) {
		ArrayList<Car> results = new ArrayList<>();
		for (Car c : filterCar(aMake, aType, aStatus, aPrice, aYear)) {
			if (aMaxMileage == -1 || c.getMileage() <= aMaxMileage) {
				results.add(c);
			}
		}
		return results;
	}
	
	// GUI addition: FR-3 Record Vehicle Transaction. Returns false if the car isn't available.
	// TODO: once PaymentInfo is written, create and store a PaymentInfo linking aCustomer and aCar here.
	public boolean recordTransaction(Car aCar, Customer aCustomer, PurchaseType aType) {
		if (aCar == null || aCustomer == null || aType == null || aCar.getStatus() != VehicleStatus.AVAILABLE) {
			return false;
		}
		if (aType == PurchaseType.RENTAL) {
			aCar.setStatus(VehicleStatus.RENTED);
		} else if (aType == PurchaseType.LEASE) {
			aCar.setStatus(VehicleStatus.LEASED);
		} else {
			aCar.setStatus(VehicleStatus.SOLD);
		}
		return true;
	}
	
	public void addCustomer(Customer aCustomer) {
		if (aCustomer != null) {
			customers.add(aCustomer);
		}
	}
	
	// GUI addition: FR-11 Remove Customer
	public boolean removeCustomer(Customer aCustomer) {
		return customers.remove(aCustomer);
	}

	// GUI addition: the GUI needs read access to show the fleet and customer list
	public ArrayList<Car> getCars() {
		return new ArrayList<>(cars);
	}

	public ArrayList<Customer> getCustomers() {
		return new ArrayList<>(customers);
	}	
}
