package org.projectreactor.simple;

import com.couchbase.client.CouchbaseClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;
import reactor.spring.context.config.EnableReactor;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jon Brisbin
 */
@EnableAutoConfiguration
@ComponentScan
@EnableReactor
public class ReactorTest {

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		return mapper;
	}

	@Bean
	public Reactor reactor(Environment env) {
		return Reactors.reactor(env, Environment.EVENT_LOOP);
	}

	@Bean
	public CouchbaseClient couchbaseClient() throws IOException {
		List<URI> uris = new ArrayList<>();
		uris.add(URI.create("http://127.0.0.1:8091/pools"));
		return new CouchbaseClient(uris, "default", "");
	}

	public static void main(String... args) {
		SpringApplication.run(ReactorTest.class);
	}

}
