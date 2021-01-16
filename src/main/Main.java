package main;

import java.io.File;
import java.io.IOException;

import com.alibaba.fastjson.JSON;

import pretreatment.EnumImporter;
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

		//EnumImporter enumImporter=new EnumImporter("D:\\maildir");

		Pretreater pretreater=new Pretreater();
		pretreater.setFile(new File("D:\\maildir\\allen-p\\inbox\\1"));
		System.out.println(pretreater.pretreat());
	}

}
