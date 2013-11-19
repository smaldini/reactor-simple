package org.projectreactor.simple;

import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.Reactor;
import reactor.spring.annotation.Consumer;
import reactor.spring.annotation.Selector;

/**
 * @author Jon Brisbin
 */
@Consumer
public class TestHandler {

	private final Reactor reactor;

	@Autowired
	public TestHandler(Reactor reactor) {
		this.reactor = reactor;
	}

	public Reactor getReactor() {
		return reactor;
	}

	@Selector("test")
	public void handleTest(String s) {
		System.out.println("\n\t**** s=" + s + "\n");
	}

}
