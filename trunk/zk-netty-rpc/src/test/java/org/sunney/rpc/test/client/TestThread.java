package org.sunney.rpc.test.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunney.rpc.client.BeanProxyFactory;
import org.sunney.rpc.test.bean.Data;
import org.sunney.rpc.test.bean.DataGener;
import org.sunney.rpc.test.bean.Hello;

public class TestThread {
	private static Logger logger = LoggerFactory.getLogger(ClientTest.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Runnable run = new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 1000; i++) {
					Hello hello = BeanProxyFactory.create(Hello.class, "//localhost:9991/hello");
					Data data = hello.say(DataGener.genData());
					logger.info("{},实际返回的值：{}", i, data);
				}
			}
		};

		ExecutorService exec = Executors.newFixedThreadPool(1000);
		for (int i = 0; i < 1000; i++) {
			exec.submit(run);
		}
		exec.shutdown();
	}

}
