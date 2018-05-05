
import java.io.File;
import java.io.IOException;
import java.util.*;

//
// Filter class will test and train data, through Maps
//
// WordCount code was used in this code

public class Filter {
    private WordMap trainSpamFreq;
    private WordMap trainHamFreq;

    private boolean changes;
    private TreeMap<String,Double> map;

    //Constructor
    public Filter(){
        this.trainHamFreq = new WordMap();
        this.trainSpamFreq = new WordMap();
        this.changes = true;
        this.map = new TreeMap<>();
    }

    //Method train, has two Param
    //->File file, is a directory
    //->Wordmap map, contains the map for each word and the amount of time it shows up
    //  within all file
    //
    //This method will generate a word map for testing
    //
    //Note: A compilation error occurred stating an IOException, so ive added that within the function
    public void train(File file, WordMap map) throws IOException{
        if (file.isDirectory()){
            // process every file in directory
            File[] filesInDir = file.listFiles();
            for (int i = 0; i < filesInDir.length; i++){
                train(filesInDir[i], map);
            }
        }else if(file.exists()){
            TreeMap<String,Integer> tmpMap = new TreeMap<>();
            map.incrementsFile();
            Scanner scan = new Scanner(file);
            while(scan.hasNext()){
                String word = scan.next().toLowerCase();
                if(isWord(word) && !tmpMap.containsKey(word)){
                    tmpMap.put(word,1);
                    map.incrementWord(word);
                }

            }
            scan.close();
        }

    }

    //This method will train for spam files
    public void trainSpam(File file)throws IOException{
        train(file,trainSpamFreq);
    }

    //This method will train for ham files
    public void trainHam(File file)throws IOException{
        train(file,trainHamFreq);
    }

    //Pr(W|S)
    //This method will generate Spam Probability
    public double spamProbability(String word){
        return trainSpamFreq.getWord(word)/trainSpamFreq.getnFile();
    }

    //Pr(W|H)
    //This method will generate ham Probability
    public double hamProbability(String word){
        return trainHamFreq.getWord(word)/trainHamFreq.getnFile();
    }


    //This function will generate the spam probability through method spamProbability
    private void genSpamProb(){
        if (!changes) {
            return;
        }

        String word;
        double spProb;

        Iterator<String> iter = trainSpamFreq.getIterWord();
        while (iter.hasNext()){
            word = iter.next();
            spProb = spamProbability(word);
            //Pr(S|W)
            //Pr(S|W)
            map.put(word, (spProb / (spProb + hamProbability(word))));
        }
        changes = false;
    }

    public double probSpam(String word){
        genSpamProb();
        Double prob = map.get(word);

        if (prob != null) {
            return (prob);
        }else{
            return 0.0;
        }
    }


    //This function will generate the eta value
    //But will have to normalize if it exceeds value
    // as exceeding 1 means the probability is going to happen no matter what
    private double eta(String word){
        double spPr = probSpam(word);

        if (spPr >= 1){
            spPr = 0.9;
        } else if (spPr == 0){
            spPr = 0.1;
        }

        return Math.log(1-spPr) - Math.log(spPr);
    }

    //Method test takes two param
    //->File file, is a Directory
    //->List<TestFile> fileList, is a list for storing the data
    //
    //This method will test how accurate the spam and ham probability
    public void test(File file, List<TestFile> fileList) throws IOException {
        if (file.isDirectory()) {

            File[] filesInDir = file.listFiles();
            for (int i = 0; i < filesInDir.length; i++) {
                test(filesInDir[i], fileList);
            }

        } else if (file.exists()) {

            Scanner scan = new Scanner(file);
            double etaVar = 0;
            while (scan.hasNext()) {
                String word = scan.next().toLowerCase();
                if (isWord(word)) {
                    etaVar += eta(word);
                }
            }

            // calculate probability that file is spam
            double prSF = 1 / (1 + Math.pow(Math.E, etaVar));
            String parentName;

            if (file.getParentFile().getName().equals("ham")){
                parentName = "ham";
            }else{
                parentName = "spam";
            }

            fileList.add(new TestFile(file.getName(), prSF, parentName));
            scan.close();
        }

    }


