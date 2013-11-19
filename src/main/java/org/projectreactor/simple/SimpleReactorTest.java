package org.projectreactor.simple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.Reactor;
import reactor.event.Event;

/**
 * @author Jon Brisbin
 */
@Component
public class SimpleReactorTest {

	private final Reactor reactor;

	@Autowired
	public SimpleReactorTest(Reactor reactor) {
		this.reactor = reactor;
	}

	public void publish(String msg) {
		reactor.notify("test", Event.wrap(msg));
	}

}
