package org.projectreactor.simple;

import reactor.event.Event;
import reactor.spring.annotation.Consumer;
import reactor.spring.annotation.Selector;
import reactor.spring.annotation.SelectorType;

/**
 * @author Jon Brisbin
 */
@Consumer
public class TestHandler {

	@Selector(value = "test.(.+)", type = SelectorType.REGEX, reactor = "@reactor")
	public void handleTest(Event<String> ev) {
		System.out.println("data=" + ev.getData() + ", topic=" + ev.getHeaders().get("group1"));
	}

}
