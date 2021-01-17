package pretreatment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class EnumImporter {// 给文件夹路径，枚举其中的文档，整到项目文件夹里来
    private File rootFile;
    private Pretreater pretreater;
    private int counter;
    private File[] fileToBePretreatList;


    public EnumImporter(String rootFileDirectory) {
        rootFile = new File(rootFileDirectory);
        pretreater = new Pretreater();
        counter=0;
        fileToBePretreatList=new File[1048576];                 //按需
        dfs(rootFile);

        //for(int i=0;i<counter;i++)System.out.println(fileToBePretreatList[i].getName());
        RunnableImport runnableImport=new RunnableImport(fileToBePretreatList, counter);	//多线程import，使用合适的thread数目以最大化利用cpu和io
        Thread[] threadList=new Thread[16];
        for(int i=0;i<threadList.length;i++)threadList[i]=new Thread(runnableImport,"Thread:"+i);
        for(int i=0;i<threadList.length;i++)threadList[i].start();
    }

    private void dfs(File file) {
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++)
                dfs(fileList[i]);
        } else {
            fileToBePretreatList[counter]=file;
            counter++;
        }
    }

    /*private void importJSONString(String jsonString) throws IOException {
        if(counter==(counter/100)*100)System.out.println("writing "+counter);
        String filePath="E:\\HSIO-Workspace\\"+counter+".txt";
        File file=new File(filePath);

        counter++;
        if(file.createNewFile()==false)return;//仅在测试的时候避免同内容反复写使用
        //file.createNewFile();             //生产环境请用这一行

        FileWriter fileWriter=new FileWriter(file,false);
        fileWriter.write(jsonString);
        fileWriter.close();
    }*/

    class RunnableImport implements Runnable{
        private int length;
        private File[] fileList;
        private Pretreater pretreater;
        
        public RunnableImport(File[] fileList,int length){
            this.fileList=fileList;
            this.length=length;
            this.pretreater=new Pretreater();
        }

        public void run(){
            for(int i=0;i<length;i++){
                try {
                    runnableImportJSONString(pretreater.pretreatFile(fileList[i]),i);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        private void runnableImportJSONString(String jsonString,int counter) throws IOException {
            if(counter==(counter/1000)*1000)System.out.println(Thread.currentThread().getName()+" writing "+counter);
            String filePath="E:\\TEMP-Workspace\\"+counter+".txt";
            File file=new File(filePath);
    
            counter++;
            if(file.createNewFile()==false)return;//仅在测试的时候避免同内容反复写使用
            //file.createNewFile();             //生产环境请用这一行
    
            FileWriter fileWriter=new FileWriter(file,false);
            fileWriter.write(jsonString);
            fileWriter.close();
        }
    }
}
