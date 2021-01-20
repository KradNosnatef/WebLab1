package main;

import java.io.IOException;

import pretreatment.EnumImporter;
import pretreatment.Inverter;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		System.out.println("day0!");

		EnumImporter enumImporter=new EnumImporter("T:\\maildir");
		enumImporter.saveAt("T:\\TEMP-Workspace",16);

		Inverter inverter=new Inverter("T:\\TEMP-Workspace");
		inverter.invertAll();
		inverter.invertedIndexTree.saveAt("T:\\IndexSpace",12);

	}
}
