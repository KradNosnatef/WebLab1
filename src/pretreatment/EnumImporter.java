package pretreatment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class EnumImporter {// 给文件夹路径，枚举其中的文档，整到项目文件夹里来
    private File rootFile;
    private Pretreater pretreater;
    private int counter;


    public EnumImporter(String rootFileDirectory) {
        rootFile = new File(rootFileDirectory);
        pretreater = new Pretreater();
        counter=0;
        dfs(rootFile);
    }

    private void dfs(File file) {
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++)
                dfs(fileList[i]);
        } else {
            try {
                importJSONString(pretreater.pretreatFile(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void importJSONString(String jsonString) throws IOException {
        if(counter==(counter/100)*100)System.out.println("writing "+counter);
        String filePath="E:\\HSIO-Workspace\\"+counter+".txt";
        File file=new File(filePath);

        if(file.createNewFile()==false)return;//仅在测试的时候避免同内容反复写使用
        //file.createNewFile();             //生产环境请用这一行

        counter++;

        FileWriter fileWriter=new FileWriter(file,false);
        fileWriter.write(jsonString);
        fileWriter.close();
    }
}
