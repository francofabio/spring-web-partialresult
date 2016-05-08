package br.com.binarti.spring.web.partialresult;

import br.com.binarti.sjog.ObjectGraph;
import br.com.binarti.sjog.ObjectGraphBuilder;
import br.com.binarti.spring.web.partialresult.Response;

public class ResponseBuilder<T> {

	private T object;
	private ObjectGraphBuilder template;
	
	public ResponseBuilder(T object) {
		this.object = object;
		this.template = new ObjectGraphBuilder();
	}
	
	public ResponseBuilder<T> include(String...properties) {
		for (String prop : properties) {
			template.include(prop);
		}
		return this;
	}
	
	public ResponseBuilder<T> exclude(String...properties) {
		for (String prop : properties) {
			template.exclude(prop);
		}
		return this;
	}
	
	public Response<T> build() {
		Response<T> response = new Response<T>(object);
		ObjectGraph objectGraph = template.build(object);
		objectGraph.sortByName();
		response.setObjectGraph(objectGraph);
		return response;
	}
	
	public static <T> ResponseBuilder<T> from(T object) {
		return new ResponseBuilder<T>(object);
	}
	
}
