package assets;

public class Separator {
    public static boolean isSeparator(char c){
        if(((c>='A')&&(c<='Z'))||((c>='a')&&(c<='z'))||((c>='0')&&(c<='9')))return(false);
        else return(true);
    }
}
