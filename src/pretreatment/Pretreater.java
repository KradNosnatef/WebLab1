package pretreatment;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.alibaba.fastjson.JSON;

import assets.Separator;

public class Pretreater {//预处理后将文档保存为JSON String然后返回
    private String string;
    private String jsonString;

    public Pretreater() {
    }

    public void setFile(File file) throws IOException {
    	char[] c=new char[32768];
        FileReader fileReader=new FileReader(file);
        fileReader.read(c);
        string=new String(c);
        fileReader.close();
        System.out.println(string);
    }

    public String pretreat(){
        String[] wordsArray=new String[4096];
        PretreatedFile pretreatedFile=new PretreatedFile();

        int j=0,beginIndex=0;
        for(int i=0;i<string.length();i++){
        	System.out.println("char is: "+string.charAt(i));
            if(Separator.isSeparator(string.charAt(i))){
                if(beginIndex==i)beginIndex++;
                else{
                    wordsArray[j]=string.substring(beginIndex, i);
                	System.out.println("word is:"+wordsArray[j]);
                    j++;
                    beginIndex=i+1;
                }
            }
        }

        pretreatedFile.setOriginalPath("sui/bian/xie");
        pretreatedFile.setWordsArray(wordsArray);
        pretreatedFile.setWordsNum(j);

        jsonString=JSON.toJSONString(pretreatedFile);

        return(jsonString);
    }

}
