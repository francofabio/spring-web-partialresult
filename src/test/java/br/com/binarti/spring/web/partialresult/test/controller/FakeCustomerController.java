package br.com.binarti.spring.web.partialresult.test.controller;

import java.util.List;

import br.com.binarti.spring.web.partialresult.Response;
import br.com.binarti.spring.web.partialresult.annotation.Include;
import br.com.binarti.spring.web.partialresult.annotation.PartialResult;
import br.com.binarti.spring.web.partialresult.test.model.Customer;

public class FakeCustomerController extends BaseCrudController<Customer, Long> {

	public FakeCustomerController(Customer data) {
		super(data);
	}
	
	@Override
	public Response<List<Customer>> listForOverride() {
		return super.listForOverride();
	}
	
	@PartialResult(includes = { 
		@Include(value="address")
	})
	public Response<List<Customer>> listForOverridePartialResult() {
		return super.listForOverridePartialResult();
	}

}
