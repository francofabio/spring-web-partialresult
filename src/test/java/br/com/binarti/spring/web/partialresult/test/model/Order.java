package br.com.binarti.spring.web.partialresult.test.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {

	private Long id;
	private Date date;
	private Customer customer;
	private List<Item> itens;

	public Order() {
	}
	
	public Order(Long id, Date date, Customer customer) {
		super();
		this.id = id;
		this.date = date;
		this.customer = customer;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<Item> getItens() {
		return itens;
	}

	public void setItens(List<Item> itens) {
		this.itens = itens;
	}
	
	public Order addItem(Item item) {
		if (itens == null) {
			itens = new ArrayList<>();
		}
		itens.add(item);
		return this;
	}

}
