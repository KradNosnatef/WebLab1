package pretreatment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import com.alibaba.fastjson.JSON;

import assets.WordKit;

//每个字母节点存储一个后续字母数组，和一个频率统计链表，该链表是“从树根到当前位置路径上的节点拼成的单词对应的频率统计信息”
//频率链表的第一个节点用于储存有序无序相关信息，初次初始化构建链表的时候简单拼接各个文档的单词出现信息，这是无序的，链表中的所有freq都应当是0
//链表对应的单词首次被搜索会使它启动有序化
//对两个树进行merge操作的时候，对于同单词合并，当且仅当两个链表都是无序链表时才使用简单拼接成一个新的无序链表，否则拼接成有序链表

public class InvertedIndexTree {
    public CharNode headChar; // wordtree的头，本身不代表任何字母

    public class CharNode {
        public char thisChar;
        public CharNode[] znextChar;
        public FreqNode freqNode;

        public CharNode() {// 简单的留空节点
            znextChar = new CharNode[37];
            for (int i = 0; i < 37; i++)
                znextChar[i] = null;
            freqNode = new FreqNode(-1);
        }

        public CharNode(char c) {// 明确本节点是什么字母    
            thisChar=WordKit.to_dash(c);
            znextChar = new CharNode[37];
            for (int i = 0; i < 37; i++)
                znextChar[i] = null;
            freqNode = new FreqNode(-1);
        }

        public boolean existCheck() {// 检查本节点对应的字母下的freq链表是否是空链表
            if (freqNode.znext == null)
                return (false);
            else
                return (true);
        }

        public void insertFreqNode(int counter) {// 标准的单个插入
            if (freqNode.frequency == 1) {// 有序插入
                FreqNode freqNodeBuf = searchFreqNode(counter, 1);
                FreqNode freqNodeBufNext = freqNodeBuf.znext;
                freqNodeBuf.znext = new FreqNode(counter, 1);
                freqNodeBuf.znext.znext = freqNodeBufNext;
            } else {// 无序头插
                FreqNode freqNodeBuf = freqNode.znext;
                freqNode.znext = new FreqNode(counter);
                freqNode.znext.znext = freqNodeBuf;
            }
        }

        public void insertFreqNode(int counter, int frequency) {// 插入带频率信息的节点，如果链表无序将会被有序化
            FreqNode freqNodeBuf = searchFreqNode(counter, 1);
            FreqNode freqNodeBufNext = freqNodeBuf.znext;
            freqNodeBuf.znext = new FreqNode(counter, frequency);
            freqNodeBuf.znext.znext = freqNodeBufNext;
        }

        public FreqNode searchFreqNode(int counter, int mode) {// mode0为默认方法的重载，mode1搜索首先会确认freq链是否有序（无序则会跑一次sort），然后返回最大的值不大于counter的节点，如果链表只有头则会返回头
            switch (mode) {
                case 0: {
                    return (searchFreqNode(counter));
                }
                case 1: {
                    if (freqNode.frequency == 0)
                        freqNode.initSort();
                    FreqNode freqNodeBuf = freqNode;
                    for (;;) {
                        if (freqNodeBuf.counter == counter)
                            break;
                        if (freqNodeBuf.znext == null)
                            break;
                        if (freqNodeBuf.znext.counter > counter)
                            break;
                    }
                    return (freqNodeBuf);
                }
                default: {
                    System.out.println("mode error");
                    return (null);
                }
            }
        }

        public FreqNode searchFreqNode(int counter) {// 返回counter匹配的节点，否则返回null
            if (freqNode.frequency == 1) {
                FreqNode freqNodeBuf = freqNode.znext;
                for (;;) {
                    if (freqNodeBuf == null)
                        return (null);
                    if (freqNodeBuf.counter > counter)
                        return (null);
                    if (freqNodeBuf.counter == counter)
                        return (freqNodeBuf);
                    freqNodeBuf = freqNodeBuf.znext;
                }
            } else {
                FreqNode freqNodeBuf = freqNode.znext;
                for (;;) {
                    if (freqNodeBuf == null)
                        return (null);
                    if (freqNodeBuf.counter == counter)
                        return (freqNodeBuf);
                    freqNodeBuf = freqNodeBuf.znext;
                }
            }
        }

