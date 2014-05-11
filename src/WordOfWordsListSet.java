// author = 'mvonderlieth'

import java.io.*;
import java.util.*;

// simple class to process a list of words and find the longest word-of-words in the list!
public class WordOfWordsListSet
{
    protected int numberOfWords = 0;
    protected int shortestWordLength = 255;
    protected int longestWordLength = 0;
    protected int numberOfWordOfWordsFound = 0;
    protected int longestWordOfWordsLength = 0;
    protected static String longestWordOfWords = "No word-of-words found!";
    protected static String secondLongest = "Second longest word-of-words not found!";

    protected ArrayList<String> wordsList = null;
    protected Set<String> wordsSet = null;
    protected ArrayList<String> wordsWordList = null;
    protected ArrayList<String> longestWordOfWordsWordList = null;

    public WordOfWordsListSet()
    {
        wordsList = new ArrayList<java.lang.String>();
        wordsSet = new TreeSet<String>();
        wordsWordList = new ArrayList<java.lang.String>();
        longestWordOfWordsWordList = new ArrayList<java.lang.String>();
    }

    public boolean loadSortedWordsFromFile(String wordsFilePath, boolean skipSingleCharacterWords)
    {
        System.out.println("loading words from file=" + wordsFilePath + "...");
        boolean loaded = false;
        longestWordLength = 0;

        int minLength = 0;
        if (skipSingleCharacterWords)
        {
            minLength = 1;
        }

        BufferedReader br = null;
        String line;

        try
        {
            br = new BufferedReader(new FileReader(wordsFilePath));

            while ((line = br.readLine()) != null)
            {
                String word = line.trim();
                int length = word.length();

                if (length > minLength)
                {
                    loaded = true;
                    numberOfWords += 1;

                    if (numberOfWords % 10000 == 0)
                    {
                        System.out.print(". ");
                    }

                    wordsList.add(word);
                    wordsSet.add(word);


                    if (length != 0 && length < shortestWordLength)
                    {
                        shortestWordLength = length;
                    }

                    if (length > longestWordLength)
                    {
                        longestWordLength = length;
                    }

                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.err.flush();
        }
        finally
        {
            try
            {
                if (null != br) br.close();
            }
            catch (IOException ignored)
            {
            }
        }

        System.out.println();
        return (loaded);
    }

    public void processListOfWords(boolean leftToRight)
    {
        System.out.println("processing words...");
        int count = 1;

        for (String word : wordsList)
        {
            if (count % 10000 == 0)
            {
                System.out.print(". ");
            }
            int length = word.length();

            boolean found;

            if (leftToRight)
            {
                found = processWordOfWords(word, length);
            }
            else
            {
                found = processWordOfWordsFromEnd(word, length);
            }

            if (found)
            {
                numberOfWordOfWordsFound += 1;
                if (length > longestWordOfWordsLength)
                {
                    if (longestWordOfWordsLength > 0)
                    {
                        secondLongest = longestWordOfWords;
                    }
                    longestWordOfWordsLength = length;
                    longestWordOfWords = word;
                    longestWordOfWordsWordList = wordsWordList;
                }
                else
                {
                    if (length > secondLongest.length())
                    {
                        secondLongest = word;
                    }
                }
            }

            count += 1;

        }

        System.out.println();
    }

    //  walk word finding longest word from left-to-right
    public boolean processWordOfWords(String word, int length)
    {
        boolean found = false;
        int first = 0;
        int last = 1;

        wordsWordList = new ArrayList<String>();

        while (last < length + 1)
        {
            String tw = word.substring(first, last);
            if (wordsSet.contains(tw) && !tw.contentEquals(word))
            {
                wordsWordList.add(tw);
                first = last;
                last += 1;
            }
            else
            {
                last += 1;
            }
        }

        if (first == length)
        {
            found = true;
        }

        return found;
    }


    //  walk word finding longest word-in-word from right-to-left, so to speak
//  slightly faster than the other way
    public boolean processWordOfWordsFromEnd(String word, int length)
    {
        boolean found = false;
        int first = 1;
        int last = length;

        wordsWordList = new ArrayList<String>();

        while (first < last)
        {
            String tw = word.substring(first, last);

            if (wordsSet.contains(tw))
            {
                wordsWordList.add(tw);

                // found word, adjust first and last
                // special case first character
                if (first == 1)
                {
                    tw = word.substring(0, 1);
                    if (wordsSet.contains(tw))
                    {
                        first = 0;
                    }
                    break;
                }
                last = first;
                first = 0;
            }
            else
            {
                first += 1;
            }
        }

        if (first == 0)
        {
            found = true;
        }

        return found;
    }


    public void outputResults()
    {
        System.out.println();
        System.out.println("numberOfWords=" + numberOfWords);
        System.out.println("shortestWordLength=" + shortestWordLength);
        System.out.println("longestWordLength=" + longestWordLength);
        System.out.println("numberOfWordOfWordsFound=" + numberOfWordOfWordsFound);
        System.out.println("longestWordOfWordsLength=" + longestWordOfWordsLength);
        System.out.println("longestWordOfWords=" + longestWordOfWords);
        System.out.println("longestWordOfWordsWordList=" + longestWordOfWordsWordList);
        System.out.println("secondLongest=" + secondLongest);
        System.out.println();
    }

    public static void main(String args[])
    {
        Date d = new Date();

        System.out.println("Start " + d.toString());

//        String wordsFilePath = "testWords.txt";
        String wordsFilePath = "/usr/share/dict/words";

//        boolean leftToRight = false;
        boolean leftToRight = true;
        boolean skipSingleCharacterWords = true;

        WordOfWordsListSet wow = new WordOfWordsListSet();

        long startLoading = System.currentTimeMillis();
        boolean loaded = wow.loadSortedWordsFromFile(wordsFilePath, skipSingleCharacterWords);
        long endLoading = System.currentTimeMillis();

        if (loaded)
        {
            long startProcessing = endLoading;
            wow.processListOfWords(leftToRight);
            long endProcessing = System.currentTimeMillis();

            wow.outputResults();

            long loadTime = endLoading - startLoading;
            loadTime = (loadTime == 0) ? 1 : loadTime;
            long loadWordsPerSecond = wow.numberOfWords / loadTime;
            long processTime = endProcessing - startProcessing;
            processTime = (processTime == 0) ? 1 : processTime;
            long processWordsPerSecond = wow.numberOfWords / processTime;
            long elapsed = endProcessing - startLoading;

            System.out.println("Load Words Per Second=" + loadWordsPerSecond);
            System.out.println("Process Words Per Second=" + processWordsPerSecond);
            System.out.println("Elapsed Time=" + elapsed);


            d = new Date();
            System.out.println("End " + d.toString());
        }
    }
}
