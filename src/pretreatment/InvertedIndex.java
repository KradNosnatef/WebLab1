package pretreatment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.alibaba.fastjson.JSON;

public class InvertedIndex {
    String rootPath;
    InvertedIndexesList invertedIndexesList;

    public InvertedIndex(String rootPath) {// 使用预处理文件存放根目录来创建倒排器实例
        this.rootPath = rootPath;
        invertedIndexesList=new InvertedIndexesList();
        System.out.println(JSON.toJSONString(invertedIndexesList));

    }

    public PretreatedFile loadPretreatedFile(int counter) throws IOException {
        File file=new File(rootPath+"\\"+counter+".txt");
        FileReader fileReader=new FileReader(file);
        
        char[] c=new char[65536];
        int len=fileReader.read(c);
        String jsonString=new String(c, 0, len);

        System.out.println(jsonString);

        PretreatedFile pretreatedFile=JSON.parseObject(jsonString,PretreatedFile.class);
        
        return(pretreatedFile);
    }

    public void invert(PretreatedFile pretreatedFile){
        String[] wordsArray=pretreatedFile.getWordsArray();
        int counter=pretreatedFile.getCounter();
        for(int i=0;i<pretreatedFile.getWordsNum();i++){
            System.out.println("inserting "+wordsArray[i]);
            invertedIndexesList.insert(wordsArray[i],counter);
            System.out.println(JSON.toJSONString(invertedIndexesList));
            System.out.println(JSON.toJSONString(invertedIndexesList.stem[34]));
        }

        System.out.println(JSON.toJSONString(invertedIndexesList));
    }
}