        public void graft(CharNode charNode2) {// 嫁接，将charNode2上的freq链表拼接到本节点链表上，删除其head以外的节点，不对其它节点作出操作
            if (this.freqNode.frequency == 1 || charNode2.freqNode.frequency == 1) {// 有序拼接
                this.freqNode.initSort();
                charNode2.freqNode.initSort();

                FreqNode headBuf, freqNodeBuf, freqNodeBuf1 = this.freqNode.znext,
                        freqNodeBuf2 = charNode2.freqNode.znext;
                freqNodeBuf = new FreqNode(-1, 1);
                headBuf = freqNodeBuf;

                int shorter = 0;
                for (;;) {
                    if (freqNodeBuf1 == null)
                        shorter = 1;
                    if (freqNodeBuf2 == null)
                        shorter = 2;
                    if (shorter != 0)
                        break;

                    if (freqNodeBuf1.counter < freqNodeBuf2.counter) {
                        freqNodeBuf.znext = new FreqNode(freqNodeBuf1.counter, freqNodeBuf1.frequency);
                        freqNodeBuf = freqNodeBuf.znext;
                        freqNodeBuf1 = freqNodeBuf1.znext;
                    } else if (freqNodeBuf1.counter == freqNodeBuf2.counter) {
                        freqNodeBuf.znext = new FreqNode(freqNodeBuf1.counter,
                                freqNodeBuf1.frequency + freqNodeBuf2.frequency);
                        freqNodeBuf = freqNodeBuf.znext;
                        freqNodeBuf1 = freqNodeBuf1.znext;
                        freqNodeBuf2 = freqNodeBuf2.znext;
                    } else {
                        freqNodeBuf.znext = new FreqNode(freqNodeBuf2.counter, freqNodeBuf2.frequency);
                        freqNodeBuf = freqNodeBuf.znext;
                        freqNodeBuf2 = freqNodeBuf2.znext;
                    }
                }

                if (shorter == 1)
                    freqNodeBuf.znext = freqNodeBuf2;
                else
                    freqNodeBuf.znext = freqNodeBuf1;
                this.freqNode = headBuf;
                charNode2.freqNode.znext = null;
            } else {// 无序拼接
                FreqNode freqNodeBuf = this.freqNode.getTail();
                freqNodeBuf.znext = charNode2.freqNode.znext;
                charNode2.freqNode.znext = null;
            }
        }

        public void merge(CharNode charNode2) {// 合并，把以charNode2为根的子树合并到本节点上，请务必保证本节点和它节点是“同位体”
            graft(charNode2);
            for (int i = 0; i < 37; i++) {
                if (this.znextChar[i] == null) {
                    this.znextChar[i] = charNode2.znextChar[i];
                    charNode2.znextChar[i] = null;
                } else if (charNode2.znextChar[i] != null)
                    this.znextChar[i].merge(charNode2.znextChar[i]);
            }
        }

        public class FreqNode {
            public int counter;// 为-1代表是一个head节点
            public int frequency;// head节点的该值为0代表未经初始化（无序），为1代表有序（升序！）
            public FreqNode znext;

            public FreqNode(int counter) {// 只含counter的默认创建
                this.counter = counter;
                this.frequency = 0;
                this.znext = null;
            }

            public FreqNode(int counter, int frequency) {// 用于有序插入
                this.counter = counter;
                this.frequency = frequency;
                this.znext = null;
            }

            public int getLength() {// 返回自己以后（不含自己）的链表节点数量
                if(this.znext==null)return(0);
                else return(this.znext.getLength()+1);
            }

            public FreqNode getTail() {
                if (this.znext == null)
                    return (this);
                else
                    return (this.znext.getTail());
            }

            // 对以本节点为head的无序链表的counter升序排序，并且会对同counter的节点作合并处理，如果链表已经有序则无操作直接返回
            public void initSort() {
                if (counter != -1) {
                    System.out.println("cannot run initSort in no-head node");
                    return;
                }
                if (frequency == 1) {
                    // System.out.println("already initiated");
                    return;
                }
                this.frequency = 1;
                int num = getLength();

                FreqNode freqNode = this.znext;
                int[] buf = new int[num];
                freqNode = this.znext;
                for (int i = 0;;) {
                    if (freqNode == null)
                        break;
                    buf[i] = freqNode.counter;
                    freqNode = freqNode.znext;
                }

                Arrays.sort(buf);

                freqNode = this.znext;
                for (int i = 0;;) {
                    if (freqNode == null)
                        break;
                    freqNode.counter = buf[i];
                    freqNode = freqNode.znext;
                }

                // 合并同类项
                freqNode = this.znext;
                freqNode.frequency = 1;
                for (;;) {
                    if (freqNode == null)
                        break;
                    if (freqNode.znext == null)
                        break;
                    if (freqNode.counter == freqNode.znext.counter) {
                        freqNode.frequency++;
                        freqNode.znext = freqNode.znext.znext;
                    } else {
                        freqNode.znext.frequency = 1;
                        freqNode = freqNode.znext;
                    }
                }

            }

        }
    }

    public InvertedIndexTree() {
        headChar = new CharNode();
        for (int i = 0; i < 37; i++) {
            headChar.znextChar[i] = new CharNode(WordKit.int37toc(i));
        }
    }

