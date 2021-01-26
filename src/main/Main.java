package main;

import java.io.IOException;

import com.alibaba.fastjson.JSON;

import assets.FileKit;
import pretreatment.EnumImporter;
import pretreatment.Inverter;
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


		Service service=new Service();
		service.gotoMainMenu();
	}
}
