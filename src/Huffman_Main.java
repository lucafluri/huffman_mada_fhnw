import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class Huffman_Main {

    public static void main(String[] args) throws IOException {
        createHuffmanCode(createFreqTable(readASCIIFile("ascii.txt")));

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

        //for(int i : freqTable){System.out.print(i);}

        return freqTable;
    }

    public static void createHuffmanCode(int[] freqTable){
        PriorityQueue<Node> queue = new PriorityQueue<>((o1, o2) -> (o1.freq < o2.freq) ? -1 : 1); // sorted frequency table with (freq, char int)
        TreeMap<String, String> map = new TreeMap<>(); //Huffmancode table
        for(char i=0; i<freqTable.length; i++){
            if((freqTable[i] > 0) ?  queue.add(new Node(freqTable[i], String.valueOf(i))) : false);
        } //frequency table generated

        Node left, right, top = null;

        while(queue.size()!=1){
            left = queue.poll();
            right = queue.poll();
            top = new Node(left, right);
            queue.add(top);
        }
        //"Tree" finished, we have top node of tree

        getCodes(map, "", top);

        System.out.println(map);


    }

    public static void getCodes(TreeMap<String, String> map, String code, Node top){
        if(top.left == null && top.right==null){
            map.put(top.value, code);
        }else{
            getCodes(map, code + "0", top.left);
            getCodes(map, code + "1", top.right);
        }

    }


}

class Node implements Comparable<Node>{
    Node left = null;
    Node right = null;
    String value;
    int freq;

    public Node(int _freq, String _value){ //Constructor for bottom Nodes
        this.freq = _freq;
        this.value = _value;
    }

    public Node(Node _left, Node _right){ //Constructor for all the upper Nodes with children
        if(_left.freq > _right.freq){ //Correct binary tree order
            this.right = _left;
            this.left = _right;
        }else {
            this.left = _left;
            this.right = _right;
        }

        this.value = _left.value + _right.value;
        this.freq = _left.freq + _right.freq;
    }


    @Override
    public int compareTo(Node o) {
        return this.freq - o.freq;
    }


    @Override
    public String toString(){
        return this.freq + ": " + this.value;
    }

}
