import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;

//
// WordMap class will make a map of word, and how many time it has been repeated
//

public class WordMap {
    private Map<String,Integer> wordmap;
    private double nFile;

    //Constructor
    WordMap(){
        wordmap = new TreeMap<>();
        nFile = 0;
    }

    //This method will increment word count
    public void incrementWord(String word){
        if (!wordmap.containsKey(word)){
            wordmap.put(word, 1);
        } else {
            wordmap.put(word, (wordmap.get(word) + 1) );
        }
    }

    //This method will return the word count
    public Integer getWord(String word){
        if (wordmap.containsKey(word)){
            return wordmap.get(word);
        }

        return 0;
    }

    //This method will iterate through words
    public Iterator<String> getIterWord(){
        return wordmap.keySet().iterator();
    }

    //This method will increment the file counter
    public void incrementsFile(){nFile+=1;}

    //This method will return the count of file
    public double getnFile(){return this.nFile;}

}
