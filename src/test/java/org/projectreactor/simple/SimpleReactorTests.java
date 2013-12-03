package org.projectreactor.simple;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.projectreactor.simple.domain.Person;
import org.projectreactor.simple.domain.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Jon Brisbin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ReactorTest.class, loader = SpringApplicationContextLoader.class)
public class SimpleReactorTests {

	private static final Logger LOG = LoggerFactory.getLogger(SimpleReactorTests.class);

	@Autowired
	PersonService personService;

	@Test
	public void testPersonService() throws InterruptedException {
		String name = personService.save(new Person("John Doe"))
		                           .<String>map(p -> {
			                           LOG.info("map(): {}", p);
			                           return p.getName();
		                           })
		                           .await();

		assertThat("Person was saved", name, is("John Doe"));
	}

}
