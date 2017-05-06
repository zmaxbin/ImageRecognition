package com.myproject.identImage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class sortMap {
	 public static List<Map.Entry<String,Integer>> sort_map(Map<String, Integer> map){
	        List<Map.Entry<String,Integer>> list = new ArrayList<Map.Entry<String,Integer>>(map.entrySet());
	        Collections.sort(list,new Comparator<Map.Entry<String,Integer>>() {
		        //降序排序
		        public int compare(Entry<String, Integer> o1,Entry<String, Integer> o2) {
		             return o2.getValue().compareTo(o1.getValue());
		        }
	        });
	        
	        return list;
	  }
}
