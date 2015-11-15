package br.com.binarti.spring.web.partialresult.test.model;

import java.util.ArrayList;
import java.util.List;

public class Product {

	private Long id;
	private String name;
	private List<Unit> units;

	public Product(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<Unit> getUnits() {
		return units;
	}
	
	public void setUnits(List<Unit> units) {
		this.units = units;
	}
	
	public Product addUnit(Unit unit) {
		if (units == null) {
			units = new ArrayList<>();
		}
		units.add(unit);
		return this;
	}

}
