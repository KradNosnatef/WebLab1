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
        String filePath="src\\assets\\pretreatedFiles\\"+counter+".txt";
        File file=new File(filePath);
        file.createNewFile();
        counter++;

        FileWriter fileWriter=new FileWriter(file,false);
        fileWriter.write(jsonString);
        fileWriter.close();
    }
}
