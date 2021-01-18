package pretreatment;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.alibaba.fastjson.JSON;

import pretreatment.InvertedIndexesList.StemNode;

public class InvertedIndex {
    String rootPath;
    public InvertedIndexesList invertedIndexesList;

    public InvertedIndex(String rootPath) {// 使用预处理文件存放根目录来创建倒排器实例
        this.rootPath = rootPath;
        invertedIndexesList=new InvertedIndexesList();
        //System.out.println(JSON.toJSONString(invertedIndexesList));

    }

    public void invert(PretreatedFile pretreatedFile){
        String[] wordsArray=pretreatedFile.getWordsArray();
        int counter=pretreatedFile.getCounter();
        for(int i=0;i<pretreatedFile.getWordsNum();i++){
            //System.out.println("inserting "+wordsArray[i]);
            invertedIndexesList.insert(wordsArray[i],counter);
            //System.out.println(JSON.toJSONString(invertedIndexesList));
            //System.out.println(JSON.toJSONString(invertedIndexesList.stem[34]));
        }
        System.out.println("file "+counter+" inverted");
        //System.out.println(JSON.toJSONString(invertedIndexesList));
    }

    public void invertAll() throws IOException, InterruptedException {
        int counts;
        PretreatedFile[] pretreatedFileArray=new PretreatedFile[1048576];
        counts=EnumImporter.loadFrom(pretreatedFileArray, rootPath, 16);

        for(int i=0;i<counts;i++){
            invert(pretreatedFileArray[i]);
        }

        invertedIndexesList.saveAt("T:\\IndexSpace",12);
    }
}
