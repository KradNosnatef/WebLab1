package assets;

public class WordKit {//简单的字词工具

    //分隔符判断器
    public static boolean isSeparator(char c){
        if(((c>='A')&&(c<='Z'))||((c>='a')&&(c<='z')))return(false);
        else return(true);
    }

    public static int cto36int(char c){
        if((c>='a')&&(c<='z'))return(c-'a');
        else return(c-'0'+26);
    }
    public static char int36toc(int i){
        if(i<=25)return (char) ('a' + i);
        else return (char) ('0' + i - 26);
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
