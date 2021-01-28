package pretreatment;

import java.beans.Expression;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.alibaba.fastjson.JSON;

import assets.WordKit;

//每个字母节点存储一个后续字母数组，和一个频率统计链表，该链表是“从树根到当前位置路径上的节点拼成的单词对应的频率统计信息”
//频率链表的第一个节点用于储存有序无序相关信息，初次初始化构建链表的时候简单拼接各个文档的单词出现信息，这是无序的，链表中的所有freq都应当是0
//链表对应的单词首次被搜索会使它启动有序化
//对两个树进行merge操作的时候，对于同单词合并，当且仅当两个链表都是无序链表时才使用简单拼接成一个新的无序链表，否则拼接成有序链表

//有一些用gvt开头的，其实本来写的是get的，但是傻逼fastjson封装对象的时候会把这些用get开头的function跑一遍然后把结果储存成一个静态的对象，为了阻止它这样干就写作gvt了

public class InvertedIndexTree {
    public CharNode headChar; // wordtree的头，本身不代表任何字母
    public int counts;//存储counter的数量，用于在布尔检索中求反

    public static class CharNode {
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
                FreqNode freqNodeBuf = this.freqNode.gvtTail();
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

        public int[] gvtCounterArrayOfFreqList(){//把本节点下的freq链表有序化，然后抽出其中的counter信息组成链表返回
            this.freqNode.initSort();
            if(this.freqNode.gvtLength()==0)return(null);
            int[] counterArray=new int[this.freqNode.gvtLength()];
            FreqNode freqNodeBuf=this.freqNode.znext;
            for(int i=0;;){
                counterArray[i]=freqNodeBuf.counter;
                i++;
                freqNodeBuf=freqNodeBuf.znext;
                if(freqNodeBuf==null)break;
            }
            //System.out.println(JSON.toJSONString(counterArray));
            return(counterArray);
        }

        public static class FreqNode {
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

            public int gvtLength() {// 返回自己以后（不含自己）的链表节点数量
                if(this.znext==null)return(0);
                else return(this.znext.gvtLength()+1);
            }

