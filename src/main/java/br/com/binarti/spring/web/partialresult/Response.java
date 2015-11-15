package br.com.binarti.spring.web.partialresult;

import br.com.binarti.sjog.ObjectGraph;

public class Response<T> {

	private T data;
	private ObjectGraph objectGraph;
	
	public Response(T data) {
		this.data = data;
	}
	
	public T getData() {
		return data;
	}
	
	public ObjectGraph getObjectGraph() {
		return objectGraph;
	}
	
	void setObjectGraph(ObjectGraph objectGraph) {
		this.objectGraph = objectGraph;
	}
	
	@Override
	public String toString() {
		return "Response [ " + data + "]";
	}
	
	public static <T> Response<T> ok(T data) {
		return new Response<T>(data);
	}
	
	public static Response<Object> empty() {
		return new Response<>(null);
	}
	
}
