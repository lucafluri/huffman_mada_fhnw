import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class Huffman_Main {

    private static String inputPath = "ascii.txt";          // Ascii Text File to decode
    private static String tablePath = "dec_tab.txt";        // Path to Huffman Table
    private static String comprPath = "output.dat";         // Path to compressed File
    private static String decomprPath = "decompress.txt";   // Path to decompressed File

    public static void main(String[] args) throws IOException {
        compress(); // creates freq table and creates a huffman tree which is used to compress/encode the original text
                    // Both the Huffman table and the compressed text are stored.

        decompress(); // Decompresses the specified file with the specified table and saves the result.
    }

    public static String readASCIIFile(String filepath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filepath)));
    }

    public static int[] createFreqTable(String input){
        System.out.println("Creating Frequency Table...");

        int[] freqTable = new int[256]; //ASCII Table Size = 256, index is used as char identifier and elements as frequency.
        for(char c : input.toCharArray()){
            if(c<256){ //only ascii characters!
                freqTable[c]++;
            }
        }
        return freqTable;
    }

    public static TreeMap<String, String> createHuffmanCode(int[] freqTable) throws IOException{
        System.out.println("Creating Huffman Tree...");

        PriorityQueue<Node> queue = new PriorityQueue<>((o1, o2) -> (o1.freq < o2.freq) ? -1 : 1); // sorted frequency table with (freq, char int)
        TreeMap<String, String> map = new TreeMap<>(); //Huffmancode table as treemap
        for(char i=0; i<freqTable.length; i++){
            if((freqTable[i] > 0) && queue.add(new Node(freqTable[i], String.valueOf(i)))); //saves every character and corresponding frequency that appears more than once in the text.
        } //frequency table generated

        Node left, right, top = null;

        while(queue.size()!=1){ //Huffman Tree Algorithm
            left = queue.poll();
            right = queue.poll(); //removes the 2 nodes with lowest frequency
            top = new Node(left, right); // creates a parent node with the combined frequency fo left and right nodes. -> see Node Class at bottom
            queue.add(top); //Adds Parent Node to queue.
            //Repeats until only one element is present in queue -> Algorithm finished.
        }
        //"Tree" finished, we have top node of tree

        getCodes(map, "", top); //We use the top node to recursively traverse the Tree and generate the codes.
        saveHuffman(map); //Save the generated Huffman Code

        return map;


    }

    public static void getCodes(TreeMap<String, String> map, String code, Node top){
        if(top.left == null && top.right==null){ //This means the node is a leaf node and we have found a character.
            map.put(top.value, code); //Add the newly found character and huffman code pair to the map.
        }else{
            getCodes(map, code + "0", top.left); //If we go left we add a 0 to the code and call the function recursively
            getCodes(map, code + "1", top.right); //right -> +1
        }

    }

    public static void saveHuffman(TreeMap<String, String> table) throws IOException {
        String toWrite = "";
        TreeMap<String, String> map = (TreeMap) table.clone();

        System.out.println("Saving Huffman Tree to " + tablePath + "...");

        int size = map.size();
        for(int i = 0; i<size; i++){ //Saving Huffman Table according to Assignment
            toWrite += Integer.valueOf(map.firstEntry().getKey().toCharArray()[0]) + ":";
            toWrite += map.firstEntry().getValue() + "-"; //The last "-" is ignored for simplicity
            map.pollFirstEntry();
        }
        Files.write(Paths.get(tablePath), toWrite.getBytes()); //saves the Huffman Table to the specified file path
    }

    public static void compress() throws IOException{
        System.out.println("COMPRESSING:\nReading File " + inputPath + "...");
        String bitString = "";
        String text = readASCIIFile(inputPath);
        TreeMap<String, String> table = createHuffmanCode(createFreqTable(text)); //creates the huffman table
        System.out.println("Compressing Text...");

        for(char c : text.toCharArray()){
            bitString += table.get(String.valueOf(c));
        }
        bitString += "1";
        while(bitString.length() % 8 != 0){
            bitString += "0";
        }

        //System.out.println(bitString);

        //create byteArray
        byte[] byteArray = new byte[bitString.length()/8];
        for(int i = 0; i<bitString.length()/8;i++){
            byteArray[i] = (byte) Integer.parseInt(bitString.substring(i*8, (i+1)*8), 2);
        }
        //System.out.println(byteArray);


        System.out.println("Saving Compressed Text to " + comprPath + "...\n");
        FileOutputStream fos = new FileOutputStream(comprPath);
        fos.write(byteArray);
        fos.close();

    }

    public static void decompress() throws FileNotFoundException, IOException{
        File file = new File(comprPath);
        System.out.println("DECOMPRESSING:\nDecompressing " + comprPath + "...");
        byte[] bFile = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(bFile);
        fis.close();

        //bitString with ending 1000*


        String bitString = "";
        //remove tailing 0s and 1
        //System.out.println(bitString);
        for(byte b : bFile){
            bitString += Integer.toBinaryString((b & 0xFF)+0x100).substring(1); // Wasted too much time here. Thanks Stackoverflow^^
        }

        bitString = bitString.substring(0, bitString.lastIndexOf("1"));
        //System.out.println(bitString);

        String decTable = readASCIIFile(tablePath);
        System.out.println("Reading Huffman Table from " + tablePath + "...");
        //Parse String into TreeMap
        TreeMap<String, String> table = new TreeMap<>();
        String[] mappings = decTable.split("-");

        for(String mapping : mappings){
            String c = String.valueOf((char) Integer.parseInt(mapping.split(":")[0]));
            String v = mapping.split(":")[1];
            table.put(c, v);
        }

        //System.out.println("read Table: " + table);

        String decoded = "";
        int a = 0;
        for(int i = 0; i<=bitString.length(); i++) {
            for(Map.Entry<String, String> entry: table.entrySet()){
                String substring = bitString.substring(a, i);
                if(entry.getValue().equals(substring)){
                    decoded += entry.getKey();

                    a = i;
                }
            }

        }

        //System.out.println(decoded);


        //Write decoded File
        Files.write(Paths.get(decomprPath), decoded.getBytes());
        System.out.println("Decompressed File saved to: " + decomprPath);


    }




}

class Node{
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
    public String toString(){
        return this.freq + ": " + this.value;
    }

}
