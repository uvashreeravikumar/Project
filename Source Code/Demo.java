package com.gts.controller;

import java.util.ArrayList;
import java.util.List;

public class Demo {
	static boolean name;

	public static void main(String[] args) {

		List<String> list = new ArrayList<String>();
		list.add("mani");
		list.add("mani");
		list.add("kumar");
		list.add("arun");
		
		boolean boolval = list.contains("wwww");

		if(boolval== true) {
			System.out.println("llll");
		}else {
			System.out.println("lddddlll");
		}

	}

}
