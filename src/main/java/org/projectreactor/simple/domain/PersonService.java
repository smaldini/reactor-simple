package org.projectreactor.simple.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.composable.Deferred;
import reactor.core.composable.Promise;
import reactor.core.composable.spec.Promises;
import reactor.event.Event;
import reactor.util.UUIDUtils;

import java.util.UUID;

import static reactor.event.selector.Selectors.$;

/**
 * @author Jon Brisbin
 */
@Service
public class PersonService {

	private static Logger LOG = LoggerFactory.getLogger(PersonService.class);

	private final Environment env;
	private final Reactor     reactor;

	@Autowired
	public PersonService(Environment env, Reactor reactor) {
		this.env = env;
		this.reactor = reactor;
	}

	public Promise<Person> save(Person person) {
		Deferred<Person, Promise<Person>> d = Promises.defer(env, Environment.RING_BUFFER);

		UUID id = UUIDUtils.create();
		reactor.<Event<Event<Person>>>on($(id), ev -> {
			LOG.info("Got reply: {}", ev.getData());
			d.accept(ev.getData().getData());
		}).cancelAfterUse();
		LOG.info("Saving {} to {}", person, id);
		reactor.send("/store/" + id, Event.wrap(person).setReplyTo(id));

		return d.compose();
	}

}