    public double getPrecision(List<TestFile> fileList, String classSH){

        double precision = 0;
        int comparisons = 0;
        for (int i = 0; i < fileList.size(); i++){
            for (int j = i + 1; j < fileList.size(); j++){
                if (fileList.get(i).getActualClass().equals(classSH) && fileList.get(j).getActualClass().equals(classSH)) {

                    precision += Math.abs(fileList.get(i).getSpamProbability() - fileList.get(j).getSpamProbability());
                    comparisons++;
                }
            }
        }
        if (comparisons > 0) {
            return (1 - (precision / comparisons));
        }

        return 0;
    }

    public double getAccuracyHam(List<TestFile> fileList){
        double accuracy = 0;
        int comparisons = 0;
        for (int i = 0; i < fileList.size(); i++){
            if (fileList.get(i).getActualClass().equalsIgnoreCase("ham")){
                accuracy += 1 - fileList.get(i).getSpamProbability();
                comparisons++;
            }
        }
        return compare(accuracy,comparisons);

    }
    public double getAccuracySpam(List<TestFile> fileList){
        double accuracy = 0;
        int comparisons = 0;
        for (int i = 0; i < fileList.size(); i++){
            if (fileList.get(i).getActualClass().equalsIgnoreCase("spam")){
                accuracy += (fileList.get(i).getSpamProbability());
                comparisons++;
            }
        }
        return compare(accuracy,comparisons);
    }

    private double compare(double accuracy, int com){
        if(com > 0){
            return accuracy/= com;
        }
        return 0;
    }

    //====================================================================================
    //
    //N-GRAM METHOD
    //
    //====================================================================================

    //Basically repeated code, but with N-Gram functionality

    public void trainSpamN(File file, int n)throws IOException {
        trainNGram(file,trainSpamFreq, n);
    }

    //This method will train for ham files
    public void trainHamN(File file, int n)throws IOException{
        trainNGram(file,trainHamFreq, n);
    }


    public void trainNGram(File file, WordMap map, int n) throws IOException{
        if (file.isDirectory()){
            // process every file in directory
            File[] filesInDir = file.listFiles();
            for (int i = 0; i < filesInDir.length; i++){
                trainNGram(filesInDir[i], map, n);
            }
        }else if(file.exists()){
            TreeMap<String,Integer> tmpMap = new TreeMap<>();
            map.incrementsFile();
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()){
                String word = scan.nextLine().toLowerCase();

                for(String ngram : ngrams(n,word)){
                    if(!tmpMap.containsKey(ngram)){
                        tmpMap.put(ngram,1);
                        map.incrementWord(ngram);
                    }
                }

            }
            scan.close();
        }

    }


    public void testNGram(File file, List<TestFile> fileList, int n) throws IOException {
        if (file.isDirectory()) {

            File[] filesInDir = file.listFiles();
            for (int i = 0; i < filesInDir.length; i++) {
                testNGram(filesInDir[i], fileList, n);
            }

        } else if (file.exists()) {

            Scanner scan = new Scanner(file);
            double etaVar = 0;
            while(scan.hasNextLine()){
                String word = scan.nextLine().toLowerCase();

                for(String ngram : ngrams(n,word)){
                    etaVar += eta(ngram);
                }

            }

            // calculate probability that file is spam
            double prSF = 1 / (1 + Math.pow(Math.E, etaVar));
            String parentName;

            if (file.getParentFile().getName().equals("ham")){
                parentName = "ham";
            }else{
                parentName = "spam";
            }

            fileList.add(new TestFile(file.getName(), prSF, parentName));
            scan.close();
        }

    }

    public static boolean isWord(String word){
        return word.matches("^[a-zA-Z]*$");
    }



    public static List<String> ngrams(int n, String str) {
        List<String> ngrams = new ArrayList<>();
        String[] words = str.split(" ");
        for (int i = 0; i < words.length - n + 1; i++) {
            if(isWord(words[i])) {
                ngrams.add(concat(words, i, i + n));
            }
        }

        return ngrams;
    }

    public static String concat(String[] words, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++)
            sb.append((i > start ? " " : "") + words[i]);
        return sb.toString();
    }



}
