package org.projectreactor.simple.domain;

/**
 * @author Jon Brisbin
 */
public class DomainException extends IllegalStateException {

	private final Object key;

	public DomainException(Object key, Throwable cause) {
		super(cause.getMessage(), cause);
		this.key = key;
	}

	public Object getKey() {
		return key;
	}

}
