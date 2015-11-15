package br.com.binarti.spring.web.partialresult;

@SuppressWarnings("serial")
public class PartialResultException extends RuntimeException {

	public PartialResultException() {
		super();
	}

	public PartialResultException(String message, Throwable cause) {
		super(message, cause);
	}

	public PartialResultException(String message) {
		super(message);
	}

	public PartialResultException(Throwable cause) {
		super(cause);
	}

}