            public FreqNode gvtTail() {
                if (this.znext == null)
                    return (this);
                else
                    return (this.znext.gvtTail());
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
                if(this.znext==null)return;
                int num = gvtLength();

                FreqNode freqNode = this.znext;
                int[] buf = new int[num];
                freqNode = this.znext;
                for (int i = 0;;i++) {
                    if (freqNode == null)
                        break;
                    buf[i] = freqNode.counter;
                    freqNode = freqNode.znext;
                }

                Arrays.sort(buf);

                freqNode = this.znext;
                for (int i = 0;;i++) {
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

    public InvertedIndexTree() {    //注意留有测试期代码
        headChar = new CharNode();
        for (int i = 0; i < 37; i++) {
            headChar.znextChar[i] = new CharNode(WordKit.int37toc(i));
        }
    }

    public CharNode searchCharNode(String word) {// 标准搜索，返回word对应的charnode，如无则返回null，注意，返回非null不代表这个词存在，只代表这个节点存在
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

    public CharNode searchCharNode(String word, int mode) {// mode0是标准搜索，mode1会把搜索的这个单词对应的子树（如无）创建出来并返回单词对应的charnode，mode2执行标准搜索，但是会把搜到的那个charnode下的freq链表有序化
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
            case 2:{
                CharNode charNode=searchCharNode(word);
                if(charNode!=null)charNode.freqNode.initSort();
                return(charNode);
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
        private int logSignal;

        public RunnableSave(CharNode[] charNodeArray, int begin, int end, String path) {
            this.begin = begin;
            this.end = end;
            this.charNodeArray = charNodeArray;
            this.path = path;
        }

        public void run() {
            if(Thread.currentThread().getName().compareTo("Thread：" + 0)==0)this.logSignal=1;
            else this.logSignal=0;

            for (int i = begin; i <= end; i++) {
                CharNode charNode = charNodeArray[i];
                System.out.println("creating index file:" + (i + 1) + " of "+(end+1));
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
        private int logSignal;

        public RunnableLoad(CharNode[] charNodeArray, int begin, int end, String path) {
            this.begin = begin;
            this.end = end;
            this.charNodeArray=charNodeArray;
            this.path = path;
        }

        public void run() {
            if(Thread.currentThread().getName().compareTo("Thread：" + 0)==0)this.logSignal=1;
            else this.logSignal=0;

            for (int i = begin; i <= end; i++) {
                CharNode charNode=null;
                File tempfile = new File(path + "\\_" + WordKit.int37toc(i) + ".txt");

                try {
                    FileReader fileReader = new FileReader(tempfile);
                    char[] charBuf=new char[1073741824];                                                   //按需
                    int len=fileReader.read(charBuf);
                    String jsonString=new String(charBuf,0,len);
                    charBuf=null;
                    fileReader.close();
                    //System.out.println(Thread.currentThread().getName()+"--this is "+WordKit.int37toc(i));
                    charNode=JSON.parseObject(jsonString,InvertedIndexTree.CharNode.class);
                    //System.out.println("this is "+WordKit.int37toc(i));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                charNodeArray[i]=charNode;
            }
        }
    }

    public int[] boolSearcher(String expression){//句首必须是左括号，返回从该括号到其匹配右括号为止的表达式的结果，变量必须被且仅被一对括号环绕，严禁无端空格
        int depth=0;
        char operator=0;
        int i=0;
        int[] result;
        for(;;i++){
            if(i>=expression.length()){
                System.out.println("invalid input");
                return(null);
            }
            switch(expression.charAt(i)){
                case '(':{
                    depth++;
                    break;
                }
                case ')':{
                    depth--;
                    break;
                }
                case '&':{
                    if(depth==1)operator='&';
                    break;
                }
                case '|':{
                    if(depth==1)operator='|';
                    break;
                }
                case '^':{
                    if(depth==1)operator='^';
                    break;
                }
                default:break;
            }
            if(depth==0)break;
            if(operator!=0)break;
        }

        if(depth==0){
            String stringBuf;
            stringBuf=expression.substring(1,i);

            stringBuf=WordKit.stemming(stringBuf);
            char[] stringBuf2=stringBuf.toCharArray();
            for(int j=0;j<stringBuf.length();j++)stringBuf2[j]=WordKit.to_dash(stringBuf2[j]);
            stringBuf=new String(stringBuf2);

            if(searchCharNode(stringBuf,2)!=null)result=searchCharNode(stringBuf,2).gvtCounterArrayOfFreqList();
            else result=null;
            if(result==null)result=new int[0];
            return(result);
        }

        switch(operator){
            case '&':{
                return(counterArrayAnd(boolSearcher(expression.substring(1)),boolSearcher(expression.substring(i+1))));
            }
            case '|':{
                return(counterArrayOr(boolSearcher(expression.substring(1)),boolSearcher(expression.substring(i+1))));
            }
            case '^':{
                return(counterArrayNot(boolSearcher(expression.substring(i+1))));
            }
        }

        System.out.println("invalid input");
        return(null);
    }

    public int[] counterArrayAnd(int[] counterArray1,int[] counterArray2){
        if(counterArray1==null)counterArray1=new int[0];
        if(counterArray2==null)counterArray2=new int[0];
		ArrayList<Integer> counterArrayList=new ArrayList<Integer>();
		for(int i=0,j=0;i<counterArray1.length && j<counterArray2.length;) {
			if(counterArray1[i] == counterArray2[j]) {
				counterArrayList.add(counterArray1[i]);
				i++;
				j++;
			}
			else if(counterArray1[i] > counterArray2[j]) {
				j++;
			}
			else {
				i++;
			}	
		}
 
		int[] result = new int[counterArrayList.size()];
		for(int i=0;i<result.length;i++)result[i]=counterArrayList.get(i);
		return(result);
    }
    public int[] counterArrayOr(int[] counterArray1,int[] counterArray2){
        if(counterArray1==null)counterArray1=new int[0];
        if(counterArray2==null)counterArray2=new int[0];
        ArrayList<Integer> counterArrayList=new ArrayList<Integer>();
        
        int i,j;
		for(i=0,j=0;i<counterArray1.length && j<counterArray2.length;) {
			if(counterArray1[i] == counterArray2[j]) {
				counterArrayList.add(counterArray1[i]);
				i++;
				j++;
			}
			else if(counterArray1[i] > counterArray2[j]) {
                counterArrayList.add(counterArray2[j]);
				j++;
			}
			else {
                counterArrayList.add(counterArray1[i]);
				i++;
			}	
        }
        if(i<counterArray1.length)for(;i<counterArray1.length;i++)counterArrayList.add(counterArray1[i]);
        if(j<counterArray2.length)for(;j<counterArray2.length;j++)counterArrayList.add(counterArray2[j]);
 
		int[] result = new int[counterArrayList.size()];
		for(int k=0;k<result.length;k++)result[k]=counterArrayList.get(k);
		return(result);
    }
    public int[] counterArrayNot(int[] counterArray){
        if(counterArray==null)counterArray=new int[0];
        int i=0,j=0;
        ArrayList<Integer> counterArrayList=new ArrayList<Integer>();
        if(counterArray.length!=0)for(i=0;i<counts;i++){
            if(counterArray[j]!=i)counterArrayList.add(i);
            else if(j<counterArray.length-1)j++;
        }
        else for(i=0;i<=counts;i++)counterArrayList.add(i);
        int[] result = new int[counterArrayList.size()];
		for(int k=0;k<result.length;k++)result[k]=counterArrayList.get(k);
		return(result);
    }
}