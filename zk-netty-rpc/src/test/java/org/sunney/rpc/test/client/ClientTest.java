package org.sunney.rpc.test.client;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunney.rpc.client.BeanProxyFactory;
import org.sunney.rpc.test.bean.Data;
import org.sunney.rpc.test.bean.DataGener;
import org.sunney.rpc.test.bean.Hello;

public class ClientTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	private Logger logger = LoggerFactory.getLogger(ClientTest.class);

	@Test
	public void testCount() {
		long startMilli = System.currentTimeMillis();
		int count = 0;
		for (int i = 0; i < 1000 * 1000; i++) {
			Hello hello = BeanProxyFactory
					.create(Hello.class, "//localhost:9991/hello",
							ClientTest.class.getClassLoader());
			Data data = hello.say(DataGener.genData());
			if (data != null) {
				logger.info("{},实际返回的值：{}", count++, data);
			}
		}
		long endMilli = System.currentTimeMillis();
		logger.info("startMilli: {},endMilli: {}, (endMilli - startMilli)={}",
				new Long[] { startMilli, endMilli, (endMilli - startMilli) });
	}

	@Test
	public void testNettyClient() {
		long startMilli = System.currentTimeMillis();
		
		Hello hello = BeanProxyFactory.create(Hello.class,
				"//localhost:9991/hello", ClientTest.class.getClassLoader());
		for(int i=0;i<100;i++){
			Data data = hello.say(DataGener.genData());
			System.out.println(data);
		}
		
		long endMilli = System.currentTimeMillis();
		logger.info("startMilli: {},endMilli: {}, (endMilli - startMilli)={}",
				new Long[] { startMilli, endMilli, (endMilli - startMilli) });
	}
}
