package customer;

public class Customer {
	private String name;
	private String phoneNumber;
	private String email;
	
	
	public Customer(String name, String phoneNumber, String email) {
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}
	
	public void updateContactInfo(String phoneNumber, String email) {
		this.phoneNumber = phoneNumber;
		this.email = email;	
	}
	
	public String getName() {
		return name;
	}
	
	public String getPhoneNumber() {
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
