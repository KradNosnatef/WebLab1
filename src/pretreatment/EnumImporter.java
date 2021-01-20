package pretreatment;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.alibaba.fastjson.JSON;

public class EnumImporter {// 给文件夹路径，枚举其中的文档，整到项目文件夹里来
    private File rootFile;
    private int counter;
    private File[] fileToBePretreatArray;

    public EnumImporter(String rootFileDirectory) {
        rootFile = new File(rootFileDirectory);
        new Pretreater();
        counter = 0;
        fileToBePretreatArray = new File[1048576]; // 按需
        dfs(rootFile);
    }

    public void saveAt(String filePath, int threadNum) throws InterruptedException {// 给路径，多线程save所有pretreatedFile
        RunnableImport[] runnableImportArray = new RunnableImport[threadNum];
        for (int i = 0; i < threadNum - 1; i++)
            runnableImportArray[i] = new RunnableImport(fileToBePretreatArray, (counter / threadNum) * i,
                    (counter / threadNum) * (i + 1) - 1, filePath);
        runnableImportArray[threadNum - 1] = new RunnableImport(fileToBePretreatArray,
                (counter / threadNum) * (threadNum - 1), counter - 1, filePath);

        Thread threadArray[] = new Thread[threadNum];
        for (int i = 0; i < threadNum; i++)
            threadArray[i] = new Thread(runnableImportArray[i], "Thread：" + i);
        for (int i = 0; i < threadNum; i++)
            threadArray[i].start();
        for(int i=0;i<threadNum;i++)threadArray[i].join();
    }

    public static int loadFrom(PretreatedFile[] pretreatedFileArray, String filePath,int threadNum)
            throws InterruptedException {// 给路径，把其中所有pretreatedFile给load到pretreatedFileArray里,返回文件数,这个方法不需要例化就能使用
        RunnableLoad[] runnableLoadArray=new RunnableLoad[threadNum];
        File file=new File(filePath);
        int counter=file.listFiles().length;
        for(int i=0;i<threadNum-1;i++)runnableLoadArray[i] = new RunnableLoad(filePath,pretreatedFileArray, (counter / threadNum) * i,(counter / threadNum) * (i + 1) - 1);
        runnableLoadArray[threadNum - 1] = new RunnableLoad(filePath,pretreatedFileArray,(counter / threadNum) * (threadNum - 1), counter - 1);
        
        Thread threadArray[] = new Thread[threadNum];
        for (int i = 0; i < threadNum; i++)
            threadArray[i] = new Thread(runnableLoadArray[i], "Thread：" + i);
        for (int i = 0; i < threadNum; i++)
            threadArray[i].start();
        for(int i=0;i<threadNum;i++)threadArray[i].join();
        return (counter);
    }

    private void dfs(File file) {
        if (file.isDirectory()) {
            File[] fileArray = file.listFiles();
            for (int i = 0; i < fileArray.length; i++)
                dfs(fileArray[i]);
        } else {
            fileToBePretreatArray[counter] = file;
            counter++;
        }
    }

    // 多线程import
    class RunnableImport implements Runnable {
        private int begin, end;
        private File[] fileArray;
        private Pretreater pretreater;
        private String filePath;

        public RunnableImport(File[] fileArray, int begin, int end, String filePath) {// end(含)
            this.fileArray = fileArray;
            this.begin = begin;
            this.end = end;
            this.pretreater = new Pretreater();
            this.filePath = filePath;
        }

        public void run() {
            for (int i = begin; i <= end; i++) {
                try {
                    runnableImportJSONString(pretreater.pretreatFile(fileArray[i], i), i);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        private void runnableImportJSONString(String jsonString, int counter) throws IOException {
            if (counter == (counter / 1000) * 1000)
                System.out.println(Thread.currentThread().getName() + " writing " + counter);
            String filePath = this.filePath + "\\" + counter + ".txt";
            File file = new File(filePath);

            counter++;
            if (file.createNewFile() == false)
                return;// 仅在测试的时候避免同内容反复写使用
            // file.createNewFile(); //生产环境请用这一行

            FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.write(jsonString);
            fileWriter.close();
        }
    }

    // 多线程load
    static class RunnableLoad implements Runnable {
        private String filePath;
        private PretreatedFile[] pretreatedFileArray;
        int begin;
        int end;

        public RunnableLoad(String filePath, PretreatedFile[] pretreatedFileArray, int begin, int end) {
            this.filePath = filePath;
            this.pretreatedFileArray = pretreatedFileArray;
            this.begin = begin;
            this.end = end;
        }

        public void run() {
            for (int i = begin; i <= end; i++) {
                try {
                    System.out.println(Thread.currentThread().getName()+" loading"+i);
                    File file = new File(filePath+"\\" + i + ".txt");
                    FileReader fileReader;
                    fileReader = new FileReader(file);
                    char[] c=new char[262144];
                    int len=fileReader.read(c);
                    String jsonString=new String(c, 0, len);
                    fileReader.close();
                    pretreatedFileArray[i]=JSON.parseObject(jsonString,PretreatedFile.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
