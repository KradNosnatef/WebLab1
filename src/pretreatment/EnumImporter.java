package pretreatment;

import java.io.File;
import java.io.IOException;

public class EnumImporter {// 给文件夹路径，枚举其中的文档，整到项目文件夹里来
    private File rootFile;
    private long counter;
    private Pretreater pretreater;

    public EnumImporter(String rootFileDirectory) {
        rootFile = new File(rootFileDirectory);
        counter = 0;
        pretreater = new Pretreater();
        dfs(rootFile);
    }

    public void dfs(File file) {
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++)
                dfs(fileList[i]);
        } else {
            counter++;
            //System.out.println(counter);

            try {
                pretreater.setFile(file);
                //System.out.print("\n");
                
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}