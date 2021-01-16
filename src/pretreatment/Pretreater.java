package pretreatment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Pretreater {
    private String string;

    public Pretreater() {
    }

    public void setFile(File file) throws IOException {
        char[] c=new char[16384];
        FileReader fileReader=new FileReader(file);
        fileReader.read(c);
        string=new String(c);
        fileReader.close();

        //System.out.println(string);
    }

    public String pretreat(){
        wordSegment();
        stem();
        removeStopWords();
        return(string);
    }

    private void wordSegment(){

    }

    private void stem(){

    }

    private void removeStopWords(){

    }
}
