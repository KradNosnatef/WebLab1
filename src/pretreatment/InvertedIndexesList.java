package pretreatment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.alibaba.fastjson.JSON;

public class InvertedIndexesList {// 类化的索引表
    public StemNode[] stem;// 二维链表按首字符分组

    public class StemNode {// 按word的字典序排序
        public String word;
        public StemNode znext;
        public LeafNode leafHead;

        public StemNode(String word) {
            this.word = word;
            this.leafHead = new LeafNode(-1);
            this.znext = null;
        }

        public StemNode search(String word) {// 返回从本节点起不大于给定word的最大word的节点，如果没有这样的节点，返回null
            int cmp = this.word.compareTo(word);
            if (cmp == 0) {
                // System.out.println("FFFFOUNNNND: "+JSON.toJSONString(this));
                return (this);
            } else if (cmp > 0)
                return (null);
            else if (znext == null)
                return (this);
            else {
                StemNode result = znext.search(word);
                if (result == null) {
                    // System.out.println("FFFFOUNNNND: "+JSON.toJSONString(this));
                    return (this);
                } else
                    return (result);
            }
        }

        public class LeafNode {// 按counter排升序
            public int counter;
            public int frequency;
            public LeafNode znext;

            public LeafNode(int counter) {
                this.counter = counter;
                this.frequency = 0;
                this.znext = null;
            }

            public LeafNode(int counter, int frequency) {
                this.counter = counter;
                this.frequency = frequency;
                this.znext = null;
            }

            public LeafNode search(int counter) {// 返回从本节点起不大于给定counter的最大的counter的节点，如果没有这样的节点，返回null
                int cmp = this.counter - counter;
                if (cmp == 0)
                    return (this);
                else if (cmp > 0)
                    return (null);
                else if (this.znext == null)
                    return (this);
                else {
                    LeafNode result = this.znext.search(counter);
                    if (result == null) {
                        return (this);
                    } else
                        return (result);
                }
            }
        }
    }

    public InvertedIndexesList() {
        stem = new StemNode[36];// 0~25为小写字母，26~35为数字
        for (int i = 0; i < 26; i++) {
            char c = (char) (i + 97);
            stem[i] = new StemNode("" + c);
        }
        for (int i = 26; i < 36; i++) {
            char c = (char) (i - 26 + 48);
            stem[i] = new StemNode("" + c);
        }
    }

    public void insert(String word, int counter) {// 给出词和对应的counter标识符，插入维护表内容
        int headChar;
        StemNode stemNode;
        if (word.charAt(0) > 96)
            headChar = word.charAt(0) - 97;
        else
            headChar = word.charAt(0) - 48 + 26;

        //System.out.println(word.charAt(0) + " " + word + " " + counter);
        stemNode = stem[headChar].search(word);
        //System.out.println("good");
        if (stemNode.word.compareTo(word) == 0) {
            // System.out.println("already have word");
            StemNode.LeafNode leafNode = stemNode.leafHead.search(counter);
            if (leafNode.counter == counter) {
                leafNode.frequency++;
                // System.out.println("frequency++!");
            } else {
                StemNode.LeafNode leafNext = leafNode.znext;
                leafNode.znext = stemNode.new LeafNode(counter, 1);
                leafNode.znext.znext = leafNext;
            }
        } else {
            StemNode.LeafNode leafNode;

            StemNode stemNext = stemNode.znext;
            stemNode.znext = new StemNode(word);
            stemNode.znext.znext = stemNext;
            leafNode = stemNode.znext.leafHead;

            StemNode.LeafNode leafNext = leafNode.znext;
            leafNode.znext = stemNode.new LeafNode(counter, 1);
            leafNode.znext.znext = leafNext;
        }
    }

    class RunnableSave implements Runnable {
        private int begin, end;
        private StemNode[] stem;
        private String pathName;

        public RunnableSave(StemNode[] stem, int begin, int end,String pathName) {
            this.stem = stem;
            this.begin = begin;
            this.end = end;
            this.pathName=pathName;
        }

        public void run() {
            for (int i = begin; i <= end; i++) {
                StemNode stemNode = stem[i];
                for (;;) {
                    System.out.println("creating index file:" + stemNode.word);
                    File tempfile = new File(pathName+"\\_" + stemNode.word + ".txt");
                    try {
                        tempfile.createNewFile();
                        //if(tempfile.isFile())System.out.println("created!");
                        FileWriter fileWriter=new FileWriter(tempfile);
                        String jsonString=JSON.toJSONString(stemNode.leafHead);
                        fileWriter.write(jsonString);
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    stemNode=stemNode.znext;
                    if(stemNode==null)break;
                }
            }  
        }
    }

    public void saveAt(String pathName,int threadNum) throws InterruptedException {
        RunnableSave[] runnableSaveArray=new RunnableSave[threadNum];
        for(int i=0;i<threadNum-1;i++)runnableSaveArray[i]=new RunnableSave(stem,(36/threadNum)*i,((36)/threadNum)*(i+1)-1,pathName);
        runnableSaveArray[threadNum-1]=new RunnableSave(stem,(36/threadNum)*(threadNum-1), 36-1,pathName);

        Thread threadArray[]=new Thread[threadNum];
        for(int i=0;i<threadNum;i++)threadArray[i]=new Thread(runnableSaveArray[i],"Thread："+i);
        for(int i=0;i<threadNum;i++)threadArray[i].start();
        for(int i=0;i<threadNum;i++)threadArray[i].join();
    }
}
