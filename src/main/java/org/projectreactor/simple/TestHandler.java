package org.projectreactor.simple;

import reactor.spring.annotation.Consumer;
import reactor.spring.annotation.Selector;

/**
 * @author Jon Brisbin
 */
@Consumer
public class TestHandler {

	@Selector(value = "test", reactor = "@reactor")
	public void handleTest(String s) {
		System.out.println("\n\t**** s=" + s + "\n");
	}

}
