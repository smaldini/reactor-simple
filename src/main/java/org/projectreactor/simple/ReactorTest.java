package org.projectreactor.simple;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;
import reactor.spring.context.config.EnableReactor;

/**
 * @author Jon Brisbin
 */
@Configuration
@ComponentScan
@EnableReactor
public class ReactorTest {

	@Bean
	public Reactor reactor(Environment env) {
		return Reactors.reactor(env);
	}

	public static void main(String... args) {
		ApplicationContext ctx = SpringApplication.run(ReactorTest.class);

		ctx.getBean(SimpleReactorTest.class).publish("Hello World!");
	}

}
