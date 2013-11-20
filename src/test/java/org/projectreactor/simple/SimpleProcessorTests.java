package org.projectreactor.simple;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.processor.Operation;
import reactor.core.processor.Processor;
import reactor.core.processor.spec.ProcessorSpec;
import reactor.function.Consumer;
import reactor.function.Supplier;
import reactor.function.Suppliers;
import reactor.io.Buffer;
import reactor.spring.context.config.EnableReactor;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.springframework.test.util.AssertionErrors.assertTrue;

/**
 * @author Jon Brisbin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SimpleProcessorTests.ProcessorTestConfig.class,
                      loader = SpringApplicationContextLoader.class)
public class SimpleProcessorTests {

	@Autowired
	Environment env;
	int batchSize = 1024;
	int runs      = 250000 * batchSize;
	Random                      rand;
	CountDownLatch              latch;
	Processor<Buffer>           proc;
	Supplier<Processor<Buffer>> procs;
	long                        start;
	long                        end;
	double                      elapsed;

	@SuppressWarnings("unchecked")
	@Before
	public void setup() {
		rand = ThreadLocalRandom.current();
		latch = new CountDownLatch(runs);

		Consumer<Buffer> consumer = buff -> {
			//buff.readLong();
			latch.countDown();
		};
		Supplier<Processor<Buffer>> creator = () -> new ProcessorSpec<Buffer>()
				.dataBufferSize(2 * batchSize)
				.dataSupplier(() -> new Buffer(8, false))
				.singleThreadedProducer()
				.consume(consumer)
				.get();

		procs = Suppliers.roundRobin(
				creator.get(),
				creator.get()
		);
		proc = new ProcessorSpec<Buffer>()
				.dataSupplier(() -> new Buffer(8, false))
				.singleThreadedProducer()
				.consume(consumer)
				.get();
	}

	private void start() {
		start = System.currentTimeMillis();
	}

	private void stop(String msg) throws InterruptedException {
		assertTrue("Latch was counted down", latch.await(5, TimeUnit.SECONDS));

		end = System.currentTimeMillis();
		elapsed = end - start;
		System.out.println(msg + " throughput: "
				                   + Math.round(runs / (elapsed / 1000)) + "/sec in "
				                   + Math.round(elapsed) + "ms");
	}

	@Test
	public void testSingleOperation() throws InterruptedException {
		start();
		for(int i = 0; i < runs; i++) {
			Operation<Buffer> op = proc.prepare();
			Buffer buff = op.get();

			buff.append(i).append(rand.nextLong()).flip();

			op.commit();
		}
		stop("Single");
	}

	@Test
	public void testBatchWithRandomOperation() throws InterruptedException {
		final AtomicInteger counter = new AtomicInteger();

		start();
		for(long l = 0; l < (runs / batchSize); l++) {
			procs.get().batch(batchSize, buff -> {
				buff.append(counter.incrementAndGet()).append(rand.nextLong()).flip();
			});
		}
		stop("Batch w/ Random");

	}

	@Test
	public void testBatchPublication() throws InterruptedException {
		final AtomicLong counter = new AtomicLong();

		start();
		for(long l = 0; l < (runs / batchSize); l++) {
			proc.batch(batchSize, buff -> {
				//buff.append(counter.incrementAndGet()).flip();
			});
		}
		stop("Batch Publish");

	}

	@Configuration
	@EnableReactor
	static class ProcessorTestConfig {
	}

}
