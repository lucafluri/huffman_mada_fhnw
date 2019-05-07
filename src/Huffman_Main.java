import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.TreeMap;


public class Huffman_Main {

    public static void main(String[] args) throws IOException {
        createFreqTable(readASCIIFile("ascii.txt"));

    }

    public static String readASCIIFile(String filepath) throws IOException {
        String input = Files.readAllLines(Paths.get(filepath)).get(0);
        System.out.println(input);

        return input;
    }

    public static int[] createFreqTable(String input){
        int[] freqTable = new int[256]; //ASCII Table Size = 256
        for(char c : input.toCharArray()){
            if(c<256){ //only ascii characters!
                freqTable[c]++;
            }
        }


        for(int i : freqTable){System.out.print(i);}

        return freqTable;
    }

    public static void createHuffmanCode(int[] freqtable){
        TreeMap<Integer, Integer> freqMap = new TreeMap();
        for(int i : freqtable){
            if(i>0){ //frequency not 0
                freqMap.put(freqtable[i], i);
            }
        }

    }

}

class Node{
    Node left = null;
    Node right = null;
    String value = "";
    int freq = 0;

    public void Node(int freq, String value){ //Constructor for bottom Nodes
        this.freq = freq;
        this.value = value;
    }

    public void Node(Node left, Node right){ //Constructor for all the upper Nodes with children
        this.left = left;
        this.right = right;


    }
}
