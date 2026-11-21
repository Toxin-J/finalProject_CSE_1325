package customer;

public class Customer {
	private String name;
	private int phoneNumber;
	private String email;
	
	
	public Customer(String name, int phoneNumber, String email) {
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}
	
	public void updateContactInfo(int phoneNumber, String email) {
		this.phoneNumber = phoneNumber;
		this.email = email;
		
		
	}
	
	public String getName() {
		return name;
	}
	
	public int getPhoneNumber() {
		return phoneNumber;
	}
	
	public String getEmail() {
		return email;
	}
		
		
		
		
	public String toString() {
		return "Name: " + name
				+"\nPhone number: " + phoneNumber
				+ "\nEmail: " + email;
	}

}
