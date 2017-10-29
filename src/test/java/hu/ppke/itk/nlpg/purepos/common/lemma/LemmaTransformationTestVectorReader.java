package hu.ppke.itk.nlpg.purepos.common.lemma;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class LemmaTransformationTestVectorReader {

    static String SEPARATOR = "\t";

    static public LemmaTransformationTestVector read(String inputPath) throws FileNotFoundException{
            Scanner sc = new Scanner(new File(inputPath));
            ArrayList<String> token = new ArrayList<String>();
            ArrayList<String> lemma = new ArrayList<String>();;
            ArrayList<String> expectedLemmaStuff = new ArrayList<String>();
            ArrayList<Integer> expectedCode = new ArrayList<Integer>();
            while(sc.hasNext()){
                String[] line = parseLine(sc.nextLine());
                token.add(line[0]);
                lemma.add(line[1]);
                expectedLemmaStuff.add(line[2]);
                expectedCode.add(Integer.parseInt(line[3]));
            }
        return new LemmaTransformationTestVector(token.toArray(new String[token.size()]),lemma.toArray(new String[lemma.size()]),
                expectedLemmaStuff.toArray(new String[expectedLemmaStuff.size()]),expectedCode.toArray(new Integer[expectedCode.size()]));
    };

    static private String[] parseLine(String line){
        String[] params = line.split(SEPARATOR);
        return params;
    }

    static public class LemmaTransformationTestVector{
        protected String[] token;
        protected String[] lemma;
        protected String[] expectedLemmaStuff;
        protected Integer[] expectedCode;

        public LemmaTransformationTestVector(String[] _token, String[] _lemma,String[] _expectedLemmaStuff, Integer[] _expectedCode){
            token = _token;
            lemma = _lemma;
            expectedLemmaStuff = _expectedLemmaStuff;
            expectedCode = _expectedCode;
        }

    }
}
