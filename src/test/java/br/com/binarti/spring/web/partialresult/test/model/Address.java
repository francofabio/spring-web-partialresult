package br.com.binarti.spring.web.partialresult.test.model;

public class Address {

	private String street;
	private City city;

	public Address(String street, City city) {
		super();
		this.street = street;
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

}
