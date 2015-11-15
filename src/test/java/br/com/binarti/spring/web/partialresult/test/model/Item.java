package br.com.binarti.spring.web.partialresult.test.model;

public class Item {

	private Long id;
	private Product product;
	private Double price;

	public Item(Long id, Product product, Double price) {
		super();
		this.id = id;
		this.product = product;
		this.price = price;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

}
