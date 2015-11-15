package br.com.binarti.spring.web.partialresult.test.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import br.com.binarti.spring.web.partialresult.Response;
import br.com.binarti.spring.web.partialresult.annotation.Include;
import br.com.binarti.spring.web.partialresult.annotation.PartialResult;
import br.com.binarti.spring.web.partialresult.test.model.Address;
import br.com.binarti.spring.web.partialresult.test.model.City;
import br.com.binarti.spring.web.partialresult.test.model.Customer;
import br.com.binarti.spring.web.partialresult.test.model.Item;
import br.com.binarti.spring.web.partialresult.test.model.Order;
import br.com.binarti.spring.web.partialresult.test.model.Product;

public class FakeOrderController {

	@PartialResult
	public Response<Order> get(Long id) {
		return Response.ok(new Order(1L, new Date(), new Customer(1L, "Customer 1")));
	}
	
	@PartialResult(type = Order.class)
	public Response<Order> getWithAnnotedType(Long id) {
		return Response.ok(new Order(1L, new Date(), new Customer(1L, "Customer 1")));
	}
	
	@PartialResult(includes = {
		@Include("customer")
	})
	public Response<Order> getOrderWithCustomer(Long id) {
		return Response.ok(new Order(1L, new Date(), new Customer(1L, "Customer 1")));
	}
	
	@PartialResult(includePrimitives=false, includes = {
		@Include("customer")
	})
	public Response<Order> getOrderWithoutIncludePrimitive(Long id) {
		return Response.ok(new Order(1L, new Date(), new Customer(1L, "Customer 1")));
	}
	
	@PartialResult(includes = {
		@Include(value = "customer", includePrimitives=false, includes = "address")
	})
	public Response<Order> getOrderWithoutIncludePrimitiveInNestedInclude(Long id) {
		return Response.ok(new Order(1L, new Date(), new Customer(1L, "Customer 1")));
	}
	
	@PartialResult(includes = {
		@Include("customer"),
		@Include("itens")
	})
	public Response<Order> getOrderWithItens(Long id) {
		return Response.ok(new Order(1L, new Date(), new Customer(1L, "Customer 1")).addItem(new Item(1L, new Product(1L, "Product 1"), 100d)));
	}
	
	@PartialResult(includes = {
		@Include("customer"),
		@Include(value = "itens", includes = "product")
	})
	public Response<Order> getOrderWithItensAndDeep(Long id) {
		return Response.ok(new Order(1L, new Date(), new Customer(1L, "Customer 1")).addItem(new Item(1L, new Product(1L, "Product 1"), 100d)));
	}
	
	@PartialResult(includes = {
		@Include("customer"),
		@Include(value = "itens", includes = "product")
	})
	public Response<List<Order>> getOrders(String customer) {
		return Response.ok(Arrays.asList(new Order(1L, new Date(), new Customer(1L, "Customer 1")).addItem(new Item(1L, new Product(1L, "Product 1"), 100d))));
	}
	
	@PartialResult(includes = {
		@Include(value="address", includes = "city")
	})
	@SuppressWarnings("unused")
	public Response<Object> getAnonymousPersonObject() {
		return Response.ok(new Object() {
			private String name = "John Smith";
			private int age = 32;
			private Address address = new Address("Street A", new City("Topeka", "KS"));
			
			public String getName() {
				return name;
			}
			
			public int getAge() {
				return age;
			}
			
			public Address getAddress() {
				return address;
			}
		});
	}

	@PartialResult(includes = {
		@Include("customer"),
		@Include(value = "itens", includes = "product")
	})
	public Response<List<Object>> getOrderAsObject() {
		return Response.ok(Arrays.asList(new Order(1L, new Date(), new Customer(1L, "Customer 1")).addItem(new Item(1L, new Product(1L, "Product 1"), 100d))));
	}
	
	@SuppressWarnings("rawtypes")
	@PartialResult(includes = {
		@Include("customer"),
		@Include(value = "itens", includes = "product")
	})
	public Response<List> getOrderAsRawList() {
		return Response.ok(Arrays.asList(new Order(1L, new Date(), new Customer(1L, "Customer 1")).addItem(new Item(1L, new Product(1L, "Product 1"), 100d))));
	}

	@PartialResult(includePrimitives=false, includes = {
		@Include("date"),
		@Include("customer")
	})
	public Response<Order> getOrderWithoutIncludePrimitiveAndPrimitiveIncludeExplicit(Long id) {
		return Response.ok(new Order(1L, new Date(), new Customer(1L, "Customer 1")));
	}
	
	@PartialResult(includes = {
		@Include(value = "customer", includePrimitives=false),
		@Include("customer.address")
	})
	public Response<Order> getOrderWithoutIncludePrimitiveInSingleNestedInclude(Long id) {
		return Response.ok(new Order(1L, new Date(), new Customer(1L, "Customer 1")));
	}
	
}
