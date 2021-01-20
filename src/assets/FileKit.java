package assets;

import java.io.File;

public class FileKit {// 一些文件工具
    
    public static void cleanRoom(String path,int threadNum){//如无path路径则建立这个路径文件夹，如有则清空文件夹内容
        File file=new File(path);
        if(file.isDirectory()==false){
            file.mkdirs();
            return;
        }

        File[] fileArray=file.listFiles();
        int num=fileArray.length;

        RunnableCleanRoom[] runnableCleanRoomArray=new RunnableCleanRoom[num];
        for(int i=0;i<threadNum-1;i++)runnableCleanRoomArray[i] = new RunnableCleanRoom(fileArray, (num / threadNum) * i,(num / threadNum) * (i + 1) - 1);
        runnableCleanRoomArray[threadNum - 1] = new RunnableCleanRoom(fileArray,(num / threadNum) * (threadNum - 1), num - 1);
        
    }

    class RunnableCleanRoom implements Runnable{
        private int begin,end;
        private File[] fileArray;
        
        public RunnableCleanRoom(File[] fileArray,int begin,int end){
            this.fileArray=fileArray;
            this.begin=begin;
            this.end=end;
        }

        public void run(){
            for(int i=begin;i<=end;i++)fileArray[i].delete();
        }
    }

}
