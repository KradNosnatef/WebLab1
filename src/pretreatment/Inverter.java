package pretreatment;

import java.io.IOException;

public class Inverter {
    String rootPath;
    public InvertedIndexTree invertedIndexTree;

    public Inverter(String rootPath) {// 使用预处理文件存放根目录来创建倒排器实例
        this.rootPath = rootPath;
        invertedIndexTree=new InvertedIndexTree();
    }

    public void invert(PretreatedFile pretreatedFile){
        String[] wordsArray=pretreatedFile.getWordsArray();
        int counter=pretreatedFile.getCounter();
        for(int i=0;i<pretreatedFile.getWordsNum();i++){
            invertedIndexTree.insertWord(wordsArray[i],counter);
        }
        System.out.println("file "+counter+" inverted");
    }

    public void invertAll() throws IOException, InterruptedException {
        int counts;
        PretreatedFile[] pretreatedFileArray=new PretreatedFile[1048576];
        counts=EnumImporter.loadFrom(pretreatedFileArray, rootPath, 16);

        for(int i=0;i<counts;i++){
            invert(pretreatedFileArray[i]);
        }
    }
}
