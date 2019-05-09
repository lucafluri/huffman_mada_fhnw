import sun.reflect.generics.tree.Tree;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class Huffman_Main {

    public static void main(String[] args) throws IOException {
        String text = readASCIIFile("ascii.txt");
        TreeMap<String, String> huffmanTable = createHuffmanCode(createFreqTable(text));
        System.out.println(huffmanTable);
        encode(text, huffmanTable);

        decode();
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

    public static TreeMap<String, String> createHuffmanCode(int[] freqTable) throws IOException{
        PriorityQueue<Node> queue = new PriorityQueue<>((o1, o2) -> (o1.freq < o2.freq) ? -1 : 1); // sorted frequency table with (freq, char int)
        TreeMap<String, String> map = new TreeMap<>(); //Huffmancode table
        for(char i=0; i<freqTable.length; i++){
            if((freqTable[i] > 0) && queue.add(new Node(freqTable[i], String.valueOf(i))));
        } //frequency table generated

        Node left, right, top = null;

        while(queue.size()!=1){
            left = queue.poll();
            //System.out.println("Left: " + left);
            right = queue.poll();
            //System.out.println("Right: " + right);
            top = new Node(left, right);
            queue.add(top);

            //System.out.println(queue);
        }
        //"Tree" finished, we have top node of tree

        getCodes(map, "", top);
        saveHuffman(map);


        //System.out.println(map);

        return map;


    }

    public static void getCodes(TreeMap<String, String> map, String code, Node top){
        if(top.left == null && top.right==null){
            map.put(top.value, code);
        }else{
            getCodes(map, code + "1", top.left);
            getCodes(map, code + "0", top.right);
        }

    }

    public static void saveHuffman(TreeMap<String, String> table) throws IOException {
        String path = "dec_tab.txt";
        String toWrite = "";
        TreeMap<String, String> map = (TreeMap) table.clone();



        int size = map.size();
        for(int i = 0; i<size; i++){
            toWrite += Integer.valueOf(map.firstEntry().getKey().toCharArray()[0]) + ":";
            toWrite += map.firstEntry().getValue() + "-";
            map.pollFirstEntry();
        }


        Files.write(Paths.get(path), toWrite.getBytes());
    }

    public static void encode(String text, TreeMap<String, String> table) throws FileNotFoundException, IOException{
        String bitString = "";

        for(char c : text.toCharArray()){
            bitString += table.get(String.valueOf(c));
        }
        bitString += "1";
        while(bitString.length() % 8 != 0){
            bitString += "0";
        }

        //System.out.println(bitString);

        //create bytearray
        byte[] bytearray = bitString.getBytes();
        //System.out.println(bytearray);

        FileOutputStream fos = new FileOutputStream("output.dat");
        fos.write(bytearray);
        fos.close();

    }

    public static void decode() throws FileNotFoundException, IOException{
        File file = new File("output.dat");
        byte[] bFile = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(bFile);
        fis.close();

        //bitString with ending 1000*

        String bitString = new String(bFile);
        //remove tailing 0s and 1
        bitString = bitString.substring(0, bitString.lastIndexOf("1"));
        System.out.println(bitString);

        String decTable = readASCIIFile("dec_tab.txt");
        //Parse String into TreeMap
        TreeMap<String, String> table = new TreeMap<>();
        String[] mappings = decTable.split("-");

        for(String mapping : mappings){
            String c = String.valueOf((char) Integer.parseInt(mapping.split(":")[0]));
            String v = mapping.split(":")[1];
            table.put(c, v);
        }

        System.out.println("read Table: " + table);

        for(int i = 0; i<bitString.length(); i++);
            if(bitString.substring(0, i))


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
