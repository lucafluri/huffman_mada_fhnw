import java.io.IOException;
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
        } //frequency table generated

        Node left = new Node;
        Node right = null;
        while(freqMap.size()!=0){
            if(left==null || right==null){
                int Key = freqMap.firstEntry().getKey();
                String Value = String.valueOf((char)(int)freqMap.firstEntry().getValue());

                left = new Node(freqMap.firstEntry().getKey(), freqMap.firstEntry().getValue().toString());
                freqMap.remove(Key);

                Key = freqMap.firstEntry().getKey();
                Value = String.valueOf((char)(int)freqMap.firstEntry().getValue());

                right = new Node(freqMap.firstEntry().getKey(), freqMap.firstEntry().getValue().toString());
                freqMap.remove(Key);
            }
        }

    }


}

class Node {
    static Node left = null;
    static Node right = null;
    static String value = "";
    static int freq = 0;

    public Node(int _freq, String _value){ //Constructor for bottom Nodes
        freq = _freq;
        value = _value;
    }

    public Node(Node _left, Node _right){ //Constructor for all the upper Nodes with children
        if(_left.freq > _right.freq){ //Correct binary tree order
            right = _left;
            left = _right;
        }else {
            left = _left;
            right = _right;
        }

        value = _left.value + _right.value;
        freq = _left.freq + _right.freq;


    }
}
