package main;

import java.io.File;
import java.io.IOException;

import com.alibaba.fastjson.JSON;

import pretreatment.EnumImporter;
import pretreatment.InvertedIndex;
import pretreatment.PretreatedFile;
import pretreatment.Pretreater;

public class Main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("day0!");
		/*PretreatedFile pretreatedFile=new PretreatedFile();
		pretreatedFile.setOriginalPath("123");
		
		String[] wordsArray= {"123","456"};
		pretreatedFile.setWordsArray(wordsArray);
		
		System.out.println(JSON.toJSONString(pretreatedFile));*/

		//EnumImporter enumImporter=new EnumImporter("E:\\maildir\\allen-p");

		/*Pretreater pretreater=new Pretreater();
		String jsonString=pretreater.pretreatFile(new File("D:\\maildir\\allen-p\\inbox\\1"));
		enumImporter.importJSONString(jsonString);*/

		InvertedIndex invertedIndex=new InvertedIndex("E:\\TEMP-Workspace");
		invertedIndex.invert(invertedIndex.loadPretreatedFile(1));

	}

}
