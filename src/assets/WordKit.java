package assets;

public class WordKit {//简单的字词工具

    //一元分隔符判断器
    public static boolean isSeparator(char c){
        if((c>='0')&&(c<='9'))return(true);//测试期间把数字排掉

        if(((c>='A')&&(c<='Z'))||((c>='a')&&(c<='z'))||((c>='0')&&(c<='9')))return(false);
        else return(true);
    }
    //用于词内单个连接符的分隔符判断器
    public static boolean isSeparator(String string,int i){

        if(!isSeparator(string.charAt(i)))return(false);
        else if(i<=0||i>=string.length()-1)return(true);
        else if((!isSeparator(string.charAt(i-1)))&&(!isSeparator(string.charAt(i+1)))){
            String stringbuf=new String("|-|'|_|");
            if(stringbuf.indexOf("|"+string.charAt(i)+"|")==-1)return(true);
            return(false);
        }
        else return(true);
    }

    public static void deleteSeparator(String word){
        
    }

    //用长度为37的数组存放字符信息的时候，正数的0~25代表26小写字母，26~35代表数字，如果都不是，则放在36号位，这个位还原时一律还原成'_'
    public static int cto37int(char c){
        if((c>='a')&&(c<='z'))return(c-'a');
        else if((c>='0')&&(c<='9'))return(c-'0'+26);
        else return(36);
    }
    public static char int37toc(int i){
        if(i<=25)return (char) ('a' + i);
        else if(i<=35)return (char) ('0' + i - 26);
        else return('_');
    }
    public static char to_dash(char c){
        if(isSeparator(c))return('_');
        else return(c);
    }


    //停用词判断器
    public static boolean isStopWord(String word){

        if(word.length()<=2)return(true);

        String stopWords=new String(//脏方法，建设于源码层面的停用词库
            "|at|in|on|above|over|below|under|between|among|before|"
            +"after|behind|about|around|beside|by|near|toward|towards|"
            +"through|throughtout|within|without|across|along|down|up|"
            +"from|into|off|to|with|beyond|upon|"
            +"re|"
            +"i|me|my|mine|you|your|yours|he|his|she|her|we|our|ours|they|their|theirs|"
            +"id|enron|message|com|time|times|unit|text|if|new|mail|"
            +"date|us|bit|ascii|the|for|and|is|of|this|have|all|are|be|am|will|that|it|can|or|as|not|an|any|do|here|get|what|where|which|when|why|web|whom|who|whose|has|may|out|"
            +"but|there|email|so|filename|version|bcc|cc|"
            +"ww|www|"
        );

        if(stopWords.indexOf("|"+word+"|")==-1)return(false);
        else return(true);
    }

    //词根还原器,目前只做了全小写化
    public static String stemming(String word){
        return(word.toLowerCase());
    }
}
