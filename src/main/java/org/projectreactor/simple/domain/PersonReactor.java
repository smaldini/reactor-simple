package org.projectreactor.simple.domain;

import com.couchbase.client.CouchbaseClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.event.Event;
import reactor.spring.annotation.Consumer;
import reactor.spring.annotation.ReplyTo;
import reactor.spring.annotation.Selector;
import reactor.spring.annotation.SelectorType;

/**
 * @author Jon Brisbin
 */
@Consumer
public class PersonReactor {

	private static final Logger LOG = LoggerFactory.getLogger(PersonReactor.class);

	private final CouchbaseClient couchbaseClient;
	private final ObjectMapper    mapper;

	@Autowired
	public PersonReactor(CouchbaseClient couchbaseClient, ObjectMapper mapper) {
		this.couchbaseClient = couchbaseClient;
		this.mapper = mapper;
	}

	@Selector(value = "/store/{key}", reactor = "@reactor", type = SelectorType.URI)
	@ReplyTo
	public Event<Person> handleStore(Event<Person> ev) {
		String key = ev.getHeaders().get("key");
		try {
			LOG.info("\t***** Saving {} to Couchbase server...", ev.getData());
			couchbaseClient.set(key, mapper.writeValueAsString(ev.getData()));
		} catch(JsonProcessingException e) {
			throw new DomainException(key, e);
		}
		return ev;
	}

}
