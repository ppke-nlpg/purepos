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
            ArrayList<Long> expectedCode = new ArrayList<Long>();
            ArrayList<Integer> expectedTag = new ArrayList<Integer>();
            while(sc.hasNext()){
                String[] line = parseLine(sc.nextLine());
                token.add(line[0]);
                lemma.add(line[1]);
                expectedLemmaStuff.add(line[2]);
                expectedCode.add(Long.parseLong(line[3]));
                expectedTag.add(Integer.parseInt(line[4]));
            }
        return new LemmaTransformationTestVector(token.toArray(new String[token.size()]),lemma.toArray(new String[lemma.size()]),
                expectedLemmaStuff.toArray(new String[expectedLemmaStuff.size()]),expectedCode.toArray(new Long[expectedCode.size()]),
                expectedTag.toArray(new Integer[expectedTag.size()]));
    };

    static private String[] parseLine(String line){
        String[] params = line.split(SEPARATOR);
        return params;
    }

    static public class LemmaTransformationTestVector{
        protected String[] token;
        protected String[] lemma;
        protected String[] expectedLemmaStuff;
        protected Long[] expectedCode;
        protected Integer[] expectedTag;

        public LemmaTransformationTestVector(String[] _token, String[] _lemma,String[] _expectedLemmaStuff,
                                             Long[] _expectedCode, Integer[] _expectedTag){
            token = _token;
            lemma = _lemma;
            expectedLemmaStuff = _expectedLemmaStuff;
            expectedCode = _expectedCode;
            expectedTag = _expectedTag;
        }

    }
}