    public CharNode searchCharNode(String word) {// 标准搜索，返回word对应的charnode，如无则返回null
        CharNode charNode = headChar;

        int char37;
        for (int i = 0; i < word.length(); i++) {
            char37 = WordKit.cto37int(word.charAt(i));
            if (charNode.znextChar[char37] == null)
                return (null);
            charNode = charNode.znextChar[char37];
        }
        return (charNode);
    }

    public CharNode searchCharNode(String word, int mode) {// mode0是标准搜索，mode1会把搜索的这个单词对应的子树（如无）创建出来并返回单词对应的charnode
        switch (mode) {
            case 0: {
                return (searchCharNode(word));
            }
            case 1: {
                CharNode charNode = headChar;
                int char37;
                for (int i = 0; i < word.length(); i++) {
                    char37 = WordKit.cto37int(word.charAt(i));
                    if (charNode.znextChar[char37] == null)
                        charNode.znextChar[char37] = new CharNode(word.charAt(i));
                    charNode = charNode.znextChar[char37];
                }
                return (charNode);
            }
            default: {
                System.out.println("mode error in searchCharNode");
                return (null);
            }
        }
    }

    public void insertWord(String word, int counter) {// 标准的词插入
        CharNode charNode = searchCharNode(word, 1);
        charNode.insertFreqNode(counter);
    }

    public void saveAt(String path, int threadNum) throws InterruptedException {
        RunnableSave[] runnableSaveArray = new RunnableSave[threadNum];
        for (int i = 0; i < threadNum - 1; i++)
            runnableSaveArray[i] = new RunnableSave(this.headChar.znextChar, (37 / threadNum) * i,
                    ((37) / threadNum) * (i + 1) - 1, path);
        runnableSaveArray[threadNum - 1] = new RunnableSave(this.headChar.znextChar, (37 / threadNum) * (threadNum - 1),
                37 - 1, path);

        Thread threadArray[] = new Thread[threadNum];
        for (int i = 0; i < threadNum; i++)
            threadArray[i] = new Thread(runnableSaveArray[i], "Thread：" + i);
        for (int i = 0; i < threadNum; i++)
            threadArray[i].start();
        for (int i = 0; i < threadNum; i++)
            threadArray[i].join();
    }

    class RunnableSave implements Runnable {
        private int begin, end;
        private CharNode[] charNodeArray;
        private String path;

        public RunnableSave(CharNode[] charNodeArray, int begin, int end, String path) {
            this.begin = begin;
            this.end = end;
            this.charNodeArray = charNodeArray;
            this.path = path;
        }

        public void run() {
            for (int i = begin; i <= end; i++) {
                CharNode charNode = charNodeArray[i];
                System.out.println("creating index file:" + (i + 1));
                File tempfile = new File(path + "\\_" + charNode.thisChar + ".txt");

                try {
                    tempfile.createNewFile();
                    FileWriter fileWriter = new FileWriter(tempfile);
                    String jsonString = JSON.toJSONString(charNode);
                    fileWriter.write(jsonString);
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadFrom(String path, int threadNum) throws InterruptedException {
        RunnableLoad[] runnableLoadArray = new RunnableLoad[threadNum];
        for (int i = 0; i < threadNum - 1; i++)
            runnableLoadArray[i] = new RunnableLoad(this.headChar.znextChar, (37 / threadNum) * i,
                    ((37) / threadNum) * (i + 1) - 1, path);
        runnableLoadArray[threadNum - 1] = new RunnableLoad(this.headChar.znextChar, (37 / threadNum) * (threadNum - 1),
                37 - 1, path);

        Thread threadArray[] = new Thread[threadNum];
        for (int i = 0; i < threadNum; i++)
            threadArray[i] = new Thread(runnableLoadArray[i], "Thread：" + i);
        for (int i = 0; i < threadNum; i++)
            threadArray[i].start();
        for (int i = 0; i < threadNum; i++)
            threadArray[i].join();
    }

    class RunnableLoad implements Runnable {
        private int begin, end;
        private CharNode[] charNodeArray;
        private String path;

        public RunnableLoad(CharNode[] charNodeArray, int begin, int end, String path) {
            this.begin = begin;
            this.end = end;
            this.charNodeArray=charNodeArray;
            this.path = path;
        }

        public void run() {
            for (int i = begin; i <= end; i++) {
                CharNode charNode=null;
                File tempfile = new File(path + "\\_" + WordKit.int37toc(i) + ".txt");

                try {
                    FileReader fileReader = new FileReader(tempfile);
                    char[] charBuf=new char[1048576];                                                   //按需
                    int len=fileReader.read(charBuf);
                    String jsonString=new String(charBuf,0,len);
                    fileReader.close();
                    charNode=JSON.parseObject(jsonString,CharNode.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                charNodeArray[i]=charNode;
            }
        }
    }
}