package pretreatment;

public class PretreatedFile {//类化的已预处理的文件
    private String originalPath;
    private String[] wordsArray;
    private int wordsNum;
    
    public void setOriginalPath(String originalPath) {
    	this.originalPath=originalPath;
    }
    public String getOriginalPath() {
    	return(this.originalPath);
    }

    public void setWordsArray(String[] wordsArray) {
    	this.wordsArray=wordsArray;
    }
    public String[] getWordsArray() {
    	return(this.wordsArray);
    }
    
    public void setWordsNum(int wordsNum) {
    	this.wordsNum=wordsNum;
    }
    public int getWordsNum() {
    	return(wordsNum);
    }
}
