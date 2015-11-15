package br.com.binarti.spring.web.partialresult.test.controller;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import br.com.binarti.spring.web.partialresult.Response;
import br.com.binarti.spring.web.partialresult.annotation.Include;
import br.com.binarti.spring.web.partialresult.annotation.PartialResult;

public class BaseCrudController<T, ID extends Serializable> {

	protected T data;
	
	public BaseCrudController(T data) {
		this.data = data;
	}
	
	@PartialResult
	public Response<T> get() {
		return Response.ok(data);
	}
	
	@PartialResult(includes = { 
		@Include("address")
	})
	public Response<List<T>> list() {
		return Response.ok(Arrays.asList(data));
	}
	
	@PartialResult(includes = { 
		@Include(value="address", includePrimitives=true, includes = "city")
	})
	public Response<List<T>> listForOverride() {
		return Response.ok(Arrays.asList(data));
	}

	@PartialResult(includes = { 
		@Include(value="address", includePrimitives=true, includes = "city")
	})
	public Response<List<T>> listForOverridePartialResult() {
		return Response.ok(Arrays.asList(data));
	}
	
}
