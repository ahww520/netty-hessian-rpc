package org.sunney.rpc.test.bean;

import java.util.Arrays;
import java.util.HashMap;

import java.util.Map;

public class DataGener {
	public static int age = 1;

	public static Data genData() {
		Data data = new Data();
		data.setAge(age++);
		data.setName("zhoukui");
		data.setTags(new String[] { "篮球", "宅", "读书", "妹子" });
		data.setCates(Arrays.asList(new String[] { "屌丝", "待解救青年", "愤青" }));
		Map<Integer, String> map = new HashMap<Integer, String>();
		map.put(165, "身高");
		data.setMap(map);
		return data;
	}

}
