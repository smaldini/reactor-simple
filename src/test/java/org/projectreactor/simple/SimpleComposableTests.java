package org.projectreactor.simple;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.Environment;
import reactor.core.composable.Deferred;
import reactor.core.composable.Promise;
import reactor.core.composable.Stream;
import reactor.core.composable.spec.Promises;
import reactor.core.composable.spec.Streams;
import reactor.spring.context.config.EnableReactor;

/**
 * @author Jon Brisbin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SimpleComposableTests {

	@Autowired
	Environment env;

	@Test
	public void testStream() {
		Deferred<String, Stream<String>> d = Streams.defer(env, Environment.RING_BUFFER);

		d.compose().map(String::toUpperCase)
		 .filter((String s) -> s.startsWith("HELLO"))
		 .consume(SimpleComposableTests::print);

		d.accept("Hello World!");
	}

	@Test
	public void testPromise() {
		Deferred<String, Promise<String>> d = Promises.defer(env, Environment.THREAD_POOL);
		Promise<String> p = d.compose();

		p.onSuccess(SimpleComposableTests::print)
		 .onError(Throwable::printStackTrace);

		d.accept("Hello World!");
		//d.accept(new IllegalArgumentException());

		print("success? " + p.isSuccess());
		print("complete? " + p.isComplete());
	}

	static void print(String s) {
		System.out.println("\n****\n\t" + s + "\n****\n");
	}

	@Configuration
	@EnableReactor
	static class ComposableTestConfig {
	}

}
