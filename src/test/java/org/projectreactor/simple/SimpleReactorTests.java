package org.projectreactor.simple;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;
import reactor.event.Event;
import reactor.spring.context.config.EnableReactor;

import static reactor.event.selector.Selectors.*;

/**
 * @author Jon Brisbin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SimpleReactorTests.ReactorTestConfig.class,
                      loader = SpringApplicationContextLoader.class)
public class SimpleReactorTests {

	static final Logger LOG = LoggerFactory.getLogger(SimpleReactorTest.class);

	@Autowired
	Reactor reactor;

	@Test
	public void testObjectSelector() {
		reactor.<Event<String>>on($("test"),
		                          ev -> LOG.info("testObjectSelector: {}",
		                                         ev));
		reactor.notify("test", Event.wrap("Hello World!"));
	}

	@Test
	public void testRegexSelector() {
		reactor.<Event<String>>on(R("test.(.+)"),
		                          ev -> LOG.info("testRegexSelector: {}, header: {}",
		                                         ev,
		                                         ev.getHeaders().get("group1")));
		reactor.notify("test.1", Event.wrap("Hello World!"));
	}

	@Test
	public void testUriTemplateSelector() {
		reactor.<Event<String>>on(U("/test/{segment}"),
		                          ev -> LOG.info("testUriTemplateSelector: {}, header: {}",
		                                         ev,
		                                         ev.getHeaders().get("segment")));
		reactor.notify("/test/1", Event.wrap("Hello World!"));
	}

	@Configuration
	@EnableReactor
	static class ReactorTestConfig {

		@Bean
		public Reactor reactor(Environment env) {
			return Reactors.reactor(env);
		}

	}

}
