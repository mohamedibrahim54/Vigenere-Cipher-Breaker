/**
 * Vigenere Cipher Breaker (Unknown Language, Unknown Key Length)
 * it can crack messages in any language (language existed in dictionaries folder)
 *
 * @author Mohamed Ibrahim
 * @version 1.0
 */

import java.util.*;
import java.io.*;
import edu.duke.*;

public class VigenereBreaker {
    public String sliceString(String message, int whichSlice, int totalSlices) {
        StringBuilder sb = new StringBuilder();
        for(int k = whichSlice; k < message.length(); k += totalSlices){
            sb.append(message.charAt(k));
        }
        return sb.toString();
    }

    public int[] tryKeyLength(String encrypted, int klength, char mostCommon) {
        int[] key = new int[klength];
        for(int k = 0; k < key.length; k++){
            String slice = sliceString(encrypted, k, klength);
            CaesarCracker cc = new CaesarCracker(mostCommon);
            key[k] = cc.getKey(slice);
        }
        return key;
    }

    public HashSet<String> readDictionary(FileResource fr){
        HashSet<String> words = new HashSet<String>();
        for(String line : fr.lines()){
            String word = line.toLowerCase();
            words.add(word);
        }
        return words;
    }

    //This method return the integer count of how many valid words it found.
    public int countWords(String message, HashSet<String> dicWords){
        int count = 0;
        for(String word : message.split("\\W+") ){
            if( dicWords.contains(word.toLowerCase()) ){
                count++;
            }
        }
        return count;
    }
    
    public char getMaxCharOccurrence(String message){
        String alpha = "abcdefghijklmnopqrstuvwxyz";
        int[] counts = new int[26];
        for(int k = 0; k< message.length(); k++){
            char ch = Character.toLowerCase(message.charAt(k));
            int dex = alpha.indexOf(ch);
            if(dex != -1){
                counts[dex] += 1;
            }
        }
        int index = indexOfMax(counts);
        return ( alpha.charAt(index) );
    }
    
    public int indexOfMax(int[] counts){
        int maxdex = 0;
        for(int k = 0; k < counts.length; k++){
            if(counts[k] > counts[maxdex]){
                maxdex = k;
            }
        }
        return maxdex;
    }
    
    public char mostCommonCharIn(HashSet<String> dicWords){
        StringBuilder sb = new StringBuilder();
        for(String word : dicWords){
            sb.append(word);
        }
        return getMaxCharOccurrence(sb.toString());
    }
    
    public String breakForAllLangs(String encrypted, HashMap<String,HashSet<String>> languages){
        int maxCount = 0;
        String decrypted = "";
        String lang = "";
        for(String language : languages.keySet()){
            HashSet<String> dicWords = languages.get(language);
            String s = breakForLanguage(encrypted, dicWords);
            int count = countWords(s, dicWords);
            if(maxCount < count){
                maxCount = count;
                lang = language;
                decrypted = s;
            }
        }
        System.out.println("language: "  + lang);
        System.out.println("valid words: " + maxCount);
        return decrypted;
    }

    public String breakForLanguage(String message, HashSet<String> dicWords){
        int maxCount = 0;
        int klength = 0;
        String result = "";
        char commonChar = mostCommonCharIn(dicWords);
        for(int k = 1; k <= 100; k++){
            int[] key = tryKeyLength(message, k, commonChar);
            VigenereCipher vc = new VigenereCipher(key);
            String decrypted = vc.decrypt(message);
            int count = countWords(decrypted, dicWords);
            if(maxCount < count){
                maxCount = count;
                klength = k;
                result = decrypted;
            }
        }
        System.out.println("valid words: " + maxCount);
        System.out.println("key length: " + klength);
        return result;
    }

    public void breakVigenere () {
        FileResource fr = new FileResource();
        String encrypted = fr.asString();
        DirectoryResource dictionaries = new DirectoryResource();
        HashMap<String,HashSet<String>> languages = new HashMap<String,HashSet<String>>();
        for(File f: dictionaries.selectedFiles()){
            languages.put(f.getName(), readDictionary(new FileResource(f)));
        }
        
        String decrypted = breakForAllLangs(encrypted, languages);
        System.out.println("decrypted: \n"  + decrypted);
        
    }

}
