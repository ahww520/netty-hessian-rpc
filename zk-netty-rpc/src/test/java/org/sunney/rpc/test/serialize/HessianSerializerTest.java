package org.sunney.rpc.test.serialize;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunney.rpc.serialize.HessianSerializer;
import org.sunney.rpc.serialize.Serializer;
import org.sunney.rpc.test.bean.Data;

public class HessianSerializerTest {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testEnDe() throws Exception {
		int count = 0;
		while (count++ < 1000 * 10) {
			Data data = new Data();
			data.setAge(25);
			data.setName("zhoukui");
			data.setTags(new String[] { "篮球", "宅", "读书", "妹子" });
			data.setCates(Arrays.asList(new String[] { "屌丝", "待解救青年", "愤青" }));
			Map<Integer, String> map = new HashMap<Integer, String>();
			map.put(165, "身高");
			data.setMap(map);
			logger.info("Before:{}", data);

			Serializer serializer = new HessianSerializer();
			byte[] bytes = serializer.enSerialize(data);

			Object newData = serializer.deSerialize(bytes);
			logger.info("After :{}", newData);
		}
	}

}
