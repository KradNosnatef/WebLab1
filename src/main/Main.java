package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.alibaba.fastjson.JSON;

import pretreatment.EnumImporter;
import pretreatment.InvertedIndex;
import pretreatment.PretreatedFile;
import pretreatment.Pretreater;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		System.out.println("day0!");

		EnumImporter enumImporter=new EnumImporter("T:\\maildir\\allen-p");
		enumImporter.saveAt("T:\\TEMP-Workspace",16);
		//PretreatedFile[] pretreatedFileArray=new PretreatedFile[1048576];
		//enumImporter.loadFrom(pretreatedFileArray,"E:\\TEMP-Workspace",16);

		InvertedIndex invertedIndex=new InvertedIndex("T:\\TEMP-Workspace");
		invertedIndex.invertAll();

	}
}
