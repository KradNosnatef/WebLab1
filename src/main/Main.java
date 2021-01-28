package main;

import java.io.IOException;

import service.Service;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		//System.out.println("day0!");

		//EnumImporter enumImporter=new EnumImporter("T:\\maildir\\shackleton-s");
		//enumImporter.saveAt("T:\\TEMP-Workspace",16);

		//Inverter inverter=new Inverter("T:\\TEMP-Workspace");
		//inverter.invertAll();
		//System.out.println(JSON.toJSONString(inverter.invertedIndexTree.searchCharNode("automatically")));
		//inverter.invertedIndexTree.saveAt("T:\\IndexSpace",12);

		//System.out.println(InvertedIndexTree.boolSearcher("(((^(2))|(3))&(((1)|(^(2)))|(15)))"));

		//int[] a1=null;
		//int[] a2={5,7,9,11};
		//System.out.println(JSON.toJSONString(InvertedIndexTree.counterArrayAnd(a1,a2)));
		//System.out.println(JSON.toJSONString(InvertedIndexTree.counterArrayOr(a1,a2)));
		//System.out.println(JSON.toJSONString(InvertedIndexTree.counterArrayNot(a1,30)));

		Service service=new Service();
		service.gotoMainMenu();
	}
}
