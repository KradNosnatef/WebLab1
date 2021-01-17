package pretreatment;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.alibaba.fastjson.JSON;

import assets.WordKit;

public class Pretreater {//预处理后将文档保存为JSON String然后返回
    private String string;
    private String jsonString;
    private String originalPath;

    public Pretreater() {
    }

    //返回预处理完的jsonString
    public String pretreatFile(File file) throws IOException {
        originalPath=file.getAbsolutePath();
    	char[] c=new char[65536];                            //按需
        FileReader fileReader=new FileReader(file);
        fileReader.read(c);
        string=new String(c);
        fileReader.close();
        //System.out.println(originalPath);
        //System.out.println(string);
        return(pretreat());
    }

    private String pretreat(){
        String[] wordsArray=new String[16384];               //按需
        PretreatedFile pretreatedFile=new PretreatedFile();
        String word;

        int j=0,beginIndex=0;//分词-词根还原-去停用词
        for(int i=0;i<string.length();i++){
        	//System.out.println("char is: "+string.charAt(i));
            if(WordKit.isSeparator(string.charAt(i))){
                if(beginIndex==i)beginIndex++;
                else{
                    word=WordKit.stemming(string.substring(beginIndex, i));
                    if(WordKit.isStopWord(word)){
                        //System.out.println("stop word is:"+word);
                    }
                    else{
                        wordsArray[j]=word;
                        //System.out.println("word is:"+word);
                        j++;
                    }
                    beginIndex=i+1;
                }
            }
        }
        String[] wordsArrayCutted=new String[j];
        System.arraycopy(wordsArray,0,wordsArrayCutted,0,j);

        pretreatedFile.setOriginalPath(originalPath);
        pretreatedFile.setWordsArray(wordsArrayCutted);
        pretreatedFile.setWordsNum(j);

        jsonString=JSON.toJSONString(pretreatedFile);

        return(jsonString);
    }

}
